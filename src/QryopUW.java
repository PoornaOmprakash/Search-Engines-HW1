import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class QryopUW extends Qryop {
		int near;
	  public QryopUW(int n, Qryop... q) {
	    for (int i = 0; i < q.length; i++)
	      {
	    	this.near=n;
	    	this.args.add(q[i]);
	      }
	  }
	    public QryopUW(int n) {
		    	this.near=n;
	  }


	  public QryResult evaluate() throws IOException {
	    //Qryop impliedQryOp = new QryopScore(args.get(0));
	    QryResult result = args.get(0).evaluate();
        Map <Qryop,QryResult> iResult = new HashMap<Qryop,QryResult>();
	    //QryResult [] iResult;
	    // Each pass of the loop evaluates one query argument.
	    for (int i = 1; i < args.size(); i++) {          //Store results for all query terms in an array

	      //impliedQryOp = new QryopScore(args.get(i));
	      iResult[i] = args.get(i).evaluate();
	    }
	    
	    for(int i=0;i<args.size();i++)   //Advance the pointers in each iDoc until a match is found
	    {
	    int rDoc = 0; /* Index of a document in result. */
	      int [] iDoc; /* Index of a document in iResult. */
	      //int flag=0;
	      //Use postpos instead of iDoc      
	        // Check for documents matched by both at a distance <=n
	        while ((iDoc[i] < iResult[i].invertedList.postings.size())&&(result.invertedList.postings.get(rDoc).docid > iResult[i].invertedList.postings.get(iDoc[i]).docid))
	        {
	        	iDoc[i]++;
	        }
	      }
	  while (rDoc < result.invertedList.postings.size()) {   //Number of documents containing the first query term
	        int x=0,y=0;   //Index for the document in each posting - In rDoc's while loop
	        //System.out.println(iDoc);
	           if(result.invertedList.postings.get(rDoc).docid==iResult[i].invertedList.postings.get(iDoc[i]).docid)  //DocIDs match
	           { 
	        	   //System.out.println(iDoc);
	        		while(x<result.invertedList.postings.get(rDoc).positions.size())//;x++)
	        	        {
	        			 while ((y < iResult.invertedList.postings.get(iDoc).positions.size()) &&(result.invertedList.postings.get(rDoc).positions.get(x) > iResult.invertedList.postings.get(iDoc).positions.get(y)))
	        		        {
	        		           y++;
	        		        }
	        	   		if((y < iResult.invertedList.postings.get(iDoc).positions.size())&&(iResult.invertedList.postings.get(iDoc).positions.get(y)-result.invertedList.postings.get(rDoc).positions.get(x)<=Math.abs(near)))  //Match
	        			  {
	        				result.invertedList.postings.get(rDoc).positions.set(x,iResult.invertedList.postings.get(iDoc).positions.get(y));
	        				x++;  //Location in rDoc
	        				y++;   //Location in iDoc
	        				//flag=1;
	        			  }
	        			//else if (result.invertedList.postings.get(p).positions.get(x)<iResult.invertedList.postings.get(q).positions.get(y))
	        				//x++;
	        			else
	        				{    //Remove if there is no match
	        				  result.invertedList.postings.get(rDoc).positions.remove(x);
	        				  result.invertedList.postings.get(rDoc).tf--;
	        				  result.invertedList.ctf--;
	        				}
	        	        }
	        	if(result.invertedList.postings.get(rDoc).positions.size()<=0)   //Term does not occur in the document
	           {
	        	   result.invertedList.postings.remove(rDoc);
	        	   result.invertedList.df--;
	        	}
	           else
	           {
	        	   rDoc++;
	           }
	      iDoc++;
	      }	
	           else// If the rDoc document appears in both lists, keep it, otherwise discard it.
	           {
	        	   result.invertedList.postings.remove(rDoc);
	        	   result.invertedList.df--;
	           }
	      }
	    }

	    return result;
	  }

}
