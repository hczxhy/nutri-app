package edu.sfsu.cs.orange.ocr;

import java.util.Vector;

import android.annotation.SuppressLint;
import android.os.AsyncTask;


public class Dictionary_comparison extends AsyncTask<Void, String, Void>{
	private String input_text="";
	private String output_text="";
	private String[] dictionary={"Amount","Teneur","Calories / Calories", "Fat / Lipides", "Cholesterol / Cholesterol", "Sodium / Sodium", "Carbohydrate / Glucides", "Protein / Proteines"};
	private int[] scores={4,4,10,7,14,9,15,10};
	private float[] core_values=new float[6]; 			//Core values are in this order: Calories, Fat, Cholesterol, Sodium, Carbohydrate, Protein
	
	public Dictionary_comparison(String text){
		if(text!=null){
			input_text=text;
		}
	}
	public String get_translated_text(){
		System.out.println(output_text);
		return output_text;
	}
	
	public float[] get_core_fields(){
		return core_values;
	}
	
	@SuppressLint("NewApi")
	@Override
	protected Void doInBackground(Void... params) {
		Boolean[] fields_found={false,false,false,false,false,false,false,false};
		//Separate the original input text into substrings which will be individually compared to the fields in the dictionary
		String[] untrimmed_substrings=input_text.split("\n");
		Vector<String> substring_vec=new Vector<String>();
		for(String substring:untrimmed_substrings){
			if(!substring.trim().isEmpty()){
				substring_vec.add(substring);
			}
		}
		String[] substrings=new String[substring_vec.size()];
		for(int m=0;m<substrings.length;m++){
			substrings[m]=substring_vec.get(m);
		}
		//Go through input text line by line and compare to dictionary
		for(int i=3;i<substrings.length;i++){
			int min_dist=10000;
			String curr_line=substrings[i];
			//Check special cases where numbers were misidentified as letters, or vice versa
			curr_line=fix_misid(curr_line);
			
			int field_index=-1;
			String text_in_line;
			String rest_of_line;
			//Try to split the line according to text and numbers
			int index_of_first_int=0;
			while(index_of_first_int<curr_line.length()&&!Character.isDigit(curr_line.charAt(index_of_first_int))){
				index_of_first_int++;
			}
			if(index_of_first_int!=curr_line.length()){
				text_in_line=curr_line.substring(0,index_of_first_int);
				rest_of_line=curr_line.substring(index_of_first_int);
			}else{
				text_in_line=curr_line;
				rest_of_line="";
			}

			String transformed_text=text_in_line;
			//Go through all the words in the dictionary and compare to the input substring
			for(int j=0;j<dictionary.length;j++){
				//Do not compare with this dictionary word if it has been already found earlier
				if(fields_found[j]==false){
					Distance_calculator dist_calc=new Distance_calculator(text_in_line, dictionary[j]);
					int dist=dist_calc.calculate_dist();
					//Check to see if the strings have any similarity at all first before comparing to dictionary
					if(dist==Math.max(text_in_line.length(), dictionary[j].length())){
						System.out.println("No match");
					}else {
						if(dist<min_dist&&dist<scores[j]){
							min_dist=dist;
							transformed_text=dictionary[j];
							field_index=j;
						}
					}
				}
			}
			
			substrings[i]=transformed_text+" "+rest_of_line;
			//Store values of core fields
			if(field_index==2 && fields_found[field_index]==false)
				core_values[0]=parse_value(rest_of_line);
			if(field_index==3 && fields_found[field_index]==false)
				core_values[1]=parse_value(rest_of_line);
			if(field_index==4 && fields_found[field_index]==false)
				core_values[2]=parse_value(rest_of_line);
			if(field_index==5 && fields_found[field_index]==false)
				core_values[3]=parse_value(rest_of_line);
			if(field_index==6 && fields_found[field_index]==false)
				core_values[4]=parse_value(rest_of_line);
			if(field_index==7 && fields_found[field_index]==false)
				core_values[5]=parse_value(rest_of_line);
			if(field_index!=-1){
				fields_found[field_index]=true;
			}
		}
		
		//Compile the array of substrings into the output text string
		for(int i=0; i<substrings.length;i++){
			output_text=output_text+"\n"+substrings[i];
		}
		return null;
	}
	
	private float parse_value(String text){
		float result=9999;
		if(text.length()!=0){
			int m=0;
			while(m<text.length()&&!Character.isDigit(text.charAt(m))){
				m++;
			}
			int n=m;
			while(n<text.length()&&Character.isDigit(text.charAt(n))){
				n++;
				//Catch the case where the last digit is actually a misidentified g
			}
			result=Float.parseFloat(text.substring(m, n));
			if(text.indexOf(" g")!=-1){
				result=Float.parseFloat(text.substring(m, n));
			}else if(text.indexOf("mg")!=-1){
				result=Float.parseFloat(text.substring(m,n))/1000;
			}else if(n<text.length()&&text.charAt(n)=='g'){
				result=Float.parseFloat(text.substring(m, n));
			}else if(text.charAt(n-1)==9){
				result=Float.parseFloat(text.substring(m, n-1));
			}
		}
		return result;
	}
	
	private String fix_misid(String text){
		if(text!=null){
			if(text.indexOf("o g")!=-1){
				text.replaceFirst("o g", " 0 g");
			}
			if(text.indexOf("t g")!=-1){
				text.replaceFirst("t g", "1 g");
			}
			if(text.indexOf("I g")!=-1){
				text.replaceFirst("I g", "1 g");
			}
			if(text.indexOf("o 9")!=-1){
				text.replaceFirst("o 9", "0 g");
			}
			if(text.indexOf("t 9 ")!=-1){
				text.replaceFirst("t 9", "1 g");
			}
			if(text.indexOf("I 9")!=-1){
				text.replaceFirst("I 9", "1 g");
			}
			if(text.indexOf("m9")!=-1){
				text.replaceFirst("m9", "mg");
			}
			if((text.indexOf(" 9")!=-1) && Character.isDigit(text.charAt(text.indexOf(" 9")-1))){
				text.replaceFirst(" 9", " g");
			}
			if((text.indexOf(" m")!=-1) && Character.isDigit(text.charAt(text.indexOf(" m")-1))){
				text.replaceFirst(" m", " mg");
			}
		}
		return text;
	}
	
}
