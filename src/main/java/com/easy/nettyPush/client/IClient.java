package com.easy.nettyPush.client;

import java.util.UUID;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;

public interface IClient {

	public void onHeartbeat(HttpRequest request);
	void onDiscount();
	public void writeConnect(HttpRequest request);
	public void writeResponse(HttpRequest request,String text);
	public void writeText(String text);
	public void setChannel(Channel channel);
	public Channel getChannel();
	public String getSessionId();
	public String getRoomId();
}
