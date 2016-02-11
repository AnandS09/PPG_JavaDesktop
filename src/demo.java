import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Stack;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import fftLib.*;
import mySingleFrameOps.*;
import mySingleFrameOps.singleFrameOps.im_type;
import myQueueOps.*;


public class demo {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		myDemo();
		//myTest();
	
	}
	
	public static void myTest(){
		my2dDoubleQueueOps Q = new my2dDoubleQueueOps();
		double [][] arrQ = new double[10][2];
		
		double [][]elem = new double [1][2];
		for (int i = 0; i < 10; i ++){
					
			elem[0][0] = i;
			elem[0][1] = 10 - i;
			
			Q.Qpush(elem);
		}
		Q.Qtake();
		
		elem[0][0] = 0;
		elem[0][1] = 0;
		
		Q.Qpush(elem);
		
		arrQ = Q.toArray();
		
		System.out.println(Arrays.deepToString(arrQ));
 	}
	
	public static void myDemo() {
		Mat myFrame = new Mat();
		Mat myRedframe = new Mat();
		singleFrameOps op = new singleFrameOps();
		fftLib 	   		f = new fftLib();
		my2dDoubleQueueOps imQ = new my2dDoubleQueueOps();
		
		int points = 1024;
		
		double [][] tmp = new double[1][2];
		double [][] sample_arr = new double[points][2];
		double [] freq_arr = new double[points];
		
//		String vidFile = "C:\\Users\\asamajda\\workspace\\PPG_JavaSystem\\res\\vid_mycam2.mp4";
		String vidFile = "C:\\Users\\asamajda\\workspace\\PPG_JavaSystem\\res\\vid_76bpm.mp4";
		
		VideoCapture cap = new VideoCapture();
		cap.open(vidFile);
		
		if(! cap.isOpened()){
			System.err.println("Error : Problem opening the video file!\n Closing the Program");
			return;
		}
		
		int count = 0;
		int frame_ctr = 0;
		
		double fps = 0.0;
		double max_frames = 0;
		
		fps = cap.get(5);
		max_frames = cap.get(7);
		
		System.out.println("Frame Rate = " + fps);
		System.out.println("Max Frames = " + max_frames);
		try {
			PrintWriter wr = new PrintWriter(new FileOutputStream(new File("C:\\Users\\asamajda\\workspace\\PPG_JavaSystem\\out\\fft_out.csv"),true));
			PrintWriter wr_data = new PrintWriter(new FileOutputStream(new File("C:\\Users\\asamajda\\workspace\\PPG_JavaSystem\\out\\data_points.csv"),true));
			
			double [] avg_q = {0,0,0,0,0,0,0,0};
		do{
			cap.read(myFrame);
			myRedframe = op.getColorComponent(myFrame, im_type.R_ONLY_1C);
			//double avg = op.getMatAvg(myRedframe);
			
			double avg = Core.mean(myRedframe).val[0];
			
			wr_data.append(avg + "\n");
			
			tmp[0][0] = (double) avg;
			tmp[0][1] = 0.0;
			
			if(frame_ctr >= points){
				sample_arr = imQ.toArray();
				//System.out.println("Debug: Input size = " + sample_arr.length);
				freq_arr = f.fft_energy_squared(sample_arr, points);
				
				wr.append(Arrays.toString(freq_arr));
				wr.append("\n");
				
				//Apply a filter
				int deno  =(int) (2 * fps);
				int n_min = points/deno;	  //start of the filter window
				int n_max = 6 * n_min;		  //end of the filter window
				
				double max = freq_arr[n_min];
				int pos    = n_min;
				for(int i = n_min + 1; i <= n_max; i++ ){
					if(freq_arr[i] > max){
						max = freq_arr[i];
						pos = i;
					}
				}
				
				//System.out.println("DEBUG : n_min = " + n_min + " n_max = " + n_max);
				//System.out.println("DEBUG : The position is : " + pos);
				
				double bps = (pos * fps) / points;	//Calculate the freq
				double bpm = (double) 60.0 * bps;				//Calculate bpm
				long bpm_int = Math.round(bpm);
				System.out.println("The BPM heartbeat is = " + bpm_int + " Data point = " + pos);
				imQ.Qtake();
				//System.out.println("Debug: frame_ctr = "+ frame_ctr + " count =" + count);
			}
			
			imQ.Qpush(tmp);
			
			frame_ctr ++;
			count ++;
			//System.out.println("frame # = " + frame_ctr);
		}while(count < (max_frames - 1)); //Limit it to the end of file
		
		wr.close();
		wr_data.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
