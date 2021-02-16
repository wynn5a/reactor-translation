package io.github.wynn5a.singlethread;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable{
    private final ServerSocketChannel serverSocket;
    private final Selector selector;
    public Acceptor(ServerSocketChannel serverSocket, Selector selector) {
        this.serverSocket = serverSocket;
        this.selector = selector;
    }

    @Override
    public void run() {
        try{
            SocketChannel channel = serverSocket.accept();
            if(channel!=null){
                new Handler(selector, channel);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
