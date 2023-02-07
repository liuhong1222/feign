package cn.feignclient.credit_feign_web.controller;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import cn.feignclient.credit_feign_web.service.CreditProviderService;
import cn.feignclient.credit_feign_web.service.OpenApiService;
import cn.feignclient.credit_feign_web.service.UserAccountFeignService;
import cn.feignclient.credit_feign_web.service.UserAuthService;
import cn.feignclient.credit_feign_web.utils.DateUtils;
import cn.feignclient.credit_feign_web.utils.FileUtils;
import cn.feignclient.credit_feign_web.utils.UUIDTool;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.FileUploadDomain;
import main.java.cn.domain.UserAccountDomain;
import main.java.cn.domain.carriers.LicenseResultDomain;
import main.java.cn.until.Base64Img;

@RestController
@RequestMapping("/web/file")
public class FileController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(FileController.class);

	@Value("${fielUrl}")
	private String fielUrl;

	@Autowired
	private CreditProviderService creditProviderService;
	
	@Autowired
	private OpenApiService openApiService;
	
	@Autowired
	private UserAuthService userAuthService;
	
	@Autowired
	private UserAccountFeignService userAccountFeignService;

	// 文件上传相关代码
	@RequestMapping("/upload")
	@ResponseBody
	public BackResult<FileUploadDomain> upload(HttpServletRequest request, HttpServletResponse response,
			MultipartFile file, String mobile, String token) {

		BackResult<FileUploadDomain> result = new BackResult<FileUploadDomain>();

		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户校验失败");
			return result;
		}

		if (null == file || file.isEmpty()) {
			logger.error("用户手机号：【" + mobile + "】执行文件上传出现异常文件不存在");
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("文件地址为空");
			return result;
		}
		
		if (file.getSize() > 10485760 * 4) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("文件已超过40M");
			return result;
		}
		//获取用户冻结信息
		BackResult<UserAccountDomain> userAccountDomain = userAccountFeignService.findbyMobile(mobile);
		if(!ResultCode.RESULT_SUCCEED.equals(userAccountDomain.getResultCode())){
			result.setResultCode(userAccountDomain.getResultCode());
			result.setResultMsg(userAccountDomain.getResultMsg());
			return result;
		}
		
		if(userAccountDomain.getResultObj().getIsFrozen() != null
				&& userAccountDomain.getResultObj().getIsFrozen()==1){
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("上传文件失败，您的帐号已被冻结，请联系客服");
			return result;
		}

		// 获取文件名
		String fileName = file.getOriginalFilename();
		logger.info("上传的文件名为：" + fileName);
		// 获取文件的后缀名
		String suffixName = fileName.substring(fileName.lastIndexOf("."));
		
		// 文件上传后的路径
		String filePath = fielUrl + DateUtils.formatDate(new Date()) + "/";
		
		// 解决中文问题，liunx下中文路径，图片显示问题
		fileName = UUIDTool.getInstance().getUUID() + "_" + mobile + suffixName;
		File dest = new File(filePath + fileName);
		
		// 检测是否存在目录
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}

		try {

			file.transferTo(dest);
			// 文件名存入数据库
			FileUploadDomain domain = new FileUploadDomain();
			domain.setUserId(this.findByMobile(mobile).getId().toString());
			domain.setFileName(file.getOriginalFilename().substring(0,file.getOriginalFilename().lastIndexOf(".")));
			domain.setFileRows(FileUtils.getFileLinesNotNullRow(filePath + fileName));
			domain.setFileUploadUrl(filePath + fileName);
			BackResult<FileUploadDomain> resultFileUpload = creditProviderService.saveFileUpload(domain);
			if (!resultFileUpload.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
				result = new BackResult<>(resultFileUpload.getResultCode(), resultFileUpload.getResultMsg());
			}

			result.setResultObj(resultFileUpload.getResultObj());
			result.setResultMsg("上传成功");
			
			logger.info("用户手机号：" + "【" + mobile + "】执行文件上传(" + fileName + ")成功!");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户手机号：【" + mobile + "】执行文件上传出现系统异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}

		return result;
	}
	
	// 文件上传相关代码
		@RequestMapping("/authPictureUpload")
		@ResponseBody
		public BackResult<JSONObject> authPictureUpload(HttpServletRequest request, HttpServletResponse response,
				MultipartFile file, String mobile,String apiName,String userId,String pictureType, String token) {

			BackResult<JSONObject> result = new BackResult<JSONObject>();
			//返回结果
			JSONObject resultJson = new JSONObject();
			
			if (!isLogin(mobile, token)) {
				result.setResultCode(ResultCode.RESULT_SESSION_STALED);
				result.setResultMsg("用户校验失败");
				return result;
			}

			if (null == file || file.isEmpty()) {
				logger.error("用户手机号：【" + mobile + "】执行文件上传出现异常文件不存在");
				result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
				result.setResultMsg("文件地址为空");
				return result;
			}
			
			if (file.getSize() > 5242880) {
				result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
				result.setResultMsg("上传文件不得超过5M");
				return result;
			}

			// 获取文件名
			String fileName = file.getOriginalFilename();
			logger.info(mobile + "上传的文件名为：" + fileName);
			// 获取文件的后缀名
			String suffixName = fileName.substring(fileName.lastIndexOf("."));
			
			// 文件上传后的路径
			String filePath = fielUrl + "webAuthPicture/" + apiName + "/";
			
			// 解决中文问题，liunx下中文路径，图片显示问题
			fileName = UUIDTool.getInstance().getUUID() + suffixName;
			File dest = new File(filePath + fileName);
			
			// 检测是否存在目录
			if (!dest.getParentFile().exists()) {
				dest.getParentFile().mkdirs();
			}

			try {
				//参数列表
				JSONObject paramList = new JSONObject();
				paramList.put("imgBase64Str", Base64Img.GetImageStrFromBytes(file.getBytes()));
				//上传文件
				file.transferTo(dest);
				if("authIdFace".equals(pictureType)){
					paramList.put("side", "face");
				}else if("authIdBack".equals(pictureType)){
					paramList.put("side", "back");
				}			
				paramList.put("appid", apiName);
				paramList.put("orderNo", System.nanoTime());
				if("authBusiLiven".equals(pictureType)){//营业执照OCR
					String effectDate = null;
					String expireDate = null;
					// 执行检测返回检测结果
					BackResult<LicenseResultDomain> resultApi = openApiService.businessLicenseOcr(paramList);
					if(resultApi == null || !"000000".equals(resultApi.getResultCode()) || 
							("000000".equals(resultApi.getResultCode()) && !"true".equals(resultApi.getResultObj().getSuccess()))){
						result.setResultCode(resultApi.getResultCode());
						result.setResultMsg(resultApi.getResultMsg());
						return result;
					}
					//查询该营业执照是否已认证过
					BackResult<Boolean> isAuth = userAuthService.isAuthByIdentyNo("business", resultApi.getResultObj().getRegNum());
					if(isAuth.getResultObj()==true){
						result.setResultCode(ResultCode.RESULT_FAILED);
						result.setResultMsg("认证失败，该证件号码已认证");
						return result;
					}
					//营业执照生效时间
					effectDate = DateUtils.formatDate(StringUtils.isBlank(resultApi.getResultObj().getEstablishDate())?DateUtils.getToday():resultApi.getResultObj().getEstablishDate(), "yyyy-MM-dd");
					//营业执照失效时间
					expireDate = DateUtils.formatDate(StringUtils.isBlank(resultApi.getResultObj().getValidPeriod())?DateUtils.getToday():resultApi.getResultObj().getValidPeriod(), "yyyy-MM-dd");
					Map<String,Object> resultMap = new HashMap<>();
					resultMap.put("cre_user_id", userId);
					resultMap.put("regnum", resultApi.getResultObj().getRegNum());
					resultMap.put("name", resultApi.getResultObj().getName());
					resultMap.put("person", resultApi.getResultObj().getPerson());
					resultMap.put("effectDate", effectDate);
					resultMap.put("expireDate", expireDate);
					resultMap.put("address", resultApi.getResultObj().getAddress());
					resultMap.put("captial", resultApi.getResultObj().getCaptial());
					resultMap.put("business", resultApi.getResultObj().getBusiness());
					resultMap.put("elbem", resultApi.getResultObj().getElbem());
					resultMap.put("title", resultApi.getResultObj().getTitle());
					resultMap.put("stamp", resultApi.getResultObj().getStamp());
					resultMap.put("qrcode", resultApi.getResultObj().getQrcode());
					resultMap.put("picture_url", "webAuthPicture/" + apiName + "/" + fileName);
					//保存营业执照信息
					BackResult<String> msg = userAuthService.saveBusAuthData(resultMap);
					if(!"success".equals(msg.getResultObj())){
						result.setResultCode(ResultCode.RESULT_FAILED);
						result.setResultMsg("操作失败, 请重新上传图片");
						return result;
					}
					
					resultJson.put("name", resultApi.getResultObj().getName());
					resultJson.put("regnum", resultApi.getResultObj().getRegNum());
					resultJson.put("address", resultApi.getResultObj().getAddress());
					resultJson.put("person", resultApi.getResultObj().getPerson());
					resultJson.put("effectDate", effectDate);
					resultJson.put("expireDate", expireDate);
					resultJson.put("business", resultApi.getResultObj().getBusiness());
				}else{//身份证OCR
					// 执行检测返回检测结果
					BackResult<JSONObject> resultApi = openApiService.idCardOcr(paramList);
					if(resultApi == null || !"000000".equals(resultApi.getResultCode()) || 
							("000000".equals(resultApi.getResultCode()) && !"true".equals(resultApi.getResultObj().getString("success")))){
						result.setResultCode(resultApi.getResultCode());
						result.setResultMsg(resultApi.getResultMsg());
						return result;
					}
					//身份证正面
					if("authIdFace".equals(pictureType)){
						//查询该身份证是否已认证过
						BackResult<Boolean> isAuth = userAuthService.isAuthByIdentyNo("idcard", resultApi.getResultObj().getString("num"));
						if(isAuth.getResultObj()==true){
							result.setResultCode(ResultCode.RESULT_FAILED);
							result.setResultMsg("认证失败，该证件号码已认证");
							return result;
						}
						
						Map<String,Object> resultMap = new HashMap<>();
						resultMap.put("cre_user_id", userId);
						resultMap.put("username", resultApi.getResultObj().getString("name"));
						resultMap.put("sex", resultApi.getResultObj().getString("sex"));
						resultMap.put("nation", resultApi.getResultObj().getString("nationality"));
						resultMap.put("birthday", DateUtils.formatDate(StringUtils.isBlank(resultApi.getResultObj().getString("birth"))?DateUtils.getToday():resultApi.getResultObj().getString("birth"), "yyyy-MM-dd"));
						resultMap.put("address", resultApi.getResultObj().getString("address"));
						resultMap.put("idno", resultApi.getResultObj().getString("num"));
						resultMap.put("faceUrl", "webAuthPicture/" + apiName + "/" + fileName);
						//保存身份证信息
						BackResult<String> msg = userAuthService.saveIdcardAuthData(resultMap);
						if(!"success".equals(msg.getResultObj())){
							result.setResultCode(ResultCode.RESULT_FAILED);
							result.setResultMsg("操作失败, 请重新上传图片");
							return result;
						}
						
						resultJson.put("username", resultApi.getResultObj().getString("name"));
						resultJson.put("address", resultApi.getResultObj().getString("address"));
						resultJson.put("idno", resultApi.getResultObj().getString("num"));
					}else{
						//身份证生效日期
						String effectDate = DateUtils.formatDate(StringUtils.isBlank(resultApi.getResultObj().getString("startDate"))?DateUtils.getToday():resultApi.getResultObj().getString("startDate"), "yyyy-MM-dd");
						//身份证失效日期
						String expireDate = DateUtils.formatDate(StringUtils.isBlank(resultApi.getResultObj().getString("endDate"))?DateUtils.getToday():resultApi.getResultObj().getString("endDate"), "yyyy-MM-dd");
						if(DateUtils.getNowDate().getTime()>DateUtils.parseDate(expireDate, "yyyy-MM-dd").getTime()){
							result.setResultCode(ResultCode.RESULT_FAILED);
							result.setResultMsg("认证失败，该身份证已过期，请重新上传身份证图片");
							return result;
						}
						Map<String,Object> resultMap = new HashMap<>();
						resultMap.put("cre_user_id", userId);
						resultMap.put("signer", resultApi.getResultObj().getString("issue"));
						resultMap.put("effectDate", effectDate);
						resultMap.put("expireDate", expireDate);						
						resultMap.put("backUrl", "webAuthPicture/" + apiName + "/" + fileName);
						//保存身份证信息
						BackResult<String> msg = userAuthService.saveIdcardAuthData(resultMap);
						if(!"success".equals(msg.getResultObj())){
							result.setResultCode(ResultCode.RESULT_FAILED);
							result.setResultMsg("操作失败, 请重新上传图片");
							return result;
						}
						
						resultJson.put("effectDate", effectDate);
						resultJson.put("expireDate", expireDate);						
					}
					
				}
				
				resultJson.put("pictureUrl", "webAuthPicture/" + apiName + "/" + fileName);
				result.setResultObj(resultJson);
				result.setResultMsg("上传成功");
				
				logger.info("用户手机号：" + "【" + mobile + "】执行文件上传(" + fileName + ")成功!");

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("用户手机号：【" + mobile + "】执行文件上传出现系统异常：" + e.getMessage());
				result.setResultCode(ResultCode.RESULT_FAILED);
				result.setResultMsg("系统异常");
			}

			return result;
		}
}
