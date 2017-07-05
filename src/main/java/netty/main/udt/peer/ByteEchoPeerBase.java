package netty.main.udt.peer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import netty.handler.udt.peer.ByteEchoPeerHandler;
import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UDT Byte Stream Peer
 * @author donald
 * 2017年7月3日
 * 上午9:01:35
 */
public class ByteEchoPeerBase {
	private static final Logger log = LoggerFactory.getLogger(ByteEchoPeerBase.class);
	protected final String peerName;
    protected final int messageSize;
    protected final SocketAddress myAddress;
    protected final SocketAddress peerAddress;

    public ByteEchoPeerBase(String peerName,int messageSize, SocketAddress myAddress, SocketAddress peerAddress) {
        this.peerName = peerName; 
    	this.messageSize = messageSize;
        this.myAddress = myAddress;
        this.peerAddress = peerAddress;
    }

    @SuppressWarnings("deprecation")
	public void run() throws Exception {
        final ThreadFactory connectFactory = new DefaultThreadFactory("rendezvous");
        final NioEventLoopGroup connectGroup = new NioEventLoopGroup(1,
                connectFactory, NioUdtProvider.BYTE_PROVIDER);
        try {
            final Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(connectGroup)
                    .channelFactory(NioUdtProvider.BYTE_RENDEZVOUS)
                    .handler(new ChannelInitializer<UdtChannel>() {
                        @Override
                        protected void initChannel(UdtChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LoggingHandler(LogLevel.INFO),
                                    new ByteEchoPeerHandler(messageSize));
                        }
                    });
            final ChannelFuture future = bootstrap.connect(peerAddress, myAddress).sync();
            log.info("========="+peerName +" is start=========");
            future.channel().closeFuture().sync();
        } finally {
            connectGroup.shutdownGracefully();
        }
    }
}
