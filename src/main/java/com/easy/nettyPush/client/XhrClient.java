package com.easy.nettyPush.client;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easy.nettyPush.common.Configuration;
import com.easy.nettyPush.enums.SocketIoTypeEnum;
import com.easy.nettyPush.room.RoomClient;
import com.easy.nettyPush.room.Rooms;
import com.easy.nettyPush.room.SessionClient;

public class XhrClient extends AClient{
	private static Logger logger = LoggerFactory.getLogger(XhrClient.class);
	private BlockingQueue<String> queue;
	public  XhrClient(String sessionId,Channel channel,String roomId){
		super(sessionId,channel,roomId);
		this.queue = new LinkedBlockingQueue<String>();
	}
	@Override
	public void onHeartbeat(HttpRequest request){
		String text = null;
		//System.out.println("============onHeartbeat");
		try {
			text = queue.poll(Configuration.POLLING_DURATION,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(text ==null){
			text = SocketIoTypeEnum.NOOP.getText();
		}
		//System.out.println(channel.isOpen()+"======="+text+"==============text"+sessionId);
		
		try{
			if (channel.isOpen()){
				//logger.error(text);
			   writeResponse(request, text);
			}else {
				System.out.println("==========queueOffer=========="+queue.size());
				queue.offer(text);				
				
			}
		}catch(Exception e){
			e.printStackTrace();
			queue.offer(text);
			System.out.println("==========queueOffer=========="+queue.size());
		}
	}
	@Override
	public void writeText(String text) {
		//System.out.println("======="+text+"==============text"+sessionId);
		
		queue.offer(text);
		
	}
	@Override
	public void onDiscount() {
		RoomClient roomClient = Rooms.getInstance().getRoomClient(
				roomId);
		System.out.println(roomClient.size() + "======1=====remove");
		roomClient.removeClient(sessionId);
		System.out.println(roomClient.size() + "=====2======remove");
		channel.close().addListener(ChannelFutureListener.CLOSE);
	}
	@Override
	public void writeConnect(HttpRequest request) {
		writeResponse(request, SocketIoTypeEnum.CONNECT.getText());
		
	}
	
}
