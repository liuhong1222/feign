package cn.feignclient.credit_feign_web.domain;

/**
 * 极验 验证码
 *
 */
public class JyDomain {

	private String success;
	
	private String challenge;
	
	private String gt;

	/**
	 * @return the success
	 */
	public String getSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(String success) {
		this.success = success;
	}

	/**
	 * @return the challenge
	 */
	public String getChallenge() {
		return challenge;
	}

	/**
	 * @param challenge the challenge to set
	 */
	public void setChallenge(String challenge) {
		this.challenge = challenge;
	}

	/**
	 * @return the gt
	 */
	public String getGt() {
		return gt;
	}

	/**
	 * @param gt the gt to set
	 */
	public void setGt(String gt) {
		this.gt = gt;
	}
	
	
}
