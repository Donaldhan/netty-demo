package netty.main.udt.peer;

import io.netty.util.internal.SocketUtils;
import netty.constant.udt.Config;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * UDT Byte Stream Peer two
 * @author donald
 * 2017年7月3日
 * 上午9:02:14
 */
public class ByteEchoPeerTwo extends ByteEchoPeerBase {
	 private static final String PEER_NAME = "peerTwo";
    public ByteEchoPeerTwo(int messageSize, SocketAddress myAddress, SocketAddress peerAddress) {
        super(PEER_NAME,messageSize, myAddress, peerAddress);
    }
    public static void main(String[] args) throws Exception {
        final int messageSize = 64 * 1024;
        final InetSocketAddress myAddress = SocketUtils.socketAddress(Config.hostTwo, Config.portTwo);
        final InetSocketAddress peerAddress = SocketUtils.socketAddress(Config.hostOne, Config.portOne);
        new ByteEchoPeerTwo(messageSize, myAddress, peerAddress).run();
    }
}
