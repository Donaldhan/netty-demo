package netty.handler.udt.peer;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.udt.nio.NioUdtProvider;
import netty.handler.udt.bytes.ByteEchoClientHandler;
import netty.main.udt.bytes.ByteEchoClient;


/**
 * Handler implementation for the echo client. It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server on activation.
 * @author donald
 * 2017年7月3日
 * 上午9:07:13
 */
public class ByteEchoPeerHandler extends SimpleChannelInboundHandler<ByteBuf> {
	private static final Logger log = LoggerFactory.getLogger(ByteEchoPeerHandler.class);
    private final ByteBuf message;
    public ByteEchoPeerHandler(final int messageSize) {
        super(false);
		String hello = "Hello peer...";
		message = Unpooled.buffer(ByteEchoClient.SIZE);//堆buffer
        try {
        	message.writeBytes(hello.getBytes("UTF-8"));
        	message.retainedDuplicate();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    	log.info("ECHO active " + NioUdtProvider.socketUDT(ctx.channel()).toStringOptions());
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
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
    	log.info("===reciever message from UDT Byte Stream Peer:" +message);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
