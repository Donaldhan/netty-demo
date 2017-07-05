package netty.handler.discard;

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
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
   private static final Logger log = LoggerFactory.getLogger(DiscardServerHandler.class);
   /**
    * 读client通道数据，通道处理器上下文ChannelHandlerContext与Mina的会话很像
    */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	ByteBuf in = null;
    	try{
    		in = (ByteBuf)msg;
    		/*ByteBuffer buffer = ByteBuffer.allocate(1024);
    		in.getBytes(0, buffer);
    		buffer.flip();*/
    		log.info("===Server reciever message:" +in.toString(CharsetUtil.UTF_8));
    	}
    	finally{
    		//如果msg为引用计数对象，在使用后注意释放，一般在通道handler中释放
//            ReferenceCountUtil.release(msg);
    	}
    	 // Initialize the message.
    	ByteBuf out = ctx.alloc().directBuffer(1024);
    	String ackMessage = "hello client ...";
    	try {
			out.writeBytes(ackMessage.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	out.retainedDuplicate();
    	ctx.writeAndFlush(out);
        //这两句与上面一句效果等同
    	/*
    	ctx.write(out);
    	ctx.flush();
    	*/
    	//对于Write(ByteBuf)方法，一般不用自动释放ByteBuf，
    	//ctx会帮我们释放ByteBuf
//    	out.release();
        
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	//异常发生时，关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}