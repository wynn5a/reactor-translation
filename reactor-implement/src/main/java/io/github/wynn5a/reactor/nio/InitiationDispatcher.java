package io.github.wynn5a.reactor.nio;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author wynn5a
 * @date 2022/8/17
 */
public class InitiationDispatcher implements Dispatcher {

  private final Selector selector;
  private final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
  private final Map<SelectionKey, Class<? extends EventHandler>> handlers = new ConcurrentHashMap<>();

  private final ReadWriteLock selectorLock = new ReentrantReadWriteLock();

  public InitiationDispatcher() {
    try {
      this.selector = Selector.open();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void handleEvents() {
    while (true) {
      try {
        checkBeforeSelect();
        selector.select();
        var i = selector.selectedKeys().iterator();
        while (i.hasNext()) {
          System.out.println("ready selected key");
          SelectionKey handle = i.next();
          Class<? extends EventHandler> handlerClass = handlers.get(handle);
          if (handlerClass != null) {
            EventHandler handler = handlerClass.getDeclaredConstructor().newInstance();
            handler.setHandle(handle);
            Future<Boolean> handled = pool.submit(handler);
            if (handled.get()) {
              i.remove();
            }
          }
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * make sure none of handler thread is updating selector
   */
  private void checkBeforeSelect() {
    selectorLock.writeLock().lock();
    selectorLock.writeLock().unlock();
  }

  @Override
  public void registerHandler(SelectableChannel channel, Class<? extends EventHandler> handlerClass, int event) {
    try {
      lockBeforeRegister();
      channel.configureBlocking(false);
      SelectionKey key = channel.register(selector, event);
      key.attach(this);
      handlers.put(key, handlerClass);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      unlockAfterRegister();
    }
  }

  private void unlockAfterRegister() {
    selectorLock.readLock().unlock();
  }

  private void lockBeforeRegister() {
    selectorLock.readLock().lock();
    selector.wakeup();
  }

  @Override
  public void removeHandler(SelectableChannel channel) {
    boolean registered = channel.isRegistered();
    if (registered) {
      SelectionKey selectionKey = channel.keyFor(selector);
      selectionKey.cancel();
      handlers.remove(selectionKey);
    }
  }
}
