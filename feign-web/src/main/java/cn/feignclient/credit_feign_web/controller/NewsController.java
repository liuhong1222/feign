package cn.feignclient.credit_feign_web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.feignclient.credit_feign_web.service.NewsService;
import main.java.cn.common.BackResult;
import main.java.cn.domain.page.PageDomain;
import main.java.cn.until.ResultJsonUtil;

@RestController
@RequestMapping("/web/news")
public class NewsController extends BaseController{
	
	@Autowired
	private NewsService newsService;
	
	private final static Logger logger = LoggerFactory.getLogger(NewsController.class);
	
	/**
	 * 新闻列表
	 * @return
	 */
	@RequestMapping("/newsList")
	public BackResult<PageDomain<Map<String,Object>>> newsList(HttpServletRequest request, HttpServletResponse response,String domain, int pageNo, int pageSize) {
		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");		
		BackResult<PageDomain<Map<String,Object>>> result = new BackResult<PageDomain<Map<String,Object>>>();
		PageDomain<Map<String,Object>> page = new PageDomain<Map<String,Object>>();
		
		//获取该用户新闻列表
		List<Map<String,Object>> resultList = newsService.newsList(domain,(pageNo-1)*pageSize,pageSize);
		int resultCount = (int)newsService.getNewsListCount(domain);
		int totalPage = resultCount%pageSize==0?(resultCount/pageSize):((resultCount/pageSize)+1);
		page.setTotalPages(totalPage);
		page.setNumPerPage(pageSize);
		page.setCurrentPage(pageNo);
		page.setTotalNumber((int)resultCount);
		page.setTlist(resultList);
		if(resultList == null || resultList.size()==0){
			logger.error("操作失败，新闻列表为空");
			return result; 
		}
		result.setResultObj(page);
		return result;
	}
	
	/**
	 * 查看某条新闻
	 * @return
	 */
	@RequestMapping("/readNews")
	public String readNews(HttpServletRequest request, HttpServletResponse response, String news_id) {
		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");		
		
		//获取消息信息
		Map<String,Object> resultList = newsService.readNews(news_id);
		if(resultList == null){
			logger.error("查看新闻失败，数据库异常");
			return ResultJsonUtil.getFailResultJson("操作失败, 数据库异常");
		}
		return ResultJsonUtil.getSuccessResultJson(resultList);
	}
	
	/**
	 * 获取网页关于我们的新闻内容
	 * @return
	 */
	@RequestMapping("/getAboutMeContent")
	public String getAboutMeContent(HttpServletRequest request, HttpServletResponse response, String domain) {
		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");		
		
		//获取网页关于我们的新闻内容
		Map<String,Object> resultList = newsService.getAboutMeContent(domain);
		if(resultList == null){
			logger.error("获取关于我们的新闻内容失败，数据库异常");
			return ResultJsonUtil.getFailResultJson("操作失败, 数据库异常");
		}
		return ResultJsonUtil.getSuccessResultJson(resultList);
	}
	
	/**
	 * 获取最新2条新闻
	 * @return
	 */
	@RequestMapping("/getTop2News")
	public String getTop2News(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");		
		
		//获取消息信息
		List<Map<String,Object>> resultList = newsService.getTop2News();
		return ResultJsonUtil.getSuccessResultJson(resultList);
	}
}
