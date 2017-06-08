package org.shangyang.springcloud;

import java.security.Principal;

import javax.sql.DataSource;

import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * OAuth2 Authorization Server configuration; 
 * 
 * @author shangyang
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAuthorizationServer
@EnableResourceServer
@RestController
public class AuthenticationApplication {

	@Autowired
	private DataSource dataSource;	
	
    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }


    // 配置 URL 到 view 之间的映射
    @Configuration
    static class MvcConfig extends WebMvcConfigurerAdapter {
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("login").setViewName("login");
            registry.addViewController("/").setViewName("index");
        }
    }
    
    /**
     * 
     * 记录下之前这里碰到的坑，掉坑很久没爬出来.. 
     * 
     * 下面这里主要是针对 web 请求的拦截的配置，有别于直接通过 http 协议进行访问，比如 restful；web 请求拦截默认是必须提供 CSRF token 的，以避免 XSRF 攻击；
     * 
     * @author shangyang
     *
     */
    @Configuration
    @EnableWebSecurity
    @Order(-20)
    static class LoginConfig extends WebSecurityConfigurerAdapter {
    	
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
            	.formLogin().loginPage("/login").permitAll() // 访问 /login 资源是被完全允许的
                .and()
                .requestMatchers()
                .antMatchers("/", "/login", "/oauth/authorize", "/oauth/confirm_access") // 这三个资源是需要验证申请的
                .and()
                .authorizeRequests()
                .anyRequest().authenticated(); // 访问其余的所有资源的前提是，必须被验证通过
        }
    }
    
    @RequestMapping("/user")
    Object user(Principal p) {
    	
    	OAuth2Authentication a = (OAuth2Authentication) p;
    	
    	return a.getUserAuthentication().getPrincipal();    	
        
    }    
    
    /**
     * 为测试环境添加相关的 Request Dumper information，便于调试
     * @return
     */
    @Profile("!cloud")
    @Bean
    RequestDumperFilter requestDumperFilter() {
        return new RequestDumperFilter();
    }
    
    /**
     * 
     * 这里主要是针对 OAuth2 的配置
     * 
     * @author shangyang
     *
     */
	@Configuration
	@EnableAuthorizationServer
	protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private AuthenticationManager auth;

		@Autowired
		private DataSource dataSource;

		private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		@Bean
		public JdbcTokenStore tokenStore() {
			return new JdbcTokenStore(dataSource);
		}

		@Bean
		protected AuthorizationCodeServices authorizationCodeServices() {
			return new JdbcAuthorizationCodeServices(dataSource);
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
			
			security.passwordEncoder(passwordEncoder);
			
			// 这个等价于配置 security.oauth2.client.auto-approve-scopes: .*
			security.allowFormAuthenticationForClients();
			
			// 允许 /oauth/check/token 等链接访问；
			security.checkTokenAccess("permitAll()");						
			
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
				throws Exception {
			endpoints.authorizationCodeServices(authorizationCodeServices())
					 .authenticationManager(auth).tokenStore(tokenStore())
					 .approvalStoreDisabled();
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			// @formatter:off
			// 通过 @see ClientBuilder 在每次应用启动的时候，将如下的 CLIENT 用户自动添加到 OAUTH_CLIENT_DETAILS  表中；
			clients.jdbc(dataSource)
					.passwordEncoder(passwordEncoder)
					.withClient("demo")
						.authorizedGrantTypes("password")
						.authorities("ROLE_CLIENT").scopes("read", "write", "trust")
						.resourceIds("oauth2-resource").secret("demo")
						.accessTokenValiditySeconds(3600);
			// @formatter:on
		}

	}
	
	/**
	 * 在数据库中设置初始账户
	 * 
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		
		// 通过 @see UserDetailsBuilder 在应用程序启动的时候，自动将测试用户添加到 USERS 表中；
		auth.jdbcAuthentication().dataSource(dataSource)
								 .withUser("shangyang").password("password").roles("USER", "ACTUATOR").and()
								 .withUser("user").password("password").roles("USER", "ACTUATOR");
		
	}
    
    
    
    
}
