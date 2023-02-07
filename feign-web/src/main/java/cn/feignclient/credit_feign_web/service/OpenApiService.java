package cn.feignclient.credit_feign_web.service;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.alibaba.fastjson.JSONObject;

import main.java.cn.common.BackResult;
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

/**
 * 飓金荣通api调用
 *
 */
@FeignClient(value = "carriersService")
public interface OpenApiService{
	
	@RequestMapping(value = "/chuanglan/openApi", method = RequestMethod.POST)
	BackResult<List<ApiResultDomain>> openApi(@RequestBody JSONObject paramJsonString);
	
	@RequestMapping(value = "/chuanglan/bankAuth", method = RequestMethod.POST)
	BackResult<BankResultDomain> bankAuth(@RequestBody JSONObject paramJsonString);
	
	@RequestMapping(value = "/chuanglan/mobileAuth", method = RequestMethod.POST)
	BackResult<CarriersResultDomain> mobileAuth(@RequestBody JSONObject paramJsonString);
	
	@RequestMapping(value = "/chuanglan/mobileThreeDetailAuth", method = RequestMethod.POST)
	BackResult<CarriersDetailResultDomain> mobileThreeDetailAuth(@RequestBody JSONObject paramJsonString);
	
	@RequestMapping(value = "/chuanglan/idCardAuth", method = RequestMethod.POST)
	BackResult<IdCardResultDomain> idCardAuth(@RequestBody JSONObject paramJsonString);
	
	@RequestMapping(value = "/chuanglan/mobileStatusQuery", method = RequestMethod.POST)
	BackResult<MobileStateDomain> mobileStatusQuery(@RequestBody JSONObject paramJsonString);
	
	@RequestMapping(value = "/chuanglan/ocr/faceIdentyCompare", method = RequestMethod.POST)
	BackResult<SelfIdenVDomain> faceIdentyCompare(@RequestBody JSONObject paramJsonString);
	
	@RequestMapping(value = "/chuanglan/ocr/businessLicenseOcr", method = RequestMethod.POST)
	BackResult<LicenseResultDomain> businessLicenseOcr(@RequestBody JSONObject paramJsonString);
	
	@RequestMapping(value = "/chuanglan/ocr/idCardOcr", method = RequestMethod.POST)
	BackResult<JSONObject> idCardOcr(@RequestBody JSONObject paramJsonString);
	
	@RequestMapping(value = "/chuanglan/ocr/bankOcr", method = RequestMethod.POST)
	BackResult<BankOcrDomain> bankOcr(@RequestBody JSONObject paramJsonString);
	
	@RequestMapping(value = "/chuanglan/ocr/driverOcr", method = RequestMethod.POST)
	BackResult<JSONObject> driverOcr(@RequestBody JSONObject paramJsonString);
	
	@RequestMapping(value = "/chuanglan/ocr/faceToFaceCompare", method = RequestMethod.POST)
	BackResult<SelfSelfDomain> faceToFaceCompare(@RequestBody JSONObject paramJsonString);
	
	@RequestMapping(value = "/chuanglan/ocr/checkLiveness", method = RequestMethod.POST)
	BackResult<LivenessDomain> checkLiveness(@RequestBody JSONObject paramJsonString);
	
	@RequestMapping(value = "/chuanglan/ocr/getCloudImage", method = RequestMethod.POST)
	BackResult<JSONObject> getCloudImage(@RequestBody JSONObject paramJsonString);
	
	@RequestMapping(value = "/chuanglan/bankAuthDetail", method = RequestMethod.POST)
	BackResult<BankResultDomain> bankAuthDetail(@RequestBody JSONObject paramJsonString);
}
