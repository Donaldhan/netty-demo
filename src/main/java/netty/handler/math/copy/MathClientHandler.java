package netty.handler.math.copy;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.constant.math.ProtocolConstants;
import netty.main.math.MathClient;
import netty.message.AckMessage;
import netty.message.MathMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Handler for a client-side channel.  This handler maintains stateful
 * information which is specific to a certain channel using member variables.
 * Therefore, an instance of this handler can cover only one channel.  You have
 * to create a new handler instance whenever you create a new channel and insert
 * this handler to avoid a race condition.
 * @author donald
 * 2017年6月23日
 * 上午12:29:00
 */
public class MathClientHandler extends SimpleChannelInboundHandler<AckMessage> {
	private static final Logger log = LoggerFactory.getLogger(MathClientHandler.class);
    private ChannelHandlerContext ctxLocal;
    private int receivedMesCount;
    private int sendedMesCount = 1;
    final BlockingQueue<AckMessage> ackMessQueue = new LinkedBlockingQueue<AckMessage>();

    public AckMessage getAckMessage() {
        boolean interrupted = false;
        try {
            for (;;) {
                try {
                    return ackMessQueue.take();
                } catch (InterruptedException ignore) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.ctxLocal = ctx;
        sendMathMessages();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, final AckMessage msg) {
    	receivedMesCount ++;
        if (receivedMesCount == MathClient.count) {
            // Offer the answer after closing the connection.
        	ctxLocal.channel().close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    boolean offered = ackMessQueue.offer(msg);
                    assert offered;
                }
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    /**
     * 
     */
    private void sendMathMessages() {
        // Do not send more than 1024 message.
        ChannelFuture future = null;
        for (int i = 0; i < 1024 && sendedMesCount <= MathClient.count; i++) {
        	MathMessage mes = new MathMessage();
        	if(i%2 != 0){
        		mes.setProtocolCode(ProtocolConstants.SUM_PROTOCOL_300000);
        	}
        	else{
        		mes.setProtocolCode(ProtocolConstants.MULTI_PROTOCOL_300100);
        	}
        	mes.setFirstNum(17);
        	mes.setSecondNum(8);
        	mes.setEndMark(ProtocolConstants.PROTOCOL_END);
        	mes.setDataLenth(ProtocolConstants.OPERATE_NUM_LENGTH*2+ProtocolConstants.PROTOCOL_END_LENGTH);
            future = ctxLocal.write(mes);
        	sendedMesCount++;
        }
        if (sendedMesCount <= MathClient.count) {
            assert future != null;
            future.addListener(mathMesSendListener);
        }
        ctxLocal.flush();
    }

    private final ChannelFutureListener mathMesSendListener = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
            	sendMathMessages();
            } else {
                future.cause().printStackTrace();
                future.channel().close();
            }
        }
    };
}
