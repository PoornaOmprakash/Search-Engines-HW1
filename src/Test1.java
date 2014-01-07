
public class Test1 {
	static String s;//="#AND ( barack #OR ( obama president ) usa )";
	//static int n=0;
	static String [] tokens;
	static int length=0;
	static TreeNode tree=null;
	static int i=0;
	static TreeNode root=null;
	Test1(String str)
	{
		s=str;
	}
	Test1()
	{
		
	}
	  
	public static void main(String[] args) throws Exception
	{
		Test1 t=new Test1("#OR ( obama family tree )");
		String delims = "[ ]";
		tokens = s.split(delims);   //tokens[] now contains individual query terms (with operators)
		length=tokens.length;
		t.create_tree(root);
	}
    
	void create_tree(TreeNode rooti)
    {
    	TreeNode temp=new TreeNode();
    	if (i==length)
    		return;
    	if(tokens[i].startsWith("#"))
    		{
    		  temp=new TreeNode(tokens[i]);
    		  if (i!=0)      //Check whether it is the root node or not
    		  {
    		    temp.parent=rooti;
    		    rooti.addChild(temp);
    		    
    		  }
    		  if(i==0)
    		  {
    			  root=new TreeNode(temp);
    		  }
    		  i=i+2;
    		  //System.out.println(temp.q.term);
    		  create_tree(temp);
    		} 
    	
    	else if(tokens[i].equals(")"))
    		{
    		  i++;
    		  create_tree(rooti.parent);
    		}
    	
    	else
    	{
    		temp=new TreeNode(tokens[i]);
    		temp.parent=new TreeNode(rooti);
    		//System.out.println(temp.parent.q.term);
    		//System.out.println(temp.q.term);
    	    i++;
    	    rooti.addChild(temp);
    	    create_tree(temp.parent);
    	}
    }
}
