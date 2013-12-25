package com.easy.nettyPush.enums;

public enum SocketIoTypeEnum {
	DISCONNECT(0,"0::"), CONNECT(1,"1::"), HEARTBEAT(2,"2::"), MESSAGE(3,"3::"), JSON(4,"4::"), EVENT(5,"5::"), ACK(6,"6::"), ERROR(7,"7::"), NOOP(8,"8::");
	private final int value;
	private final String text;
	SocketIoTypeEnum(int value,String text){
		this.value = value;
		this.text = text;
	}
	public int getValue() {
		return value;
	}
	public String getText() {
		return text;
	}
}
