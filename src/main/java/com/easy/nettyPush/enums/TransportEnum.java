package com.easy.nettyPush.enums;

import com.easy.nettyPush.upstreamhandler.FlashSocketHandler;
import com.easy.nettyPush.upstreamhandler.WebSocketHandler;
import com.easy.nettyPush.upstreamhandler.XHRPollingHandler;

public enum TransportEnum {
	WEBSOCKET(WebSocketHandler.NAME), FLASHSOCKET(FlashSocketHandler.NAME), XHRPOLLING(
			XHRPollingHandler.NAME);

	private final String value;

	TransportEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
