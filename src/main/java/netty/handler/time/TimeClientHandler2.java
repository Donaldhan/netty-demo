package netty.handler.time;


import java.nio.charset.Charset;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
/**
 *TimeClientHandlerX处理粘包情况，
 *由于网络等原因，有时候我们不能够一次接收一个完成的数据包，我们必须等待完成的数据包，
 *我们可以等待数据包数据完成时，才解析数据。
 *在此示例中，我们接收的是一个8字节的long数据，在网络不佳的情况下，也有可能出现不能一次
 *接收的情况。
 * @author donald
 * 2017年6月21日
 * 下午12:47:53
 */
public class TimeClientHandler2 extends ChannelInboundHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(TimeClientHandler2.class);
	private static final String TIME_PROTOCL = "?time";
	private static final Charset charsetEncoder= Charset.forName("UTF-8").newEncoder().charset();
	private ByteBuf buf;
	/**
	 * 在通道连接建立时（准备传输数据）触发
	 */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    	ByteBuf timeReq = ctx.alloc().buffer(5);
    	timeReq.writeCharSequence(TIME_PROTOCL, charsetEncoder);
    	ctx.writeAndFlush(timeReq);
    }  
    /**
     * Gets called after the {@link ChannelHandler} was added to the 
     * actual context and it's ready to handle events.
     * 在通道处理器添加到实际上下文，准备处理事件时触发，可以用于初始化阻塞时间较短的任务
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        buf = ctx.alloc().buffer(8);
    }
    /**
     * Gets called after the {@link ChannelHandler} was removed from the actual
     *  context and it doesn't handle events anymore.
     * 在通道处理器从实际上下文移除，不再处理事件时触发，可以用于释放初始化任务申请的资源
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        buf.release();
        buf = null;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        
    	ByteBuf in = (ByteBuf)msg;
    	//将所有的字节序列数据累积到buf中
    	buf.writeBytes(in);
    	in.release();
    	/*
    	 待buffer中有足够的数据时，解析数据，否则当更多的数据到达时，netty将会再次调用#channelRead方法
    	 */
    	if(buf.readableBytes()>=8){
    		long nowTime = buf.readLong();
        	Date nowDay = new Date(nowTime);
        	log.info("===Server Time:" +nowDay.toLocaleString());
    	}
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
