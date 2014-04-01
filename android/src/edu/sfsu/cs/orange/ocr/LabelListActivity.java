package edu.sfsu.cs.orange.ocr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LabelListActivity extends Activity implements OnItemClickListener  {
	
	private SavedLabel[] savedlabels;
	private String[] names;
	private String[] types;
	private String[] costs;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.food_list_frag);
		Typeface arialblack=Typeface.createFromAsset(getAssets(), "fonts/arialblack.ttf");
		TextView title=(TextView) findViewById(R.id.list_title);
		title.setTypeface(arialblack);
		init();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		view.setSelected(true);
		final float[] currVals=savedlabels[position].getVals();
		final String filename = ((TextView) view.findViewById(R.id.food_item)).getText().toString();
			
		new AlertDialog.Builder(this)
				.setMessage("You may view or delete this label")
				.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								deleteImage(filename);
							}
						})
				.setNegativeButton("View",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
								Bundle b = new Bundle();
								b.putString(GraphActivity.LABEL_NAME, filename);
								b.putFloatArray(GraphActivity.NUTRITION_QUANT_KEY, currVals);
								intent.putExtras(b);
								startActivity(intent);
							}
						}).show();
		
	}
	
	public void init(){
		read_files();
		ListView mListView=(ListView) findViewById(R.id.labels_listview);
		LabelListCustomAdapter adapter= new LabelListCustomAdapter(this, names, types, costs);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
	}
	
	public void deleteImage(String filename) {
		deleteFile(filename);
		Toast.makeText(getApplicationContext(), "Label deleted", Toast.LENGTH_SHORT).show();
		init();
	}
	
	public void read_files(){
		String[] filelist=fileList();
		String[] n=new String[filelist.length];
		String[] t=new String[filelist.length];
		String[] c=new String[filelist.length];
		SavedLabel[] sl=new SavedLabel[filelist.length];
		for(int i=0;i<filelist.length;i++){
			try {
				FileInputStream fis=openFileInput((filelist[i]));
				ObjectInputStream ois=new ObjectInputStream(fis);
				SavedLabel label=(SavedLabel) ois.readObject();
				ois.close();
				fis.close();
				sl[i]=label;
				n[i]=label.getName();
				t[i]=label.getType();
				c[i]=label.getCost();
			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		savedlabels=sl;
		names=n;
		types=t;
		costs=c;
	}


}
