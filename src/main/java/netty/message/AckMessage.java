package netty.message;
/**
 * 计算结果协议消息
 * @author donald
 * 2017年6月22日
 * 下午1:04:03
 */
public class AckMessage {
	
	/** 协议编码*/
	private String protocolCode = "";
	/** 协议数据内容长度*/
	private int dataLenth = 0;
	/** 计算结果*/
	private int result = 0;
	/** 协议结束符*/
	private String endMark = "";
	
	public String getProtocolCode() {
		return protocolCode;
	}
	public void setProtocolCode(String protocolCode) {
		this.protocolCode = protocolCode;
	}
	public int getDataLenth() {
		return dataLenth;
	}
	public void setDataLenth(int dataLenth) {
		this.dataLenth = dataLenth;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getEndMark() {
		return endMark;
	}
	public void setEndMark(String endMark) {
		this.endMark = endMark;
	}		
}
