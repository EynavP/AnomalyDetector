package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class TimeSeries 
{
	//Data class for column details
	public class Data{
		
		String propertyName;
		ArrayList<Float> numbers;
		String coraleted;
		//Constructor
		public Data(String propertyName) 
		{
			this.setPropertyName(propertyName);
			this.setCoraleted(null);
			this.numbers = new ArrayList<Float>();
		}
		//Getters and Setters
		public String getPropertyName() {
			return propertyName;
		}
		public void setPropertyName(String propertyName) {
			this.propertyName = propertyName;
		}
		public String getCoraleted() {
			return coraleted;
		}
		public void setCoraleted(String coraleted) {
			this.coraleted = coraleted;
		}
	}
	
	//TimeSerie completion continue
	ArrayList<Data> table = new ArrayList<>();
	String fileName;
	int colNum=0;
	int lineNum=0;
	double coralation = 0.9;
	
	
	public double getCoralation() {
		return coralation;
	}


	public void setCoralation(double coralation) {
		this.coralation = coralation;
	}

	//Analyze time series by file name
	public TimeSeries(String csvFileName)
	{
		this.fileName=csvFileName;
		Path pathToFile = Paths.get(csvFileName);
		try {
			BufferedReader br = Files.newBufferedReader(pathToFile,
			        StandardCharsets.US_ASCII);
			String rightLine = br.readLine();
			String[] firstLine = rightLine.split(",");
			for(int i=0;i<firstLine.length;i++)
			{
				Data temp = new Data(firstLine[i]);
				this.table.add(temp);
				this.colNum++;
			}
	    rightLine = br.readLine();
	    while(rightLine!=null)
	    {
	    	firstLine = rightLine.split(",");
	    	for(int i=0;i<this.getColNum();i++)
	    	{
	    		float f = Float.parseFloat(firstLine[i]);
	    		this.table.get(i).numbers.add(f);
	    	}
	    	rightLine = br.readLine();
	    }
	    br.close();
	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//Getters and Setters

	public int getColNum() {
		return colNum;
	}

	public void setColNum(int colNum) {
		this.colNum = colNum;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
	
	public ArrayList<Float> getDataByName(String name )
	{
		for(int i=0;i<this.colNum;i++)
		{
			if(this.table.get(i).propertyName.equals(name))
				return this.table.get(i).numbers;
		}
		return null;
	}
	
}
