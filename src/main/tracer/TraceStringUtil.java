package main.tracer;

import java.util.HashSet;
import java.util.Set;

public class TraceStringUtil {

	private Trace trace;

	public TraceStringUtil(Trace trace){
		this.trace = trace;
	}

	/**
	 * Returns all of the fields of the program traced
	 * */
	public Set<String> getCompareMethods(){
		//the set of method names
		Set<String> methods = new HashSet<>();

		//place the names in set to remove duplicates

			for(TraceEntry te : trace.getLines()){
				methods.add(te.getMethodName());
		}
		return methods;
	}

	public Set<String> getDisplayMethods(){
		//the set of method names
		Set<String> methods = new HashSet<>();

		//place the names in set to remove duplicates

		for(TraceEntry te : trace.getLines()){
			methods.add(te.getMethod().getName());
		}

		return methods;
	}


	/**
	 * Returns all of the parameters of the program traced
	 * */
	public String getFields(){
		//the string to return
		String fields  = "Fields: \n";

		//the set of method names
		Set<String> fieldSet = new HashSet<>();


		//place the names in set to remove duplicates

			for(TraceEntry te : trace.getLines()){
				if(te.getState() != null)fieldSet.add(te.getState().toString());
		}

		//add each of the unique method names to the string
		for(String s : fieldSet){
				fields += "\t " + s + "\n";
		}
		return fields;
	}


	/**
	 * Returns a String containing the method names of all the methodEntry events.
	 * */
	public String methodEntryEvent(){
			//the string to return
				String entryEvents  = "Entry Events: \n";

				//the set of method names
				Set<String> methods = new HashSet<>();


				//place the names in set to remove duplicates

					for(TraceEntry te : trace.getLines()){
						if(!te.isExit())methods.add(te.getLongMethodName());
					}

				//add each of the unique method names to the string
				for(String s : methods){
						entryEvents += "\t " + s + "\n";
				}
				return entryEvents;
	}


	/**
	 * Returns a String containing the method names of all the methodExit events.
	 * */
	public String methodExitEvent(){
		//the string to return
			String exitEvents  = "Exit Events: \n";

			//the set of method names
			Set<String> methods = new HashSet<>();


			//place the names in set to remove duplicates
			for(TraceEntry te : trace.getLines()){
				if(te.isExit())methods.add(te.getLongMethodName());
			}

			//add each of the unique method names to the string
			for(String s : methods){
					exitEvents += "\t " + s + "\n";
			}
			return exitEvents;
	}
}
