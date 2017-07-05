package netty.test.common;

import java.util.Date;

/**
 * 
 * @author donald
 * 2017年6月21日
 * 下午5:33:42
 */
public class TestDay {
	public static void main(String[] args) {
         long nowTime = System.currentTimeMillis();
         Date nowDay = new Date(nowTime);
         System.out.println(nowDay.toLocaleString());
	}
}
