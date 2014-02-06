package edu.cmu.master.view.department;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ListView;
import edu.cmu.master.R;
import edu.cmu.master.control.CourseListAdapter;
import edu.cmu.master.control.DepartmentCourseLoader;
import edu.cmu.master.model.DBHandler;
import edu.cmu.master.model.db_schema.CVMasterDbHelper;
import edu.cmu.master.model.entities.Course;
import edu.cmu.master.view.course.CourseDetailActivity;

public class DepartmentCourseFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Course[]> {
	public static final String TAG = "DepartmentCourseFragment";
	
	/* start activity for result : request codes */
	public static final int REQUEST_COURSE_DETAIL = 1;
	
	public static final String ARG_REGISTERED_ID = "registered_course_id";	
	public static final String ARG_DEPARTMENT = "department";
	public static final String ARG_SEMESTER = "semester";
	public static final String ARG_STUDENT_EMAIL = "student_email";
	public static final String ARG_REGISTERED_IDS = "registered_course_ids";
	
	/* loader ID */
	private static final int LOADER_ID = 1;
	
	private CourseListAdapter mAdapter;	
	private ListViewSelectListener listener;
	
	/* parameters to query */
	private String department;
	private String semester;
	private String student_email;
	
	private HashSet<Integer> registeredIDs;
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public DepartmentCourseFragment() {	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle argument = getArguments();
		department = argument.getString(ARG_DEPARTMENT);
		semester = argument.getString(ARG_SEMESTER);
		student_email = argument.getString(ARG_STUDENT_EMAIL);
		
		// obtain registered course IDs
		CVMasterDbHelper dbHelper = new CVMasterDbHelper(getActivity());
		Course[] rCourses = DBHandler.getInstance().getRegisteredCourses(dbHelper, student_email);
		dbHelper.close();
		registeredIDs = new HashSet<Integer>();
		for (Course c : rCourses)
			registeredIDs.add(c.getCourseId());

		// enable options menu in activity's action bar
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		final ListView listView = getListView();	
		
		mAdapter = new CourseListAdapter(getActivity(), R.layout.item_course_list, listView, registeredIDs);
		setListAdapter(mAdapter);

		/* setup for contextual action bar */
		listener = new ListViewSelectListener(listView);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(listener);
		
		// initialize a department list loader
		this.getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// create customized layout for this ListFragment
		View rootView = inflater.inflate(R.layout.fragment_department_course,
				container, false);
		return rootView;
	}
		
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	   inflater.inflate(R.menu.actions_department_course, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   // handle item selection
	   switch (item.getItemId()) {
	      case R.id.action_add_course:
	    	// show contextual action bar
	    	getListView().startActionMode(listener);
			return true;
	      default:
	         return super.onOptionsItemSelected(item);
	   }
	}
	
	public void showCheckBoxes() {
		mAdapter.setShowCheckbox(true);
		getListView().invalidateViews();
	}
	
