package netty.message;
/**
 * 计算协议消息
 * @author donald
 * 2017年6月22日
 * 下午1:03:27
 */
public class MathMessage {
	/** 协议编码*/
	private String protocolCode = "";
	/** 协议数据内容长度*/
	private int dataLenth = 0;
	/** 左操作数*/
	private int firstNum = 0;
	/** 右操作数*/
	private int secondNum = 0;
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
	public int getFirstNum() {
		return firstNum;
	}
	public void setFirstNum(int firstNum) {
		this.firstNum = firstNum;
	}
	public int getSecondNum() {
		return secondNum;
	}
	public void setSecondNum(int secondNum) {
		this.secondNum = secondNum;
	}
	public String getEndMark() {
		return endMark;
	}
	public void setEndMark(String endMark) {
		this.endMark = endMark;
	}	
}
