package org.shangyang.springcloud.stock.web;

import org.shangyang.springcloud.stock.api.ProductVO;
import org.shangyang.springcloud.stock.api.StockVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    ServiceInstance serviceInstance;

    /**
     * 扣减库存，模拟根据 productid 来进行扣减库存 
     * 
     * @param productid
     * @param stock
     * @return true if reduce success.
     */
    @RequestMapping(value = "/{productid}", method = RequestMethod.PUT )
    public ResponseEntity<String> reduce(@PathVariable long productid, @RequestBody StockVO stock) {
        logger.info("/reduce stock, host:" + serviceInstance.getHost() + ", service_id:" + serviceInstance.getServiceId());
        logger.info("====> success reduced " + stock.getReduce() + " products with product id:"+productid);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/product/{productid}", method = RequestMethod.GET )
    @ResponseBody
    public ResponseEntity<ProductVO> getProduct(@PathVariable long productid ){
        logger.info("/get product, host:" + serviceInstance.getHost() + ", service_id:" + serviceInstance.getServiceId());
        ProductVO product = new ProductVO(productid, "sample");
        return new ResponseEntity<>( product, HttpStatus.OK );
    }
	
}
