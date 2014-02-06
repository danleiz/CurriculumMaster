package edu.cmu.master.control;

import android.app.Application;
import edu.cmu.master.model.entities.Student;

/**
 * Global application context to manage states across different activities
 * 
 * @author zhe
 */
public class MasterApplication extends Application {

	private Student currentStudent;

	public Student getCurrentStudent() {
		return currentStudent;
	}

	public void setCurrentStudent(Student currentStudent) {
		this.currentStudent = currentStudent;
	}

	public void onCreate() {
		super.onCreate();

	}


}
