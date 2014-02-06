package edu.cmu.master.view.department;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import edu.cmu.master.R;

public class DepartmentCourseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_department_course);
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Intent intent = getIntent();
			String department = intent.getStringExtra(DepartmentCourseFragment.ARG_DEPARTMENT);
			String semester = intent.getStringExtra(DepartmentCourseFragment.ARG_SEMESTER);
			
			Bundle arguments = new Bundle();
			arguments.putString(DepartmentCourseFragment.ARG_DEPARTMENT, department);
			arguments.putString(DepartmentCourseFragment.ARG_SEMESTER, semester);
			
			DepartmentCourseFragment fragment = new DepartmentCourseFragment();
			fragment.setArguments(arguments); 
			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			fragmentTransaction.add(R.id.department_course_detail_container, fragment);
			fragmentTransaction.commit();
		}		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpTo(this, new Intent(this, DepartmentListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
