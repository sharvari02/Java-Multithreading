package p2;

import java.util.ArrayList;
import java.util.List;

import p2.SimulationEvent;


/**
 * Validates a simulation
 */
public class Validate {
	private static class InvalidSimulationException extends Exception {
		public InvalidSimulationException() { }
	};

	// Helper method for validating the simulation
	private static boolean check(boolean check,
			String message) throws InvalidSimulationException {
		if (!check) {
			System.err.println("SIMULATION INVALID : "+message);
			throw new Validate.InvalidSimulationException();
		}
		return true;
	}

	/** 
	 * Validates the given list of events is a valid simulation.
	 * Returns true if the simulation is valid, false otherwise.
	 *
	 * @param events - a list of events generated by the simulation
	 *   in the order they were generated.
	 *
	 * @returns res - whether the simulation was valid or not
	 */
	public static boolean validateSimulation(List<SimulationEvent> events, int numCooks, int numCustomers,int numTables, int machineCapacity) {
		try {

			/* In P2 you will write validation code for things such as:
				Should not have more eaters than specified +
				Should not have more cooks than specified +
				The coffee shop capacity should not be exceeded +
				The capacity of each machine should not be exceeded +
				Eater should not receive order until cook completes it
				Eater should not leave coffee shop until order is received +
				Eater should not place more than one order +
				Cook should not work on order before it is placed +
			 */
			
			boolean error = true;
			
			error = check(events.get(0).event == SimulationEvent.EventType.SimulationStarting,
					"Simulation didn't start with initiation event") && error; // && with at least 1 false will result false
			
			error = check(events.get(events.size()-1).event == 
					SimulationEvent.EventType.SimulationEnded,
					"Simulation didn't end with termination event") && error;
			
			error = check(checkCustomers(events, numCustomers),"Customers Are More Than Defined") && error;
			
			error = check(checkCooks(events, numCooks),"Cooks Are More Than Defined") && error;
			
			error = check(checkMachineCapacity(events, machineCapacity),"Customer has placed more than 1 order") && error;
			
			error = check(customerMoreThanCapacity(events, numTables),"Customers In Coffee Shop Exceeds Max Capacity") && error;
			
			error = check(orderBeforeEntering(events),"") && error;
			
			error = check(oneOrderPerCustomer(events),"Customer has placed more than 1 order") && error;
			
			error = check(orderReceivedBeforPlacing(events),"Cook Received Order Before Placing It") && error;
			
			error = check(foodStartedBeforeRecevingOrder(events),"Cook Started Food Before Receiving Order ") && error;
			
			error = check(machineStartsBeforeCook(events),"Machine Started Befor Cook Places The Order ") && error;
			
			error = check(machineCompletesBeforeFoodIsFinishedCooking(events),"Machine Completes Food Befor Food Is Ready ") && error;
			
			error = check(ordersPending(events),"Some Orders Are Still Pending") && error;
			
			error = check(customerLeavesBeforeFinishing(events),"Customer Left Before Finishing The Order") && error;
			
//			error = check(customerMoreThanCapacity(events, numTables),"Customers in Coffee Shop Exceeds Max Capacity") && error;
			

			return error;
		} catch (InvalidSimulationException e) {
			return false;
		}
	}
	
	public static boolean checkCustomers(List<SimulationEvent> events, int numCustomers){
		int totalCust = 0;
		for(SimulationEvent s: events){
			if(s.event == SimulationEvent.EventType.CustomerStarting)
				totalCust++;
		}
		if(totalCust > numCustomers)
			return false;
		
		return true;
	}
	
	public static boolean checkCooks(List<SimulationEvent> events, int numCooks){
		int totalCooks = 0;
		for(SimulationEvent s: events){
			if(s.event == SimulationEvent.EventType.CookStarting)
				totalCooks++;
		}
		if(totalCooks > numCooks)
			return false;
		
		return true;
	}
	
	public static boolean checkMachineCapacity(List<SimulationEvent> events, int machineCapacity) throws InvalidSimulationException{
		int capGrill = 0;
		int capFryer = 0;
		int capCoffeeMaker2000 = 0;
		boolean isGrill = true;
		boolean isFryer = true;
		boolean isCoffeeMaker2000 = true;
		for(SimulationEvent s: events){
			if(s.event == SimulationEvent.EventType.MachineStartingFood){
				if(s.food == FoodType.burger){
					capGrill++;
				}else if(s.food == FoodType.coffee){
					capCoffeeMaker2000++;
				}else if(s.food == FoodType.fries){
					capFryer++;
					
				}
			}
			else if(s.event == SimulationEvent.EventType.MachineDoneFood){
				if(s.machine.machineFoodType == FoodType.burger){
					capGrill--;
				}else if(s.machine.machineFoodType == FoodType.coffee){
					capCoffeeMaker2000--;
				}else if(s.machine.machineFoodType == FoodType.fries){
					capFryer--;
				}
			}
			if(capGrill > machineCapacity){
				isGrill = false;
				return check(isGrill, "Grill Exceeded");
			}
			if(capCoffeeMaker2000 > machineCapacity){
				isCoffeeMaker2000 = false;
				return check(isCoffeeMaker2000, "CoffeeMaker Exceeded");
			}
			if(capFryer > machineCapacity){
				isFryer = false;
				return check(isFryer, "Fryer Exceeded");
			}
		}
		return true;
	}
	
