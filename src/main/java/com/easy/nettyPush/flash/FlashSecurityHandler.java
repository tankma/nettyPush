package com.easy.nettyPush.flash;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlashSecurityHandler extends SimpleChannelUpstreamHandler
{
  private static final Logger log = LoggerFactory
	.getLogger(FlashSecurityHandler.class);

  private static ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer(
    "<?xml version=\"1.0\"?><!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\"><cross-domain-policy>    <site-control permitted-cross-domain-policies=\"master-only\"/>   <allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>", 
    CharsetUtil.UTF_8);

  public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
    throws Exception
  {
	  System.out.println("==push====messageReceived FlashSecurityHandler=======");
    ChannelFuture f = e.getChannel().write(channelBuffer);
    f.addListener(ChannelFutureListener.CLOSE);
  }

  public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
    throws Exception
  {
    log.warn("Exception now ...");
    e.getChannel().close();
  }
}