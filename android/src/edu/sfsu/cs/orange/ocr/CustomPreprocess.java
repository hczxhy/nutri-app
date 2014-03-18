package edu.sfsu.cs.orange.ocr;

import android.graphics.Bitmap;
import android.graphics.Color;

public class CustomPreprocess {

	public static byte[] createBinaryImage(Bitmap bm) {
		int[] pixels = new int[bm.getWidth() * bm.getHeight()];
		bm.getPixels(pixels, 0, bm.getWidth(), 0, 0, bm.getWidth(),
				bm.getHeight());
		int w = bm.getWidth();

		// Calculate overall lightness of image
		long gLightness = 0;
		int lLightness;
		int c;
		int size = bm.getWidth() * bm.getHeight();
		for (int i = 0; i < size; i++) {
			c = pixels[i];

			lLightness = ((c & 0x00FF0000) >> 16) + ((c & 0x0000FF00) >> 8)
					+ (c & 0x000000FF);
			pixels[i] = lLightness;
			gLightness += lLightness;
		}
		gLightness /= bm.getWidth() * bm.getHeight();
		gLightness = gLightness * 5 / 6;

		// Extract features
		byte[] binaryImage = new byte[size];

		for (int i = 0; i < size; i++)
			binaryImage[i] = (byte) ((pixels[i] <= gLightness) ? 1 : 0);

		return binaryImage;
	}

	public static Bitmap createBinaryBitmap(Bitmap bmpOriginal) {
		int width, height, threshold;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		threshold = 127;
		Bitmap bmpBinary = Bitmap.createBitmap(bmpOriginal);

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get one pixel color
				int pixel = bmpOriginal.getPixel(x, y);
				int red = Color.red(pixel);
				int gray = (int)(Color.red(pixel)*0.3+Color.green(pixel)*0.59+Color.blue(pixel)*0.11);

				// get binary value
				if (gray < threshold) {
					bmpBinary.setPixel(x, y, 0xFF000000);
				} else {
					bmpBinary.setPixel(x, y, 0xFFFFFFFF);
				}

			}
		}
		return bmpBinary;
	}
}
