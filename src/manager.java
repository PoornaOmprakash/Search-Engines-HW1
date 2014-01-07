import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class manager {
	static QryopOr root = new QryopOr();
	public static List<String> get_tokens(String line){
		List<String> tokens = new ArrayList<String>();
		while (line.length()>0){
			//System.out.println(line);
			if(line.startsWith(" "))
				line=line.substring(1);
			if(line.startsWith("#AND")){
				tokens.add("#AND");
				line = line.substring(4);
			}
			else if(line.startsWith("#OR")){
				tokens.add("#OR");
				line = line.substring(3);
			}
			else if(line.startsWith("#NEAR")){
				int index=line.indexOf('(');
				String sub=line.substring(0,index);
				tokens.add(sub);
				line = line.substring(index);
			}
			else if(line.startsWith("(")){
				tokens.add("(");
				line = line.substring(1);
			}
			else if(line.startsWith(")")){
				tokens.add(")");
				line = line.substring(1);
			}
			else{
				Pattern nextToken = Pattern.compile("^\\w+");
				Matcher nextTokenMatcher = nextToken.matcher(line);
				
				if(nextTokenMatcher.find()){
					String md = nextTokenMatcher.group();
					String[] tok=null;
					try {
						tok = QryEval.tokenizeQuery(md);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					if(tok.length!=0)
					   tokens.add(tok[0]);
					line = line.substring(md.length());
				}
			}
			
		}
		/*for(String s:tokens)
			System.out.println(s);*/
		return tokens;
	}
    
  public static void parse_tree(List<String> tokens){
	  Stack<Qryop> st = new Stack <Qryop> ();  
	  int n;
	    st.push(root);
		if(st.size()==0){
            return;
		}
		while (tokens.size()>0){
			String token = tokens.remove(0);
			if(token.equals("#AND")){
				Qryop op = new QryopAnd();
				st.lastElement().args.add(op);
				st.push(op);
			}
			else if(token.equals("#OR")){
				Qryop op = new QryopOr();
				st.lastElement().args.add(op);
				st.push(op);
			}
			else if(token.startsWith("#NEAR"))
			{
				String [] sub=token.split("/");
				n=Integer.parseInt(sub[1]);
				Qryop op = new QryopNear(n);
				st.lastElement().args.add(op);
				st.push(op);
			}
				
			else if(token.equals("(")){
				//nothing
			}
			else if(token.equals(")")){
				st.pop();
			}
			else {
				if(token.contains("."))           //Field based search
				{
					String [] temp = token.split(".");
					st.lastElement().args.add(new QryopTerm(temp[0],temp[1]));
				}
				else
				   st.lastElement().args.add(new QryopTerm(token));
			}
		}
	}
}
