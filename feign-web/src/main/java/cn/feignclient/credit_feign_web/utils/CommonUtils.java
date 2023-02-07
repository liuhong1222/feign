package cn.feignclient.credit_feign_web.utils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class CommonUtils {

	/**
	 * 判断集合是否为空
	 * @param collection
	 * @return
	 */
	public static Boolean isNotEmpty(Collection<?> collection) {
		return (null == collection || collection.size() <= 0);
	}
	
	/**
	 * 判断字符是否为空
	 * @param str
	 * @return
	 */
	public static Boolean isNotString(String str){
		return (null == str || "".equals(str));
	}
	
	public static Boolean isNotIngeter(Integer str){
		return (null == str || "".equals(str));
	}
	
	public static Boolean isNotBigDecimal(BigDecimal bigDecimal){
		return (null == bigDecimal || "".equals(bigDecimal));
	}
	
	/**
	 * 验证是否为11位有效数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (str.length() != 11) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/** 
     * 随机指定范围内N个不重复的数 
     * 利用HashSet的特征，只能存放不同的值 
     * @param min 指定范围最小值 
     * @param max 指定范围最大值 
     * @param n 随机数个数 
     * @param HashSet<Integer> set 随机数结果集 
     */  
	   public static void randomSet(int min, int max, int n, HashSet<Integer> set) {  
	       if (n > (max - min + 1) || max < min) {  
	           return;  
	       }  
	       for (int i = 0; i < n; i++) {  
	           // 调用Math.random()方法  
	           int num = (int) (Math.random() * (max - min)) + min;  
	           set.add(num);// 将不同的数存入HashSet中  
	       }  
	       int setSize = set.size();  
	       // 如果存入的数小于指定生成的个数，则调用递归再生成剩余个数的随机数，如此循环，直到达到指定大小  
	       if (setSize < n) {  
	        randomSet(min, max, n - setSize, set);// 递归  
	       }  
	   }  
	   
	   /** 
	    * 随机指定范围内N个不重复的数 
	    * 最简单最基本的方法 
	    * @param min 指定范围最小值 
	    * @param max 指定范围最大值 
	    * @param n 随机数个数 
	    */  
	   public static int[] randomCommon(int min, int max, int n){  
	       if (n > (max - min + 1) || max < min) {  
	              return null;  
	          }  
//	       int[] result = new int[n];  
//	       int count = 0;  
//	       while(count < n) {  
//	           int num = (int) (Math.random() * (max - min)) + min;  
//	           boolean flag = true;  
//	           for (int j = 0; j < n; j++) {  
//	               if(num == result[j]){  
//	                   flag = false;  
//	                   break;  
//	               }  
//	           }  
//	           if(flag){  
//	               result[count] = num;  
//	               count++;  
//	           }  
//	       }  
	       
	       int[] result = new int[n];
	        Random random = new Random();
	        HashSet<Integer> hashset = new HashSet();
	        for (;;) {
	            int randomNum = random.nextInt(max - min + 1) + min;
	            hashset.add(randomNum);
	            if (hashset.size() == n) break;
	        }
	        int i = 0;
	        for (Integer integer : hashset) {
	            result[i] = integer;
	            i++;
	        }
	       
	       return result;  
	   }
	
	public static void main(String[] args) {
		int[] result = CommonUtils.randomCommon(1,72,36);
		for(int s : result){
			System.out.println(s);
		}
		
//		System.out.println(CommonUtils.isNotString(""));
	}
}
