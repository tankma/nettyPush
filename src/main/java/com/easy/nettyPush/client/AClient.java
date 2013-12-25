package com.easy.nettyPush.client;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import com.easy.util.HttpUtil;

public abstract class AClient implements IClient{

	protected String sessionId;
	protected Channel channel;
	protected String roomId;
	public  AClient(String sessionId,Channel channel,String roomId){
		this.sessionId = sessionId;
		this.channel = channel;
		this.roomId = roomId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	public void writeResponse(HttpRequest request,String text){
		HttpResponse res = HttpUtil.getInitResponse(request);
		ChannelBuffer message = ChannelBuffers.copiedBuffer(text,
				CharsetUtil.UTF_8);
		res.setContent(message);
		HttpHeaders.setContentLength(res, message.readableBytes());
		//System.out.println(channel.isOpen()+"=============");
		try{
			channel.write(res).addListener(ChannelFutureListener.CLOSE);
		}catch(Exception e){
			System.out.println("=======Exception=========");
			e.printStackTrace();
		}
		
	}
}
