package edu.cmu.master.view.welcome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import edu.cmu.master.R;
import edu.cmu.master.control.MasterApplication;
import edu.cmu.master.control.StudentCourseLoader;
import edu.cmu.master.control.UpdateService;
import edu.cmu.master.model.DBHandler;
import edu.cmu.master.model.db_schema.CVMasterDbHelper;
import edu.cmu.master.model.entities.Course;
import edu.cmu.master.model.entities.Curriculum;
import edu.cmu.master.model.entities.Student;
import edu.cmu.master.view.semester.SemesterListActivity;

public class MainPageActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Course[]> {

	private Student student;

	private Curriculum cm;

	private MasterApplication ma;

	private LoaderManager lm;

	private static final int LOADER_ID = 1;

	private Course[] courses;

	private boolean isUpdated;

	protected void onCreate(Bundle savedInstanceState) {
		Log.i("position", "in main page activity oncreate");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_page);

		ma = (MasterApplication) getApplicationContext();
		student = ma.getCurrentStudent();
		lm = this.getLoaderManager();

		lm.initLoader(LOADER_ID, null, this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainpage_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public Loader<Course[]> onCreateLoader(int id, Bundle args) {
		Log.i("position", "in main page activity on create loader");

		Context ctex = this.getApplicationContext();
		StudentCourseLoader loader = new StudentCourseLoader(ctex);
		return loader;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add_courses:
				// jump to SemesterListActivity to show all registered course
				Intent intent = new Intent(this, SemesterListActivity.class);

				startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i("position", "in main page activity on start");

		lm.restartLoader(LOADER_ID, null, this);
	}

	@Override
	public void onLoadFinished(Loader<Course[]> loader, Course[] newCourses) {
		Log.i("position", "in main page activity on load finished");
		isUpdated = isUpdated(newCourses);

		Log.i("isUpdates", String.valueOf(isUpdated));

		if (isUpdated) {

			courses = newCourses;
			cleanOldLayout();
			updatePage();

		}
	}

	@Override
	public void onLoaderReset(Loader<Course[]> loader) {
		// TODO Auto-generated method stub

	}

	private boolean isUpdated(Course[] newCourses) {
		boolean result = true;
		if (courses != null && courses.length == newCourses.length) {
			Arrays.sort(courses);
			Arrays.sort(newCourses);
			int i = 0;
			for (; i < courses.length; i++) {
				if (courses[i] != newCourses[i]) {
					break;
				}

			}
			if (i == courses.length)
				result = false;
		}

		return result;
	}

	private void cleanOldLayout() {
		TableLayout[] layouts = new TableLayout[5];

		layouts[0] = (TableLayout) findViewById(R.id.core_finished);
		layouts[1] = (TableLayout) findViewById(R.id.core_unfinished);
		layouts[2] = (TableLayout) findViewById(R.id.restricted_finished);
		layouts[3] = (TableLayout) findViewById(R.id.restricted_unfinished);
		layouts[4] = (TableLayout) findViewById(R.id.option_finished);
		Log.i("position", "in clean layouts");
		for (int i = 0; i < layouts.length; i++) {
			layouts[i].removeAllViews();
		}

	}

	private void updatePage() {
		int totalUnits = getCurriculum();
		int takenUnits = 0;
		setInformation();

		HashSet<Integer> takenCourse = new HashSet<Integer>();
		for (Course c : courses) {
			takenCourse.add(c.getCourseId());
			takenUnits += c.getUnit();
		}

		float percent = (float) takenUnits / (float) totalUnits;

		ProgressBar pb = (ProgressBar) this.findViewById(R.id.progress_horizontal);
		pb.setProgress(Math.round(percent * 100));

		TextView total = (TextView) this.findViewById(R.id.completion_percentage);

		total.setText(String.format(getResources().getString(R.string.text_completion_num, takenUnits, totalUnits)));

		TableLayout[] layouts = new TableLayout[5];

		layouts[0] = (TableLayout) findViewById(R.id.core_finished);
		layouts[1] = (TableLayout) findViewById(R.id.core_unfinished);
		layouts[2] = (TableLayout) findViewById(R.id.restricted_finished);
		layouts[3] = (TableLayout) findViewById(R.id.restricted_unfinished);
		layouts[4] = (TableLayout) findViewById(R.id.option_finished);
		// Create a new row to be added.
		int totalCoreUnits = 0;
		int totalSelectiveUnits = 0;
		for (int k = 0; k < 2; k++) {

			ArrayList<Course> currCourse = new ArrayList<Course>();
			switch (k) {
				case 0:
					currCourse.clear();
					currCourse.addAll(Arrays.asList(cm.getCoreCourses()));
					break;
				case 1:
					currCourse.clear();
					currCourse.addAll(Arrays.asList(cm.getSelectiveCourses()));
					break;
			}

			for (int i = 0; i < currCourse.size(); i++) {
				Course course = currCourse.get(i);
				Log.i(String.valueOf(i), course.getCourseName());
				String courseInfo = course.getCourseId() + " "
						+ course.getCourseName();
				switch (k) {
					case 0:
						totalCoreUnits += course.getUnit();
						break;
					case 1:
						totalSelectiveUnits += course.getUnit();
						break;
				}

				if (takenCourse.contains(course.getCourseId())) {
					TableRow tr = createTableRow(courseInfo, getResources().getColor(android.R.color.holo_green_light));

					layouts[k * 2].addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
					takenCourse.remove(course.getCourseId());

				} else {
					TableRow tr = createTableRow(courseInfo, getResources().getColor(android.R.color.holo_red_light));
					layouts[k * 2 + 1].addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
				}
			}
		}
		int optionUnits = 0;
		for (Course op : courses) {
			if (takenCourse.contains(op.getCourseId())) {
				optionUnits += op.getUnit();
				String courseInfo = op.getCourseId() + " " + op.getCourseName();
				Log.i("option courses", courseInfo);
				TableRow tr = createTableRow(courseInfo, getResources().getColor(android.R.color.holo_green_light));
				layouts[4].addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
			}
		}
		int leftOptionUnits = totalUnits - totalCoreUnits - totalSelectiveUnits
				- optionUnits;
		leftOptionUnits = leftOptionUnits < 0 ? 0 : leftOptionUnits;
		Log.i("mainpage", String.valueOf(leftOptionUnits));
		TextView option = (TextView) this.findViewById(R.id.curriculum_option);
		option.setText(String.format(getResources().getString(R.string.text_main_option, leftOptionUnits)));

	}

	public TableRow createTableRow(String text, int color) {
		TableRow tr = new TableRow(this);
		tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		TextView tv = new TextView(this);
		tv.setText(text);
		tv.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);
		tv.setTextColor(color);
		tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		tr.addView(tv);

		return tr;
	}

