package com.easy.nettyPush.server;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketIOServer implements IServer {
	private static Logger logger = LoggerFactory
			.getLogger(SocketIOServer.class);
	private ServerBootstrap bootstrap;
	private Channel serverChannel;
	private int port;
	private boolean running;

	public SocketIOServer(int port){
		this.port = port;
	}
	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
