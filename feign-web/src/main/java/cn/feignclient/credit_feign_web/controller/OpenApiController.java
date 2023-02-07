package cn.feignclient.credit_feign_web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.feignclient.credit_feign_web.service.EmptyCheckService;
import main.java.cn.common.ApiResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.ApiAccountInfoDomain;
import main.java.cn.until.MD5Util;

@RestController
@RequestMapping("/open/empty")
public class OpenApiController extends BaseController {
	
	private final static Logger logger = LoggerFactory.getLogger(OpenApiController.class);
	
	@Autowired
	private EmptyCheckService emptyCheckService;

	@PostMapping("/batch-ucheck")
	public ApiResult batchUcheck(HttpServletRequest request, HttpServletResponse response,
			String appId,String mobiles,String sign) {
		if (StringUtils.isBlank(appId)) {
			return ApiResult.failed(ResultCode.RESULT_PARAM_EXCEPTIONS, "appId不能为空");
		}
		
		if (StringUtils.isBlank(mobiles)) {
			return ApiResult.failed(ResultCode.RESULT_PARAM_EXCEPTIONS, "手机号码不能为空");
		}
		
		if (StringUtils.isBlank(sign)) {
			return ApiResult.failed(ResultCode.RESULT_PARAM_EXCEPTIONS, "sign不能为空");
		}
		
		ApiAccountInfoDomain apiAccountInfoDomain = emptyCheckService.getApiAccountInfoByAppId(appId);
		if (apiAccountInfoDomain == null) {
			return ApiResult.failed(ResultCode.RESULT_API_NOTACCOUNT, "appId不存在");
		}
		
		if (!sign.equals(MD5Util.MD5(appId+apiAccountInfoDomain.getPassword()+mobiles).toLowerCase())) {
			return ApiResult.failed(ResultCode.RESULT_PARAM_EXCEPTIONS, "签名错误");
		}
		
		if (mobiles.length() > 23999) {
			return ApiResult.failed(ResultCode.RESULT_PARAM_EXCEPTIONS, "仅支持单次1-2000个的号码调用");
		}
		
		return emptyCheckService.batchUcheck(mobiles, apiAccountInfoDomain.getCreUserId(),super.getIpAddr(request));
	}
	
	@PostMapping("/get-balance")
	public ApiResult getBalance(HttpServletRequest request, HttpServletResponse response,
			String appId) {
		if (StringUtils.isBlank(appId)) {
			return ApiResult.failed(ResultCode.RESULT_PARAM_EXCEPTIONS, "appId不能为空");
		}
		
		ApiAccountInfoDomain apiAccountInfoDomain = emptyCheckService.getApiAccountInfoByAppId(appId);
		if (apiAccountInfoDomain == null) {
			return ApiResult.failed(ResultCode.RESULT_API_NOTACCOUNT, "appId不存在");
		}
				
		return ApiResult.success(emptyCheckService.getBalance(apiAccountInfoDomain.getCreUserId()), 0);
	}
}
