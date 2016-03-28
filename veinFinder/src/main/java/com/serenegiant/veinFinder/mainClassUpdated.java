package com.serenegiant.veinFinder;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
//import org.opencv.highgui.Highgui;

//import org.opencv.imgcodecs; // imread, imwrite, etc
import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.videoio;   // VideoCapture


@SuppressWarnings("unused")
public class mainClassUpdated
{
	static final double ISDEGCHOSENFLAG = 10;
	static final int TRESHOLD = 1;

	public static Bitmap processImage(Bitmap origBit) //TODO need to add arguments
	{
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//		FIXME DO NOT LOAD DIRECTLY THIS LIB = System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//		String imgtest = "javetest_2_10_10.jpg";
		Mat orig_img = new Mat (origBit.getWidth(), origBit.getHeight(), CvType.CV_8UC1);// = Imgcodecs.imread(imgtest,0);
		Utils.bitmapToMat(origBit, orig_img);
		Mat img = MyCLAHE.apply(orig_img); //need to be improved
//		boolean bool = Imgcodecs.imwrite("img_grey_java.jpg", img);
		/*
		Size size = img.size();
		Mat veinMat = new Mat(size, CvType.CV_8UC1); // u or s 
		Mat directMat = new Mat(size, CvType.CV_8UC1);
		int numOfDirection = 4;
		int d = 20;
		double[] tempAndColor;
		for (int row = 0; row<size.height; row++) // i is num of line, j is num of col
		{
			for (int col = 0; col<size.width; col++)
			{
				tempAndColor = checkPixelIsVein(img, row, col, numOfDirection, d);
	*/
				/*if (!(temp == ISDEGCHOSENFLAG))
				{
					veinMat.put(row, col, color);
					directMat.put(row, col, (byte) temp);
				
				}*/
		/*
				veinMat.put(row, col, tempAndColor[1]);
				directMat.put(row, col, tempAndColor[0]);
				if (tempAndColor[1] != 0)
				{
				//	System.out.println(row);
				//	System.out.println(col);
				}
			}
		}
		*/

		Bitmap outBit = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(img, outBit);
		return outBit;

		/*
		Bitmap outBit = Bitmap.createBitmap(veinMat.cols(), veinMat.rows(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(veinMat, outBit);
		return outBit;
		*/
	}

	public static double[] getMinMax(Mat mat)
	{
		double max = mat.get(0, 0)[0];
		double min = max;
		double imin = 0;
		double temp = 0;
		for(int i = 0; i<mat.size().height; i++)
		{
			temp = mat.get(i, 0)[0];
			if (temp > max)
			{
				max = temp;
			}
			if (temp < min)
			{
				imin = (double) i;
				min = temp;
			}
		}
		return new double[] {max, min, imin};
	}
	
	//Some deutch voodoo
	public static double checkProfile(Mat profile)
	{
		int C = (int) profile.size().height/2;
		
		//System.out.println(profile.dump());
		//System.out.println(profile.submat(0, C, 0, 1).dump());
		//System.out.println(profile.submat(C, (int) profile.size().height, 0,1).dump());

		double[] L = getMinMax(profile.submat(0, C, 0, 1));
		double L_max = L[0];
		double L_min = L[1];
		double L_imin = L[2];
		double[] R = getMinMax(profile.submat(C, (int) profile.size().height, 0,1));
		double R_max = R[0];
		double R_min = R[1];
		double R_imin = R[2] + C;
		/*
		System.out.println(Arrays.toString(L));
		System.out.println(Arrays.toString(R));
		*/
		double V_avg = (L_max + R_max)/2;
		double V_min, V_min_i;
		if (L_min < R_min)
		{
			V_min_i = L_imin;
			V_min = L_min;
		}
		else
		{
			V_min_i = R_imin;
			V_min = R_min;
		}
		
		/*
		
		System.out.println("R_max: "+R_max);
		System.out.println("L_max: "+L_max);
		System.out.println("V_avg: "+V_avg);
		System.out.println("v_min_i: "+V_min_i);
		System.out.println("C: "+C);
		
		*/
		
		if (Math.abs(L_max-R_max) < V_avg && Math.abs(V_min_i - C) < TRESHOLD)
		{
			return (V_avg - V_min);
		}
		else
		{
			return -1;
		}
	}
	
	
	public static Mat myimprofile(Mat img, double start_row, double end_row, double start_col, double end_col, int d)
	{
		double stepX = (end_row-start_row)/(d); // not sure if 2d or d like in omer's
		double stepY = (end_col-start_col)/(d);
		Mat profile = Mat.zeros(d, 1, CvType.CV_8UC1); // first rows than cols
		int x,y,putt;
		
		//System.out.println("stepX: "+stepX);
		//System.out.println("stepY: "+stepY);
		
		Size imgSize = img.size();
		for(int i = 1; i <= d; i++) 
		{
			//x= (int) Math.floor(start_row+i*stepX-1);
			//y= (int) Math.floor(start_col+i*stepY-1);
			x= (int) Math.floor(start_row+i*stepX);
			y= (int) Math.floor(start_col+i*stepY);
			if (x >= 0 && y >= 0 && x < imgSize.height && y < imgSize.width)
			{
			
				putt = (int) img.get(x,y)[0];
				/*if (start_row == 199 && start_col == 199)
						{
					//System.out.println(i);
					//System.out.println(y);
					//System.out.println(putt);
						}*/
				profile.put(i-1,0, putt);
				//System.out.println(x+" , "+y+ " i: "+ i);
				
			}

		}
		return profile;
	}
	
	public static double[] checkPixelIsVein(Mat img, int row, int col, int numOfDirection, int d)
	{
		double d_degree = Math.PI/numOfDirection;
		boolean isDegChosen = false;
		double deg_pixel_val = 0;
		double chosenDeg = 0;
		double chosenRating = 0;
		double degree, rating;
		double start_row, start_col, final_row, final_col;
		double double_row = (double) row; //num of row
		double double_col = (double) col;// num of col
		double double_d = (double) d;
		for(int i = 1; i<=(numOfDirection); i++)
		{
			degree = d_degree * i;
			start_row = (double_row - double_d*Math.sin(degree)); //tsabari used (int)
			start_col = (double_col - double_d*Math.cos(degree));
			final_row = (double_row + double_d*Math.sin(degree));
			final_col = (double_col + double_d*Math.cos(degree));
			
			//System.out.println(double_col);
			
			/*
			System.out.println(start_row);
			System.out.println(final_row);
			System.out.println(start_col);
			System.out.println(final_col);
			*/
			Mat profile = myimprofile(img, start_row, final_row, start_col, final_col, d);
			//System.out.println(profile.dump());
			rating = checkProfile(profile);
			if (rating != -1)
			{
				deg_pixel_val = 255;
				if (!isDegChosen)
				{
					isDegChosen = true;
					chosenDeg = degree;
					chosenRating = rating;
			
					
				}
				else
				{
					if(rating > chosenRating)
					{
						chosenDeg = degree;
						chosenRating = rating;
					}
				}
			}
		}
		if (!isDegChosen)
		{
			chosenDeg = ISDEGCHOSENFLAG;
			deg_pixel_val = 0;
		}
		return new double[] {chosenDeg, deg_pixel_val};
	}
}
