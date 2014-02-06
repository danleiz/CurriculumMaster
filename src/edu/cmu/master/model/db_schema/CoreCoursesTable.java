package edu.cmu.master.model.db_schema;

import java.sql.Connection;
import java.sql.PreparedStatement;

import edu.cmu.master.server.CVMasterServer;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public abstract class CoreCoursesTable {
	public static final String TABLE_NAME = "coreCourses";
	public static final String COLUMN_NAME_CV_ID = "curriculumId";
	public static final String COLUMN_NAME_COURSE_ID = "courseId";

	public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ CoreCoursesTable.TABLE_NAME + " ("
			+ CoreCoursesTable.COLUMN_NAME_CV_ID + " INTEGER,"
			+ CoreCoursesTable.COLUMN_NAME_COURSE_ID + " INTEGER, "
			+ "PRIMARY KEY (" + CoreCoursesTable.COLUMN_NAME_CV_ID + ", "
			+ CoreCoursesTable.COLUMN_NAME_COURSE_ID + "), "
			+ "FOREIGN KEY(" + CoreCoursesTable.COLUMN_NAME_CV_ID
			+ ") REFERENCES " + CurriculumTable.TABLE_NAME + "("
			+ CurriculumTable.COLUMN_NAME_CV_ID + "), " + "FOREIGN KEY("
			+ CoreCoursesTable.COLUMN_NAME_COURSE_ID + ") REFERENCES "
			+ CourseTable.TABLE_NAME + "("
			+ CourseTable.COLUMN_NAME_COURSE_ID + "))";

	public static final String SQL_DELETE_TABLE = "DELETE FROM "
			+ TABLE_NAME;
	public static boolean insert(SQLiteDatabase db, int curriculumId, int courseId) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_CV_ID, curriculumId);
		values.put(COLUMN_NAME_COURSE_ID, courseId);

		long newRowId;
		newRowId = db.insert(TABLE_NAME, null, values);
		if (newRowId == -1) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean mysqlInsert(Connection connect, int curriculumId, int courseId) {
		PreparedStatement stat = null;
		int result = 0;
		try {
			stat = connect.prepareStatement("insert into "
					+ CVMasterServer.DB_NAME + "." + TABLE_NAME
					+ " values (?,?)");
			stat.setInt(1, curriculumId);
			stat.setInt(2, courseId);
			result = stat.executeUpdate();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		if (result == 0) {
			return false;
		}
		return true;
	}
}
