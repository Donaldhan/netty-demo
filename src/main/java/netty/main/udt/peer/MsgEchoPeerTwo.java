package netty.main.udt.peer;

import io.netty.util.internal.SocketUtils;
import netty.constant.udt.Config;

import java.net.InetSocketAddress;

/**
 * UDT Message Flow Peer two
 * warn: netty 4.1.12 UDT transport is no longer 
 * maintained and will be removed
 * see #netty.main.udt.package-info
 * @author donald
 * 2017年7月3日
 * 上午9:31:24
 */
public class MsgEchoPeerTwo extends MsgEchoPeerBase {
	private static final String PEER_NAME = "msgPeerTwo";
    public MsgEchoPeerTwo(final InetSocketAddress self, final InetSocketAddress peer, final int messageSize) {
        super(PEER_NAME,self, peer, messageSize);
    }
    public static void main(final String[] args) throws Exception {
        final int messageSize = 64 * 1024;
        final InetSocketAddress self = SocketUtils.socketAddress(Config.hostTwo, Config.portTwo);
        final InetSocketAddress peer = SocketUtils.socketAddress(Config.hostOne, Config.portOne);
        new MsgEchoPeerTwo(self, peer, messageSize).run();
    }
}
