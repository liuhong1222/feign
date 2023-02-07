package cn.feignclient.credit_feign_web.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.cn.common.BackResult;
import main.java.cn.domain.PackageDomain;
import main.java.cn.domain.TrdOrderDomain;

@FeignClient(value = "user-provider-service",fallback = TrdOrderFeignServiceHiHystric.class)
public interface TrdOrderFeignService {

	@RequestMapping(value = "/trdorder/alipayrecharge", method = RequestMethod.GET)
	BackResult<String> alipayrecharge(@RequestParam("creUserId")Integer creUserId,@RequestParam("productsId")Integer productsId,@RequestParam("number")Integer number,@RequestParam("money")BigDecimal money,@RequestParam("payType")String payType,@RequestParam("type")String type,@RequestParam("userType")String userType);

	@RequestMapping(value = "/trdorder/findOrderInfoByOrderNo", method = RequestMethod.GET)
	BackResult<TrdOrderDomain> findOrderInfoByOrderNo(@RequestParam("orderNo")String orderNo);
	
	@RequestMapping(value = "/trdorder/getPayPackage", method = RequestMethod.GET)
	BackResult<List<PackageDomain>> getPayPackage(@RequestParam("creUserId")Integer creUserId, @RequestParam("productId")String productId);
	
	@RequestMapping(value = "/trdorder/getSummyOrderList", method = RequestMethod.GET)
	BackResult<List<Map<String,Object>>> getSummyOrderList(@RequestParam("startDate")String startDate, @RequestParam("endDate")String endDate);
	
	@RequestMapping(value = "/trdorder/messageInvite", method = RequestMethod.POST)
	BackResult<String> messageInvite(@RequestParam("creUserId")String creUserId, @RequestParam("userName")String userName, @RequestParam("userPhone")String userPhone,@RequestParam("inviteType")int inviteType);
	
	@RequestMapping(value = "/trdorder/inviteList", method = RequestMethod.POST)
	BackResult<List<Map<String, Object>>> inviteList(@RequestParam("creUserId")String creUserId);
}
