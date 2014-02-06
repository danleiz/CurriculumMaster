package edu.cmu.master.view.welcome;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import edu.cmu.master.R;
import edu.cmu.master.control.MasterApplication;
import edu.cmu.master.model.DBHandler;
import edu.cmu.master.model.db_schema.CVMasterDbHelper;
import edu.cmu.master.model.db_schema.ChooseCourseTable;
import edu.cmu.master.model.db_schema.CourseTable;
import edu.cmu.master.model.db_schema.CurriculumTable;
import edu.cmu.master.model.db_schema.MajorCurriculumTable;
import edu.cmu.master.model.db_schema.StudentTable;
import edu.cmu.master.model.entities.ChooseCourse;
import edu.cmu.master.model.entities.Course;
import edu.cmu.master.model.entities.Curriculum;
import edu.cmu.master.model.entities.MajorCurriculum;
import edu.cmu.master.model.entities.Student;
import edu.cmu.master.server.CVMasterMessage;

public class WelcomeActivity extends FragmentActivity {

	/**
	 * The number of pages (wizard steps) to show.
	 */

	private static int NUM_PAGES = 2;

	private static final int NUM_PAGES_FIRST = 5;

	private static final int NUM_PAGES_COMMON = 2;

	private static final String PREFS_NAME = "MyPrefsFile";

	private static final String IS_FIRST = "firstTimeLaunch";

