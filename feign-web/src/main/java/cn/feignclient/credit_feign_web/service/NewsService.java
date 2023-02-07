package cn.feignclient.credit_feign_web.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 新闻
 *
 */
@FeignClient(value = "user-provider-service")
public interface NewsService{
	
	@RequestMapping(value = "/news/newsList", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	List<Map<String,Object>> newsList(@RequestParam("domain")String domain,@RequestParam("offset")int offset,@RequestParam("pageSize")int pageSize);
	
	@RequestMapping(value = "/news/getNewsListCount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	long getNewsListCount(@RequestParam("domain")String domain);
	
	@RequestMapping(value = "/news/readNews", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> readNews(@RequestParam("news_id")String news_id);
	
	@RequestMapping(value = "/news/getTop2News", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	List<Map<String,Object>> getTop2News();
	
	@RequestMapping(value = "/news/getAboutMeContent", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getAboutMeContent(@RequestParam("domain")String domain);
}
