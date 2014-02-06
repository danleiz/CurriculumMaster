package edu.cmu.master.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import edu.cmu.master.model.db_schema.ChooseCourseTable;
import edu.cmu.master.model.db_schema.CoreCoursesTable;
import edu.cmu.master.model.db_schema.CourseTable;
import edu.cmu.master.model.db_schema.CurriculumTable;
import edu.cmu.master.model.db_schema.MajorCurriculumTable;
import edu.cmu.master.model.db_schema.SelectiveCoursesTable;
import edu.cmu.master.model.db_schema.StudentTable;
import edu.cmu.master.model.entities.ChooseCourse;
import edu.cmu.master.model.entities.Course;
import edu.cmu.master.model.entities.Curriculum;
import edu.cmu.master.model.entities.MajorCurriculum;
import edu.cmu.master.model.entities.Student;

/**
 * Server program for CMaster App
 */
public class CVMasterServer {
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_NAME = "CVMASTER";
	public static final String DB_URL = "jdbc:mysql://localhost/" + DB_NAME;
	public static final String USER = "root";
	public static final String PASSWORD = "111";

	/**
	 * Worker thread for doing the real work
	 */
	class Worker implements Runnable {
		private Socket mySocket;

		public Worker(Socket mySocket) {
			this.mySocket = mySocket;
		}

		/*
		 * Get all the courses offered
		 */
		private Course[] getAllCourses() {
			Connection connect = null;
			Statement statement = null;
			ResultSet resultSet = null;
			ArrayList<Course> courses = new ArrayList<Course>();

			try {
				connect = DriverManager.getConnection(DB_URL, USER, PASSWORD);
				// Statements allow to issue SQL queries to the database
				statement = connect.createStatement();
				// Result set get the result of the SQL query
				resultSet = statement.executeQuery("select * from " + DB_NAME
						+ "." + CourseTable.TABLE_NAME);
				while (resultSet.next()) {
					String[] content = new String[resultSet.getMetaData()
							.getColumnCount()];
					for (int i = 0; i < content.length; i++) {
						content[i] = resultSet.getString(resultSet
								.getMetaData().getColumnName(i + 1));
					}
					Course newCourse = new Course(content);
					courses.add(newCourse);
				}

			} catch (Exception ex) {
				System.err.println("Exception when doing the query");
				System.err.println(ex.getMessage());
				ex.printStackTrace();
				return null;
			}
			Course[] result = courses.toArray(new Course[0]);
			return result;
		}

		/*
		 * Send back the error message to client
		 */
		private void sendBackError(CVMasterMessage message, String errorMessage) {
			message.setStatus(false);
			message.setPayload(errorMessage);
			message.send(this.mySocket);
		}

		/**
		 * Get all the information related to the student
		 * 
		 * @param newMessage
		 *            The request message received
		 */
		public void getStudentInfo(CVMasterMessage newMessage) {
			Object[] payload = (Object[]) newMessage.getPayload();
			String studentEmail = (String) payload[0];
			int password = ((String) payload[1]).hashCode();
			System.out.println("Trying to get a student info");

			/* Get all the offered course */
			Course[] allCourses = this.getAllCourses();
			if (allCourses == null) {
				this.sendBackError(newMessage, "Cannot get the courses");
				return;
			}

			System.out.println("Got all the courses");

			/* Get the student info */
			Student student = this.getStudent(studentEmail);
			if (student == null) {
				this.sendBackError(newMessage, "The user does not exist");
				return;
			}
			System.out.println("Get the student");
			if (student.getPassword() != password) {
				this.sendBackError(newMessage, "The password is not correct");
				return;
			}

			/* Get the major info */
			MajorCurriculum mc = this.getMajorCurriculumRelation(student);
			if (mc == null) {
				this.sendBackError(newMessage,
						"The major name seems to be invalid");
				return;
			}
			System.out.println("Get the major-curriculum");

			/* Get the curriculum info */
			Curriculum curriculum = this.getCurriculum(mc);
			if (curriculum == null) {
				this.sendBackError(newMessage, "Cannot get the curriculum");
				return;
			}
			System.out.println("Get the curriculum");

			/* Get all the registered courses */
			ChooseCourse[] registeredCourses = this
					.getRegisteredCourse(studentEmail);
			if (registeredCourses == null) {
				System.out
						.println("The use seems to not registered for any course");
				registeredCourses = new ChooseCourse[0];
			}

			newMessage.setStatus(true);
			newMessage.setPayload(new Object[] { student, mc, curriculum,
					registeredCourses, allCourses });
			newMessage.send(this.mySocket);
		}

