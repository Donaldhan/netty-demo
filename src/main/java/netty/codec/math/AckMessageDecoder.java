package netty.codec.math;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import netty.constant.math.ProtocolConstants;
import netty.message.AckMessage;
import util.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 计算结果协议解码器
 * @author donald
 * 2017年6月22日
 * 下午10:47:31
 */
public class AckMessageDecoder extends ByteToMessageDecoder {
    private static final Logger log = LoggerFactory.getLogger(AckMessageDecoder.class);
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    	//待数据可用时，解码消息
        int protocolLenth = ProtocolConstants.PROTOCOL_CODE_LENGTH+
        		ProtocolConstants.PROTOCOL_DATA_LENGTH;
        if (in.readableBytes() < protocolLenth) {
            return;
        }
        in.markReaderIndex();
        byte[] protocolCodeBytes = new byte[ProtocolConstants.PROTOCOL_CODE_LENGTH];
        in.readBytes(protocolCodeBytes);
        String protocolCode = "";
		try {
			protocolCode = new String(protocolCodeBytes,ProtocolConstants.CHARSET_UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        if (!protocolCode.equals(ProtocolConstants.ACK_PROTOCOL_300200) &&
        		!protocolCode.equals(ProtocolConstants.ACK_PROTOCOL_300300) ){
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid protocol code: " + protocolCode);
        }
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        //转换接收的数据为MathMessage
        AckMessage mes = new AckMessage();
        mes.setProtocolCode(protocolCode);
        mes.setDataLenth(dataLength);
        mes.setResult(in.readInt());
        byte[] endMarkBytes = new byte[ProtocolConstants.PROTOCOL_END_LENGTH];
        in.readBytes(endMarkBytes);
        String endMark = "";
		try {
			endMark = new String(endMarkBytes,ProtocolConstants.CHARSET_UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		mes.setEndMark(endMark);
        out.add(mes);
        log.info("=======解码计算结果协议成功："+JsonUtil.toJson(mes));
    }
}
