package org.shangyang.springcloud.commons.support;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import javax.servlet.*;
import java.io.IOException;

/**
 * 
 * 现在遇到的问题是，从 Order Service 向 Stock Service 转发的时候，Credentials 丢失，原因是 Hystrix 不转发；所以，需要补发，该类的逻辑就是取转发相关遗漏的 Credentials；在 OAuth 认证中是关键；
 * 
 * Godden Code...
 * 
 * @author shangyang
 *
 */
@Configuration
public class HystrixCredentialsContext {

	private static final Logger logger = LoggerFactory.getLogger(HystrixCredentialsContext.class);

    private static final HystrixRequestVariableDefault<Authentication> authentication = new HystrixRequestVariableDefault<>();

    public static HystrixRequestVariableDefault<Authentication> getInstance() {
        return authentication;
    }		
	
	/**
	 * 下面这段代码是关键，实现 @See feign.RequestInterceptor，
	 * 1. 添加认证所需的 oauth token；
	 * 2. 添加认证所需的 user;
	 * 
	 * 目前仅实现了 oauth toke，将来看情况是否实现 user;
	 * 
	 * 特别要注意一点，因为 HystrixRequestContext 和 RequestContext 不在同一个线程中，所以，不能直接在 RequestInterceptor 的实现方法中调用 RequestContext 中的资源，因为 HystrixRequestContext 是在自己
	 * 的 ThreadPool 中执行的；所以，这里搞得比较的麻烦... 不能在 {@link RequestInterceptor#apply(RequestTemplate)} 中直接使用 RequestContext / SecurityContextHolder，否则取到的资源全部是 null；
	 * 
	 * @return
	 */
	@Bean
	public RequestInterceptor requestTokenBearerInterceptor() {
		
	        return new RequestInterceptor() {
	        	
	            @Override
	            public void apply(RequestTemplate requestTemplate) {
	            	
	            	Authentication auth = HystrixCredentialsContext.getInstance().get();
	            	
	            	if( auth != null ){
	            		
		            	logger.debug("try to forward the authentication by Hystrix, the Authentication Object: "+ auth );
		            	
		            	// 记得，因为 Feign Interceptor 是通过自有的 ThreadPool 中的线程执行的，与当前的 Request 线程不是同一个线程，所以这里不能使用 debug 模式进行调试；
		                requestTemplate.header("Authorization", "bearer " + ( (OAuth2AuthenticationDetails) auth.getDetails()).getTokenValue() );
	                
	            	}else{
	            		
	            		logger.debug("attention, there is no Authentication Object needs to forward");
	            		
	            	}
	                
	        }
	    };
	}
	
    @Bean
    public FilterRegistrationBean hystrixFilter() {
    	
        FilterRegistrationBean r = new FilterRegistrationBean();
        
        r.setFilter(new Filter(){

			@Override
			public void init(FilterConfig filterConfig) throws ServletException {

			}

			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException {

				// as the comments described by HystrixRequestContext, for using HystrixRequestVariable should first initialize the context at the beginning of each request
				// so made it here... 
				HystrixRequestContext.initializeContext();
				
				SecurityContext securityContext = SecurityContextHolder.getContext();
				
				if( securityContext != null ){

					Authentication auth = (Authentication) securityContext.getAuthentication();	    
			    	
			    	HystrixCredentialsContext.getInstance().set(auth);

			    	logger.debug("try to register the authentication into Hystrix Context, the Authentication Object: "+ auth );
			    	
				}
		    	
		    	chain.doFilter(request, response);
				
			}

			@Override
			public void destroy() {
				
			}
        	
        });
        
        // In case you want the filter to apply to specific URL patterns only
        r.addUrlPatterns("/*");
        
        return r;
    }	
	
}