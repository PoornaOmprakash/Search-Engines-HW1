/*
 *  Copyright (c) 2013, Carnegie Mellon University.  All Rights Reserved.
 */

import java.io.IOException;
import java.util.Collections;

public class QryopAnd extends Qryop {
int qlen;
  /**
   * It is convenient for the constructor to accept a variable number of arguments. Thus new
   * qryopAnd (arg1, arg2, arg3, ...).
   */
  public QryopAnd(Qryop... q) {
    for (int i = 0; i < q.length; i++)
       this.args.add(q[i]);
       qlen=q.length;
       //System.out.println(q);
  }

  /**
   * Evaluate the query operator.
   */
  public QryResult evaluate() throws IOException {

    // Seed the result list by evaluating the first query argument. The result could be docScores or
    // invList, depending on the query operator. Wrap a SCORE query operator around it to force it
    // to be a docScores list.
    Qryop impliedQryOp = new QryopScore(args.get(0));
    QryResult result = impliedQryOp.evaluate();

    // Each pass of the loop evaluates one query argument.
    for (int i = 1; i < args.size(); i++) {
      //System.out.println(i);
      impliedQryOp = new QryopScore(args.get(i));
      QryResult iResult = impliedQryOp.evaluate();

      // Use the results of the i'th argument to incrementally compute the query operator.
      // Intersection-style query operators iterate over the incremental results, not the results of
      // the i'th query argument.
      int rDoc = 0; /* Index of a document in result. */
      int iDoc = 0; /* Index of a document in iResult. */

      while (rDoc < result.docScores.scores.size()) {
        if(QryEval.retrievalAlgortihm.equals("RankedBoolean")|(QryEval.retrievalAlgortihm.equals("UnrankedBoolean")))
        {
          while ((iDoc < iResult.docScores.scores.size())      //Number of documents containing the query term
            && (result.docScores.getDocid(rDoc) > iResult.docScores.getDocid(iDoc))) {
                  iDoc++;
        }   
      }
       else if(QryEval.retrievalAlgortihm.equals("Indri"))
        {
        	while ((iDoc < iResult.docScores.scores.size())      //Number of documents containing the query term
                    && (result.docScores.getDocid(rDoc) > iResult.docScores.getDocid(iDoc))) { //Add second query term to the result, with default score for the first query term
        		  //System.out.println(iDoc);
        		  float score_temp;
	    		  float score1;
	    		  float score2;
	    		  float score;
		      		long length_c=0;
		      		int tf_d=0;  //Term frequency of the term in the posting i (in document i)
		      		int ctf_qt=result.invertedList.ctf;     //Corpus term frequency for the particular term
		      		//System.out.println(result.invertedList.postings.size());
		      		long length_d=QryEval.READER.getSumTotalTermFreq("body")/QryEval.READER.getDocCount("body"); //Average document length by default
		      		//System.out.println(length_d);
		      		//System.out.println(length_d);
		      		length_c=QryEval.READER.getSumTotalTermFreq("body");
		      		//System.out.println(length_c);
		      		score_temp=(float)Math.log((QryEval.lambda*((tf_d+(QryEval.mu*(ctf_qt/length_c)))/(length_d+QryEval.mu))) + (1-QryEval.lambda)*(ctf_qt/length_c));  //Default score for the first term in rDoc
		      		//System.out.println(qlen);
		      		//score1=(float)Math.pow(score_temp,(1/qlen));  
		      		//score2=(float)Math.pow(iResult.docScores.getDocidScore(iDoc), (1/qlen));
		      		score=score_temp+iResult.docScores.getDocidScore(iDoc);
		      		result.docScores.add(iResult.docScores.getDocid(iDoc), score);  //Add first query term to iResult with default score
		    	    //System.out.println(score);
		      		iDoc++;          
         }
       }
        	
 

        // If the rDoc document appears in both lists
        if ((iDoc < iResult.docScores.scores.size())
            && (result.docScores.getDocid(rDoc) == iResult.docScores.getDocid(iDoc))) {
        	 if(QryEval.retrievalAlgortihm.equals("RankedBoolean")|QryEval.retrievalAlgortihm.equals("UnrankedBoolean"))
        		 result.docScores.setDocidScore (rDoc, Math.min(result.docScores.getDocidScore(rDoc),iResult.docScores.getDocidScore(iDoc)));
        	 else if(QryEval.retrievalAlgortihm.equals("Indri"))
        	 {
        		  //float score1=(float)Math.pow(result.docScores.getDocidScore(rDoc), 1/qlen);
	    		  //float score2=(float)Math.pow(iResult.docScores.getDocidScore(iDoc), 1/qlen);
	    		  float score=result.docScores.getDocidScore(rDoc)+result.docScores.getDocidScore(iDoc);
	    		  result.docScores.setDocidScore(rDoc, score);
        	 }
          rDoc++;
          iDoc++;
        } else {
          if(QryEval.retrievalAlgortihm.equals("RankedBoolean")|(QryEval.retrievalAlgortihm.equals("UnrankedBoolean")))
        	  result.docScores.scores.remove(rDoc);
          else if(QryEval.retrievalAlgortihm.equals("Indri"))
          {
        	  float score_temp;
	    		float score1;
	    		float score2;
	    		float score;
	      		long length_c=0;
	      		int tf_d=0;  //Term frequency of the term in the posting i (in document i)
	      		int ctf_qt=iResult.invertedList.ctf;     //Corpus term frequency for the particular term
	      		long length_d=QryEval.READER.getSumTotalTermFreq("body")/QryEval.READER.getDocCount("body");
	      		//System.out.println(length_d);
	      		//System.out.println(length_d);
	      		length_c=QryEval.READER.getSumTotalTermFreq("body");  //iResult field in this section
	      		//System.out.println(length_c);
	      		score_temp=(float)Math.log((QryEval.lambda*((tf_d+(QryEval.mu*(ctf_qt/length_c)))/(length_d+QryEval.mu))) + (1-QryEval.lambda)*(ctf_qt/length_c));  //Default score for the first term in rDoc
	      		//System.out.println(qlen);
	      		score1=(float)Math.pow(score_temp,1);  
	      		score2=(float)Math.pow(iResult.docScores.getDocidScore(iDoc), 1);
	      		score=score1+score2;
	      		result.docScores.setDocidScore(rDoc, score);  //Add first query term to iResult with default score
	    	    //System.out.println(score);
	      		rDoc++;          
          }
        }
      }
    }

    return result;
  }
}
