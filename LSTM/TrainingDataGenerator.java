import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class TrainingDataGenerator{
	public static final double limit = 0.000005d;
	public static final double limit2 = 0.00001d;
    public static void main(String args[]) throws IOException{
    	FileWriter fw = new FileWriter(".\\testData.csv",true);
    	
    	StringBuilder sb = new StringBuilder();
    	double[] d = new double[6];
    	int count = 0,count2 = 0;
    	for(int i=0;i<500;i++){
	    	makeRandomLocation(d);
	    	//printRandomLocation(d);
	    	if(function(d[0],d[1],d[2],d[3],d[4],d[5])){
	    		sb.append(d[0]).append(",");
	    		sb.append(d[1]).append(",");
	    		sb.append(d[2]).append(",");
	    		sb.append(d[3]).append(",");
	    		sb.append(d[4]).append(",");
	    		sb.append(d[5]).append(",");
	    		sb.append("1").append("\n");
	    		sb.append(d[2]).append(",");
	    		sb.append(d[3]).append(",");
	    		sb.append(d[0]).append(",");
	    		sb.append(d[1]).append(",");
	    		sb.append(d[4]).append(",");
	    		sb.append(d[5]).append(",");
	    		sb.append("1").append("\n");
	    		count+=2;
	    	}else {
	    		sb.append(d[0]).append(",");
	    		sb.append(d[1]).append(",");
	    		sb.append(d[2]).append(",");
	    		sb.append(d[3]).append(",");
	    		sb.append(d[4]).append(",");
	    		sb.append(d[5]).append(",");
	    		sb.append("0").append("\n");
	    		sb.append(d[2]).append(",");
	    		sb.append(d[3]).append(",");
	    		sb.append(d[0]).append(",");
	    		sb.append(d[1]).append(",");
	    		sb.append(d[4]).append(",");
	    		sb.append(d[5]).append(",");
	    		sb.append("0").append("\n");
	    		count2+=2;
	    	}
	    	
    	}
    	System.out.println(count);
    	fw.append(sb.toString());
    	fw.close();
    }
    
    public static void printRandomLocation(double[] d){
    	System.out.println(d[0] + " " + d[1] + "\n" + d[2] + " " + d[3] + "\n" + d[4] +" " + d[5]);
    }
    
    public static void makeRandomLocation(double[] d){
    	Random random = new Random();
    	double x1 = random.nextDouble()*5 + 125;
    	double y1 = random.nextDouble()*6 + 33;
    	
    	double tx = random.nextDouble()/1000;
    	double ty = random.nextDouble()/1000;
    	
    	double x2 = tx + x1;
    	double y2 = ty + y1;
    	

    	double dis = Math.sqrt(tx*tx+ty*ty);
    	double cx = random.nextDouble()*(dis+limit2*2)-limit2;
    	double cy = random.nextDouble()*limit2*2-limit2;
    	double r = Math.sqrt(cx*cx+cy*cy);
    	double theta1 = Math.atan(cy/cx);
    	double theta = Math.atan(ty/tx);
    	if(theta1>0){
    		if(cx>0){
    			double tmp = 3*Math.PI/2-theta1-theta;
    			cx = r*Math.cos(tmp) + x1;
    			cy = r*Math.cos(tmp) + y1;
    		}else{
    			double tmp = theta1+theta;
    			cx = r*Math.cos(tmp) + x1;
    			cy = r*Math.cos(tmp) + y1;
    		}
    	}else{
    		if(cx<0){
    			double tmp = 2*Math.PI+theta1-theta;
    			cx = r*Math.cos(tmp) + x1;
    			cy = r*Math.cos(tmp) + y1;
    		}else{
    			double tmp = Math.PI -theta + theta1;
    			cx = r*Math.cos(tmp) + x1;
    			cy = r*Math.cos(tmp) + y1;
    		}
    	}
    	d[0] = x1;
    	d[1] = y1;
    	d[2] = x2;
    	d[3] = y2;
    	d[4] = cx;
    	d[5] = cy;
    }
    
    public static boolean function(double x1, double y1, double x2, double y2, double cx, double cy){
    	if(x2!=x1){
	    	double a = (y2-y1) / (x2-x1);
	    	double d = Math.sqrt((y2-y1)*(y2-y1) +(x2-x1)*(x2-x1));
	    	if(a>0){
	    		double x,y;
	    		if(x2<x1) {
	    			x = x2;
	    			y = y2;
	    		}else{
	    			x = x1;
	    			y = y1;
	    		}
	    		cx = cx-x;
	    		cy = cy-y;
	    		
	    		double r = Math.sqrt(cx*cx + cy*cy);
	    		double theta1;
	    		if(cx!=0)
	    			theta1 = Math.atan(cy/cx);
	    		else
	    			theta1 = Math.PI / 2;
	    		double theta = Math.atan(a);
	    		
	    		double aa = r * Math.cos(theta1 - theta);
	    		double bb = r * Math.sin(theta1 - theta);
	    		
	    		if(aa>=-limit && aa<=limit+d && bb>=-limit && bb<=limit) return true;
	    		else return false;
	    	}else if(a<0){
	    		double x,y;
	    		if(x2>x1){
	    			x = x2;
	    			y = y2;
	    		}else{
	    			x = x1;
	    			y = y1;
	    		}
	    		cx = cx-x;
	    		cy = cy-y;
	    		
	    		double r = Math.sqrt(cx*cx + cy*cy);
	    		double theta1;
	    		if(cx!=0)
	    			theta1 = Math.atan(cy/cx);
	    		else
	    			theta1 = Math.PI/2;
	    		
	    		double theta = Math.atan(a);
	    		
	    		double aa = r * Math.cos(theta1 - theta);
	    		double bb = r * Math.sin(theta1 - theta);
	    		
	    		if(aa>=-limit-d && aa<=limit && bb>=-limit && bb<=limit) return true;
	    		else return false;
	
	    	}else {
	    		if(y1>y2){
	    			double tx,ty;
	    			tx = x1;
	    			x1 = x2;
	    			x2 = tx;
	    			ty = y1;
	    			y1 = y2;
	    			y2 = ty;
	    		}
	    		
	    		if(cx>=x1-limit && cx<=x2+limit && cy>=y1-limit && cy<=y2+limit) return true;
	    		else return false;
	    	}
    	}else{
    		if(y2<y1){
    			double tx = x2;
    			double ty = y2;
    			x2 = x1;
    			y2 = y1;
    			x1 = tx;
    			y1 = ty;
    		}
    		if(cx>= x1-limit && cx<=x1+limit && cy>=y1-limit && cy<=y1+limit) return true;
    		else return false;
    	}
    }
    
    public static double getDistanceFromLatLon(double lon1, double lat1, double lon2, double lat2){
        double R = 6371;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) *Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) *Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c * 1000;
        return d;
    }
    
}