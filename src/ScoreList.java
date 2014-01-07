/*
 *  Copyright (c) 2013, Carnegie Mellon University.  All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.List;

public class ScoreList {

  /**
   * A little utilty class to create a <docid, score> object.
   */
  protected class ScoreListEntry {
    private int docid;
    private float score;

    private ScoreListEntry(int docid, float score) {  
      this.docid = docid;       ////Sort this to display rank in unranked
      this.score = score;      //Sort this to display rank in ranked
    }
  }

  List<ScoreListEntry> scores = new ArrayList<ScoreListEntry>();

  /**
   * Append a document score to a score list.
   */
  
  public void swap(int i,int j)
  {
	  ScoreListEntry temp=scores.get(i);
	  scores.set(i, scores.get(j));
	  scores.set(j,temp);
  }
  public void add(int docid, float score) {
    scores.add(new ScoreListEntry(docid, score));
  }

  public int getDocid(int n) {          //Returns the docID for the nth score term
    return this.scores.get(n).docid;
  }

  public float getDocidScore(int n) {  //Returns the score for a particular Document ID.
    return this.scores.get(n).score;
  }
 
  public void setDocidScore(int n, float score)        //Sets the score for a particular document
  {
	  int docid = getDocid(n);
	  scores.set(n, new ScoreListEntry(docid, score));
  }
  
}
