package com.easy.nettyPush.resource;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.easy.nettyPush.common.Common;

public class FlashResource {

	private final static Map<String, File> resources = new HashMap<String, File>();
	static {
		addResource(Common.CONTEXT + "/static/flashsocket/WebSocketMain.swf",
				"/static/flashsocket/WebSocketMain.swf");
		addResource(Common.CONTEXT
				+ "/static/flashsocket/WebSocketMainInsecure.swf",
				"/static/flashsocket/WebSocketMainInsecure.swf");
	}

	private static void addResource(String pathPart, String resourcePath) {
		URL resource = FlashResource.class.getResource(resourcePath);
		File file = new File(resource.getFile());
		resources.put(pathPart, file);
	}
	public static File getFlashResource(String path){
		return resources.get(path);
	}
}
