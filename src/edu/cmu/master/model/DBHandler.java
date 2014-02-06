package edu.cmu.master.model;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.cmu.master.model.db_schema.CVMasterDbHelper;
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
import edu.cmu.master.model.entities.Student;

public class DBHandler {

	private final String[] semesterCode;

	private static DBHandler _instance;

	public static DBHandler getInstance() {
		if (_instance == null) {
			_instance = new DBHandler();
		}
		return _instance;
	}

	private DBHandler() {
		semesterCode = new String[] { "Spring", "Summer", "Fall" };
	}

	/* Put in some dummy data */
	public void initDummyData(CVMasterDbHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		/* Course */
		CourseTable.insert(db, 15410, "Operating System", "Spring", "CS", "David", 12, "This is hard!!!", 40, null);
		CourseTable.insert(db, 15440, "Distributed System", "Spring", "CS", "David Anderson", 12, "This is also hard!!!", 40, null);
		CourseTable.insert(db, 15412, "OS Practicum", "Fall", "CS", "David", 12, "This is extremely hard!!!", 13, null);
		CourseTable.insert(db, 18641, "Java smartphone", "Fall", "ECE", "I don't know", 12, "HeHe", 13, null);
		CourseTable.insert(db, 18648, "Real-time embedded system", "Spring", "ECE", "I don't know", 12, "Linux kernel hacking", 50, null);
		CourseTable.insert(db, 95771, "Data-structure", "Spring", "Heinz", "I don't know", 12, "Come on! It's Heinz technical course!", 50, null);

		/* Major-Curriculum */
		MajorCurriculumTable.insert(db, "Graduate_Information Networking", 2, 0);

		/* Core-course */
		CoreCoursesTable.insert(db, 0, 15410);
		CoreCoursesTable.insert(db, 0, 15412);

		/* Selective-course */
		SelectiveCoursesTable.insert(db, 0, 18641);
		SelectiveCoursesTable.insert(db, 0, 15440);
		SelectiveCoursesTable.insert(db, 0, 95772);

		/* Curriculum */
		CurriculumTable.insert(db, 0, 145);
		Log.i("BD", "insertion finished!");

		db.close();
	}

	public String[] getAllSemesters(CVMasterDbHelper dbHelper) {
		return semesterCode;
	}

	public String[] getAllDepartment(CVMasterDbHelper dbHelper) {
		SQLiteDatabase readDB = dbHelper.getReadableDatabase();

		final String query = "SELECT DISTINCT "
				+ CourseTable.COLUMN_NAME_DEPARTMENT_NAME + " FROM "
				+ CourseTable.TABLE_NAME;

		Cursor cur = readDB.rawQuery(query, null);
		ArrayList<String> depList = new ArrayList<String>();
		if (cur.moveToFirst()) {
			do {
				String nextDepartment = cur.getString(cur.getColumnIndexOrThrow(CourseTable.COLUMN_NAME_DEPARTMENT_NAME));
				depList.add(nextDepartment);
			} while (cur.moveToNext());
		}

		// close database
		readDB.close();

		String[] result = depList.toArray(new String[0]);
		return result;
	}

	/**
	 * Sem is one of {Spring, Summer, Fall}
	 */
	public Course[] getAllCourse(CVMasterDbHelper dbHelper, String departmentName, String sem) {
		SQLiteDatabase readDB = dbHelper.getReadableDatabase();

		int index = sem.indexOf('_');
		if (index > 0) {
			sem = sem.substring(index + 1);
		}

		final String query = "SELECT * FROM " + CourseTable.TABLE_NAME
				+ " WHERE " + CourseTable.COLUMN_NAME_DEPARTMENT_NAME + "=\""
				+ departmentName + "\" AND " + CourseTable.COLUMN_NAME_SEMESTER
				+ "=\"" + sem + "\"";

		Cursor cur = readDB.rawQuery(query, null);
		ArrayList<Course> courseList = new ArrayList<Course>();
		if (cur.moveToFirst()) {
			do {
				String[] content = new String[cur.getColumnCount()];
				for (int i = 0; i < content.length; i++) {
					content[i] = cur.getString(i);
				}
				Course newCourse = new Course(content);
				courseList.add(newCourse);
			} while (cur.moveToNext());
		}

		// close database
		readDB.close();

		Course[] result = courseList.toArray(new Course[0]);
		return result;
	}

