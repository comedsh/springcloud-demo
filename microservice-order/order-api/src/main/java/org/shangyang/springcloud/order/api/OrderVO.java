package org.shangyang.springcloud.order.api;

import org.shangyang.springcloud.stock.api.ProductVO;

/**
 * 一个很粗略的 Order..  
 *  
 * @author shangyang
 *
 */
public class OrderVO {

	long orderId;
	
	ProductVO product;
	
	int quantity;

	public OrderVO(){
		
	}
	
	public OrderVO(long orderId, long productId, String productName, int quantity ){
		
		this.orderId = orderId;
		
		product = new ProductVO(productId, productName);
		
		this.quantity = quantity;
		
	}
	
	public ProductVO getProduct() {
		return product;
	}

	public void setProduct(ProductVO product) {
		this.product = product;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public String toString(){
		
		return "Order Instance: id: "+orderId+"; productId: "+product.getProductId()+"; product name:"+product.getProductName()+"; quantity: "+quantity;
		
	}
	
}
