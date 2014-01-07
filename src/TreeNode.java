import java.util.ArrayList;
import java.util.List;


public class TreeNode {         //Accepts a string and casts it into QryopTerm type to create the node
	    String q;
	    TreeNode parent;
	    boolean isLeaf;
	    ArrayList <TreeNode> children = new ArrayList<TreeNode>();
	  
	    TreeNode()
	    {
	    	children = new ArrayList<TreeNode>();
	    	isLeaf=false;
	        q = null;
	        this.parent = null;
	        //this.children=null;
	    }
	    
	    TreeNode(String s1)
	    {
	    	children = new ArrayList<TreeNode>();
	    	q=s1;
	        this.parent = null;
	    }
	    
	    TreeNode(TreeNode tn)
	    {
	    	children = new ArrayList<TreeNode>();
	        this.q = tn.q;
	        this.parent = tn.parent;
	        this.children=tn.children;
	    }
	    
	    void addChild(TreeNode n)
	    {
	    	this.children.add(n);
	    }
	}
