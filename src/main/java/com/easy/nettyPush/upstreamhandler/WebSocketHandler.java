package com.easy.nettyPush.upstreamhandler;

import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easy.nettyPush.client.IClient;
import com.easy.nettyPush.client.WebSocketClient;
import com.easy.nettyPush.common.Common;
import com.easy.nettyPush.enums.SocketIoTypeEnum;
import com.easy.nettyPush.pipelinefactory.NettyPushPipelineFactory;
import com.easy.nettyPush.room.SessionClient;
import com.easy.nettyPush.room.RoomClient;
import com.easy.nettyPush.room.Rooms;
import com.easy.nettyPush.scheduler.CancelableScheduler;
import com.easy.nettyPush.scheduler.SchedulerKey;
import com.easy.nettyPush.scheduler.SchedulerKey.Type;

@Sharable
public class WebSocketHandler extends TransportHandler {

	private static Logger logger = LoggerFactory
			.getLogger(WebSocketHandler.class);
	public static final String NAME = "websocket";
	

	public WebSocketHandler() {
		this.path = Common.START_PATH + NAME;
	}
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object msg = e.getMessage();
		if (msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;
			QueryStringDecoder queryDecoder = new QueryStringDecoder(req
					.getUri());
			String path = queryDecoder.getPath();
			//System.out.println(path);
			if (path.startsWith(this.path)) {
				handshake(ctx, queryDecoder, req);
			} else {
				ctx.sendUpstream(e);
			}
		} else if (msg instanceof TextWebSocketFrame) {
			
			TextWebSocketFrame frame = (TextWebSocketFrame) msg;
			String text = frame.getText();
			if (text.startsWith(SocketIoTypeEnum.DISCONNECT.getText())) {
				
				//System.out.println(text+"=====text=");
				ctx.getChannel().close();
			}else if(text.startsWith(SocketIoTypeEnum.HEARTBEAT.getText())){
				SessionClient.getSession(String.valueOf(ctx.getChannel().getId())).onHeartbeat(null);
				//heartbeatHandler.onHeartbeat(Clients.get(ctx.getChannel()
						//.getId()));
			}else {
				super.broadcast(frame.getText());
			}
		} else {
			ctx.sendUpstream(e);
		}
	}

	private void handshake(ChannelHandlerContext ctx,
			QueryStringDecoder queryDecoder, HttpRequest req) {
		Channel channel = ctx.getChannel();
		WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(
				getWebSocketLocation(req), null, false);
		WebSocketServerHandshaker handshaker = factory.newHandshaker(req);
		if (handshaker != null) {
			handshaker.handshake(channel, req);

			String roomId = getRoomId(queryDecoder);
			String[] parts = queryDecoder.getPath().split("/");
			IClient webSocketClient = new WebSocketClient(parts[4], channel, roomId);
			SessionClient.putSession(String.valueOf(channel.getId()), webSocketClient);
			webSocketClient.onHeartbeat(null);
			
			webSocketClient.writeConnect(null);
			// connectClient(channel, sessionId);
		} else {
			factory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
		}
	}

	private String getWebSocketLocation(HttpRequest req) {
		String protocol = "ws://";
		return protocol + req.getHeader(HttpHeaders.Names.HOST) + req.getUri();
	}

	@Override
	public void removeHandler(ChannelPipeline channelPipeline) {
		channelPipeline.remove(NettyPushPipelineFactory.FLASHSOCKET);
	}


}
