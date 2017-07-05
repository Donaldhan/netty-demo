package netty.handler.udt.peer;

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

/**
 * Handler implementation for the echo peer. It initiates the ping-pong traffic
 * between the echo peers by sending the first message to the other peer on
 * activation.
 * @author donald
 * 2017年7月3日
 * 上午9:32:24
 */
@SuppressWarnings("deprecation")
public class MsgEchoPeerHandler extends SimpleChannelInboundHandler<UdtMessage> {
	private static final Logger log = LoggerFactory.getLogger(MsgEchoPeerHandler.class);
    private final UdtMessage message;

    public MsgEchoPeerHandler(final int messageSize) {
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
    	log.info("===reciever message from UDT Message Flow Peer:" +message);
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