		/**
		 * Get all the registered course for the use
		 * 
		 * @param studentEmail
		 *            Email of the user
		 * @return All the ChooseCourses relation of the student
		 */
		private ChooseCourse[] getRegisteredCourse(String studentEmail) {
			Connection connect = null;
			Statement statement = null;
			ResultSet resultSet = null;
			ChooseCourse[] result = null;

			try {
				ArrayList<ChooseCourse> choiceList = new ArrayList<ChooseCourse>();
				connect = DriverManager.getConnection(DB_URL, USER, PASSWORD);
				// Statements allow to issue SQL queries to the database
				statement = connect.createStatement();
				// Result set get the result of the SQL query
				resultSet = statement.executeQuery("select * from " + DB_NAME
						+ "." + ChooseCourseTable.TABLE_NAME + " WHERE "
						+ ChooseCourseTable.COLUMN_NAME_STUDENT_EMAIL + "=\""
						+ studentEmail + "\"");

				while (resultSet.next()) {
					String[] content = new String[resultSet.getMetaData()
							.getColumnCount()];
					for (int i = 0; i < content.length; i++) {
						content[i] = resultSet.getString(resultSet
								.getMetaData().getColumnName(i + 1));
					}

					ChooseCourse newChoice = new ChooseCourse(content);
					choiceList.add(newChoice);
				}

				if (choiceList.size() != 0) {
					result = choiceList.toArray(new ChooseCourse[0]);
				}

			} catch (Exception ex) {
				System.err.println("Exception when doing the query");
				System.err.println(ex.getMessage());
			}
			return result;
		}

		/**
		 * Get the curriculum related to the major
		 * 
		 * @param mc
		 *            major-curriculum relationship
		 * @return Curriculum binded to the major
		 */
		private Curriculum getCurriculum(MajorCurriculum mc) {
			int curriculumId = mc.getCurriculumId();
			Connection connect = null;
			Statement statement = null;
			ResultSet resultSet = null;
			Curriculum result = null;

			try {
				connect = DriverManager.getConnection(DB_URL, USER, PASSWORD);
				// Statements allow to issue SQL queries to the database
				statement = connect.createStatement();
				// Result set get the result of the SQL query
				resultSet = statement.executeQuery("select * from " + DB_NAME
						+ "." + CurriculumTable.TABLE_NAME + " WHERE "
						+ CurriculumTable.COLUMN_NAME_CV_ID + "="
						+ curriculumId);

				int requiredUnit = -1;
				if (resultSet.next()) {
					requiredUnit = resultSet
							.getInt(CurriculumTable.COLUMN_NAME_REQUIRED_UNIT);
				}

				/* Get the core courses */
				Course[] coreCourses = this.getCoreCourses(curriculumId);
				if (coreCourses == null) {
					System.out.println("coreCoures is null");
				}
				/* Get the selective courses */
				Course[] selectiveCourses = this
						.getSelectiveCourses(curriculumId);
				if (selectiveCourses == null) {
					System.out.println("selectiveCoures is null");
				}
				if (requiredUnit != -1 && coreCourses != null
						&& selectiveCourses != null) {
					result = new Curriculum(curriculumId, requiredUnit,
							coreCourses, selectiveCourses);
				}
			} catch (Exception ex) {
				System.err.println("Exception when doing the query");
				System.err.println(ex.getMessage());
			}
			return result;
		}

