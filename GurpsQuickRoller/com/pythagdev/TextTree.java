package com.pythagdev;

import java.util.Stack;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class TextTree {

	private JTree tree;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
	private DefaultMutableTreeNode currNode = root;
	private Stack<DefaultMutableTreeNode> nodeStack = new Stack<DefaultMutableTreeNode>();
	private DefaultTreeModel dtm;
	
    public TextTree()
    {
        // Create the tree by passing in the root node
        tree = new JTree(root);
        tree.setRootVisible(false);
        dtm = (DefaultTreeModel) tree.getModel();
        
        // Remove the default folder icons
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
    	renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        
        nodeStack.push(root);
    }
    
    public JTree getTree() {
    	return tree;
    }
    
    // Add future text under the most recent entry
    public void addLevel() {
    	if (currNode != root)
    		nodeStack.push(currNode);
    }
    
    // Add future text up a level
    public void endLevel() {
    	// Pop and graphically expand
    	currNode = nodeStack.pop();
    	tree.scrollPathToVisible(new TreePath(currNode.getPath()));
    }
    
    // Add a text entry
    public void addText(String text) {
    	if (text == null)
    		return;
    	currNode = new DefaultMutableTreeNode(text);	// Create the text node
    	DefaultMutableTreeNode parentNode = nodeStack.peek();
    	parentNode.add(currNode);						// Add the child node to the stack parent node
    	dtm.nodesWereInserted( parentNode, new int[] { parentNode.getIndex( currNode ) } );		// Refresh
    }
}
