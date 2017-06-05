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
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * 测试客户端链接；
 * 
 * @author shangyang
 *
 */
public class ClientTest {

	private static final Logger logger = LoggerFactory.getLogger(ClientTest.class);
	
	final static int PORT = 8000;
	
	final static String CLIENT_ID = "demo";
	
	final static String CLIENT_SECRET = "demo";
	
	final static long TEST_ORDER_ID = 1000;
	
	@Test
	public void testGetOrder(){
	    
		String accessToken = this.getAccessToken();
		
		String uri = "http://localhost:8000/order/"+TEST_ORDER_ID;
		
	    RestTemplate rest = new RestTemplate();
	    
	    HttpHeaders headers = new HttpHeaders();
	    
	    headers.setContentType( MediaType.TEXT_PLAIN );
	    
	    headers.add( "authorization", "Bearer " + accessToken );

	    HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
	    // pay attention, if using get with headers, should use exchange instead of getForEntity / getForObject
	    ResponseEntity<OrderVO> resp = rest.exchange( uri, HttpMethod.GET, entity, OrderVO.class, new Object[]{ null } );
	    
	    OrderVO order = resp.getBody();
	    
	    logger.debug( order.toString() );
	    
	    assertTrue( order.getOrderId() == 1000 );
	    
	    assertEquals( order.getProduct().getProductId(), 1000 );
	    
	    assertEquals( order.getProduct().getProductName(), "sample" );
	    
		
	}
	
	private String getAccessToken(){
		
	    final String uri = "http://localhost:"+PORT+"/uaa/oauth/token?grant_type=password&username=user&password=password";
	    
	    RestTemplate rest = new RestTemplate();
	    
	    HttpHeaders headers = new HttpHeaders();
	    
	    headers.setContentType( MediaType.TEXT_PLAIN );
	    
	    headers.add("authorization", getBasicAuthHeader());

	    HttpEntity<String> entity = new HttpEntity<String>(null, headers);
	    
	    ResponseEntity<OAuth2AccessToken> resp = rest.postForEntity(uri, entity, OAuth2AccessToken.class);
	    
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
