package cn.feignclient.credit_feign_web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.feignclient.credit_feign_web.client.service.ApiAccountInfoClientService;
import cn.feignclient.credit_feign_web.domain.JyDomain;
import cn.feignclient.credit_feign_web.domain.TokenUserDomain;
import cn.feignclient.credit_feign_web.jysdk.GeetestConfig;
import cn.feignclient.credit_feign_web.jysdk.GeetestLib;
import cn.feignclient.credit_feign_web.service.NewsService;
import cn.feignclient.credit_feign_web.service.SmsCodeService;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import cn.feignclient.credit_feign_web.utils.HttpClient;
import cn.feignclient.credit_feign_web.utils.MD5Util;
import cn.feignclient.credit_feign_web.utils.UUIDTool;
import main.java.cn.common.BackResult;
import main.java.cn.common.RedisKeys;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.AgentWebSiteDomain;
import main.java.cn.domain.ApiAccountInfoClientDomain;
import main.java.cn.domain.CreUserDomain;
import main.java.cn.domain.WebSiteInfoDomain;
import main.java.cn.sms.util.ChuangLanSmsUtil;
import main.java.cn.sms.util.SmallSmsUtil;

@RestController
@RequestMapping("/web/login")
public class LoginController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(ApiAccountInfoController.class);
	
	@Value("${yangmaoUrl}")
	private String yangmaoUrl;
	
	@Value("${ymAppID}")
	private String ymAppID;
	
	@Value("${ymAppKey}")
	private String ymAppKey;
	
	@Value("${newDomain}")
	private String newDomain;
	
	@Autowired
	private SmsCodeService smsCodeService;
	
	@Autowired
	private NewsService newsService;
	
	@Autowired
	private ApiAccountInfoClientService apiAccountInfoClientService;
	  
	/**
	 * 加载极验 验证码
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/initjyCode")
	public void initjyCode(HttpServletRequest request, HttpServletResponse response, String mobile) throws IOException {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<JyDomain> result = new BackResult<JyDomain>();		
		GeetestLib gtSdk = new GeetestLib(GeetestConfig.getGeetest_id(), GeetestConfig.getGeetest_key(),
				GeetestConfig.isnewfailback());

		// 自定义参数,可选择添加
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("user_id", mobile); // 网站用户id
		param.put("client_type", "web"); // web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
		param.put("ip_address", super.getIpAddr(request)); // 传输用户请求验证时所携带的IP

		// 进行验证预处理
		int gtServerStatus = gtSdk.preProcess(param);

		// 将服务器状态设置到session中
		redisClinet.set(gtSdk.gtServerStatusSessionKey + "_" + mobile, String.valueOf(gtServerStatus));
		// 将userid设置到session中
		redisClinet.set(gtSdk.gtServerStatusSessionKey + "_user_id_" + mobile, mobile);
		String str = gtSdk.getResponseStr();

		JSONObject json = JSONObject.parseObject(str);
		String resStr = gtSdk.getResponseStr();
		JyDomain jydomain = new JyDomain();
		jydomain.setChallenge(json.getString("challenge"));
		jydomain.setSuccess(json.getString("success"));
		jydomain.setGt(json.getString("gt"));
		result.setResultObj(jydomain);
		PrintWriter out = response.getWriter();
		out.println(resStr);
	}

	/**
	 * 加载极验 验证码
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/verifyTyCode")
	public void verifyTyCode(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		GeetestLib gtSdk = new GeetestLib(GeetestConfig.getGeetest_id(), GeetestConfig.getGeetest_key(), 
				GeetestConfig.isnewfailback());
			
		String challenge = request.getParameter(GeetestLib.fn_geetest_challenge);
		String validate = request.getParameter(GeetestLib.fn_geetest_validate);
		String seccode = request.getParameter(GeetestLib.fn_geetest_seccode);
		String mobile = request.getParameter("mobile");
		String domain = request.getParameter("domain");
		
//		CreUserDomain creUserDomain = super.findByMobileNew(mobile);
//		if (creUserDomain == null) {
//			JSONObject data = new JSONObject();
//			data.put("status", "fail");
//			data.put("version", gtSdk.getVersionInfo());
//			PrintWriter out = response.getWriter();
//			out.println(data.toString());
//			return;
//		}
		//从session中获取gt-server状态
		int gt_server_status_code = Integer.valueOf(redisClinet.get(gtSdk.gtServerStatusSessionKey + "_" + mobile));
		
		//从session中获取userid
		String userid = redisClinet.get(gtSdk.gtServerStatusSessionKey + "_user_id_" + mobile);
		
		//自定义参数,可选择添加
		HashMap<String, String> param = new HashMap<String, String>(); 
		param.put("user_id", userid); //网站用户id
		param.put("client_type", "web"); //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
		param.put("ip_address", super.getIpAddr(request)); //传输用户请求验证时所携带的IP
		
		int gtResult = 0;

		if (gt_server_status_code == 1) {
			//gt-server正常，向gt-server进行二次验证
			gtResult = gtSdk.enhencedValidateRequest(challenge, validate, seccode, param);
		} else {
			// gt-server非正常情况下，进行failback模式验证
			gtResult = gtSdk.failbackValidateRequest(challenge, validate, seccode);
		}


		if (gtResult == 1) {
			
			int code = (int) ((Math.random() * 9 + 1) * 100000);
			//获取代理商域名以及短信签名
			BackResult<AgentWebSiteDomain> agentInfo = super.getAgentIdByDoman(domain);
			if(!agentInfo.getResultCode().equals(ResultCode.RESULT_SUCCEED)){
				// 验证失败
				JSONObject data = new JSONObject();
				data.put("status", "fail");
				data.put("version", gtSdk.getVersionInfo());
				PrintWriter out = response.getWriter();
				out.println(data.toString());
			}
			SmallSmsUtil.getInstance().sendSmsByMobile(mobile, String.valueOf(code), agentInfo.getResultObj());
			//保存验证码记录到数据库
			smsCodeService.saveSmsCode(mobile, String.valueOf(code));
			logger.info(mobile+"验证码：" + String.valueOf(code));
			//保存验证码记录到redis
			redisClinet.set("se_ken_" + mobile, String.valueOf(code));			
			// 验证成功
			PrintWriter out = response.getWriter();
			JSONObject data = new JSONObject();
			data.put("status", "success");
			data.put("version", gtSdk.getVersionInfo());
			out.println(data.toString());
		}
		else {
			// 验证失败
			JSONObject data = new JSONObject();
			data.put("status", "fail");
			data.put("version", gtSdk.getVersionInfo());
			PrintWriter out = response.getWriter();
			out.println(data.toString());
		}
	}
	
	/**
	 * 代理商加载极验 验证码
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/verifyTyCodeByDls")
	public void verifyTyCodeByDls(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		GeetestLib gtSdk = new GeetestLib(GeetestConfig.getGeetest_id(), GeetestConfig.getGeetest_key(), 
				GeetestConfig.isnewfailback());
			
		String challenge = request.getParameter(GeetestLib.fn_geetest_challenge);
		String validate = request.getParameter(GeetestLib.fn_geetest_validate);
		String seccode = request.getParameter(GeetestLib.fn_geetest_seccode);
		String mobile = request.getParameter("mobile");
		String source = request.getParameter("source")==null?null:request.getParameter("source");
		//从session中获取gt-server状态
		int gt_server_status_code = Integer.valueOf(redisClinet.get(gtSdk.gtServerStatusSessionKey + "_" + mobile));
		
		//从session中获取userid
		String userid = redisClinet.get(gtSdk.gtServerStatusSessionKey + "_user_id_" + mobile);
		
		//自定义参数,可选择添加
		HashMap<String, String> param = new HashMap<String, String>(); 
		param.put("user_id", userid); //网站用户id
		param.put("client_type", "web"); //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
		param.put("ip_address", super.getIpAddr(request)); //传输用户请求验证时所携带的IP
		
		int gtResult = 0;

		if (gt_server_status_code == 1) {
			//gt-server正常，向gt-server进行二次验证
			gtResult = gtSdk.enhencedValidateRequest(challenge, validate, seccode, param);
		} else {
			// gt-server非正常情况下，进行failback模式验证
			gtResult = gtSdk.failbackValidateRequest(challenge, validate, seccode);
		}


		if (gtResult == 1) {
			
			int code = (int) ((Math.random() * 9 + 1) * 100000);
			String messageSign = null;
			if(StringUtils.isNotBlank(source)){
				messageSign = redisClinet.get(RedisKeys.getInstance().getMessageSignByDls(source))==null?null:redisClinet.get(RedisKeys.getInstance().getMessageSignByDls(source));
			}
			ChuangLanSmsUtil.getInstance().sendSmsByMobileToDls(mobile, String.valueOf(code),messageSign);

//			System.out.println(mobile+"验证码：" + String.valueOf(code));
			logger.info(mobile+"验证码：" + String.valueOf(code));

			redisClinet.set("se_ken_" + mobile, String.valueOf(code));
			
			// 验证成功
			PrintWriter out = response.getWriter();
			JSONObject data = new JSONObject();
			data.put("status", "success");
			data.put("version", gtSdk.getVersionInfo());
			out.println(data.toString());
		}
		else {
			// 验证失败
			JSONObject data = new JSONObject();
			data.put("status", "fail");
			data.put("version", gtSdk.getVersionInfo());
			PrintWriter out = response.getWriter();
			out.println(data.toString());
		}
	}

	/**
	 * 发送手机号码
	 * 
	 * @param request
	 * @param response
	 * @param mobile
	 * @return
	 */
	@RequestMapping("/sendSms")
	public BackResult<Boolean> sendSms(HttpServletRequest request, HttpServletResponse response, String mobile) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<Boolean> result = new BackResult<Boolean>();

		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
			return result;
		}

		return result;
	}

	/**
	 * 登录
	 * 
	 * @param request
	 * @param response
	 * @param mobile
	 * @param code
	 * @return
	 */
	@RequestMapping("/userLogin")
	public BackResult<TokenUserDomain> userLogin(HttpServletRequest request, HttpServletResponse response,
			String mobile, String code,String userName,String password,String loginType,String domain) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<TokenUserDomain> result = new BackResult<TokenUserDomain>();
		TokenUserDomain user = new TokenUserDomain();
		String isInitPwd ="";
		String token = "";
		if(StringUtils.isBlank(loginType)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("登录类型不能为空");
			return result;
		}
		
		if("mobile".equals(loginType)) {
			if (CommonUtils.isNotString(mobile)) {
				result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
				result.setResultMsg("手机号码不能为空");
				return result;
			}

	 		if (CommonUtils.isNotString(code)) {
				result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
				result.setResultMsg("code不能为空");
				return result;
			}

			String sessionCode = redisClinet.get("se_ken_" + mobile);
			if (StringUtils.isBlank(sessionCode) || !sessionCode.equals(code)) {
				result.setResultMsg("验证码验证错误！");
				result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
				return result;
			}

			//获取代理商id
			BackResult<AgentWebSiteDomain> agentResult = super.getAgentIdByDoman(domain);
			if(!agentResult.getResultCode().equals(ResultCode.RESULT_SUCCEED)){
				result.setResultMsg(agentResult.getResultMsg());
				result.setResultCode(agentResult.getResultCode());
				return result;
			}
			
			Map<String,Object> param = new HashMap<>();
			param.put("userPhone", mobile);
			param.put("lastLoginIp", super.getIpAddr(request));
			param.put("agentId", agentResult.getResultObj().getAgentId());
			param.put("isWhiteUser", isWhiteUser(mobile,super.getIpAddr(request)));
			param.put("userId", request.getParameter("userId"));
			param.put("domain", domain);
			
			// 查询用户
			BackResult<CreUserDomain> creResult = userFeignService.findOrsaveUser(param);
			if (!creResult.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
				result.setResultMsg(creResult.getResultMsg());
				result.setResultCode(creResult.getResultCode());
				return result;
			}
			
			CreUserDomain creUserDomain = findByMobileNew(mobile,null);
			if (creUserDomain != null && creUserDomain.getAgentId() == 0) {
				user.setDomain(newDomain);
				user.setMobile(mobile);
			}
			
			token = mobile;
			// 清空 se_ken_
			redisClinet.remove("se_ken_" + mobile);
		}else {
			if(StringUtils.isBlank(userName)) {
				result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
				result.setResultMsg("帐号/手机号/邮箱不能为空");
				return result;
			}
			
			if(StringUtils.isBlank(password)) {
				result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
				result.setResultMsg("密码不能为空");
				return result;
			}
			
			BackResult<ApiAccountInfoClientDomain> tempResult = apiAccountInfoClientService.findByUserName(userName);
			if (!tempResult.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
				result.setResultMsg(tempResult.getResultMsg());
				result.setResultCode(tempResult.getResultCode());
				return result;
			}
			
			String ip = super.getIpAddr(request);
	 		if(StringUtils.isNotBlank(tempResult.getResultObj().getBdIp()) && !tempResult.getResultObj().getBdIp().contains(ip)){
	 			result.setResultMsg("登录失败，ip地址不合法");
				result.setResultCode(ResultCode.RESULT_API_NOTIPS);
				return result;
	 		}
	 		
	 		String tempPassword = MD5Util.getInstance().getMD5Code(password+(tempResult.getResultObj().getSalt()==null?"":tempResult.getResultObj().getSalt())).toUpperCase();
	 		if(!tempPassword.equals(tempResult.getResultObj().getPassword())){
	 			result.setResultMsg("登录失败，密码错误");
				result.setResultCode(ResultCode.RESULT_PASSWORD_ERROR);
				return result;
	 		}
	 		
	 		CreUserDomain creUserDomain = findByMobileNew(userName,super.getIpAddr(request));
			if (creUserDomain != null && creUserDomain.getAgentId() == 0) {
				user.setDomain(newDomain);
				user.setMobile(userName);
			}
	 		
	 		isInitPwd = "670B14728AD9902AECBA32E22FA4F6BD".equals(tempResult.getResultObj().getPassword())?"true":"false";
	 		user.setIsInitPwd(isInitPwd);
	 		user.setUserName(tempResult.getResultObj().getResultPwd());
	 		user.setUserId(tempResult.getResultObj().getCreUserId().toString());
	 		token = tempResult.getResultObj().getResultPwd();
		}
		
		String userMd5str = UUIDTool.getInstance().getUUID();
		redisClinet.set("user_token_" + token, userMd5str);
		user.setToken(userMd5str);		
		result.setResultObj(user);
		logger.info("login return info: {}",JSON.toJSONString(result));
		return result;
	}

	/**
	 * 登出
	 * 
	 * @param request
	 * @param response
	 * @param mobile
	 * @param token
	 * @return
	 */
	@RequestMapping("/logout")
	public BackResult<Boolean> logout(HttpServletRequest request, HttpServletResponse response, String mobile,
			String token) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<Boolean> result = new BackResult<Boolean>();

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

		// 清空 se_ken_
		redisClinet.remove("user_token_" + mobile);
		result.setResultObj(true);

		return result;
	}

	/**
	 * 检测是否已经登出
	 * 
	 * @param request
	 * @param response
	 * @param mobile
	 * @param token
	 * @return
	 */
	@RequestMapping("/isLogout")
	public BackResult<Boolean> isLogout(HttpServletRequest request, HttpServletResponse response, String mobile,
			String token) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<Boolean> result = new BackResult<Boolean>();

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

		Boolean fag = isLogin(mobile, token);

		result.setResultObj(fag);
		result.setResultMsg(fag ? "处于登录状态" : "用户已经注销登录");

		return result;
	}
	
	/**
	 * 代理商网页初始化
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/webSiteInit")
	public BackResult<WebSiteInfoDomain> webSiteInit(HttpServletRequest request, HttpServletResponse response,String domain) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<WebSiteInfoDomain> result = new BackResult<WebSiteInfoDomain>();
		//获取代理商id
		BackResult<AgentWebSiteDomain> agentInfo = super.getAgentIdByDoman(domain);
		if(!agentInfo.getResultCode().equals(ResultCode.RESULT_SUCCEED)){
			result.setResultMsg(agentInfo.getResultMsg());
			result.setResultCode(agentInfo.getResultCode());
			return result;
		}
		//代理商id
		String agentId = agentInfo.getResultObj().getAgentId();
		BackResult<WebSiteInfoDomain> webSiteInfo = agentService.getWebSiteInfo(agentId);
		if(!webSiteInfo.getResultCode().equals(ResultCode.RESULT_SUCCEED)){
			result.setResultMsg(webSiteInfo.getResultMsg());
			result.setResultCode(webSiteInfo.getResultCode());
			return result;
		}
		
		//获取网页关于我们的新闻内容
		Map<String,Object> resultList = newsService.getAboutMeContent(domain);
		webSiteInfo.getResultObj().setIsAboutMe(resultList==null?"no":"yes");
		
		return webSiteInfo;
	}
	
	/**
	 * 代理商是否有支付模块
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/isPay")
	private Boolean isPay(HttpServletRequest request, HttpServletResponse response,String domain){
		Boolean result = false;
		BackResult<AgentWebSiteDomain> awsd = super.getAgentIdByDoman(domain);
        if(ResultCode.RESULT_SUCCEED.equals(awsd.getResultCode())){
        	//获取代理商的支付信息
			BackResult<String> agentPay = agentService.getAgentPayInfo(awsd.getResultObj().getAgentId());
			if(ResultCode.RESULT_SUCCEED.equals(agentPay.getResultCode())){
				result = "1".equals(agentPay.getResultObj())?true:false;
			}
        }
		return result;		
	}
	
	private Boolean isWhiteUser(String mobile,String ip){
		Boolean result = false;
		//请求参数
		Map<String,Object> param = new HashMap<>();
		param.put("appId", ymAppID);
		param.put("appKey", ymAppKey);
		param.put("mobile", mobile);
		param.put("ip", ip);
		param.put("type", "0");
		//1. 调用防羊毛党接口
		String tempResult = HttpClient.post(yangmaoUrl, param);
		JSONObject resultJson = JSONObject.parseObject(tempResult);
		 // 2.处理返回结果
        if (resultJson != null) {
            //响应code码。200000：成功，其他失败
            String code = resultJson.getString("code");
            if ("200000".equals(code)) {
                // 调用羊毛党检测成功
                // 解析结果数据，进行业务处理
                // 检测结果  W1：白名单 B1 ：黑名单  B2 ：可信用度低  N：未找到
                String status = JSONObject.parseObject(resultJson.getString("data")).getString("status");
                result = "W1".equals(status)?true:false;
                logger.info("用户【" + mobile + "】调用防羊毛党接口成功，ip: " + ip + ",resultJson: " + tempResult);
            }
        }
        
		return result;		
	}
}
