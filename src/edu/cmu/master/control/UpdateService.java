package edu.cmu.master.control;

import java.net.Socket;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.cmu.master.R;
import edu.cmu.master.model.DBHandler;
import edu.cmu.master.model.db_schema.CVMasterDbHelper;
import edu.cmu.master.model.entities.ChooseCourse;
import edu.cmu.master.server.CVMasterMessage;

public class UpdateService extends IntentService {

	public UpdateService() {

		super("UpdateService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		DBHandler db = DBHandler.getInstance();
		CVMasterDbHelper helper = new CVMasterDbHelper(UpdateService.this);
		String email = (String) intent.getCharSequenceExtra("email");
		ChooseCourse[] cc = db.getChoosedCourses(helper, email);

		CVMasterMessage regCourses = new CVMasterMessage(CVMasterMessage.NEW_REGISTERED_COURSE_FLAG, true, new Object[] {
				email, cc });
		Socket sSocket;
		try {
			sSocket = new Socket(CVMasterMessage.serverHostName, CVMasterMessage.servicePort);
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return;
		}
		regCourses = regCourses.sendReceiveRound(sSocket);
		Log.i("position", "courses registered updated");
		
		DBHandler dbUtil = DBHandler.getInstance();
		CVMasterDbHelper dbHelper = new CVMasterDbHelper(getApplicationContext());
		
		//deleteDatabase(dbHelper.getDatabaseName());
		dbUtil.deleteDb(dbHelper);
		Log.i("position","db deleted");

		/**************** Notification Sample Code *********************/
		Intent notificationIntent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		Notification.Builder mBuilder = new Notification.Builder(this);
		mBuilder.setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("Course registration Approved")
		        .setContentText("Administrator has approved your request!")
		        .setAutoCancel(true)
		        .setContentIntent(contentIntent);
	
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification notification = mBuilder.build();
		mNotificationManager.notify(1, notification);
	}
}
