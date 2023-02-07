package cn.feignclient.credit_feign_web.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import cn.feignclient.credit_feign_web.domain.UploadFileStatusParam;
import cn.feignclient.credit_feign_web.redis.DistributedLockWrapper;
import cn.feignclient.credit_feign_web.utils.DateUtils;
import cn.feignclient.credit_feign_web.utils.FileUtil;
import main.java.cn.common.BackResult;
import main.java.cn.common.ResultCode;
import main.java.cn.domain.FileUploadDomain;
import main.java.cn.domain.UserAccountDomain;
import redis.clients.jedis.JedisPool;

@Service
public class ChunkService {
	
	private final static Logger log = LoggerFactory.getLogger(ChunkService.class);
	
	@Value("${fielUrl}")
	private String fielUrl;
	
	@Autowired
	private JedisPool jedisPool;
	
	@Autowired
	private CreditProviderService creditProviderService;
	
	@Autowired
	private UserAccountFeignService userAccountFeignService;
	
	private static ExecutorService executorService = Executors.newFixedThreadPool(6);
	
	public BackResult upload(MultipartFile file, String md5, String name, int chunks, int chunk,Long chunkSize,String userId,String mobile) {
		//获取用户冻结信息
		BackResult<UserAccountDomain> userAccountDomain = userAccountFeignService.findbyMobile(mobile);
		if(!ResultCode.RESULT_SUCCEED.equals(userAccountDomain.getResultCode())){
			return BackResult.error(userAccountDomain.getResultCode(), userAccountDomain.getResultMsg());
		}
		
		if(userAccountDomain.getResultObj().getIsFrozen() != null
				&& userAccountDomain.getResultObj().getIsFrozen()==1){
			return BackResult.error(ResultCode.RESULT_FAILED, "上传文件失败，您的帐号已被冻结，请联系客服");
		}
		
		uploadFileChunk(userId,file, md5, name, chunks, chunk,chunkSize);
		return BackResult.succeed();
	}
	
	public BackResult uploadStatus(UploadFileStatusParam param,String userId) {
		try {
			// 检查是否已经全部上传完成
			String filePath = unionFileChunks(userId, param.getMd5(), param.getFileName(), param.getChunks());
			if(StringUtils.isBlank(filePath)) {
				return BackResult.error(ResultCode.FILE_UPLOADING,"正在上传中");
			}
			
			// 检测上传文件数量
			int lineNum = getLineNum(userId, param.getMd5(), param.getFileName(), filePath);
			Integer minLineNum = 3001;
			if(lineNum < minLineNum) {
				return BackResult.error(String.format("请上传不少于%s个号码的文件", minLineNum));
			}
			
			if(lineNum > 3000000) {
				return BackResult.error(String.format("请上传不多于%s个号码的文件", 3000000));
			}
			
			FileUploadDomain domain = new FileUploadDomain();
			domain.setUserId(userId);
			domain.setFileName(param.getFileName().substring(0,param.getFileName().lastIndexOf(".")));
			domain.setFileRows(lineNum);
			domain.setFileUploadUrl(filePath.replace(".xls", ".txt"));
			BackResult<FileUploadDomain> resultFileUpload = creditProviderService.saveFileUpload(domain);
			if (!resultFileUpload.getResultCode().equals(ResultCode.RESULT_SUCCEED)) {
				log.info("{}， 文件上传失败，param:{},response:{}",userId,JSON.toJSONString(param),JSON.toJSONString(resultFileUpload));
				return new BackResult<>(resultFileUpload.getResultCode(), resultFileUpload.getResultMsg());
			}

			BackResult<FileUploadDomain> result = new BackResult<FileUploadDomain>();
			result.setResultObj(resultFileUpload.getResultObj());
			result.setResultMsg("上传成功");
			
			log.info("{}， 文件上传成功，param:{}",userId,JSON.toJSONString(param));
			return result;
		} catch (Exception e) {
			log.error("{}，文件上传异常，param:{},info:",userId,JSON.toJSONString(param),e);
			return BackResult.error("系统异常，请重新上传");
		}
	}
	
