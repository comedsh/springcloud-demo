package org.shangyang.springcloud;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Configuration
@RestController
public class MvcController {

    @RequestMapping("/user")
    Object user(Principal p) {
        OAuth2Authentication a = (OAuth2Authentication) p;
        return a.getUserAuthentication().getPrincipal();
    }


}
