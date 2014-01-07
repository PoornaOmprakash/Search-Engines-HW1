import java.io.IOException;


public class IndriAnd extends Qryop{
	//int qlen;       //Query length
	/**
	   * It is convenient for the constructor to accept a variable number of arguments. Thus new
	   * qryopAnd (arg1, arg2, arg3, ...).
	   */
	  public IndriAnd(Qryop... q) {
	    for (int i = 0; i < q.length; i++)
	      this.args.add(q[i]);
	  //  qlen=this.args.size();
	  }

	  /**
	   * Evaluate the query operator.
	   */
	  public QryResult evaluate() throws IOException {

	    // Seed the result list by evaluating the first query argument. The result could be docScores or
	    // invList, depending on the query operator. Wrap a SCORE query operator around it to force it
	    // to be a docScores list. There are more efficient ways to do this. This approach is just easy
	    // to see and understand.
		  
		//System.out.println(args.get(0));
	    Qryop impliedQryOp = new QryopScore(args.get(0));
	    QryResult result = impliedQryOp.evaluate();
	    //System.out.println(result.invertedList.ctf);

	    // Each pass of the loop evaluates one query argument.
	    //System.out.println(args.size());
	    for (int i = 1; i < args.size(); i++) {
          //System.out.println(i);
          System.out.println(args.get(i));
	      impliedQryOp = new QryopScore(args.get(i));
	      QryResult iResult = impliedQryOp.evaluate();

	      // Use the results of the i'th argument to incrementally compute the query operator.
	      // Intersection-style query operators iterate over the incremental results, not the results of
	      // the i'th query argument.
	      int rDoc = 0; /* Index of a document in result. */
	      int iDoc = 0; /* Index of a document in iResult. */
         
	     //System.out.println(result.docScores.scores.size());
	     while (rDoc < result.docScores.scores.size()) {
	    	  //System.out.println(rDoc);
		   while (iDoc < iResult.docScores.scores.size())
		     {
			   //System.out.println(iDoc);
	    	  if(result.docScores.getDocid(rDoc)<(iResult.docScores.getDocid(iDoc)))  //First query term occurs in rDoc but not in iDoc
	    	   {
	    		float score_temp;
	    		float score1;
	    		float score2;
	    		float score;
	      		long length_c=0;
	      		int tf_d=0;  //Term frequency of the term in the posting i (in document i)
	      		int ctf_qt=result.invertedList.ctf;     //Corpus term frequency for the particular term
	      		//long length_d=QryEval.dls.getDocLength(result.invertedList.field, result.invertedList.postings.get(i).docid); //Number of word occurrences in document 
	      		/*for(int j=0;j<result.invertedList.postings.size();j++)
	      			{
	      			  length_c += QryEval.dls.getDocLength(result.invertedList.field, result.invertedList.postings.get(j).docid); //Number of word occurrences in collection
	      			}*/
	      		length_c=QryEval.READER.getSumTotalTermFreq(result.invertedList.field);
	      		long length_d=QryEval.dls.getDocLength(result.invertedList.field,result.invertedList.postings.get(iDoc).docid);
	      		score_temp=(float)Math.log((QryEval.lambda*((tf_d+(QryEval.mu*(ctf_qt/length_c)))/(length_d+QryEval.mu))) + ((1-QryEval.lambda)*(ctf_qt/length_d)));
	      		score1=(float)Math.pow(score_temp,1/qlen);  
	      		score2=(float)Math.pow(result.docScores.getDocidScore(rDoc), 1/qlen);
	      		score=score1+score2;
	      		result.docScores.setDocidScore(rDoc, score);  //Add first query term to iResult with default score
	    	    rDoc++;
	      		//iResult.docScores.add(result.invertedList.postings.get(rDoc).docid, score);  //Add first query term to iResult with default score
	    	    //rDoc++;
	    	   }
	    	  
	    	  else if(result.docScores.getDocid(rDoc)>(iResult.docScores.getDocid(iDoc)))
	    	  {
	    		  //This means that the first query term does not appear in rDoc, but appears in iDoc.
	    		  //Now, assign default score to the first query term and add the sum to 'result'
	    		  float score_temp;
	    		  float score1;
	    		  float score2;
	    		  float score;
		      		long length_c=0;
		      		int tf_d=0;  //Term frequency of the term in the posting i (in document i)
		      		int ctf_qt=result.invertedList.ctf;     //Corpus term frequency for the particular term
		      		//System.out.println(ctf_qt);
		      		//long length_d=QryEval.dls.getDocLength(result.invertedList.field, result.invertedList.postings.get(i).docid); //Number of word occurrences in document 
		      		/*for(int j=0;j<result.invertedList.postings.size();j++)
		      			{
		      			  length_c += QryEval.dls.getDocLength(result.invertedList.field, result.invertedList.postings.get(j).docid); //Number of word occurrences in collection
		      			}
		      		long length_d=length_c/QryEval.READER.getDocCount(result.invertedList.field);  //Average document length*/
		      		//System.out.println(result.invertedList.field);
		      		System.out.println(result.invertedList.postings.size());
		      		long length_d=QryEval.dls.getDocLength("body",result.invertedList.postings.get(rDoc).docid);
		      		//System.out.println(length_d);
		      		length_c=QryEval.READER.getSumTotalTermFreq("body");
		      		score_temp=(float)Math.log((QryEval.lambda*((tf_d+(QryEval.mu*(ctf_qt/length_c)))/(length_d+QryEval.mu))) + ((1-QryEval.lambda)*(ctf_qt/length_d)));  //Default score for the second term in rDoc
		      		score1=(float)Math.pow(score_temp,1/qlen);  
		      		score2=(float)Math.pow(iResult.docScores.getDocidScore(iDoc), 1/qlen);
		      		score=score1+score2;
		      		result.docScores.setDocidScore(iDoc, score);  //Add first query term to iResult with default score
		    	    iDoc++;
	    	  }
	    	  
	    	  else if(result.docScores.getDocid(rDoc)==(iResult.docScores.getDocid(iDoc)))
	    	  {
	    		  //Document occurs in both the lists. Perform AND operation
	    		  float score1=(float)Math.pow(result.docScores.getDocidScore(rDoc), 1/qlen);
	    		  float score2=(float)Math.pow(iResult.docScores.getDocidScore(iDoc), 1/qlen);
	    		  float score=score1+score2;
	    		  result.docScores.setDocidScore(rDoc, score);
	    		  rDoc++;
	    		  iDoc++;
	  
	    	  }
		     }
	    	  
	     } 
	    }
	    return result;
	 }
}

