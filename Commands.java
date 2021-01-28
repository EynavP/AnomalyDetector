package test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.text.DecimalFormat;
public class Commands {
	
	// Default IO interface
	public interface DefaultIO{
		public String readText();
		public void write(String text);
		public float readVal();
		public void write(float val);
		
		// you may add default methods here
	}
	
	// the default IO to be used in all commands
	DefaultIO dio;
	public Commands(DefaultIO dio) {
		this.dio=dio;
	}
	
	// you may add other helper classes here
	
	
	
	// the shared state of all commands
	private class SharedState{
		TimeSeries train;
		TimeSeries test;
		List<AnomalyReport> rep=null;
		List<Point> points = null;
		
		public List<AnomalyReport> getRep() {
			return rep;
		}
		public TimeSeries getT() {
			return train;
		}
		public void setT(TimeSeries train) {
			this.train = train;
		}
		public TimeSeries getTest() {
			return test;
		}
		public void setTest(TimeSeries test) {
			this.test = test;
		}
		
	}
	
	private  SharedState sharedState=new SharedState();
	
	public SharedState getSharedState() {
		return this.sharedState;
	}


	// Command abstract class
	public abstract class Command{
		protected String description;
		
		public Command(String description) {
			this.description=description;
		}
		
		public abstract void execute();
	}
	
	// Command class for example:
	public class ExampleCommand extends Command{

		public ExampleCommand() {
			super("this is an example of command");
		}

		@Override
		public void execute() {
			dio.write(description);
		}		
	}
	public class MainCommand extends Command{
		public MainCommand() {
			super("Welcome to the Anomaly Detection Server.\r\n"
					+ "Please choose an option:\n"
					+"1. upload a time series csv file\n"
					+"2. algorithm settings\n"
					+"3. detect anomalies\n"
					+"4. display results\n"
					+"5. upload anomalies and analyze results\n"
					+"6. exit\n");
		}

		@Override
		public void execute() {
			dio.write(description);
			String request = dio.readText();
			int r = Integer.parseInt(request);
			if(r == 1)
				new uploadCSV().execute();
			else if (r==2)
				new algorithmSet().execute();
			else if (r==3)
				new detectAnomaly().execute();
			else if (r==4)
				new displayResult().execute();
			else if (r==5)
				new uploadAndAnalyze().execute();
			else if (r==6)
				new exit().execute();
		}	
	}
	//
	public class uploadCSV extends Command{
		
		public uploadCSV() {
			super("Please upload your local train CSV file.\n");
		}
		
		
		@Override
		public void execute() {
			dio.write(description);
			String word;
			ArrayList<String> text = new ArrayList<String>();
			while(!(word=dio.readText()).equals("done"))
			{
				text.add(word);
				text.add("\n");
			}
			try  
			{
				FileWriter writer = new FileWriter("anomalyTrain.csv");
				String collection = text.stream().collect(Collectors.joining());
				writer.write(collection);
				writer.close();
			}catch (IOException e) {
				e.printStackTrace();}
			
			TimeSeries timeS = new TimeSeries("anomalyTrain.csv");
			SharedState s = getSharedState();
			s.setT(timeS);
			dio.write("Upload complete.\n");
			
			dio.write("Please upload your local test CSV file.\n");
			word=null;
			ArrayList<String> text2 = new ArrayList<String>();
			while(!(word=dio.readText()).equals("done"))
			{
				text2.add(word);
				text2.add("\n");
			}
			try  
			{
				FileWriter writer2 = new FileWriter("anomalyTest.csv");
				String collection = text2.stream().collect(Collectors.joining());
				writer2.write(collection);
				writer2.close();
			}catch (IOException e) {
				e.printStackTrace();}
			
			TimeSeries timeS2 = new TimeSeries("anomalyTest.csv");
			s.setTest(timeS2);
			dio.write("Upload complete.\n");
			new MainCommand().execute();		
		}	
	}
	//
	public class algorithmSet extends Command{
		public algorithmSet() {
			super("The current correlation threshold is ");
		}

		@Override
		public void execute() {
			dio.write(description);
			SharedState s = getSharedState();
			String temp = String.valueOf(s.getTest().coralation);
			dio.write(temp);
			dio.write("\n");
			dio.write("Type a new threshold\n");
			String choice = dio.readText();
			double answer = Double.valueOf(choice);
			while(answer<0 || answer>1)
			{
				dio.write("“please choose a value between 0 and 1.\n");
				choice = dio.readText();
				answer = Double.valueOf(choice);
			}
			s.getTest().setCoralation(answer);
			new MainCommand().execute();
			
			}	
	}
	//
	public class detectAnomaly extends Command{
		public detectAnomaly() {
			super("anomaly detection complete.\n");
		}

