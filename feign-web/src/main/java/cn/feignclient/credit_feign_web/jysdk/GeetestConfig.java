package cn.feignclient.credit_feign_web.jysdk;

/**
 * GeetestWeb配置文件
 * 
 *
 */
public class GeetestConfig {

	// 填入自己的captcha_id和private_key
	private static final String geetest_id = "f7882e0d02be7e77a853fe8c527f86d7";
	private static final String geetest_key = "f7cc591d551b1dbdb2d2afc861f44be6";
	private static final boolean newfailback = true;

	public static final String getGeetest_id() {
		return geetest_id;
	}

	public static final String getGeetest_key() {
		return geetest_key;
	}
	
	public static final boolean isnewfailback() {
		return newfailback;
	}

}
