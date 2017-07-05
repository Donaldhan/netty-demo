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
import netty.handler.udt.peer.MsgEchoPeerHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UDT Message Flow Peer
 * warn: netty 4.1.12 UDT transport is no longer 
 * maintained and will be removed
 * see #netty.main.udt.package-info
 * @author donald
 * 2017年7月3日
 * 上午9:28:32
 */
public abstract class MsgEchoPeerBase {
	private static final Logger log = LoggerFactory.getLogger(MsgEchoPeerBase.class);
	protected final String peerName;
    protected final int messageSize;
    protected final InetSocketAddress self;
    protected final InetSocketAddress peer;

    protected MsgEchoPeerBase(final String peerName,final InetSocketAddress self, final InetSocketAddress peer, final int messageSize) {
        this.peerName = peerName;
    	this.messageSize = messageSize;
        this.self = self;
        this.peer = peer;
    }

    @SuppressWarnings("deprecation")
	public void run() throws Exception {
        // Configure the peer.
        final ThreadFactory connectFactory = new DefaultThreadFactory("rendezvous");
        final NioEventLoopGroup connectGroup = new NioEventLoopGroup(1,
                connectFactory, NioUdtProvider.MESSAGE_PROVIDER);
        try {
            final Bootstrap boot = new Bootstrap();
            boot.group(connectGroup)
                    .channelFactory(NioUdtProvider.MESSAGE_RENDEZVOUS)
                    .handler(new ChannelInitializer<UdtChannel>() {
                        @Override
                        public void initChannel(final UdtChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(
                                    new LoggingHandler(LogLevel.INFO),
                                    new MsgEchoPeerHandler(messageSize));
                        }
                    });
            // Start the peer.
            final ChannelFuture f = boot.connect(peer, self).sync();
            log.info("========="+peerName +" is start=========");
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            connectGroup.shutdownGracefully();
        }
    }
}
