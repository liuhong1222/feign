package cn.feignclient.credit_feign_web.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.feignclient.credit_feign_web.domain.MultipartFileParam;
import cn.feignclient.credit_feign_web.domain.UploadFileStatusParam;
import cn.feignclient.credit_feign_web.service.ChunkService;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/web/file")
public class ChunkController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(ChunkController.class);
	
	@Autowired
	private ChunkService chunkService;
	
	@RequestMapping("/chunkUpload")
    public BackResult chunkUpload(MultipartFileParam param, HttpServletRequest request) {
		if (!isLogin(param.getMobile(), param.getToken())) {
			return BackResult.error("用户校验失败");
		}
		
		if(StringUtils.isBlank(param.getIdentifier())) {
			return BackResult.error("md5不能为空");
		}
		
		if(param.getFile() == null) {
			return BackResult.error("未选中文件");
		}
		
		if(param.getTotalChunks() == null) {
			return BackResult.error("分片总数量不能为空");
		}
		
		if(param.getChunkNumber() == null) {
			return BackResult.error("当前文件分片不能为空");
		}
		
		if(param.getChunkSize() == null) {
			return BackResult.error("当前分片大小不能为空");
		}
		
		if(StringUtils.isBlank(param.getFileRealName())) {
			return BackResult.error("文件名称不能为空");
		}
		
	    return chunkService.upload(param.getFile(), param.getIdentifier(), param.getFileRealName(), 
				param.getTotalChunks(), param.getChunkNumber(),param.getChunkSize(),this.findByMobile(param.getMobile()).getId().toString(),param.getMobile());
	}
	
	@RequestMapping("/uploadStatus")
    public BackResult uploadStatus(UploadFileStatusParam param, HttpServletRequest request) {
		if (!isLogin(param.getMobile(), param.getToken())) {
			return BackResult.error("用户校验失败");
		}
		
		if(StringUtils.isBlank(param.getMd5())) {
			return BackResult.error("文件md5不能为空");
		}
		
		if(param.getChunks() == null) {
			return BackResult.error("文件分片总数不能为空");
		}
		
		if(StringUtils.isBlank(param.getFileName())) {
			return BackResult.error("文件名称不能为空");
		}
		
	    return chunkService.uploadStatus(param,this.findByMobile(param.getMobile()).getId().toString());
	}
}
