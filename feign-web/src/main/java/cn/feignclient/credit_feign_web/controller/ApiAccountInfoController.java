package cn.feignclient.credit_feign_web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.feignclient.credit_feign_web.client.service.ApiAccountInfoClientService;
import cn.feignclient.credit_feign_web.client.service.UserFeignClientService;
import cn.feignclient.credit_feign_web.service.ApiAccountInfoFeignService;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.ApiAccountInfoClientDomain;
import main.java.cn.domain.ApiAccountInfoDomain;
import main.java.cn.domain.CreUserClientDomain;
import main.java.cn.until.MD5Util;

@RestController
@RequestMapping("/web/feign/apiAccountInfo")
public class ApiAccountInfoController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(ApiAccountInfoController.class);

	@Autowired
	private ApiAccountInfoFeignService apiAccountInfoFeignService;
	
	@Autowired
	private ApiAccountInfoClientService apiAccountInfoClientService;
	
	@Autowired
	protected UserFeignClientService userFeignService;
	
	@RequestMapping(value = "/findByCreUserId", method = RequestMethod.POST)
	public BackResult<ApiAccountInfoDomain> findTrdOrderByMobile(HttpServletRequest request,
			HttpServletResponse response, String mobile, String token,String creUserId) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<ApiAccountInfoDomain> result = new BackResult<ApiAccountInfoDomain>();

		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
			return result;
		}

		if (CommonUtils.isNotString(token)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("token不能为空");
			return result;
		}

		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户校验失败");
			return result;
		}

		try {
			result = apiAccountInfoFeignService.findByCreUserId(creUserId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户手机号：" + mobile + "执行查询API账户信息出现异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}

		return result;
	}
	
	@RequestMapping(value = "/updateApiAccountInfo", method = RequestMethod.POST)
	public BackResult<ApiAccountInfoDomain> updateApiAccountInfo(HttpServletRequest request,
			HttpServletResponse response, String mobile, String token,ApiAccountInfoDomain domain) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<ApiAccountInfoDomain> result = new BackResult<ApiAccountInfoDomain>();

		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
			return result;
		}

		if (CommonUtils.isNotString(token)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("token不能为空");
			return result;
		}

		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户校验失败");
			return result;
		}
		
		if (CommonUtils.isNotIngeter(domain.getId())) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("API编号不能为空");
			return result;
		}
		
		if (CommonUtils.isNotIngeter(domain.getCreUserId())) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("用户编号不能为空");
			return result;
		}

		try {
			result = apiAccountInfoFeignService.updateApiAccountInfo(domain);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户手机号：" + mobile + "执行修改API账户信息出现异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}

		return result;
	}
	
	@RequestMapping("/updateCreUserPwd")
	public BackResult<String> updateCreUserPwd(HttpServletRequest request, HttpServletResponse response,
			String mobile, String token,String userId,String password) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<String> result = new BackResult<String>();
		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("登录号码不能为空");
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

		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败，请先登录");
			return result;
		} 
		
		int code = (int) ((Math.random() * 9 + 1) * 100000);
		ApiAccountInfoClientDomain apiAccountInfoDomain = new ApiAccountInfoClientDomain();
		apiAccountInfoDomain.setCreUserId(Integer.parseInt(userId));
		apiAccountInfoDomain.setSalt(code+"");
		apiAccountInfoDomain.setPassword(MD5Util.MD5(password+code+""));
		BackResult<String> tempResult = apiAccountInfoClientService.updateApiAccountInfo(apiAccountInfoDomain);
		if(!ResultCode.RESULT_SUCCEED.equals(tempResult.getResultCode())){
			result.setResultCode(tempResult.getResultCode());
			result.setResultMsg(tempResult.getResultMsg());
			logger.info("用户【" + mobile + "】修改密码失败，失败原因为：" + tempResult.getResultMsg());
			return result;
		}
		
		logger.info("用户【" + mobile + "】修改密码成功");
		return tempResult;
	}
	
	@RequestMapping("/checkCreUserOldPwd")
	public BackResult<String> checkCreUserOldPwd(HttpServletRequest request, HttpServletResponse response,
			String mobile, String token,String userId,String password) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<String> result = new BackResult<String>();
		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("号码不能为空");
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

		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败，请先登录");
			return result;
		} 
		
		BackResult<ApiAccountInfoClientDomain> apiAccountInfo = userFeignService.getApiAccountInfoByUserName(mobile);
 		if (!apiAccountInfo.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
			result.setResultMsg(apiAccountInfo.getResultMsg());
			result.setResultCode(apiAccountInfo.getResultCode());
			return result;
		}
 		
 		String tempPassword = MD5Util.MD5(password+(apiAccountInfo.getResultObj().getSalt()==null?"":apiAccountInfo.getResultObj().getSalt()));
		
		logger.info("用户【" + mobile + "】验证原始密码成功");
		result.setResultObj(tempPassword.equals(apiAccountInfo.getResultObj().getPassword())?"true":"false");
		return result;
	}
}
