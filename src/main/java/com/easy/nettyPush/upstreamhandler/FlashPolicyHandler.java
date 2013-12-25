package com.easy.nettyPush.upstreamhandler;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.util.CharsetUtil;

import com.easy.nettyPush.pipelinefactory.NettyPushPipelineFactory;

@Sharable
public class FlashPolicyHandler extends SimpleChannelUpstreamHandler {

    private final ChannelBuffer requestBuffer = ChannelBuffers.copiedBuffer("<policy-file-request/>", CharsetUtil.UTF_8);
    private final ChannelBuffer responseBuffer = ChannelBuffers.copiedBuffer(
                            "<?xml version=\"1.0\"?>"
                            + "<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">"
                            + "<cross-domain-policy> "
                            + "   <site-control permitted-cross-domain-policies=\"master-only\"/>"
                            + "   <allow-access-from domain=\"*\" to-ports=\"*\" />"
                            + "</cross-domain-policy>", CharsetUtil.UTF_8);


    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
    	System.out.println("====FlashPolicyHandler===========");
        ChannelBuffer inBuffer = (ChannelBuffer) e.getMessage();
        ChannelBuffer data = inBuffer.slice(0, requestBuffer.readableBytes());
        if (data.equals(requestBuffer)) {
            ChannelFuture f = e.getChannel().write(responseBuffer);
            f.addListener(ChannelFutureListener.CLOSE);
            return;
        }
        ctx.getPipeline().remove(NettyPushPipelineFactory.FLASH_POLICY);
        //System.out.println("===FlashPolicyHandler");
        super.messageReceived(ctx, e);
    }
}
