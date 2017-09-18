package visualization;

public class Curve_xt {
	float[] x, y; double[][] range=new double[2][2];
	public Curve_xt(Data data, double px){
		int s=0;
		int Nt=data.fp[s].getWidth();
		float ymin=0,ymax=0;
		x=new float[Nt]; y=new float[Nt];
		for (int i=0; i<Nt; i++) {
			x[i]=(float) (i*data.ht*data.kstep);
			y[i]=data.fp[s].getf(i,(int) (data.fp[s].getHeight()*px));
			if (y[i]<ymin) ymin=y[i];
			if (y[i]>ymax) ymax=y[i];
		}
		range[0][0]=0; range[0][1]=Nt*data.ht*data.kstep;// range of x value (min/max)
		range[1][0]=ymin; range[1][1]=ymax;// range of y value (min/max)
		System.out.println(data.name+","+""+","+ymax);
	}
	public boolean whether_magnified(){
		boolean result=false; 
		if (y[0]>0) 
			result=true;//decide if curve meets some criteria
		return result;
	}

}
