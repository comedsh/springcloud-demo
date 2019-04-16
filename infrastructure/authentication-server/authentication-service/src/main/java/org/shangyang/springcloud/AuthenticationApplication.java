package org.shangyang.springcloud;

import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

/**
 *
 * OAuth2 Authorization Server configuration; 
 *
 * Steps to records how to enable the Asymmetric JWT:
 * 1. generate the certification
 *    $ keytool -genkeypair -alias mytest -keyalg RSA -keypass mypass -keystore mytest.jks -storepass mypass
 *    copy the mytest.jks into authorization server path: src/main/java/resources/
 * 2. generate the public.txt
 *    $ keytool -list -rfc --keystore mytest.jks | openssl x509 -inform pem -pubkey -noout
 *    copy the content into resource server path: src/main/java/resources/public.txt
 *
 * @author shangyang
 *
 */
@SpringBootApplication(scanBasePackages = "org.shangyang.springcloud")
@EnableDiscoveryClient
public class AuthenticationApplication extends SpringBootServletInitializer {
	/**
	 * 为测试环境添加相关的 Request Dumper information，便于调试
	 * @return
	 */
	@Profile("!cloud")
	@Bean
	RequestDumperFilter requestDumperFilter() {
		return new RequestDumperFilter();
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApplication.class, args);
	}

}
