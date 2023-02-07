package cn.feignclient.credit_feign_web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;

@RestController
@RequestMapping("/web/apiInvokingTest")
public class ApiInvokingTestController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(ApiInvokingTestController.class);
	
	/**
	 * 获取用户接口消耗量
	 * 
	 * @param request
	 * @param response
	 * @param paramJson
	 * @return
	 */
	@RequestMapping(value = "/getCreUserApiConsumeCounts", method = RequestMethod.POST)
	public BackResult<List<Map<String,Object>>> getCreUserApiConsumeCounts(HttpServletRequest request,
			HttpServletResponse response, @RequestBody JSONObject paramJson) {
		//返回结果
		BackResult<List<Map<String,Object>>> result = new BackResult<List<Map<String,Object>>>();
		System.currentTimeMillis();
		String userId = paramJson.getString("userId");
		String productName = paramJson.getString("productName");
		String month = paramJson.getString("month");
		String mobile = paramJson.getString("mobile");
		String token = paramJson.getString("token");
		
		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户校验失败");
			return result;
		}
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("用户id不能为空不能为空");
			return result;
		}

		if (CommonUtils.isNotString(productName)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("产品名称不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(month)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("查询月份不能为空");
			return result;
		}
		
		result = userFeignService.getCreUserApiConsumeCounts(userId, productName, month);
		return result;
	}	
}
