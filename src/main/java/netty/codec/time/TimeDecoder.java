package netty.codec.time;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
/**
 * 字节流消息解码器ByteToMessageDecoder，是#ChannelInboundHandler的实现，可以解决粘包问题；
 * 字节消息解码的内部有一个可累计buffer，当有数据到达时，将会调用#decode方法，解码消息，如果累计buffer中
 * 没有足够的数据，则不会添加对象到out，如果有对象添加到out，表示解码器成功解码了一个消息；我们不需要一次解码多个消息，
 * 解码器将会不断地调用#decode方法，直到没有对象可以添加到out。
 * @author donald
 * 2017年6月22日
 * 上午8:55:20
 */
public class TimeDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 8) {
            return;
        }
        out.add(in.readBytes(8));
    }
}
