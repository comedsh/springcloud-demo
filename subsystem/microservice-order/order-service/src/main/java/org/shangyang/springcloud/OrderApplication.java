package org.shangyang.springcloud;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@SpringBootApplication(scanBasePackages="org.shangyang.springcloud")
@EnableDiscoveryClient
@EnableResourceServer
@EnableFeignClients
@EnableCircuitBreaker
public class OrderApplication extends ResourceServerConfigurerAdapter {

    /**
     * 设置资源 URI 的访问的方式和权限
     * 
     * 1. 设置哪些 URI 的访问需要通过 token 验证；
     * 2. 需要哪些 OAuth 的访问权限；
     * 
     * 下面这段程序做了两件事
     * 1. 访问 URI /api/** 需要 token 验证
     * 2. GET 请求需要 READ OAuth 的权限；POST 请求需要 WRITE OAuth 的权限；
     * 
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/order/**").access("#oauth2.hasScope('read')")
                .antMatchers(HttpMethod.POST, "/order/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.PUT, "/order/**").access("#oauth2.hasScope('write')");
    }	
    
	public static void main(String[] args) {
		new SpringApplicationBuilder(OrderApplication.class).web(WebApplicationType.SERVLET).run(args);
	}

}
