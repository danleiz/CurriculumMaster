package edu.cmu.master.model.db_schema;

import java.sql.Connection;
import java.sql.PreparedStatement;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import edu.cmu.master.model.entities.MajorCurriculum;
import edu.cmu.master.server.CVMasterServer;

public abstract class MajorCurriculumTable {
	public static final String TABLE_NAME = "majorCurriculum";
	public static final String COLUMN_NAME_MAJOR_NAME = "majorName";
	public static final String COLUMN_NAME_MAJOR_DURATION = "majorDuration";
	public static final String COLUMN_NAME_CURRICULUM_ID = "curriculumId";

	public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ MajorCurriculumTable.TABLE_NAME + " ("
			+ MajorCurriculumTable.COLUMN_NAME_MAJOR_NAME + " VARCHAR(200) PRIMARY KEY,"
			+ MajorCurriculumTable.COLUMN_NAME_MAJOR_DURATION + " INTEGER,"
			+ MajorCurriculumTable.COLUMN_NAME_CURRICULUM_ID + " INTEGER, "
			+ "FOREIGN KEY(" + MajorCurriculumTable.COLUMN_NAME_CURRICULUM_ID
			+ ") REFERENCES " + CurriculumTable.TABLE_NAME + "("
			+ CurriculumTable.COLUMN_NAME_CV_ID + "))";

	public static final String SQL_DELETE_TABLE = "DELETE FROM "
			+ TABLE_NAME;
	
	public static boolean insert(SQLiteDatabase db, String majorName, int duration, int curriculumId) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_MAJOR_NAME, majorName);
		values.put(COLUMN_NAME_MAJOR_DURATION, duration);
		values.put(COLUMN_NAME_CURRICULUM_ID, curriculumId);

		long newRowId;
		newRowId = db.insert(TABLE_NAME, null, values);
		if (newRowId == -1) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean mysqlInsert(Connection connect, String majorName, int duration, int curriculumId) {
		PreparedStatement stat = null;
		int result = 0;
		try {
			stat = connect.prepareStatement("insert into "
					+ CVMasterServer.DB_NAME + "." + TABLE_NAME
					+ " values (?,?,?)");
			stat.setString(1, majorName);
			stat.setInt(2, duration);
			stat.setInt(3, curriculumId);
			result = stat.executeUpdate();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		if (result == 0) {
			return false;
		}
		return true;
	}
	
	public static boolean insertIntoSqlite(CVMasterDbHelper dbHelper, MajorCurriculum cu) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		boolean status = MajorCurriculumTable.insert(db, cu.getMajorName(), cu.getDuration(),
				cu.getCurriculumId());
		db.close();
		return status;
	}
}

