package p2;

import java.util.ArrayList;
import java.util.List;


/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;
	public List<Food> foodCompleted = new ArrayList<Food>();
	private Customer currentCustomer;
	
	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			
			while(true) {
				//YOUR CODE GOES HERE...
				synchronized (Simulation.pendingOrders) {
				
					while(Simulation.pendingOrders.isEmpty()){
						Simulation.pendingOrders.wait();
					}
					Order o = Simulation.pendingOrders.remove();
//					System.out.println("Order removed = "+o);
//					System.out.println("Customer = "+o.customer);
					
					currentCustomer = o.customer;
					Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, o.list , o.orderNumber));
					Simulation.pendingOrders.notifyAll();
						
				}
				
				for(int i=0; i< currentCustomer.getOrderList().size(); i++){
					Food f = currentCustomer.getOrderList().get(i);
					if(f.equals(FoodType.burger)){
						synchronized (Simulation.Grill.listOfFood) {
							while(!(Simulation.Grill.listOfFood.size() < Simulation.Grill.capacityIn)){
								Simulation.Grill.listOfFood.wait();
							}
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, f, currentCustomer.getOrderNumber()));
							Simulation.Grill.makeFood(this, currentCustomer.getOrderNumber());
							Simulation.Grill.listOfFood.notifyAll();
						}
					}else if(f.equals(FoodType.fries)){
						synchronized (Simulation.Fryer.listOfFood) {
							while(!(Simulation.Fryer.listOfFood.size() < Simulation.Fryer.capacityIn)){
								Simulation.Fryer.listOfFood.wait();
							}
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, f, currentCustomer.getOrderNumber()));
							Simulation.Fryer.makeFood(this, currentCustomer.getOrderNumber());
							Simulation.Fryer.listOfFood.notifyAll();
						}
					}else if(f.equals(FoodType.coffee)){
						synchronized (Simulation.CoffeeMaker2000.listOfFood) {
							while(!(Simulation.CoffeeMaker2000.listOfFood.size() < Simulation.CoffeeMaker2000.capacityIn)){
								Simulation.CoffeeMaker2000.listOfFood.wait();
							}
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, f, currentCustomer.getOrderNumber()));
							Simulation.CoffeeMaker2000.makeFood(this, currentCustomer.getOrderNumber());
							Simulation.CoffeeMaker2000.listOfFood.notifyAll();
						}
					}
				}
				synchronized (foodCompleted) {
					while(!(foodCompleted.size() == currentCustomer.getOrderList().size())){
						foodCompleted.wait();
						foodCompleted.notifyAll();
					}
				}
				
				Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, currentCustomer.getOrderNumber()));
				synchronized (Simulation.ordersCompleted) {
					Simulation.ordersCompleted.put(currentCustomer, true);
					Simulation.ordersCompleted.notifyAll();
				}
				foodCompleted = new ArrayList<Food>();
			}
		}
		catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}