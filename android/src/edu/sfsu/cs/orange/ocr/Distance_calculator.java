package edu.sfsu.cs.orange.ocr;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class Distance_calculator{

	private String s, t;
	private int dist;
	
	public Distance_calculator(String preprocessed_text, String dictionary_term) {
		if(preprocessed_text!=null&&dictionary_term!=null){
			if(preprocessed_text.length()<=dictionary_term.length()){
				s=preprocessed_text.toLowerCase(Locale.getDefault());
				t=dictionary_term.toLowerCase(Locale.getDefault());
			}else{
				s=dictionary_term.toLowerCase(Locale.getDefault());
				t=preprocessed_text.toLowerCase(Locale.getDefault());
			}
		}else{
			System.out.println("Passed in invalid string!");
		}
	}

	public int calculate_dist(){
		int cost;
		//Degenerate Cases
		if(s==t)
			dist=0;
		if(s.length()==0)
			dist=t.length();
		if(t.length()==0)
			dist=s.length();
		
		int n=s.length();
		int m=t.length();
		
		int[] v0=new int[m+1];
		int[] v1=new int[m+1];
		//First row
		for(int i=0; i<=m; i++){
			v0[i]=i;
		}
		//Iterative process
		for(int i=1; i<=n;i++){
			v1[0]=i;
			for (int j = 1; j <= m; j++)
	        {
	            if(similarity_check(s.charAt(i-1),t.charAt(j-1))){
	            	cost=0;
	            }else{
	            	cost=1;
	            }
	            int min1 = Math.min(v1[j-1] + 1, v0[j] + 1);
	            v1[j] = Math.min(min1, v0[j-1] + cost);
	        }
			v0=v1.clone();
		}
		dist=v1[m];
		return dist;
	}
	
	public Boolean similarity_check(char c1, char c2){

		List<Character> array1=Arrays.asList('a', 'o', '0', 'O', 'Q');
		List<Character> array2=Arrays.asList('b', 'h', '6', '&');
		List<Character> array3=Arrays.asList('c', 'e', 'C', 'G');
		List<Character> array4=Arrays.asList('f', 't', '7');
		List<Character> array5=Arrays.asList('g', 'q','8', '9', 'B');
		List<Character> array6=Arrays.asList('i','j', '1', 'l','t', '!', 'I', '/');
		List<Character> array7=Arrays.asList('m','n','w');
		List<Character> array8=Arrays.asList('p', 'P');
		List<Character> array9=Arrays.asList('s', '5', 'S');
		List<Character> array10=Arrays.asList('u','v','V');
		List<Character> array11=Arrays.asList('x','%','X');
		
		if(c1==c2){
			return true;
		}else if(array1.contains(c1)){
			if(array1.contains(c2)){
				return true;
			}else{
				return false;
			}
		}else if(array2.contains(c1)){
			if(array2.contains(c2)){
				return true;
			}else{
				return false;
			}
		}else if(array3.contains(c1)){
			if(array3.contains(c2)){
				return true;
			}else{
				return false;
			}
		}else if(array4.contains(c1)){
			if(array4.contains(c2)){
				return true;
			}else{
				return false;
			}
		}else if(array5.contains(c1)){
			if(array5.contains(c2)){
				return true;
			}else{
				return false;
			}
		}else if(array6.contains(c1)){
			if(array6.contains(c2)){
				return true;
			}else{
				return false;
			}
		}else if(array7.contains(c1)){
			if(array7.contains(c2)){
				return true;
			}else{
				return false;
			}
		}else if(array8.contains(c1)){
			if(array8.contains(c2)){
				return true;
			}else{
				return false;
			}
		}else if(array9.contains(c1)){
			if(array9.contains(c2)){
				return true;
			}else{
				return false;
			}
		}else if(array10.contains(c1)){
			if(array10.contains(c2)){
				return true;
			}else{
				return false;
			}		
		}else if(array11.contains(c1)){
			if(array11.contains(c2)){
				return true;
			}else{
				return false;
			}	
		}else{
			return false;
		}
		
	}
}








