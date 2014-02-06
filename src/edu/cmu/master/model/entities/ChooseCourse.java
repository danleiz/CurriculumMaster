package edu.cmu.master.model.entities;

import java.io.Serializable;

import edu.cmu.master.model.db_schema.ChooseCourseTable;

public class ChooseCourse implements Serializable {

	private static final long serialVersionUID = 11L;
	private String studentEmail;
	private int courseId;
	private int year;
	private String semester;
	
	public ChooseCourse(String[] content) {
		this.studentEmail = content[ChooseCourseTable.COLUMN_INDEX_STUDENT_EMAIL];
		this.courseId = Integer.parseInt(content[ChooseCourseTable.COLUMN_INDEX_COURSE_ID]);
		this.year = Integer.parseInt(content[ChooseCourseTable.COLUMN_INDEX_YEAR]);
		this.semester = content[ChooseCourseTable.COLUMN_INDEX_SEMESTER];
	}
	
	public ChooseCourse(String studentEmail, int courseId, int year, String semester) {
		this.setStudentEmail(studentEmail);
		this.setCourseId(courseId);
		this.setYear(year);
		this.setSemester(semester);
	}

	public String getStudentEmail() {
		return studentEmail;
	}

	public void setStudentEmail(String studentEmail) {
		this.studentEmail = studentEmail;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}
	
	public String toString() {
		String result = "studentEmail:" + this.studentEmail + "\n";
		result += "courseId:" + this.courseId + "\n";
		result += "year:" + this.year + "\n";
		result += "semester:" + this.semester + "\n";
		return result;
	}
}
