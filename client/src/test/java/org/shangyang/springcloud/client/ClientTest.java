package org.shangyang.springcloud.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.shangyang.springcloud.order.api.OrderVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 
 * 测试客户端链接；
 * 
 * @author shangyang
 *
 */
public class ClientTest {

	private static final Logger logger = LoggerFactory.getLogger(ClientTest.class);
	
	final static long GATEWAY_PORT = 8000;
	
	final static String CLIENT_ID = "demo";
	
	final static String CLIENT_SECRET = "demo";
	
	final static String USERNAME = "user";
	
	final static String PASS = "password";
	
	final static long TEST_ORDER_ID = 1000;
	
	final static String GET_ORDER_URI = "http://localhost:"+GATEWAY_PORT+"/order/"+TEST_ORDER_ID;
	
	final static String POST_ORDER_URI = "http://localhost:"+GATEWAY_PORT+"/order";
	
	final static String TOKEN_REQUEST_URI = "http://localhost:"+GATEWAY_PORT+"/uaa/oauth/token?grant_type=password&username=" + USERNAME + "&password=" + PASS;
	
	/**
	 * 该测试用例的流程为 
	 * 
	 * Client -> Gateway -> Order -> Stock；期间，Client -> Gateway、Gateway -> Order、Order -> Stock 均产生了 Token Relay
	 * 
	 */
	@Test
	public void testGetOrder(){
	    
	    RestTemplate rest = new RestTemplate();
	    
	    HttpHeaders headers = new HttpHeaders();
	    
	    headers.add( "authorization", "Bearer " + getAccessToken() );

	    HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
	    // pay attention, if using get with headers, should use exchange instead of getForEntity / getForObject
	    ResponseEntity<OrderVO> resp = rest.exchange( GET_ORDER_URI, HttpMethod.GET, entity, OrderVO.class, new Object[]{ null } );
	    
	    OrderVO order = resp.getBody();
	    
	    logger.debug( order.toString() );
	    
	    assertTrue( order.getOrderId() == 1000 );
	    
	    assertEquals( order.getProduct().getProductId(), 1000 );
	    
	    assertEquals( order.getProduct().getProductName(), "sample" );
		
	}
	
	@Test
	public void testPostOrder(){
		
	    OrderVO order = new OrderVO(1000, 2000, "macbook", 10);
	    
	    RestTemplate restTemplate = new RestTemplate();
	    
	    MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
	    
	    jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	    
	    restTemplate.getMessageConverters().add(jsonHttpMessageConverter);
	    
	    HttpHeaders headers = new HttpHeaders();
	    
	    headers.setContentType( MediaType.APPLICATION_JSON );
	    
	    headers.add( "authorization", "Bearer " + getAccessToken() );
	    
	    HttpEntity<OrderVO> entity = new HttpEntity<OrderVO>(order, headers);
	    
	    ResponseEntity<OrderVO> resp = restTemplate.postForEntity( POST_ORDER_URI, entity, OrderVO.class);
	 
	    logger.debug( resp.toString() );
	    
	    assertTrue( HttpStatus.CREATED.equals(resp.getStatusCode()) );
	    
	    OrderVO o = resp.getBody();
	    
	    assertEquals( o.getOrderId(), 1000 );
	    
	    assertEquals( o.getProduct().getProductName(), "macbook");	    
		
	}
	
	private String getAccessToken(){
	    
	    RestTemplate rest = new RestTemplate();
	    
	    HttpHeaders headers = new HttpHeaders();
	    
	    headers.setContentType( MediaType.TEXT_PLAIN );
	    
	    headers.add("authorization", getBasicAuthHeader());

	    HttpEntity<String> entity = new HttpEntity<String>(null, headers);
	    
	    ResponseEntity<OAuth2AccessToken> resp = rest.postForEntity( TOKEN_REQUEST_URI, entity, OAuth2AccessToken.class);
	    
	    if( !resp.getStatusCode().equals( HttpStatus.OK )){
	    	
	    	throw new RuntimeException( resp.toString() );
	    }
	    
	    OAuth2AccessToken t = resp.getBody();
	    
	    logger.debug("the response, access_token: " + t.getValue() +"; token_type: " + t.getTokenType() +"; "
	    		+ "refresh_token: " + t.getRefreshToken() +"; expiration: " + t.getExpiresIn() +", expired when:" + t.getExpiration() );			
		
	    return t.getValue();

	}
	
	private String getBasicAuthHeader(){
		
        String auth = CLIENT_ID + ":" + CLIENT_SECRET;
        
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
        
        String authHeader = "Basic " + new String(encodedAuth);
        
        return authHeader;
	}
	
	
	
}
