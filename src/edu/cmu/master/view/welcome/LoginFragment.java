package edu.cmu.master.view.welcome;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.cmu.master.R;

public class LoginFragment extends Fragment {

	public static final String ARG_PAGE = "page";

	/**
	 * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
	 */
	private int mPageNumber;

	/**
	 * Factory method for this fragment class. Constructs a new fragment for the given page number.
	 */
	public static LoginFragment create(int pageNumber) {
		LoginFragment fragment = new LoginFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public LoginFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_login, container, false);
		return rootView;
	}

	/**
	 * Returns the page number represented by this fragment object.
	 */
	public int getPageNumber() {
		return mPageNumber;
	}

	

}
