package cn.feignclient.credit_feign_web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.feignclient.credit_feign_web.service.MessageService;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.ApiLogPageDomain;
import main.java.cn.domain.page.PageDomain;
import main.java.cn.until.ResultJsonUtil;

@RestController
@RequestMapping("/web/message")
public class MessageController extends BaseController{
	
	@Autowired
	private MessageService messageService;
	
	private final static Logger logger = LoggerFactory.getLogger(MessageController.class);
	
	/**
	 * 消息列表
	 * @return
	 */
	@RequestMapping("/messageList")
	public BackResult<PageDomain<Map<String,Object>>> messageList(HttpServletRequest request, HttpServletResponse response, int pageNo, int pageSize, String userId,String userPhone, String isRead,String token) {
		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");		
		BackResult<PageDomain<Map<String,Object>>> result = new BackResult<PageDomain<Map<String,Object>>>();
		PageDomain<Map<String,Object>> page = new PageDomain<Map<String,Object>>();
		if (!isLogin(userPhone, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户校验失败");
			return result;
		}
		
		//获取该用户消息列表
		List<Map<String,Object>> resultList = messageService.messageList(userId,isRead,(pageNo-1)*pageSize,pageSize);
		int resultCount = (int)messageService.getMessageListCount(userId,isRead);
		int totalPage = resultCount%pageSize==0?(resultCount/pageSize):((resultCount/pageSize)+1);
		page.setTotalPages(totalPage);
		page.setNumPerPage(pageSize);
		page.setCurrentPage(pageNo);
		page.setTotalNumber((int)resultCount);
		page.setTlist(resultList);
		if(resultList == null || resultList.size()==0){
			logger.error("加载用户"+userId+"的消息列表: 操作失败，消息列表为空");
			return result; 
		}
		result.setResultObj(page);
		return result;
	}
	
	/**
	 * 获取用户未读消息个数
	 * @return
	 */
	@RequestMapping("/getNoReadMessageCount")
	public String getNoReadMessageCount(HttpServletRequest request, HttpServletResponse response, String userId,String userPhone, String token) {
		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");		
		
		if (!isLogin(userPhone, token)) {
			logger.error(userPhone+": 获取用户未读消息个数: 操作失败，用户未登录");
			return ResultJsonUtil.getFailResultJson("加载未读消息失败, 用户未登录"); 
		}
		if(StringUtils.isBlank(userId)){
			logger.error(userPhone+": 获取用户未读消息个数: 操作失败，用户id为空");
			return ResultJsonUtil.getFailResultJson("加载未读消息失败, 用户未登录"); 
		}
		//获取该用户未读消息个数
		long resultList = messageService.getNoReadMessageCount(userId);
		return ResultJsonUtil.getSuccessResultJson(resultList);
	}
	
	/**
	 * 修改用户消息为已读
	 * @return
	 */
	@RequestMapping("/updateMessageStatus")
	public String updateMessageStatus(HttpServletRequest request, HttpServletResponse response, String userId,String userPhone, String token) {
		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");		
		
		if (!isLogin(userPhone, token)) {
			logger.error(userPhone+": 获取用户未读消息个数: 操作失败，用户未登录");
			return ResultJsonUtil.getFailResultJson("加载未读消息失败, 用户未登录"); 
		}
		if(StringUtils.isBlank(userId)){
			logger.error(userPhone+": 修改用户消息为已读: 操作失败，用户id为空");
			return ResultJsonUtil.getFailResultJson("操作失败, 用户未登录"); 
		}
		//获取修改用户消息的个数
		int resultList = messageService.updateMessageStatus(userId);
		return ResultJsonUtil.getSuccessResultJson(resultList);
	}
	
	/**
	 * 查看某条消息
	 * @return
	 */
	@RequestMapping("/readMessage")
	public String readMessage(HttpServletRequest request, HttpServletResponse response, String userId,String message_id,String userPhone, String token) {
		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");		
		
		if (!isLogin(userPhone, token)) {
			logger.error(userPhone+": 获取用户未读消息个数: 操作失败，用户未登录");
			return ResultJsonUtil.getFailResultJson("加载未读消息失败, 用户未登录"); 
		}		
		if(StringUtils.isBlank(userId)){
			logger.error(userPhone+": 查看某条消息: 操作失败，用户id为空");
			return ResultJsonUtil.getFailResultJson("操作失败, 用户未登录"); 
		}
		if(StringUtils.isBlank(message_id)){
			logger.error(userPhone+": 用户"+userId+"查看某条消息: 操作失败，未选中任何消息");
			return ResultJsonUtil.getFailResultJson("操作失败, 未选中任何消息"); 
		}
		//获取消息信息
		Map<String,Object> resultList = messageService.readMessage(userId,message_id);
		if(resultList == null){
			logger.error(userPhone+": 用户"+userId+": 查看"+message_id+"消息失败，数据库异常");
			return ResultJsonUtil.getFailResultJson("操作失败, 数据库异常");
		}
		return ResultJsonUtil.getSuccessResultJson(resultList);
	}
	
	/**
	 * 删除消息
	 * @return
	 */
	@RequestMapping("/deleteMessage")
	public String deleteMessage(HttpServletRequest request, HttpServletResponse response, String userId,String message_ids,String userPhone, String token) {
		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");		
		
		if (!isLogin(userPhone, token)) {
			logger.error(userPhone+":操作失败，用户未登录");
			return ResultJsonUtil.getFailResultJson("加载未读消息失败, 用户未登录"); 
		}		
		if(StringUtils.isBlank(userId)){
			logger.error(userPhone+": 操作失败，用户id为空");
			return ResultJsonUtil.getFailResultJson("操作失败, 用户未登录"); 
		}
		if(StringUtils.isBlank(message_ids)){
			logger.error(userPhone+": 用户"+userId+"操作失败，未选中任何消息");
			return ResultJsonUtil.getFailResultJson("操作失败, 未选中任何消息"); 
		}
		String[] messageList = message_ids.split(",");
		String messageStr = "";
		for(String message: messageList){
			messageStr += "'" + message + "',";
		}		
		//获取消息信息
		int count = messageService.deleteMessage(userId,messageStr.substring(0, messageStr.length()-1));
		if(count != messageList.length){
			logger.error(userPhone+": 用户"+userId+": 删除"+message_ids+"消息失败，数据库异常");
			return ResultJsonUtil.getFailResultJson("操作失败, 数据库异常");
		}
		return ResultJsonUtil.getSuccessResultJson(count);
	}
}
