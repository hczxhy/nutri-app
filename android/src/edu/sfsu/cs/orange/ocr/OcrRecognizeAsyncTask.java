/*
 * Copyright 2011 Robert Theis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sfsu.cs.orange.ocr;

import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;
import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.googlecode.tesseract.android.TessBaseAPI.PageIteratorLevel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Class to send OCR requests to the OCR engine in a separate thread, send a success/failure message,
 * and dismiss the indeterminate progress dialog box. Used for non-continuous mode OCR only.
 */
final class OcrRecognizeAsyncTask extends AsyncTask<Void, Void, Boolean> {

  //  private static final boolean PERFORM_FISHER_THRESHOLDING = false; 
  //  private static final boolean PERFORM_OTSU_THRESHOLDING = false; 
  //  private static final boolean PERFORM_SOBEL_THRESHOLDING = false; 

  private CaptureActivity activity;
  private TessBaseAPI baseApi;
  private byte[] data;
  private int width;
  private int height;
  private OcrResult ocrResult;
  private long timeRequired;

  OcrRecognizeAsyncTask(CaptureActivity activity, TessBaseAPI baseApi, byte[] data, int width, int height) {
    this.activity = activity;
    this.baseApi = baseApi;
    this.data = data;
    this.width = width;
    this.height = height;
  }

  @Override
  protected Boolean doInBackground(Void... arg0) {
    long start = System.currentTimeMillis();
    Bitmap bitmap = activity.getCameraManager().buildLuminanceSource(data, width, height).renderCroppedGreyscaleBitmap();
    String textResult;

    //      if (PERFORM_FISHER_THRESHOLDING) {
    //        Pix thresholdedImage = Thresholder.fisherAdaptiveThreshold(ReadFile.readBitmap(bitmap), 48, 48, 0.1F, 2.5F);
    //        Log.e("OcrRecognizeAsyncTask", "thresholding completed. converting to bmp. size:" + bitmap.getWidth() + "x" + bitmap.getHeight());
    //        bitmap = WriteFile.writeBitmap(thresholdedImage);
    //      }
    //      if (PERFORM_OTSU_THRESHOLDING) {
    //        Pix thresholdedImage = Binarize.otsuAdaptiveThreshold(ReadFile.readBitmap(bitmap), 48, 48, 9, 9, 0.1F);
    //        Log.e("OcrRecognizeAsyncTask", "thresholding completed. converting to bmp. size:" + bitmap.getWidth() + "x" + bitmap.getHeight());
    //        bitmap = WriteFile.writeBitmap(thresholdedImage);
    //      }
    //      if (PERFORM_SOBEL_THRESHOLDING) {
    //        Pix thresholdedImage = Thresholder.sobelEdgeThreshold(ReadFile.readBitmap(bitmap), 64);
    //        Log.e("OcrRecognizeAsyncTask", "thresholding completed. converting to bmp. size:" + bitmap.getWidth() + "x" + bitmap.getHeight());
    //        bitmap = WriteFile.writeBitmap(thresholdedImage);
    //      }
    // fix bitmap rotation
	int w = bitmap.getWidth();
	int h = bitmap.getHeight();
    Matrix RotationMatrix = new Matrix();
    RotationMatrix.postRotate(90);
    bitmap = Bitmap.createBitmap(bitmap,0,0,w,h,RotationMatrix,true);
    
    // binarize
    Pix thresholdedImage = Binarize.otsuAdaptiveThreshold(ReadFile.readBitmap(bitmap), 48, 48, 1, 1, 0.1F);
    
    //bitmap = WriteFile.writeBitmap(thresholdedImage);
    try {     
    
      //Bitmap CroppedBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth()/4,bitmap.getHeight());
    	/*
    	byte[] bm = CustomPreprocess.createBinaryImage(bitmap);
    	int w = bitmap.getWidth();
    	int h = bitmap.getHeight();
    	baseApi.setImage(bm,w,h,0,w);
    	*/
    	baseApi.setImage(thresholdedImage);
    	//baseApi.setRectangle(0,0,432,576/4);
    	/*
    	final ResultIterator ri = baseApi.getResultIterator();
    	ri.begin();
    	textResult="";
    	do{
    		String line = ri.getUTF8Text(PageIteratorLevel.RIL_TEXTLINE);
    		textResult+=line;
    		} while(ri.next(PageIteratorLevel.RIL_TEXTLINE));
    	*/
    	//baseApi.setRectangle(left, top, width, height)
      textResult = baseApi.getUTF8Text();
      timeRequired = System.currentTimeMillis() - start;

      // Check for failure to recognize text
      if (textResult == null || textResult.equals("")) {
        return false;
      }
      ocrResult = new OcrResult();
      ocrResult.setWordConfidences(baseApi.wordConfidences());
      ocrResult.setMeanConfidence( baseApi.meanConfidence());
      ocrResult.setRegionBoundingBoxes(baseApi.getRegions().getBoxRects());
      ocrResult.setTextlineBoundingBoxes(baseApi.getTextlines().getBoxRects());
      ocrResult.setWordBoundingBoxes(baseApi.getWords().getBoxRects());
      ocrResult.setStripBoundingBoxes(baseApi.getStrips().getBoxRects());
      //ocrResult.setCharacterBoundingBoxes(baseApi.getCharacters().getBoxRects());
    } catch (RuntimeException e) {
      Log.e("OcrRecognizeAsyncTask", "Caught RuntimeException in request to Tesseract. Setting state to CONTINUOUS_STOPPED.");
      e.printStackTrace();
      try {
        baseApi.clear();
        activity.stopHandler();
      } catch (NullPointerException e1) {
        // Continue
      }
      return false;
    }
    timeRequired = System.currentTimeMillis() - start;
    ocrResult.setBitmap(bitmap);
    ocrResult.setText(textResult);
    ocrResult.setRecognitionTimeRequired(timeRequired);
    return true;
  }

  @Override
  protected void onPostExecute(Boolean result) {
    super.onPostExecute(result);

    Handler handler = activity.getHandler();
    if (handler != null) {
      // Send results for single-shot mode recognition.
      if (result) {
        Message message = Message.obtain(handler, R.id.ocr_decode_succeeded, ocrResult);
        message.sendToTarget();
      } else {
        Message message = Message.obtain(handler, R.id.ocr_decode_failed, ocrResult);
        message.sendToTarget();
      }
      activity.getProgressDialog().dismiss();
    }
    if (baseApi != null) {
      baseApi.clear();
    }
  }
}