		/**
		 * Get the core courses for the curriculum
		 * 
		 * @param curriculumId
		 *            Id of the curriculum
		 * @return All the core courses
		 */
		private Course[] getCoreCourses(int curriculumId) {
			Connection connect = null;
			Statement statement = null;
			ResultSet resultSet = null;
			Course[] result = null;

			try {
				ArrayList<Course> courseList = new ArrayList<Course>();
				connect = DriverManager.getConnection(DB_URL, USER, PASSWORD);
				// Statements allow to issue SQL queries to the database
				statement = connect.createStatement();
				// Result set get the result of the SQL query
				resultSet = statement.executeQuery("select * from " + DB_NAME
						+ "." + CourseTable.TABLE_NAME + " WHERE "
						+ CourseTable.COLUMN_NAME_COURSE_ID + " in ( select "
						+ CoreCoursesTable.COLUMN_NAME_COURSE_ID + " from "
						+ DB_NAME + "." + CoreCoursesTable.TABLE_NAME
						+ " where " + CoreCoursesTable.COLUMN_NAME_CV_ID + "="
						+ curriculumId + ")");

				while (resultSet.next()) {
					String[] content = new String[resultSet.getMetaData()
							.getColumnCount()];
					for (int i = 0; i < content.length; i++) {
						content[i] = resultSet.getString(resultSet
								.getMetaData().getColumnName(i + 1));
					}

					Course newCourse = new Course(content);
					courseList.add(newCourse);
				}

				if (courseList.size() != 0) {
					result = courseList.toArray(new Course[0]);
				}

			} catch (Exception ex) {
				System.err.println("Exception when doing the query");
				System.err.println(ex.getMessage());
			}
			return result;
		}

		/**
		 * Get all the selective courses for the curriculum
		 * 
		 * @param curriculumId
		 *            Id of the curriculum
		 * @return All the selective courses for the curriculum
		 */
		private Course[] getSelectiveCourses(int curriculumId) {
			Connection connect = null;
			Statement statement = null;
			ResultSet resultSet = null;
			Course[] result = null;

			try {
				ArrayList<Course> courseList = new ArrayList<Course>();
				connect = DriverManager.getConnection(DB_URL, USER, PASSWORD);
				// Statements allow to issue SQL queries to the database
				statement = connect.createStatement();
				// Result set get the result of the SQL query
				resultSet = statement.executeQuery("select * from " + DB_NAME
						+ "." + CourseTable.TABLE_NAME + " WHERE "
						+ CourseTable.COLUMN_NAME_COURSE_ID + " in ( select "
						+ SelectiveCoursesTable.COLUMN_NAME_COURSE_ID
						+ " from " + DB_NAME + "."
						+ SelectiveCoursesTable.TABLE_NAME + " where "
						+ SelectiveCoursesTable.COLUMN_NAME_CV_ID + "="
						+ curriculumId + ")");

				while (resultSet.next()) {
					String[] content = new String[resultSet.getMetaData()
							.getColumnCount()];
					for (int i = 0; i < content.length; i++) {
						content[i] = resultSet.getString(resultSet
								.getMetaData().getColumnName(i + 1));
					}

					Course newCourse = new Course(content);
					courseList.add(newCourse);
				}

				if (courseList.size() != 0) {
					result = courseList.toArray(new Course[0]);
				}

			} catch (Exception ex) {
				System.err.println("Exception when doing the query");
				System.err.println(ex.getMessage());
			}
			return result;
		}