	public String[] getAllSemesterForStudent(CVMasterDbHelper dbHelper, String studentEmail) {
		SQLiteDatabase readDB = dbHelper.getReadableDatabase();

		final String query = "SELECT " + StudentTable.TABLE_NAME + "."
				+ StudentTable.COLUMN_NAME_START_SEMESTER + ", "
				+ MajorCurriculumTable.COLUMN_NAME_MAJOR_DURATION + " FROM "
				+ StudentTable.TABLE_NAME + ","
				+ MajorCurriculumTable.TABLE_NAME + " WHERE "
				+ StudentTable.TABLE_NAME + "."
				+ StudentTable.COLUMN_NAME_STUDENT_EMAIL + "=\"" + studentEmail
				+ "\" AND " + StudentTable.TABLE_NAME + "."
				+ StudentTable.COLUMN_NAME_MAJOR_NAME + "="
				+ MajorCurriculumTable.TABLE_NAME + "."
				+ MajorCurriculumTable.COLUMN_NAME_MAJOR_NAME;
		Cursor cur = readDB.rawQuery(query, null);
		String semesterCode = null;
		int duration = -1;
		if (cur.moveToFirst()) {
			semesterCode = cur.getString(0);
			duration = cur.getInt(1);
		}
		readDB.close();

		if (semesterCode == null) {
			return null;
		} else {
			String[] tmp = semesterCode.split("_");
			int year = Integer.valueOf(tmp[0]);
			return this.showAllSemester(year, tmp[1], duration);
		}
	}

	private String[] showAllSemester(int year, String semester, int duration) {
		ArrayList<String> tmp = new ArrayList<String>();
		int index = 0;
		for (; index < this.semesterCode.length; index++) {
			if (semester.equals(this.semesterCode[index])) {
				break;
			}
		}

		for (int i = 0; i < duration * 3; i++) {
			tmp.add(year + "_" + this.semesterCode[index]);
			index++;
			if (index == this.semesterCode.length) {
				year++;
				index = 0;
			}
		}

		String[] result = tmp.toArray(new String[0]);
		return result;
	}

	public ChooseCourse[] getChoosedCourses(CVMasterDbHelper dbHelper, String studentEmail) {
		SQLiteDatabase readDB = dbHelper.getReadableDatabase();
		final String query = "SELECT * FROM " + ChooseCourseTable.TABLE_NAME
				+ " WHERE " + ChooseCourseTable.COLUMN_NAME_STUDENT_EMAIL
				+ "=\"" + studentEmail + "\"";

		Cursor cur = readDB.rawQuery(query, null);
		ArrayList<ChooseCourse> choiceList = new ArrayList<ChooseCourse>();
		if (cur.moveToFirst()) {
			do {
				String[] content = new String[cur.getColumnCount()];
				for (int i = 0; i < content.length; i++) {
					content[i] = cur.getString(i);
				}
				ChooseCourse newChoice = new ChooseCourse(content);
				choiceList.add(newChoice);
			} while (cur.moveToNext());
		}

		readDB.close();

		ChooseCourse[] result = choiceList.toArray(new ChooseCourse[0]);
		return result;
	}

	public Course[] getRegisteredCourses(CVMasterDbHelper dbHelper, String studentEmail) {
		SQLiteDatabase readDB = dbHelper.getReadableDatabase();
		final String query = "SELECT * FROM " + CourseTable.TABLE_NAME
				+ " WHERE " + CourseTable.COLUMN_NAME_COURSE_ID
				+ " in ( SELECT " + ChooseCourseTable.COLUMN_NAME_COURSE_ID
				+ " FROM " + ChooseCourseTable.TABLE_NAME + " WHERE "
				+ ChooseCourseTable.COLUMN_NAME_STUDENT_EMAIL + "=\""
				+ studentEmail + "\")";

		Cursor cur = readDB.rawQuery(query, null);
		ArrayList<Course> courseList = new ArrayList<Course>();
		if (cur.moveToFirst()) {
			do {
				String[] content = new String[cur.getColumnCount()];
				for (int i = 0; i < content.length; i++) {
					content[i] = cur.getString(i);
				}
				Course newCourse = new Course(content);
				courseList.add(newCourse);
			} while (cur.moveToNext());
		}

		readDB.close();

		Course[] result = courseList.toArray(new Course[0]);
		return result;
	}

	public boolean isRegistered(CVMasterDbHelper dbHelper, String studentEmail, int courseId) {
		Course[] courses = this.getRegisteredCourses(dbHelper, studentEmail);
		for (int i = 0; i < courses.length; i++) {
			if (courses[i].getCourseId() == courseId) {
				return true;
			}
		}
		return false;
	}

