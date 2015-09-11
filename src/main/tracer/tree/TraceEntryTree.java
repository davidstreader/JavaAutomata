package main.tracer.tree;

import java.util.List;
import java.util.Stack;

import main.tracer.MethodKey;
import main.tracer.TraceEntry;

/**
 * {@code TraceEntryTree} represents a tree of the method calls executed within
 * a Java program. A {@code TraceEntryTreeNode} in the {@code TraceEntryTree} represents
 * a method execution and contains information regarding the state of the program
 * before and after execution of that method. Children of each {@code TraceEntryTreeNode}
 * represents methods executed within that particular method.
 *
 * @author David Sheridan
 *
 */
public class TraceEntryTree {

	// fields
	private TraceEntryTreeNode root;

	/**
	 * Constructs a new instance of a {@code TraceEntryTree} with
	 * the specified {@code TraceEntryTreeNode} as the root.
	 *
	 * @param root
	 * 		- root of the tree
	 */
	protected TraceEntryTree(TraceEntryTreeNode root){
		this.root = root;
	}

	/**
	 * Generates a {@code TraceEntryTree} from the specified list of {@code TraceEntries}.
	 *
	 * @param entries
	 * 		- list of entries
	 * @return
	 * 		- {@code TraceEntryTree}
	 */
	public static TraceEntryTree generateTraceEntryTree(List<TraceEntry> entries){
		TraceEntryTreeNode root = null;

		Stack<TraceEntryTreeNode> treeNodes = new Stack<TraceEntryTreeNode>();

		// iterate through entries and construct tree
		for(int i = 0; i < entries.size(); i++){
			// create root on first iteration of loop
			if(i == 0){
				root = new TraceEntryTreeNode(entries.get(i).getState());
				treeNodes.push(root);
			}

			// check if current entry is entering or exiting method
			else if(!entries.get(i).isReturn()){
				TraceEntryTreeNode current = new TraceEntryTreeNode(entries.get(i).getState());
				treeNodes.peek().addChild(current);
				treeNodes.push(current);
			}
			else{
				TraceEntryTreeNode previous = treeNodes.pop();
				previous.setStateAfter(entries.get(i).getState());
			}
		}

		return new TraceEntryTree(root);
	}
}