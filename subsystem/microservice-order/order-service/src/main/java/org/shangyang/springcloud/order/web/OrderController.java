package org.shangyang.springcloud.order.web;

import org.shangyang.springcloud.commons.support.OAuthUser;
import org.shangyang.springcloud.commons.support.UserContext;
import org.shangyang.springcloud.order.api.OrderVO;
import org.shangyang.springcloud.stock.api.IRemoteStock;
import org.shangyang.springcloud.stock.api.ProductVO;
import org.shangyang.springcloud.stock.api.StockVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * 该用例模拟创建订单，并且在调用过程中 Order 微服务将会调用 Stock 微服务；
 * 
 * @author shangyang
 *
 */
@RestController
@EnableOAuth2Client
@RequestMapping(value="/order")
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    @Autowired
    IRemoteStock stock;
    
    @Autowired
    private DiscoveryClient client;
    
    /**
     * 模拟生成订单接口，生成订单的同时会调用 stock 的远程接口进行库存扣减操作；
     * 
     * @param order
     * @return
     */
    @RequestMapping( method = RequestMethod.POST, consumes = "application/json") // 加上 consumes 表示该接口只接受 header 为 application/json 的接口调用 
    public ResponseEntity<OrderVO> create(@RequestBody OrderVO order) {
        ServiceInstance instance = client.getLocalServiceInstance();
        logger.info("/create order, host:" + instance.getHost() + ", service_id:" + instance.getServiceId());
        // 调用 stock 微服务接口，进行库存扣减；
        ResponseEntity<String> entity = stock.reduce( order.getProduct().getProductId(), new StockVO( order.getProduct().getProductId(),
        		order.getProduct().getProductName(), order.getQuantity()));
        if( entity.getStatusCode().equals(HttpStatus.OK) ){
        	logger.info("====> success of creating the order with order id: " + order.getOrderId() );
        }else{
        	logger.error("====> failed of creating the order with order id: " + order.getOrderId() );
        }        
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
    
    /**
     * 模拟取得订单接口，内部逻辑只是 mock
     * 
     * @param id
     * @return
     */
    @RequestMapping( value = "/{id}", method = RequestMethod.GET )
    public ResponseEntity<OrderVO> get(@PathVariable("id") long id){
    	OAuthUser user = UserContext.getUser(); // this is the base user authorize information;
        ServiceInstance instance = client.getLocalServiceInstance();
        logger.info("/get order, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", " + user.toString() );
        long productId = 1000; // mock a product id;
        ResponseEntity<ProductVO> entity = stock.getProduct( productId ); // then get the product from the Stock Service;
        ProductVO product = entity.getBody();
        OrderVO order = new OrderVO(id, product.getProductId(), product.getProductName(), 10 );
        return new ResponseEntity<OrderVO>(order, HttpStatus.OK );
    }

}
