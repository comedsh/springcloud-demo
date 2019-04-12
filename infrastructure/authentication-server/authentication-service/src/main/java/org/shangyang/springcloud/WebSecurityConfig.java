package org.shangyang.springcloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

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

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 在初始化密码的时候，如果使用的是 plain text，那么必须加上前缀 {noop}，否则报错 Spring Security – There is no PasswordEncoder
     * mapped for the id “null”；
     * 参考 https://www.mkyong.com/spring-boot/spring-security-there-is-no-passwordencoder-mapped-for-the-id-null/
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		JdbcUserDetailsManagerConfigurer c = auth.jdbcAuthentication().dataSource(dataSource);
		UserDetailsManager m = c.getUserDetailsService();
		m.deleteUser("shangyang");
		m.deleteUser("user");
		c.withUser("shangyang").password("{noop}password").roles("USER", "ACTUATOR").and()
		 .withUser("user").password("{noop}password").roles("USER", "ACTUATOR");
    }
}
