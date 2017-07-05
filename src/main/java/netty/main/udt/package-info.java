/**
 * Examples show how to use UDT Byte Streams and Message Flow.
 * the UDT Message Flow demo in the peer & msg package will appear the error :
 * java.lang.AbstractMethodError: java.nio.channels.SocketChannel.bind
 * for:
 * NioUdtMessageConnectorChannel is @deprecated
 * Message Connector for UDT Datagrams.
 * Note: send/receive must use {@link UdtMessage} in the pipeline
 * @deprecated The UDT transport is no longer maintained and will be removed.
 * see io.netty.channel.udt.nio.NioUdtMessageConnectorChannel
 */
package netty.main.udt;

