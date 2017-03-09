package p2;

import java.util.List;

public class Order  implements Comparable<Order>{

	int priority;
	int orderNumber;
	Customer customer;
	List<Food> list;
	
	public Order(int priority, int orderNumber, Customer customer, List<Food> list){
		this.priority = priority;
		this.orderNumber = orderNumber;
		this.list = list;
		this.customer = customer;
	}
	
	public int compareTo(Order o){
		return (this.priority - o.priority);
	}
}
