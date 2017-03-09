package p0;

import static org.junit.Assert.*;

import java.text.DecimalFormat;
import java.util.*;


import org.junit.Test;

import p0.ParallelAverage;

public class PublicTest {

	private static DecimalFormat form = new DecimalFormat(".##");

	private int	threadCount = 100000; // number of threads to run
	private ParallelAverage average = new ParallelAverage(threadCount);
	
	@Test
	public void compareMax() {
		int size = 100; // size of list
		LinkedList<Integer> list = new LinkedList<Integer>();
		Random rand = new Random();
		double serialAvg = 0;
		double parallelAvg = 0;
		// populate list with random elements
		for (int i=0; i<size; i++) {
			int next = rand.nextInt();
			if(next>0){
			list.add(next);
			
//			if(i==0){
//				serialAvg = next;
//			}
			
			serialAvg = (serialAvg + next)/2; // compute serialMax
			}
			else{
				i--;
			}
			
		}
		// try to find parallelMax
		try {
//			System.out.println(list);
			parallelAvg = average.avg(list,4);
//			parallelAvg = Math.round(parallelAvg);
//			serialAvg = Math.round(serialAvg);
			System.out.println("serialMax= "+ form.format(serialAvg));
			System.out.println("parallelMax = " +form.format(parallelAvg));
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("The test failed because the max procedure was interrupted unexpectedly.");
		} catch (Exception e) {
			e.printStackTrace();
			fail("The test failed because the max procedure encountered a runtime error: " + e.getMessage());
		}
		
		assertEquals("The serial max doesn't match the parallel max", serialAvg, parallelAvg,1e-15);
	}
}