	private void setInformation() {

		TextView department = (TextView) this.findViewById(R.id.main_department);
		TextView major = (TextView) this.findViewById(R.id.main_major);
		TextView degree = (TextView) this.findViewById(R.id.main_degree);
		TextView name = (TextView) this.findViewById(R.id.main_basic);

		String[] terms = student.getMajorName().split("_");
		String studentName = student.getStudentName();

		ImageView iv = (ImageView) findViewById(R.id.main_image);

		byte[] imageByte = student.getImage();
		if (imageByte != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
			iv.getLayoutParams().height = bitmap.getHeight();
			iv.getLayoutParams().width = bitmap.getWidth();
			iv.setImageBitmap(bitmap);
		}
		department.setText(student.getDepartmentName());
		degree.setText(terms[0].trim());
		major.setText(terms[1].trim());
		name.setText(studentName);

	}

	private int getCurriculum() {
		int units = 0;
		DBHandler dbUtil = DBHandler.getInstance();
		CVMasterDbHelper dbHelper = new CVMasterDbHelper(getApplicationContext());
		cm = dbUtil.getCurriculum(dbHelper, student.getStudentEmail());
		if (cm != null)
			units = cm.getRequiredUnit();
		return units;

	}

	@Override
	public void onBackPressed() {
		Intent us = new Intent(getApplicationContext(), UpdateService.class);
		us.putExtra("email", student.getStudentEmail());
		getApplicationContext().startService(us);
		super.onBackPressed();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
