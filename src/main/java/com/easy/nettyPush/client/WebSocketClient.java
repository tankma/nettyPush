package com.easy.nettyPush.client;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.util.CharsetUtil;

import com.easy.nettyPush.common.Configuration;
import com.easy.nettyPush.enums.SocketIoTypeEnum;
import com.easy.nettyPush.room.RoomClient;
import com.easy.nettyPush.room.Rooms;
import com.easy.nettyPush.room.SessionClient;
import com.easy.nettyPush.scheduler.CancelableScheduler;
import com.easy.nettyPush.scheduler.SchedulerKey;
import com.easy.nettyPush.scheduler.SchedulerKey.Type;

public class WebSocketClient extends AClient {

	public WebSocketClient(String sessionId, Channel channel, String roomId) {
		super(sessionId, channel, roomId);
	}

	@Override
	public void onHeartbeat(HttpRequest request) {
		final SchedulerKey key = new SchedulerKey(Type.HEARBEAT_TIMEOUT,
				getSessionId());
		// cancel heartbeat check because the client answered
		CancelableScheduler.cancel(key);
		CancelableScheduler.schedule(key,new Runnable() {
			public void run() {

				ChannelBuffer message = ChannelBuffers.copiedBuffer("2::",
						CharsetUtil.UTF_8);
				WebSocketFrame res = new TextWebSocketFrame(message);
				if (channel.isOpen()) {
					channel.write(res);
					
				}
			}
		}, Configuration.HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
	}

	@Override
	public void writeText(String text) {
		WebSocketFrame message = new TextWebSocketFrame(text);
		if (channel.isOpen()) {
			channel.write(message);
		}
		onHeartbeat(null);
	}

	@Override
	public void onDiscount() {
		//SessionClient.removeSession(String.valueOf(channel.getId()));
	}

	@Override
	public void writeConnect(HttpRequest request) {
		ChannelBuffer message = ChannelBuffers.copiedBuffer(SocketIoTypeEnum.CONNECT.getText(),
				CharsetUtil.UTF_8);
		WebSocketFrame res = new TextWebSocketFrame(message);
		channel.write(res);
		
	}

}
