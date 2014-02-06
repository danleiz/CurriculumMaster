package edu.cmu.master.control;

import android.content.AsyncTaskLoader;
import android.content.Context;
import edu.cmu.master.model.DBHandler;
import edu.cmu.master.model.db_schema.CVMasterDbHelper;

public class DepartmentLoader extends AsyncTaskLoader<String[]> {
	// We hold a reference to the Loader’s data here.
	private String[] departments;
	
	public DepartmentLoader(Context context) {
		// Loaders may be used across multiple Activities (assuming they aren't
	    // bound to the LoaderManager), so NEVER hold a reference to the context
	    // directly. Doing so will cause you to leak an entire Activity's context.
	    // The superclass constructor will store a reference to the Application
	    // Context instead, and can be retrieved with a call to getContext().
		super(context);
	}
	
	/************************************************/
	/** A task that performs the asynchronous load **/
	/************************************************/	
	@Override
	public String[] loadInBackground() {
		DBHandler dbUtil = DBHandler.getInstance();
		CVMasterDbHelper dbHelper = new CVMasterDbHelper(getContext());
		return dbUtil.getAllDepartment(dbHelper);
	}	

	/****************************************************/
	/** Deliver the results to the registered listener **/
	/****************************************************/
	@Override
	public void deliverResult(String[] data) {
		if(isReset()) {
			// The Loader has been reset; ignore the result and invalidate the data.
			departments = null;
			return;
		}
		
		// Hold a reference to the old data so it doesn't get garbage collected.
	    // We must protect it until the new data has been delivered.
		String[] oldData = departments;
		departments = data;
		
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
		if (departments != null) {
			// Deliver any previously loaded data immediately.
		    deliverResult(departments);
		}
		
		if (this.takeContentChanged() || departments == null) {
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
	    
	    departments = null;
	}
	
	@Override
	public void onCanceled(String[] data) {
		// Attempt to cancel the current asynchronous load.
	    super.onCanceled(data);
	    
	    departments = null;
	}
}
