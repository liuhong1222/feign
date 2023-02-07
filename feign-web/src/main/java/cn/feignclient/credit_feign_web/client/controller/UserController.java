package cn.feignclient.credit_feign_web.client.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.feignclient.credit_feign_web.client.service.ApiAccountInfoClientService;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;

@RestController("UserClientController")
@RequestMapping("/web/client/user")
public class UserController extends BaseController {
	
	private final static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private ApiAccountInfoClientService apiAccountInfoFeignService;

	@RequestMapping("/findbyUserName")
	public BackResult<Map<String,Object>> findbyUserName(HttpServletRequest request, HttpServletResponse response,
			String userName) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<Map<String,Object>> result = new BackResult<Map<String,Object>>();
		String token = request.getHeader("token");
		if (CommonUtils.isNotString(userName)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("登录帐号不能为空");
			return result;
			
		}

		if (CommonUtils.isNotString(token)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("token不能为空");
			return result;
		}

		if (!isLogin(userName, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败，请先登录");
			return result;
		} 
		
		BackResult<Map<String,Object>> creUserDomain = userFeignService.findCreUserInfobyUserName(userName);
		if(!ResultCode.RESULT_SUCCEED.equals(creUserDomain.getResultCode())){
			result.setResultCode(creUserDomain.getResultCode());
			result.setResultMsg(creUserDomain.getResultMsg());
			return result;
		}
		
		return creUserDomain;
	}
	
	@RequestMapping("/bindCreUserMail")
	public BackResult<String> bindCreUserMail(HttpServletRequest request, HttpServletResponse response,
			String userName,String userId,String email,String code) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<String> result = new BackResult<String>();
		String token = request.getHeader("token");
		if (CommonUtils.isNotString(userName)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("登录帐号不能为空");
			return result;
			
		}

		if (CommonUtils.isNotString(token)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("token不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("用户id不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(email)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("邮箱不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(code)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("邮箱验证码不能为空");
			return result;
		}

		if (!isLogin(userName, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败，请先登录");
			return result;
		}
		
		String mailCode = redisClinet.get("client:email_code_" + userName);
		if(StringUtils.isBlank(mailCode) || !code.equals(mailCode)){
			result.setResultCode(ResultCode.RESULT_PASSWORD_ERROR);
			result.setResultMsg("验证码输入错误，请重新输入");
			return result;
		}
		BackResult<String> tempResult = userFeignService.bindCreUserMail(userId,email);
		if(!ResultCode.RESULT_SUCCEED.equals(tempResult.getResultCode())){
			result.setResultCode(tempResult.getResultCode());
			result.setResultMsg(tempResult.getResultMsg());
			logger.info("用户【" + userName + "】绑定邮箱失败，失败原因为：" + tempResult.getResultMsg());
			return result;
		}
		
		logger.info("用户【" + userName + "】绑定邮箱成功");
		return tempResult;
	}
}
