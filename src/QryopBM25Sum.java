import java.io.IOException;
public class QryopBM25Sum extends Qryop {
	
	  public QryopBM25Sum(Qryop... q) {       //obama family tree
	    for (int i = 0; i < q.length; i++)
	      this.args.add(q[i]);
	  }

	  /**
	   * Evaluate the query operator.
	   */
	  public QryResult evaluate() throws IOException {
		  
		  Qryop impliedQryOp = new QryopScore(args.get(0));       // Seed the result list with the first query term
		     QryResult result = impliedQryOp.evaluate();             // Contains the docScores for the first query term

		    //QryResult result = null;
			  // Each pass of the loop evaluates one query argument.
		    for (int i = 1; i < args.size(); i++) {
	         //System.out.println(i);
		      impliedQryOp = new QryopScore(args.get(i));          // Gets next query term
		      QryResult iResult = impliedQryOp.evaluate();         // Contains the docScores for the i'th query term (Doc ID's that contain the required query trem)

		      int rDoc = 0; /* Index of a document in result. */
		      int iDoc = 0; /* Index of a document in iResult. */       // docScores from the i'th query term

		      while (rDoc < result.docScores.scores.size()) {
                //System.out.println(rDoc);
		        while ((iDoc < iResult.docScores.scores.size())
		            && (result.docScores.getDocid(rDoc) > iResult.docScores.getDocid(iDoc))) {
		            result.docScores.scores.add(rDoc,iResult.docScores.scores.get(iDoc));
		        	rDoc++;
		            iDoc++;
		        }

		        if ((iDoc < iResult.docScores.scores.size()) && (result.docScores.getDocid(rDoc) == iResult.docScores.getDocid(iDoc)))
		        	{
		        	   float score=result.docScores.getDocidScore(rDoc)+iResult.docScores.getDocidScore(iDoc);
		        	   result.docScores.setDocidScore (rDoc,score);  //Ranked Bookean
		        	rDoc++;
		          iDoc++;
		       }
		        else
		        	rDoc++;
		      }
		    }
		     
		    return result;
		  }
	  
	  
	  
		    
		  /*Qryop impliedQryOp = new QryopScore(args.get(0));
		    QryResult result = impliedQryOp.evaluate();
            
		    //System.out.println(args.size());
		    // Each pass of the loop evaluates one query argument.
		    for (int i = 1; i < args.size(); i++) {
              //System.out.println(i);
		      impliedQryOp = new QryopScore(args.get(i));
		      QryResult iResult = impliedQryOp.evaluate();

		      // Use the results of the i'th argument to incrementally compute the query operator.
		      int rDoc = 0; /* Index of a document in result. 
		      int iDoc = 0; /* Index of a document in iResult. 
              //System.out.println(result.docScores.scores.size());
		      while (rDoc < result.docScores.scores.size()) {    
               //System.out.println(rDoc);
		        // If a term occurs only in iResult, add it to result with same score (0 default score for term in result)
		        while ((iDoc < iResult.docScores.scores.size())
		            && (result.docScores.getDocid(rDoc) > iResult.docScores.getDocid(iDoc))) {  //These are all the terms that occur in iDoc but not rDoc
		            result.docScores.scores.add(rDoc,iResult.docScores.scores.get(iDoc));
		        	iDoc++;
		        	rDoc++;
		        }

		        // If the rDoc document appears in both lists, update the score for the document by adding scores of individual query terms.
		        if ((iDoc < iResult.docScores.scores.size())
		            && (result.docScores.getDocid(rDoc) == iResult.docScores.getDocid(iDoc))) {
		         float score=result.docScores.getDocidScore(rDoc)+iResult.docScores.getDocidScore(iDoc);
		         result.docScores.setDocidScore(rDoc, score);
		          rDoc++;
		          iDoc++;
		        } 
		         else {     //No need to remove any elements (I think?)
		           rDoc++;//result.docScores.scores.remove(rDoc);
		       }
		      }
		    }

		    return result;*/
		  }
	
