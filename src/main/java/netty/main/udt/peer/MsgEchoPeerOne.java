package netty.main.udt.peer;

import io.netty.util.internal.SocketUtils;
import netty.constant.udt.Config;

import java.net.InetSocketAddress;

/**
 * UDT Message Flow Peer one
 * warn: netty 4.1.12 UDT transport is no longer 
 * maintained and will be removed
 * see #netty.main.udt.package-info
 * @author donald
 * 2017年7月3日
 * 上午9:30:28
 */
public class MsgEchoPeerOne extends MsgEchoPeerBase {
	private static final String PEER_NAME = "msgPeerOne";
    public MsgEchoPeerOne(final InetSocketAddress self, final InetSocketAddress peer, final int messageSize) {
        super(PEER_NAME,self, peer, messageSize);
    }

    public static void main(final String[] args) throws Exception {
        final int messageSize = 64 * 1024;
        final InetSocketAddress self = SocketUtils.socketAddress(Config.hostOne, Config.portOne);
        final InetSocketAddress peer = SocketUtils.socketAddress(Config.hostTwo, Config.portTwo);
        new MsgEchoPeerOne(self, peer, messageSize).run();
    }
}
