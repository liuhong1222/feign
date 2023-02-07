package cn.feignclient.credit_feign_web.service;

import main.java.cn.common.BackResult;
import main.java.cn.domain.BusinessInfoDomain;
import main.java.cn.domain.CreUserAccountDomain;
import main.java.cn.domain.CreUserDomain;
import main.java.cn.domain.CreUserWarningDomain;
import main.java.cn.domain.IdCardInfoDomain;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;

@FeignClient(value = "user-provider-service",fallback = UserFeignServiceHiHystric.class)
public interface UserFeignService {
	
	@RequestMapping(value = "/user/findbyMobile", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<CreUserDomain> findbyMobile(@RequestParam("mobile")String mobile,@RequestParam("ip")String ip);
	
	@RequestMapping(value = "/user/findOrsaveUser", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<CreUserDomain> findOrsaveUser(@RequestBody Map<String,Object> param);
	
	@RequestMapping(value = "/user/findOrSaveUserByOpenId", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<CreUserDomain> findOrSaveUserByOpenId(@RequestBody Map<String,Object> param);
	
	@RequestMapping(value = "/user/findUserByOpenId", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<CreUserDomain> findUserByOpenId(@RequestParam("openId")String openId);
	
	@RequestMapping(value = "/user/updateCreUser", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<CreUserDomain> updateCreUser(@RequestBody CreUserDomain creUserDomain);
	
	@RequestMapping(value = "/user/updateCreUserEmail", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<CreUserDomain> updateCreUser(@RequestParam("userPhone")String userPhone, @RequestParam("email")String email);
	
	@RequestMapping(value = "/user/activateUser", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<CreUserDomain> activateUser(@RequestBody CreUserDomain creUserDomain);
	
	@RequestMapping(value = "/user/activateUserZzt", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<CreUserDomain> activateUserZzt(@RequestBody CreUserDomain creUserDomain);

	@RequestMapping(value = "/user/findById", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<CreUserDomain> findById(@RequestParam("id")Integer id);

	@RequestMapping(value = "/user/getUserBalance", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<CreUserAccountDomain> getUserBalance(@RequestParam("apiName")String apiName,@RequestParam("password")String password);
	
	@RequestMapping(value = "/user/getUserWarning", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<CreUserWarningDomain> getUserWarning(@RequestParam("userId")String userId,@RequestParam("productName")String productName);
	
	@RequestMapping(value = "/user/updateUserWarning", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<String> updateUserWarning(@RequestParam("warningCount")String warningCount,@RequestParam("informMobiles")String informMobiles,@RequestParam("userId")String userId,@RequestParam("productName")String productName);
	
	@RequestMapping(value = "/user/subUserAuthByIdCard", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<String> subUserAuthByIdCard(@RequestBody IdCardInfoDomain idCardInfoDomain);
	
	@RequestMapping(value = "/user/subUserAuthByBusiness", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<String> subUserAuthByBusiness(@RequestBody BusinessInfoDomain businessInfoDomain);
	
	@RequestMapping(value = "/user/getUserAuthInfo", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<Map<String,Object>> getUserAuthInfo(@RequestParam("userId")String userId,@RequestParam("userType")String userType);
	
	@RequestMapping(value = "/user/getCreUserApiConsumeCounts", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<List<Map<String,Object>>> getCreUserApiConsumeCounts(@RequestParam("userId")String userId,@RequestParam("productName")String productName,@RequestParam("month")String month);

	@RequestMapping(value = "/user/updateUserPhone", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<Integer> updateUserPhone(@RequestParam("userId")String userId,@RequestParam("phone")String phone);
	
	@RequestMapping(value = "/user/userRegister", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<CreUserDomain> userRegister(@RequestBody Map<String,Object> param);
	
	@RequestMapping(value = "/user/getAllUserData", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<List<CreUserDomain>> getAllUserData(@RequestParam("mobile")String mobile);
}

