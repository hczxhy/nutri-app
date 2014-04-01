package edu.sfsu.cs.orange.ocr;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SaveDialog extends DialogFragment {
 
	float[] vals={0,0,0,0,0};
	
	public void set_vals(float[] _vals){
		if(_vals.length==5){
			vals=_vals;
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    final View view=inflater.inflate(R.layout.save_dialog, null);
	    builder.setView(view);
	    // Add action buttons
	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int id) {
			   	EditText ed_name= (EditText) view.findViewById(R.id.food_name);
			   	EditText ed_type=(EditText) view.findViewById(R.id.food_type);
			   	EditText ed_cost=(EditText) view.findViewById(R.id.food_cost);
			   	String name=ed_name.getText().toString();
			   	String type=ed_type.getText().toString();
			   	String cost=ed_cost.getText().toString();
			   	
			   	if(ed_name.getText().length()==0||name==""||name==null){
			   		name="Untitled";
			   	}
			   	if(ed_type.getText().length()==0||type==null){
			   		type="";
			   	}
			   	if(ed_cost.getText().length()==0||cost==null){
			   		cost="";
			   	}
			   	
				try {
					FileOutputStream fs= getActivity().openFileOutput(ed_name.getText().toString(), Context.MODE_PRIVATE);
					ObjectOutputStream oos=new ObjectOutputStream(fs);
					SavedLabel sl=new SavedLabel();
					sl.setIdentifiers(ed_name.getText().toString(),ed_type.getText().toString(),ed_cost.getText().toString());
					sl.setLabelValues(vals);
					oos.writeObject(sl);
					oos.close();
					fs.close();
					System.out.println("File Saved!");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
           	}
	    });
	    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	       public void onClick(DialogInterface dialog, int id) {
	           SaveDialog.this.getDialog().cancel();
	       	}
	    });      
	    return builder.create();
	}
	
}
