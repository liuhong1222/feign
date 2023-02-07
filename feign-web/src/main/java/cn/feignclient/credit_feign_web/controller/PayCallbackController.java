package cn.feignclient.credit_feign_web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.feignclient.credit_feign_web.service.PayCallBackService;

@RestController
@RequestMapping("/web/payCallback")
public class PayCallbackController {

	private final static Logger logger = LoggerFactory.getLogger(PayCallbackController.class);
	
	@Autowired
	private PayCallBackService payCallBackService;

	@RequestMapping("/alipayRecharge")
	public void service(HttpServletRequest request, HttpServletResponse response) {
		ServletOutputStream out = null;
		try {
			
			out = response.getOutputStream();
			// 获取支付宝POST过来反馈信息
			Map<String, String> params = getParamsMap(request);
			logger.info("支付宝回调"+params.toString());
			String outTradeNo = request.getParameter("out_trade_no");
			String tradeNo = request.getParameter("trade_no");
			String status = request.getParameter("trade_status");
			
			// 本地业务代码
			payCallBackService.recharge(outTradeNo, status,tradeNo);
			
			out.println("success");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			try {
				out.println("fail");
			} catch (IOException e1) {
			}
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

	}
	
	private Map<String, String> getParamsMap(HttpServletRequest request) throws UnsupportedEncodingException {
		Map<String, String> params = new HashMap<String, String>();
		@SuppressWarnings("unchecked")
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			
			logger.info("key：" + name + "values：" + values);
			
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		return params;
	}
	
	
}
