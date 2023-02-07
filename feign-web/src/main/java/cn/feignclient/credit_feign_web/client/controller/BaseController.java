package cn.feignclient.credit_feign_web.client.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import cn.feignclient.credit_feign_web.client.service.UserFeignClientService;
import cn.feignclient.credit_feign_web.redis.RedisClient;
import cn.feignclient.credit_feign_web.thread.ThreadExecutorService;
import main.java.cn.common.BackResult;
import main.java.cn.common.RedisKeys;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.CreUserClientDomain;
import main.java.cn.domain.CreUserDomain;

public class BaseController {
	
	@Autowired
	protected UserFeignClientService userFeignService;

	@Autowired
	protected RedisClient redisClinet;

	@Autowired
	protected ThreadExecutorService threadExecutorService;

	@Autowired
	private RedisTemplate<String, CreUserClientDomain> redisTemplate;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	public Boolean isLogin(String userName, String token) {
		String redisToken = redisClinet.get("client:user_token_" + userName);
		if (null == redisToken || "".equals(redisToken)) {
			return false;
		}

		redisClinet.set("client:user_token_" + userName, redisToken);
		return redisToken.equals(token) ? true : false;
	}
	
	public Boolean isLoginToSingle(String mobile) {
		String redisToken = redisClinet.get("client:user_token_" + mobile);
		if (null == redisToken || "".equals(redisToken)) {
			return false;
		}

		return true;
	}
	
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

	protected CreUserClientDomain findByUserName(String userName) {

		String skey = RedisKeys.getInstance().getSessUserInfo(userName);
		CreUserClientDomain creuserdomain = redisTemplate.opsForValue().get(skey);
		if (null == creuserdomain) {
			BackResult<CreUserClientDomain> result = userFeignService.findByUserName(userName);
			if (result.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
				creuserdomain = result.getResultObj();
				redisTemplate.opsForValue().set(skey, creuserdomain, 30 * 60, TimeUnit.SECONDS);
			}
		}
		return creuserdomain;
	}

	protected CreUserClientDomain findByUserId(Integer userId) {

		CreUserClientDomain creuserdomain = new CreUserClientDomain();
		String skey = RedisKeys.getInstance().getSessUserInfoByUserId(userId);
		creuserdomain = redisTemplate.opsForValue().get(skey);

		if (null == creuserdomain) {
			BackResult<CreUserClientDomain> result = userFeignService.findById(userId);
			if (result.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
				creuserdomain = result.getResultObj();
				redisTemplate.opsForValue().set(skey, creuserdomain, 30 * 60, TimeUnit.SECONDS);
			}
		}
		return creuserdomain;
	}
}
