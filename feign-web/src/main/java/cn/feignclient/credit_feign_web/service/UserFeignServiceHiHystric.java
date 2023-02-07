package cn.feignclient.credit_feign_web.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.BusinessInfoDomain;
import main.java.cn.domain.CreUserAccountDomain;
import main.java.cn.domain.CreUserDomain;
import main.java.cn.domain.CreUserWarningDomain;
import main.java.cn.domain.IdCardInfoDomain;

@Component
public class UserFeignServiceHiHystric implements UserFeignService {

	@Override
	public BackResult<CreUserDomain> findbyMobile(String mobile,String ip) {
		return new BackResult<CreUserDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务findbyMobile出现异常");
	}

	@Override
	public BackResult<CreUserDomain> findOrsaveUser(Map<String,Object> param) {
		return new BackResult<CreUserDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务findOrsaveUser出现异常");
	}

	@Override
	public BackResult<CreUserDomain> updateCreUser(CreUserDomain creUserDomain) {
		return new BackResult<CreUserDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务updateCreUser出现异常");
	}

	@Override
	public BackResult<CreUserDomain> updateCreUser(String userPhone, String email) {
		return new BackResult<CreUserDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务updateCreUser出现异常");
	}

	@Override
	public BackResult<CreUserDomain> activateUser(CreUserDomain creUserDomain) {
		return new BackResult<CreUserDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务activateUser出现异常");
	}

	@Override
	public BackResult<CreUserDomain> activateUserZzt(CreUserDomain creUserDomain) {
		return new BackResult<CreUserDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务activateUserZzt出现异常");
	}

	@Override
	public BackResult<CreUserDomain> findById(Integer id) {
		return new BackResult<CreUserDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务findById出现异常");
	}

	@Override
	public BackResult<CreUserAccountDomain> getUserBalance(String apiName, String password) {
		return new BackResult<CreUserAccountDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务getUserBalance出现异常");
	}

	@Override
	public BackResult<CreUserWarningDomain> getUserWarning(String userId, String productName) {
		return new BackResult<CreUserWarningDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务getUserWarning出现异常");
	}

	@Override
	public BackResult<String> updateUserWarning(String warningCount, String informMobiles, String userId,
			String productName) {
		return new BackResult<String>(ResultCode.RESULT_FAILED, "user-provider-service服务updateUserWarning出现异常");
	}

	@Override
	public BackResult<List<Map<String, Object>>> getCreUserApiConsumeCounts(String userId, String productName,
			String month) {
		return new BackResult<List<Map<String, Object>>>(ResultCode.RESULT_FAILED, "user-provider-service服务getCreUserApiConsumeCounts出现异常");
	}

	@Override
	public BackResult<Integer> updateUserPhone(String userId, String phone) {
		return new BackResult<Integer>(ResultCode.RESULT_FAILED, "user-provider-service服务updateUserPhone出现异常");
	}

	@Override
	public BackResult<Map<String, Object>> getUserAuthInfo(String userId, String userType) {
		return new BackResult<Map<String, Object>>(ResultCode.RESULT_FAILED, "user-provider-service服务getUserAuthInfo出现异常");
	}

	@Override
	public BackResult<CreUserDomain> findOrSaveUserByOpenId(Map<String, Object> param) {
		return new BackResult<CreUserDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务findOrSaveUserByOpenId出现异常");
	}

	@Override
	public BackResult<CreUserDomain> findUserByOpenId(String openId) {
		return new BackResult<CreUserDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务findUserByOpenId出现异常");
	}

	@Override
	public BackResult<String> subUserAuthByIdCard(IdCardInfoDomain idCardInfoDomain) {
		return new BackResult<String>(ResultCode.RESULT_FAILED, "user-provider-service服务subUserAuthByIdCard出现异常");
	}

	@Override
	public BackResult<String> subUserAuthByBusiness(BusinessInfoDomain businessInfoDomain) {
		return new BackResult<String>(ResultCode.RESULT_FAILED, "user-provider-service服务subUserAuthByBusiness出现异常");
	}

	@Override
	public BackResult<CreUserDomain> userRegister(Map<String, Object> param) {
		return new BackResult<CreUserDomain>(ResultCode.RESULT_FAILED, "user-provider-service服务userRegister出现异常");
	}

	@Override
	public BackResult<List<CreUserDomain>> getAllUserData(String mobile) {
		return new BackResult<List<CreUserDomain>>(ResultCode.RESULT_FAILED, "user-provider-service服务getAllUserData出现异常");
	}

}
