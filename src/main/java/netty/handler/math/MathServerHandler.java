package netty.handler.math;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.constant.math.ProtocolConstants;
import netty.message.AckMessage;
import netty.message.MathMessage;

/**
 * Handler for a server-side channel.  This handler maintains stateful
 * information which is specific to a certain channel using member variables.
 * Therefore, an instance of this handler can cover only one channel.  You have
 * to create a new handler instance whenever you create a new channel and insert
 * this handler  to avoid a race condition.
 * @author donald
 * 2017年6月23日
 * 上午12:21:24
 */
public class MathServerHandler extends SimpleChannelInboundHandler<MathMessage> {
	private static final Logger log = LoggerFactory.getLogger(MathServerHandler.class);
    private MathMessage mathMes = new MathMessage();
    private AckMessage ackMes   = new AckMessage();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, MathMessage msg) throws Exception {
    	mathMes = msg;
    	if(!mathMes.getEndMark().equals(ProtocolConstants.PROTOCOL_END)){
    		ackMes.setProtocolCode(ProtocolConstants.ACK_PROTOCOL_300300);
    	}
    	else{
    		ackMes.setProtocolCode(ProtocolConstants.ACK_PROTOCOL_300200);
    	}
    	String protocolCode = mathMes.getProtocolCode();
    	int result = 0;
    	if(protocolCode.equals(ProtocolConstants.SUM_PROTOCOL_300000)){
    		result = mathMes.getFirstNum() + mathMes.getSecondNum();
    	}
    	if(protocolCode.equals(ProtocolConstants.MULTI_PROTOCOL_300100)){
    		result = mathMes.getFirstNum() * mathMes.getSecondNum();
    	}
    	ackMes.setResult(result);
    	ackMes.setEndMark(ProtocolConstants.PROTOCOL_END);
    	ackMes.setDataLenth(ProtocolConstants.OPERATE_NUM_LENGTH+ProtocolConstants.PROTOCOL_END_LENGTH);
        ctx.writeAndFlush(ackMes);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	ackMes = null;
    	mathMes	= null;	
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
