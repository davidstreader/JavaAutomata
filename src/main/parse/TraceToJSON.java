package main.parse;

import org.json.JSONArray;
import org.json.JSONObject;

import main.tracer.state.State;
import main.tracer.tree.TraceEntryTree;
import main.tracer.tree.TraceEntryTreeNode;

/**
 * Class for converting the {@code TraceEntryTree} from a trace output of a Java program
 * into a JSON format.
 *
 * @author David Sheridan
 *
 */
public class TraceToJSON {

	public static final String METHOD_NAME = "methodName";
	public static final String STATE_BEFORE = "stateBefore";
	public static final String STATE_AFTER = "stateAfter";
	public static final String CHILDREN = "children";
	public static final String START_STATE = "startState";
	public static final int INDENT_FACTOR = 2;
	public static final JSONObject EMPTY_JSON_OBJECT = new JSONObject();

	/**
	 * Converts the specified {@code TraceEntryTree} into a JSON {@code String}.
	 *
	 * @param tree
	 * 		- the tree to be converted
	 * @return
	 * 		- json representation of tree
	 */
	public static String generateJSON(TraceEntryTree tree){
		JSONObject trace = convertTraceEntryTreeNodeToJSON(tree.getRoot());
		return trace.toString(INDENT_FACTOR);
	}

	/**
	 * Converts the specified {@code TraceEntryTreeNode} into a {@code JSONObject}.
	 * Recursively calls this method on any children that the {@TraceEntryTreeNode} may
	 * have.
	 *
	 * @param node
	 * 		- the node to be converted
	 * @return
	 * 		- json representation of node
	 */
	private static JSONObject convertTraceEntryTreeNodeToJSON(TraceEntryTreeNode node){
		JSONObject json = new JSONObject();
		json.append(METHOD_NAME, node.getMethodName());

		State before = node.getStateBefore();
		if(before == null){
			json.put(STATE_BEFORE, EMPTY_JSON_OBJECT);
		}
		else{
			json.put(STATE_BEFORE, node.getStateBefore().toJSON());
		}

		State after = node.getStateAfter();
		if(after == null){
			json.put(STATE_AFTER, EMPTY_JSON_OBJECT);
		}
		else{
			json.put(STATE_AFTER, node.getStateAfter().toJSON());
		}

		json.append(START_STATE, node.isStartState());

		// get children
		JSONArray children = new JSONArray();
		for(TraceEntryTreeNode child : node.getChildren()){
			children.put(convertTraceEntryTreeNodeToJSON(child));
		}
		json.put(CHILDREN, children);

		return json;
	}
}
