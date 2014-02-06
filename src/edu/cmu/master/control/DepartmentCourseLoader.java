package edu.cmu.master.control;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import edu.cmu.master.model.DBHandler;
import edu.cmu.master.model.db_schema.CVMasterDbHelper;
import edu.cmu.master.model.entities.Course;

public class DepartmentCourseLoader extends AsyncTaskLoader<Course[]> {
	public static final String TAG = "DepartmentCourseLoader";
	
	// We hold a reference to the Loader’s data here.
	private Course[] departmentCourses;

	private String departmentName;
	private String semester;

	public DepartmentCourseLoader(Context context, String departmentName, String semester) {
		super(context);
		this.departmentName = departmentName;
		this.semester = semester;
	}

	/************************************************/
	/** A task that performs the asynchronous load **/
	/************************************************/	
	@Override
	public Course[] loadInBackground() {
		DBHandler dbUtil = DBHandler.getInstance();
		CVMasterDbHelper dbHelper = new CVMasterDbHelper(getContext());
		
		Course[] courses = dbUtil.getAllCourse(dbHelper, departmentName, semester);
		Log.i(TAG, "# of courses = " + courses.length);
		
		return courses;
	}
	
	/****************************************************/
	/** Deliver the results to the registered listener **/
	/****************************************************/
	@Override
	public void deliverResult(Course[] data) {
		if(isReset()) {
			// The Loader has been reset; ignore the result and invalidate the data.
			departmentCourses = null;
			return;
		}
		
		// Hold a reference to the old data so it doesn't get garbage collected.
	    // We must protect it until the new data has been delivered.
		Course[] oldData = departmentCourses;
		departmentCourses = data;
		
		if (isStarted()) {
			// If the Loader is in a started state, deliver the results to the
		    // client. The superclass method does this for us.
			super.deliverResult(data);
			
			Log.i(TAG, "coure data deliverred");
		} 

		// Invalidate the old data as we don't need it any more.
	    if (oldData != null && oldData != data) {
	    	oldData = null;
	    }	    
	}
	
	/*****************************************************/
	/** Implement the Loader’s state-dependent behavior **/
	/*****************************************************/	 
	@Override
	protected void onStartLoading() {
		if (departmentCourses != null) {
			// Deliver any previously loaded data immediately.
		    deliverResult(departmentCourses);
		}
		
		if (this.takeContentChanged() || departmentCourses == null) {
			// When the observer detects a change, it should call onContentChanged()
			// on the Loader, which will cause the next call to takeContentChanged() 
			// to return true. If this is ever the case (or if the current data is
			// null), we force a new load.
			forceLoad();
		}
	}
	
	@Override
	protected void onStopLoading() {
		// The Loader is in a stopped state, so we should attempt to cancel the 
	    // current load (if there is one).
	    cancelLoad();
	}
	
	@Override
	protected void onReset() {
		super.onReset();
		
		// Ensure the loader has been stopped.
	    onStopLoading();
	    
	    departmentCourses = null;
	}
	
	@Override
	public void onCanceled(Course[] data) {
		// Attempt to cancel the current asynchronous load.
	    super.onCanceled(data);
	    
	    departmentCourses = null;
	}
	
}
