package cn.feignclient.credit_feign_web.domain;

import java.io.Serializable;

public class FileDomain implements Serializable{

	private static final long serialVersionUID = 6401148377439297273L;
	
	private Integer txtCount;
	
	private String fileUploadUrl;

	public Integer getTxtCount() {
		return txtCount;
	}

	public void setTxtCount(Integer txtCount) {
		this.txtCount = txtCount;
	}

	public String getFileUploadUrl() {
		return fileUploadUrl;
	}

	public void setFileUploadUrl(String fileUploadUrl) {
		this.fileUploadUrl = fileUploadUrl;
	}
	

}
