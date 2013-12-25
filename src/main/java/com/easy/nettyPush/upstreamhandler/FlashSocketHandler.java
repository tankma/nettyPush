package com.easy.nettyPush.upstreamhandler;

import org.jboss.netty.channel.ChannelPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easy.nettyPush.common.Common;
import com.easy.nettyPush.pipelinefactory.NettyPushPipelineFactory;

public class FlashSocketHandler extends WebSocketHandler{

	
	private static Logger logger = LoggerFactory.getLogger(FlashSocketHandler.class);
	public static final String NAME="flashsocket";
	public FlashSocketHandler() {
		this.path = Common.START_PATH+NAME;
	}
	@Override
	public void removeHandler(ChannelPipeline channelPipeline) {
		channelPipeline.remove(NettyPushPipelineFactory.WEBSOCKET);
	}
}
