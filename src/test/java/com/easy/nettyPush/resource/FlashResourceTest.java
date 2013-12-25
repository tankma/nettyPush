package com.easy.nettyPush.resource;

import java.io.File;

import org.junit.Test;

import com.easy.nettyPush.common.Common;

public class FlashResourceTest {

	@Test
	public void testGetFlashResource() {
		File f = FlashResource.getFlashResource(Common.CONTEXT + "/static/flashsocket/WebSocketMain.swf");
		System.out.println(f.isFile());
	}

}
