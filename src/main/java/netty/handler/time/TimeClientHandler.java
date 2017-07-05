package netty.handler.time;


import java.nio.charset.Charset;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
/**
 * 
 * @author donald
 * 2017年6月21日
 * 下午12:47:53
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(TimeClientHandler.class);
	private static final String TIME_PROTOCL = "?time";
	private static final Charset charsetEncoder= Charset.forName("UTF-8");
	/**
	 * 在通道连接建立时（准备传输数据）触发
	 */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    	ByteBuf timeReq = ctx.alloc().buffer(5);
    	timeReq.writeCharSequence(TIME_PROTOCL, charsetEncoder);
    	ctx.writeAndFlush(timeReq);
    }  
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	ByteBuf in = (ByteBuf)msg;
    	long nowTime = in.readLong();
    	Date nowDay = new Date(nowTime);
    	log.info("===Server Time:" +nowDay.toLocaleString());
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
