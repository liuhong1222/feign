package cn.feignclient.credit_feign_web.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.ApiResultDomain;
import main.java.cn.domain.carriers.BankOcrDomain;
import main.java.cn.domain.carriers.BankResultDomain;
import main.java.cn.domain.carriers.CarriersDetailResultDomain;
import main.java.cn.domain.carriers.CarriersResultDomain;
import main.java.cn.domain.carriers.IdCardResultDomain;
import main.java.cn.domain.carriers.LicenseResultDomain;
import main.java.cn.domain.carriers.LivenessDomain;
import main.java.cn.domain.carriers.MobileStateDomain;
import main.java.cn.domain.carriers.SelfIdenVDomain;
import main.java.cn.domain.carriers.SelfSelfDomain;

@Component
public class OpenApiServiceHiHystric implements OpenApiService {

	@Override
	public BackResult<List<ApiResultDomain>> openApi(JSONObject paramJsonString) {
		return new BackResult<>(ResultCode.RESULT_FAILED, "carriersService服务openApi出现异常");
	}

	@Override
	public BackResult<BankResultDomain> bankAuth(JSONObject paramJsonString) {
		return new BackResult<BankResultDomain>(ResultCode.RESULT_FAILED, "carriersService服务bankAuth出现异常");
	}

	@Override
	public BackResult<CarriersResultDomain> mobileAuth(JSONObject paramJsonString) {
		return new BackResult<CarriersResultDomain>(ResultCode.RESULT_FAILED, "carriersService服务mobileAuth出现异常");
	}

	@Override
	public BackResult<IdCardResultDomain> idCardAuth(JSONObject paramJsonString) {
		return new BackResult<IdCardResultDomain>(ResultCode.RESULT_FAILED, "carriersService服务idCardAuth出现异常");
	}

	@Override
	public BackResult<MobileStateDomain> mobileStatusQuery(JSONObject paramJsonString) {
		return new BackResult<MobileStateDomain>(ResultCode.RESULT_FAILED, "carriersService服务mobileStatusQuery出现异常");
	}

	@Override
	public BackResult<SelfIdenVDomain> faceIdentyCompare(JSONObject paramJsonString) {
		return new BackResult<SelfIdenVDomain>(ResultCode.RESULT_FAILED, "carriersService服务faceIdentyCompare出现异常");
	}

	@Override
	public BackResult<LicenseResultDomain> businessLicenseOcr(JSONObject paramJsonString) {
		return new BackResult<LicenseResultDomain>(ResultCode.RESULT_FAILED, "carriersService服务businessLicenseOcr出现异常");
	}

	@Override
	public BackResult<JSONObject> idCardOcr(JSONObject paramJsonString) {
		return new BackResult<JSONObject>(ResultCode.RESULT_FAILED, "carriersService服务idCardOcr出现异常");
	}

	@Override
	public BackResult<BankOcrDomain> bankOcr(JSONObject paramJsonString) {
		return new BackResult<BankOcrDomain>(ResultCode.RESULT_FAILED, "carriersService服务bankOcr出现异常");
	}

	@Override
	public BackResult<JSONObject> driverOcr(JSONObject paramJsonString) {
		return new BackResult<JSONObject>(ResultCode.RESULT_FAILED, "carriersService服务driverOcr出现异常");
	}

	@Override
	public BackResult<SelfSelfDomain> faceToFaceCompare(JSONObject paramJsonString) {
		return new BackResult<SelfSelfDomain>(ResultCode.RESULT_FAILED, "carriersService服务faceToFaceCompare出现异常");
	}

	@Override
	public BackResult<LivenessDomain> checkLiveness(JSONObject paramJsonString) {
		return new BackResult<LivenessDomain>(ResultCode.RESULT_FAILED, "carriersService服务checkLiveness出现异常");
	}

	@Override
	public BackResult<JSONObject> getCloudImage(JSONObject paramJsonString) {
		return new BackResult<JSONObject>(ResultCode.RESULT_FAILED, "carriersService服务getCloudImage出现异常");
	}

	@Override
	public BackResult<BankResultDomain> bankAuthDetail(JSONObject paramJsonString) {
		return new BackResult<BankResultDomain>(ResultCode.RESULT_FAILED, "carriersService服务bankAuthDetail出现异常");
	}

	@Override
	public BackResult<CarriersDetailResultDomain> mobileThreeDetailAuth(JSONObject paramJsonString) {
		return new BackResult<CarriersDetailResultDomain>(ResultCode.RESULT_FAILED, "carriersService服务mobileThreeDetailAuth出现异常");
	}

}
