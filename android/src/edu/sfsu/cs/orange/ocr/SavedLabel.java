package edu.sfsu.cs.orange.ocr;

public class SavedLabel implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1963175052466022363L;
	private String name;
	private String type;
	private String cost;
	private float[] vals;
	
	public void setIdentifiers(String n, String l, String c){
		name=n;
		type=l;
		cost=c;
	}
	public void setLabelValues(float[] values){
		if(values.length==5){
			vals=values;
		}
	}
	public String getName(){
		return name;
	}
	public String getType(){
		return type;
	}
	public String getCost(){
		return cost;
	}
	public float[] getVals(){
		return vals;
	}

}
