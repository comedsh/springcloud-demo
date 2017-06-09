package org.shangyang.springcloud.oauth;

import java.util.List;

/**
 * 
 * the Base User Interface
 * 
 * @author shangyang
 *
 */
public interface IUser {

	public String getUsername();
	
	public List<String> getRoles();
	
}
