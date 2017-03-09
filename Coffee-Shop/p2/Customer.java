package p2;

import java.util.ArrayList;
import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the 
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	private final int orderNum;    
	private int eatingTime;
	private int priority;
	
	
	
	private static int runningCounter = 0;

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order, int eatingTime, int priority) {
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
		this.eatingTime = eatingTime;
		this.priority = priority;
		
	}

	public int priority(){
		return this.priority;
	}
	public String toString() {
		return name;
	}
	
//	public static List<Food> orderReceived = new LinkedList<Food>();
	
	public List<Food> getOrderList(){
		return this.order;
	}
	
	public int getOrderNumber(){
		return this.orderNum;
	}
	public List<Integer> priorityList = new ArrayList<Integer>();;
  
	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the coffee shop (only successful when the coffee shop has a
	 * free table), place its order, and then leave the coffee shop
	 * when the order is complete.
	 */
	public void run() {
		
		Simulation.logEvent(SimulationEvent.customerStarting(this));
		
		
		synchronized (Simulation.customerInsideCoffeeShop) {
			while(!(Simulation.customerInsideCoffeeShop.size() < Simulation.maxCap)){
				try {
					Simulation.customerInsideCoffeeShop.wait();
				} catch (InterruptedException e) {
					System.out.println("Customer thread interrupted.");
//					e.printStackTrace();
				}
			}
				
			Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
			Simulation.customerInsideCoffeeShop.add(this);
		}

		
		if(!( Simulation.customersAndOrders.containsValue(this))){ // As customer can place only one order
			synchronized (Simulation.customersAndOrders) {
				Simulation.customersAndOrders.put(orderNum, this);
			}	
			synchronized (Simulation.ordersAndOrderNumbers) {
				Simulation.ordersAndOrderNumbers.put(orderNum,order);
			}
			synchronized (Simulation.pendingOrders) {
				Order o = new Order(this.priority, this.orderNum, this, this.order);
				Simulation.pendingOrders.add(o);
				Simulation.pendingOrders.notifyAll();
				Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, order, orderNum));
			}
		}
		synchronized (Simulation.ordersCompleted) {
			Simulation.ordersCompleted.put(this, false);
			while(!(Simulation.ordersCompleted.get(this))){
				try{
					Simulation.ordersCompleted.wait();
				}
				catch(InterruptedException e){
					System.out.println("The order was incomplete");
				}
			}
			
//				for(Food f : order){
//					for(Food f1: orderReceived){
//						if(f.equals(f1))
//						orderReceived.remove(f1);
//					}
//				}
		}
//			if(orderReceived.isEmpty()){
			Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, order, orderNum));
			try {
				Thread.sleep(eatingTime);
			} catch (InterruptedException e) {
				System.out.println("Eating thread interrupted.");
			}
//			}
			synchronized (Simulation.customerInsideCoffeeShop) {
			Simulation.customerInsideCoffeeShop.remove(this);
			Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
			Simulation.customerInsideCoffeeShop.notifyAll();
		}
	}
	
}