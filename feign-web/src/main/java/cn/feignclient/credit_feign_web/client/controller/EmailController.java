package cn.feignclient.credit_feign_web.client.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.feignclient.credit_feign_web.client.service.EmailService;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;

@RestController("EmailClientController")
@RequestMapping("/web/client/email")
public class EmailController extends BaseController {
	
	private final static Logger logger = LoggerFactory.getLogger(EmailController.class);
	
	@Autowired
	private EmailService emailService;

	@RequestMapping("/sendMail")
	public BackResult<String> sendMail(HttpServletRequest request, HttpServletResponse response,
			String userName, String userId,String emailRecive) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<String> result = new BackResult<String>();
		String token = request.getHeader("token");		
		if (CommonUtils.isNotString(emailRecive)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("收件邮箱不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(userName)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("登录帐号不能为空");
			return result;
			
		}
		
		if(StringUtils.isNotBlank(userId)) {			

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
		}
		
		int code = (int) ((Math.random() * 9 + 1) * 100000);
		BackResult<String> creUserDomain = emailService.sendMail(userName,emailRecive,code+"",userId);
		if(!ResultCode.RESULT_SUCCEED.equals(creUserDomain.getResultCode())){
			result.setResultCode(creUserDomain.getResultCode());
			result.setResultMsg(creUserDomain.getResultMsg());
			return result;
		}
		
		redisClinet.set("client:email_code_" + userName,code+"",60 * 60 * 1000);		
		return creUserDomain;
	}	
	
	@RequestMapping("/checkMailCode")
	public BackResult<String> checkMailCode(HttpServletRequest request, HttpServletResponse response,
			String userName, String userId,String emailRecive,String code) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<String> result = new BackResult<String>();
		String token = request.getHeader("token");
		if (CommonUtils.isNotString(userName)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("登录帐号不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(emailRecive)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("收件邮箱不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(code)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("验证码不能为空");
			return result;
		}

		if(StringUtils.isNotBlank(userId)) {
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
		}
		
		String mailCode = redisClinet.get("client:email_code_" + userName);
		if(StringUtils.isBlank(mailCode)){
			result.setResultCode(ResultCode.RESULT_PASSWORD_ERROR);
			result.setResultMsg("验证码未发送成功");
			return result;
		}
		
		result.setResultObj(code.equals(mailCode)?"true":"false");
		return result;
	}	
}
