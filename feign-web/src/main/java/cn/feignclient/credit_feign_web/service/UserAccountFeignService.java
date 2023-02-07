package cn.feignclient.credit_feign_web.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.cn.common.BackResult;
import main.java.cn.domain.ErpTradeDomain;
import main.java.cn.domain.TrdOrderDomain;
import main.java.cn.domain.UserAccountDomain;
import main.java.cn.domain.page.PageDomain;

@FeignClient(value = "user-provider-service",fallback = UserAccountFeignServiceHiHystric.class)
public interface UserAccountFeignService {
	
	@RequestMapping(value = "/userAccount/findbyMobile", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<UserAccountDomain> findbyMobile(@RequestParam("mobile")String mobile);
	
	@RequestMapping(value = "/userAccount/rechargeOrRefunds", method = RequestMethod.GET)
	BackResult<ErpTradeDomain> rechargeOrRefunds(@RequestBody TrdOrderDomain trdOrderDomain);
	
	@RequestMapping(value = "/userAccount/findTrdOrderByCreUserId", method = RequestMethod.GET)
	BackResult<List<TrdOrderDomain>> findTrdOrderByCreUserId(@RequestParam("creUserId")Integer creUserId);
	
	@RequestMapping(value = "/userAccount/consumeApiAccount" , method = RequestMethod.POST)
	BackResult<Boolean> consumeApiAccount(@RequestParam("creUserId")String creUserId,@RequestParam("count")String count);

	@RequestMapping(value = "/userAccount/consumeRqApiAccount" , method = RequestMethod.POST)
	BackResult<Boolean> consumeRqApiAccount(@RequestParam("creUserId")String creUserId,@RequestParam("count")String count);
	
	@RequestMapping(value = "/userAccount/consumeAccount" , method = RequestMethod.POST)
	BackResult<Boolean> consumeAccount(@RequestParam("creUserId")String creUserId,@RequestParam("count")String count);
	
	@RequestMapping(value = "/userAccount/pageFindTrdOrderByCreUserId", method = RequestMethod.POST)
	public BackResult<PageDomain<Map<String,Object>>> pageFindTrdOrderByCreUserId(@RequestParam("creUserId")Integer creUserId,@RequestParam("pageSize")Integer pageSize,@RequestParam("pageNum")Integer pageNum);
	
	@RequestMapping(value = "/userAccount/updateResultPwd" , method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<String> updateResultPwd(@RequestParam("mobile")String mobile,@RequestParam("resultPwd")String resultPwd);
	
	@RequestMapping(value = "/userAccount/getResultPwdNew" , method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<String> getResultPwd(@RequestParam("creUserId")String creUserId);
	
	@RequestMapping(value = "/userAccount/findByUserId", method = RequestMethod.POST)
	BackResult<UserAccountDomain> findByUserId(@RequestParam("creUserId")String creUserId);
}
