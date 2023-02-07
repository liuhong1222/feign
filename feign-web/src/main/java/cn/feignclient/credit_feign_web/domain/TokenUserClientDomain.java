package cn.feignclient.credit_feign_web.domain;

import java.io.Serializable;

public class TokenUserClientDomain implements Serializable{

	private static final long serialVersionUID = 7358757345381118714L;
	private String token;
	
	private String isInitPwd;
	
	private String isBindMail;
	
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

	public String getIsBindMail() {
		return isBindMail;
	}

	public void setIsBindMail(String isBindMail) {
		this.isBindMail = isBindMail;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
