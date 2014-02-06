package edu.cmu.master.view.semester;

import java.util.ArrayList;

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
import edu.cmu.master.control.SemesterCourseLoader;
import edu.cmu.master.model.DBHandler;
import edu.cmu.master.model.db_schema.CVMasterDbHelper;
import edu.cmu.master.model.entities.Course;
import edu.cmu.master.view.course.CourseDetailActivity;
import edu.cmu.master.view.department.DepartmentListActivity;

/**
 * A fragment representing a single Course detail screen. This fragment is
 * either contained in a {@link SemesterListActivity} in two-pane mode (on
 * tablets) or a {@link SelectedCourseActivity} on handsets.
 */
public class SelectedCourseFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Course[]> {

	public static final String TAG = "SelectedCourseFragment";

	public static final String ARG_STUDENT_EMAIL = "student_email";

	public static final String ARG_SEMESTER = "semester";

	/* loader ID */
	private static final int LOADER_ID = 1;

	private CourseListAdapter mAdapter;

	private ListViewSelectListener listener;

	/* parameters to query */
	private String studentEmail;

	private String semester;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SelectedCourseFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = getArguments();
		studentEmail = arguments.getString(ARG_STUDENT_EMAIL);
		semester = arguments.getString(ARG_SEMESTER);

		// enable options menu in activity's action bar
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final ListView listView = getListView();

		mAdapter = new CourseListAdapter(getActivity(), R.layout.item_course_list, listView);
		setListAdapter(mAdapter);

		/* setup for contextual action bar */
		listener = new ListViewSelectListener(listView);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(listener);

		// initialize a department list loader
		this.getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_selected_course, container, false);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.actions_selected_course, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
			case R.id.action_remove_course:
				getListView().startActionMode(listener);
				return true;
			case R.id.action_add_course:
				// jump to DepartmentListActivity to show all available courses of selected semester
				Intent intent = new Intent(getActivity(), DepartmentListActivity.class);
				intent.putExtra(DepartmentListActivity.TARGET_SEMESTER, semester);
				intent.putExtra(DepartmentListActivity.STUDENT_EMAIL, studentEmail);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.action_remove_course);

		if (mAdapter.getCount() == 0) {
			item.setEnabled(false);
		} else {
			item.setEnabled(true);
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

		startActivity(courseIntent);
	}

	@Override
	public Loader<Course[]> onCreateLoader(int id, Bundle args) {
		Context ctex = getActivity();
		SemesterCourseLoader loader = new SemesterCourseLoader(ctex, studentEmail, semester);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Course[]> loader, Course[] courses) {
		mAdapter.clear();
		mAdapter.addAll(courses);
		mAdapter.notifyDataSetChanged();

		getActivity().invalidateOptionsMenu();
		Log.i(TAG, "onLoadFinished() is called");

	}

	@Override
	public void onLoaderReset(Loader<Course[]> loader) {
		mAdapter.clear();
	}

	private class ListViewSelectListener implements
			AbsListView.MultiChoiceModeListener {

		private ListView listView;

		public ListViewSelectListener(ListView listView) {
			this.listView = listView;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
				case R.id.cab_remove_coures:
					ArrayList<Integer> posList = new ArrayList<Integer>();
					SparseBooleanArray checked = listView.getCheckedItemPositions();
					for (int i = 0; i < listView.getCount(); i++) {
						if (checked.get(i)) {
							posList.add(i);
						}
					}

					int[] selectedIDs = mAdapter.getSelectedCourseIDs(posList);

					// log debug info
					String msg = "";
					for (int courseID : selectedIDs)
						msg += courseID + " ";
					Log.i(TAG, "Selected courses = " + msg);

					// remove selected courses from database (in UI thread)
					CVMasterDbHelper dbHelper = new CVMasterDbHelper(SelectedCourseFragment.this.getActivity());
					DBHandler.getInstance().removeCourses(dbHelper, studentEmail, selectedIDs);
					SelectedCourseFragment.this.getLoaderManager().restartLoader(LOADER_ID, null, SelectedCourseFragment.this);

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
			inflater.inflate(R.menu.cab_delete_course, menu);

			SelectedCourseFragment.this.showCheckBoxes();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// Here you can make any necessary updates to the activity when
			// the CAB is removed. By default, selected items are deselected/unchecked.

			// reset all selected check boxes
			SparseBooleanArray checked = listView.getCheckedItemPositions();
			for (int i = 0; i < listView.getCount(); i++) {
				if (checked.get(i)) {
					CheckBox checkbox = (CheckBox) listView.getChildAt(i).findViewById(R.id.course_checkbox);
					checkbox.setChecked(false);
				}
			}

			// hide all check boxes
			SelectedCourseFragment.this.hideCheckBoxes();
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// Here you can perform updates to the CAB due to an invalidate() request
			return false;
		}

		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
			int checkedCount = listView.getCheckedItemCount();

			CheckBox checkbox = (CheckBox) listView.getChildAt(position).findViewById(R.id.course_checkbox);
			checkbox.setChecked(checked);

			mode.setTitle(checkedCount + " selected");
		}
	}
}
