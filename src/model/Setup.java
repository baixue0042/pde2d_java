package model;

public class Setup {
	public static double[] toDouble(String[] string){
		double[] arr = new double[string.length];
		for (int i=0; i<string.length; i++) { arr[i] =  Double.parseDouble(string[i]); }
		return arr;
	}

}
