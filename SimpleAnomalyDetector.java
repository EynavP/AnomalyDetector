package test;

import java.util.ArrayList;
import java.util.List;


public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector
{
	ArrayList<CorrelatedFeatures> correlatedArray = new ArrayList<CorrelatedFeatures>();

	//Convert arraylist to float array
	public float[] changeTofloat(ArrayList<Float> a)
	{
		float[] fArray = new float[a.size()];
		for (int i=0;i<a.size();i++) 
		{
		    fArray[i] = a.get(i);
		}
		return fArray;
	}
	//Convert x and y arrays to points array
	public Point[] changeToPoint(float[]a,float[]b)
	{
		Point[] p = new Point[a.length];
		for(int i=0;i<a.length;i++)
		{
			p[i]=new Point(a[i],b[i]);
		}
		return p;
	}
	//Checking if two features already corralated to each other
	public boolean alreadyCor(String a,String b)
	{
		for(int i=0;i<this.correlatedArray.size();i++)
		{
			if(this.correlatedArray.get(i).feature1.equals(b)&&(this.correlatedArray.get(i).feature2.equals(a)))
				return true;
		}
		return false;
	}
	//Preliminary stage before real test
	public void learnNormal(TimeSeries ts) 
	{
		int size= ts.getColNum();
		int place=0;
		float max=0;
		for(int i=0;i<size-1;i++)
		{
			float[] property1 = changeTofloat(ts.table.get(i).numbers);
			ts.table.get(i).setCoraleted(ts.table.get(i+1).propertyName);
			place = 0;
			for(int j=0;j<size;j++)
			{				
				if(i!=j)
				{
					float[] property2 = changeTofloat(ts.table.get(j).numbers);
					//max =StatLib.pearson(property1,property2);
					float maybeMax = Math.abs(StatLib.pearson(changeTofloat(ts.table.get(i).numbers), changeTofloat(ts.table.get(j).numbers)));
					if(max<=maybeMax&&maybeMax>ts.coralation)
					{
						max=maybeMax;
						ts.table.get(i).setCoraleted(ts.table.get(j).propertyName);
						place=j;
					}
				}
			}
			if(max!=0)
			{
				Point[] temp = changeToPoint(property1,changeTofloat(ts.table.get(place).numbers));
				Line tempLine = StatLib.linear_reg(temp);
				float th = StatLib.dev(temp[0],tempLine);
				for(int k=0;k<temp.length;k++)
				{
					float help = StatLib.dev(temp[k],tempLine);
					if(th<help)
						th=help;
				}
				if(alreadyCor(ts.table.get(i).propertyName,ts.table.get(i).coraleted)==false)
				{
					th=(float) (th+0.027);
					CorrelatedFeatures helpCor = new CorrelatedFeatures(ts.table.get(i).propertyName, ts.table.get(i).coraleted,max,tempLine,th);
					correlatedArray.add(helpCor);
				}
			}
			max=0;
		}
	}
	//Analyze real flight
	public List<AnomalyReport> detect(TimeSeries ts)
	{
		ArrayList<AnomalyReport> resultList = new ArrayList<AnomalyReport>();
		for(int i=0;i<this.correlatedArray.size();i++)
		{
			ArrayList<Float>property1 = ts.getDataByName(this.correlatedArray.get(i).feature1);
			ArrayList<Float>property2 = ts.getDataByName(this.correlatedArray.get(i).feature2);
			Point[] arrayPoint = changeToPoint(changeTofloat(property1),changeTofloat(property2));
			String f1 = this.correlatedArray.get(i).feature1;
			String f2 = this.correlatedArray.get(i).feature2;
			String desc = f1+"-"+f2;
			for(int j=0;j<arrayPoint.length;j++)
			{
				float tempValue = StatLib.dev(arrayPoint[j],this.correlatedArray.get(i).lin_reg);
				if(tempValue>this.correlatedArray.get(i).threshold)
				{
					AnomalyReport report = new AnomalyReport(desc, j+1);	
					resultList.add(report);
				}					
			}
		}
		return resultList;
	}
	//Gettting the correlated Array
	public List<CorrelatedFeatures> getNormalModel(){
		return this.correlatedArray;
	}
}
