package netty.codec.math;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import netty.constant.math.ProtocolConstants;
import netty.message.AckMessage;
import util.JsonUtil;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 计算结果协议编码器
 * @author donald
 * 2017年6月22日
 * 下午10:23:21
 */
public class AckMessageEncoder extends MessageToByteEncoder<AckMessage> {
	private static final Logger log = LoggerFactory.getLogger(AckMessageEncoder.class);
    @Override
    protected void encode(ChannelHandlerContext ctx, AckMessage msg, ByteBuf out) {
        try {
			out.writeBytes(msg.getProtocolCode().
					getBytes(ProtocolConstants.CHARSET_UTF8));
			out.writeInt(msg.getDataLenth());
		    out.writeInt(msg.getResult());
		    out.writeBytes(msg.getEndMark().
					getBytes(ProtocolConstants.CHARSET_UTF8));
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        log.info("=======编码计算结果协议成功："+JsonUtil.toJson(msg));
    }
}
