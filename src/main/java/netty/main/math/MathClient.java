package netty.main.math;

import java.net.InetSocketAddress;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import netty.handler.math.MathClientHandler;
import netty.initializer.math.MathClientInitializer;
import util.JsonUtil;
/**
 * 
 * @author donald
 * 2017年6月21日
 * 下午12:48:10
 */
public final class MathClient {
	private static final Logger log = LoggerFactory.getLogger(MathClient.class);
	private static final boolean SSL = System.getProperty("ssl") != null;
	public static final String ip = System.getProperty("host", "10.16.7.107");
	public static final int port = Integer.parseInt(System.getProperty("port", "10010"));
	public static final int count = Integer.parseInt(System.getProperty("count", "2"));
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
        	//Bootstrap与 ServerBootstrap相似，不同的是Bootstrap用于配置客户端，
        	//一般为Socket通道，或无连接通道
            Bootstrap bootstrap = new Bootstrap();
            //EventLoopGroup有 boss和worker两组,对于客户端只需要用worker
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new MathClientInitializer(sslCtx));
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ip,port);
            //连接socket地址
            ChannelFuture f = bootstrap.connect(inetSocketAddress).sync();
            log.info("=========Client is start=========");
            // Get the handler instance to retrieve the answer.
            MathClientHandler handler =
                (MathClientHandler) f.channel().pipeline().last();
            // Print out the answer.
            log.info("=======Calculat result:"+JsonUtil.toJson(handler.getAckMessage()));
            //等待，直到连接关闭
            f.channel().closeFuture().sync();
        } finally {
        	workerGroup.shutdownGracefully();
        }
    }
}
