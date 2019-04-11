package org.shangyang.springcloud.stock.api;

public class ProductVO {

	long productId;
	
	String productName;

	public ProductVO(){
		
	}
	
	public ProductVO(long id, String name){
		
		productId = id;
		productName = name;
		
	}
	
	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
}
