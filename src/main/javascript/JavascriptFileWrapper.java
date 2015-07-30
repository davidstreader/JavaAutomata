package main.javascript;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.xml.sax.SAXException;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptFileWrapper {

	private String filename;
	private ScriptEngine engine;
	private Invocable invocable;

	/**
	 * <p>
	 * creates a wrapper around a specified javascript file, allowing acess to
	 * it's fuinctions.
	 * </p>
	 * <p>
	 * Use:
	 * </p>
	 * <p>
	 * >create a new JavascriptFileWrapper(), passing in the filename of the
	 * javascript file which's functions you want to call.
	 * </p>
	 * <p>
	 * >use invokeJavascriptMethod(functionName, params...) to invoke the
	 * function you want, passing in all required arguments, either as an Object
	 * array, or as a varags.. param.
	 * </p>
	 * 
	 * @param filename
	 *            - name of the javaScript file that contains functions you want
	 *            to call
	 * @throws ScriptException
	 * @throws FileNotFoundException
	 */
	public JavascriptFileWrapper(String filename) throws FileNotFoundException, IllegalArgumentException {
		File f = new File(filename);
		if(!f.exists())
			throw new FileNotFoundException("file " +filename+" doesnt exist");
		if (f.isDirectory())
			throw new IllegalArgumentException(filename+" specified points to a directory");
		
		this.filename = filename;
		this.engine = new ScriptEngineManager().getEngineByName("nashorn");
		try {
			engine.eval(new FileReader(filename));
			
		} catch (ScriptException se) {
			se.printStackTrace();
		} catch (FileNotFoundException fe) { // hopefully this doesn't throw here as opposed to above
			fe.printStackTrace();
		}
		this.invocable = (Invocable) engine;				
	}

	/**
	 * calls specified function from the wrapped javascript file
	 * 
	 * @param methodName
	 *            -name of the javascript function you want to call
	 * @param parameters
	 *            -variable length of arguments to be passed to function
	 * @return -returns the object returned by the javascript function
	 */
	public Object invokeJavascriptMethod(String methodName, Object... parameters) {
		Object ret = null;
		try {
			ret = invocable.invokeFunction(methodName, parameters);
		} catch (NoSuchMethodException me) {
			me.printStackTrace();
		} catch (ScriptException se) {
			se.printStackTrace();
		}
		return ret;
	}

	/**
	 * @return -filename of loaded javascript file
	 */
	public String filename() {
		return this.filename;
	}

	//TODO add way to look at variables or functions?
	 

	/*public static String fun1(String name) {
		System.out.format("Hi there from Java, %s \n", name);
		return "greetings from java";
	}

	
	
	 public static String fun1(String name) {
	 System.out.format("Hi there from Java, %s \n", name);
	 return "greetings from java";
	 }
	 */

	//
	// public static void fun3(ScriptObjectMirror mirror) throws IOException,
	// SAXException {
	// System.out.println(mirror.getClassName() + ": " +
	// Arrays.toString(mirror.getOwnKeys(true)));
	// File htmlFile = new File("index.html");
	// Desktop.getDesktop().browse(htmlFile.toURI());
	// }
	//

	// static void fun4(ScriptObjectMirror person) throws IOException {
	// System.out.println("Full Name is: " + person.callMember("getFullName"));
	//
	// }

	/**
	 * just for testing *
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws ScriptException
	 */
	/*public static void main(String[] args) throws FileNotFoundException, ScriptException {
		JavascriptFileWrapper i = new JavascriptFileWrapper("src/main/javascript/script.js");
		i.invokeJavascriptMethod("fun1", "chris");
	}*/

}