	public static boolean oneOrderPerCustomer(List<SimulationEvent> events){
		List<String> cust = new ArrayList<String>();
		for(SimulationEvent s: events){
			if(s.event == SimulationEvent.EventType.CustomerPlacedOrder){
				if(cust.contains(s.customer.toString()))
					return false;
				cust.add(s.customer.toString());
			}
		}
		return true;
				
	}
	
	public static boolean customerMoreThanCapacity(List<SimulationEvent> events, int numTables){
		int customersIn = 0;
		for(SimulationEvent s: events){
			if(s.event == SimulationEvent.EventType.CustomerEnteredCoffeeShop)
				customersIn++;
			
			if(s.event == SimulationEvent.EventType.CustomerLeavingCoffeeShop)
				customersIn--;
			
			if(customersIn > numTables)
				return false;
		}
		return true;
	}
	
	public static boolean orderBeforeEntering(List<SimulationEvent> events){
		List<String> customerEntered = new ArrayList<String>();
		for(SimulationEvent s: events){
			if(s.event == SimulationEvent.EventType.CustomerEnteredCoffeeShop)
				customerEntered.add(s.customer.toString());
			
			if(s.event == SimulationEvent.EventType.CustomerPlacedOrder){
				if(! (customerEntered.contains(s.customer.toString()))){
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean orderReceivedBeforPlacing(List<SimulationEvent> events){
		List<Integer> customerPlacedOrder = new ArrayList<Integer>();
		for(SimulationEvent s: events){
			if(s.event == SimulationEvent.EventType.CustomerPlacedOrder){
				customerPlacedOrder.add(s.orderNumber);
			}
			if(s.event == SimulationEvent.EventType.CookReceivedOrder){
				if(!(customerPlacedOrder.contains(s.orderNumber)))
					return false;
			}
		}
		
		return true;
	}
	
	public static boolean foodStartedBeforeRecevingOrder(List<SimulationEvent> events){
		List<Integer> ordersReceived = new ArrayList<Integer>();
		for(SimulationEvent s: events){
			if(s.event == SimulationEvent.EventType.CookReceivedOrder)
				ordersReceived.add(s.orderNumber);
			
			if(s.event == SimulationEvent.EventType.CookStartedFood){
				if(!(ordersReceived.contains(s.orderNumber)))
					return false;
			}
		}
		
		return true;
	}
	
	public static boolean machineStartsBeforeCook(List<SimulationEvent> events){
		int burgerCount = 0;
		int friesCount = 0;
		int coffeeCount = 0;
		
		for(SimulationEvent s: events){
			if(s.event == SimulationEvent.EventType.CookStartedFood){
				if(s.food == FoodType.burger){
					burgerCount++;
				}else if(s.food == FoodType.fries){
					friesCount++;
				}else if(s.food == FoodType.coffee){
					coffeeCount++;
				} 
			}else if(s.event == SimulationEvent.EventType.CookFinishedFood){
				if(s.food == FoodType.burger){
					burgerCount--;
				}else if(s.food == FoodType.fries){
					friesCount--;
				}else if(s.food == FoodType.coffee){
					coffeeCount--;
				} 
			}
			
			if(burgerCount < 0 || friesCount < 0 || coffeeCount < 0)
				return false;
		}
		return true;
	}
	
	public static boolean machineCompletesBeforeFoodIsFinishedCooking(List<SimulationEvent> events){
		int burgerCount = 0;
		int friesCount = 0;
		int coffeeCount = 0;
		
		for(SimulationEvent s: events){
			if(s.event == SimulationEvent.EventType.MachineStartingFood){
				if(s.food == FoodType.burger){
					burgerCount++;
				}else if(s.food == FoodType.fries){
					friesCount++;
				}else if(s.food == FoodType.coffee){
					coffeeCount++;
				} 
			}else if(s.event == SimulationEvent.EventType.MachineDoneFood){
				if(s.food == FoodType.burger){
					burgerCount--;
				}else if(s.food == FoodType.fries){
					friesCount--;
				}else if(s.food == FoodType.coffee){
					coffeeCount--;
				} 
			}
			
			if(burgerCount < 0 || friesCount < 0 || coffeeCount < 0)
				return false;
		}
		return true;
	}
	
	public static boolean ordersPending(List<SimulationEvent> events){
		int numOrders = 0;
		for(SimulationEvent s: events){
			if(s.event == SimulationEvent.EventType.CookReceivedOrder)
				numOrders++;
			if(s.event == SimulationEvent.EventType.CookCompletedOrder)
				numOrders--;
		}
		if(numOrders != 0)
			return false;
		
		return true;
	}
	public static boolean customerLeavesBeforeFinishing(List<SimulationEvent> events){
		List<String> customerReceivedOrders = new ArrayList<String>();
		for(SimulationEvent s: events){
			if(s.event == SimulationEvent.EventType.CustomerReceivedOrder)
				customerReceivedOrders.add(s.customer.toString());
			
			if(s.event == SimulationEvent.EventType.CustomerLeavingCoffeeShop){
				if(!(customerReceivedOrders.contains(s.customer.toString())))
					return false;
			}
		}
		return true;
	}
}
