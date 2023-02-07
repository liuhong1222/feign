package cn.feignclient.credit_feign_web.domain;

import java.io.Serializable;

public class TokenUserDomain implements Serializable{

	private static final long serialVersionUID = 7358757345381118714L;
	private String token;
	
	private String isInitPwd;
	
	private String userName;
	
	private String domain;
	
	private String mobile;
	
	private String userId;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getIsInitPwd() {
		return isInitPwd;
	}

	public void setIsInitPwd(String isInitPwd) {
		this.isInitPwd = isInitPwd;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
}
