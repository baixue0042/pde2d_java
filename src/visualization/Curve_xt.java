package visualization;

public class Curve_xt {
	float[] x, y, range=new float[4]; 
	float ymin,ymax,x_ymax=0;
	public Curve_xt(Data data, double px){
		int s=0;
		int Nt=data.fp[s].getWidth();
		x=new float[Nt]; y=new float[Nt];
		for (int i=0; i<Nt; i++) {
			x[i]=(float) (i*data.ht*data.kstep);
			y[i]=data.fp[s].getf(i,(int) (data.fp[s].getHeight()*px));
			ymin=y[0]; ymax=y[0];
			if (y[i]<ymin) ymin=y[i];
			if (y[i]>ymax) {
				ymax=y[i];
				x_ymax=x[i];// value of x when y reach max
			}
		}
		range[0]=0; range[1]=(float) (Nt*data.ht*data.kstep);// range of x value (min/max)
		range[2]=ymin; range[3]=ymax;// range of y value (min/max)
	}
	public boolean whether_magnified(){
		boolean result=false; 
		if (x_ymax>5) 
			result=true;//decide if curve meets some criteria
		return result;
	}

}