	/**
	 * The pager widget, which handles animation and allows swiping horizontally to access previous
	 * and next wizard steps.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private PagerAdapter mPagerAdapter;

	private Bitmap image;

	private String studentEmail;

	private String studentPassword;

	private EditText email;

	private EditText password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.i("position", "in activity oncreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_slide);

		Object image = getLastCustomNonConfigurationInstance();
		if (image != null && image instanceof Bitmap) {

			Log.i("position", "in activity oncreate getLastCustomNonConfigurationInstance");
			this.image = (Bitmap) image;
		}

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		boolean isFirst = settings.getBoolean(IS_FIRST, true);
		if (isFirst) {
			settings.edit().putBoolean(IS_FIRST, false).commit();
			NUM_PAGES = NUM_PAGES_FIRST;

		} else {
			NUM_PAGES = NUM_PAGES_COMMON;
		}

		Log.i("welcome", String.valueOf(isFirst));

		mPager = (ViewPager) findViewById(R.id.welcome_slide);
		mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
		mPager.setAdapter(mPagerAdapter);

	}

	/**
	 * store image in register page when orientation is changed.
	 */
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		Log.i("position", "in activity onRetainCustomNonConfigurationInstance");
		final Bitmap data = image;
		return data;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.welcome_activity, menu);

		menu.findItem(R.id.action_bar_login).setEnabled(true);
		menu.findItem(R.id.action_bar_register).setEnabled(true);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case R.id.action_bar_login:
				mPager.setCurrentItem(NUM_PAGES - 2);
				return true;

			case R.id.action_bar_register:
				mPager.setCurrentItem(NUM_PAGES - 1);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment} objects, in
	 * sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (NUM_PAGES == 5) {

				if (position == NUM_PAGES - 2) {

					return LoginFragment.create(position);
				} else if (position == NUM_PAGES - 1) {
					return RegisterFragment.create(position, image);

				} else {
					return ScreenSlidePageFragment.create(position);
				}
			} else {
				if (position == NUM_PAGES - 2) {

					return LoginFragment.create(position);
				} else {

					return RegisterFragment.create(position, image);
				}
			}
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}

	public void login(View view) {

		email = (EditText) findViewById(R.id.login_email);
		studentEmail = email.getText().toString();
		password = (EditText) findViewById(R.id.login_password);
		studentPassword = password.getText().toString();
		StudentInfoTask getStudent = new StudentInfoTask();
		getStudent.execute();
	}

	public void register(View view) {
		mPager.setCurrentItem(NUM_PAGES - 1);
	}

	public void btnRegister(View view) {

		EditText emailEditText = (EditText) this.findViewById(R.id.register_email);
		EditText passwordEditText = (EditText) this.findViewById(R.id.register_password);
		EditText repeatPasswordEditText = (EditText) this.findViewById(R.id.register_repeat_password);
		EditText yearEditText = (EditText) this.findViewById(R.id.register_enroll_year);
		EditText fnameEditText = (EditText) this.findViewById(R.id.register_fname);
		EditText lnameEditText = (EditText) this.findViewById(R.id.register_lname);

		String email = emailEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		String repeatPassword = repeatPasswordEditText.getText().toString();
		String year = yearEditText.getText().toString();
		String fname = fnameEditText.getText().toString();
		String lname = lnameEditText.getText().toString();

		Spinner departmentSpinner = (Spinner) findViewById(R.id.register_department);
		String department = departmentSpinner.getSelectedItem().toString();

		Spinner majorSpinner = (Spinner) findViewById(R.id.register_major);
		String major = majorSpinner.getSelectedItem().toString();

		Spinner degreeSpinner = (Spinner) findViewById(R.id.register_degree);
		String degree = degreeSpinner.getSelectedItem().toString();

		Spinner semesterSpinner = (Spinner) findViewById(R.id.semester_spinner);
		String semester = semesterSpinner.getSelectedItem().toString();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		ImageView iv = (ImageView) findViewById(R.id.register_avatar);
		iv.buildDrawingCache();
		Bitmap bmap = iv.getDrawingCache();
		bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();

		String errorMsg = null;
		boolean isValidEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
		boolean isPasswordValid = password.length() > 0
				&& password.equals(repeatPassword);
		boolean isNameValid = fname.length() > 0 && lname.length() > 0;
		boolean isYearValid = year.length() > 0
				&& Integer.parseInt(year) > 2000;

		if (!isValidEmail) {
			errorMsg = "Please input a valid email address.";

		} else if (!isPasswordValid) {
			errorMsg = "Please input valid passwords.";

		} else if (!isNameValid) {
			errorMsg = "Please input name.";

		} else if (!isYearValid) {
			errorMsg = "Please input a valid year.";

		}

		if (errorMsg == null) {

			Student newStudent = new Student(email, fname + " " + lname, password, department, degree
					+ "_" + major, year + "_" + semester, byteArray);

			StudentRegisterTask registerTask = new StudentRegisterTask();
			registerTask.execute(newStudent);

		} else {
			TextView error = (TextView) this.findViewById(R.id.register_error);
			error.setText(errorMsg);

		}

	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public class StudentInfoTask extends AsyncTask<Void, byte[], Boolean> {

		Socket sSocket;

		String errorMsg;

		CVMasterMessage getStudent;

		@Override
		protected Boolean doInBackground(Void... params) {
			try {

				getStudent = new CVMasterMessage(CVMasterMessage.GET_STUDENT_INFO_FLAG, true, new Object[] {
						studentEmail, studentPassword });

				try {
					sSocket = new Socket(CVMasterMessage.serverHostName, CVMasterMessage.servicePort);
				}
				catch (Exception ex) {
					errorMsg = "Can't connect socket, please try again later.";
					Log.e("error", errorMsg);

					return false;
				}

				getStudent = getStudent.sendReceiveRound(sSocket);
				if (getStudent == null) {
					errorMsg = "Can't find user, please input again or register.";
					Log.e("error", errorMsg);
					return false;
				}
				return true;
			}

			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					sSocket.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {

				CVMasterDbHelper helper = new CVMasterDbHelper(WelcomeActivity.this);
				Log.i("AsyncTask", "onPostExecute: Completed correct.");

				Object[] payload = (Object[]) getStudent.getPayload();
				Student student = (Student) payload[0];
				MajorCurriculum mc = (MajorCurriculum) payload[1];
				Curriculum cv = (Curriculum) payload[2];
				ChooseCourse[] registeredCourse = (ChooseCourse[]) payload[3];
				Course[] courses = (Course[]) payload[4];

				StudentTable.insertIntoSqlite(helper, student);

				MajorCurriculumTable.insertIntoSqlite(helper, mc);
				CurriculumTable.insertIntoSqlit(helper, cv);
				for (ChooseCourse c : registeredCourse)
					ChooseCourseTable.insertToSqlite(helper, c);
				for (Course c : courses)
					CourseTable.insertToSqlite(helper, c);
				Intent intent = new Intent(WelcomeActivity.this, MainPageActivity.class);

				MasterApplication ma = (MasterApplication) getApplicationContext();
				ma.setCurrentStudent(student);

				startActivity(intent);
				finish();
			} else {
				mPager.setCurrentItem(NUM_PAGES - 2);
				email.setText("");
				password.setText("");
				TextView errorMsg = (TextView) findViewById(R.id.register_success);
				errorMsg.setText(this.errorMsg);

				Log.i("AsyncTask", "onPostExecute: Completed with an Error.");
			}
		}
	}

	public class StudentRegisterTask extends
			AsyncTask<Student, byte[], Boolean> {

		Socket sSocket; // Network Socket

		CVMasterMessage register;

		String errorMsg;

		@Override
		protected Boolean doInBackground(Student... params) {
			try {
				register = new CVMasterMessage(CVMasterMessage.REGISTER_STUDENT_FLAG, true, params[0]);
				try {
					sSocket = new Socket(CVMasterMessage.serverHostName, CVMasterMessage.servicePort);
				}
				catch (Exception ex) {
					errorMsg = "Can't connect socket, please try again later.";
					Log.e("error", errorMsg);
					return false;
				}
				register = register.sendReceiveRound(sSocket);
				if (register == null) {
					errorMsg = "Please Register again..";
					Log.e("error", errorMsg);
					return false;
				}
				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					sSocket.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				mPager.setCurrentItem(NUM_PAGES - 2);
				TextView successMsg = (TextView) findViewById(R.id.register_success);
				successMsg.setText("Register Successfully. Please login");
				successMsg.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));

			} else {
				TextView error = (TextView) findViewById(R.id.register_error);
				error.setText(errorMsg);
			}
		}
	}
}
