package org.shangyang.springcloud.commons.support;

import feign.Feign;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;

/**
 *
 * 该 OAuth2 FeignRequestInterceptor 的目的是使得 Feign Client 能够 replay access tokens；分两种情况
 *
 * 一) Feign Client 在不使用 Hystrix 的时候，
 *
 * 默认情况下，Feign Client 不转发 Access Token；OAuth2FeignAutoConfiguration 的目的就是使得 Feign Client 转发 Access Token，
 * 实现的方式就是往容器中注入一个 Filter {@link OAuth2FeignRequestInterceptor}，使得每次 Feign Client 请求的时候，自动的注入
 * Access Token，不过，前提是，要求使用 Feign Client 转发 Token 的客户端 Resource Server 或者是
 *
 * Reference: https://jmnarloch.wordpress.com/2015/10/14/spring-cloud-feign-oauth2-authentication/
 *
 * 二) Feign Client 在使用 Hystrix 的时候，
 *
 * 但是，一旦启动了 Hystrix，token relay 便会失效，错误如下，
     java.lang.IllegalStateException: No thread-bound request found: Are you referring to request attributes outside of
        an actual web request, or processing a request outside of the originally receiving thread?
        If you are actually operating within a web request and still receive this message, your code is probably running
        outside of DispatcherServlet: In this case, use RequestContextListener or RequestContextFilter to expose the current request.
     at org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes(RequestContextHolder.java:131) ~[spring-web-5.1.6.RELEASE.jar:5.1.6.RELEASE]
     at org.springframework.web.context.request.AbstractRequestAttributesScope.get(AbstractRequestAttributesScope.java:42) ~[spring-web-5.1.6.RELEASE.jar:5.1.6.RELEASE]
     at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:353) ~[spring-beans-5.1.6.RELEASE.jar:5.1.6.RELEASE]
     at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199) ~[spring-beans-5.1.6.RELEASE.jar:5.1.6.RELEASE]
     at org.springframework.aop.target.SimpleBeanTargetSource.getTarget(SimpleBeanTargetSource.java:35) ~[spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
     at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:193) ~[spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
     at com.sun.proxy.$Proxy113.getAccessTokenRequest(Unknown Source) ~[na:na]
     at org.shangyang.springcloud.commons.support.OAuth2FeignRequestInterceptor.apply(OAuth2FeignRequestInterceptor.java:29) ~[classes/:na]
     at feign.SynchronousMethodHandler.targetRequest(SynchronousMethodHandler.java:169) ~[feign-core-10.1.0.jar:na]
     ......
 * 原因是 oauth2ClientContext 无法从当前 thread 中获取，是因为如果启动了 Hystrix，它会启动一个独立于 Web Thread 之外的单独的线程；
 * https://github.com/spring-cloud/spring-cloud-security/issues/118 描述了具体的原因；那么如何解决呢？在当前最新的 Greenwich
 * 的 Spring Cloud 版本的官方文档中有了相关的介绍，有两种解决方案，
 * 1. 让 Hystrix 使用同一个线程
 *    官方文档的描述如下，
 *    <b>If you encounter a runtime exception that says it cannot find the scoped context, you need to use the same thread.</b>
 *    如果当你遇到 runtime exception 并且该异常是因为不能找到 scoped context 所导致的，那么你可以使用相同的线程（这句话的意思是，你可以配置
 *    Hystrix 使得它能够和你的当前线程是同一个线程；
 *    配置也很简单，在 application.yml 中添加如下的内容，
 *      hystrix.command.default.execution.isolation.strategy: SEMAPHORE
 *    将 hystrix 的默认线程模式从 THREAD 改为 SEMAPHORE 即可，SEMAPHORE 表示让 Hystrix 使用相同线程；
 * 2. 另外一个解决方案是，将属性 hystrix.shareSecurityContext 设置为 <b>true</b>
 *    这样，可以不改动 Hystrix 的默认线程模式，
 * Reference:
 *  - 官网: https://cloud.spring.io/spring-cloud-static/spring-cloud-netflix/2.1.0.RELEASE/single/spring-cloud-netflix.html#netflix-hystrix-starter
 *  - Hystrix 配置详细介绍: https://github.com/Netflix/Hystrix/wiki/Configuration#execution.isolation.strategy
 *
 *
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