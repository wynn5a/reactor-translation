package io.github.wynn5a.reactor.app;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.charset.StandardCharsets.UTF_8;

import io.github.wynn5a.reactor.nio.Dispatcher;
import io.github.wynn5a.reactor.nio.EventHandler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author wynn5a
 * @date 2022/8/17
 */
public class LoggingAcceptor implements EventHandler {

  private SelectionKey handle;

  @Override
  public void handleEvent() {
    ServerSocketChannel channel = (ServerSocketChannel) handle.channel();
    if (channel.isOpen() && handle.isAcceptable()) {
      try {
        SocketChannel accept = channel.accept();
        ByteBuffer buffer = ByteBuffer.wrap("Connect Successfully with Logging Server! \n".getBytes(UTF_8));
        accept.write(buffer);
        Dispatcher dispatcher = (Dispatcher) handle.attachment();
        dispatcher.registerHandler(accept, LoggingHandler.class, OP_READ);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public SelectionKey getHandle() {
    return handle;
  }

  @Override
  public void setHandle(SelectionKey key) {
    this.handle = key;
  }

  @Override
  public Boolean call() {
    handleEvent();
    return true;
  }
}