		/**
		 * Get the student info
		 * 
		 * @param studentEmail
		 *            Email of the student
		 * @return The student instance
		 */
		private Student getStudent(String studentEmail) {
			Connection connect = null;
			Statement statement = null;
			ResultSet resultSet = null;
			Student result = null;

			try {
				connect = DriverManager.getConnection(DB_URL, USER, PASSWORD);
				// Statements allow to issue SQL queries to the database
				statement = connect.createStatement();
				// Result set get the result of the SQL query
				resultSet = statement.executeQuery("select * from " + DB_NAME
						+ "." + StudentTable.TABLE_NAME + " WHERE "
						+ StudentTable.COLUMN_NAME_STUDENT_EMAIL + "=\""
						+ studentEmail + "\"");

				if (resultSet.next()) {
					String[] content = new String[resultSet.getMetaData()
							.getColumnCount() - 1];
					for (int i = 0; i < content.length; i++) {
						content[i] = resultSet.getString(resultSet
								.getMetaData().getColumnName(i + 1));
					}
					byte[] avatar = resultSet.getBytes(resultSet.getMetaData()
							.getColumnName(content.length + 1));
					result = new Student(content, avatar);
				}

			} catch (Exception ex) {
				System.err.println("Exception when doing the query");
				System.err.println(ex.getMessage());
			}
			return result;
		}

		/**
		 * Get the major-curriculum relationship
		 * 
		 * @param student
		 *            Student instance
		 * @return the major-curriculum instance
		 */
		public MajorCurriculum getMajorCurriculumRelation(Student student) {
			Connection connect = null;
			Statement statement = null;
			ResultSet resultSet = null;
			String majorName = student.getMajorName();
			MajorCurriculum result = null;

			try {
				connect = DriverManager.getConnection(DB_URL, USER, PASSWORD);
				// Statements allow to issue SQL queries to the database
				statement = connect.createStatement();
				// Result set get the result of the SQL query
				resultSet = statement.executeQuery("select * from " + DB_NAME
						+ "." + MajorCurriculumTable.TABLE_NAME + " WHERE "
						+ MajorCurriculumTable.COLUMN_NAME_MAJOR_NAME + "=\""
						+ majorName + "\"");
				if (resultSet.next()) {
					int duration = resultSet
							.getInt(MajorCurriculumTable.COLUMN_NAME_MAJOR_DURATION);
					int curriculumId = resultSet
							.getInt(MajorCurriculumTable.COLUMN_NAME_CURRICULUM_ID);
					result = new MajorCurriculum(majorName, duration,
							curriculumId);
				}

			} catch (Exception ex) {
				System.err.println("Exception when doing the query");
				System.err.println(ex.getMessage());
			}

			return result;
		}

		/**
		 * Register a new student account
		 * 
		 * @param newMessage
		 *            the register message
		 */
		public void registerStudent(CVMasterMessage newMessage) {
			Student student = (Student) newMessage.getPayload();
			Connection conn = null;
			boolean status = true;
			try {
				conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
				status = StudentTable.mysqlInsert(conn,
						student.getStudentEmail(), student.getStudentName(),
						student.getPassword(), student.getDepartmentName(),
						student.getMajorName(), student.getStartSemester(),
						student.getImage());

			} catch (Exception ex) {
				System.err.println("Exception when doing the query");
				System.err.println(ex.getMessage());
			}

			newMessage.setStatus(status);
			if (!status) {
				newMessage.setPayload("Failed to register the user");
			}
			newMessage.send(this.mySocket);
		}

		/**
		 * Update the registered courses
		 * 
		 * @param newMessage
		 *            the request message received
		 */
		public void newRegisteredCourses(CVMasterMessage newMessage) {
			Object[] payload = (Object[]) newMessage.getPayload();
			String studentEmail = (String) payload[0];
			ChooseCourse[] allRegisteredCourse = (ChooseCourse[]) payload[1];

			Connection conn = null;
			boolean finalStatus = true;
			boolean status = true;
			try {
				conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
				ChooseCourseTable.mysqlDelete(conn, studentEmail);
				for (ChooseCourse tmp : allRegisteredCourse) {
					status = ChooseCourseTable.mysqlInsert(conn,
							tmp.getStudentEmail(), tmp.getCourseId(),
							tmp.getYear(), tmp.getSemester());
					if (!status) {
						finalStatus = false;
					}
				}
			} catch (Exception ex) {
				System.err.println("Exception when doing the query");
				System.err.println(ex.getMessage());
			}

			newMessage.setStatus(finalStatus);
			if (!finalStatus) {
				newMessage
						.setPayload("Cannot add in all the registered course");
			}
			newMessage.send(this.mySocket);
		}

