package edu.cmu.master.view.welcome;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import edu.cmu.master.R;

public class ScreenSlidePageFragment extends Fragment {

	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";

	/**
	 * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
	 */
	private int mPageNumber;

	/**
	 * Factory method for this fragment class. Constructs a new fragment for the given page number.
	 */
	public static ScreenSlidePageFragment create(int pageNumber) {
		ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public ScreenSlidePageFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		View rootView = inflater.inflate(R.layout.fragment_welcome_slide, container, false);

		switch (mPageNumber) {
			case 0:
				((ImageView) rootView.findViewById(R.id.welcome_slide_picture)).setImageResource(R.drawable.main_page_1);
				break;
			case 1:
				((ImageView) rootView.findViewById(R.id.welcome_slide_picture)).setImageResource(R.drawable.register_courses_1);
				break;
			case 2:
				((ImageView) rootView.findViewById(R.id.welcome_slide_picture)).setImageResource(R.drawable.add_course_1);
				break;

		}

		// Set the title view to show the page number.
		// ((ImageView)
		// rootView.findViewById(R.id.welcome_slide_picture)).setImageResource(R.drawable.ic_action_new);
		return rootView;
	}

	/**
	 * Returns the page number represented by this fragment object.
	 */
	public int getPageNumber() {
		return mPageNumber;
	}
}
