package cn.feignclient.credit_feign_web.client.controller;

import java.io.File;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import cn.feignclient.credit_feign_web.service.CreditProviderService;
import cn.feignclient.credit_feign_web.utils.DateUtils;
import cn.feignclient.credit_feign_web.utils.FileUtils;
import cn.feignclient.credit_feign_web.utils.UUIDTool;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.FileUploadDomain;

@RestController("FileClientController")
@RequestMapping("/web/client/file")
public class FileController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(FileController.class);

	@Value("${fielUrl}")
	private String fielUrl;

	@Autowired
	private CreditProviderService creditProviderService;

	// 文件上传相关代码
	@RequestMapping("/upload")
	@ResponseBody
	public BackResult<FileUploadDomain> upload(HttpServletRequest request, HttpServletResponse response,
			MultipartFile file, String userName) {

		BackResult<FileUploadDomain> result = new BackResult<FileUploadDomain>();
		String token = request.getHeader("token");
		if (!isLogin(userName, token)) {
			result.setResultCode(ResultCode.RESULT_SESSION_STALED);
			result.setResultMsg("上传失败，请先登录");
			return result;
		}

		if (null == file || file.isEmpty()) {
			logger.error("用户【" + userName + "】执行文件上传出现异常，文件不存在");
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("文件地址为空");
			return result;
		}
		
		if (file.getSize() > 10485760 * 2) {
			result.setResultCode(ResultCode.RESULT_PARAM_EXCEPTIONS);
			result.setResultMsg("文件不能超过20M");
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
		fileName = UUIDTool.getInstance().getUUID() + "_" + userName + suffixName;
		File dest = new File(filePath + fileName);
		
		// 检测是否存在目录
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}

		try {

			file.transferTo(dest);
			// 文件名存入数据库
			FileUploadDomain domain = new FileUploadDomain();
			domain.setUserId(this.findByUserName(userName).getId().toString());
			domain.setFileName(file.getOriginalFilename().substring(0,file.getOriginalFilename().lastIndexOf(".")));
			domain.setFileRows(FileUtils.getFileLinesNotNullRow(filePath + fileName));
			domain.setFileUploadUrl(filePath + fileName);
			BackResult<FileUploadDomain> resultFileUpload = creditProviderService.saveFileUpload(domain);
			if (!resultFileUpload.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
				result = new BackResult<>(resultFileUpload.getResultCode(), resultFileUpload.getResultMsg());
			}

			result.setResultObj(resultFileUpload.getResultObj());
			result.setResultMsg("上传成功");
			
			logger.info("用户" + "【" + userName + "】执行文件上传(" + fileName + ")成功!");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户【" + userName + "】执行文件上传出现系统异常：" + e.getMessage());
			result.setResultCode(ResultCode.RESULT_FAILED);
			result.setResultMsg("系统异常");
		}

		return result;
	}
}
