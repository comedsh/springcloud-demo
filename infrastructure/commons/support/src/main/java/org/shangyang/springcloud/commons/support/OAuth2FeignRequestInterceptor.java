package org.shangyang.springcloud.commons.support;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.util.Assert;

/**
 * OAuth2FeignRequestInterceptor 的 token relay 的解决方案同时兼容
 *  1. SEMAPHORE 模式
 *     使用 OAuth2ClientContext
 *  2. shareSecurityContext=true
 *     使用 Security Context
 * 的两种解决方案；
 */
public class OAuth2FeignRequestInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";
    private static final Logger logger = LoggerFactory.getLogger(OAuth2FeignRequestInterceptor.class);
    private final OAuth2ClientContext oauth2ClientContext;

    public OAuth2FeignRequestInterceptor(OAuth2ClientContext oauth2ClientContext) {
        Assert.notNull(oauth2ClientContext, "Context can not be null");
        this.oauth2ClientContext = oauth2ClientContext;
    }

    @Override
    public void apply(RequestTemplate template) {
        String accessToken = null;
        try{
            // 1. Hystrix 使用 SEMAPHORE 的线程模式，可以从 OAuth2ClientContext 对象中获取 Access Token
            AccessTokenRequest r = this.oauth2ClientContext.getAccessTokenRequest();
            accessToken = r.getExistingToken().toString();
        }catch(BeanCreationException e){
            // 2. 使用 Hystrix shareSecurityContext 特性，不能获得 OAuth2ClientContext，通过 SecurityContext 获取 access token
            Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
            if (details instanceof OAuth2AuthenticationDetails) {
                accessToken = ((OAuth2AuthenticationDetails) details).getTokenValue();
            }
        }
        logger.debug("access token: {}", accessToken);
        if(accessToken != null) {
            if (template.headers().containsKey(AUTHORIZATION_HEADER)) {
                logger.warn("The Authorization token has been already set");
            } else {
                logger.debug("Constructing Header {} for Token {}", AUTHORIZATION_HEADER, BEARER_TOKEN_TYPE);
                template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, accessToken));
            }
        }
    }
}