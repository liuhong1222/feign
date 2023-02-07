package cn.feignclient.credit_feign_web.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.feignclient.credit_feign_web.redis.RedisClient;
import cn.feignclient.credit_feign_web.service.CreditProviderService;
import cn.feignclient.credit_feign_web.service.UserAccountFeignService;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import main.java.cn.common.BackResult;
import main.java.cn.common.RedisKeys;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.CreUserDomain;
import main.java.cn.domain.CvsFilePathDomain;
import main.java.cn.domain.FileUploadDomain;
import main.java.cn.domain.RunTestDomian;
import main.java.cn.domain.UserAccountDomain;
import main.java.cn.domain.page.PageDomain;

@RestController
@RequestMapping("/web/credit")
public class CreditProviderController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(CreditProviderController.class);
	
	@Autowired
    private CreditProviderService creditProviderService;
	
	@Autowired
	private UserAccountFeignService userAccountFeignService;
	
	@Autowired
	private RedisClient redisClient;
	
	@RequestMapping(value = "/getTestProcessMobile", method = RequestMethod.POST)
    public BackResult<JSONObject> getTestProcessMobile(HttpServletRequest request, HttpServletResponse response,String userId,String fileCode,String mobile,String token){
    	BackResult<JSONObject> result = new BackResult<JSONObject>();
    	JSONObject json = new JSONObject();
    	
    	response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		
		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
			return result;
		}

		if (CommonUtils.isNotString(token)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("token不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("用户ID不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(fileCode)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("文件Code不能为空");
			return result;
		}
		
		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户已经注销登录无法进行操作");
			return result;
		}
		
		//文件检测是否出现异常
		String exceptionkey = RedisKeys.getInstance().getkhExceptionkey(userId, fileCode);		
        String exceptions = redisClinet.get(exceptionkey);
        // 出现异常终止检测
        if (StringUtils.isNotBlank(exceptions)  && exceptions.equals(ResultCode.RESULT_FAILED)) {
        	result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("获取进度失败，检测异常，请联系客服处理");
			return result;
        }
        //文件是否检测完成
      	String khTheRunkey = RedisKeys.getInstance().getkhTheRunkey(userId, fileCode);        
        String runStatus = redisClinet.get(khTheRunkey);
        //检测完成
        if (StringUtils.isNotBlank(runStatus)  && runStatus.equals(ResultCode.RESULT_FAILED)) {
        	result.setResultCode(ResultCode.TEST_SUCCESS);
			result.setResultMsg("检测已完成");
			return result;
        }
        //获取文件已经检测的条数
      	String testCountsKey = RedisKeys.getInstance().getkhSucceedTestCountkey(userId,fileCode);
        String testCounts = redisClinet.get(testCountsKey);
        
    	//获取检测完成该的号码
        JSONArray resultList = new JSONArray();
        try {
        	//获取已经检测成功的号码用于前端显示
        	String tempStr = redisClient.get(RedisKeys.getInstance().getMobileDisplayWebkey(userId, fileCode));
        	if(StringUtils.isBlank(tempStr)){
        		testCounts = "36";
        		//获取用户文件里默认的号码用户前端显示
        		String defaultStr = redisClient.get(RedisKeys.getInstance().getDefaultMobileDisplayWebkey(userId, fileCode));
        		if(StringUtils.isNotBlank(defaultStr)){
        			JSONArray list = JSONArray.parseArray(defaultStr);
        			//随机获取36个数
                	int[] intList = CommonUtils.randomCommon(0, list.size()-1, list.size()<36?list.size():36);   
                	for(int i: intList){
                		resultList.add(list.get(i));
                	}
        		}else{
        			//随机获取36个数
                	int[] firstThreeList = CommonUtils.randomCommon(130, 189, 36);  
                	//随机获取36个数
                	int[] lastFourList = CommonUtils.randomCommon(1000, 9999, 36); 
                	for(int j=0;j<36;j++){
                		JSONObject tempJson = new JSONObject();
                		tempJson.put("mobile", firstThreeList[j] + "****" + lastFourList[j]);
                		tempJson.put("color", j%6==0?"yellow":"blue");
                		resultList.add(tempJson);
                	}
        		}
        	}else{        		
        		JSONArray list = JSONArray.parseArray(tempStr);
            	//随机获取36个数
            	int[] intList = CommonUtils.randomCommon(0, list.size()-1, list.size()<36?list.size():36);   
            	for(int i: intList){
            		resultList.add(list.get(i));
            	}
        	}        	
        	
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户【" +userId+ "】获取文件：" + fileCode + "检测进度异常,异常信息为：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("获取进度失败，检测异常，请联系客服处理");
			return result;
		}
        
        if(StringUtils.isBlank(testCounts) || "0".equals(testCounts)){
        	testCounts = "36";
        }
        //需要检测的号码总条数
        String fileCountsKey = RedisKeys.getInstance().getkhSucceedClearingCountkey(userId, fileCode);
        String fileCounts = redisClinet.get(fileCountsKey);
        json.put("testCounts", testCounts);
        json.put("fileCounts", StringUtils.isBlank(fileCounts)?"0":fileCounts);
        json.put("mobileList", resultList);
        json.put("fileCode", fileCode);
        result.setResultObj(json);
    	return result;
    }
    
    @RequestMapping(value = "/theTest", method = RequestMethod.POST)
  	public synchronized BackResult<RunTestDomian> theTest(HttpServletResponse response,String mobile,String token, String code,
  			String source, String startLine, String type) {
      	
      	response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
  		response.setContentType("text/json;charset=UTF-8");

  		BackResult<RunTestDomian> result = new BackResult<RunTestDomian>();
  		
  		if (CommonUtils.isNotString(token)) {
  			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
  			result.setResultMsg("token不能为空");
  			return result;
  		}

  		if (CommonUtils.isNotString(mobile)) {
  			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
  			result.setResultMsg("手机号码不能为空");
  			return result;
  		}
  		
  		if (!isLogin(mobile, token)) {
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
  			logger.info("PC网站通手机号：" + mobile + "请求进行实号检测");
  		} else {
  			logger.info("PC网站手机号：" + mobile + "请求进行实号检测，查询检测结果");
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
  				if ((resultFile.getResultObj().getFileRows()) < 3001) {
  					result.setResultCode(ResultCode.RESULT_BUSINESS_EXCEPTIONS);
  					runTestDomian.setStatus("5"); // 1执行中 2执行结束 // 3执行异常4账户余额不足5检测的条数小于3000条
  					result.setResultObj(runTestDomian);
  					result.setResultMsg("检测条数必须大于3000条");
  					return result;
  				}

				if ((resultFile.getResultObj().getFileRows()) > 3000000) {
					result.setResultCode(ResultCode.RESULT_BUSINESS_EXCEPTIONS);
					runTestDomian.setStatus("6"); // 1执行中 2执行结束 // 3执行异常4账户余额不足5检测的条数小于3000条
					result.setResultObj(runTestDomian);
					result.setResultMsg("检测条数最大支持3000000条");
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
  				String freezeAccount = redisClient.get(wAccountKey);
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
  				redisClient.set(KhTestCountKey, String.valueOf(resultFile.getResultObj().getFileRows()), expire);
  				
  				result = creditProviderService.theTest(code, String.valueOf(user.getId()), source, mobile, startLine,
  	  					"1");
  				
  			} else {
  				result = creditProviderService.theTest(code, String.valueOf(user.getId()), source, mobile, startLine,
  	  					"2");
  			}

  		} catch (Exception e) {
  			e.printStackTrace();
  			logger.error("PC网站手机号：" + mobile + "请求进行实号检测，出现系统异常" + e.getMessage());
  			result.setResultCode(ResultCode.RESULT_FAILED);
  			result.setResultMsg("系统异常");
  		}

  		return result;
  	}    
    
    @RequestMapping(value = "/findByUserId", method = RequestMethod.GET)
    public BackResult<List<CvsFilePathDomain>> findByUserId(HttpServletRequest request, HttpServletResponse response,String userId,String mobile,String token){
    	BackResult<List<CvsFilePathDomain>> result = new BackResult<List<CvsFilePathDomain>>();
    	
    	response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		
		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
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
		
		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户已经注销登录无法进行操作");
			return result;
		}
		
		try {
			result = creditProviderService.findByUserId(userId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户手机号：" + mobile + "执行查询用户下载列表出现异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
    	
    	return result;
    }
    
    /**
	 * 分页获取实号检测下载列表
	 * 
	 * @param request
	 * @param pageNo
	 * @param pageSize
	 * @param mobile
	 * @return
	 */
	@RequestMapping(value = "/getPageByMobile", method = RequestMethod.POST)
	public BackResult<PageDomain<CvsFilePathDomain>> getPageByUserId(HttpServletResponse response,String userId,String mobile,String token, int pageNo,
			int pageSize,String startDate,String endDate) {
		
		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		logger.info("PC网站手机号：" + mobile + "请求分页获取历史检测记录");

		BackResult<PageDomain<CvsFilePathDomain>> result = new BackResult<PageDomain<CvsFilePathDomain>>();

		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
			return result;
		}

		if (CommonUtils.isNotString(token)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("token不能为空");
			return result;
		}
		
		if (!isLogin(mobile, token)) {
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
			logger.error("自助通手机号：" + mobile + "请求分页获取历史检测记录，出现系统异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
		return result;
	}
    
    @RequestMapping(value = "/deleteCvsByIds", method = RequestMethod.GET)
	public BackResult<Boolean> deleteCvsByIds(HttpServletRequest request, HttpServletResponse response,String ids,String userId,String token,String mobile) {
    	
    	response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		
		BackResult<Boolean> result = new BackResult<Boolean>();
		
		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
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
		
		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户已经注销登录无法进行操作");
			return result;
		}
		
		try {
			result = creditProviderService.deleteCvsByIds(ids, userId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户手机号：" + mobile + "执行查询用户下载列表出现异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
		
		return result;
    }
    
    @RequestMapping(value = "/getTxtZipByIds", method = RequestMethod.POST)
	public BackResult<String> getTxtZipByIds(HttpServletRequest request, HttpServletResponse response,String ids,String userId,String token,String mobile) {
    	
    	response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		
		BackResult<String> result = new BackResult<String>();
		
		if (CommonUtils.isNotString(mobile)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("手机号码不能为空");
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
		
		if (!isLogin(mobile, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户已经注销登录无法进行操作");
			return result;
		}
		
		try {
			result = creditProviderService.getTxtZipByIds(ids, userId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户手机号：" + mobile + "执行下载检测列表出现异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
		
		return result;
    }
}
