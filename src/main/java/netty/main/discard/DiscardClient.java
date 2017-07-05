/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package netty.main.discard;

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
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import netty.handler.discard.DiscardClientHandler;

/**
 * Keeps sending random data to the specified address.
 */
public final class DiscardClient {
	private static final Logger log = LoggerFactory.getLogger(DiscardClient.class);
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
                     pipeline.addLast(new DiscardClientHandler());
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
