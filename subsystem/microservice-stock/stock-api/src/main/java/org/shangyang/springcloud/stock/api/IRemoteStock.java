package org.shangyang.springcloud.stock.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * 该接口是放在 Order 工程中还是放在 Stock 工程中？最开始我觉得应该放到 Order 工程中，因为该接口是 HTTP Rest 接口，
 * 按照常规思维，HTTP 是由调用发发起，那么自然应该放在 Order 工程中；但是，后来想想，这样做并不太好，因为，一旦 Stock 中该接口
 * 发生变化了怎么办？1. 自然是由 Stock 开发团队来维护接口的变化更好；2. 另外，如果有其他的微服务工程，也恰好需要调用 Stock 该接口，
 * 那么同时需要由其它开发组同时更新两个地方的变化；所以，综上所述，接口定义，还是应该由 Stock 自己来维护是最好的；
 * 
 * @author shangyang
 *
 */
@FeignClient("stock-service")
public interface IRemoteStock {

	@RequestMapping(method=RequestMethod.PUT, value="/stock/{productid}")
	ResponseEntity<String> reduce(@PathVariable(value="productid") long productid, @RequestBody StockVO stock );
	
	@RequestMapping(method=RequestMethod.GET, value = "/stock/product/{productid}")
	ResponseEntity<ProductVO> getProduct(@PathVariable(value="productid") long productid );
	
	
}
