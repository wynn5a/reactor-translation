package io.github.wynn5a.reactor.nio;

import java.nio.channels.SelectableChannel;

/**
 * @author wynn5a
 * @date 2022/8/17
 */
public interface Dispatcher {

  void handleEvents();

  void registerHandler(SelectableChannel channel, Class<? extends EventHandler> clazz, int event);


  void removeHandler(SelectableChannel channel);
}
