package org.shangyang.springcloud.stock.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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
@FeignClient(name="stock-service", fallback = HystrixRemoteStockFallback.class)
public interface IRemoteStock {

	@RequestMapping(method = RequestMethod.PUT, value = "/stock/{productid}")
	ResponseEntity<String> reduce(@PathVariable(value = "productid") long productid, @RequestBody StockVO stock);

	@RequestMapping(method = RequestMethod.GET, value = "/stock/product/{productid}")
	ResponseEntity<ProductVO> getProduct(@PathVariable(value = "productid") long productid);
}

/**
 * 如何添加 Hystrix 参考 http://nphumbert.github.io/blog/2017/07/23/setup-a-circuit-breaker-with-hystrix/
 * 如果想要捕获异常，在 @FeignClient 的属性中使用 fallbackFactory；
 *
 * 这里需要注意的是，Hystrix 需要配置到上游系统中，因为 Hystrix 就是对下游系统不可用提供熔断的机制，因此需要在 Order 子系统中配置并启动
 * Hystrix；
 */
@Component
class HystrixRemoteStockFallback implements IRemoteStock{

	@Override
	public ResponseEntity<String> reduce(long productid, StockVO stock) {
		return new ResponseEntity<>( "failed, hystrix enabled", HttpStatus.SERVICE_UNAVAILABLE );
	}

	@Override
	public ResponseEntity<ProductVO> getProduct(@PathVariable(value = "productid") long productid){
		return new ResponseEntity<>( new ProductVO(-1, null), HttpStatus.SERVICE_UNAVAILABLE );
	}
}
