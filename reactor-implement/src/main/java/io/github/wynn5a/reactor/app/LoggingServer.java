package io.github.wynn5a.reactor.app;

import static java.nio.channels.SelectionKey.OP_ACCEPT;

import io.github.wynn5a.reactor.nio.Dispatcher;
import io.github.wynn5a.reactor.nio.InitiationDispatcher;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

/**
 * @author wynn5a
 * @date 2022/8/17
 */
public class LoggingServer {

  static Dispatcher dispatcher = new InitiationDispatcher();

  public static void main(String[] args) {
    try {
      ServerSocketChannel channel = ServerSocketChannel.open();
      channel.configureBlocking(false);
      channel.bind(new InetSocketAddress("127.0.0.1", 1234));

      dispatcher.registerHandler(channel, LoggingAcceptor.class, OP_ACCEPT);
      dispatcher.handleEvents();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
