package netty.handler.udt.msg;

import io.netty.channel.ChannelHandler.Sharable;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.udt.UdtMessage;
import io.netty.channel.udt.nio.NioUdtProvider;
import netty.main.udt.bytes.ByteEchoServer;

/**
 * 
 * @author donald
 * 2017年7月1日
 * 下午4:54:24
 */
@Sharable
public class MsgEchoServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(MsgEchoServerHandler.class);
	@SuppressWarnings("deprecation")
	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
		log.info("ECHO active " +
		 NioUdtProvider.socketUDT(ctx.channel()).toStringOptions());
	}
	@SuppressWarnings("deprecation")
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		UdtMessage udtMsg = (UdtMessage)msg;
		//从udt消息获取消息内存（ByteBuf）
		ByteBuf in = udtMsg.content();
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
    	ctx.write(new UdtMessage(in));
	}
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}
	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
