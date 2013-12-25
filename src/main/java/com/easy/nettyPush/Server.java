package com.easy.nettyPush;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easy.nettyPush.flash.FlashSecurityServer;
import com.easy.nettyPush.pipelinefactory.NettyPushPipelineFactory;

public class Server {
	private static Logger logger = LoggerFactory.getLogger(Server.class);
	public static void start(final String hostName,final int port){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Configure the server.
				ServerBootstrap bootstrap = new ServerBootstrap(
						new NioServerSocketChannelFactory(Executors
								.newCachedThreadPool(), Executors.newCachedThreadPool()));
				// Set up the event pipeline factory.
				bootstrap.setPipelineFactory(new NettyPushPipelineFactory());
				bootstrap.setOption("child.reuseAddress", Boolean.valueOf(true));
				bootstrap.setOption("child.tcpNoDelay", true);
		        bootstrap.setOption("child.keepAlive", true);
				// Bind and start to accept incoming connections.
				bootstrap.bind(new InetSocketAddress(port));

				logger.info("Web socket server started at  "+ port + '.');
				/*int flashSecurityPort=10843;
				        logger.info("start the FlashSecurityServer with port : " + 
				          flashSecurityPort);
				        FlashSecurityServer flashSecurityServer = new FlashSecurityServer(flashSecurityPort);
				        flashSecurityServer.start();*/
			}
		}).start();
		
	}
	public static void main(String[] args) {
		Server.start("127.0.0.1",9001);
	}
}