	public Course[] getRegisteredCourseInSemester(CVMasterDbHelper dbHelper, String studentEmail, String semester) {
		SQLiteDatabase readDB = dbHelper.getReadableDatabase();

		String[] tmp = semester.split("_");
		int year = Integer.valueOf(tmp[0]);
		String sem = tmp[1];

		final String query = "SELECT * FROM " + CourseTable.TABLE_NAME
				+ " WHERE " + CourseTable.COLUMN_NAME_COURSE_ID
				+ " in ( SELECT " + ChooseCourseTable.COLUMN_NAME_COURSE_ID
				+ " FROM " + ChooseCourseTable.TABLE_NAME + " WHERE "
				+ ChooseCourseTable.COLUMN_NAME_STUDENT_EMAIL + "=\""
				+ studentEmail + "\" AND "
				+ ChooseCourseTable.COLUMN_NAME_SEMESTER + "=\"" + sem
				+ "\" AND " + ChooseCourseTable.COLUMN_NAME_YEAR + "=" + year
				+ ")";

		Cursor cur = readDB.rawQuery(query, null);
		ArrayList<Course> courseList = new ArrayList<Course>();
		if (cur.moveToFirst()) {
			do {
				String[] content = new String[cur.getColumnCount()];
				for (int i = 0; i < content.length; i++) {
					content[i] = cur.getString(i);
				}
				Course newCourse = new Course(content);
				courseList.add(newCourse);
			} while (cur.moveToNext());
		}

		readDB.close();

		Course[] result = courseList.toArray(new Course[0]);
		return result;
	}

	public boolean registerCourse(CVMasterDbHelper dbHelper, String studentEmail, int courseId, String semester) {
		SQLiteDatabase writeDB = dbHelper.getWritableDatabase();

		String[] tmp = semester.split("_");
		int year = Integer.valueOf(tmp[0]);
		String sem = tmp[1];
		boolean status = ChooseCourseTable.insert(writeDB, studentEmail, courseId, year, sem);
		writeDB.close();
		return status;
	}

	public void removeCourses(CVMasterDbHelper dbHelper, String studentEmail, int[] courseIds) {
		SQLiteDatabase writeDB = dbHelper.getWritableDatabase();
		for (int courseId : courseIds) {
			ChooseCourseTable.remove(writeDB, studentEmail, courseId);
		}
		writeDB.close();
	}

	public Course getCourse(CVMasterDbHelper dbHelper, int courseId) {
		SQLiteDatabase readDB = dbHelper.getReadableDatabase();

		final String query = "SELECT * FROM " + CourseTable.TABLE_NAME
				+ " WHERE " + CourseTable.COLUMN_NAME_COURSE_ID + "="
				+ courseId;
		Cursor cur = readDB.rawQuery(query, null);
		Course result = null;
		if (cur.moveToFirst()) {
			String[] content = new String[cur.getColumnCount()];
			for (int i = 0; i < content.length; i++) {
				content[i] = cur.getString(i);
			}
			result = new Course(content);
		}

		readDB.close();

		return result;
	}

	public Curriculum getCurriculum(CVMasterDbHelper dbHelper, String studentEmail) {
		SQLiteDatabase readDB = dbHelper.getReadableDatabase();

		final String getCurriculumQuery = "SELECT * FROM "
				+ CurriculumTable.TABLE_NAME + " WHERE "
				+ CurriculumTable.COLUMN_NAME_CV_ID + " in ( SELECT "
				+ MajorCurriculumTable.COLUMN_NAME_CURRICULUM_ID + " FROM "
				+ StudentTable.TABLE_NAME + ", "
				+ MajorCurriculumTable.TABLE_NAME + " WHERE "
				+ StudentTable.TABLE_NAME + "."
				+ StudentTable.COLUMN_NAME_STUDENT_EMAIL + "=\"" + studentEmail
				+ "\" AND " + StudentTable.TABLE_NAME + "."
				+ StudentTable.COLUMN_NAME_MAJOR_NAME + "="
				+ MajorCurriculumTable.TABLE_NAME + "."
				+ MajorCurriculumTable.COLUMN_NAME_MAJOR_NAME + ")";

		Cursor cur = readDB.rawQuery(getCurriculumQuery, null);
		int cvId = -1;
		int requiredUnit = -1;
		if (cur.moveToFirst()) {
			cvId = cur.getInt(cur.getColumnIndexOrThrow(CurriculumTable.COLUMN_NAME_CV_ID));
			requiredUnit = cur.getInt(cur.getColumnIndexOrThrow(CurriculumTable.COLUMN_NAME_REQUIRED_UNIT));
		}
		if (cvId == -1) {
			readDB.close();
			return null;
		}

		final String getCoreCoursesQuery = "SELECT * FROM "
				+ CourseTable.TABLE_NAME + " WHERE "
				+ CourseTable.COLUMN_NAME_COURSE_ID + " in ( SELECT "
				+ CoreCoursesTable.COLUMN_NAME_COURSE_ID + " FROM "
				+ CoreCoursesTable.TABLE_NAME + " WHERE "
				+ CoreCoursesTable.COLUMN_NAME_CV_ID + "=" + cvId + ")";

		cur = readDB.rawQuery(getCoreCoursesQuery, null);
		ArrayList<Course> courseList = new ArrayList<Course>();
		if (cur.moveToFirst()) {
			do {
				String[] content = new String[cur.getColumnCount()];
				for (int i = 0; i < content.length; i++) {
					content[i] = cur.getString(i);
				}
				Course newCourse = new Course(content);
				courseList.add(newCourse);
			} while (cur.moveToNext());
		}
		Course[] coreCourses = courseList.toArray(new Course[0]);

		final String getSelectiveCoursesQuery = "SELECT * FROM "
				+ CourseTable.TABLE_NAME + " WHERE "
				+ CourseTable.COLUMN_NAME_COURSE_ID + " in ( SELECT "
				+ SelectiveCoursesTable.COLUMN_NAME_COURSE_ID + " FROM "
				+ SelectiveCoursesTable.TABLE_NAME + " WHERE "
				+ SelectiveCoursesTable.COLUMN_NAME_CV_ID + "=" + cvId + ")";

		cur = readDB.rawQuery(getSelectiveCoursesQuery, null);
		courseList = new ArrayList<Course>();
		if (cur.moveToFirst()) {
			do {
				String[] content = new String[cur.getColumnCount()];
				for (int i = 0; i < content.length; i++) {
					content[i] = cur.getString(i);
				}
				Course newCourse = new Course(content);
				courseList.add(newCourse);
			} while (cur.moveToNext());
		}
		Course[] selectiveCourses = courseList.toArray(new Course[0]);

		readDB.close();

		return new Curriculum(cvId, requiredUnit, coreCourses, selectiveCourses);
	}

