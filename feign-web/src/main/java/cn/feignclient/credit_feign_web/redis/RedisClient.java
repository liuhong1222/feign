package cn.feignclient.credit_feign_web.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;  

@Component 
public class RedisClient {
	
	@Autowired  
    private JedisPool jedisPool;  
      
    public void set(String key, String value) {  
        Jedis jedis = null;  
        try {
        	jedis = jedisPool.getResource();  
            jedis.set(key, value);
//            jedis.expire(key, 30 * 60 * 1000);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			//返还到连接池  
            jedis.close(); 
		}
    }  
    
    public void set(String key, String value,int expire) {  
        Jedis jedis = null;  
        try {
        	jedis = jedisPool.getResource();  
            jedis.set(key, value);
            jedis.expire(key, expire);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			//返还到连接池  
            jedis.close(); 
		}
    }  
    
    public long incrBy(String key,Long value) {    	
    	long result = 0;
        try (Jedis jedis = jedisPool.getResource()) {
        	result = jedis.incrBy(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //返还到连接池
        return result;
    }
    
    public long decrBy(String key,Long value) {
    	long result = 0;
        try (Jedis jedis = jedisPool.getResource()) {
            result = jedis.decrBy(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //返还到连接池
        return result;
    }
      
    public String get(String key)  {  
  
        Jedis jedis = null;  
        String value = "";
        try {
        	jedis = jedisPool.getResource();  
        	value = jedis.get(key);  
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			 //返还到连接池  
            jedis.close();  
		}
        
        return value;
    }  
    
    public Boolean exists(String key)  {  
    	  
        Jedis jedis = null;  
        Boolean value = false;
        try {
        	jedis = jedisPool.getResource();  
        	value = jedis.exists(key);  
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			 //返还到连接池  
            jedis.close();  
		}
        
        return value;
    }
    
    public void remove(String key) {
    	Jedis jedis = null;  
        try {
        	jedis = jedisPool.getResource();  
        	jedis.del(key);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			 //返还到连接池  
            jedis.close();  
		}
        
    }
    
    public void decr(String key) {
    	Jedis jedis = null;  
        try {
        	jedis = jedisPool.getResource();  
        	jedis.decr(key);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			 //返还到连接池  
            jedis.close();  
		}
        
    }
    
 // 存对象
    public void setObject(String key, Object obj, int expireOfSeconds) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            ObjectOutputStream oos = null;  //对象输出流
            ByteArrayOutputStream bos = null;  //内存缓冲流
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            byte[] byt = bos.toByteArray();
            jedis.set(key.getBytes(), byt);
            jedis.expire(key, expireOfSeconds);
            bos.close();
            oos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //返还到连接池

    }

    // 取对象
    public Object getObject(String key) throws Exception {
        Object obj = null;
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] byt = jedis.get(key.getBytes());
            if (byt != null) {
                ObjectInputStream ois = null;  //对象输入流
                ByteArrayInputStream bis = null;   //内存缓冲流
                bis = new ByteArrayInputStream(byt);
                ois = new ObjectInputStream(bis);
                obj = ois.readObject();
                bis.close();
                ois.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //返还到连接池
        return obj;
    }
}
