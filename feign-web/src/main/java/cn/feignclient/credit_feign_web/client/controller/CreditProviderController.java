package cn.feignclient.credit_feign_web.client.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.feignclient.credit_feign_web.client.service.UserFeignClientService;
import cn.feignclient.credit_feign_web.redis.RedisClient;
import cn.feignclient.credit_feign_web.service.CreditProviderService;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import main.java.cn.common.BackResult;
import main.java.cn.common.RedisKeys;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.CreUserClientDomain;
import main.java.cn.domain.CvsFilePathDomain;
import main.java.cn.domain.FileUploadDomain;
import main.java.cn.domain.RunTestDomian;
import main.java.cn.domain.page.PageDomain;

@RestController("CreditProviderClientController")
@RequestMapping("/web/client/credit")
public class CreditProviderController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(CreditProviderController.class);
	
	@Autowired
    private CreditProviderService creditProviderService;
	
	@Autowired
	private RedisClient redisClient;
	
	@Autowired
	private UserFeignClientService userFeignService;
	
    @RequestMapping(value = "/theTest", method = RequestMethod.POST)
  	public synchronized BackResult<RunTestDomian> theTest(HttpServletRequest request,HttpServletResponse response,String userName, String code,
  			String source, String startLine, String type) {
      	
      	response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
  		response.setContentType("text/json;charset=UTF-8");

  		BackResult<RunTestDomian> result = new BackResult<RunTestDomian>();
  		String token = request.getHeader("token");
  		if (CommonUtils.isNotString(token)) {
  			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
  			result.setResultMsg("token不能为空");
  			return result;
  		}

  		if (CommonUtils.isNotString(userName)) {
  			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
  			result.setResultMsg("登录帐号不能为空");
  			return result;
  		}
  		
  		if (!isLogin(userName, token)) {
  			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
  			result.setResultMsg("用户已经注销登录无法进行操作");
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
  			logger.info("客户端帐号：" + userName + "请求进行实号检测");
  		} else {
  			logger.info("客户端帐号：" + userName + "请求进行实号检测，查询检测结果");
  		}

  		try {
  			CreUserClientDomain user = findByUserName(userName);
  			if (null == user) {
  				result.setResultCode(ResultCode.RESULT_SESSION_STALED);
  				result.setResultMsg("用户校验失败，系统不存在该手机号码的用户");
  				return result;
  			}
  			
  			if (type.equals("1")) {
  				RunTestDomian runTestDomian = new RunTestDomian();  				
  				BackResult<FileUploadDomain> resultFile = creditProviderService.findFileUploadById(code);  				
  				if (!resultFile.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
  					return new BackResult<RunTestDomian>(ResultCode.RESULT_DATA_EXCEPTIONS, "文件检测异常，没有检测到可以检测的文件！");
  				}
  				
  				if ((resultFile.getResultObj().getFileRows()) < 3001) {
  					result.setResultCode(ResultCode.RESULT_BUSINESS_EXCEPTIONS);
  					runTestDomian.setStatus("5"); 
  					result.setResultObj(runTestDomian);
  					result.setResultMsg("检测条数必须大于3000条");
  					return result;
  				}

				if ((resultFile.getResultObj().getFileRows()) > 1500000) {
					result.setResultCode(ResultCode.RESULT_BUSINESS_EXCEPTIONS);
					runTestDomian.setStatus("6");
					result.setResultObj(runTestDomian);
					result.setResultMsg("检测条数最大支持1500000条");
					return result;
				}

  				BackResult<Map<String,Object>> resultUserAccount = userFeignService.findCreUserInfobyUserName(userName);  				
  				if (!resultUserAccount.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
  					result.setResultCode(ResultCode.RESULT_BUSINESS_EXCEPTIONS);
  					runTestDomian.setStatus("3");
  					result.setResultObj(runTestDomian);
  					result.setResultMsg("检测用户余额信息失败");
  					return result;
  				}
  				
  				String wAccountKey = RedisKeys.getInstance().getAcountKey(user.getId().toString()); // 冻结的上传文件空号账户条数
  				String freezeAccount = redisClient.get(wAccountKey);
  				int account = 0;
  				if (!CommonUtils.isNotString(freezeAccount)) {
  					account = account + Integer.valueOf(freezeAccount);
  				}
  				
  				if ((Integer.parseInt(resultUserAccount.getResultObj().get("account").toString()) - account) < resultFile.getResultObj().getFileRows()) {
  					result.setResultCode(ResultCode.RESULT_BUSINESS_EXCEPTIONS);
  					runTestDomian.setStatus("4");
  					result.setResultObj(runTestDomian);
  					result.setResultMsg("账户余额不足");
  					return result;
  				}
  				
  				String KhTestCountKey = RedisKeys.getInstance().getKhTestCountKey(String.valueOf(user.getId()),code);
  				int expire = 4 * 60 * 60 * 1000;
  				redisClient.set(KhTestCountKey, String.valueOf(resultFile.getResultObj().getFileRows()), expire);  				
  				result = creditProviderService.theTest(code, String.valueOf(user.getId()), source, userName, startLine,
  	  					"1");  				
  			} else {
  				result = creditProviderService.theTest(code, String.valueOf(user.getId()), source, userName, startLine,
  	  					"2");
  			}

  		} catch (Exception e) {
  			e.printStackTrace();
  			logger.error("客户端帐号：" + userName + "请求进行实号检测，出现系统异常" + e.getMessage());
  			result.setResultCode(ResultCode.RESULT_FAILED);
  			result.setResultMsg("系统异常");
  		}

  		return result;
  	}    
    
	@RequestMapping(value = "/getPageByUserId", method = RequestMethod.POST)
	public BackResult<PageDomain<CvsFilePathDomain>> getPageByUserId(HttpServletRequest request,HttpServletResponse response,String userId,String userName, int pageNo,
			int pageSize,String startDate,String endDate) {
		
		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		logger.info("客户端帐号：：" + userName + "请求分页获取历史检测记录");

		BackResult<PageDomain<CvsFilePathDomain>> result = new BackResult<PageDomain<CvsFilePathDomain>>();
		String token = request.getHeader("token");
		if (CommonUtils.isNotString(userName)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("登录帐号不能为空");
			return result;
		}

		if (CommonUtils.isNotString(token)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("token不能为空");
			return result;
		}
		
		if (!isLogin(userName, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户已经注销登录无法进行操作");
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
			if(pageNo<1)pageNo=1;
			if(pageSize<1)pageSize=10;
			if(StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)){
				result = creditProviderService.getPageByUserId(pageNo, pageSize, String.valueOf(userId));
			}else{
				if(StringUtils.isBlank(startDate) &&  StringUtils.isNotBlank(endDate)){
					result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
					result.setResultMsg("开始日期不能为空");
					return result;
				}
				
				if(StringUtils.isNotBlank(startDate) &&  StringUtils.isBlank(endDate)){
					result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
					result.setResultMsg("结束日期不能为空");
					return result;
				}
				
				result = creditProviderService.getPageByUserIdNew(pageNo, pageSize, userId, startDate, endDate);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("客户端帐号：" + userName + "请求分页获取历史检测记录，出现系统异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
		return result;
	}
    
    @RequestMapping(value = "/deleteCvsByIds", method = RequestMethod.GET)
	public BackResult<Boolean> deleteCvsByIds(HttpServletRequest request, HttpServletResponse response,String ids,String userId,String userName) {
    	
    	response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		
		BackResult<Boolean> result = new BackResult<Boolean>();
		String token = request.getHeader("token");
		if (CommonUtils.isNotString(userName)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("登录帐号不能为空");
			return result;
		}

		if (CommonUtils.isNotString(token)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("token不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("用户ID 不能为空");
			return result;
		}
		
		if (!isLogin(userName, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户已经注销登录无法进行操作");
			return result;
		}
		
		try {
			result = creditProviderService.deleteCvsByIds(ids, userId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户帐号：" + userName + "执行删除检测结果记录出现异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
		
		return result;
    }
    
    @RequestMapping(value = "/getTxtZipByIds", method = RequestMethod.POST)
	public BackResult<String> getTxtZipByIds(HttpServletRequest request, HttpServletResponse response,String ids,String userId,String userName) {
    	
    	response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		
		BackResult<String> result = new BackResult<String>();
		String token = request.getHeader("token");
		if (CommonUtils.isNotString(userName)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("登录帐号不能为空");
			return result;
		}

		if (CommonUtils.isNotString(token)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("token不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("用户ID 不能为空");
			return result;
		}
		
		if (!isLogin(userName, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户已经注销登录无法进行操作");
			return result;
		}
		
		try {
			result = creditProviderService.getTxtZipByIds(ids, userId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户帐号：" + userName + "执行查询检测结果包下载地址出现异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
		
		return result;
    }
}
