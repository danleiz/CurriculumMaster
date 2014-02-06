package edu.cmu.master.model.entities;

import java.io.Serializable;

public class Curriculum implements Serializable {

	private static final long serialVersionUID = 15L;
	private int curriculumId;
	private Integer requiredUnit;
	private Course[] coreCourses;
	private Course[] electiveCourses;

	public Curriculum(int curriculumId, Integer requiredUnit,
			Course[] coreCourses, Course[] selectiveCourses) {
		this.setCurriculumId(curriculumId);
		this.setRequiredUnit(requiredUnit);
		this.setCoreCourses(coreCourses);
		this.setSelectiveCourses(selectiveCourses);
	}

	public Integer getRequiredUnit() {
		return requiredUnit;
	}

	public void setRequiredUnit(Integer requiredUnit) {
		this.requiredUnit = requiredUnit;
	}

	public Course[] getCoreCourses() {
		return coreCourses;
	}

	public void setCoreCourses(Course[] coreCourses) {
		this.coreCourses = coreCourses;
	}

	public Course[] getSelectiveCourses() {
		return electiveCourses;
	}

	public void setSelectiveCourses(Course[] selectiveCourses) {
		this.electiveCourses = selectiveCourses;
	}

	public int getCurriculumId() {
		return curriculumId;
	}

	public void setCurriculumId(int curriculumId) {
		this.curriculumId = curriculumId;
	}

	public String toString() {
		String result = "requiredUnit:" + this.requiredUnit + "\n";
		for (Course course : this.coreCourses) {
			result += course.toString();
		}
		for (Course course : this.electiveCourses) {
			result += course.toString();
		}
		return result;
	}
}
