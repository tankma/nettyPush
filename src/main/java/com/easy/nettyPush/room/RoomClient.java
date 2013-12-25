package com.easy.nettyPush.room;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.easy.nettyPush.client.IClient;

public class RoomClient {
private  ConcurrentMap<String, IClient> clients = new ConcurrentHashMap<String,IClient>();
	
	public void addClient(String key,IClient client){
		clients.put(key, client);
	}
	public void removeClient(String key){
		clients.remove(key);
	}
	public IClient getClient(String key){
		return clients.get(key);
	}
	public void replaceClient(String key,IClient newClient){
		if(clients.containsKey(key)){
			clients.put(key, newClient);
		}
	}
	public int size(){
		return clients.size();
	}
	public Collection<IClient> values(){
		return clients.values();
	}
}
