package com.serenegiant.veinFinder;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.CLAHE;
import org.opencv.core.Core;
import org.opencv.imgcodecs.Imgcodecs;
import static java.lang.System.out;

public class MyCLAHE 
{
	public static Mat apply(Mat img)
	{
		Mat dest_img = new Mat();
		CLAHE c = Imgproc.createCLAHE();
		c.setClipLimit(2);
		c.setTilesGridSize(new Size(10, 10));
		c.apply(img, dest_img);
		return dest_img;
	}
	
}
