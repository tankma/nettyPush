package com.easy.nettyPush.room;

import java.util.concurrent.ConcurrentHashMap;

import com.easy.nettyPush.client.IClient;

public class SessionClient {

	private static ConcurrentHashMap<String, IClient> clients = new ConcurrentHashMap<String, IClient>();

	public static IClient getSession(String key) {
		return clients.get(key);
	}

	public static void putSession(String key, IClient client) {
		RoomClient roomClient = Rooms.getInstance().createRoomClient(client.getRoomId());
		roomClient.addClient(key, client);

		clients.put(key, client);
	}

	public static void removeSession(String key) {
		IClient client = getSession(key);
		if(null!=client){
			RoomClient roomClient = Rooms.getInstance().getRoomClient(
					client.getRoomId());
			//System.out.println(roomClient.getClient(key).getSessionId().toString()+"===========remove");
			roomClient.removeClient(key);
			clients.remove(key);
			
		}
	}

	public static boolean containsSessionKey(String key) {
		return clients.containsKey(key);
	}

	public static int size() {
		return clients.size();
	}
}
