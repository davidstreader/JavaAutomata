package main.tracer;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.jar.Attributes.Name;

import main.Main;
import main.load.JarData;
import main.load.JarLoader;

public class TraceLauncher {

	//the path to the jar to be executed
	private String jarPath;


	//the arguments to execute the traced program with
	//private String commanLineArgumnets;
	private String commandLineArguments;

	private TraceFilter filter;


	/**
	 * Sets the command line arguments for the program to be executed
	 * with
	 *
	 * @param arguments for program
	 * */
	public void setCommandLineArguments(String arguments){
		this.commandLineArguments = arguments;
	}

	/**
	 * Sets the filter for the program to be executed
	 * with
	 *
	 * @param filter for the program
	 * */
	public void setFilter(TraceFilter filter){
		this.filter = filter;
	}


	/**
	 * Constructs the TraceLauncher object with the pathname of the jar
	 *
	 * @param path of the jar to execute
	 * */
	public TraceLauncher(String JarPathName){
		this.jarPath = JarPathName;
		//this.executions = new ArrayList<ExecutionData>(Arrays.asList(new ExecutionData()));
	}


	/**
	 * Runs the jar file and generates and returns the traces specified by the jarPath
	 *
	 * @return The traces generated by the jar
	 * */
	public Trace run(){
		if(jarPath == null)return null;

		//The file of the jar to trace
		File file = new File(jarPath);

		//loads in the jar file
		JarData jd = JarLoader.loadJarFile(file);


		//the path of the jar file
		jarPath = jd.getFile().getAbsolutePath();


		//grabs the main class of the jar file
		final String mainClass = jd.getManifest().getMainAttributes().getValue(Name.MAIN_CLASS);

		//grabs the classes from the jar file
		final Set<String> loadedClasses = new HashSet<String>();
		for (Class<?> cl : jd.getClasses()){
			loadedClasses.add(cl.getName());
		}
		System.out.println(commandLineArguments);

		TestThread thread = new TestThread(loadedClasses, commandLineArguments, this.filter, mainClass);

		//set thread options and start
		thread.setName("MainWindow tracer thread");
		thread.setDaemon(true);
		thread.start();

		//returns the generated traces
		return thread.getTraces();
	}


	private class TestThread extends Thread{

		//loaded classes of the program
		private Set<String> loadedClasses;

		//private ExecutionData[] executionsArray;
		private String mainClass;

		//the arguments to run the program with
		private String commanLineArgumnets;

		//the filter to filter the program with
		private TraceFilter filter;


		//the trace of the program
		private Trace trace;

		public TestThread(Set<String> loadedClasses, String commandLineArguments, TraceFilter filter, String mainClass){
			this.loadedClasses = loadedClasses;
			this.commanLineArgumnets = commandLineArguments;
			this.mainClass = mainClass;
			this.filter = filter;
		}

		@Override
		public void start(){
			run();
		}

		@Override
		public void run() {
			//Creates a filter that filters class names from the jar
			TraceFilter initialFilter = new TraceFilter() {
				@Override
				public boolean isMethodTraced(MethodKey m) {
					return loadedClasses.contains(m.getClassName());
				}

				@Override
				public boolean isFieldTraced(FieldKey f) {
					return loadedClasses.contains(f.getClassName());
				}

				@Override
				public boolean isParameterTraced(ParameterKey p) {
					return true;
				}
			};

			//check if a filter has been set
			if(this.filter != null){
				initialFilter = this.filter;
			}

				FutureTraceConsumer future = new FutureTraceConsumer();
				try {
					Tracer.launchAndTraceAsync("-cp \"" + jarPath + "\"",
							mainClass + " " + commanLineArgumnets,
							initialFilter, future);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					trace = future.get();
				} catch (InterruptedException | ExecutionException e) {
					Main.printToWindow("Multithreading not Supported");

					e.printStackTrace();
					return;
				}
		}


		/**
		 * Returns the trace from the executed program
		 *
		 * @return The generated trace
		 * */
		public Trace getTraces(){
			return this.trace;
		}
	}
}
