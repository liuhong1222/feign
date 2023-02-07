package cn.feignclient.credit_feign_web.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.ErpTradeDomain;
import main.java.cn.domain.TrdOrderDomain;
import main.java.cn.domain.UserAccountDomain;
import main.java.cn.domain.page.PageDomain;

@Component
public class UserAccountFeignServiceHiHystric implements UserAccountFeignService {

	@Override
	public BackResult<UserAccountDomain> findbyMobile(String mobile) {
		return new BackResult<UserAccountDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务findbyMobile出现异常");
	}

	@Override
	public BackResult<ErpTradeDomain> rechargeOrRefunds(TrdOrderDomain trdOrderDomain) {
		return new BackResult<ErpTradeDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务rechargeOrRefunds出现异常");
	}

	@Override
	public BackResult<List<TrdOrderDomain>> findTrdOrderByCreUserId(Integer creUserId) {
		return new BackResult<List<TrdOrderDomain>>(ResultCode.RESULT_FAILED, "user-provider-service服务findTrdOrderByCreUserId出现异常");
	}

	@Override
	public BackResult<Boolean> consumeApiAccount(String creUserId, String count) {
		return new BackResult<Boolean>(ResultCode.RESULT_FAILED, "user-provider-service服务consumeApiAccount出现异常");
	}

	@Override
	public BackResult<Boolean> consumeRqApiAccount(String creUserId, String count) {
		return new BackResult<Boolean>(ResultCode.RESULT_FAILED, "user-provider-service服务consumeRqApiAccount出现异常");
	}

	@Override
	public BackResult<Boolean> consumeAccount(String creUserId, String count) {
		return new BackResult<Boolean>(ResultCode.RESULT_FAILED, "user-provider-service服务consumeAccount出现异常");
	}

	@Override
	public BackResult<PageDomain<Map<String,Object>>> pageFindTrdOrderByCreUserId(Integer creUserId, Integer pageSize,
			Integer pageNum) {
		return new BackResult<PageDomain<Map<String,Object>>>(ResultCode.RESULT_FAILED, "user-provider-service服务pageFindTrdOrderByCreUserId出现异常");
	}

	@Override
	public BackResult<String> updateResultPwd(String mobile, String resultPwd) {
		return new BackResult<String>(ResultCode.RESULT_FAILED, "user-provider-service服务updateResultPwd出现异常");
	}

	@Override
	public BackResult<String> getResultPwd(String creUserId) {
		return new BackResult<String>(ResultCode.RESULT_FAILED, "user-provider-service服务getResultPwd出现异常");
	}

	@Override
	public BackResult<UserAccountDomain> findByUserId(String creUserId) {
		// TODO Auto-generated method stub
		return null;
	}

}
