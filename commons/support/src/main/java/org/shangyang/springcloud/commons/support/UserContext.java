package org.shangyang.springcloud.commons.support;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * 
 * the user context for user information retrieving;
 * 
 * @author shangyang
 *
 */
public class UserContext {
	
	/**
	 * 
	 * @return the authenticated base user information 
	 */
    public static OAuthUser getUser(){
    	
    	SecurityContext securityContext = SecurityContextHolder.getContext();
    	
    	if( securityContext == null ) return null;
    	
    	OAuth2Authentication oAuth = (OAuth2Authentication)securityContext.getAuthentication();
    	
    	UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) oAuth.getUserAuthentication();
    	
    	return OAuthUser.convert(token);
    	
    }
	
}
