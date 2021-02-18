package io.github.wynn5a.singlethread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

public class ReactorServer implements Runnable {

  final Selector selector;
  final ServerSocketChannel serverSocket;


  public ReactorServer(int port) throws IOException {
    this.selector = Selector.open();
    this.serverSocket = ServerSocketChannel.open();
    serverSocket.socket().bind(new InetSocketAddress(port));
    serverSocket.configureBlocking(false);
    SelectionKey sk = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    sk.attach(new Acceptor(serverSocket, selector));
    System.out.println("Server started:" + port);
  }


  @Override
  public void run() {
    try {
      while (!Thread.interrupted()) {
        selector.select();
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        for (SelectionKey selectionKey : selectionKeys) {
          dispatch(selectionKey);
        }
        selectionKeys.clear();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void dispatch(SelectionKey key) {
    Runnable r = (Runnable) key.attachment();
    if (r != null) {
      r.run();
    }
  }

  public static void main(String[] args) throws IOException {
    new Thread(new ReactorServer(8080), "Server-1").start();
  }
}
