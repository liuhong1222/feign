package cn.feignclient.credit_feign_web.dao;

import java.io.Serializable;

/**
 * 在线检测结果
 *
 */
public class TestHistory implements Serializable{

	private String date; //日期
	
	private String real; //实号
	
	private String empty; //空号
	
	private String shut; //风险号
	
	private String silence; //沉默号
	
	private String total; //总条数

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getReal() {
		return real;
	}

	public void setReal(String real) {
		this.real = real;
	}

	public String getEmpty() {
		return empty;
	}

	public void setEmpty(String empty) {
		this.empty = empty;
	}

	public String getShut() {
		return shut;
	}

	public void setShut(String shut) {
		this.shut = shut;
	}

	public String getSilence() {
		return silence;
	}

	public void setSilence(String silence) {
		this.silence = silence;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}
}
