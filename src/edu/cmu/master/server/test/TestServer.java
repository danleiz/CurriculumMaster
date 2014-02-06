package edu.cmu.master.server.test;

import java.net.Socket;

import edu.cmu.master.model.entities.ChooseCourse;
import edu.cmu.master.model.entities.Curriculum;
import edu.cmu.master.model.entities.MajorCurriculum;
import edu.cmu.master.model.entities.Student;
import edu.cmu.master.server.CVMasterMessage;

public class TestServer {
	public static void main(String[] args) {
		Socket sSocket = null;

		Student newStudent = new Student("123@gmail.com", "Tom", "111", "INI",
				"Graduate_Information Networking", "2011_Spring", null);

		CVMasterMessage regStudent = new CVMasterMessage(
				CVMasterMessage.REGISTER_STUDENT_FLAG, true, newStudent);

		try {
			sSocket = new Socket(CVMasterMessage.serverHostName,
					CVMasterMessage.servicePort);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			return;
		}

		regStudent = regStudent.sendReceiveRound(sSocket);
		
		/* Test register some courses */
		ChooseCourse course1 = new ChooseCourse("123@gmail.com", 15410, 2013,
				"Spring");
		ChooseCourse course2 = new ChooseCourse("123@gmail.com", 15440, 2013,
				"Spring");
		ChooseCourse course3 = new ChooseCourse("123@gmail.com", 15412, 2013,
				"Fall");

		CVMasterMessage regCourses = new CVMasterMessage(
				CVMasterMessage.NEW_REGISTERED_COURSE_FLAG, true, new Object[] {
						"123@gmail.com", new ChooseCourse[] {course1, course2, course3}});
		
		try {
			sSocket = new Socket(CVMasterMessage.serverHostName,
					CVMasterMessage.servicePort);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			return;
		}

		regCourses = regCourses.sendReceiveRound(sSocket);

		CVMasterMessage getStudent = new CVMasterMessage(
				CVMasterMessage.GET_STUDENT_INFO_FLAG, true, new Object[] {
						"123@gmail.com", "111" });

		try {
			sSocket = new Socket(CVMasterMessage.serverHostName,
					CVMasterMessage.servicePort);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		getStudent = getStudent.sendReceiveRound(sSocket);
		if (getStudent == null) {
			System.err.println("It is not working!!!");
			return;
		}
		Object[] payload = (Object[]) getStudent.getPayload();
		Student student = (Student) payload[0];
		MajorCurriculum mc = (MajorCurriculum) payload[1];
		Curriculum cv = (Curriculum) payload[2];
		ChooseCourse[] registeredCourse = (ChooseCourse[]) payload[3];
		System.out.println("Student:");
		System.out.println(student.toString());
		System.out.println("MajorCurriculum:");
		System.out.println(mc.toString());
		System.out.println("Curriculum:");
		System.out.println(cv.toString());
		System.out.println("RegisteredCourse:");
		for (ChooseCourse course : registeredCourse) {
			System.out.println(course.toString());
		}
		
		/* Test register some courses */
		course1 = new ChooseCourse("123@gmail.com", 95771, 2013,
				"Spring");
		course2 = new ChooseCourse("123@gmail.com", 15440, 2013,
				"Spring");
		course3 = new ChooseCourse("123@gmail.com", 15412, 2013,
				"Fall");

		regCourses = new CVMasterMessage(
				CVMasterMessage.NEW_REGISTERED_COURSE_FLAG, true, new Object[] {
						"123@gmail.com", new ChooseCourse[]{course1, course2, course3}});
		
		try {
			sSocket = new Socket(CVMasterMessage.serverHostName,
					CVMasterMessage.servicePort);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			return;
		}
		regCourses = regCourses.sendReceiveRound(sSocket);
	}
}
