package netty.handler.udt.msg;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.udt.UdtMessage;
import io.netty.channel.udt.nio.NioUdtProvider;
import netty.main.udt.bytes.ByteEchoClient;
import netty.main.udt.msg.MsgEchoClient;

/**
 * 
 * @author donald
 * 2017年7月1日
 * 下午3:59:43
 */
public class MsgEchoClientHandler extends SimpleChannelInboundHandler<UdtMessage> {
	private static final Logger log = LoggerFactory.getLogger(MsgEchoServerHandler.class);
    @SuppressWarnings("deprecation")
	private final UdtMessage message;

    @SuppressWarnings("deprecation")
	public MsgEchoClientHandler() {
        super(false);
        String hello = "Hello UDT Server...";
        ByteBuf msgByteBuf = Unpooled.buffer(ByteEchoClient.SIZE);//堆buffer
        try {
        	msgByteBuf.writeBytes(hello.getBytes("UTF-8"));
        	msgByteBuf.retainedDuplicate();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        //将byteBuffer消息包装证Udt消息
        message = new UdtMessage(msgByteBuf);
    }

    @SuppressWarnings("deprecation")
	@Override
    public void channelActive(final ChannelHandlerContext ctx) {
    	log.info("ECHO active " + NioUdtProvider.socketUDT(ctx.channel()).toStringOptions());
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, UdtMessage msg) {
    	UdtMessage udtMsg = (UdtMessage)msg;
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
    	log.info("===Client reciever ack message from Server:" +message);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        ctx.flush();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
