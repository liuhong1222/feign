package cn.feignclient.credit_feign_web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.feignclient.credit_feign_web.service.UserAccountFeignService;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.AgentWebSiteDomain;
import main.java.cn.domain.TrdOrderDomain;
import main.java.cn.domain.UserAccountDomain;
import main.java.cn.domain.page.PageDomain;
import main.java.cn.sms.util.ChuangLanSmsUtil;

@RestController
@RequestMapping("/web/userAccount")
public class UserProviderController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserAccountFeignService userAccountFeignService;

	@RequestMapping(value = "/findbyMobile", method = RequestMethod.GET)
	public BackResult<UserAccountDomain> findbyMobile(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String mobile, String token) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<UserAccountDomain> result = new BackResult<UserAccountDomain>();

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
			result.setResultMsg("注销校验失败无法注销");
			return result;
		}

		result = userAccountFeignService.findbyMobile(mobile);

		return result;
	}

	@RequestMapping(value = "/findTrdOrderByCreUserId", method = RequestMethod.POST)
	public BackResult<List<TrdOrderDomain>> findTrdOrderByMobile(HttpServletRequest request,
			HttpServletResponse response, @RequestParam("mobile") String mobile, String token, Integer creUserId) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<List<TrdOrderDomain>> result = new BackResult<List<TrdOrderDomain>>();

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
			result.setResultMsg("注销校验失败无法注销");
			return result;
		}

		try {
			result = userAccountFeignService.findTrdOrderByCreUserId(creUserId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户手机号：" + mobile + "执行查询订单信息出现异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}

		return result;
	}

	@RequestMapping(value = "/pageFindTrdOrderByCreUserId", method = RequestMethod.POST)
	public BackResult<PageDomain<Map<String,Object>>> pageFindTrdOrderByCreUserId(HttpServletRequest request,
			HttpServletResponse response, String mobile, String token, Integer creUserId, Integer pageSize,
			Integer pageNum) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<PageDomain<Map<String,Object>>> result = new BackResult<PageDomain<Map<String,Object>>>();

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
			result.setResultMsg("注销校验失败无法注销");
			return result;
		}

		if (CommonUtils.isNotIngeter(pageSize)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("pageSize不能为空");
			return result;
		}

		if (CommonUtils.isNotIngeter(pageNum)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("pageNum不能为空");
			return result;
		}

		try {
			if(pageNum<1)pageNum=1;
			if(pageSize<1)pageSize=10;
			result = userAccountFeignService.pageFindTrdOrderByCreUserId(creUserId, pageSize, pageNum);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户手机号：" + mobile + "执行查询订单信息出现异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}

		return result;
	}

	@RequestMapping(value = "/updateResultPwd", method = RequestMethod.POST)
	public BackResult<String> updateResultPwd(HttpServletRequest request, HttpServletResponse response,
			String mobile, String token,String resultPwd) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<String> result = new BackResult<String>();

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
		
		if (CommonUtils.isNotString(resultPwd)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("压缩包密码不能为空");
			return result;
		}

		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("注销校验失败无法注销");
			return result;
		}
		//压缩包密码设置为空字符用于取消密码
		resultPwd = "cancel".equals(resultPwd)?"":resultPwd;
		return userAccountFeignService.updateResultPwd(mobile,resultPwd);
	}
	
	@RequestMapping(value = "/checkOldResultPwd", method = RequestMethod.GET)
	public BackResult<String> checkOldResultPwd(HttpServletRequest request, HttpServletResponse response,
			String mobile, String token,String creUserId,String oldResultPwd) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<String> result = new BackResult<String>();

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
		
		if (CommonUtils.isNotString(creUserId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("用户id不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(oldResultPwd)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("旧密码不能为空");
			return result;
		}

		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("注销校验失败无法注销");
			return result;
		}
		
		BackResult<String> tempResult = userAccountFeignService.getResultPwd(creUserId);
		if(!ResultCode.RESULT_SUCCEED.equals(tempResult.getResultCode())){
			result.setResultCode(tempResult.getResultCode());
			result.setResultMsg(tempResult.getResultMsg());
			return result;
		}
		//判断旧密码是否正确
		result.setResultObj(oldResultPwd.equals(tempResult.getResultObj())?"true":"false");
		return result;
	}
	
	@RequestMapping(value = "/forgetResultPwd", method = RequestMethod.GET)
	public BackResult<String> forgetResultPwd(HttpServletRequest request, HttpServletResponse response,
			String mobile, String token,String creUserId,String domain) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<String> result = new BackResult<String>();

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
		
		if (CommonUtils.isNotString(creUserId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("用户id不能为空");
			return result;
		}

		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("注销校验失败无法注销");
			return result;
		}
		
		BackResult<String> tempResult = userAccountFeignService.getResultPwd(creUserId);
		if(!ResultCode.RESULT_SUCCEED.equals(tempResult.getResultCode())){
			result.setResultCode(tempResult.getResultCode());
			result.setResultMsg(tempResult.getResultMsg());
			return result;
		}
		//获取代理商域名以及短信签名
		BackResult<AgentWebSiteDomain> agentInfo = super.getAgentIdByDoman(domain);
		if(!agentInfo.getResultCode().equals(ResultCode.RESULT_SUCCEED)){
			result.setResultCode(agentInfo.getResultCode());
			result.setResultMsg(agentInfo.getResultMsg());
			return result;
		}
		//发送短信通知用户密码
		ChuangLanSmsUtil.getInstance().sendSmsByResultPwd(mobile, tempResult.getResultObj(), agentInfo.getResultObj());
		result.setResultObj("成功");
		return result;
	}
}
