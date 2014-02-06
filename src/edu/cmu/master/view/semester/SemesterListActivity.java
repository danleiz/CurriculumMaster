package edu.cmu.master.view.semester;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import edu.cmu.master.R;
import edu.cmu.master.control.MasterApplication;
import edu.cmu.master.model.entities.Student;

/**
 * An activity representing a list of Courses. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link SelectedCourseActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SemesterListFragment} and the item details (if present) is a
 * {@link SelectedCourseFragment}.
 * <p>
 * This activity also implements the required
 * {@link SemesterListFragment.Callbacks} interface to listen for item selections.
 */
public class SemesterListActivity extends Activity implements
		SemesterListFragment.Callbacks {

	public static final String TAG = "SemesterListActivity";
	
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	
	private String mSelectedSemester;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_semester_list);

		if (findViewById(R.id.selected_course_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((SemesterListFragment) getFragmentManager().findFragmentById(
					R.id.semester_list)).setActivateOnItemClick(true);
		}
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
//		((SemesterListFragment) getFragmentManager().findFragmentById(
//				R.id.semester_list)).getListView().setItemChecked(ListView.INVALID_POSITION, true);
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		
		Student current = ((MasterApplication)getApplicationContext()).getCurrentStudent();
		
		Bundle arguments = new Bundle();
		arguments.putString(SelectedCourseFragment.ARG_SEMESTER, mSelectedSemester);
		arguments.putString(SelectedCourseFragment.ARG_STUDENT_EMAIL, current.getStudentEmail());
		
		SelectedCourseFragment fragment = new SelectedCourseFragment();
		fragment.setArguments(arguments);
		getFragmentManager().beginTransaction()
				.replace(R.id.selected_course_detail_container, fragment, mSelectedSemester).commit();
	}

	/**
	 * Callback method from {@link SemesterListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String semester) {
		mSelectedSemester = semester;
		
		Student current = ((MasterApplication)getApplicationContext()).getCurrentStudent();		
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(SelectedCourseFragment.ARG_SEMESTER, semester);
			arguments.putString(SelectedCourseFragment.ARG_STUDENT_EMAIL, current.getStudentEmail());
			Log.i(TAG, "email: " + current.getStudentEmail() + ", semester: " + semester);
			
			SelectedCourseFragment fragment = new SelectedCourseFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.selected_course_detail_container, fragment, mSelectedSemester).commit();
		} else {
			// In single-pane mode, simply start the detail activity for the selected item ID.
			Intent detailIntent = new Intent(this, SelectedCourseActivity.class);
			detailIntent.putExtra(SelectedCourseFragment.ARG_SEMESTER, semester);
			detailIntent.putExtra(SelectedCourseFragment.ARG_STUDENT_EMAIL, current.getStudentEmail());
			startActivity(detailIntent);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.actions_semester_list_activity, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	NavUtils.navigateUpFromSameTask(this);
	    	break;
	    case R.id.action_settings:
	    	// TODO: add a setting activity and show it from here
	    	break;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
