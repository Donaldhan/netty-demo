package netty.handler.echo;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * Handles a server-side channel.
 * @author donald
 * 2017年6月16日
 * 上午9:36:53
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
   private static final Logger log = LoggerFactory.getLogger(EchoServerHandler.class);
   /**
    * 读client通道数据，通道处理器上下文ChannelHandlerContext与Mina的会话很像
    */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	ByteBuf in = (ByteBuf)msg;
    	byte[] bytes = new byte[in.writerIndex()];
    	in.readBytes(bytes);
    	//针对堆buf，direct buf不支持
//    	byte[] bytes = in.array();
    	String message = null;
		try {
			message = new String(bytes,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	try{
    		log.info("===Server reciever message:" +message);
    	}
    	finally{
    		//如果msg为引用计数对象，在使用后注意释放，一般在通道handler中释放
//            ReferenceCountUtil.release(msg);
    	}
    	String ackMessage = "hello client ...";
    	in.clear();
    	try {
			in.writeBytes(ackMessage.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	ctx.write(in);
        
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	//异常发生时，关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}