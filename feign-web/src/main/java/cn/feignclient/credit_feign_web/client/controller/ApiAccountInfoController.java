package cn.feignclient.credit_feign_web.client.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.feignclient.credit_feign_web.client.service.ApiAccountInfoClientService;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.ApiAccountInfoClientDomain;
import main.java.cn.domain.CreUserClientDomain;
import main.java.cn.until.MD5Util;

@RestController("ApiAccountInfoClientController")
@RequestMapping("/web/client/apiAccountInfo")
public class ApiAccountInfoController extends BaseController {
	
	private final static Logger logger = LoggerFactory.getLogger(ApiAccountInfoController.class);
	
	@Autowired
	private ApiAccountInfoClientService apiAccountInfoFeignService;

	@RequestMapping("/updateCreUserPwd")
	public BackResult<String> updateCreUserPwd(HttpServletRequest request, HttpServletResponse response,
			String userName, String userId,String password) {

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
		
		if (CommonUtils.isNotString(password)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("密码不能为空");
			return result;
		}

		if (!isLogin(userName, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败，请先登录");
			return result;
		} 
		
		int code = (int) ((Math.random() * 9 + 1) * 100000);
		ApiAccountInfoClientDomain apiAccountInfoDomain = new ApiAccountInfoClientDomain();
		apiAccountInfoDomain.setCreUserId(Integer.parseInt(userId));
		apiAccountInfoDomain.setSalt(code+"");
		apiAccountInfoDomain.setPassword(MD5Util.MD5(password+code+""));
		BackResult<String> tempResult = apiAccountInfoFeignService.updateApiAccountInfo(apiAccountInfoDomain);
		if(!ResultCode.RESULT_SUCCEED.equals(tempResult.getResultCode())){
			result.setResultCode(tempResult.getResultCode());
			result.setResultMsg(tempResult.getResultMsg());
			logger.info("用户【" + userName + "】修改密码失败，失败原因为：" + tempResult.getResultMsg());
			return result;
		}
		
		logger.info("用户【" + userName + "】修改密码成功");
		return tempResult;
	}
	
	@RequestMapping("/checkCreUserOldPwd")
	public BackResult<String> checkCreUserOldPwd(HttpServletRequest request, HttpServletResponse response,
			String userName, String userId,String password) {

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
		
		if (CommonUtils.isNotString(password)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("密码不能为空");
			return result;
		}

		if (!isLogin(userName, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败，请先登录");
			return result;
		} 
		
		BackResult<ApiAccountInfoClientDomain> apiAccountInfo = userFeignService.getApiAccountInfoByUserName(userName);
 		if (!apiAccountInfo.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
			result.setResultMsg(apiAccountInfo.getResultMsg());
			result.setResultCode(apiAccountInfo.getResultCode());
			return result;
		}
 		
 		String tempPassword = MD5Util.MD5(password+(apiAccountInfo.getResultObj().getSalt()==null?"":apiAccountInfo.getResultObj().getSalt()));
		
		logger.info("用户【" + userName + "】验证原始密码成功");
		result.setResultObj(tempPassword.equals(apiAccountInfo.getResultObj().getPassword())?"true":"false");
		return result;
	}
	
	@RequestMapping("/updateResultPwd")
	public BackResult<String> updateResultPwd(HttpServletRequest request, HttpServletResponse response,
			String userName, String userId,String resultPwd) {

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
		
		if (CommonUtils.isNotString(resultPwd)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("密码不能为空");
			return result;
		}

		if (!isLogin(userName, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败，请先登录");
			return result;
		} 
		
		ApiAccountInfoClientDomain apiAccountInfoDomain = new ApiAccountInfoClientDomain();
		apiAccountInfoDomain.setCreUserId(Integer.parseInt(userId));
		apiAccountInfoDomain.setResultPwd(resultPwd);
		BackResult<String> tempResult = apiAccountInfoFeignService.updateApiAccountInfo(apiAccountInfoDomain);
		if(!ResultCode.RESULT_SUCCEED.equals(tempResult.getResultCode())){
			result.setResultCode(tempResult.getResultCode());
			result.setResultMsg(tempResult.getResultMsg());
			logger.info("用户【" + userName + "】修改检测结果包的安全密码失败，失败原因为：" + tempResult.getResultMsg());
			return result;
		}
		
		logger.info("用户【" + userName + "】修改检测结果包的安全密码成功");
		return tempResult;
	}
	
	@RequestMapping("/forgetCreUserPwd")
	public BackResult<String> forgetCreUserPwd(HttpServletRequest request, HttpServletResponse response,
			String email,String password) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<String> result = new BackResult<String>();
		if (CommonUtils.isNotString(email)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("绑定邮箱不能为空");
			return result;
			
		}
	
		if (CommonUtils.isNotString(password)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("密码不能为空");
			return result;
		}
		
		BackResult<CreUserClientDomain> creUserDomain = apiAccountInfoFeignService.findCreUserBymail(email);
		if(!ResultCode.RESULT_SUCCEED.equals(creUserDomain.getResultCode())){
			result.setResultCode(creUserDomain.getResultCode());
			result.setResultMsg(creUserDomain.getResultMsg());
			return result;
		}
		
		int code = (int) ((Math.random() * 9 + 1) * 100000);
		ApiAccountInfoClientDomain apiAccountInfoDomain = new ApiAccountInfoClientDomain();
		apiAccountInfoDomain.setCreUserId(creUserDomain.getResultObj().getId());
		apiAccountInfoDomain.setSalt(code+"");
		apiAccountInfoDomain.setPassword(MD5Util.MD5(password+code+""));
		BackResult<String> tempResult = apiAccountInfoFeignService.updateApiAccountInfo(apiAccountInfoDomain);
		if(!ResultCode.RESULT_SUCCEED.equals(tempResult.getResultCode())){
			result.setResultCode(tempResult.getResultCode());
			result.setResultMsg(tempResult.getResultMsg());
			return result;
		}
		
		logger.info("用户【" + email + "】重置密码成功");
		return tempResult;
	}
}
