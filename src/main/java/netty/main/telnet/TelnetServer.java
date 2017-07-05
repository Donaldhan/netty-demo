package netty.main.telnet;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import netty.initializer.telnet.TelnetServerInitializer;

/**
 * textline通信server
 * @author donald
 * 2017年6月30日
 * 上午11:07:08
 */
public final class TelnetServer {
	private static final Logger log = LoggerFactory.getLogger(TelnetServer.class);
	private static final boolean SSL = System.getProperty("ssl") != null;
    private static final  String ip = "10.16.7.107";
    private static final int port = Integer.parseInt(System.getProperty("port", SSL? "10010" : "10020"));
    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBoot = new ServerBootstrap();
            serverBoot.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new TelnetServerInitializer(sslCtx));
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ip,port);
            ChannelFuture f = serverBoot.bind(inetSocketAddress).sync();
            log.info("=========Server is start=========");
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
