package com.easy.nettyPush.pipelinefactory;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import com.easy.nettyPush.upstreamhandler.AuthorizeHandler;
import com.easy.nettyPush.upstreamhandler.FlashPolicyHandler;
import com.easy.nettyPush.upstreamhandler.FlashResourceHandler;
import com.easy.nettyPush.upstreamhandler.FlashSocketHandler;
import com.easy.nettyPush.upstreamhandler.WebSocketHandler;
import com.easy.nettyPush.upstreamhandler.XHRPollingHandler;

public class NettyPushPipelineFactory implements ChannelPipelineFactory{

	public final static String FLASH_POLICY = "flash_policy";
	public final static String FLASH_RESOURCE = "flash_resource";
	public final static String WEBSOCKET = "websocket";
	public final static String FLASHSOCKET = "flashsocket";
	public final static String AUTHORIZE = "authorize";
	public final static String XHRPOLLING = "xhrpolling";
	public NettyPushPipelineFactory(){
	}
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = pipeline();
		pipeline.addLast(FLASH_POLICY, new FlashPolicyHandler());
		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast(FLASH_RESOURCE, new FlashResourceHandler());
		pipeline.addLast(AUTHORIZE, new AuthorizeHandler());
		pipeline.addLast(WEBSOCKET, new WebSocketHandler());
		pipeline.addLast(FLASHSOCKET, new FlashSocketHandler());
		pipeline.addLast(XHRPOLLING, new XHRPollingHandler());
		
		return pipeline;
	}

}
