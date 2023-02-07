package cn.feignclient.credit_feign_web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import cn.feignclient.credit_feign_web.redis.RedisClient;
import cn.feignclient.credit_feign_web.service.AgentService;
import cn.feignclient.credit_feign_web.service.UserFeignService;
import cn.feignclient.credit_feign_web.thread.ThreadExecutorService;
import main.java.cn.common.BackResult;
import main.java.cn.common.RedisKeys;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.AgentWebSiteDomain;
import main.java.cn.domain.CreUserDomain;
import main.java.cn.hhtp.util.MD5Util;

public class BaseController {
	
	@Autowired
	protected UserFeignService userFeignService;

	@Autowired
	protected RedisClient redisClinet;

	@Autowired
	protected ThreadExecutorService threadExecutorService;
	
	@Value("${clNumberAppid}")
	protected String clNumberAppid;

	@Autowired
	private RedisTemplate<String, CreUserDomain> redisTemplate;
	
	@Autowired
	protected AgentService agentService;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	/**
	 * 检查是否登录
	 * 
	 * @param mobile
	 * @param token
	 * @return
	 */
	public Boolean isLogin(String mobile, String token) {
		String redisToken = redisClinet.get("user_token_" + mobile);

		if (null == redisToken || "".equals(redisToken)) {
			return false;
		}

		redisClinet.set("user_token_" + mobile, redisToken);

		return redisToken.equals(token) ? true : false;
	}
	
	/**
	 * 只允许单点登录
	 * 
	 * @param mobile
	 * @return
	 */
	public Boolean isLoginToSingle(String mobile) {
		String redisToken = redisClinet.get("user_token_" + mobile);
		if (null == redisToken || "".equals(redisToken)) {
			return false;
		}

		return true;
	}

	/**
	 * 获取请求的真实ＩＰ地址
	 * 
	 * @param request
	 * @return
	 */
//	public String getIpAddr(HttpServletRequest request) {
//		String ip = request.getHeader("x-forwarded-for");
//		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//			ip = request.getHeader("Proxy-Client-IP");
//		}
//		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//			ip = request.getHeader("WL-Proxy-Client-IP");
//		}
//		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//			ip = request.getRemoteAddr();
//		}
//		return ip;
//	}
	
	/**
	 * 获取请求的真实ＩＰ地址
	 * 
	 * @param request
	 * @return
	 */
	public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for"); 
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {  
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if( ip.indexOf(",")!=-1 ){
                ip = ip.split(",")[0];
            }
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("X-Real-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        } 
        return ip;  
    }
	
	/**
	 * 数据中心接口 签名验证
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Boolean checkSignForCLSource(HttpServletRequest request) {
		Enumeration paramNames = request.getParameterNames();
		Map map = new HashMap();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			if (paramValues.length == 1) {
				String paramValue = paramValues[0];
				if (paramValue.length() != 0) {
					map.put(paramName, paramValue);
				}
			}
		}

		if (map == null || map.size() <= 0) {
			return Boolean.FALSE;
		}
		
		if (map.get("appID") == null || map.get("appID").equals("null") || map.get("appID").equals("")) {
			return Boolean.FALSE;
		}
		
		if (!clNumberAppid.equals(map.get("appID").toString())) {
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}



	
	/**
	 * 根据手机号码获取用户对象 (缓存30分钟)
	 *
	 * @param mobile
	 * @return
	 */
	protected CreUserDomain findByMobile(String mobile) {

		CreUserDomain creuserdomain = new CreUserDomain();
		String skey = RedisKeys.getInstance().getSessUserInfo(mobile);
		creuserdomain = redisTemplate.opsForValue().get(skey);

		if (null == creuserdomain) {
			BackResult<CreUserDomain> result = userFeignService.findbyMobile(mobile,null);

			if (result.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
				creuserdomain = result.getResultObj();
				redisTemplate.opsForValue().set(skey, creuserdomain, 30 * 60, TimeUnit.SECONDS);
			}
		}
		return creuserdomain;
	}
	
	/**
	 * 根据手机号码获取用户对象 (缓存30分钟)
	 *
	 * @param mobile
	 * @return
	 */
	protected CreUserDomain findByMobileNew(String mobile,String ip) {
		BackResult<CreUserDomain> result = userFeignService.findbyMobile(mobile,ip);
		if (result.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
			return result.getResultObj();
		}
		
		return null;
	}

	/**
	 * 根据手机号码获取用户对象 (缓存30分钟)
	 *
	 * @param userId
	 * @return
	 */
	protected CreUserDomain findByUserId(Integer userId) {

		CreUserDomain creuserdomain = new CreUserDomain();
		String skey = RedisKeys.getInstance().getSessUserInfoByUserId(userId);
		creuserdomain = redisTemplate.opsForValue().get(skey);

		if (null == creuserdomain) {
			BackResult<CreUserDomain> result = userFeignService.findById(userId);

			if (result.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
				creuserdomain = result.getResultObj();
				redisTemplate.opsForValue().set(skey, creuserdomain, 30 * 60, TimeUnit.SECONDS);
			}
		}
		return creuserdomain;
	}
	
	
	
//	/**
//	 * 根据手机号码获取用户对象 (缓存30分钟)
//	 * @param mobile
//	 * @return tds
//	 */
//	protected TdsUserDomain getUserModuRole(Integer userId) {
//
//		List<TdsFunctionDomain> tdsFunctionDomain = new ArrayList<TdsFunctionDomain>();
//		String skey = RedisKeys.getInstance().getUserInfokey(userId);
//		tdsFunctionDomain= redisTemplateTdList.opsForValue().get(skey);
//
//		if (null == tdsFunctionDomain) {
//			BackResult<List<TdsFunctionDomain>> result = tdsUserLoginFeignService.moduleLoadingByUsreId(userId);
//
//			if (result.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
//				tdsFunctionDomain = result.getResultObj();
//				redisTemplateTds.opsForValue().set(skey, tdsFunctionDomain, 30 * 60, TimeUnit.SECONDS);
//			}
//		} 
//		return tdsFunctionDomain;
//	}
	
	public BackResult<AgentWebSiteDomain> getAgentIdByDoman(String domain) {
		BackResult<AgentWebSiteDomain> result = new BackResult<AgentWebSiteDomain>();
//		String domain = request.getHeader("X-Server-Name");
		if(StringUtils.isBlank(domain)){
			result.setResultCode(ResultCode.DOMAIN_NULL);
			result.setResultMsg("获取域名失败，域名为空");
			return result;
		}
		return agentService.getAgentIdByDomain(domain);		
	}
	
	

}
