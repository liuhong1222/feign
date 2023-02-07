package cn.feignclient.credit_feign_web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import main.java.cn.common.BackResult;
import main.java.cn.domain.ApiLogPageDomain;
import main.java.cn.domain.BankPageDomain;
import main.java.cn.domain.CarriersPageDomain;
import main.java.cn.domain.LivenessPageDomain;
import main.java.cn.domain.MobileTestLogDomain;
import main.java.cn.domain.OcrPageDomain;
import main.java.cn.domain.SelfIdenVPageDomain;
import main.java.cn.domain.page.PageDomain;

@RestController
@RequestMapping("/web/feign/apiMobileTest")
public class ApiMobileTestController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(ApiAccountInfoController.class);

	@RequestMapping(value = "/getPageByUserId", method = RequestMethod.POST)
	public BackResult<PageDomain<MobileTestLogDomain>> getPageByUserId(HttpServletRequest request,
			HttpServletResponse response, int pageNo, int pageSize, String creUserId, String mobile, String token,String type) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		return new BackResult<>();

	}
	
	@RequestMapping(value = "/getPageByCustomerId", method = RequestMethod.POST)
	public BackResult<PageDomain<ApiLogPageDomain>> getPageByCustomerId(HttpServletRequest request,
			HttpServletResponse response, int pageNo, int pageSize, String customerId, String mobile, String token, String method) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		return new BackResult<>();	}

	/**
	 * 银行卡鉴权接口查询列表
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param customerId
	 * @param mobile
	 * @param token
	 * @param method
	 * @return
	 */
	@RequestMapping(value = "/getBankPageByCustomerId", method = RequestMethod.POST)
	public BackResult<PageDomain<BankPageDomain>> getBankPageByCustomerId(HttpServletRequest request,
			HttpServletResponse response, int pageNo, int pageSize, String customerId, String mobile, String token, String method) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		return new BackResult<>();	}
	
	/**
	 * 运营商三要素接口查询列表
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param customerId
	 * @param mobile
	 * @param token
	 * @param method
	 * @return
	 */
	@RequestMapping(value = "/getCarriersPageByCustomerId", method = RequestMethod.POST)
	public BackResult<PageDomain<CarriersPageDomain>> getCarriersPageByCustomerId(HttpServletRequest request,
			HttpServletResponse response, int pageNo, int pageSize, String customerId, String mobile, String token, String method) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		return new BackResult<>();

	}
	
	/**
	 * OCR相关接口查询列表
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param customerId
	 * @param mobile
	 * @param token
	 * @param method
	 * @return
	 */
	@RequestMapping(value = "/getOcrPageByCustomerId", method = RequestMethod.POST)
	public BackResult<PageDomain<OcrPageDomain>> getOcrPageByCustomerId(HttpServletRequest request,
			HttpServletResponse response, int pageNo, int pageSize, String customerId, String mobile, String token, String method) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		return new BackResult<>();

	}
	
	/**
	 * 活体检测接口查询列表
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param customerId
	 * @param mobile
	 * @param token
	 * @param method
	 * @return
	 */
	@RequestMapping(value = "/getLivenessPageByCustomerId", method = RequestMethod.POST)
	public BackResult<PageDomain<LivenessPageDomain>> getLivenessPageByCustomerId(HttpServletRequest request,
			HttpServletResponse response, int pageNo, int pageSize, String customerId, String mobile, String token, String method) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		return new BackResult<>();

	}
	
	/**
	 * 人脸识别接口查询列表
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param customerId
	 * @param mobile
	 * @param token
	 * @param method
	 * @return
	 */
	@RequestMapping(value = "/getSelfPageByCustomerId", method = RequestMethod.POST)
	public BackResult<PageDomain<SelfIdenVPageDomain>> getSelfPageByCustomerId(HttpServletRequest request,
			HttpServletResponse response, int pageNo, int pageSize, String customerId, String mobile, String token, String method) {

		response.setHeader("Access-Control-Allow-Origin", "*"); //有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		return new BackResult<>();
	}
	
	/**
	 * 号码实时在线查询接口查询列表
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param customerId
	 * @param mobile
	 * @param token
	 * @param method
	 * @return
	 */
	@RequestMapping(value = "/getMobileStatePageByCustomerId", method = RequestMethod.POST)
	public BackResult<PageDomain<MobileTestLogDomain>> getMobileStatePageByCustomerId(HttpServletRequest request,
			HttpServletResponse response, int pageNo, int pageSize, String customerId, String mobile, String token, String method) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		return new BackResult<>();
	}
}