		/**
		 * Parse the received request message
		 * 
		 * @param newMessage
		 *            new request message
		 */
		public void parseMessage(CVMasterMessage newMessage) {
			int flag = newMessage.getFlag();
			switch (flag) {
			case CVMasterMessage.GET_STUDENT_INFO_FLAG:
				this.getStudentInfo(newMessage);
				break;
			case CVMasterMessage.REGISTER_STUDENT_FLAG:
				this.registerStudent(newMessage);
				break;
			case CVMasterMessage.NEW_REGISTERED_COURSE_FLAG:
				this.newRegisteredCourses(newMessage);
				break;
			default:
				System.err.println("Invalid flag");
			}
		}

		public void run() {
			CVMasterMessage newMessage = CVMasterMessage.receive(this.mySocket);
			if (newMessage == null) {
				System.out.println("The client seems to send nothing");
				return;
			}

			this.parseMessage(newMessage);
			try {
				this.mySocket.close();
			} catch (Exception ex) {
				System.err.println("Cannot close the worker socket");
				System.err.println(ex.getMessage());
			}
		}
	}

	/**
	 * Start the server
	 */
	public void start() {
		ServerSocket sSocket = null;
		try {
			sSocket = new ServerSocket(CVMasterMessage.servicePort);
		} catch (Exception ex) {
			System.out.println("Fatal: Cannot create the server socket");
			System.out.println(ex.getMessage());
			return;
		}

		Socket cSocket = null;
		while (true) {
			try {
				cSocket = sSocket.accept();
			} catch (Exception ex) {
				System.out.println("Cannot get the request from client");
				System.out.println(ex.getMessage());
				continue;
			}
			Worker newWorker = new Worker(cSocket);
			Thread newThread = new Thread(newWorker);
			newThread.start();
		}
	}

