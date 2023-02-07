package cn.feignclient.credit_feign_web.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.cn.common.BackResult;
					 
@FeignClient(value = "credit-provider-service")
public interface ContractService {
	
	@RequestMapping(value = "/contract/getPdfFileByHtml", method = RequestMethod.POST)
	public BackResult<String> getPdfFileByHtml(@RequestParam(value = "userId")String userId);

}
