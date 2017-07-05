package netty.main.udt.peer;

import io.netty.util.internal.SocketUtils;
import netty.constant.udt.Config;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * UDT Byte Stream Peer one
 * @author donald
 * 2017年7月3日
 * 上午9:01:58
 */
public class ByteEchoPeerOne extends ByteEchoPeerBase {
    private static final String PEER_NAME = "peerOne";
    public ByteEchoPeerOne(int messageSize, SocketAddress myAddress, SocketAddress peerAddress) {
        super(PEER_NAME,messageSize, myAddress, peerAddress);
    }

    public static void main(String[] args) throws Exception {
        final int messageSize = 64 * 1024;
        final InetSocketAddress myAddress = SocketUtils.socketAddress(Config.hostOne, Config.portOne);
        final InetSocketAddress peerAddress = SocketUtils.socketAddress(Config.hostTwo, Config.portTwo);
        new ByteEchoPeerOne(messageSize, myAddress, peerAddress).run();
    }
}
