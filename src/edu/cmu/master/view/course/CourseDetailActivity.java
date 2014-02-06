package edu.cmu.master.view.course;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.master.R;
import edu.cmu.master.control.MasterApplication;
import edu.cmu.master.model.DBHandler;
import edu.cmu.master.model.db_schema.CVMasterDbHelper;
import edu.cmu.master.model.entities.Course;
import edu.cmu.master.model.entities.Student;
import edu.cmu.master.view.department.DepartmentCourseFragment;

public class CourseDetailActivity extends Activity {
	public static final String ARG_COURSE = "arg_course";
	public static final String ARG_SEMESTER = "arg_semester";
	
	private Course mCourse;
	private String mSemester;		   // semester to register this course to
	private boolean isRegistered;      // is this course is already registered 
	private Student currentStudent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_detail);
		
		Intent intent = getIntent();
		mCourse = (Course) intent.getSerializableExtra(ARG_COURSE);
		mSemester = intent.getStringExtra(ARG_SEMESTER);
		
		currentStudent = ((MasterApplication) getApplication()).getCurrentStudent();
		
		CVMasterDbHelper dbHelper = new CVMasterDbHelper(CourseDetailActivity.this);		
		isRegistered = DBHandler.getInstance().isRegistered(dbHelper,
				currentStudent.getStudentEmail(), mCourse.getCourseId());		
		
		/******* populate info on UI *******/
		setTitle(String.valueOf(mCourse.getCourseId()) + " " + mCourse.getCourseName());
		
		TextView detailView = (TextView) findViewById(R.id.textview_course_description);
		detailView.setText(mCourse.getDescription());
		
		TextView departmentView = (TextView) findViewById(R.id.textview_course_department);
		departmentView.setText(mCourse.getDepartmentName());
		
		TextView instructorView = (TextView) findViewById(R.id.textview_course_instructor);
		instructorView.setText(mCourse.getInstructorName());
	
		TextView unitView = (TextView) findViewById(R.id.textview_course_unit);
		unitView.setText(mCourse.getUnit() + " units");
		
		TextView capacityView = (TextView) findViewById(R.id.textview_course_capacity);
		capacityView.setText(mCourse.getCapacity() + " students");
		
		TextView urlView = (TextView) findViewById(R.id.textview_course_url);
		if (mCourse.getUrl().equals("dummy"))
			urlView.setText(R.string.text_course_no_url);
		else
			urlView.setText(mCourse.getUrl());

		// show up navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.actions_course_detail_activity, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.action_register_course);
	    
		if (isRegistered) {
			item.setTitle(R.string.action_already_registered);
			item.setEnabled(false);
		}
		
	    return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	Intent cancelIntent = new Intent();
	    	setResult(RESULT_CANCELED, cancelIntent); 
	    	this.finish();
	    	return true;
	    case R.id.action_register_course:
	    	DBHandler db = DBHandler.getInstance();
			CVMasterDbHelper dbHelper = new CVMasterDbHelper(CourseDetailActivity.this);

			db.registerCourse(dbHelper,	currentStudent.getStudentEmail(),
					mCourse.getCourseId(), mSemester);
			dbHelper.close();
			
			Toast.makeText(CourseDetailActivity.this,
					"Course " + mCourse.getCourseId() + " registered!",
					Toast.LENGTH_LONG).show();
			
	    	Intent okIntent = new Intent();
	    	okIntent.putExtra(DepartmentCourseFragment.ARG_REGISTERED_ID, mCourse.getCourseId());
	    	setResult(RESULT_OK, okIntent);   
			this.finish();
	    	return true;
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
	}
}
