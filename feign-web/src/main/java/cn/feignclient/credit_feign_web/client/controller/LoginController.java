package cn.feignclient.credit_feign_web.client.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.feignclient.credit_feign_web.client.service.LoginLogService;
import cn.feignclient.credit_feign_web.domain.TokenUserClientDomain;
import cn.feignclient.credit_feign_web.domain.TokenUserDomain;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import cn.feignclient.credit_feign_web.utils.UUIDTool;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.ApiAccountInfoClientDomain;
import main.java.cn.domain.CreUserClientDomain;
import main.java.cn.until.MD5Util;

@RestController("LoginClientController")
@RequestMapping("/web/client/login")
public class LoginController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private LoginLogService loginLogService;

	@RequestMapping("/userLogin")
	public BackResult<TokenUserClientDomain> userLogin(HttpServletRequest request, HttpServletResponse response,
			String userName, String password) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<TokenUserClientDomain> result = new BackResult<TokenUserClientDomain>();
		if (CommonUtils.isNotString(userName)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("登录帐号不能为空");
			return result;
		}

 		if (CommonUtils.isNotString(password)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("密码不能为空");
			return result;
		}
 		BackResult<ApiAccountInfoClientDomain> apiAccountInfo = userFeignService.getApiAccountInfoByUserName(userName);
 		if (!apiAccountInfo.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
			result.setResultMsg(apiAccountInfo.getResultMsg());
			result.setResultCode(apiAccountInfo.getResultCode());
			return result;
		}
 		
 		String ip = super.getIpAddr(request);
 		if(StringUtils.isNotBlank(apiAccountInfo.getResultObj().getBdIp()) && !apiAccountInfo.getResultObj().getBdIp().contains(ip)){
 			result.setResultMsg("登录失败，ip地址不合法");
			result.setResultCode(ResultCode.RESULT_API_NOTIPS);
			return result;
 		}
 		
 		String tempPassword = MD5Util.MD5(password+(apiAccountInfo.getResultObj().getSalt()==null?"":apiAccountInfo.getResultObj().getSalt()));
 		if(!tempPassword.equals(apiAccountInfo.getResultObj().getPassword())){
 			result.setResultMsg("登录失败，密码错误");
			result.setResultCode(ResultCode.RESULT_PASSWORD_ERROR);
			return result;
 		}
 		
 		String isInitPwd = "670B14728AD9902AECBA32E22FA4F6BD".equals(apiAccountInfo.getResultObj().getPassword())?"true":"false";
 		BackResult<CreUserClientDomain> creUserDomain = userFeignService.findByUserName(userName);
 		if (!creUserDomain.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
			result.setResultMsg(creUserDomain.getResultMsg());
			result.setResultCode(creUserDomain.getResultCode());
			return result;
		}
 		
 		String isBindMail = StringUtils.isBlank(creUserDomain.getResultObj().getEmail())?"false":"true"; 		
		Map<String,String> param = new HashMap<>();
		param.put("userName", userName);
		param.put("lastLoginIp", ip);
		loginLogService.saveLoginLog(param);
		
		String token = UUIDTool.getInstance().getUUID();
		redisClinet.set("client:user_token_" + userName,token);
		TokenUserClientDomain user = new TokenUserClientDomain();
		user.setToken(token);
		user.setIsBindMail(isBindMail);
		user.setIsInitPwd(isInitPwd);
		user.setUserId(creUserDomain.getResultObj().getId().toString());
		result.setResultObj(user);
		logger.info("用户【" + userName + "】登录成功，登录ip地址为：" + ip);
		return result;
	}

	@RequestMapping("/logout")
	public BackResult<Boolean> logout(HttpServletRequest request, HttpServletResponse response, String userName) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<Boolean> result = new BackResult<Boolean>();
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
			result.setResultMsg("注销校验失败无法注销");
			return result;
		}

		redisClinet.remove("client:user_token_" + userName);
		result.setResultObj(true);
		return result;
	}

	@RequestMapping("/isLogout")
	public BackResult<Boolean> isLogout(HttpServletRequest request, HttpServletResponse response, String userName) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<Boolean> result = new BackResult<Boolean>();
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

		Boolean fag = isLogin(userName, token);
		result.setResultObj(fag);
		result.setResultMsg(fag ? "处于登录状态" : "用户已经注销登录");
		return result;
	}
}
