package netty.handler.time;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * @author donald
 * 2017年6月21日
 * 下午12:48:01
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
   private static final Logger log = LoggerFactory.getLogger(TimeServerHandler.class);
   private static final String TIME_PROTOCL = "?time";
   private static final Charset charsetDecoder= Charset.forName("UTF-8");
   /**
    * 读client通道数据，通道处理器上下文ChannelHandlerContext与Mina的会话很像
    */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	ByteBuf in = (ByteBuf)msg;
    	String message = (String) in.readCharSequence(in.writerIndex(), charsetDecoder);
        log.info("===Server reciever message:" +message);
        if(message.equals(TIME_PROTOCL)){
        	//通过通道处理器上下文的ByteBufAllocator创建容量至少为8个字节的ByteBuf
        	ByteBuf time = ctx.alloc().buffer(8);
        	time.writeLong(System.currentTimeMillis());
        	/*
        	在发送数据时，我们并没有调用nio的ByteBuffer#flip类似的方法，这是由于
        	为了避免nio忘记flip操作的问题，Netty通过readIndex和writeIndex两个index
        	表示ByteBuf的相对开始和结束位置；当向ByteBuffer中写数据时，writeIndex将会增长，
        	而readIndex不变。
        	*/
        	/*
        	ctx#write,writeAndFlush方法返回一个写结果ChannelFuture,
        	ChannelFuture表示一个IO事件操作，如果想要在ctx写操作后，关闭连接，不可以用如下方式：
        	Channel ch = ...;
        	ch.writeAndFlush(message);
        	ch.close();
        	因为Netty的写操作时异步的，上面这种关闭连接方式，有可能在消息没发送完前，连接已经关闭，为了
        	能在消息发送完毕后再关闭会话，可以通过添加通道结果监听器，在消息发送完时，触发监听器operationComplete
        	事件。*/
        	
        	final ChannelFuture cfuture = ctx.writeAndFlush(time);
        	final ChannelHandlerContext ctx_refer = ctx;
        	cfuture.addListener(new ChannelFutureListener() {
        	        @Override
        	        public void operationComplete(ChannelFuture future) {
        	            assert cfuture == future;
        	            ctx_refer.close();
        	        }
        	 }); 
        	//上面添加监听器，可以直接使用通道结果监听器内部的CLOSE监听器
        	//cfuture.addListener(ChannelFutureListener.CLOSE);
        }   
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	//异常发生时，关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}