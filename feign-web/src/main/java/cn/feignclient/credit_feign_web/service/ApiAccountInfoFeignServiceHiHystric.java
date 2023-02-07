package cn.feignclient.credit_feign_web.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.AccountInfoDomain;
import main.java.cn.domain.ApiAccountInfoDomain;

@Component
public class ApiAccountInfoFeignServiceHiHystric implements ApiAccountInfoFeignService {

	@Override
	public BackResult<ApiAccountInfoDomain> findByCreUserId(String creUserId) {
		return new BackResult<ApiAccountInfoDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务findByCreUserId出现异常");
	}

	@Override
	public BackResult<ApiAccountInfoDomain> updateApiAccountInfo(ApiAccountInfoDomain domain) {
		return new BackResult<ApiAccountInfoDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务updateApiAccountInfo出现异常");
	}

	@Override
	public BackResult<Integer> checkApiAccount(String apiName, String password, String ip, int checkCount) {
		return new BackResult<Integer>(ResultCode.RESULT_FAILED, "user-provider-service服务checkApiAccount出现异常");
	}

	@Override
	public BackResult<Integer> checkRqApiAccount(String apiName, String password, String ip, int checkCount) {
		return new BackResult<Integer>(ResultCode.RESULT_FAILED, "user-provider-service服务checkRqApiAccount出现异常");
	}

	@Override
	public BackResult<AccountInfoDomain> checkTcAccount(String apiName, String password, String method, String ip) {
		return new BackResult<>(ResultCode.RESULT_FAILED, "user-provider-service服务checkTcAccount出现异常");
	}

	@Override
	public BackResult<Integer> updateTcAccount(Map<String, Object> params) {
		return new BackResult<>(ResultCode.RESULT_FAILED, "user-provider-service服务updateTcAccount出现异常");
	}

	@Override
	public BackResult<Integer> checkMsAccount(String apiName, String password, String ip, int checkCount) {
		return new BackResult<>(ResultCode.RESULT_FAILED, "user-provider-service服务checkMsAccount出现异常");
	}

	@Override
	public BackResult<Integer> checkFcAccountN(String apiName, String password, String ip, int checkCount) {
		return new BackResult<>(ResultCode.RESULT_FAILED, "user-provider-service服务checkFcAccountN出现异常");
	}

	@Override
	public BackResult<Integer> checkCtAccountN(String apiName, String password, String ip, int checkCount) {
		return new BackResult<>(ResultCode.RESULT_FAILED, "user-provider-service服务checkCtAccountN出现异常");
	}

	@Override
	public BackResult<Integer> checkTcAccountN(String apiName, String password, String ip, int checkCount) {
		return new BackResult<>(ResultCode.RESULT_FAILED, "user-provider-service服务checkTcAccountN出现异常");
	}

	@Override
	public BackResult<Integer> checkFiAccount(String apiName, String password, String ip, int checkCount) {
		return new BackResult<>(ResultCode.RESULT_FAILED, "user-provider-service服务checkFiAccount出现异常");
	}

	@Override
	public BackResult<Integer> checkFfAccount(String apiName, String password, String ip, int checkCount) {
		return new BackResult<>(ResultCode.RESULT_FAILED, "user-provider-service服务checkFfAccount出现异常");
	}

	@Override
	public BackResult<Integer> checkClAccount(String apiName, String password, String ip, int checkCount) {
		return new BackResult<>(ResultCode.RESULT_FAILED, "user-provider-service服务checkClAccount出现异常");
	}

	@Override
	public BackResult<Integer> checkIdocrAccount(String apiName, String password, String ip, int checkCount) {
		return new BackResult<>(ResultCode.RESULT_FAILED, "user-provider-service服务checkIdocrAccount出现异常");
	}

	@Override
	public BackResult<Integer> checkBlocrAccount(String apiName, String password, String ip, int checkCount) {
		return new BackResult<>(ResultCode.RESULT_FAILED, "user-provider-service服务checkBlocrAccount出现异常");
	}

	@Override
	public BackResult<Integer> checkBocrAccount(String apiName, String password, String ip, int checkCount) {
		return new BackResult<>(ResultCode.RESULT_FAILED, "user-provider-service服务checkBocrAccount出现异常");
	}

	@Override
	public BackResult<Integer> checkDocrAccount(String apiName, String password, String ip, int checkCount) {
		return new BackResult<>(ResultCode.RESULT_FAILED, "user-provider-service服务checkDocrAccount出现异常");
	}

	@Override
	public BackResult<ApiAccountInfoDomain> findByAppId(String appId) {
		// TODO Auto-generated method stub
		return null;
	}

}
