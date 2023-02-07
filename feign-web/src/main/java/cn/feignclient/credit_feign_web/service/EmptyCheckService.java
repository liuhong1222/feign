package cn.feignclient.credit_feign_web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.feignclient.credit_feign_web.redis.DistributedLockWrapper;
import cn.feignclient.credit_feign_web.redis.RedisClient;
import main.java.cn.common.ApiResult;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.ApiAccountInfoDomain;
import main.java.cn.domain.UserAccountDomain;
import main.java.cn.until.RedisKeyUtil;
import redis.clients.jedis.JedisPool;

@Service
public class EmptyCheckService {

	private final static Logger logger = LoggerFactory.getLogger(EmptyCheckService.class);
	
	@Autowired
	private ApiAccountInfoFeignService apiAccountInfoFeignService;
	
	@Autowired
	private CreditProviderService creditProviderService;
	
	@Autowired
	private UserAccountFeignService userAccountFeignService;
	
	@Autowired
	private RedisClient redisClient;
	
	@Autowired
	private JedisPool jedisPool;
	
	private Map<String, ApiAccountInfoDomain> apiAccountInfoMap = new HashMap<String, ApiAccountInfoDomain>();
	
	public ApiAccountInfoDomain getApiAccountInfoByAppId(String appId) {
		ApiAccountInfoDomain domain = apiAccountInfoMap.get(appId);
		if (domain == null) {
			BackResult<ApiAccountInfoDomain> result = apiAccountInfoFeignService.findByAppId(appId);
			if (!ResultCode.RESULT_SUCCEED.equals(result.getResultCode())) {
				return null;
			}
			
			if (StringUtils.isBlank(result.getResultObj().getName()) || StringUtils.isBlank(result.getResultObj().getPassword())) {
				return null;
			}
			
			apiAccountInfoMap.put(appId, result.getResultObj());
			return result.getResultObj();
		}
				
		return domain;
	}
	
	public ApiResult batchUcheck(String mobiles,Integer creUserId,String ip) {
		Long st = System.currentTimeMillis();
		List<String> mobileList = new ArrayList<String>(Arrays.asList(mobiles.split(",")));
		Boolean preChargeBoolean = preDeductFee(creUserId, mobileList.size());
		if (preChargeBoolean == null) {
			logger.error("{},检测失败，账号无余额记录",creUserId);
			return ApiResult.failed(ResultCode.RESULT_API_NOTACCOUNT, "API账号余额不存在");
		}
		
		if (!preChargeBoolean) {
			return ApiResult.failed(ResultCode.RESULT_API_NOTCOUNT, "余额不足");
		}
		
		ApiResult apiResult = creditProviderService.batchUcheck(mobiles, creUserId);
		if (apiResult == null || !apiResult.getCode().equals(ResultCode.RESULT_SUCCEED)) {
			logger.error("{}, 检测失败，info:{},ip:{},耗时：{}",creUserId,JSON.toJSONString(apiResult),ip,(System.currentTimeMillis()-st));
			backFee(creUserId, mobileList.size());
			return apiResult;
		}
		
		deductFee(creUserId, mobileList.size(), apiResult.getChargeCount());
		logger.error("{}, 检测成功，号码个数:{},ip:{},耗时：{}",creUserId,mobileList.size(),ip,(System.currentTimeMillis()-st));
		
		st = null;
		mobileList = null;
		preChargeBoolean = null;
		
		return apiResult;
	}
	
	public Integer getBalance(Integer creUserId) {
		return getApiAccount(creUserId);
	}
	
	private void deductFee(Integer creUserId,Integer preCounts,Integer counts) {
		try {
			if (preCounts > counts) {
				redisClient.incrBy(RedisKeyUtil.getApiAccountKey(creUserId), Long.valueOf(preCounts-counts));
			}else if(preCounts < counts){
				logger.error("{}, 扣费异常，预扣条数大于实际扣费条数，preCounts:{},counts:{}",creUserId,preCounts,counts);
			}else {
				
			}
			
			redisClient.incrBy(RedisKeyUtil.getApiSettlementKey(creUserId), Long.valueOf(counts));
			logger.error("{}, 扣费成功，preCounts:{},counts:{}",creUserId,preCounts,counts);
		} catch (Exception e) {
			logger.error("{}, 扣费异常，counts:{}, info:",creUserId,counts,e);
		}
	}
	
	private void backFee(Integer creUserId,Integer counts) {
		try {
			long balance = redisClient.incrBy(RedisKeyUtil.getApiAccountKey(creUserId), Long.valueOf(counts));
			logger.error("{}, 返还预扣费成功，counts:{}, balance:{}",creUserId,counts,balance);
		} catch (Exception e) {
			logger.error("{}, 返还预扣费异常，counts:{}, info:",creUserId,counts,e);
		}
		
	}
	
	private Boolean preDeductFee(Integer creUserId,Integer counts) {
		try {
			Integer apiCounts = getApiAccount(creUserId);
			if (apiCounts == null) {
				return null;
			}
			
			Long balanceLong = redisClient.decrBy(RedisKeyUtil.getApiAccountKey(creUserId), Long.valueOf(counts));
			if (balanceLong < 0) {
				logger.error("{}，预扣费失败，余额不足，counts:{},扣减后余额:{}",creUserId,counts,balanceLong);
				redisClient.incrBy(RedisKeyUtil.getApiAccountKey(creUserId), Long.valueOf(counts));
				return false;
			}
			
			logger.info("{}，预扣费成功，counts:{},扣减后余额:{}",creUserId,counts,balanceLong);
			
			apiCounts = null;
			balanceLong = null;
			
			return true;
		} catch (Exception e) {
			logger.error("{}，预扣费异常，counts:{},info:",creUserId,counts);
			return null;
		}
		
	}
	
	private Integer getApiAccount(Integer creUserId) {
		Integer apiCounts = 0;
		String redisAccount = redisClient.get(RedisKeyUtil.getApiAccountKey(creUserId));
		if (StringUtils.isBlank(redisAccount)) {
			DistributedLockWrapper lock = new DistributedLockWrapper(jedisPool, RedisKeyUtil.getApiAccountKey(creUserId)+"_hitcross_locker", 1000L * 10, 60 * 1000);
			try {
				if (StringUtils.isBlank(lock.getIdentifier())) {
		        	Thread.sleep(80l);
					return getApiAccount(creUserId);
		        }
				
				BackResult<UserAccountDomain> domainResult = userAccountFeignService.findByUserId(creUserId.toString());
				if (!ResultCode.RESULT_SUCCEED.equals(domainResult.getResultCode())) {
					return null;
				}
				
				apiCounts = domainResult.getResultObj().getApiAccount();
				redisClient.set(RedisKeyUtil.getApiAccountKey(creUserId), apiCounts.toString());
				return apiCounts;
			} catch (Exception e) {
				logger.error("{}，获取用户数据库余额异常，info:",creUserId,e);
				return null;
			}finally {
				lock.releaseLock();
			}			
		}
		
		return Integer.valueOf(redisAccount);
	}
}
