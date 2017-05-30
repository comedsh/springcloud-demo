package org.shangyang.springcloud.stock.api;

/**
 * The Stock Business Visualize Object
 * 
 * @author shangyang
 *
 */
public class StockVO {

	ProductVO product;
	
	// how many products should be reduced?
	int reduce;

	public StockVO(){
		
	}
	
	public StockVO(long productId, String productName, int reduce){
		
		this.product = new ProductVO(productId, productName);
		
		this.reduce = reduce;
	}

	public int getReduce() {
		return reduce;
	}

	public void setReduce(int reduce) {
		this.reduce = reduce;
	}	
	
}
