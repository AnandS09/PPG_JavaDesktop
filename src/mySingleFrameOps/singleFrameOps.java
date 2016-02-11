package mySingleFrameOps;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.*;

public class singleFrameOps {
	private static Mat img;
	private static Mat sub_img;

	private static int img_rows;
	private static int img_cols;
	
	public enum im_type{
		B_ONLY_1C, G_ONLY_1C, R_ONLY_1C,
		B_ONLY_3C, G_ONLY_3C, R_ONLY_3C,
		BG_ONLY, BR_ONLY, GR_ONLY
	}
	
	public singleFrameOps(){
		img = new Mat();
	}
	
	public singleFrameOps(Mat extern_img){
		img = extern_img;
		img_rows = img.rows();
		img_cols = img.cols();
	}
	
	private void update_vals(){
		img_rows = img.rows();
		img_cols = img.cols();
	}
	
	public Mat getSubFrame(Mat extern_img, int row_start, int row_end, int col_start, int col_end){
		img = extern_img;
		update_vals();
		if((row_end >= img_rows ) || (col_end >= img_cols)){
			System.err.println("Error : Image range out of bound");
			System.err.println("Max Rows = " + img_rows);
			System.err.println("Max Cols = " + img_cols);
			return img;
		}
		
		Range rowRange = new Range(row_start, row_end);
		Range colRange = new Range(col_start, col_end);
		
		sub_img = new Mat(img, rowRange, colRange);
		return sub_img;
	}
	
