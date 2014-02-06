package edu.cmu.master.model.entities;

import java.io.Serializable;

import edu.cmu.master.model.db_schema.StudentTable;

public class Student implements Serializable {

	private static final long serialVersionUID = 14L;
	private String studentEmail;
	private String studentName;
	private int password; // hash code of password string
	private String departmentName;
	private String majorName;
	private String startSemester;
	private byte[] image;

	public Student(String[] content, byte[] avatar) {
		this.setStudentEmail(content[StudentTable.COLUMN_INDEX_STUDENT_EMAIIL]);
		this.setStudentName(content[StudentTable.COLUMN_INDEX_STUDENT_NAME]);
		this.setPassword(Integer
				.valueOf(content[StudentTable.COLUMN_INDEX_PASSWORD]));
		this.setDepartmentName(content[StudentTable.COLUMN_INDEX_DEPARTMENT_NAME]);
		this.setMajorName(content[StudentTable.COLUMN_INDEX_MAJOR_NAME]);
		this.setStartSemester(content[StudentTable.COLUMN_INDEX_START_SEMESTER]);
		this.setImage(avatar);
	}

	public Student(String studentEmail, String studentName, String password,
			String departmentName, String majorName, String startSemester, byte[] image) {
		this.studentEmail = studentEmail;
		this.studentName = studentName;
		this.password = password.hashCode();
		this.departmentName = departmentName;
		this.majorName = majorName;
		this.startSemester = startSemester;
		this.image = image;
	}

	public String getStudentEmail() {
		return studentEmail;
	}

	public void setStudentEmail(String studentEmail) {
		this.studentEmail = studentEmail;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public int getPassword() {
		return password;
	}

	public void setPassword(int password) {
		this.password = password;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getMajorName() {
		return majorName;
	}

	public void setMajorName(String majorName) {
		this.majorName = majorName;
	}

	public String getStartSemester() {
		return startSemester;
	}

	public void setStartSemester(String startSemester) {
		this.startSemester = startSemester;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String toString() {
		String result = "studentEmail:" + this.studentEmail + "\n";
		result += "studentName:" + this.studentName + "\n";
		result += "password:" + this.password + "\n";
		result += "departmentName:" + this.departmentName + "\n";
		result += "majorName:" + this.majorName + "\n";
		result += "startSemester:" + this.startSemester + "\n";
		return result;
	}
}