	public static void main(String[] args) {
		/* Create the database and all the tables */
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
			stmt = conn.createStatement();

			/* Delete all the table if exists */
			stmt.execute(MajorCurriculumTable.SQL_DELETE_TABLE);
			stmt.execute(SelectiveCoursesTable.SQL_DELETE_TABLE);
			stmt.execute(CoreCoursesTable.SQL_DELETE_TABLE);
			stmt.execute(ChooseCourseTable.SQL_DELETE_TABLE);
			stmt.execute(CourseTable.SQL_DELETE_TABLE);
			stmt.execute(CurriculumTable.SQL_DELETE_TABLE);
			stmt.execute(StudentTable.SQL_DELETE_TABLE);

			/* Create all the table */
			stmt.execute(StudentTable.SQL_CREATE_TABLE);
			stmt.execute(CurriculumTable.SQL_CREATE_TABLE);
			stmt.execute(CourseTable.SQL_CREATE_TABLE);
			stmt.execute(ChooseCourseTable.SQL_CREATE_TABLE);
			stmt.execute(CoreCoursesTable.SQL_CREATE_TABLE);
			stmt.execute(SelectiveCoursesTable.SQL_CREATE_TABLE);
			stmt.execute(MajorCurriculumTable.SQL_CREATE_TABLE);

			/* Insert some testing data */
			/* Course */
			CourseTable.mysqlInsert(conn, 15410, "Operating System", "Spring",
					"CS", "David", 12, "This is hard!!!", 40,
					"www.cs.cmu.edu/~410/1");
			CourseTable.mysqlInsert(conn, 15440, "Distributed System",
					"Spring", "CS", "David Anderson", 12,
					"This is also hard!!!", 40,
					"www.andrew.cmu.edu/course/15-440-f13/");
			CourseTable.mysqlInsert(conn, 15441, "Computer network", "Summer",
					"CS", "David Anderson", 12, "This is also hard!!!", 40,
					"www.andrew.cmu.edu/course/15-440-f13/");
			CourseTable.mysqlInsert(conn, 15411, "Compiler", "Spring", "CS",
					"Who knows", 12, "As hard as OS", 21,
					"www.andrew.cmu.edu/course/15-440-f13/");
			CourseTable.mysqlInsert(conn, 15648, "Data Studio", "Spring", "CS",
					"Garth", 12, "Seems to be good", 40,
					"www.andrew.cmu.edu/course/15-440-f13/");
			CourseTable.mysqlInsert(conn, 15649, "Data Studio 2", "Spring",
					"CS", "Garth", 12, "I have no idea", 40,
					"www.andrew.cmu.edu/course/15-440-f13/");
			CourseTable.mysqlInsert(conn, 15112, "Intro to Programming",
					"Spring", "CS", "David Anderson", 12, "For freshmen!!!",
					140, "www.andrew.cmu.edu/course/15-440-f13/");
			CourseTable.mysqlInsert(conn, 15637, "Web App", "Spring", "CS",
					"Apinger", 12, "Interesting courses", 15,
					"www.andrew.cmu.edu/course/15-440-f13/");
			CourseTable.mysqlInsert(conn, 15412, "OS Practicum", "Fall", "CS",
					"David", 12, "This is extremely hard!!!", 13, "dummy");
			CourseTable.mysqlInsert(conn, 18641, "Java smartphone", "Fall",
					"ECE", "I don't know", 12, "HeHe", 13, "dummy");
			CourseTable.mysqlInsert(conn, 18648, "Real-time embedded system",
					"Spring", "ECE", "I don't know", 12,
					"Linux kernel hacking", 50, "dummy");
			CourseTable.mysqlInsert(conn, 95771, "Data-structure", "Spring",
					"Heinz", "I don't know", 12,
					"Come on! It's Heinz technical course!", 50, "dummy");
			CourseTable.mysqlInsert(conn, 95772,
					"Data-structure For application programmer", "Summer",
					"Heinz", "I don't know", 12,
					"Come on! It's Heinz technical course!", 50, "dummy");
			CourseTable.mysqlInsert(conn, 95773, "DataBase", "Fall", "Heinz",
					"I don't know", 12, "Required database course for INI", 25,
					"dummy");
			CourseTable.mysqlInsert(conn, 95774, "Advanced Database", "Spring",
					"Heinz", "I don't know", 12, "Seems to be advance?", 27,
					"abcdefg hijklmn opq rst uvw xyz");
			CourseTable.mysqlInsert(conn, 95775, "Strategy Management", "Fall",
					"Heinz", "I don't know", 12, "This is a writing course...",
					50, "dummy");

			/* Curriculum */
			CurriculumTable.mysqlInsert(conn, 0, 145);

			String[] degree = new String[] { "Undergraduate", "Graduate",
					"PH.D" };
			String[] major = new String[] { "Computer Science",
					"Information Networking"};

			/* Curriculum */
			for (int i = 0; i < degree.length; i++) {
				for (int j = 0; j < major.length; j++) {
					MajorCurriculumTable.mysqlInsert(conn, degree[i] + "_" + major[j], 2, 0);
				}
			}

			/* Core-course */
			CoreCoursesTable.mysqlInsert(conn, 0, 15410);
			CoreCoursesTable.mysqlInsert(conn, 0, 15412);
			CoreCoursesTable.mysqlInsert(conn, 0, 15411);
			CoreCoursesTable.mysqlInsert(conn, 0, 15637);

			/* Selective-course */
			SelectiveCoursesTable.mysqlInsert(conn, 0, 18641);
			SelectiveCoursesTable.mysqlInsert(conn, 0, 15440);
			SelectiveCoursesTable.mysqlInsert(conn, 0, 95771);
			SelectiveCoursesTable.mysqlInsert(conn, 0, 95775);
			SelectiveCoursesTable.mysqlInsert(conn, 0, 15648);

			System.out.println("Finish inserting the fake datas");
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
			return;
		}

		CVMasterServer server = new CVMasterServer();
		server.start();
	}
}
