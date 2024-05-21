package ngo.friendship.satellite.model;

import java.io.Serializable;

//Table(name = "patient_interview_detail")
public class PatientInterviewDetail implements Serializable {
  public static final String MODEL_NAME="patient_interview_detail";

       

  //Id
  //Column(name = "INTERVIEW_DTL_ID")
  private long interviewDtlId;
  //Column(name = "INTERVIEW_ID")
  private long interviewId;
  //Column(name = "Q_ID")
  private long qId;
  //Column(name = "ANSWER")
  private String answer;
  
  private int order;

  public long getInterviewDtlId() {
      return interviewDtlId;
  }

  public void setInterviewDtlId(long interviewDtlId) {
      this.interviewDtlId = interviewDtlId;
  }

  public long getInterviewId() {
      return interviewId;
  }

  public void setInterviewId(long interviewId) {
      this.interviewId = interviewId;
  }

  public long getqId() {
      return qId;
  }

  public void setqId(long qId) {
      this.qId = qId;
  }

  public String getAnswer() {
      return answer;
  }

  public void setAnswer(String answer) {
      this.answer = answer;
  }

  public int getOrder() {
      return order;
  }

  public void setOrder(int order) {
      this.order = order;
  }

  //TRANS_REF
  private long transRef;

  public void setTransRef(long transRef) {
      this.transRef = transRef;
  }

  public long getTransRef() {
      return transRef;
  }

}
