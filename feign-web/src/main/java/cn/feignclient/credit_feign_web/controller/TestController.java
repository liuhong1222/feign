package cn.feignclient.credit_feign_web.controller;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import cn.feignclient.credit_feign_web.service.ContractService;
import cn.feignclient.credit_feign_web.service.CreditProviderService;
import cn.feignclient.credit_feign_web.service.OpenApiService;
import cn.feignclient.credit_feign_web.service.UserAuthService;
import cn.feignclient.credit_feign_web.utils.DateUtils;
import cn.feignclient.credit_feign_web.utils.FileUtils;
import cn.feignclient.credit_feign_web.utils.UUIDTool;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.FileUploadDomain;
import main.java.cn.domain.carriers.LicenseResultDomain;
import main.java.cn.until.Base64Img;

@RestController
@RequestMapping("/web/test")
public class TestController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(TestController.class);

	@Autowired
	private ContractService contractService;
	
	@Autowired
	private UserAuthService userAuthService;

	// 文件上传相关代码
	@RequestMapping("/test")
	@ResponseBody
	public BackResult<String> upload(HttpServletRequest request, HttpServletResponse response,
			String userId, String userType, String token) {

		BackResult<String> result = new BackResult<String>();
		BackResult<String> contractData = userAuthService.getUserContractData(userId, userType);
		result = contractService.getPdfFileByHtml(userId);
		return result;
		}

	public static void main(String[] args) {
		String fileName = "444.txt";
		// 获取文件的后缀名
//		String suffixName = fileName.substring(fileName.lastIndexOf("."));
		System.out.println(fileName.substring(0,fileName.lastIndexOf(".")));
	}
}
