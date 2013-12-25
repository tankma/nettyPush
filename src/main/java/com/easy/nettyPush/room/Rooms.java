package com.easy.nettyPush.room;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Rooms {

	private static Rooms room = new Rooms();
	private static ConcurrentMap<String, RoomClient> roomClients = new ConcurrentHashMap<String, RoomClient>();

	private Rooms(){}
	public static Rooms getInstance(){
		return room;
	}
	public RoomClient createRoomClient(String name) {
		RoomClient roomClient = roomClients.get(name);
		if (roomClient == null) {
			roomClient = new RoomClient();
			RoomClient oldRoomClient = roomClients.putIfAbsent(name,
					roomClient);
			if (oldRoomClient != null) {
				roomClient = oldRoomClient;
			}
		}
		return roomClient;
	}
    public RoomClient getRoomClient(String key){
    	return roomClients.get(key);
    }
	public void removeRoomClient(String key) {
		roomClients.remove(key);
	}
	public int size(){
		return roomClients.size();
	}

}
