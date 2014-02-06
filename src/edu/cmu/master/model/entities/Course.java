package edu.cmu.master.model.entities;

import java.io.Serializable;

import edu.cmu.master.model.db_schema.CourseTable;

public class Course implements Serializable, Comparable<Course> {
	private static final long serialVersionUID = 1L;

	private Integer courseId;
	private String courseName;
	private String departmentName;
	private String instructorName;
	private Integer unit;
	private String description;
	private Integer capacity;
	private String url;
	private String semester;

	public Course(String[] content) {
		this.courseId = Integer
				.valueOf(content[CourseTable.COLUMN_INDEX_COURSE_ID]);
		this.courseName = content[CourseTable.COLUMN_INDEX_COURSE_NAME];
		this.departmentName = content[CourseTable.COLUMN_INDEX_DEPARTMENT_NAME];
		this.instructorName = content[CourseTable.COLUMN_INDEX_INSTRUCTOR];
		this.unit = Integer.valueOf(content[CourseTable.COLUMN_INDEX_UNIT]);
		this.description = content[CourseTable.COLUMN_INDEX_DESCRIPTION];
		this.capacity = Integer
				.valueOf(content[CourseTable.COLUMN_INDEX_CAPACITY]);
		this.url = content[CourseTable.COLUMN_INDEX_LINK];
		this.setSemester(content[CourseTable.COLUMN_INDEX_SEMESTER]);
	}

	public Integer getCourseId() {
		return courseId;
	}

	public String getCourseName() {
		return courseName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public String getInstructorName() {
		return instructorName;
	}

	public Integer getUnit() {
		return unit;
	}

	public String getDescription() {
		return description;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public int compareTo(Course another) {
		return this.courseId - another.courseId;
	}

	public String toString() {
		String result = "courseId:" + String.valueOf(this.courseId) + "\n";
		result += "courseName:" + this.courseName + "\n";
		result += "departmentName:" + this.departmentName + "\n";
		result += "instructorName:" + this.instructorName + "\n";
		result += "unit" + this.unit + "\n";
		result += "description:" + this.description + "\n";
		result += "url:" + this.url + "\n";
		return result;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}
}
