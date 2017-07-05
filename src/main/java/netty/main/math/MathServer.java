package netty.main.math;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import netty.initializer.math.MathServerInitializer;

/**
 * 
 * @author donald
 * 2017年6月21日
 * 下午12:48:17
 */
public class MathServer {
	private static final Logger log = LoggerFactory.getLogger(MathServer.class);
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
    	 * 第一个boss用于处理器监听连接请求，第二个worker用于数据的传输；
    	 * 具体线程是多少依赖于事件loop的具体实现
    	 * */
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
        	//ServerBootstrap，用于配置服务端，一般为ServerSocket通道
            ServerBootstrap serverBoot = new ServerBootstrap(); 
            serverBoot.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) 
             .childHandler(new MathServerInitializer(sslCtx))
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
