package io.github.wynn5a.reactor.app;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.github.wynn5a.reactor.nio.EventHandler;
import io.github.wynn5a.reactor.nio.InitiationDispatcher;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author wynn5a
 * @date 2022/8/17
 */
public class LoggingHandler implements EventHandler {

  private SelectionKey handle;
  private final ByteBuffer buffer = ByteBuffer.allocate(1024);

  @Override
  public void handleEvent() {
    SocketChannel channel = (SocketChannel) handle.channel();
    if (channel.isOpen() && handle.isReadable()) {
      try {
        buffer.clear();
        int read = channel.read(buffer);
        if (read < 0) {
          System.out.println("Client connection refused");
          InitiationDispatcher dispatcher = (InitiationDispatcher) handle.attachment();
          dispatcher.removeHandler(channel);
          channel.close();
          return;
        }
        buffer.flip();
        System.out.print("LOG: " + new String(buffer.array(), UTF_8));
//        handleLogContent();
        buffer.clear();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void handleLogContent() {
    if (buffer.remaining() > 0) {
      byte[] data = new byte[buffer.remaining()];
      buffer.get(data);
      for (int i = 0; i < data.length; i++) {
        byte b = data[i];
        if (b == '\r' || b == '\n') {
          byte[] temp = new byte[i + 1];
          System.arraycopy(data, 0, temp, 0, i + 1);
          System.out.print("LOG: " + new String(temp, UTF_8));
        }
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
