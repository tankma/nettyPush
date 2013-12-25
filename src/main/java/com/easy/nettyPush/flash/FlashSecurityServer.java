package com.easy.nettyPush.flash;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import com.easy.nettyPush.upstreamhandler.FlashResourceHandler;

public class FlashSecurityServer
{
  private Channel serverChannel;
  private ServerBootstrap bootstrap;
  private int port;
  Timer timer = new HashedWheelTimer(); 
  public FlashSecurityServer(int port)
  {
    this.port = port;
    if (this.port < 0)
      this.port = 10843;
  }

  public void start()
  {
    this.bootstrap = 
      new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), 
      Executors.newCachedThreadPool()));

    this.bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
		@Override
		public ChannelPipeline getPipeline() throws Exception {
			ChannelPipeline pipeline = Channels.pipeline();
	        pipeline.addLast("flash_timeout", new ReadTimeoutHandler(timer, 30));
	        //pipeline.addLast("flash_resource", new FlashResourceHandler());
	        pipeline.addLast("flash_decoder", new FlashSecurityDecoder());
	        pipeline.addLast("flash_handler", new FlashSecurityHandler());
	        
	        return pipeline;
		}
	});
    this.bootstrap.setOption("child.tcpNoDelay", Boolean.valueOf(true));
    this.bootstrap.setOption("child.keepAlive", Boolean.valueOf(true));

    this.serverChannel = this.bootstrap.bind(new InetSocketAddress(this.port));
  }

  public void stop()
  {
    this.serverChannel.close();
    this.bootstrap.releaseExternalResources();
  }
}