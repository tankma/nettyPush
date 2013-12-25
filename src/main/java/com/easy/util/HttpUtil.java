package com.easy.util;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE;

import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class HttpUtil {

	public static HttpResponse getInitResponse(HttpRequest req)
	  {
	    return getInitResponse(req, HttpResponseStatus.OK);
	  }

	  public static HttpResponse getInitResponse(HttpRequest req, HttpResponseStatus status)
	  {
	    HttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, 
	      status);
	    resp.addHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
		resp.addHeader(CONNECTION, KEEP_ALIVE);
	    if ((req != null) && (req.getHeader("Origin") != null)) {
	      resp.addHeader("Access-Control-Allow-Origin", 
	        req.getHeader("Origin"));
	      resp.addHeader("Access-Control-Allow-Credentials", "true");
	    }

	    return resp;
	  }
}
