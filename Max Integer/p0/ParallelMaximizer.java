package p0;

//import java.util.LinkedList;
import java.util.*;

/**
 * This class runs <code>numThreads</code> instances of
 * <code>ParallelMaximizerWorker</code> in parallel to find the maximum
 * <code>Integer</code> in a <code>LinkedList</code>.
 */
public class ParallelMaximizer {
	
	ArrayList<ParallelMaximizerWorker> workers; // = new ArrayList<ParallelMaximizerWorker>(numThreads);
//	LinkedList<Integer> threadMax = new LinkedList<Integer>();
//	int i = 0;
//	int max = Integer.MIN_VALUE; // initialize max as lowest value

	public ParallelMaximizer(int numThreads) {
		workers = new ArrayList<ParallelMaximizerWorker>(numThreads);
	}
	
	public static void main(String[] args) {
		int numThreads = 4; // number of threads for the maximizer
		int numElements = 10000; // number of integers in the list
		
		ParallelMaximizer maximizer = new ParallelMaximizer(numThreads);
		LinkedList<Integer> list = new LinkedList<Integer>();
		
		// populate the list
		// TODO: change this implementation to test accordingly
		for (int i=0; i<numElements; i++) 
			list.add(i);

		// run the maximizer
		try {
		System.out.println("max = "+maximizer.max(list,numThreads));
		} catch (InterruptedException e) {
			e.printStackTrace();
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
	public int max(LinkedList<Integer> list, int numThreads) throws InterruptedException {
//		if(i!=2){
		int max = Integer.MIN_VALUE; // initialize max as lowest value
		
		//System.out.println("max list "+list);
		// run numThreads instances of ParallelMaximizerWorker
		for (int i=0; i < numThreads; i++) {
			workers.add(i, new ParallelMaximizerWorker(list));
			workers.get(i).start();
		}
		// wait for threads to finish
		for (int i=0; i<numThreads; i++)
			workers.get(i).join();
		
//		for(int i=0;i<numThreads;i++)
//			System.out.println(workers.get(i).getPartialMax());
		
		// take the highest of the partial maximums
		// TODO: IMPLEMENT CODE HERE
		
		
//		for (int i=0; i<numThreads; i++){
//			System.out.println("for i = "+i+" : "+workers.get(i).getPartialMax());
//			threadMax.add(workers.get(i).getPartialMax());
//		}
//		i++;
//		max = max(threadMax,4);
//		System.out.println("threadMax" + threadMax);
		for (int i=0; i<numThreads; i++){
			System.out.println("for i = "+i+" : "+workers.get(i).getPartialMax());
				if(max < workers.get(i).getPartialMax())
				max = workers.get(i).getPartialMax();
		}
//		}	
		return max;
	}
}
