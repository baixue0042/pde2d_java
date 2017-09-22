package visualization;

public class Curve_xt {
	float[] x, y, range=new float[4]; 
	float ymin,ymax,x_ymax=0;
	public Curve_xt(Data data, double px){
		int s=0;
		int Nt=data.fp[s].getWidth(), index_pos=(int) (data.fp[s].getHeight()*px);
		x=new float[Nt]; y=new float[Nt];
		x[0]=0;
		y[0]=data.fp[s].getf(0,index_pos);
		y[0]=y[0]/(float) data.hss[s];
		ymin=y[0]; ymax=y[0];
		for (int i=1; i<Nt; i++) {
			x[i]=(float) (i*data.ht*data.kstep);
			y[i]=data.fp[s].getf(i,index_pos);
			y[i]=y[i]/(float) data.hss[s];
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
		boolean result=true; 
		for (int i=0; i<3; i++) {
			if (y[i+1]-y[i]<0) {//not magnified if any of the first 3 time steps has negative derivative
				result=false;
				break;
			}
		}
		return result;
	}

}