	// Use the following functions to convert and display image
	public static BufferedImage Mat2BufferedImage(Mat m){
		// source: http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
		// Fastest code
		// The output can be assigned either to a BufferedImage or to an Image

		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( m.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels()*m.cols()*m.rows();
		byte [] b = new byte[bufferSize];
		m.get(0,0,b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);  
		return image;

	}

	public static void displayImage(Image img2)
	{   
		ImageIcon icon=new ImageIcon(img2);
		JFrame frame=new JFrame();
		frame.setLayout(new FlowLayout());        
		frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
		JLabel lbl=new JLabel();
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public static void displayImage(Mat img_mat){
		Image img2 = Mat2BufferedImage(img_mat);
		ImageIcon icon=new ImageIcon(img2);
		JFrame frame=new JFrame();
		frame.setLayout(new FlowLayout());        
		frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
		JLabel lbl=new JLabel();
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void displayImage(){
		displayImage(img);
	}

	public Mat getColorComponent(Mat extern_img, im_type mode){
		// 0 -> Get blue component
		// 1 -> Get green component
		// 2 -> Get red component
		img = extern_img;
		update_vals();

		ArrayList<Mat> img_comp = new ArrayList<Mat>(3);
		Core.split(img, img_comp);

		Mat bComp = img_comp.get(0);
		Mat gComp = img_comp.get(1);
		Mat rComp = img_comp.get(2);

		Mat mRet = new Mat();
		Mat mMask = new Mat();

		switch (mode) {
		case B_ONLY_1C:
			return bComp;

		case G_ONLY_1C:
			return gComp;

		case R_ONLY_1C:
			return rComp;

		case B_ONLY_3C:
			mMask = Mat.zeros(bComp.size(),0);
			img_comp.set(1, mMask);
			img_comp.set(2, mMask);
			Core.merge(img_comp, mRet);
			return mRet;

		case G_ONLY_3C:
			mMask = Mat.zeros(bComp.size(),0);
			img_comp.set(0, mMask);
			img_comp.set(2, mMask);
			Core.merge(img_comp, mRet);
			return mRet;

		case R_ONLY_3C:
			mMask = Mat.zeros(bComp.size(),0);
			img_comp.set(0, mMask);
			img_comp.set(1, mMask);
			Core.merge(img_comp, mRet);
			return mRet;

		case BG_ONLY:
			mMask = Mat.zeros(bComp.size(),0);
			img_comp.set(2, mMask);
			Core.merge(img_comp, mRet);
			return mRet;

		case BR_ONLY:
			mMask = Mat.zeros(bComp.size(),0);
			img_comp.set(1, mMask);
			Core.merge(img_comp, mRet);
			return mRet;

		case GR_ONLY:
			mMask = Mat.zeros(bComp.size(),0);
			img_comp.set(0, mMask);
			Core.merge(img_comp, mRet);
			return mRet;

		default:
			System.out.println("Colour mode is not yet supported");
			break;
		}
		return img;
	}

	public byte[] Mat2Byte(){

		int total_bytes = img_cols * img_rows * img.channels();

		byte[] return_byte = new byte[total_bytes];
		img.get(0, 0, return_byte);

		return return_byte;
	}

	public byte[] Mat2Byte(Mat extern_img){
		img = extern_img;
		update_vals();
		int total_bytes = img_cols * img_rows * img.channels();

		byte[] return_byte = new byte[total_bytes];
		img.get(0, 0, return_byte);

		return return_byte;
	}

	//		public int[][] Mat2Int(){
	//			int [][]int_img = new int[img.rows()][img.cols()];
	//			try {
	//				PrintWriter wr = new PrintWriter("C:\\Users\\asamajda\\workspace\\FrameDiff\\out\\debug_test.txt");
	//						
	//			byte[] b_arr = Mat2Byte(img);
	//			
	//			for(int i = 0 ; i < img.rows(); i++){
	//				for (int j = 0 ; j < img.cols(); j++){
	//					int val =(int) b_arr[i*img.cols() + j];
	//					
	//					if(val < 0)
	//						val = 256 + val;
	//					
	//					int_img[i][j] = val;
	//					wr.println("Val = " + val + " byte equivalent = " + b_arr[i*img.cols() + j]);
	//				
	//				}
	//			}
	//			wr.close();
	//			} catch (FileNotFoundException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//			return int_img;
	//		}
	//		
	public int[][] Mat2Int(){
		int [][]int_img = new int[img.rows()][img.cols()];

		byte[] b_arr = Mat2Byte(img);

		for(int i = 0 ; i < img.rows(); i++){
			for (int j = 0 ; j < img.cols(); j++){
				int val =(int) b_arr[i*img.cols() + j];

				if(val < 0)
					val = 256 + val;

				int_img[i][j] = val;

			}
		}

		return int_img;
	}

	public int[][] Mat2Int(Mat extern_img){
		img = extern_img;
		update_vals();
		int [][]int_img = Mat2Int();

		return int_img;
	}

	public double getMatAvg(){
		double avg = 0.0;
		byte[] b_arr = Mat2Byte(img);

		for(int i =0 ; i < b_arr.length; i++){
			int val = (int)b_arr[i];

			if(val < 0)
				val = 256 + val;

			avg += val;
		}

		avg =(double) avg/b_arr.length;

		return avg;

	}

	public double getMatAvg(Mat extern_img){
		img = extern_img;
		update_vals();

		double val = getMatAvg();

		return val;
	}

	public int[][] getMatdiff(Mat img1, Mat img2){

		if((img1.rows() != img2.rows()) || (img1.cols() != img2.cols()))
		{
			System.err.println("Dimentions don't match for the inputs. Can't diff");
			int [][] dummy = {{0}};
			return dummy;
		}

		int [][] img1_arr = Mat2Int(img1);
		int [][] img2_arr = Mat2Int(img2);

		int[][] res_img_arr = new int[img1.rows()][img2.cols()];

		for(int i = 0; i < img1.rows(); i++)
			for (int j = 0; j < img1.cols(); j++) {
				res_img_arr[i][j] = img1_arr[i][j] - img2_arr[i][j];
				if(res_img_arr[i][j] < 0)
					res_img_arr[i][j] = -1 * res_img_arr[i][j];
			}

		return res_img_arr;
	}

	public Mat myImgExp(){
		int [][] img_arr = Mat2Int();
		double val_tmp = 0.0;

		for(int i = 0; i < img_rows; i++)
			for (int j = 0; j < img_cols; j++) {
				val_tmp = (double)img_arr[i][j] / 255;

				val_tmp = (double)Math.pow(255, val_tmp);
				img_arr[i][j] = (int) val_tmp;
				//System.out.println(val_tmp+ " ,");
			}

		img = Int2Mat(img_arr, img_rows, img_cols);
		return img;
	}

	public Mat myImgExp(Mat extern_img) {
		img = extern_img;
		update_vals();
		img = myImgExp();
		return img;
	}

	public Mat Int2Mat(int [][] array, int rows, int cols){
		Mat res = new Mat(rows, cols, CvType.CV_8UC1);

		byte [] b_arr = new byte[rows*cols];
		int k = 0;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				b_arr[k] = (byte) array[i][j];
				k++;
			}
		}

		res.put(0, 0, b_arr);

		return res;
	}

	public void writeFrameToFile(Mat extern_img, String fileName){
		try {
			PrintWriter wr = new PrintWriter(fileName);
			int [][] img_arr = Mat2Int(extern_img);

			for (int i = 0; i < extern_img.rows(); i++) {
				for (int j = 0; j < extern_img.cols(); j++) {
					wr.print(img_arr[i][j] + ", ");
				}
				wr.println();
			}
			wr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("File "+ fileName +" could not be opened!!");
			e.printStackTrace();

		}

	}
}

