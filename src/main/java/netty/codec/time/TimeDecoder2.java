package netty.codec.time;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
/**
 * 回复解码器ReplayingDecoder为字节流消息解码器ByteToMessageDecoder的实现
 * @author donald
 * 2017年6月22日
 * 上午8:55:20
 */
public class TimeDecoder2 extends ReplayingDecoder<Void> {
    @Override
    protected void decode(
            ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        out.add(in.readBytes(8));
    }
}