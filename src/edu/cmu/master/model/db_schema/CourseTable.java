package edu.cmu.master.model.db_schema;

import java.sql.Connection;
import java.sql.PreparedStatement;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import edu.cmu.master.model.entities.Course;
import edu.cmu.master.server.CVMasterServer;

public abstract class CourseTable {

	public static final String TABLE_NAME = "course";
	public static final String COLUMN_NAME_COURSE_ID = "courseId";
	public static final String COLUMN_NAME_COURSE_NAME = "courseName";
	public static final String COLUMN_NAME_SEMESTER = "offeredSemester";
	public static final String COLUMN_NAME_DEPARTMENT_NAME = "departmentName";
	public static final String COLUMN_NAME_INSTRUCTOR_NAME = "instructorName";
	public static final String COLUMN_NAME_UNIT = "unit";
	public static final String COLUMN_NAME_DESCRIPTION = "description";
	public static final String COLUMN_NAME_CAPACITY = "capacity";
	public static final String COLUMN_NAME_LINK = "url";
	
	public static final int COLUMN_INDEX_COURSE_ID = 0;
	public static final int COLUMN_INDEX_COURSE_NAME = 1;
	public static final int COLUMN_INDEX_SEMESTER = 2;
	public static final int COLUMN_INDEX_DEPARTMENT_NAME = 3;
	public static final int COLUMN_INDEX_INSTRUCTOR = 4;
	public static final int COLUMN_INDEX_UNIT = 5;
	public static final int COLUMN_INDEX_DESCRIPTION = 6;
	public static final int COLUMN_INDEX_CAPACITY = 7;
	public static final int COLUMN_INDEX_LINK = 8;

	public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ CourseTable.TABLE_NAME + " (" + CourseTable.COLUMN_NAME_COURSE_ID
			+ " INTEGER PRIMARY KEY," + CourseTable.COLUMN_NAME_COURSE_NAME
			+ " TEXT," + CourseTable.COLUMN_NAME_SEMESTER + " TEXT,"
			+ CourseTable.COLUMN_NAME_DEPARTMENT_NAME + " TEXT,"
			+ CourseTable.COLUMN_NAME_INSTRUCTOR_NAME + " TEXT,"
			+ CourseTable.COLUMN_NAME_UNIT + " INTEGER,"
			+ CourseTable.COLUMN_NAME_DESCRIPTION + " TEXT,"
			+ CourseTable.COLUMN_NAME_CAPACITY + " INTEGER,"
			+ CourseTable.COLUMN_NAME_LINK + " TEXT)";

	public static final String SQL_INSERT_ENTRY = "INSERT";

	public static final String SQL_DELETE_TABLE = "DELETE FROM " + TABLE_NAME;

	public static boolean insert(SQLiteDatabase db, int courseId,
			String courseName, String semester, String departmentName,
			String instructorName, int unit, String description, int capacity,
			String link) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_COURSE_ID, courseId);
		values.put(COLUMN_NAME_COURSE_NAME, courseName);
		values.put(COLUMN_NAME_SEMESTER, semester);
		values.put(COLUMN_NAME_DEPARTMENT_NAME, departmentName);
		values.put(COLUMN_NAME_INSTRUCTOR_NAME, instructorName);
		values.put(COLUMN_NAME_UNIT, unit);
		values.put(COLUMN_NAME_DESCRIPTION, description);
		values.put(COLUMN_NAME_CAPACITY, capacity);
		values.put(COLUMN_NAME_LINK, link);

		long newRowId;
		newRowId = db.insert(TABLE_NAME, null, values);
		if (newRowId == -1) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean mysqlInsert(Connection connect, int courseId,
			String courseName, String semester, String departmentName,
			String instructorName, int unit, String description, int capacity,
			String link) {
		PreparedStatement stat = null;
		int result = 0;
		try {
			stat = connect.prepareStatement("insert into "
					+ CVMasterServer.DB_NAME + "." + TABLE_NAME
					+ " values(?,?,?,?,?,?,?,?,?)");
			stat.setInt(1, courseId);
			stat.setString(2, courseName);
			stat.setString(3, semester);
			stat.setString(4, departmentName);
			stat.setString(5, instructorName);
			stat.setInt(6, unit);
			stat.setString(7, description);
			stat.setInt(8, capacity);
			stat.setString(9, link);
			result = stat.executeUpdate();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		if (result == 0) {
			return false;
		}
		return true;
	}

	public static boolean insertToSqlite(CVMasterDbHelper dbHelper,
			Course course) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		boolean status = CourseTable.insert(db, course.getCourseId(),
				course.getCourseName(), course.getSemester(),
				course.getDepartmentName(), course.getInstructorName(),
				course.getUnit(), course.getDescription(),
				course.getCapacity(), course.getUrl());
		db.close();
		return status;
	}
}
