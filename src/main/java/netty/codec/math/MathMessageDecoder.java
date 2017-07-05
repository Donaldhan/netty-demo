package netty.codec.math;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import netty.constant.math.ProtocolConstants;
import netty.message.MathMessage;
import util.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 计算协议解码器
 * @author donald
 * 2017年6月22日
 * 下午10:47:31
 */
public class MathMessageDecoder extends ByteToMessageDecoder {
	private static final Logger log = LoggerFactory.getLogger(MathMessageDecoder.class);
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // Wait until the length prefix is available.
    	
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
        if (!protocolCode.equals(ProtocolConstants.SUM_PROTOCOL_300000) &&
        		!protocolCode.equals(ProtocolConstants.MULTI_PROTOCOL_300100) ){
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid protocol code: " + protocolCode);
        }
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        //转换接收的数据为MathMessage
        MathMessage mes = new MathMessage();
        mes.setProtocolCode(protocolCode);
        mes.setDataLenth(dataLength);
        mes.setFirstNum(in.readInt());
        mes.setSecondNum(in.readInt());
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
      log.info("=======解码计算请求协议成功："+JsonUtil.toJson(mes));
    }
}
