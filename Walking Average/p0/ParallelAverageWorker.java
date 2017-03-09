package p0;

import java.util.LinkedList;

/**
 * Given a <code>LinkedList</code>, this class will find the maximum over a
 * subset of its <code>Integers</code>.
 */
public class ParallelAverageWorker extends Thread {

	protected LinkedList<Integer> list;
	protected double partialAvg = 0; // initialize to lowest value
	
	public ParallelAverageWorker(LinkedList<Integer> list) {
		this.list = list;
			
	}
	
	/**
	 * Update <code>partialMax</code> until the list is exhausted.
	 */
	public void run() {
		
		//System.out.println("starting thread : " + this.getName());
		
		while (true) {
			int number;
			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized(list) {
				if (list.isEmpty()){
					return;
				}// list is empty
				number = list.remove();
			}
			// update partialMax according to new value
			// TODO: IMPLEMENT CODE HERE
			partialAvg = (partialAvg + number)/2;
			
		}
	}
	
	public double getPartialAvg() {
		return partialAvg;
	}

}
