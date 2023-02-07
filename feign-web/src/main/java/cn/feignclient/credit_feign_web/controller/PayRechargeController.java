package cn.feignclient.credit_feign_web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.feignclient.credit_feign_web.service.TrdOrderFeignService;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.PackageDomain;
import main.java.cn.domain.TrdOrderDomain;

@RestController
@RequestMapping("/web/trdorder")
public class PayRechargeController extends BaseController{
	
	@Autowired
	private TrdOrderFeignService trdOrderFeignService;
	
	private final static Logger logger = LoggerFactory.getLogger(UserController.class);

	@RequestMapping("/alipayrecharge")
	public BackResult<String> recharge(HttpServletRequest request, HttpServletResponse response,
			String mobile, String token,Integer creUserId,Integer productsId,Integer number,BigDecimal money,String payType,String type,String userType) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<String> result = new BackResult<String>();

		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
			return result;
			
		}

//		if (CommonUtils.isNotString(token)) {
//			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
//			result.setResultMsg("token不能为空");
//			return result;
//		}
//
//		if (!isLogin(mobile, token)) {
//			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
//			result.setResultMsg("注销校验失败无法注销");
//			return result;
//		} 
		
		if (CommonUtils.isNotIngeter(creUserId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("用户ID不能为空");
			return result;
		}
		
		if (CommonUtils.isNotIngeter(productsId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("产品ID不能为空");
			return result;
		}
		
		if (CommonUtils.isNotIngeter(number)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("条数不能为空");
			return result;
		}
		
		if (CommonUtils.isNotBigDecimal(money)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("金额不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(payType)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("支付渠道类型不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(type)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("交易类型不能为空");
			return result;
		}
		
		try {
			result = trdOrderFeignService.alipayrecharge(creUserId,productsId,number,money,payType,type,userType);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户手机号：" + mobile + "执行支付宝充值出现异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
		
		return result;
	}
	
	@RequestMapping("/getPayPackage")
	public BackResult<JSONObject> getPayPackage(HttpServletRequest request, HttpServletResponse response,
			String mobile, String token,Integer creUserId,String productId) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<JSONObject> result = new BackResult<JSONObject>();
		JSONObject json = new JSONObject();
		
		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
			return result;
			
		}

		if (CommonUtils.isNotString(token)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("token不能为空");
			return result;
		}

		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("操作失败，请先登录");
			return result;
		} 
		
		if (CommonUtils.isNotIngeter(creUserId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("用户ID不能为空");
			return result;
		}
		
		if (StringUtils.isBlank(productId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("产品id不能为空");
			return result;
		}
		
		try {
			//获取套餐信息
			BackResult<List<PackageDomain>> packageResult = trdOrderFeignService.getPayPackage(creUserId,productId);
			if(packageResult == null || !ResultCode.RESULT_SUCCEED.equals(packageResult.getResultCode())){
				result.setResultCode(ResultCode.RESULT_FAILED);
				result.setResultMsg("获取套餐信息失败，请联系客服");
				return result;
			}
			
			List<PackageDomain> packageList = packageResult.getResultObj();
			PackageDomain definedPackage = new PackageDomain();
			List<PackageDomain> tempList = new ArrayList<>();
			for(PackageDomain pd : packageList){
				if("自定义充值".equals(pd.getPackageName())){
					definedPackage = pd;
				}else{
					tempList.add(pd);
				}
			}
			
			json.put("definedPackage", definedPackage);
			json.put("packageList", tempList);
			result.setResultObj(json);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户手机号：" + mobile + "执行获取充值套餐出现异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
		
		return result;
	}
	
	
	@RequestMapping("/findOrderInfoByOrderNo")
	public BackResult<TrdOrderDomain> findOrderInfoByOrderNo(HttpServletRequest request, HttpServletResponse response,
			String mobile, String token,String orderNo) {

		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");

		BackResult<TrdOrderDomain> result = new BackResult<TrdOrderDomain>();

		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
			return result;
			
		}

		if (CommonUtils.isNotString(token)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("token不能为空");
			return result;
		}

		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("注销校验失败无法注销");
			return result;
		} 
		
		if (CommonUtils.isNotString(orderNo)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("交易类型不能为空");
			return result;
		}
		
		try {
			result = trdOrderFeignService.findOrderInfoByOrderNo(orderNo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户手机号：" + mobile + "查询订单支付状态：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
		
		return result;
	}
	
}
