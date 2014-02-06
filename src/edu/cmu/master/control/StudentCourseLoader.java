package edu.cmu.master.control;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import edu.cmu.master.model.DBHandler;
import edu.cmu.master.model.db_schema.CVMasterDbHelper;
import edu.cmu.master.model.entities.Course;
import edu.cmu.master.model.entities.Student;

public class StudentCourseLoader extends AsyncTaskLoader<Course[]> {

	public StudentCourseLoader(Context context) {
		// Loaders may be used across multiple Activities (assuming they aren't
		// bound to the LoaderManager), so NEVER hold a reference to the context
		// directly. Doing so will cause you to leak an entire Activity's context.
		// The superclass constructor will store a reference to the Application
		// Context instead, and can be retrieved with a call to getContext().
		super(context);

	}

	private Course[] takenCourses;

	@Override
	public Course[] loadInBackground() {

		Context c = getContext();
		MasterApplication ma = (MasterApplication) c;
		Student student = ma.getCurrentStudent();
		String studentEmail = student.getStudentEmail();
		DBHandler dbUtil = DBHandler.getInstance();
		CVMasterDbHelper dbHelper = new CVMasterDbHelper(c);
		// get all the course student has taken
		Course[] courses = dbUtil.getRegisteredCourses(dbHelper, studentEmail);

		return courses;
		
	}

	@Override
	public void deliverResult(Course[] data) {
		if (isReset()) {
			// The Loader has been reset; ignore the result and invalidate the data.
			takenCourses = null;
			return;
		}

		// Hold a reference to the old data so it doesn't get garbage collected.
		// We must protect it until the new data has been delivered.
		Course[] oldData = takenCourses;
		takenCourses = data;

		if (isStarted()) {
			// If the Loader is in a started state, deliver the results to the
			// client. The superclass method does this for us.
			super.deliverResult(data);
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
		if (takenCourses != null) {
			// Deliver any previously loaded data immediately.
			deliverResult(takenCourses);
		}

		if (this.takeContentChanged() || takenCourses == null) {
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

		takenCourses = null;
	}

	@Override
	public void onCanceled(Course[] data) {
		// Attempt to cancel the current asynchronous load.
		super.onCanceled(data);

		takenCourses = null;
	}

}
