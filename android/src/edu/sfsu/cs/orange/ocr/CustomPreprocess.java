package edu.sfsu.cs.orange.ocr;

import android.graphics.Bitmap;

public class CustomPreprocess {

	public static byte[] createBinaryImage( Bitmap bm )
	{
	    int[] pixels = new int[bm.getWidth()*bm.getHeight()];
	    bm.getPixels( pixels, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight() );
	    int w = bm.getWidth();

	    // Calculate overall lightness of image
	    long gLightness = 0;
	    int lLightness;
	    int c;
	    int size = bm.getWidth()*bm.getHeight();
	    for (int i = 0; i < size; i++) {
	      c = pixels[i];

	         lLightness = ((c&0x00FF0000 )>>16) + ((c & 0x0000FF00 )>>8) + (c&0x000000FF);
	         pixels[i] = lLightness;
	         gLightness += lLightness;
	    }
	    gLightness /= bm.getWidth() * bm.getHeight();
	    gLightness = gLightness * 5 / 6;

	    // Extract features
	    byte[] binaryImage = new byte[size];

	    for ( int i = 0; i < size; i++ )
	            binaryImage[i] = (byte)((pixels[i] <= gLightness)?1:0);

	    return binaryImage;
	}
}
