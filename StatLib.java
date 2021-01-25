package test;

public class StatLib {

	// simple average
	public static float avg(float[] x)
	{
		float average = 0;
		int size = x.length;
		for (int i=0;i<size;i++)
		{
			average = average + x[i];
		}
		average = average/(float)size;
		return average;
	}

	// returns the variance of X and Y
	public static float var(float[] x)
	{
		int n = x.length;
		float u,v=0;
		
		u= avg(x);
		for(int i=0;i<n;i++)
			v= v + ((x[i]-u)*(x[i]-u));
		v= v/(float)n;
		
		return v;
	}

	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y)
	{
		float avgX,avgY,sum=0;
		avgX=avg(x);
		avgY=avg(y);
		int size = x.length;
		for(int i=0;i<size;i++)
			sum = sum + ((x[i]-avgX)*(y[i]-avgY));
		return sum/(float)size;
	}


	// returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y)
	{
		float covarXY = cov(x,y);
		float sqX,sqY;
		sqX = (float) Math.sqrt(var(x));
		sqY = (float)Math.sqrt(var(y));
		
		return covarXY/(sqX*sqY);
	}

	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points)
	{
		float a,b;
		int size = points.length;
		float []x=new float[size];
		float[]y=new float[size];
		for(int i=0;i<size;i++)
		{
			x[i]=points[i].x;
			y[i]=points[i].y;
		}
		a = cov(x,y)/var(x);
		b= avg(y)-(a*avg(x));
		Line n = new Line(a,b);
		
		return n;
	}

	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p,Point[] points)
	{
		Line n = linear_reg(points);
		float y = n.f(p.x);
		float dis = (float)(Math.abs(y-p.y));
		return dis;
	}

	// returns the deviation between point p and the line
	public static float dev(Point p,Line l){
		float y = l.f(p.x);
		float dis = (float)(Math.abs(y-p.y));
		return dis;
	}
	
}