	private String unionFileChunks(String userId,String md5,String fileRealName,Integer chunks) {
		//1.设置redis锁
        DistributedLockWrapper lock = new DistributedLockWrapper(jedisPool, String.format("file:uulk:%s:%s", 
        		userId,md5), 5* 1000L, 1000 * 5);
		try {
	        if (StringUtils.isBlank(lock.getIdentifier())) {
	        	log.error("{}, 分片文件正在合并中，md5:{},fileRealName:{}",userId,md5,fileRealName);
	        	return null;
	        }
	        
	        String foldName = fielUrl + "temp/" + DateUtils.getDate() + "/" + userId + "/";
	        // 获取分片文件名称
	        String fileName = getFileName(userId, md5, fileRealName);
	        File destFile = new File(foldName + fileName);
	        // 检查文件是否已经存在，防止前端忽略第一次请求的情况
	        if (destFile.exists()) {
	            lock.releaseLock();
	            return foldName + fileName;
	        }
	        
	        // 检查分片文件是否都存在
	        for (int chunk = 0; chunk < chunks; chunk++) {
	            String chunkFileName = getChunkFileName(userId, md5, fileRealName, chunks, chunk);
	            if (!new File(foldName + chunkFileName).exists()) {
	                lock.releaseLock();
	                return null;
	            }
	        }
	        
	        FileOutputStream fileOutputStream = new FileOutputStream(destFile);
            for (int chunk = 0; chunk < chunks; chunk++) {
                String chunkFileName = getChunkFileName(userId, md5, fileRealName, chunks, chunk);
                File chunkFile = new File(foldName + chunkFileName);

                FileInputStream fileInputStream = new FileInputStream(chunkFile);
                FileChannel fileChannel = fileInputStream.getChannel();
                ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
                while (fileChannel.read(byteBuffer) > 0) {
                    fileOutputStream.write(byteBuffer.array());
                }
                
                fileChannel.close();
                fileInputStream.close();
                chunkFile.delete();
            }
            
            fileOutputStream.flush();
            fileOutputStream.close();
            
            lock.releaseLock();
            log.info("{}, 分片上传文件合并完成，md5:{},fileRealName:{},filePath:{}",userId,md5,fileRealName,foldName + fileName);
            return foldName + fileName;
		} catch (Exception e) {
			log.error("{}, 分片上传文件合并异常，md5:{},fileRealName:{},info:",userId,md5,fileRealName,e);
			lock.releaseLock();
			return null;
		}
	}
	
	 /**
     * 获取上传文件条数
     *
     * @param accountName       账号名称
     * @param productUploadEnum 上传文件枚举
     * @param md5               上传文件MD5
     * @return 上传文件条数
     */
    private Integer getLineNum(String userId, String md5, String fileName,String filePath) throws RuntimeException {
        int lineNum = 0;
        String ext = fileName.substring(fileName.lastIndexOf("."));
        switch (ext) {
            case ".txt":
            	lineNum = FileUtil.getFileLineNum(filePath);
            	break;
            default:
                log.error("{}，不支持的文件格式，fileName:{},filePath:{}",userId,fileName,filePath);
        }
        
        return lineNum;
    }

	/**
     * 上传文件分片
     *
     * @param productUploadEnum 上传产品枚举
     * @param accountName       账号名称
     * @param file              上传文件信息
     * @param md5               上传文件MD5
     * @param name              上传文件名称
     * @param chunks            文件分片数量
     * @param chunk             当前文件分片
     */
    private void uploadFileChunk(String userId,MultipartFile file, String md5, String name, int chunks, int chunk,Long chunkSize) {
        log.info("uploadFileChunk - [开始上传] - [账号:{},md5:{},name:{},chunks:{},chunk:{}]", userId, md5,name, chunks, chunk);
        String foldName = fielUrl + "temp/" + DateUtils.getDate() + "/" + userId + "/";
        // 获取分片文件名称
        String chunkFileName = getChunkFileName(userId, md5, name, chunks, chunk);
        File chunkFile = new File(foldName + chunkFileName);

        // 分片文件如果已存在，则不再上传
        if (chunkFile.exists()) {
            log.info("uploadFileChunk - [分片已存在，停止上传] - [账号:{},md5:{},name:{},chunks:{},chunk:{}]", userId, md5, name,chunks, chunk);
            return;
        }

        // 获取临时文件名称
        String chunkFileNameOnUpload = getChunkFileNameOnUpload(userId, md5, name, chunks, chunk);
        String chunkFilePathOnUpload = foldName + chunkFileNameOnUpload;
        File chunkFileOnUpload = new File(chunkFilePathOnUpload);
        try {
            File parent = chunkFileOnUpload.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(chunkFileOnUpload));
            bos.write(file.getBytes());
            bos.close();
            log.info("uploadFileChunk - [上传临时文件完成] - [账号:{},md5:{},name:{},chunks:{},chunk:{}]",userId, md5,name, chunks, chunk);
            // 文件名称修改为完成时的文件名称
            chunkFileOnUpload.renameTo(chunkFile);
            log.info("uploadFileChunk - [上传完成] - [账号:{},md5:{},name:{},chunks:{},chunk:{}]", userId, md5,name, chunks, chunk);
        } catch (IOException e) {
        	chunkFileOnUpload.delete();
            log.error("uploadFileChunk - [上传分片文件失败] - [账号:{},md5:{},name:{},chunks:{},chunk:{},文件地址:{}] - info:",
            		userId, md5,name, chunks, chunk, chunkFilePathOnUpload, e);
        }
    }
    
    /**
     * 获取上传文件分片名称（正在上传）
     */
    private String getChunkFileNameOnUpload(String customerId, String md5, String name, int chunks, int chunk) {
        return String.format("%s.uploading", getChunkFileName(customerId, md5, name, chunks, chunk));
    }
    
    /**
     * 获取上传文件分片名称
     */
    private String getChunkFileName(String customerId, String md5, String name, int chunks, int chunk) {
        return String.format("%s.%s_%s", getFileName(customerId, md5, name), chunks, chunk);
    }
    
    /**
     * 获取上传文件名称
     */
    private String getFileName(String customerId, String md5, String name) {
        return String.format("/%s_%s.%s", md5.substring(8, 24), customerId, name.substring(name.lastIndexOf(".") + 1));
    }
}
