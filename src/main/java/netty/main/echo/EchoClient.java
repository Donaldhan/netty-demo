package netty.main.echo;

import java.net.InetSocketAddress;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import netty.handler.echo.EchoClientHandler;
/**
 * 
 * @author donald
 * 2017年6月20日
 * 下午12:44:58
 */
public final class EchoClient {
	private static final Logger log = LoggerFactory.getLogger(EchoClient.class);
	private static final boolean SSL = System.getProperty("ssl") != null;
	private static final String ip = System.getProperty("host", "10.16.7.107");
	private static final int port = Integer.parseInt(System.getProperty("port", "10010"));
    public static void main(String[] args) throws Exception {
       run();
    }
    private static void run() throws SSLException, InterruptedException{
    	 //配置安全套接字上下文
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
        	//Bootstrap，用于配置客户端，一般为Socket通道
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) throws Exception {
                	 //添加安全套接字处理器和通道处理器到
                     ChannelPipeline pipeline = ch.pipeline();
                     if (sslCtx != null) {
                    	 pipeline.addLast(sslCtx.newHandler(ch.alloc(), ip, port));
                     }
                     pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                     pipeline.addLast(new EchoClientHandler());
                 }
             });
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ip,port);
            //连接socket地址
            ChannelFuture f = bootstrap.connect(inetSocketAddress).sync();
            log.info("=========Client is start=========");
            //等待，直到连接关闭
            f.channel().closeFuture().sync();
        } finally {
        	workerGroup.shutdownGracefully();
        }
    }
}
