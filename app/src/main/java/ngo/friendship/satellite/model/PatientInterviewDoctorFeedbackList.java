package ngo.friendship.satellite.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PatientInterviewDoctorFeedbackList implements Serializable {

	private String interviewDate;
	private ArrayList<PatientInterviewDoctorFeedback>  doctorFeedBack;
	public void setInterviewDate(String interviewDate) {
		this.interviewDate = interviewDate;
	}
	public String getInterviewDate() {
		return interviewDate;
	}
	public void setDoctorFeedBack(
			ArrayList<PatientInterviewDoctorFeedback> doctorFeedBack) {
		this.doctorFeedBack = doctorFeedBack;
	}
	public ArrayList<PatientInterviewDoctorFeedback> getDoctorFeedBack() {
		return doctorFeedBack;
	}
}
