package edu.cmu.master.model.db_schema;

import java.sql.Connection;
import java.sql.PreparedStatement;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import edu.cmu.master.model.entities.Student;
import edu.cmu.master.server.CVMasterServer;

public abstract class StudentTable {

	public static final String TABLE_NAME = "student";

	public static final String COLUMN_NAME_STUDENT_EMAIL = "studentEmail";

	public static final String COLUMN_NAME_STUDENT_NAME = "studentName";

	public static final String COLUMN_NAME_PASSWORD = "password";

	public static final String COLUMN_NAME_DEPARTMENT_NAME = "departmentName";

	public static final String COLUMN_NAME_MAJOR_NAME = "majorName";

	public static final String COLUMN_NAME_START_SEMESTER = "startSemester";

	public static final String COLUMN_NAME_AVATAR = "studentAvatar";

	public static final int COLUMN_INDEX_STUDENT_EMAIIL = 0;

	public static final int COLUMN_INDEX_STUDENT_NAME = 1;

	public static final int COLUMN_INDEX_PASSWORD = 2;

	public static final int COLUMN_INDEX_DEPARTMENT_NAME = 3;

	public static final int COLUMN_INDEX_MAJOR_NAME = 4;

	public static final int COLUMN_INDEX_START_SEMESTER = 5;

	public static final int COLUMN_INDEX_AVATAR = 6;

	public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ StudentTable.TABLE_NAME + " ("
			+ StudentTable.COLUMN_NAME_STUDENT_EMAIL
			+ " VARCHAR(40) PRIMARY KEY,"
			+ StudentTable.COLUMN_NAME_STUDENT_NAME + " TEXT,"
			+ StudentTable.COLUMN_NAME_PASSWORD + " INTEGER,"
			+ StudentTable.COLUMN_NAME_DEPARTMENT_NAME + " TEXT,"
			+ StudentTable.COLUMN_NAME_MAJOR_NAME + " VARCHAR(200),"
			+ StudentTable.COLUMN_NAME_START_SEMESTER + " TEXT,"
			+ StudentTable.COLUMN_NAME_AVATAR + " BLOB)";

	public static final String SQL_DELETE_TABLE = "DELETE FROM " + TABLE_NAME;

	public static boolean insert(SQLiteDatabase db, String studentEmail,
			String studentName, String password, String departmentName,
			String majorName, int startYear, String startSemester, byte[] avatar) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_STUDENT_EMAIL, studentEmail);
		values.put(COLUMN_NAME_STUDENT_NAME, studentName);
		values.put(COLUMN_NAME_PASSWORD, password.hashCode());
		values.put(COLUMN_NAME_DEPARTMENT_NAME, departmentName);
		values.put(COLUMN_NAME_MAJOR_NAME, majorName);

		String sem = startYear + "_" + startSemester;
		values.put(COLUMN_NAME_START_SEMESTER, sem);
		values.put(COLUMN_NAME_AVATAR, avatar);

		long newRowId;
		newRowId = db.insert(TABLE_NAME, null, values);
		if (newRowId == -1) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean insert(SQLiteDatabase db, String studentEmail,
			String studentName, int password, String departmentName,
			String majorName, int startYear, String startSemester, byte[] avatar) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_STUDENT_EMAIL, studentEmail);
		values.put(COLUMN_NAME_STUDENT_NAME, studentName);
		values.put(COLUMN_NAME_PASSWORD, password);
		values.put(COLUMN_NAME_DEPARTMENT_NAME, departmentName);
		values.put(COLUMN_NAME_MAJOR_NAME, majorName);

		String sem = startYear + "_" + startSemester;
		values.put(COLUMN_NAME_START_SEMESTER, sem);
		values.put(COLUMN_NAME_AVATAR, avatar);

		long newRowId;
		newRowId = db.insert(TABLE_NAME, null, values);
		if (newRowId == -1) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean mysqlInsert(Connection connect, String studentEmail,
			String studentName, String password, String departmentName,
			String majorName, int startYear, String startSemester, byte[] avatar) {
		PreparedStatement stat = null;
		int result = 0;
		try {
			stat = connect.prepareStatement("insert into "
					+ CVMasterServer.DB_NAME + "." + StudentTable.TABLE_NAME
					+ " values(?,?,?,?,?,?,?)");
			stat.setString(1, studentEmail);
			stat.setString(2, studentName);
			stat.setInt(3, password.hashCode());
			stat.setString(4, departmentName);
			stat.setString(5, majorName);
			stat.setString(6, startYear + "_" + startSemester);
			stat.setBytes(7, avatar);
			result = stat.executeUpdate();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		if (result == 0) {
			return false;
		}
		return true;
	}

	public static boolean mysqlInsert(Connection connect, String studentEmail,
			String studentName, int password, String departmentName,
			String majorName, String startSemester, byte[] avatar) {
		PreparedStatement stat = null;
		int result = 0;
		try {
			stat = connect.prepareStatement("insert into "
					+ CVMasterServer.DB_NAME + "." + StudentTable.TABLE_NAME
					+ " values(?,?,?,?,?,?,?)");
			stat.setString(1, studentEmail);
			stat.setString(2, studentName);
			stat.setInt(3, password);
			stat.setString(4, departmentName);
			stat.setString(5, majorName);
			stat.setString(6, startSemester);
			stat.setBytes(7, avatar);
			result = stat.executeUpdate();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		if (result == 0) {
			return false;
		}
		return true;
	}

	public static boolean insertIntoSqlite(CVMasterDbHelper dbHelper,
			Student student) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String[] words = student.getStartSemester().split("_");

		boolean status = StudentTable.insert(db, student.getStudentEmail(),
				student.getStudentName(), student.getPassword(),
				student.getDepartmentName(), student.getMajorName(),
				Integer.parseInt(words[0]), words[1], student.getImage());
		db.close();
		return status;
	}
}
