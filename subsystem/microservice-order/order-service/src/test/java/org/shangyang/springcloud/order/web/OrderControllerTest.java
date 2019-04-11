package org.shangyang.springcloud.order.web;

import org.junit.Test;
import org.shangyang.springcloud.order.api.OrderVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class OrderControllerTest {

	static int PORT = 2000;
	
	@Test
	public void testCreateOrder(){
		
	    final String uri = "http://localhost:"+PORT+"/order";
	    
	    OrderVO order = new OrderVO(1000, 2000, "macbook", 10);
	    
	    RestTemplate restTemplate = new RestTemplate();
	    
	    // 当通过 RestTemplate postForObject 或者 postForObject 进行调用的时候，自动的会将 header 设置为 application/json
	    
	    // 调用方式 1, 获取 ResponseEntity
	    ResponseEntity<?> result1 = restTemplate.postForEntity(uri, order, null);
	 
	    System.out.println(result1);	
	    
	    // 调用方式 2，获取返回的自定义的 Object
	    OrderVO result2 = restTemplate.postForObject(uri, order, OrderVO.class);
	    
	    System.out.println(result2);
		
	}
	
}
