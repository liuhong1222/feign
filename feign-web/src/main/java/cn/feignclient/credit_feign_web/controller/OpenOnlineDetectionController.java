package cn.feignclient.credit_feign_web.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import cn.feignclient.credit_feign_web.service.CreditProviderService;
import cn.feignclient.credit_feign_web.service.UserAccountFeignService;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import cn.feignclient.credit_feign_web.utils.DateUtils;
import cn.feignclient.credit_feign_web.utils.FileUtils;
import cn.feignclient.credit_feign_web.utils.UUIDTool;
import main.java.cn.common.BackResult;
import main.java.cn.common.RedisKeys;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.CreUserDomain;
import main.java.cn.domain.CvsFilePathDlsDomain;
import main.java.cn.domain.CvsFilePathDomain;
import main.java.cn.domain.FileUploadDomain;
import main.java.cn.domain.RunTestDomian;
import main.java.cn.domain.UserAccountDomain;
import main.java.cn.domain.page.PageDomain;

@RestController
@RequestMapping("/web/onlineDetection")
public class OpenOnlineDetectionController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(OpenOnlineDetectionController.class);

	@Value("${fielUrl}")
	private String fielUrl;

	 @Value("${loadfilePath}")
	 private String loadfilePath;
	 
	@Autowired
	private CreditProviderService creditProviderService;
	
	@Autowired
	private UserAccountFeignService userAccountFeignService;

	//文件上传
	@RequestMapping("/uploadFile")
	@ResponseBody
	public BackResult<String> uploadFile(HttpServletRequest request, HttpServletResponse response,
			MultipartFile file, String mobile) {

		BackResult<String> result = new BackResult<String>();

		if (StringUtils.isBlank(mobile)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("参数手机号码不能为空");
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
			result.setResultMsg("上传文件过大最大40M 10485760kb");
			return result;
		}

		CreUserDomain user = findByMobile(mobile);
        if (null == user) {
            result.setResultCode(ResultCode.RESULT_SESSION_STALED);
            result.setResultMsg("用户校验失败，系统不存在该用户");
            return result;
        }
        
		// 获取文件名
		String fileName = file.getOriginalFilename();
		logger.info("【"  + mobile + "】上传的文件名为：" + fileName);
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

			result.setResultObj(resultFileUpload.getResultObj().getId());
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
	
	//执行文件检测
	@RequestMapping(value = "/theTest", method = RequestMethod.POST)
  	public synchronized BackResult<RunTestDomian> theTest(HttpServletResponse response,String mobile, String code,
  			String source, String startLine, String type) {
      	
      	response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
  		response.setContentType("text/json;charset=UTF-8");

  		BackResult<RunTestDomian> result = new BackResult<RunTestDomian>();

  		if (CommonUtils.isNotString(mobile)) {
  			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
  			result.setResultMsg("参数手机号码不能为空");
  			return result;
  		}

  		if (CommonUtils.isNotString(code)) {
  			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
  			result.setResultMsg("文件编号不能空");
  			return result;
  		}

  		if (CommonUtils.isNotString(source)) {
  			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
  			result.setResultMsg("来源不能为空");
  			return result;
  		}

  		if (CommonUtils.isNotString(startLine)) {
  			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
  			result.setResultMsg("开始条数不能为空");
  			return result;
  		}

  		if (CommonUtils.isNotString(type)) {
  			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
  			result.setResultMsg("类型不能为空：1检测 2查询检测结果");
  			return result;
  		}

  		if (type.equals("1")) {
  			logger.info("外部PC网站通手机号：" + mobile + "请求进行实号检测");
  		} else {
  			logger.info("外部PC网站手机号：" + mobile + "请求进行实号检测，查询检测结果");
  		}

  		try {

  			CreUserDomain user = findByMobile(mobile);
  			if (null == user) {
  				result.setResultCode(ResultCode.RESULT_SESSION_STALED);
  				result.setResultMsg("用户校验失败，系统不存在该手机号码的用户");
  				return result;
  			}
  			
  			// 第一次检测的时候进行校验
  			if (type.equals("1")) {
  				RunTestDomian runTestDomian = new RunTestDomian();
  				
  				BackResult<FileUploadDomain> resultFile = creditProviderService.findFileUploadById(code);
  				
  				if (!resultFile.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
  					return new BackResult<RunTestDomian>(ResultCode.RESULT_DATA_EXCEPTIONS, "文件检测异常，没有检测到可以检测的文件！");
  				}
  				
  				// 检测条数限制
  				if ((resultFile.getResultObj().getFileRows()) < 2999) {
  					result.setResultCode(ResultCode.RESULT_BUSINESS_EXCEPTIONS);
  					runTestDomian.setStatus("5"); // 1执行中 2执行结束 // 3执行异常4账户余额不足5检测的条数小于3000条
  					result.setResultObj(runTestDomian);
  					result.setResultMsg("检测条数必须大于3000条");
  					return result;
  				}

				if ((resultFile.getResultObj().getFileRows()) > 1500000) {
					result.setResultCode(ResultCode.RESULT_BUSINESS_EXCEPTIONS);
					runTestDomian.setStatus("5"); // 1执行中 2执行结束 // 3执行异常4账户余额不足5检测的条数小于3000条
					result.setResultObj(runTestDomian);
					result.setResultMsg("检测条数最大支持1500000条");
					return result;
				}

  				// 账户余额检测
  				BackResult<UserAccountDomain> resultUserAccount = userAccountFeignService.findbyMobile(mobile);  				
  				if (!resultUserAccount.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
  					result.setResultCode(ResultCode.RESULT_BUSINESS_EXCEPTIONS);
  					runTestDomian.setStatus("3"); // 1执行中 2执行结束 3执行异常4账户余额不足
  					result.setResultObj(runTestDomian);
  					result.setResultMsg("检测用户余额信息失败");
  					return result;
  				}
  				
  				// 获取缓存中的冻结条数
  				String wAccountKey = RedisKeys.getInstance().getAcountKey(user.getId().toString()); // 冻结的上传文件空号账户条数
  				// 冻结余额
  				String freezeAccount = redisClinet.get(wAccountKey);
  				int account = 0;
  				if (!CommonUtils.isNotString(freezeAccount)) {
  					account = account + Integer.valueOf(freezeAccount);
  				}
  				
  				if ((resultUserAccount.getResultObj().getAccount() - account) < resultFile.getResultObj().getFileRows()) {
  					result.setResultCode(ResultCode.RESULT_BUSINESS_EXCEPTIONS);
  					runTestDomian.setStatus("4"); // 1执行中 2执行结束 3执行异常4账户余额不足
  					result.setResultObj(runTestDomian);
  					result.setResultMsg("账户余额不足");
  					return result;
  				}
  				
  				// 校验通过将检测的条数存入redis中
  				String KhTestCountKey = RedisKeys.getInstance().getKhTestCountKey(String.valueOf(user.getId()),code);
  				int expire = 4 * 60 * 60 * 1000;
  				// 将需要检测的总条数放入redis
  				redisClinet.set(KhTestCountKey, String.valueOf(resultFile.getResultObj().getFileRows()), expire);
  				
  				result = creditProviderService.theTest(code, String.valueOf(user.getId()), source, mobile, startLine,
  	  					"1");
  				
  			} else {
  				result = creditProviderService.theTest(code, String.valueOf(user.getId()), source, mobile, startLine,
  	  					"2");
  			}

  		} catch (Exception e) {
  			e.printStackTrace();
  			logger.error("外部PC网站手机号：" + mobile + "请求进行实号检测，出现系统异常" + e.getMessage());
  			result.setResultCode(ResultCode.RESULT_FAILED);
  			result.setResultMsg("系统异常");
  		}

  		return result;
  	}
	
	/**
     * 分页获取下载列表
     * @param request
     * @param pageNo
     * @param pageSize
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/getPageByMobile", method = RequestMethod.POST)
	public BackResult<PageDomain<CvsFilePathDlsDomain>> getPageByUserId(HttpServletRequest request, int pageNo,
			int pageSize, String mobile) {
		logger.info("外部代理商账户：" + mobile + "请求分页获取历史检测记录");

		BackResult<PageDomain<CvsFilePathDlsDomain>> result = new BackResult<PageDomain<CvsFilePathDlsDomain>>();

		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
			return result;
		}

		if (CommonUtils.isNotIngeter(pageNo)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("页数不能为空");
			return result;
		}

		if (CommonUtils.isNotIngeter(pageSize)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("每页条数不能为空");
			return result;
		}

		try {
			CreUserDomain user = findByMobile(mobile);
  			if (null == user) {
  				result.setResultCode(ResultCode.RESULT_SESSION_STALED);
  				result.setResultMsg("用户校验失败，系统不存在该手机号码的用户");
  				return result;
  			}

  			BackResult<PageDomain<CvsFilePathDomain>> tempResult = creditProviderService.getPageByUserId(pageNo, pageSize, String.valueOf(user.getId()));
  			if(!ResultCode.RESULT_SUCCEED.equals(tempResult.getResultCode())){
  				result.setResultCode(tempResult.getResultCode());
  				result.setResultMsg(tempResult.getResultMsg());
  				return result;
  			}
  			if(tempResult.getResultObj() == null){
  				result.setResultCode(ResultCode.RESULT_FAILED);
  				result.setResultMsg("检测记录为空");
  				return result;
  			}
  			
  			if(tempResult.getResultObj().getTlist()==null || tempResult.getResultObj().getTlist().size()==0){
  				result.setResultCode(ResultCode.RESULT_FAILED);
  				result.setResultMsg("检测记录为空");
  				return result;
  			}
  			PageDomain<CvsFilePathDlsDomain> pageDomain = new PageDomain<CvsFilePathDlsDomain>();
  			List<CvsFilePathDlsDomain> list = new ArrayList<>();
  			for(CvsFilePathDomain domain: tempResult.getResultObj().getTlist()){
  				CvsFilePathDlsDomain temp = new CvsFilePathDlsDomain();
  				BeanUtils.copyProperties(domain, temp);
  				list.add(temp);
  			}
  			
  			pageDomain.setTlist(list);
  			pageDomain.setCurrentPage(tempResult.getResultObj().getCurrentPage());
  			pageDomain.setNumPerPage(tempResult.getResultObj().getNumPerPage());
  			pageDomain.setTotalNumber(tempResult.getResultObj().getTotalNumber());
  			pageDomain.setTotalPages(tempResult.getResultObj().getTotalPages());
  			result.setResultObj(pageDomain);
  			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("外部代理商账户：" + mobile + "请求分页获取历史检测记录，出现系统异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
		return result;
	}
    
    /**
	 * 根据id删除检测记录
	 * 
	 * @param request
	 * @param ids
	 * @param mobile
	 * @return
	 */
	@RequestMapping(value = "/deleteCvsByIds", method = RequestMethod.POST)
	public BackResult<Boolean> deleteCvsByIds(HttpServletRequest request, String ids, String mobile) {

		logger.info("外部代理商账户：" + mobile + "请求删除历史检测记录");

		BackResult<Boolean> result = new BackResult<Boolean>();

		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
			return result;
		}

		if (CommonUtils.isNotString(ids)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("删除记录的ids不能为空");
			return result;
		}

		try {
			CreUserDomain user = findByMobile(mobile);
  			if (null == user) {
  				result.setResultCode(ResultCode.RESULT_SESSION_STALED);
  				result.setResultMsg("用户校验失败，系统不存在该手机号码的用户");
  				return result;
  			}

			result = creditProviderService.deleteCvsByIds(ids, String.valueOf(user.getId()));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("外部代理商账户：" + mobile + "请求删除历史检测记录，出现系统异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}

		return result;
	}
	
	//文件下载
	@RequestMapping("/downloadFile")
	@ResponseBody
	public BackResult<String> downloadFile(HttpServletRequest request, HttpServletResponse response,String id,
			String fileCode,String type, String mobile) {

		BackResult<String> result = new BackResult<String>();

		if (StringUtils.isBlank(mobile)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("手机号码不能为空");
			return result;
		}

		if (StringUtils.isBlank(fileCode) && StringUtils.isBlank(id)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("下载文件code和记录id不能同时为空");
			return result;
		}
		
		if (StringUtils.isBlank(type)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("下载的检测结果文件类型不能为空");
			return result;
		}
		//检测该用户是否已存在
		CreUserDomain user = findByMobile(mobile);
        if (null == user) {
            result.setResultCode(ResultCode.RESULT_SESSION_STALED);
            result.setResultMsg("用户校验失败，系统不存在该用户");
            return result;
        }
        //获取下载文件的信息
        BackResult<CvsFilePathDomain> resultFile = creditProviderService.getCvsFilePathByFileCode(user.getId().toString(), id, fileCode);
		if (!resultFile.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
			return new BackResult<String>(ResultCode.RESULT_DATA_EXCEPTIONS, "下载文件异常，没有检测到可以下载的文件！");
		}
		//文件路径
		String filePath = "";
		//根据type下载相应的文件
		switch (type) {
		case "real"://实号包
			filePath = resultFile.getResultObj().getThereFilePath();
			if(StringUtils.isBlank(filePath)){
				result.setResultCode(ResultCode.RESULT_FAILED);
	            result.setResultMsg("下载失败，检测结果实号包为空");
	            return result;
			}
			break;
		case "null"://空号包
			filePath = resultFile.getResultObj().getSixFilePath();
			if(StringUtils.isBlank(filePath)){
				result.setResultCode(ResultCode.RESULT_FAILED);
	            result.setResultMsg("下载失败，检测结果空号包为空");
	            return result;
			}
			break;
		case "shutdown"://风险包
			filePath = resultFile.getResultObj().getShutFilePath();
			if(StringUtils.isBlank(filePath)){
				result.setResultCode(ResultCode.RESULT_FAILED);
	            result.setResultMsg("下载失败，检测结果风险包为空");
	            return result;
			}
			break;
		case "silent"://沉默包
			filePath = resultFile.getResultObj().getUnknownFilePath();
			if(StringUtils.isBlank(filePath)){
				result.setResultCode(ResultCode.RESULT_FAILED);
	            result.setResultMsg("下载失败，检测结果沉默包为空");
	            return result;
			}
			break;
		default://全部
			filePath = resultFile.getResultObj().getZipPath();
			if(StringUtils.isBlank(filePath)){
				result.setResultCode(ResultCode.RESULT_FAILED);
	            result.setResultMsg("下载失败，检测结果包为空");
	            return result;
			}
			break;
		}
		
		String[] temp = filePath.split("/");
		//下载文件名称
		String fileName = null;
		try {
			fileName = new String(temp[temp.length-1].getBytes("GBK"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e1) {
			logger.error("用户【" + mobile + "】下载文件异常，fileCode:"  + fileCode + ",id:" + id + "，错误信息：" + e1.getMessage());
			e1.printStackTrace();
			result.setResultCode(ResultCode.RESULT_FAILED);
	        result.setResultMsg("下载失败，服务器异常");
	        return result;
		}
		System.out.println("文件名称：" + fileName);
		File file = new File(loadfilePath + filePath);
		response.reset();
		response.setContentType("application/octet-stream");
		response.setCharacterEncoding("utf-8");
		response.setContentLength((int) file.length());
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
        	logger.error("用户【" + mobile + "】下载文件异常，fileCode:"  + fileCode + ",id:" + id + "，错误信息：" + e.getMessage());
            e.printStackTrace();
            result.setResultCode(ResultCode.RESULT_FAILED);
            result.setResultMsg("下载失败，服务器异常");
            return result;
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
            	logger.error("用户【" + mobile + "】下载文件异常，fileCode:"  + fileCode + ",id:" + id + "，错误信息：" + e.getMessage());
                e.printStackTrace();
                result.setResultCode(ResultCode.RESULT_FAILED);
	            result.setResultMsg("下载失败，服务器异常");
	            return result;
            }
        }

        result.setResultObj("success");
        logger.info("用户【" + mobile + "】下载文件成功，fileCode:"  + fileCode + ",id:" + id + "，");
		return result;
	}
}
