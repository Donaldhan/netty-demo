package netty.main.echo;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import netty.handler.echo.EchoServerHandler;

/**
 * Discards any incoming data.
 * @author donald
 * 2017年6月16日
 * 上午9:39:53
 */
public class EchoServer {
	private static final Logger log = LoggerFactory.getLogger(EchoServer.class);
	 static final boolean SSL = System.getProperty("ssl") != null;
	private static final  String ip = "10.16.7.107";
	private static final  int port = 10010;
    public static void main(String[] args) throws Exception {
      run();
    }
    public static void run() throws Exception {
    	 // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

    	/*
    	 * EventLoopGroup（多线程事件loop），处理IO操作，这里我们用了两个事件loop
    	 * 第一个用于处理器监听连接请求，第二个用于数据的传输；
    	 * 具体线程是多少依赖于事件loop的具体实现
    	 * */
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
        	//ServerBootstrap，用于配置服务端，一般为ServerSocket通道
            ServerBootstrap serverBoot = new ServerBootstrap(); 
            serverBoot.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) 
             .childHandler(new ChannelInitializer<SocketChannel>() { 
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                	//添加通道处理器到通道关联的管道，准确的中文翻译为管道线， 此管道线与Mina中过滤链十分相似,
                	//ChannelInitializer用于配置通道的管道线，ChannelPipeline
                	 ChannelPipeline pipeline = ch.pipeline();
                     if (sslCtx != null) {
                    	 pipeline.addLast(sslCtx.newHandler(ch.alloc()));
                     }
                     pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                     pipeline.addLast(new EchoServerHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)//socket监听器连接队列大小、
             .childOption(ChannelOption.SO_KEEPALIVE, true); //保活，此配置针对ServerSocket通道接收连接产生的Socket通道
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ip,port);
            // 绑定地址，开始监听
            ChannelFuture f = serverBoot.bind(inetSocketAddress).sync();
            log.info("=========Server is start=========");
            //等待，直到ServerSocket关闭
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
  
}
