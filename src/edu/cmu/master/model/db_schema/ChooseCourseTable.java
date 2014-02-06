package edu.cmu.master.model.db_schema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import edu.cmu.master.model.entities.ChooseCourse;
import edu.cmu.master.server.CVMasterServer;

public abstract class ChooseCourseTable {
	public static final String TABLE_NAME = "chooseCourse";
	public static final String COLUMN_NAME_STUDENT_EMAIL = "studentEmail";
	public static final String COLUMN_NAME_COURSE_ID = "courseId";
	public static final String COLUMN_NAME_YEAR = "year";
	public static final String COLUMN_NAME_SEMESTER = "semester";

	public static final int COLUMN_INDEX_STUDENT_EMAIL = 0;
	public static final int COLUMN_INDEX_COURSE_ID = 1;
	public static final int COLUMN_INDEX_YEAR = 2;
	public static final int COLUMN_INDEX_SEMESTER = 3;

	public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ ChooseCourseTable.TABLE_NAME + " ("
			+ ChooseCourseTable.COLUMN_NAME_STUDENT_EMAIL + " VARCHAR(200),"
			+ ChooseCourseTable.COLUMN_NAME_COURSE_ID + " INTEGER,"
			+ ChooseCourseTable.COLUMN_NAME_YEAR + " INTEGER,"
			+ ChooseCourseTable.COLUMN_NAME_SEMESTER + " TEXT, "
			+ "PRIMARY KEY(" + ChooseCourseTable.COLUMN_NAME_STUDENT_EMAIL
			+ ", " + ChooseCourseTable.COLUMN_NAME_COURSE_ID + "))";

	public static final String SQL_DELETE_TABLE = "DELETE FROM " + TABLE_NAME;

	public static boolean insert(SQLiteDatabase db, String studentEmail,
			int courseId, int year, String semester) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_STUDENT_EMAIL, studentEmail);
		values.put(COLUMN_NAME_COURSE_ID, courseId);
		values.put(COLUMN_NAME_YEAR, year);
		values.put(COLUMN_NAME_SEMESTER, semester);

		long newRowId;
		newRowId = db.insert(TABLE_NAME, null, values);
		if (newRowId == -1) {
			return false;
		} else {
			return true;
		}
	}

	public static int remove(SQLiteDatabase db, String studentEmail,
			int courseId) {
		return db.delete(TABLE_NAME, "ROWID = (SELECT ROWID FROM " + TABLE_NAME
				+ " WHERE " + COLUMN_NAME_STUDENT_EMAIL + "=? AND "
				+ COLUMN_NAME_COURSE_ID + "=?)", new String[] { studentEmail,
				String.valueOf(courseId) });
	}

	public static boolean mysqlInsert(Connection connect, String studentEmail,
			int courseId, int year, String semester) {
		PreparedStatement stat = null;
		int result = 0;
		try {
			stat = connect.prepareStatement("insert into "
					+ CVMasterServer.DB_NAME + "." + TABLE_NAME
					+ " values (?,?,?,?)");
			stat.setString(1, studentEmail);
			stat.setInt(2, courseId);
			stat.setInt(3, year);
			stat.setString(4, semester);
			result = stat.executeUpdate();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		if (result == 0) {
			return false;
		}
		return true;
	}

	public static void mysqlDelete(Connection connect, String studentEmail) {
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			String sql = "DELETE FROM " + CVMasterServer.DB_NAME + "."
					+ TABLE_NAME + " WHERE " + COLUMN_NAME_STUDENT_EMAIL
					+ "=\"" + studentEmail + "\"";
			stmt.executeUpdate(sql);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}

	public static boolean insertToSqlite(CVMasterDbHelper dbHelper,
			ChooseCourse choice) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		boolean status = ChooseCourseTable.insert(db, choice.getStudentEmail(),
				choice.getCourseId(), choice.getYear(), choice.getSemester());
		db.close();
		return status;
	}
}
