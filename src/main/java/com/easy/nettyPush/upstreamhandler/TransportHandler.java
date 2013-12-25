package com.easy.nettyPush.upstreamhandler;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easy.nettyPush.client.IClient;
import com.easy.nettyPush.room.RoomClient;
import com.easy.nettyPush.room.Rooms;
import com.easy.util.ExceptionUtil;

public abstract class TransportHandler  extends SimpleChannelUpstreamHandler{
	private static Logger logger = LoggerFactory.getLogger(TransportHandler.class);
	protected String path;
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
    throws Exception{
		System.out.println(ExceptionUtil.getDetailMessage(e.getCause()));
		
		ctx.getChannel().close();
	}
	
	/**
	 * 获取房间ID，如果没有，则返回“ALL”，代表所有人都看到消息
	 * @param queryDecoder
	 * @return
	 */
	protected String getRoomId(QueryStringDecoder queryDecoder){
		Map<String,List<String>> parameters = queryDecoder.getParameters();
		if(null != parameters){
			List<String> list = parameters.get("roomId");
			if(null!=list && list.size()>0){
				return list.get(0);
			}
		}
		return "ALL";
	}
	public void broadcast(String text){
		Pattern pat = Pattern.compile("\\{.*\\}");
		Matcher mat = pat.matcher(text);
		String jsonStr = null;
		if (mat.find()) {
			jsonStr = (mat.group(0));
		}
		//System.out.println(jsonStr);
		JSONObject jsonObject = JSON.parseObject(jsonStr);
	    String roomId = jsonObject.get("name").toString();
		RoomClient roomClient = Rooms.getInstance().getRoomClient(roomId);
		Collection<IClient> values = roomClient.values();
		System.out.println("values.size:"+values.size());
		for (Iterator<IClient> it = values.iterator(); it
				.hasNext();) {
			IClient client = it.next();
			client.writeText(text);
		}
	}
	public abstract void removeHandler(ChannelPipeline channelPipeline);
}
