package org.shangyang.springcloud.zuul;

import org.junit.Test;
import org.shangyang.springcloud.order.api.OrderVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * 模拟客户端进行测试；
 * 
 * @author shangyang
 *
 */
public class ClientTest {

	static final int PORT = 8000; // ZUUL default port
	
	@Test
	public void testCreateOrder(){
		
		// http://localhost:8000/order-service/order 将会被映射到 http://localhsot:2000/order
	    final String uri = "http://localhost:"+PORT+"/order-service/order";
	    
	    OrderVO order = new OrderVO(1000, 2000, "macbook", 10);
	    
	    RestTemplate restTemplate = new RestTemplate();
	    
	    ResponseEntity<?> result = restTemplate.postForEntity(uri, order, OrderVO.class);
	 
	    System.out.println(result);	
		
	}
	
}
