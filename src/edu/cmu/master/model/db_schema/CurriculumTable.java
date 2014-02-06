package edu.cmu.master.model.db_schema;

import java.sql.Connection;
import java.sql.PreparedStatement;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import edu.cmu.master.model.entities.Course;
import edu.cmu.master.model.entities.Curriculum;
import edu.cmu.master.server.CVMasterServer;

public abstract class CurriculumTable {
	public static final String TABLE_NAME = "curriculum";
	public static final String COLUMN_NAME_CV_ID = "curriculumId";
	public static final String COLUMN_NAME_REQUIRED_UNIT = "requiredUnit";

	public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ CurriculumTable.TABLE_NAME + " ("
			+ CurriculumTable.COLUMN_NAME_CV_ID + " INTEGER PRIMARY KEY,"
			+ CurriculumTable.COLUMN_NAME_REQUIRED_UNIT + " INTEGER)";

	public static final String SQL_DELETE_TABLE = "DELETE FROM "
			+ TABLE_NAME;
	public static boolean insert(SQLiteDatabase db, int curriculumId, int requiredUnit) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_CV_ID, curriculumId);
		values.put(COLUMN_NAME_REQUIRED_UNIT, requiredUnit);

		long newRowId;
		newRowId = db.insert(TABLE_NAME, null, values);
		if (newRowId == -1) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean mysqlInsert(Connection connect, int curriculumId, int requiredUnit) {
		PreparedStatement stat = null;
		int result = 0;
		try {
			stat = connect.prepareStatement("insert into "
					+ CVMasterServer.DB_NAME + "." + TABLE_NAME
					+ " values (?,?)");
			stat.setInt(1, curriculumId);
			stat.setInt(2, requiredUnit);
			result = stat.executeUpdate();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		if (result == 0) {
			return false;
		}
		return true;
	}
	
	public static boolean insertIntoSqlit(CVMasterDbHelper dbHelper, Curriculum curriculum) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		boolean status = true;

		int curriculumId = curriculum.getCurriculumId();
		
		status = CurriculumTable.insert(db, curriculumId,
				curriculum.getRequiredUnit());
		if (!status) {
			return false;
		}
		Course[] coreCourses = curriculum.getCoreCourses();
		for (Course course : coreCourses) {
			status = CoreCoursesTable
					.insert(db, curriculumId, course.getCourseId());
			if (!status) {
				return false;
			}
		}

		for (Course course : curriculum.getSelectiveCourses()) {
			status = SelectiveCoursesTable.insert(db, curriculumId,
					course.getCourseId());
			if (!status) {
				return false;
			}
		}
		return true;
	}
}
