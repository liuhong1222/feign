package cn.feignclient.credit_feign_web.client.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import cn.feignclient.credit_feign_web.dao.TestHistory;
import cn.feignclient.credit_feign_web.execl.ExportExcel;
import cn.feignclient.credit_feign_web.service.CreditProviderService;
import cn.feignclient.credit_feign_web.service.ReportService;
import cn.feignclient.credit_feign_web.utils.CommonUtils;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.CvsFilePathExport;

@RestController("ReportClientController")
@RequestMapping("/web/client/report")
public class ReportController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(ReportController.class);

	@Autowired
	private ReportService reportService;
	
	@Autowired
    private CreditProviderService creditProviderService;
	
	@Value("${loadfilePath}")
	private String loadfilePath;
	
	/**
	 * 在线检测折线图
	 * 
	 * @param request
	 * @param response
	 * @param paramJson
	 * @return
	 */
	@RequestMapping(value = "/getTestHistoryReport", method = RequestMethod.POST)
	public BackResult<List<Map<String,String>>> getTestHistoryReport(HttpServletRequest request,
			HttpServletResponse response, String userId,String month,String userName) {
		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		
		//返回结果
		BackResult<List<Map<String,String>>> result = new BackResult<List<Map<String,String>>>();
		String token = request.getHeader("token");
		if (!isLogin(userName, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户校验失败");
			return result;
		}
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("用户id不能为空不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(month)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("查询月份不能为空");
			return result;
		}
		
		result = reportService.getTestHistoryReport(userId, month);
		return result;
	}	
	
	/**
	 * 在线检测折线图导出
	 * 
	 * @param request
	 * @param response
	 * @param paramJson
	 * @return
	 */
	@RequestMapping(value = "/testHistoryReportExport")
	@ResponseBody
	public BackResult<String> testHistoryReportExport(HttpServletRequest request,
			HttpServletResponse response, String userId,String month,String userName,String token) {
		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		
		//返回结果
		BackResult<String> result = new BackResult<String>();	
		if (!isLogin(userName, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户校验失败");
			return result;
		}
		
		if (CommonUtils.isNotString(userId)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("用户id不能为空不能为空");
			return result;
		}
		
		if (CommonUtils.isNotString(month)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("查询月份不能为空");
			return result;
		}
		
		BackResult<List<Map<String,String>>> tempResult = reportService.getTestHistoryReport(userId, month);
		if(!ResultCode.RESULT_SUCCEED.equals(tempResult.getResultCode())){
			result.setResultCode(tempResult.getResultCode());
			result.setResultMsg(tempResult.getResultMsg());
			return result;
		}
		try {
			ExportExcel<TestHistory> ex = new ExportExcel<TestHistory>();
			String[] headers = {"日期","实号条数", "空号条数", "风险号条数","沉默号条数", "总条数"};
			String path0 = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());			 
			// 转码防止乱码
			response.setContentType("octets/stream");
			OutputStream out = response.getOutputStream();			
			path0 = "theTest" + month + "-" + path0;	
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(path0.getBytes("gb2312"), "ISO8859-1")+ ".xls");
			ex.exportExcel(headers, getBeanByMap(tempResult.getResultObj()), out, "yyyy-MM-dd HH:mm:ss");
			logger.info("用户【" + userId + "】导出在线检测折线图数据成功，月份：" + month + ",ip: " + super.getIpAddr(request) + "");
			out.close();			
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("用户【" + userId + "】导出在线检测折线图数据异常，月份：" + month + ",ip: " + super.getIpAddr(request) + "");
		}	
		
		return result;
	}
	
	@RequestMapping(value = "/deleteCvsByTime", method = RequestMethod.GET)
	public BackResult<Boolean> deleteCvsByTime(HttpServletRequest request, HttpServletResponse response,String userId,String userName
			,String ids) {
    	
    	response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		
		BackResult<Boolean> result = new BackResult<Boolean>();
		String token = request.getHeader("token");
		if (CommonUtils.isNotString(userName)) {
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
		
		if (CommonUtils.isNotString(ids)) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("至少选择一条记录");
			return result;
		}
		
		if (!isLogin(userName, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户已经注销登录无法进行操作");
			return result;
		}
		
		try {
			result = creditProviderService.deleteCvsByTime(userId,ids);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户帐号：" + userName + "执行批量删除检测记录异常，ids:" + ids + ",ip:" + super.getIpAddr(request) + ",异常信息为：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}
		
		logger.info("用户帐号：" + userName + "执行批量删除检测记录成功，ids:" + ids + ",ip:" + super.getIpAddr(request));
		return result;
    }
	
	//文件下载
	@RequestMapping("/batchDownloadFile")
	@ResponseBody
	public BackResult<String> batchDownloadFile(HttpServletRequest request, HttpServletResponse response,String userId,String userName
			,String ids,String token) {
		response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		
		BackResult<String> result = new BackResult<String>();
		if (CommonUtils.isNotString(userName)) {
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
		
		if (!isLogin(userName, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户已经注销登录无法进行操作");
			return result;
		}
		
		if(StringUtils.isBlank(ids)){
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("至少需要选择一条记录");
			return result;
		}
		
		BackResult<String> fileObj = reportService.batchDownloadFile(userId, ids);
		if(!ResultCode.RESULT_SUCCEED.equals(fileObj.getResultCode())){
			result.setResultCode(fileObj.getResultCode());
			result.setResultMsg(fileObj.getResultMsg());
			return result;
		}
		
		String[] temp = fileObj.getResultObj().split("/");
		//下载文件名称
		String fileName = null;
		try {
			fileName = new String(temp[temp.length-1].getBytes("GBK"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e1) {
			logger.error("用户【" + userName + "】批量下载文件异常，ids:"  + ids +  ",ip: " + super.getIpAddr(request) + "，错误信息：" + e1.getMessage());
			e1.printStackTrace();
			result.setResultCode(ResultCode.RESULT_FAILED);
	        result.setResultMsg("下载失败，服务器异常");
	        return result;
		}
        
		System.out.println("文件名称：" + fileName);
		File file = new File(fileObj.getResultObj());
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
        	logger.error("用户【" + userName + "】批量下载文件异常，ids:"  + ids +  ",ip: " + super.getIpAddr(request) + "，错误信息：" + e.getMessage());
            e.printStackTrace();
            result.setResultCode(ResultCode.RESULT_FAILED);
            result.setResultMsg("下载失败，服务器异常");
            return result;
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
            	logger.error("用户【" + userName + "】批量下载文件异常，ids:"  + ids +  ",ip: " + super.getIpAddr(request) + "，错误信息：" + e.getMessage());
                e.printStackTrace();
                result.setResultCode(ResultCode.RESULT_FAILED);
	            result.setResultMsg("下载失败，服务器异常");
	            return result;
            }
        }

        result.setResultObj("success");
        logger.info("用户【" + userName + "】批量下载文件成功，ids:"  + ids +  ",ip: " + super.getIpAddr(request) +"，");
		return result;
	}
	
	@RequestMapping(value = "/getHistoryTestData")
	@ResponseBody
	public BackResult<Boolean> getHistoryTestData(HttpServletRequest request, HttpServletResponse response,String userId,String userName
			,String startDate,String endDate,String token) {
    	
    	response.setHeader("Access-Control-Allow-Origin", "*"); // 有效，前端可以访问
		response.setContentType("text/json;charset=UTF-8");
		
		BackResult<Boolean> result = new BackResult<Boolean>();
		if (CommonUtils.isNotString(userName)) {
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
		
		if (!isLogin(userName, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("用户已经注销登录无法进行操作");
			return result;
		}
		
		BackResult<List<CvsFilePathExport>> tempResult = reportService.cvsFilePathExport(userId, startDate, endDate);
		if(!ResultCode.RESULT_SUCCEED.equals(tempResult.getResultCode())){
			result.setResultCode(tempResult.getResultCode());
			result.setResultMsg(tempResult.getResultMsg());
			return result;
		}
		try {
			ExportExcel<CvsFilePathExport> ex = new ExportExcel<CvsFilePathExport>();
			String[] headers = {"名称","大小", "日期", "实号包(条)","沉默包(条)", "空号包(条)","风险包(条)", "总条数(条)"};
			String path0 = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());			 
			// 转码防止乱码
			response.setContentType("octets/stream");
			OutputStream out = response.getOutputStream();			
			path0 = "historyTest-" + path0;	
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(path0.getBytes("gb2312"), "ISO8859-1")+ ".xls");
			ex.exportExcel(headers, tempResult.getResultObj(), out, "yyyy-MM-dd HH:mm:ss");
			logger.info("用户【" + userId + "】导出历史检测记录成功，startDate：" + startDate + ",endDate: " + endDate +  ",ip: " + super.getIpAddr(request) + "");
			out.close();			
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("用户【" + userId + "】导出历史检测记录异常，startDate：" + startDate +  ",endDate: " + endDate + ",ip: " + super.getIpAddr(request) + "");
		}	
		
		return result;
    }
	
	private List<TestHistory> getBeanByMap(List<Map<String,String>> list){
		if(list == null || list.size() <= 0){
			return null;
		}
		List<TestHistory> result = new ArrayList<TestHistory>();
		for(Map<String,String> param: list){
			TestHistory th = new TestHistory();
			th.setDate(param.get("date"));
			th.setReal(param.get("real"));
			th.setEmpty(param.get("empty"));
			th.setShut(param.get("shut"));
			th.setSilence(param.get("silence"));
			th.setTotal(param.get("total"));
			result.add(th);
		}
		return result;		
	}
}
