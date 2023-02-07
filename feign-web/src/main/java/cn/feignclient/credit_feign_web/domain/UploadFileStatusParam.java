package cn.feignclient.credit_feign_web.domain;

public class UploadFileStatusParam {

    private String md5;

    private Integer chunks;

    private String fileName;

    private Integer productCode;
    
    private String mobile;
    
    private String token;

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public Integer getChunks() {
		return chunks;
	}

	public void setChunks(Integer chunks) {
		this.chunks = chunks;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getProductCode() {
		return productCode;
	}

	public void setProductCode(Integer productCode) {
		this.productCode = productCode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
