package p0;

import java.text.DecimalFormat;
//import java.util.LinkedList;
import java.util.*;

/**
 * This class runs <code>numThreads</code> instances of
 * <code>ParallelMaximizerWorker</code> in parallel to find the maximum
 * <code>Integer</code> in a <code>LinkedList</code>.
 */
public class ParallelAverage {
	
	private static DecimalFormat form = new DecimalFormat(".##");
	

	ArrayList<ParallelAverageWorker> workers; // = new ArrayList<ParallelMaximizerWorker>(numThreads);

	public ParallelAverage(int numThreads) {
		workers = new ArrayList<ParallelAverageWorker>(numThreads);
	}
	
	public static void main(String[] args) {
		for(int n=1; n<11; n++){
		System.out.println("\n-------------------------------- Time : "+n+" --------------------------------\n");
			
		int numThreads = 4; // number of threads for the maximizer
		int numElements = 10000; // number of integers in the list
		
		ParallelAverage average = new ParallelAverage(numThreads);
		LinkedList<Integer> list = new LinkedList<Integer>();
		
			// populate the list
		// TODO: change this implementation to test accordingly
		for (int i=0; i<numElements; i++) 
			list.add(i);

		// run the maximizer
		try {
		System.out.println("Walking average of given list = "+form.format(average.avg(list,numThreads))+"\n");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
	}
	
	/**
	 * Finds the maximum by using <code>numThreads</code> instances of
	 * <code>ParallelMaximizerWorker</code> to find partial maximums and then
	 * combining the results.
	 * @param list <code>LinkedList</code> containing <code>Integers</code>
	 * @return Maximum element in the <code>LinkedList</code>
	 * @throws InterruptedException
	 */
	public double avg(LinkedList<Integer> list, int numThreads) throws InterruptedException {
		
		int count = 0;
		double avg = 0; // initialize max as lowest value
		
		//System.out.println("max list "+list);
		// run numThreads instances of ParallelMaximizerWorker
		for (int i=0; i < numThreads; i++) {
			workers.add(i, new ParallelAverageWorker(list));
			workers.get(i).start();
		}
		// wait for threads to finish
		for (int i=0; i<numThreads; i++)
			workers.get(i).join();
		

//		for (int i=0; i<numThreads; i++){
//		if(workers.get(i).getPartialAvg()>0){
//			avg = workers.get(i).getPartialAvg();
//			break;
//		}
//		}
		for (int i=0; i<numThreads; i++){
			System.out.println("for Thread-"+i+" partial average : "+form.format(workers.get(i).getPartialAvg()));
				if(workers.get(i).getPartialAvg() > 0)
				{
					count++;
					avg = (avg + workers.get(i).getPartialAvg());
				}
		}
		
//		}	
		return (avg/count);
	}
}
