package main.tracer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import main.tracer.state.State;
import main.tracer.tree.TraceEntryTree;

public class Trace implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<TraceEntry> lines;

	public Trace(){
		lines = new ArrayList<TraceEntry>();
	}

	public List<TraceEntry> getLines(){
		return lines;
	}

	public void setLines(List<TraceEntry> lines) {
		this.lines = lines;
	}

	public void applyFilter(TraceFilter f) {
		Iterator<TraceEntry> it = lines.iterator();

		for(int i = 0; i < lines.size(); i++){

			MethodKey meth = lines.get(i).method;

			if(!f.isMethodTraced(meth)){
				System.out.println("Removed " + lines.get(i).method);
				lines.remove(i);//new
				i--;
			}
		}

		for(TraceEntry te : lines){
			te.filterFields(f);
		}
	}

	@Override
	public String toString(){
		String toReturn = "[\n";


		for(int i = 0; i < lines.size(); i++){
			toReturn += lines.get(i).toString();
			toReturn += (i < lines.size() - 1) ? ",\n" : "\n";
		}

		toReturn += "]";
		return toReturn;
	}

	/*
	private String toJSON(){
		StringBuilder builder = new StringBuilder();
		builder.append(State.OPEN_BRACKET + "\n");

		for(int i = 0; i < lines.size(); i++){
			builder.append(lines.get(i).toString());
			if(i != lines.size() - 1){
				builder.append(",");
			}
			builder.append("\n");
		}

		builder.append(State.CLOSE_BRACKET);
		return builder.toString();
	}
	*/

	public void constructJSONFile(String filename){
		TraceEntryTree.generateTraceEntryTree(lines);
		String path = "data" + File.separatorChar + "traces" + File.separatorChar;
		FileWriter writer;
		try {
			writer = new FileWriter(path + filename + ".json");
			writer.write(toString());
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}


}
