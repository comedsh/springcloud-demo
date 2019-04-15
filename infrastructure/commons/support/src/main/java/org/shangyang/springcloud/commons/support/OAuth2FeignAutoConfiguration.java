package org.shangyang.springcloud.commons.support;

import feign.Feign;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;

/**
 * 默认情况下，Feign Client 不转发 Access Token；OAuth2FeignAutoConfiguration 的目的就是使得 Feign Client 转发 Access Token，
 * 实现的方式就是往容器中注入一个 Filter {@link OAuth2FeignRequestInterceptor}，使得每次 Feign Client 请求的时候，自动的注入
 * Access Token，不过，前提是，要求使用 Feign Client 转发 Token 的客户端 Resource Server 或者是 OAuth2 Client(@EnableOAuth2Client.)
 *
 * Reference: https://jmnarloch.wordpress.com/2015/10/14/spring-cloud-feign-oauth2-authentication/
 */
@Configuration
@ConditionalOnClass({ Feign.class })
@ConditionalOnProperty(value = "feign.oauth2.enabled", matchIfMissing = true)
public class OAuth2FeignAutoConfiguration {

    @Bean
    // Comment out the following line or else it could not be initialized
    // @ConditionalOnBean(OAuth2ClientContext.class)
    public RequestInterceptor oauth2FeignRequestInterceptor(OAuth2ClientContext oauth2ClientContext) {
        return new OAuth2FeignRequestInterceptor(oauth2ClientContext);
    }
}