	public boolean register(CVMasterDbHelper dbHelper, String studentEmail, String studentName, String password, String departmentName, String majorName, int year, String startSemester, byte[] avatar) {
		SQLiteDatabase writeDB = dbHelper.getWritableDatabase();

		boolean result = StudentTable.insert(writeDB, studentEmail, studentName, password, departmentName, majorName, year, startSemester, avatar);

		writeDB.close();
		return result;
	}

	public Student login(CVMasterDbHelper dbHelper, String studentEmail, String password) {
		SQLiteDatabase readDB = dbHelper.getReadableDatabase();

		String getStudentQuery = "SELECT * FROM " + StudentTable.TABLE_NAME
				+ " WHERE " + StudentTable.COLUMN_NAME_STUDENT_EMAIL + "=\""
				+ studentEmail + "\"";

		Cursor cur = readDB.rawQuery(getStudentQuery, null);
		Student thisStudent = null;
		int key = -1;
		if (cur.moveToFirst()) {
			String[] content = new String[cur.getColumnCount() - 1];
			for (int i = 0; i < content.length - 1; i++) {
				content[i] = cur.getString(i);
			}
			byte[] image = cur.getBlob(cur.getColumnCount() - 1);
			thisStudent = new Student(content, image);
			key = cur.getInt(cur.getColumnIndexOrThrow(StudentTable.COLUMN_NAME_PASSWORD));
		}

		readDB.close();

		if (key == -1 || key != password.hashCode()) {
			return null;
		}

		return thisStudent;
	}

	public boolean checkFirstTimeUse(CVMasterDbHelper dbHelper) {
		SQLiteDatabase readDB = dbHelper.getReadableDatabase();

		String checkFirstTimeUseQuery = "SELECT COUNT(*) FROM "
				+ StudentTable.TABLE_NAME;
		Cursor cur = readDB.rawQuery(checkFirstTimeUseQuery, null);
		int counter = 0;
		if (cur.moveToFirst()) {
			counter = cur.getInt(0);
		}

		readDB.close();
		if (counter == 0) {
			return false;
		} else {
			return true;
		}
	}

	public void deleteDb(CVMasterDbHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(StudentTable.SQL_DELETE_TABLE);
		db.execSQL(CurriculumTable.SQL_DELETE_TABLE);
		db.execSQL(CourseTable.SQL_DELETE_TABLE);
		db.execSQL(ChooseCourseTable.SQL_DELETE_TABLE);
		db.execSQL(CoreCoursesTable.SQL_DELETE_TABLE);
		db.execSQL(SelectiveCoursesTable.SQL_DELETE_TABLE);
		db.execSQL(MajorCurriculumTable.SQL_DELETE_TABLE);
		// db.deleteDatabase(dbHelper.getDatabaseName())
		db.close();
	}

}
