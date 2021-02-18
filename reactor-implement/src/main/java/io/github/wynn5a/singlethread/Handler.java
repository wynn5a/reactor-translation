package io.github.wynn5a.singlethread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Handler implements Runnable {

  private final SelectionKey selectionKey;
  private final SocketChannel socket;
  private ByteBuffer buffer = ByteBuffer.allocate(1024);

  public Handler(Selector selector, SocketChannel channel) throws IOException {
    this.socket = channel;
    socket.configureBlocking(false);
    this.selectionKey = socket.register(selector, 0);
    selectionKey.attach(this);
    selectionKey.interestOps(SelectionKey.OP_READ|SelectionKey.OP_WRITE);
    selector.wakeup();
  }

  @Override
  public void run() {
    System.out.println("IO handler:" + this + " is running at " + Thread.currentThread().getName());
    System.out.println(selectionKey.readyOps());
    try {
      if (SelectionKey.OP_READ == selectionKey.readyOps()) {
        read();
      } else if (SelectionKey.OP_WRITE == selectionKey.readyOps()) {
        send();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void send() throws IOException {
    socket.write(buffer);
    buffer.clear();
    selectionKey.interestOps(SelectionKey.OP_READ);
  }


  private void read() throws IOException {
    int length;
    while ((length = socket.read(buffer)) > 0) {
      System.out.print(new String(buffer.array(), 0, length));
    }
    buffer.flip();
    // Normally also do first write now
    selectionKey.interestOps(SelectionKey.OP_WRITE);
  }
}
