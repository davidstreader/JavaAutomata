package main.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import main.Main;
import main.load.JarData;
import main.load.JarLoader;
import main.parse.Automata;
import main.parse.GeneralFormatToAutomata;
import main.parse.JSONToAutomata;
import main.tracer.TraceLauncher;
import main.tracer.Trace;
import main.tracer.TraceManager;
import netscape.javascript.JSObject;

/**
 * First menu people see when loading the program.
 *
 * @author brewershan
 *
 */
public class MainPane extends GridPane {

	// private Browser b; // reference to the browser for javascript calls
	private int count;// number of browser windows currently open.
	Map<Integer, BrowserBox> browserWindows = new HashMap<Integer, BrowserBox>();// holds
																					// references
																					// to
																					// diff
																					// windows

	private MenuPane parent;

	/**
	 * Constructs the menu Pane
	 */
	public MainPane(MenuPane parent) {
		count = 0;
		this.parent = parent;
		setUpLoadMenu();
		setUpSaveMenu();
		setUpViewMenu();

		this.prefWidth(Double.MAX_VALUE);
	}

	/**
	 * Sets up the button layout for the Load section of the pane.
	 */
	private void setUpLoadMenu() {
		Button btn = new Button();

		// Text field to be used
		TextField loadDisplay = new TextField();
		loadDisplay.setEditable(false);
		this.add(loadDisplay, 1, 0);

		// Sets up the Jar Load button.
		btn.setMaxWidth(Double.MAX_VALUE);
		btn.setText("Load Jar");
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				File file = JarFileChooser.chooseJarFile();
				if (file != null) {
					loadDisplay.setText(file.getName());
					JarData jarData = JarLoader.loadJarFile(file);
					Main.setJarData(jarData);
					// System.out.println(parent);
					// System.out.println(parent.getSelectionPane());
					parent.getSelectionPane().makeNewTree();
				} else {
					loadDisplay.setText("");
				}

			}

		});
		this.add(btn, 0, 0);
		GridPane.setHgrow(btn, Priority.ALWAYS);

		// Sets up the Load Trace Program.
		btn = new Button();
		btn.setMaxWidth(Double.MAX_VALUE);
		btn.setText("Load Trace");
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				// TODO: Set up trace loading.
				System.out.println("TODO: load trace");
			}
		});
		this.add(btn, 0, 1);
		GridPane.setHgrow(btn, Priority.ALWAYS);

		btn = new Button();
		btn.setMaxWidth(Double.MAX_VALUE);
		btn.setText("Run Trace");
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				TraceLauncher tracer = new TraceLauncher(Main.getJarData().getFile().getAbsolutePath());
				Trace[] tr = tracer.run();
				System.out.println(tr == null);
				TraceManager manager = new TraceManager(tr);
				Main.setManager(manager);
			}
		});
		this.add(btn, 1, 1);
		GridPane.setHgrow(btn, Priority.ALWAYS);

	}

	// convertTraceToJson() TODO

	/**
	 * Sets up the Save section of the menu
	 */
	private void setUpSaveMenu() {
		Button btn = new Button();

		// Text field for user input.
		TextField loadDisplay = new TextField();
		this.add(loadDisplay, 1, 3);

		// Sets up the save button
		btn.setMaxWidth(Double.MAX_VALUE);
		btn.setText("Save Trace");
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				String fileName = loadDisplay.getText();
				if (fileName.equals("")) {
				} else {
					Main.getManager().traceToFile("data/traces/", fileName);
				}
				System.out.println(fileName + " TODO: Trace Saveing");
			}

		});
		this.add(btn, 0, 3);
	}

	/**
	 * Sets up the view button, opens browser window to view trace
	 */
	private void setUpViewMenu() {
		Button btn = new Button();
		// Sets up the load view button
		btn.setMaxWidth(Double.MAX_VALUE);
		btn.setText("Load View");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				// grab the traces
				//data/traces/
				
				/*
				 * File f = new File("data/traces/test.json");//TODO Automata
				 * auto = JSONToAutomata.generateAutomata(f);
				 * GeneralFormatToAutomata g = new
				 * GeneralFormatToAutomata(auto); String json =
				 * g.parseAutomata();
				 */
				
				File fi = new File("src/web/test/linearAutomata.json");
				Scanner scan;
				String str = "";
				try {
					scan = new Scanner(fi);
					while (scan.hasNextLine()) {
						str += scan.nextLine();
					}
				} catch (FileNotFoundException e1) {

					e1.printStackTrace();
				}

				BrowserBox bb = new BrowserBox(str);
				browserWindows.put(count++, bb);// add cb to hash map
				// bb.visualizeTrace(str); now handled internally
			}
		});
		this.add(btn, 0, 4);
	}

	/**
	 * Object that creates a new window containing a browser that is used to
	 * visualize out data. Usage: Create a new BrowserBox(dat), passing in the
	 * data (in stringified json format) you want to visualise
	 * 
	 * @author rj
	 *
	 */
	private class BrowserBox {
		private Scene scene; // (Browser) scene for calls to page
		private Stage stage; // for calls to the java- bits

		private boolean loaded = false;

		private String data;

		/**
		 * creates a new Stage that contains a Scene that contains the Browser
		 * that displays the visualization
		 *
		 * after browser creation, i don't think we need to save the references
		 * to the Scene or Stage, just the Browser (for calling script on it)
		 * this may need to be changed in the future.
		 * 
		 * @param dat
		 *            data that is to be loaded in to the visualization once the
		 *            page has loaded
		 */

		public BrowserBox(String dat) {
			this.data = dat;
			scene = new Scene(new Browser(), 700, 700, Color.web("#666970"));

			stage = new Stage();
			stage.setTitle("Visualization");
			stage.setScene(scene);
			stage.show();

			/*
			 * this next piece of code adds a listener to the browser's
			 * loadworker, which changes 'loaded' variable to true when the page
			 * is loaded. after it's loaded we're allowed to call javascript on
			 * it.
			 */
			WebView wv;
			for (Object o : scene.getRoot().getChildrenUnmodifiable()) {
				if (o instanceof WebView) {
					wv = (WebView) o;
					WebEngine enge = wv.getEngine(); 
					enge.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) ->
					{
					    JSObject window = (JSObject) enge.executeScript("window");
					    JavaBridge bridge = new JavaBridge();
					    window.setMember("java", bridge);
					    enge.executeScript("console.log = function(message)\n" +
					        "{\n" +
					        "    java.log(message);\n" +
					        "};");
					});
					enge.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
						@SuppressWarnings("rawtypes")
						public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
							if (newState == State.SUCCEEDED) {
								loaded = true;
								visualizeTrace(data);
							}
						}
					});

				}
			}
			
			
			
		}

		/**
		 * returns a reference to the newly constructed browser
		 *
		 * @return the Browser that just got created.
		 */
		/*
		 * public Browser getReference(){ return (Browser) scene.getRoot(); }
		 */

		/**
		 * use visualizeTrace() instead
		 *
		 * @return the Browser in this scene
		 */
		@Deprecated
		public Browser Browser() {
			return (Browser) scene.getRoot();
		}

		public Stage Stage() {
			return stage;
		}

		/**
		 * gives jsonString to browser to visualise
		 * 
		 * @param jsonString
		 *            string version of Jon object with data to visualise
		 */
		public void visualizeTrace(String jsonString) {
			Browser br = (Browser) scene.getRoot();
			
			String arg = "viz.automata.init(JSON.stringify({" + jsonString + "}))"; // this is
																		// wrong
																		// method
			//System.out.println(arg);
			br.executeScript(arg);// TODO check this works, that this is the
			// right context to call jscript
		}
	}
}
