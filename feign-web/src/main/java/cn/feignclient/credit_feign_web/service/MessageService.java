package cn.feignclient.credit_feign_web.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户消息
 *
 */
@FeignClient(value = "user-provider-service")
public interface MessageService{
	
	@RequestMapping(value = "/message/messageList", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	List<Map<String,Object>> messageList(@RequestParam("customer_id")String customer_id,@RequestParam("isRead")String isRead,@RequestParam("offset")int offset,@RequestParam("pageSize")int pageSize);
	
	@RequestMapping(value = "/message/getNoReadMessageCount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	long getNoReadMessageCount(@RequestParam("customer_id")String customer_id);
	
	@RequestMapping(value = "/message/getMessageListCount", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	long getMessageListCount(@RequestParam("customer_id")String customer_id,@RequestParam("isRead")String isRead);
	
	@RequestMapping(value = "/message/updateMessageStatus", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	int updateMessageStatus(@RequestParam("customer_id")String customer_id);
	
	@RequestMapping(value = "/message/readMessage", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> readMessage(@RequestParam("customer_id")String customer_id,@RequestParam("message_id")String message_id);
	
	@RequestMapping(value = "/message/deleteMessage", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	int deleteMessage(@RequestParam("customer_id")String customer_id,@RequestParam("messageStr")String messageStr);
}
