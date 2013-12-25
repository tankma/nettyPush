package com.easy.nettyPush.upstreamhandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easy.nettyPush.client.IClient;
import com.easy.nettyPush.client.XhrClient;
import com.easy.nettyPush.common.Common;
import com.easy.nettyPush.common.Configuration;
import com.easy.nettyPush.enums.SocketIoTypeEnum;
import com.easy.nettyPush.room.RoomClient;
import com.easy.nettyPush.room.Rooms;
import com.easy.nettyPush.scheduler.CancelableScheduler;
import com.easy.nettyPush.scheduler.SchedulerKey;
import com.easy.nettyPush.scheduler.SchedulerKey.Type;
import com.easy.util.HttpUtil;

@Sharable
public class XHRPollingHandler extends TransportHandler {

	private static Logger logger = LoggerFactory
			.getLogger(XHRPollingHandler.class);
	private static ConcurrentHashMap<String, IClient> sessionIdsClient = new ConcurrentHashMap<String, IClient>();
	public static final String NAME = "xhr-polling";

	public XHRPollingHandler() {
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
			Channel channel = ctx.getChannel();
			if (queryDecoder.getPath().startsWith(path)) {
				handleMessage(req, queryDecoder, channel);
				return;
			}
		}
		ctx.sendUpstream(e);
	}

	private void handleMessage(HttpRequest req,
			QueryStringDecoder queryDecoder, Channel channel)
			throws IOException {

		String[] parts = queryDecoder.getPath().split("/");
		if (parts.length > 3) {
			String sessionId = parts[4];
			String roomId = getRoomId(queryDecoder);
			if (HttpMethod.POST.equals(req.getMethod())) {
				onPost(req, sessionId, channel, req.getContent());
			} else if (HttpMethod.GET.equals(req.getMethod())) {
				onGet(sessionId, channel, req, roomId);
			}
		} else {
			logger
					.error(
							"Wrong {} method request path: {}, from ip: {}. Channel closed!",
							new Object[] { req.getMethod(), path,
									channel.getRemoteAddress() });
			channel.close();
		}
	}

	private void onGet(String sessionId, Channel channel, HttpRequest request,
			String roomId) {
		IClient xhrClient = sessionIdsClient.get(sessionId);
		if (xhrClient == null) {
			xhrClient = new XhrClient(sessionId, channel,
					roomId);
			RoomClient roomClient = Rooms.getInstance()
					.createRoomClient(roomId);
			roomClient.addClient(sessionId, xhrClient);
			sessionIdsClient.put(sessionId, xhrClient);
			// SessionClient.putSession(channel.getId(), xhrClient);
			// System.out.println("=========putSession========="+channel.getId());
			xhrClient.writeConnect(request);
		} else {
			xhrClient.setChannel(channel);
			RoomClient roomClient = Rooms.getInstance().getRoomClient(roomId);
			roomClient.replaceClient(sessionId, xhrClient);

			// SessionClient.putSession(channel.getId(), xhrClient);
			// System.out.println("=========putSession========="+channel.getId());
			xhrClient.onHeartbeat(request);
			scheduleDisconnect(xhrClient);
		}
		

	}

	private void onPost(HttpRequest req, String sessionId, Channel channel,
			ChannelBuffer content) throws IOException {
		IClient xhrClient = sessionIdsClient.get(sessionId);
		if (xhrClient == null) {
			logger.error("Client with sessionId: {} was already disconnected!",
					sessionId);
			channel.close();
			return;
		}
		// SessionClient.putSession(channel.getId(), xhrClient);
		String text = content.toString(CharsetUtil.UTF_8);
		if (text.equals(SocketIoTypeEnum.DISCONNECT.getText())) {
			xhrClient.writeText(text);
			xhrClient.onDiscount();
			sessionIdsClient.remove(sessionId);
		} else {
			RoomClient roomClient = Rooms.getInstance().createRoomClient(
					xhrClient.getRoomId());
			Collection<IClient> values = roomClient.values();
			logger.error(values.size() + "===");
			for (Iterator<IClient> it = values.iterator(); it.hasNext();) {
				it.next().writeText(text);
			}
		}

		HttpResponse resp = HttpUtil.getInitResponse(req);
		resp.setContent(ChannelBuffers.copiedBuffer("1", CharsetUtil.UTF_8));
		HttpHeaders.setContentLength(resp, resp.getContent().readableBytes());
		channel.write(resp).addListener(ChannelFutureListener.CLOSE);

		SchedulerKey closeTimeoutKey = new SchedulerKey(Type.CLOSE_TIMEOUT,
				sessionId);
		CancelableScheduler.cancel(closeTimeoutKey);

	}

	private void scheduleDisconnect(final IClient xhrClient) {
		final SchedulerKey key = new SchedulerKey(Type.CLOSE_TIMEOUT, xhrClient
				.getSessionId());
		CancelableScheduler.cancel(key);
		ChannelFuture future = xhrClient.getChannel().getCloseFuture();
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				CancelableScheduler.schedule(key, new Runnable() {
					@Override
					public void run() {
						xhrClient.onDiscount();
						sessionIdsClient.remove(xhrClient.getSessionId());
						logger.error("Client: " + xhrClient.getSessionId()
								+ " disconnected due to connection timeout");
					}
				}, Configuration.CLOSE_TIMEOUT, TimeUnit.SECONDS);
			}
		});
	}



	@Override
	public void removeHandler(ChannelPipeline channelPipeline) {
		// TODO Auto-generated method stub
		
	}
}
