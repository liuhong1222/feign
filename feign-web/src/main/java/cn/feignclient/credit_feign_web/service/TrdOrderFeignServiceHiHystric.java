package cn.feignclient.credit_feign_web.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.PackageDomain;
import main.java.cn.domain.TrdOrderDomain;

@Component
public class TrdOrderFeignServiceHiHystric implements TrdOrderFeignService {

	@Override
	public BackResult<String> alipayrecharge(Integer creUserId, Integer productsId, Integer number, BigDecimal money,
			String payType, String type,String userType) {
		return new BackResult<String>(ResultCode.RESULT_FAILED, "user-provider-service服务alipayrecharge出现异常");
	}

	@Override
	public BackResult<TrdOrderDomain> findOrderInfoByOrderNo(String orderNo) {
		return new BackResult<TrdOrderDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务findOrderInfoByOrderNo出现异常");
	}

	@Override
	public BackResult<List<PackageDomain>> getPayPackage(Integer creUserId,String productId) {
		return new BackResult<List<PackageDomain>>(ResultCode.RESULT_FAILED, "user-provider-service服务getPayPackage出现异常");
	}

	@Override
	public BackResult<List<Map<String, Object>>> getSummyOrderList(String startDate, String endDate) {
		return new BackResult<List<Map<String, Object>>>(ResultCode.RESULT_FAILED, "user-provider-service服务getSummyOrderList出现异常");
	}

	@Override
	public BackResult<String> messageInvite(String creUserId, String userName, String userPhone, int inviteType) {
		return new BackResult<String>(ResultCode.RESULT_FAILED, "user-provider-service服务messageInvite出现异常");
	}

	@Override
	public BackResult<List<Map<String, Object>>> inviteList(String creUserId) {
		return new BackResult<List<Map<String, Object>>>(ResultCode.RESULT_FAILED, "user-provider-service服务inviteList出现异常");
	}

}
