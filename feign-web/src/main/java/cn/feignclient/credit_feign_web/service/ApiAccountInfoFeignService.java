package cn.feignclient.credit_feign_web.service;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.cn.common.BackResult;
import main.java.cn.domain.AccountInfoDomain;
import main.java.cn.domain.ApiAccountInfoDomain;

@FeignClient(value = "user-provider-service",fallback = ApiAccountInfoFeignServiceHiHystric.class)
public interface ApiAccountInfoFeignService {

	@RequestMapping(value = "/apiAccountInfo/findByCreUserId", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<ApiAccountInfoDomain> findByCreUserId(@RequestParam("creUserId")String creUserId);
	
	@RequestMapping(value = "/apiAccountInfo/updateApiAccountInfo", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<ApiAccountInfoDomain> updateApiAccountInfo(@RequestBody ApiAccountInfoDomain domain);
	
	@RequestMapping(value = "/apiAccountInfo/checkApiAccount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<Integer> checkApiAccount(@RequestParam("apiName")String apiName, @RequestParam("password")String password, @RequestParam("ip")String ip, @RequestParam("checkCount")int checkCount);
	
	@RequestMapping(value = "/apiAccountInfo/checkMsAccount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<Integer> checkMsAccount(@RequestParam("apiName")String apiName, @RequestParam("password")String password, @RequestParam("ip")String ip, @RequestParam("checkCount")int checkCount);
	
	@RequestMapping(value = "/apiAccountInfo/checkFcAccountN", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<Integer> checkFcAccountN(@RequestParam("apiName")String apiName, @RequestParam("password")String password, @RequestParam("ip")String ip, @RequestParam("checkCount")int checkCount);
	
	@RequestMapping(value = "/apiAccountInfo/checkCtAccountN", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<Integer> checkCtAccountN(@RequestParam("apiName")String apiName, @RequestParam("password")String password, @RequestParam("ip")String ip, @RequestParam("checkCount")int checkCount);
	
	@RequestMapping(value = "/apiAccountInfo/checkTcAccountN", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<Integer> checkTcAccountN(@RequestParam("apiName")String apiName, @RequestParam("password")String password, @RequestParam("ip")String ip, @RequestParam("checkCount")int checkCount);
	
	@RequestMapping(value = "/apiAccountInfo/checkTcAccount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<AccountInfoDomain> checkTcAccount(@RequestParam("apiName")String apiName, @RequestParam("password")String password,@RequestParam("method")String method, @RequestParam("ip")String ip);
	
	@RequestMapping(value = "/apiAccountInfo/checkFiAccount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<Integer> checkFiAccount(@RequestParam("apiName")String apiName, @RequestParam("password")String password, @RequestParam("ip")String ip, @RequestParam("checkCount")int checkCount);
	
	@RequestMapping(value = "/apiAccountInfo/checkFfAccount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<Integer> checkFfAccount(@RequestParam("apiName")String apiName, @RequestParam("password")String password, @RequestParam("ip")String ip, @RequestParam("checkCount")int checkCount);
	
	@RequestMapping(value = "/apiAccountInfo/checkClAccount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<Integer> checkClAccount(@RequestParam("apiName")String apiName, @RequestParam("password")String password, @RequestParam("ip")String ip, @RequestParam("checkCount")int checkCount);
	
	@RequestMapping(value = "/apiAccountInfo/checkIdocrAccount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<Integer> checkIdocrAccount(@RequestParam("apiName")String apiName, @RequestParam("password")String password, @RequestParam("ip")String ip, @RequestParam("checkCount")int checkCount);
	
	@RequestMapping(value = "/apiAccountInfo/checkBlocrAccount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<Integer> checkBlocrAccount(@RequestParam("apiName")String apiName, @RequestParam("password")String password, @RequestParam("ip")String ip, @RequestParam("checkCount")int checkCount);
	
	@RequestMapping(value = "/apiAccountInfo/checkBocrAccount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<Integer> checkBocrAccount(@RequestParam("apiName")String apiName, @RequestParam("password")String password, @RequestParam("ip")String ip, @RequestParam("checkCount")int checkCount);
	
	@RequestMapping(value = "/apiAccountInfo/checkDocrAccount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<Integer> checkDocrAccount(@RequestParam("apiName")String apiName, @RequestParam("password")String password, @RequestParam("ip")String ip, @RequestParam("checkCount")int checkCount);
	
	@RequestMapping(value = "/apiAccountInfo/updateTcAccount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<Integer> updateTcAccount(@RequestBody Map<String,Object> params);
	
	@RequestMapping(value = "/apiAccountInfo/checkRqApiAccount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public BackResult<Integer> checkRqApiAccount(@RequestParam("apiName")String apiName, @RequestParam("password")String password, @RequestParam("ip")String ip, @RequestParam("checkCount")int checkCount);
	
	@RequestMapping(value = "/apiAccountInfo/findByAppId", method = RequestMethod.POST)
	public BackResult<ApiAccountInfoDomain> findByAppId(@RequestParam("appId")String appId);
}
