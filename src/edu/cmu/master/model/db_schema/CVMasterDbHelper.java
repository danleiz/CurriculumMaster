package edu.cmu.master.model.db_schema;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CVMasterDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;

	public static final String DATABASE_NAME = "CVMaster.db";

	public CVMasterDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {

		db.execSQL(StudentTable.SQL_CREATE_TABLE);
		db.execSQL(CurriculumTable.SQL_CREATE_TABLE);
		db.execSQL(CourseTable.SQL_CREATE_TABLE);
		db.execSQL(ChooseCourseTable.SQL_CREATE_TABLE);
		db.execSQL(CoreCoursesTable.SQL_CREATE_TABLE);
		db.execSQL(SelectiveCoursesTable.SQL_CREATE_TABLE);
		db.execSQL(MajorCurriculumTable.SQL_CREATE_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(StudentTable.SQL_DELETE_TABLE);
		db.execSQL(CurriculumTable.SQL_DELETE_TABLE);
		db.execSQL(CourseTable.SQL_DELETE_TABLE);
		db.execSQL(ChooseCourseTable.SQL_DELETE_TABLE);
		db.execSQL(CoreCoursesTable.SQL_DELETE_TABLE);
		db.execSQL(SelectiveCoursesTable.SQL_DELETE_TABLE);
		db.execSQL(MajorCurriculumTable.SQL_DELETE_TABLE);
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

}
