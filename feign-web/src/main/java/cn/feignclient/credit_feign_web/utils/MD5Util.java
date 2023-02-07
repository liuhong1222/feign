package cn.feignclient.credit_feign_web.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	// 全局数组
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f","g","h","j","?","~" };

    private static MD5Util mD5Util;
	
	public static MD5Util getInstance() {  
        if (mD5Util == null) {    
            synchronized (MD5Util.class) {    
               if (mD5Util == null) {    
            	   mD5Util = new MD5Util();   
               }    
            }    
        }    
        return mD5Util;   
    }  

    // 返回形式为数字跟字符串
    private String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    // 转换字节数组为16进制字串
    private String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }

    public String getMD5Code(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }
    
    public static void main(String[] args) {
    	// e10adc3949ba59abbe56e057f20f883e
    	
        System.out.println(MD5Util.getInstance().getMD5Code("000000"));
    }
}
