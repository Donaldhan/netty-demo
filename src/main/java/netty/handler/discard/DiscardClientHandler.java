/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package netty.handler.discard;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * Handles a client-side channel.
 */
public class DiscardClientHandler extends SimpleChannelInboundHandler<Object> {
	private static final Logger log = LoggerFactory.getLogger(DiscardClientHandler.class);
	static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));
    private ByteBuf buffer;
    private ChannelHandlerContext ctxLocal;
    long counter;
    //通道写结果监听器
    private final ChannelFutureListener trafficGenerator = new ChannelFutureListener() {
        public void operationComplete(ChannelFuture future) {
            if (future.isSuccess()) {
            	log.info("====客户端发送消息成功...");
            } else {
                future.cause().printStackTrace();
                future.channel().close();
            }
        }
    };
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.ctxLocal = ctx;
        String message = "Hello Server...";
        try {
        	buffer = ctxLocal.alloc().directBuffer(SIZE).writeBytes(message.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        generateTraffic();
    }  
    /**
     * 发送buffer内容，并将通道结果添加到通道结果监听器
     */
    private void generateTraffic(){
    	 buffer.retainedDuplicate();
         ChannelFuture channelFuture= ctxLocal.writeAndFlush(buffer);
         channelFuture.addListener(trafficGenerator);
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    	ByteBuf in = null;
    	try{
    		in = (ByteBuf)msg;
    		log.info("===Client reciever ack message from Server:" +in.toString(CharsetUtil.UTF_8));
    	}
    	finally{
    		//如果msg为引用计数对象，在使用后注意释放，一般在通道handler中释放
//            ReferenceCountUtil.release(msg);
    	}
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    	//通道是失效时，释放buffer
    	buffer.release();
    }
}
