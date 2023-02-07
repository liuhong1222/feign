package cn.feignclient.credit_feign_web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.feignclient.credit_feign_web.service.ApiAccountInfoFeignService;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.AgentWebSiteDomain;
import main.java.cn.domain.ApiAccountInfoDomain;
import main.java.cn.domain.BusinessInfoDomain;
import main.java.cn.domain.CreUserAccountDomain;
import main.java.cn.domain.CreUserAgentDomain;
import main.java.cn.domain.CreUserDomain;
import main.java.cn.domain.CreUserWarningDomain;
import main.java.cn.domain.IdCardInfoDomain;
import main.java.cn.sms.util.ChuangLanSmsUtil;
import main.java.cn.untils.DateUtils;

@RestController
@RequestMapping("/web/user")
public class UserController extends BaseController {
	
	private final static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private ApiAccountInfoFeignService apiAccountInfoFeignService;

	@RequestMapping("/findbyMobile")
	public BackResult<CreUserAgentDomain> findbyMobile(HttpServletRequest request, HttpServletResponse response,
			String mobile, String token,String domain) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<CreUserAgentDomain> result = new BackResult<CreUserAgentDomain>();
		CreUserAgentDomain creUserAgentDomain = new CreUserAgentDomain();
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
			//获取用户信息
			BackResult<CreUserDomain> creUserDomain = userFeignService.findbyMobile(mobile,null);
			if(!ResultCode.RESULT_SUCCEED.equals(creUserDomain.getResultCode())){
				result.setResultCode(creUserDomain.getResultCode());
				result.setResultMsg(creUserDomain.getResultMsg());
				return result;
			}
			
			BeanUtils.copyProperties(creUserDomain.getResultObj(),creUserAgentDomain);
			//获取代理商id
			BackResult<AgentWebSiteDomain> agentInfo = super.getAgentIdByDoman(domain);
			if(!ResultCode.RESULT_SUCCEED.equals(agentInfo.getResultCode())){
				result.setResultCode(agentInfo.getResultCode());
				result.setResultMsg(agentInfo.getResultMsg());
				return result;
			}
			//代理商id
			String agentId = agentInfo.getResultObj().getAgentId();
			//是否是代理商 0-不是，1-是
			creUserAgentDomain.setIsAgent("1,2,3".contains(agentId)?"0":"1");
			//获取代理商的支付信息
			BackResult<String> agentPay = agentService.getAgentPayInfo(agentId);
			creUserAgentDomain.setIsPay(agentPay.getResultObj());	
			//获取用户是否是羊毛党
			String isWhiteUser = null;
			if(!redisClinet.exists("ym:" + creUserDomain.getResultObj().getId().toString() + "_YANGMAODANG_ISFIRST")){
				isWhiteUser = redisClinet.get("ym:" + creUserDomain.getResultObj().getId().toString() + "_YANGMAODANG");
				redisClinet.set("ym:" + creUserDomain.getResultObj().getId().toString() + "_YANGMAODANG_ISFIRST", "1");
			}
			
			creUserAgentDomain.setIsWhiteUser(isWhiteUser);
			//获取用户的接口相关信息
			BackResult<ApiAccountInfoDomain> apiAccountResult = apiAccountInfoFeignService.findByCreUserId(creUserDomain.getResultObj().getId().toString());
			if(apiAccountResult == null || !ResultCode.RESULT_SUCCEED.equals(apiAccountResult.getResultCode())){
				creUserAgentDomain.setIsSetPwd("0");
			}else{
				if(StringUtils.isBlank(apiAccountResult.getResultObj().getResultPwd())){
					creUserAgentDomain.setIsSetPwd("0");
				}else{
					//设置用户是否有压缩包密码
					creUserAgentDomain.setIsSetPwd("1");
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户手机号：" + mobile + "执行查询用户信息出现异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
		
		result.setResultObj(creUserAgentDomain);
		return result;
	}
	
	@RequestMapping("/checkUserPhone")
	public BackResult<CreUserDomain> checkUserPhone(HttpServletRequest request, HttpServletResponse response,String phone,
			String mobile, String token) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<CreUserDomain> result = new BackResult<CreUserDomain>();

		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
			return result;
			
		}

