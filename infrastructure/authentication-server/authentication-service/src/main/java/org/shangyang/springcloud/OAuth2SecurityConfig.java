package org.shangyang.springcloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.ClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.builders.JdbcClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableAuthorizationServer
public class OAuth2SecurityConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authMgmr;

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
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.passwordEncoder(passwordEncoder);
        // 这个等价于配置 security.oauth2.client.auto-approve-scopes: .*
        security.allowFormAuthenticationForClients();
        // 允许 /oauth/check/token 等链接访问；
        security.checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authorizationCodeServices(authorizationCodeServices())
                .authenticationManager(authMgmr).tokenStore(tokenStore());
        //.approvalStoreDisabled();
    }

    /**
     * 如果按照官网上的描述来初始化 clients 的话，会报如下的错误，
     * "Error creating bean with name 'scopedTarget.clientDetailsService' defined in class path resource
     *  [org/springframework/security/oauth2/config/annotation/configuration/ClientDetailsServiceConfiguration.class]:
     * 这里需要自定义 MyJdbcClientDetailsServiceBuilder 来构建 clients data；
     *
     * Reference https://github.com/spring-projects/spring-security-oauth/issues/864
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        MyJdbcClientDetailsServiceBuilder clientDetailsServiceBuilder = new MyJdbcClientDetailsServiceBuilder();
        clientDetailsServiceBuilder.dataSource(dataSource).passwordEncoder(passwordEncoder)
                        .withClient("demo")
                        .authorizedGrantTypes("password")
                        .authorities("ROLE_CLIENT").scopes("read", "write", "trust")
                        .resourceIds("oauth2-resource").secret("demo")
                        .accessTokenValiditySeconds(3600)
                        .and()
                        .withClient("inner")
                        .authorizedGrantTypes("password")
                        .authorities("ROLE_CLIENT").scopes("read", "write", "trust")
                        .resourceIds("oauth2-resource").secret("inner")
                        .accessTokenValiditySeconds(3600);
        clients.setBuilder(clientDetailsServiceBuilder);
    }
}

/**
 * 需要自定义 JDBC Client Details 避免如下的错误，
 * "Error creating bean with name 'scopedTarget.clientDetailsService' defined in class path resource
 *  [org/springframework/security/oauth2/config/annotation/configuration/ClientDetailsServiceConfiguration.class]:
 *
 * Reference https://github.com/spring-projects/spring-security-oauth/issues/864
 */
class MyJdbcClientDetailsServiceBuilder extends ClientDetailsServiceBuilder<JdbcClientDetailsServiceBuilder> {

    private Set<ClientDetails> clientDetails = new HashSet<ClientDetails>();

    private DataSource dataSource;

    private PasswordEncoder passwordEncoder; // for writing client secrets

    public MyJdbcClientDetailsServiceBuilder dataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public MyJdbcClientDetailsServiceBuilder passwordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        return this;
    }

    @Override
    protected void addClient(String clientId, ClientDetails value) {
        clientDetails.add(value);
    }

    @Override
    protected ClientDetailsService performBuild() {
        Assert.state(dataSource != null, "You need to provide a DataSource");
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        if (passwordEncoder != null) {
            // This is used to encode secrets as they are added to the database (if it isn't set then the user has top
            // pass in pre-encoded secrets)
            clientDetailsService.setPasswordEncoder(passwordEncoder);
        }
        for (ClientDetails client : clientDetails) {
            try {
                clientDetailsService.updateClientDetails(client);
            } catch (NoSuchClientException e) {
                clientDetailsService.addClientDetails(client);
            }
        }
        return clientDetailsService;
    }

}
