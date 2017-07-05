package netty.handler.echo;


import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * @author donald
 * 2017年6月20日
 * 下午12:45:04
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(EchoClientHandler.class);
	private final ByteBuf firstMessage;
	public EchoClientHandler() {
		String message = "Hello Server...";
        firstMessage = Unpooled.buffer(1024);//堆buffer
        try {
			firstMessage.writeBytes(message.getBytes("UTF-8"));
			firstMessage.retainedDuplicate();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.write(firstMessage);
        ctx.flush();
    }  
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	ByteBuf in = (ByteBuf)msg;
    	byte[] bytes = new byte[in.writerIndex()];
    	in.readBytes(bytes);
    	//针对堆buf，direct buf不支持
//    	byte[] bytes = in.array();
    	String message = new String(bytes,"UTF-8");
    	log.info("===Client reciever ack message from Server:" +message);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
