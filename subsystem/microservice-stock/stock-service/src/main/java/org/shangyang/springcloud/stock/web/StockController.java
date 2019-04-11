package org.shangyang.springcloud.stock.web;

import org.shangyang.springcloud.stock.api.ProductVO;
import org.shangyang.springcloud.stock.api.StockVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * 扣减库存操作
 * 
 * @author shangyang
 *
 */
@RestController
@RequestMapping(value="/stock")
public class StockController {

    private final Logger logger = LoggerFactory.getLogger( StockController.class );
    
    @Autowired
    private DiscoveryClient client;
    
    /**
     * 扣减库存，模拟根据 productid 来进行扣减库存 
     * 
     * @param productName
     * @param quantity
     * @return true if reduce success.
     */
    @RequestMapping(value = "/{productid}", method = RequestMethod.PUT )
    public ResponseEntity<String> reduce(@PathVariable long productid, @RequestBody StockVO stock) {
    	
        ServiceInstance instance = client.getLocalServiceInstance();
        
        logger.info("/reduce stock, host:" + instance.getHost() + ", service_id:" + instance.getServiceId());
        
        logger.info("====> success reduced " + stock.getReduce() + " products with product id:"+productid);
        
        return new ResponseEntity<String>(HttpStatus.OK);
        
    }	
    
    @RequestMapping(value = "/product/{productid}", method = RequestMethod.GET )
    @ResponseBody
    public ResponseEntity<ProductVO> getProduct(@PathVariable long productid ){
    	
        ServiceInstance instance = client.getLocalServiceInstance();
        
        logger.info("/get product, host:" + instance.getHost() + ", service_id:" + instance.getServiceId());
    	
        ProductVO product = new ProductVO(productid, "sample");
        
        return new ResponseEntity<ProductVO>( product, HttpStatus.OK );
    	
    }
	
}
