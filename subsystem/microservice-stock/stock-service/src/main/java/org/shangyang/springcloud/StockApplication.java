package org.shangyang.springcloud;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


/**
 * 
 * @author shangyang
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableResourceServer
@EnableFeignClients
public class StockApplication extends ResourceServerConfigurerAdapter{

	public static void main(String[] args) {
		new SpringApplicationBuilder(StockApplication.class).web(WebApplicationType.SERVLET).run(args);
	}

    
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/stock/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.PUT, "/stock/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.GET, "/stock/**").access("#oauth2.hasScope('read')");
    }

}