	public void hideCheckBoxes() {
		mAdapter.setShowCheckbox(false);
		getListView().invalidateViews();
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		
		// Show course detail in course detail activity
		Intent courseIntent = new Intent(getActivity(), CourseDetailActivity.class);
		courseIntent.putExtra(CourseDetailActivity.ARG_COURSE, mAdapter.getItem(position));
		courseIntent.putExtra(CourseDetailActivity.ARG_SEMESTER, semester);
		
		this.startActivityForResult(courseIntent, REQUEST_COURSE_DETAIL);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_COURSE_DETAIL:
			if (resultCode == Activity.RESULT_OK) {
				int courseID = data.getIntExtra(ARG_REGISTERED_ID, -1);
				Log.i(TAG, "[onActivityResult] course ID = " + courseID);
				
				// update registered courses state in adapter and local field
				mAdapter.addRegisteredCourse(courseID);
				registeredIDs.add(courseID);
				
				// update list view to refect registered course changes
				getListView().invalidateViews();
			}
			break;
		default:
			return;
		}
	}	

	@Override
	public Loader<Course[]> onCreateLoader(int id, Bundle args) {
		Context ctex = getActivity();
		DepartmentCourseLoader loader = new DepartmentCourseLoader(ctex, department, semester);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Course[]> loader, Course[] courses) {
		mAdapter.clear();
		mAdapter.addAll(courses);
		mAdapter.notifyDataSetChanged();
		
		Log.i(TAG, "onLoadFinished() is called");
		
//		if(isResumed()) {
//			setListShown(true);
//		} else {
//			setListShownNoAnimation(true);
//		}
	}

	@Override
	public void onLoaderReset(Loader<Course[]> loader) {
		mAdapter.clear();
	}

	private class ListViewSelectListener implements AbsListView.MultiChoiceModeListener {

		private ListView listView;
		
		public ListViewSelectListener(ListView listView) {
			this.listView = listView;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch(item.getItemId()) {
			case R.id.cab_add_coures:
				ArrayList<Integer> posList = new ArrayList<Integer>();
				SparseBooleanArray checked = listView.getCheckedItemPositions();
				for (int i = 0; i < listView.getCount(); i++) {
					if (checked.get(i)) {
						posList.add(i);
					}
				}

				List<Course> selected = mAdapter.getSelectedCourses(posList);	
				
				// extract overlapped courses from selected courses
				List<Course> overlapped = getOverlappedCourses(selected, registeredIDs);
				
				// register all selected and not-yet registered courses (in UI thread, not good practice)
				DBHandler dbHandler = DBHandler.getInstance();
				CVMasterDbHelper dbHelper = new CVMasterDbHelper(DepartmentCourseFragment.this.getActivity());
				for (Course c : selected)
					dbHandler.registerCourse(dbHelper, student_email, c.getCourseId(), semester);
				
				// show dialog if some courses are already registered
				if (overlapped.size() > 0) {   
					int[] idList = new int[overlapped.size()];
					String[] nameList = new String[overlapped.size()];
					
					fillIDandName(overlapped, idList, nameList);
					
					DepartmentListActivity activity = (DepartmentListActivity) DepartmentCourseFragment.this.getActivity();					
					activity.showAlreadyRegisteredDialog(idList, nameList);
				}
	
				// update adapter and local data field about registered courses 
				mAdapter.addRegisteredCourses(selected);
				for (Course c : selected)
					registeredIDs.add(c.getCourseId());
				
				// update list view to reflect registered course changes
				listView.invalidateViews();
				
				mode.finish();
				return true; 
			default:
				return false;
			}
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate the menu for the CAB
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.cab_add_course, menu);
	        
	        DepartmentCourseFragment.this.showCheckBoxes();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {			
			// reset all selected check boxes
			SparseBooleanArray checked = listView.getCheckedItemPositions();
			for (int i = 0; i < listView.getCount(); i++) {
				if (checked.get(i)) {
					CheckBox checkbox = (CheckBox) listView.getChildAt(i).findViewById(R.id.course_checkbox);
					checkbox.setChecked(false);
				}
			}
			
			// hide all check boxes
			DepartmentCourseFragment.this.hideCheckBoxes();
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// Here you can perform updates to the CAB due to an invalidate() request
			return false;
		}

		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			int checkedCount = listView.getCheckedItemCount();
			
			CheckBox checkbox = (CheckBox) listView.getChildAt(position).findViewById(R.id.course_checkbox);
			checkbox.setChecked(checked);
			
			mode.setTitle(checkedCount + " selected");			
		}
		
		private List<Course> getOverlappedCourses(List<Course> selected, HashSet<Integer> registered) {			
			ArrayList<Course> overlap = new ArrayList<Course>();
			ArrayList<Course> unique = new ArrayList<Course>();
			
			for(int i = selected.size() - 1; i >= 0; i--) {
				if (registered.contains(selected.get(i).getCourseId())) 
					overlap.add(selected.remove(i));
				else
					unique.add(selected.remove(i));
			}
			
			// update selected to be all unique (not-yet-registered) courses
			for(Course c : unique)
				selected.add(c);
			
			return overlap;
		}
		
		private void fillIDandName(List<Course> overlap, int[] idList, String[] nameList) {
			int index = 0;
			for (Course c : overlap) {
				idList[index] = c.getCourseId();
				nameList[index] = c.getCourseName();
				index++;
			}
		}
	}
}
