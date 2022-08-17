package io.github.wynn5a.reactor.nio;


import java.nio.channels.SelectionKey;
import java.util.concurrent.Callable;

/**
 * @author wynn5a
 * @date 2022/8/17
 */
public interface EventHandler extends Callable<Boolean> {
  void handleEvent();
  SelectionKey getHandle();

  void setHandle(SelectionKey key);
}
