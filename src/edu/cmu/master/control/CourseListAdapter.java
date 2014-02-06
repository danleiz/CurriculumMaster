package edu.cmu.master.control;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import edu.cmu.master.R;
import edu.cmu.master.model.entities.Course;

public class CourseListAdapter extends ArrayAdapter<Course> {
	
	/* obtain a reference of the list view using this adapter */
	private ListView listView;
	
	private boolean showCheckbox;
	private Course[] courses;
	private HashSet<Integer> registeredCourseIDs;
	private CheckboxOnCheckedChangeListener checkboxListener;
		
	public CourseListAdapter(Context context, int resource, ListView listView) {
		super(context, resource);
		this.listView = listView;
		this.checkboxListener = new CheckboxOnCheckedChangeListener();
	}
	
	// show additional info to indicate registered courses
	public CourseListAdapter(Context context, int resource, ListView listView, HashSet<Integer> registeredIDs) {
		this(context, resource, listView);
		
		this.registeredCourseIDs = new HashSet<Integer>();
		for (int courseID : registeredIDs)
			registeredCourseIDs.add(courseID);
	}
	
	public void setShowCheckbox(boolean showCheckbox) {
		this.showCheckbox = showCheckbox;
	}
	
	public void addRegisteredCourses(List<Course> newRegistered) {
		for (Course c : newRegistered)
			registeredCourseIDs.add(c.getCourseId());
	}
	
	public void addRegisteredCourse(int newRegisteredID) {
		registeredCourseIDs.add(newRegisteredID);
	}
	
	@Override
	public void addAll(Course... courses) {
		this.courses = courses;
	}
	
	@Override
	public void clear() {
		this.courses = null;
	}

	@Override
	public int getCount() {
		if(courses == null)
			return 0;
		else
			return courses.length;
	}
	
	@Override
	public Course getItem(int position) {
		if (courses == null) 
			return null;
		else
			return courses[position];
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
		
	public List<Course> getSelectedCourses(List<Integer> positions) {
		List<Course> selected = new ArrayList<Course>(positions.size());
		for (Integer pos : positions) 
			selected.add(courses[pos]);
		return selected;
	}
	
	public int[] getSelectedCourseIDs(List<Integer> positions) {
		int[] selected = new int[positions.size()];
		int index = 0;
		for (Integer pos : positions)
			selected[index++] = courses[pos].getCourseId();
		return selected;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.item_course_list, parent, false);		
		}
		
		// Get item
		Course course = getItem(position);
		// Get textviews to show course info
		TextView titleView = (TextView) row.findViewById(R.id.course_code_name);
		TextView detailView1 = (TextView) row.findViewById(R.id.course_description_1);
		TextView detailView2 = (TextView) row.findViewById(R.id.course_description_2);
		CheckBox checkbox = (CheckBox) row.findViewById(R.id.course_checkbox);
		
		// Populate course info on textviews
		String title = getTitle(course);
		String detail1 = getDetail1(course);
		String detail2 = getDetail2(course);
		titleView.setText(title);
		detailView1.setText(detail1);
		detailView2.setText(detail2);
		
		checkbox.setTag(Integer.valueOf(position));		// set tag so we can identify it
		checkbox.setOnCheckedChangeListener(checkboxListener);
		if (showCheckbox)
			checkbox.setVisibility(View.VISIBLE);
		else
			checkbox.setVisibility(View.INVISIBLE);
		
		return row;
	}
	
	private String getTitle(Course course) {
		String title = course.getCourseId() + " " + course.getCourseName();
		if (registeredCourseIDs != null && registeredCourseIDs.size() > 0) { 
			if (registeredCourseIDs.contains(course.getCourseId()))
				title += " (Registered)";
		}
		
		return title;
	}
	private String getDetail1(Course course) {
		StringBuilder sb = new StringBuilder();
		sb.append(course.getDepartmentName()).append(", ")
		  .append(course.getUnit()).append(" units, ")
		  .append(course.getCapacity()).append(" students");
		return sb.toString();
	}
	private String getDetail2(Course course) {
		return course.getInstructorName();
	}

	private class CheckboxOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			int position = (Integer)buttonView.getTag();
			
			listView.setItemChecked(position, isChecked);
		}
	}
}