		@Override
		public void execute() {
			SharedState s=getSharedState();
			SimpleAnomalyDetector sa = new SimpleAnomalyDetector();
			sa.learnNormal(s.getT());
			List<AnomalyReport> l = new ArrayList<AnomalyReport>();
			l.addAll(sa.detect(s.getTest()));
			s.rep = l;
			dio.write(description);
			new MainCommand().execute();
		}	
	}
	//
	public class displayResult extends Command{
		public displayResult() {
			super("");
		}

		@Override
		public void execute() {
			dio.write(description);
			String timeStep;
			SharedState s = getSharedState();
			for(int i=0;i<s.getRep().size();i++)
			{
				timeStep = String.valueOf(s.getRep().get(i).timeStep);
				dio.write(timeStep+"\t"+s.getRep().get(i).description+"\n");
			}
			dio.write("done.\n");
			new MainCommand().execute();
		}	
	}
	
	//
	public class uploadAndAnalyze extends Command{
		public uploadAndAnalyze() {
			super("Please upload your local anomalies file.\n"
					+"Upload complete.\n");
		}

		@Override
		public void execute() {
			dio.write(description);
			String line = null;
			SharedState s = getSharedState();
			s.points = new ArrayList<Point>();
			while(!(line=dio.readText()).equals("done"))
			{
				String[]temp = line.split(",");
				float x = Float.valueOf(temp[0]);
				float y = Float.valueOf(temp[1]);
				s.points.add(new Point(x,y));
			}
			float negative;
			float sum=0;
			for(int i=0;i<s.points.size();i++)
				sum= sum + (s.points.get(i).y - s.points.get(i).x)+1;
			
			negative = s.getTest().table.get(0).numbers.size()-sum;
			
			ArrayList<String> disc = new ArrayList<String>();
			ArrayList<Point> val = new ArrayList<Point>();
			int k=0;
			for(int i=0;i<s.getRep().size();i++)
			{
				AnomalyReport start = s.getRep().get(i);
				AnomalyReport end = null;
				int j= i+1;
				while(j<s.getRep().size() && start.description.equals(s.getRep().get(j).description) && start.timeStep +j -k== s.getRep().get(j).timeStep)
				{
						end = s.getRep().get(j);
						j++;
				}
				if(end!=null)
				{
					disc.add(end.description);
					val.add(new Point(start.timeStep,end.timeStep));
					k=j;
				}
				else
				{
					disc.add(start.description);
					val.add(new Point(start.timeStep,start.timeStep));
					k=j;
				}
				i=j-1;
			}
			double fn=0,fp=0,tp=0;
			double tn=0;
			int flag=0;
			List<Point> temp = new ArrayList<Point>();
			for(int i=0;i<s.points.size();i++)
			{
				for(int j=0;j<val.size();j++)
				{
					if((s.points.get(i).x <= val.get(j).y && s.points.get(i).y>=val.get(j).y)||
							(s.points.get(i).x>=val.get(j).x && s.points.get(i).y>=val.get(j).y && val.get(j).y>=s.points.get(i).x)||
							(val.get(j).x<=s.points.get(i).x && val.get(j).y>=s.points.get(i).y)||
							(val.get(j).x>=s.points.get(i).x && val.get(j).y>=s.points.get(i).y && s.points.get(i).y >= val.get(j).x))
					{
						if(flag==0)
							tp+=1;
						flag = 1;
						if(!temp.contains(val.get(j)))
							temp.add(val.get(j));
					}
				}	
				if(flag==0) //No report connect to exception
					fn+=1;
				flag=0;
			}
			fp = val.size()-temp.size();
			tn = tn + s.test.lineNum-1-tp-fn-fp;
			tp = tp/s.points.size();
			fp = fp/negative;
			
			DecimalFormat fd = new DecimalFormat("0.0000");
			String t = fd.format(tp);
			String f = fd.format(fp);
			t = t.substring(0, t.length()-1);
			f = f.substring(0, f.length()-1);
			while (t.charAt(t.length()-1)=='0' && t.length()!=3)
				t = t.substring(0, t.length()-1);
			while (f.charAt(f.length()-1)=='0' && f.length()!=3)
				f = f.substring(0, f.length()-1);
			dio.write("True Positive Rate: "+t+"\n");
			dio.write("False Positive Rate: "+f+"\n");
			new MainCommand().execute();
		}	
	}
	
	//
	public class exit extends Command{
		public exit() {
			super("");
		}

		@Override
		public void execute() {
			dio.write(description);
		}	
	}
	
}
