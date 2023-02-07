package cn.feignclient.credit_feign_web.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.cn.common.BackResult;
import main.java.cn.domain.CvsFilePathDomain;
import main.java.cn.domain.CvsFilePathExport;

/**
 * 报表
 *
 */
@FeignClient(value = "credit-provider-service")
public interface ReportService{
	
	@RequestMapping(value = "/report/getTestHistoryReport", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<List<Map<String,String>>> getTestHistoryReport(@RequestParam("userId") String userId,@RequestParam("month") String month);
	
	@RequestMapping(value = "/report/cvsFilePathExport", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<List<CvsFilePathExport>> cvsFilePathExport(@RequestParam("userId") String userId,@RequestParam("startDate") String startDate,@RequestParam("endDate") String endDate);
	
	@RequestMapping(value = "/report/batchDownloadFile", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	BackResult<String> batchDownloadFile(@RequestParam("userId") String userId,@RequestParam("ids") String ids);
}
