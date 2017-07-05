package netty.initializer.math;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslContext;
import netty.codec.math.AckMessageDecoder;
import netty.codec.math.MathMessageEncoder;
import netty.handler.math.MathClientSumHandler;
import netty.main.math.MathClient;

/**
 * Creates a newly configured {@link ChannelPipeline} for a client-side channel.
 * @author donald
 * 2017年6月23日
 * 下午12:51:44
 */
public class MathClientSumInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public MathClientSumInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), MathClient.ip, MathClient.port));
        }

        // Enable stream compression (you can remove these two if unnecessary)
        pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

        // Add the number codec first,
        pipeline.addLast(new AckMessageDecoder());
        pipeline.addLast(new MathMessageEncoder());

        // and then business logic.
        pipeline.addLast(new MathClientSumHandler());
    }
}
