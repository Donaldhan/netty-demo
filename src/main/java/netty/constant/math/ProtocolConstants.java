package netty.constant.math;
/**
 * 协议常量
 * @author donald
 * 2017年6月22日
 * 下午1:10:11
 */
public class ProtocolConstants {
	/**
	 * 加法协议编码
	 */
	public static final String SUM_PROTOCOL_300000 = "300000";
	/**
	 * 乘法协议编码
	 */
	public static final String MULTI_PROTOCOL_300100 = "300100";
	/**
	 * 计算成功协议
	 */
	public static final String ACK_PROTOCOL_300200 = "300200";
	/**
	 * 服务器解析协议失败
	 */
	public static final String ACK_PROTOCOL_300300 = "300300";
	/**
	 * 协议编码长度
	 */
	public static final Integer PROTOCOL_CODE_LENGTH = 6;
	/**
	 * 协议内容长度字段
	 */
	public static final Integer PROTOCOL_DATA_LENGTH = 4;
	/**
	 * 协议操作数长度
	 */
	public static final Integer OPERATE_NUM_LENGTH = 4;
	/**
	 * 协议计算结果长度
	 */
	public static final Integer PROTOCOL_ACK_LENGTH = 4;
	/**
	 * 协议结束符
	 */
	public static final String PROTOCOL_END = "\r\n";
	/**
	 * 协议结束符长度
	 */
	public static final Integer PROTOCOL_END_LENGTH = 2;
	/**
	 * 字符集
	 */
	public static final String CHARSET_UTF8 = "UTF-8";
}
