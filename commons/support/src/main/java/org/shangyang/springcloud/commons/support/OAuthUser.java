package org.shangyang.springcloud.commons.support;

import java.util.ArrayList;
import java.util.List;

import org.shangyang.springcloud.oauth.IUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * 
 * The user information for the base authorization; 
 * 
 * 大家记住，这个只是用来做基本授权的；详细授权是在 Resources 那边；
 * 
 * @author shangyang
 *
 */
public class OAuthUser implements IUser{
	
	String username;
	
	List<String> roles = new ArrayList<String>();
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	public static OAuthUser convert(Authentication auth){
		
		if( auth == null ) return null;
		
		OAuthUser u = new OAuthUser();
		
		u.setUsername( (String) auth.getPrincipal() );
		
		for( GrantedAuthority a : auth.getAuthorities() ){
			
			u.getRoles().add(a.getAuthority());
			
		}
		
		return u;
		
	}
	
	public String toString(){
		
		StringBuilder rr = new StringBuilder();
		
		for(String role : roles ){
			rr.append(role).append(", ");
		}
		
		return "user name: "+ username +"; user roles: " + rr.toString();
		
	}
	
}
