package cn.feignclient.credit_feign_web.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.cn.common.BackResult;
import main.java.cn.domain.AgentApplyInfoDomain;
import main.java.cn.domain.AgentCreUserDomain;
import main.java.cn.domain.AgentWebSiteDomain;
import main.java.cn.domain.WebSiteInfoDomain;

/**
 * 代理商
 *
 */
@FeignClient(value = "user-provider-service")
public interface AgentService{
	
	@RequestMapping(value = "/agent/getAgentIdByDomain", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<AgentWebSiteDomain> getAgentIdByDomain(@RequestParam("domain") String domain);
	
	@RequestMapping(value = "/agent/saveAgentCreUser", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<Integer> saveAgentCreUser(@RequestBody AgentCreUserDomain agentCreUserDomain);
	
	@RequestMapping(value = "/agent/getWebSiteInfo", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<WebSiteInfoDomain> getWebSiteInfo(@RequestParam("agentId") String agentId);
	
	@RequestMapping(value = "/agent/getAgentPayInfo", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<String> getAgentPayInfo(@RequestParam("agentId") String agentId);
	
	@RequestMapping(value = "/agent/getAgentInfoByCreUserId", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<AgentWebSiteDomain> getAgentInfoByCreUserId(@RequestParam("creUserId") String creUserId);
	
	@RequestMapping(value = "/agent/agentApply", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<String> agentApply(@RequestBody AgentApplyInfoDomain agentApplyInfoDomain);
}
