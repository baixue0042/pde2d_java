package track;

import java.awt.Point;
import java.awt.Polygon;

import ij.process.ByteProcessor;

public class Contour {
	public static int[] dy = {0,1,1,1,0,-1,-1,-1};
	public static int[] dx = {1,1,0,-1,-1,-1,0,1};
	public int labelCount;
	public ByteProcessor proc;
	public ByteProcessor labeledproc;
	public Polygon contour;
	private Point p;
	private int startnext;
	private boolean notfinished;
	
	private int relativePosition(int oldP){
		if (oldP<4)
			return oldP+4;
		else
			return oldP-4;
	}
	
	private void printinfo(String append, int index){
		System.out.print(append+"***");
		System.out.print(this.startnext+",["+this.p.x+","+this.p.y+"],"+getPixelValue(0)+";");
		System.out.print(index+",["+(dx[(index%8)])+","+(dy[(index%8)])+"],"+getPixelValue(index)+";");
		System.out.print("\n");
	}
	
	private int getPixelValue(int index){
		return this.proc.get(this.p.x+dx[(index%8)],this.p.y+dy[(index%8)]);
	}
	
	private void movePoint(int index){
		this.p.translate(dx[(index%8)],dy[(index%8)]);
	}
	
	private void labelAddPoint(){
		this.labeledproc.set(this.p.x,this.p.y,labelCount);
		this.contour.addPoint(this.p.x, this.p.y);
	}

	public Contour(ByteProcessor p, ByteProcessor lp, Point fp, int l,int OBJECT){
		this.proc = p;
		this.labeledproc = lp;
		this.labelCount = l;
		this.contour = new Polygon();
		Point firstPoint = new Point(fp.x,fp.y);
		this.p = fp;
		this.startnext = 7;
		findSecond(OBJECT);
		Point secondPoint =  new Point(this.p.x,this.p.y);
		if (!this.p.equals(firstPoint)){
			secondPoint = this.p;
			this.notfinished = true;
			while (notfinished){
				findNext(firstPoint,secondPoint,OBJECT);
			}
		}
	}
		
	private void findSecond(int OBJECT){
		labelAddPoint();
		int index = this.startnext;
		//printinfo("out",index);
		while ((index<this.startnext+8) && (getPixelValue(index)!=OBJECT)){
			index++;
			//printinfo("in",index);
		}
		this.startnext = relativePosition((index%8)+2);
		movePoint(index);
	}

	private void findNext(Point firstPoint, Point secondPoint,int OBJECT){
		boolean notFirst = !(this.p.equals(firstPoint));
		findSecond(OBJECT);
		boolean notSecond = !(this.p.equals(secondPoint));
		notfinished = (notFirst||notSecond);
	}
	
}
