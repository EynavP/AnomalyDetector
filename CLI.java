package test;

import java.util.ArrayList;

import test.Commands.Command;
import test.Commands.DefaultIO;

public class CLI {

	ArrayList<Command> commands;
	DefaultIO dio;
	Commands c;
	
	public CLI(DefaultIO dio) {
		this.dio=dio;
		c=new Commands(dio); 
		commands=new ArrayList<>();
		commands.add(c.new MainCommand());
		commands.add(c.new uploadCSV());
		commands.add(c.new algorithmSet());
		commands.add(c.new detectAnomaly());
		commands.add(c.new displayResult());
		commands.add(c.new uploadAndAnalyze());
		commands.add(c.new exit());
		// implement
	}
	
	public void start() {
		this.commands.get(0).execute();
		
	}
}
