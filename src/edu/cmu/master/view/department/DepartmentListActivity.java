package edu.cmu.master.view.department;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import edu.cmu.master.R;

public class DepartmentListActivity extends Activity implements 
		DepartmentListFragment.Callbacks {
	public static final String TAG = "DepartmentListActivity";

	public static final String TARGET_SEMESTER = "semester";
	public static final String STUDENT_EMAIL = "student_email";
	
	private boolean mTwoPane;
	private String mSelectedDepartment;	
	private String mCurrentSemester;
	private String mStudentEmail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_department_list);

		if (findViewById(R.id.department_course_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and res/values-sw600dp). 
			// If this view is present, then the activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the 'activated' state when touched.
			((DepartmentListFragment) getFragmentManager().findFragmentById(
					R.id.department_list)).setActivateOnItemClick(true);
		}
		
		Intent intent = getIntent();
		mCurrentSemester = intent.getStringExtra(TARGET_SEMESTER);
		mStudentEmail = intent.getStringExtra(STUDENT_EMAIL);
		
		String title = this.getString(R.string.label_department_list)
				+ " for <font color='#00ddff'>"
				+ mCurrentSemester.replace('_', ' ') + "</font>";
		setTitle(Html.fromHtml(title));
		
		// enable up navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		
//		Bundle arguments = new Bundle();
//		arguments.putString(DepartmentCourseFragment.ARG_DEPARTMENT, mSelectedDepartment);
//		arguments.putString(DepartmentCourseFragment.ARG_SEMESTER, mCurrentSemester);
//		arguments.putString(DepartmentCourseFragment.ARG_STUDENT_EMAIL, mStudentEmail);
//		arguments.putIntArray(DepartmentCourseFragment.ARG_REGISTERED_IDS, registeredIDs);
//		
//		DepartmentCourseFragment fragment = new DepartmentCourseFragment();
//		fragment.setArguments(arguments);
//		getFragmentManager().beginTransaction()
//				.replace(R.id.department_course_detail_container, fragment, mSelectedDepartment).commit();
	}
	
	/**
	 * Callback method from {@link DepartmentListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String departmentName) {		
		mSelectedDepartment = departmentName;
		
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(DepartmentCourseFragment.ARG_DEPARTMENT, departmentName);
			arguments.putString(DepartmentCourseFragment.ARG_SEMESTER, mCurrentSemester);
			arguments.putString(DepartmentCourseFragment.ARG_STUDENT_EMAIL, mStudentEmail);
			
			DepartmentCourseFragment fragment = new DepartmentCourseFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.department_course_detail_container, fragment, mSelectedDepartment).commit();
		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, DepartmentCourseActivity.class);
			detailIntent.putExtra(DepartmentCourseFragment.ARG_DEPARTMENT, departmentName);
			detailIntent.putExtra(DepartmentCourseFragment.ARG_SEMESTER, mCurrentSemester);
			startActivity(detailIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.actions_department_list_activity, menu);
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

	public void showAlreadyRegisteredDialog(int[] idList, String[] nameList) {
		Bundle arguments = new Bundle();
		arguments.putIntArray(AlreadyRegisteredDialogFragment.ARG_ID_LIST, idList);
		arguments.putStringArray(AlreadyRegisteredDialogFragment.ARG_NAME_LIST, nameList);
		
		DialogFragment fragment = new AlreadyRegisteredDialogFragment();
		fragment.setArguments(arguments);
		fragment.show(getFragmentManager(), AlreadyRegisteredDialogFragment.TAG);
	}
	
	public static class AlreadyRegisteredDialogFragment extends DialogFragment {
		public static final String TAG = "AlreadyRegisteredDialogFragment";
		public static final String ARG_ID_LIST = "arg_id_list";
		public static final String ARG_NAME_LIST = "arg_name_list";
				
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int[] idList = getArguments().getIntArray(ARG_ID_LIST);
			String[] nameList = getArguments().getStringArray(ARG_NAME_LIST);
			
			StringBuilder sb = new StringBuilder();
			sb.append("Following courses registered already:");
			for (int i = 0; i < idList.length; i++) 
				sb.append("\n    - ").append(idList[i]).append(' ').append(nameList[i]);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Course Already Registered")
			       .setMessage(sb.toString())
			       .setPositiveButton("OK, I got it", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}			    	   
			       });				   
			 
			return builder.create();			 
		}
	}
}