		if (CommonUtils.isNotString(phone)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("新手机号不能为空");
			return result;
		}

		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("注销校验失败无法注销");
			return result;
		} 
		
		try {
			result = userFeignService.findbyMobile(phone,null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户手机号：" + mobile + "执行查询用户信息出现异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
		
		return result;
	}
	
	@RequestMapping("/updateCreUser")
	public BackResult<CreUserDomain> updateCreUser(HttpServletRequest request, HttpServletResponse response, String token ,CreUserDomain creUserDomain) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<CreUserDomain> result = new BackResult<CreUserDomain>();

		if (CommonUtils.isNotString(creUserDomain.getUserPhone())) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
			return result;
			
		}

		if (CommonUtils.isNotString(token)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("token不能为空");
			return result;
		}

		if (!isLogin(creUserDomain.getUserPhone(), token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("注销校验失败无法注销");
			return result;
		} 
		
		try {
			result = userFeignService.updateCreUser(creUserDomain);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户手机号：" + creUserDomain.getUserPhone() + "执行修改信息出现异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
		
		return result;
	}
	
	@RequestMapping("/getUserBalance")
	public BackResult<CreUserAccountDomain> getUserBalance(HttpServletRequest request, HttpServletResponse response, String apiName,String password) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<CreUserAccountDomain> result = new BackResult<CreUserAccountDomain>();
		if (CommonUtils.isNotString(apiName)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("API帐号不能为空");
			return result;
			
		}
		if (CommonUtils.isNotString(password)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("API密码不能为空");
			return result;
		}		
		try {
			result = userFeignService.getUserBalance(apiName, password);
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
			return result;
		}
		
		return result;
	}
	
	@RequestMapping("/getUserWarning")
	public BackResult<CreUserWarningDomain> getUserWarning(HttpServletRequest request, HttpServletResponse response, String userId,String productName, String mobile,String token) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<CreUserWarningDomain> result = new BackResult<CreUserWarningDomain>();
		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败, 请先登录");
			return result;
		} 
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 用户Id不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(productName)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 产品代码不能为空");
			return result;
		}
		
		try {
			result = userFeignService.getUserWarning(userId, productName);
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
			return result;
		}
		
		return result;
	}
	
	@RequestMapping("/updateUserWarning")
	public BackResult<String> updateUserWarning(HttpServletRequest request, HttpServletResponse response, 
			String userId,String productName,String warningCount,String informMobiles, String mobile,String token) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<String> result = new BackResult<String>();
		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败, 请先登录");
			return result;
		} 
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 用户Id不能为空");
			return result;
			
		}
		if (CommonUtils.isNotString(productName)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 产品代码不能为空");
			return result;
		}	
		if (CommonUtils.isNotString(warningCount)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 预警条数不能为空");
			return result;
			
		}
		if (CommonUtils.isNotString(informMobiles)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 发送号码不能为空");
			return result;
		}	
		try {
			result = userFeignService.updateUserWarning(warningCount, informMobiles,userId,productName);
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
			return result;
		}
		
		return result;
	}
	
	/**
	 * 个人用户提交认证
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/subUserAuthByIdCard")
	public BackResult<String> subUserAuthByIdCard(HttpServletRequest request, HttpServletResponse response, 
			String userId,String mobile,String token,String username,String address,String idno,String effectDate,String expireDate) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		
		IdCardInfoDomain idCardInfoDomain = new IdCardInfoDomain();
		BackResult<String> result = new BackResult<String>();
		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败, 请先登录");
			return result;
		}
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 用户Id不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(username)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 姓名不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(address)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 详细地址不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(idno)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 身份证号码不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(effectDate)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 证件生效日期不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(expireDate)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 证件失效日期不能为空");
			return result;
		}
		
		try {
			idCardInfoDomain.setCreUserId(userId);
			idCardInfoDomain.setUsername(username);
			idCardInfoDomain.setAddress(address);
			idCardInfoDomain.setIdno(idno);
			idCardInfoDomain.setEffectDate(DateUtils.parseDate(effectDate));
			idCardInfoDomain.setExpireDate(DateUtils.parseDate(expireDate));
			//保存个人认证数据
			result = userFeignService.subUserAuthByIdCard(idCardInfoDomain);
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
			return result;
		}
		
		return result;
	}
	
	/**
	 * 企业用户提交认证
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/subUserAuthByBusiness")
	public BackResult<String> subUserAuthByBusiness(HttpServletRequest request, HttpServletResponse response, 
			String userId,String mobile,String token,String name,String regnum,String address,String person,String effectDate,String expireDate,String business) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BusinessInfoDomain businessInfoDomain = new BusinessInfoDomain();
		BackResult<String> result = new BackResult<String>();
		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败, 请先登录");
			return result;
		}
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 用户Id不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(name)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 企业名称不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(regnum)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 营业执照号不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(address)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 企业详细地址不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(person)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 法人姓名不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(effectDate)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 营业执照生效日期不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(expireDate)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 营业执照失效日期不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(business)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 企业经营范围不能为空");
			return result;
		}
		
		try {
			businessInfoDomain.setCreUserId(userId);
			businessInfoDomain.setName(name);
			businessInfoDomain.setRegnum(regnum);
			businessInfoDomain.setAddress(address);
			businessInfoDomain.setPerson(person);
			businessInfoDomain.setEffectDate(DateUtils.parseDate(effectDate));
			businessInfoDomain.setExpireDate(DateUtils.parseDate(expireDate));
			businessInfoDomain.setBusiness(business);
			//保存企业认证信息
			result = userFeignService.subUserAuthByBusiness(businessInfoDomain);
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
			return result;
		}
		
		return result;
	}
	
	@RequestMapping("/getUserAuthInfo")
	public BackResult<Map<String,Object>> getUserAuthInfo(HttpServletRequest request, HttpServletResponse response, 
			String userId,String userType,String mobile,String token) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<Map<String,Object>> result = new BackResult<Map<String,Object>>();
		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败, 请先登录");
			return result;
		} 
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 用户Id不能为空");
			return result;
			
		}
		
		if (CommonUtils.isNotString(userType)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 用户类型不能为空");
			return result;
			
		}
		
		try {
			result = userFeignService.getUserAuthInfo(userId,userType);
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
			return result;
		}
		
		return result;
	}
	
	@RequestMapping("/getIdenCodeToUpMob")
	public BackResult<String> getIdenCodeToUpMob(HttpServletRequest request, HttpServletResponse response, 
			String userId,String phone,String phoneType,String mobile,String token,String domain) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<String> result = new BackResult<String>();
		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败, 请先登录");
			return result;
		} 
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 用户Id不能为空");
			return result;
			
		}
		
		if (CommonUtils.isNotString(phone)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 手机号不能为空");
			return result;
			
		}
		
		if (CommonUtils.isNotString(phoneType)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 手机号码类型不能为空");
			return result;
			
		}
		

		int code = (int) ((Math.random() * 9 + 1) * 100000);
		//获取代理商域名以及短信签名
		BackResult<AgentWebSiteDomain> agentInfo = super.getAgentIdByDoman(domain);
		if(agentInfo == null){
			result.setResultCode(agentInfo.getResultCode());
			result.setResultMsg(agentInfo.getResultMsg());
			return result;
		}
		ChuangLanSmsUtil.getInstance().sendSmsByMobile(phone, String.valueOf(code),agentInfo.getResultObj());
		logger.info(phone+"验证码：" + String.valueOf(code));
		redisClinet.set("se_ken_" + phoneType + "_" + phone, String.valueOf(code));	
		result.setResultObj("success");
		return result;
	}
	
	@RequestMapping("/updateUserPhone")
	public BackResult<String> updateUserPhone(HttpServletRequest request, HttpServletResponse response, 
			String userId,String phoneNew,String phoneNewCode,String mobile,String token) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<String> result = new BackResult<String>();
		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败, 请先登录");
			return result;		
		} 
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 用户Id不能为空");
			return result;			
		}
		
		if (CommonUtils.isNotString(phoneNew)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 新手机号不能为空");
			return result;			
		}
		
		if (CommonUtils.isNotString(phoneNewCode)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 新手机号码验证码不能为空");
			return result;			
		}
		
		String redisNewCode = redisClinet.get("se_ken_new_" + phoneNew);
		//验证新手机验证码
		if(!phoneNewCode.equals(redisNewCode)){
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("新手机验证码错误");
			return result;	
		}
		//修改手机号码
		BackResult<Integer> operate = userFeignService.updateUserPhone(userId,phoneNew);
		if(!ResultCode.RESULT_SUCCEED.equals(operate.getResultCode())){
			result.setResultCode(operate.getResultCode());
			result.setResultMsg(operate.getResultMsg());
			return result;
		}
		
		result.setResultObj("success");
		return result;
	}
	
	@RequestMapping("/checkOldCode")
	public BackResult<String> checkOldCode(HttpServletRequest request, HttpServletResponse response, 
			String userId,String phone,String phoneCode,String mobile,String token) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<String> result = new BackResult<String>();
		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败, 请先登录");
			return result;		
		} 
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 用户Id不能为空");
			return result;			
		}
		
		if (CommonUtils.isNotString(phone)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 原手机号不能为空");
			return result;			
		}
		
		if (CommonUtils.isNotString(phoneCode)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("操作失败, 原手机号码验证码不能为空");
			return result;			
		}
		
		String redisOldCode = redisClinet.get("se_ken_old_" + phone);
		//验证新手机验证码
		if(!phoneCode.equals(redisOldCode)){
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("原手机验证码错误");
			return result;	
		}
		
		result.setResultObj("success");
		return result;
	}
}
