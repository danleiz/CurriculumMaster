package edu.cmu.master.model.entities;

import java.io.Serializable;

public class MajorCurriculum implements Serializable {

	private static final long serialVersionUID = 13L;

	private String majorName;
	private int duration;
	private int curriculumId;

	public MajorCurriculum(String majorName, int duration, int cvId) {
		this.setMajorName(majorName);
		this.setDuration(duration);
		this.setCurriculumId(cvId);
	}

	public String getMajorName() {
		return majorName;
	}

	public void setMajorName(String majorName) {
		this.majorName = majorName;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getCurriculumId() {
		return curriculumId;
	}

	public void setCurriculumId(int curriculumId) {
		this.curriculumId = curriculumId;
	}

	public String toString() {
		String result = "majorName:" + this.majorName + "\n";
		result += "duration:" + this.duration + "\n";
		result += "curriculumId:" + this.curriculumId + "\n";
		return result;
	}
}
