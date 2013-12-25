package com.easy.nettyPush.common;

import java.util.ResourceBundle;

public class Configuration {
	static ResourceBundle bundle = ResourceBundle.getBundle("push");
	public static String TRANSPORTS = bundle.getString("transports");
	
	public static int HEARTBEAT_TIMEOUT = Integer.parseInt(bundle.getString("heartbeatTimeout"));
	public static int HEARTBEAT_INTERVAL = Integer.parseInt(bundle.getString("heartbeatInterval"));
	public static int CLOSE_TIMEOUT = Integer.parseInt(bundle.getString("closeTimeout"));
	public static int POLLING_DURATION = Integer.parseInt(bundle.getString("pollingDuration"));
	public static int HEARTBEAT_THREADPOOL_SIZE = Runtime.getRuntime()
	.availableProcessors() * 2;
	public static int maxHttpContentLength = 64 * 1024;

}
