package com.easy.nettyPush.upstreamhandler;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.List;
import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easy.nettyPush.client.IClient;
import com.easy.nettyPush.common.Common;
import com.easy.nettyPush.common.Configuration;
import com.easy.nettyPush.room.SessionClient;
import com.easy.nettyPush.room.RoomClient;
import com.easy.nettyPush.room.Rooms;
import com.easy.nettyPush.scheduler.CancelableScheduler;
import com.easy.nettyPush.scheduler.SchedulerKey;
import com.easy.nettyPush.scheduler.SchedulerKey.Type;
import com.easy.util.ExceptionUtil;

@Sharable
public class AuthorizeHandler extends SimpleChannelUpstreamHandler {
	private static Logger logger = LoggerFactory
			.getLogger(AuthorizeHandler.class);

	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		 logger.error("AuthorizeHandler进来一个channel：" + ctx.getChannel().getId());
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		String id = String.valueOf(ctx.getChannel().getId());
		 logger.error("A关掉一个channel：" + id);
		int size = SessionClient.size();
		if (SessionClient.containsSessionKey(id)) {
			SchedulerKey hearbeatKey = new SchedulerKey(Type.HEARBEAT_TIMEOUT, SessionClient.getSession(id).getSessionId());
			CancelableScheduler.cancel(hearbeatKey);
			SessionClient.removeSession(id);
			
		}
		// System.out.println(size+"===="+SessionClient.size()+"===========sidStore=size");
	}

	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		logger
				.error("=======AuthorizeHandler====exceptionCaught====================");
		logger.error(ExceptionUtil.getDetailMessage(e.getCause()));
		ctx.getChannel().close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object msg = e.getMessage();
		
		if (msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;
			QueryStringDecoder queryDecoder = new QueryStringDecoder(req
					.getUri());
			 
			// 如果不是以“/socket.io/1”开头的请求，则返回失败
			if (!queryDecoder.getPath().startsWith(Common.START_PATH)) {
				HttpResponse res = new DefaultHttpResponse(HTTP_1_1,
						HttpResponseStatus.BAD_REQUEST);
				ChannelFuture f = ctx.getChannel().write(res);
				f.addListener(ChannelFutureListener.CLOSE);
				return;
			}
			if (queryDecoder.getPath().equals(Common.START_PATH)) {
				connect(req, e, req.getUri());
				return;
			}
		}
		ctx.sendUpstream(e);
	}

	private void connect(HttpRequest req, MessageEvent e, String reqURI) {
		// 生成随机数
		String sessionId = UUID.randomUUID().toString();
		String contentString = sessionId + ":"
				+ Configuration.HEARTBEAT_TIMEOUT + ":"
				+ Configuration.CLOSE_TIMEOUT + ":" + Configuration.TRANSPORTS;

		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(reqURI);
		String jsonpValue = getParameter(queryStringDecoder, "jsonp");

		if (jsonpValue != null) {
			logger.debug("request uri with parameter jsonp = " + jsonpValue);
			contentString = "io.j[" + jsonpValue + "]('" + contentString
					+ "');";
		}
		ChannelBuffer content = ChannelBuffers.copiedBuffer(contentString,
				CharsetUtil.UTF_8);

		String origin = req.getHeader(HttpHeaders.Names.ORIGIN);
		HttpResponse res = new DefaultHttpResponse(HTTP_1_1,
				HttpResponseStatus.OK);
		addHeaders(origin, res);

		res.setContent(content);
		HttpHeaders.setContentLength(res, res.getContent().readableBytes());

		ChannelFuture f = e.getChannel().write(res);
		f.addListener(ChannelFutureListener.CLOSE);
	}

	private void addHeaders(String origin, HttpResponse res) {
		res.addHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
		res.addHeader(CONNECTION, KEEP_ALIVE);
		if (origin != null) {
			res.addHeader("Access-Control-Allow-Origin", origin);
			res.addHeader("Access-Control-Allow-Credentials", "true");
		}
	}

	private String getParameter(QueryStringDecoder queryStringDecoder,
			String parameterName) {
		if (queryStringDecoder == null || parameterName == null) {
			return null;
		}
		List<String> values = (List<String>) queryStringDecoder.getParameters()
				.get(parameterName);

		if ((values == null) || (values.isEmpty())) {
			return null;
		}
		return (String) values.get(0);
	}
}
