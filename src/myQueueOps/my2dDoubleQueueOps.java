package myQueueOps;

import java.util.Stack;

public class my2dDoubleQueueOps {
	private Stack<double [][]> queue;
	private int myQsize;
	
	public my2dDoubleQueueOps(){
		queue = new Stack<double [][]>();
	}
	
	public void Qpush(double [][]elem){
		//The local_elem variable is very important here.
		//If directly elem is used, Java links it to the original variable used in the function call
		//Thus changing that variable in the program elsewhere changes the contents in the queue.
		//I need to search web for more info about why this happens.
		double [][] local_elem = new double[1][2];
		
		local_elem[0][0] = elem[0][0];
		local_elem[0][1] = elem[0][1];
		
 		queue.push(local_elem);
		myQsize = queue.size();
	}
	
	public double [][] Qtake(){
		double [][] pop = new double[1][2];
		pop = queue.get(0);
		queue.remove(0);
		myQsize = queue.size();
		
		return pop;
	}
	
	public double [][] Qpeek(int index){
		
		if(index > queue.size()){
			System.err.println("ERROR : Invalid index, Out of Bounds");
			return null;
		}
		
		double [][] pop = new double[1][2];
		pop = queue.get(index);
		
		return pop;
	}
	
	public double [][] toArray(){
		double [][] arr = new double [myQsize][2];
		
		for(int i = 0 ; i < myQsize; i++){
			double [][] pop = new double[1][2];
			pop = queue.get(i);
			
			//System.out.println(" I = " + i + " Val = " + pop[0][0] +", " + pop[0][1]);
			
			arr[i][0] = pop[0][0];
			arr[i][1] = pop[0][1];
		}
		
		return arr;
	}
}
