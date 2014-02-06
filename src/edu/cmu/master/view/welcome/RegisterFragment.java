package edu.cmu.master.view.welcome;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import edu.cmu.master.R;

public class RegisterFragment extends Fragment {

	public static final String ARG_PAGE = "page";

	public static final String IMAGE_BYTE_ARRAY = "image";

	private static int SELECT_PICTURE = 1;

	private ViewGroup rootView;

	/**
	 * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
	 */
	private int mPageNumber;

	private Bitmap image;

	/**
	 * Factory method for this fragment class. Constructs a new fragment for the given page number.
	 */
	public static RegisterFragment create(int pageNumber, Bitmap image) {
		RegisterFragment fragment = new RegisterFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);

		byte[] byteArray = null;
		if (image != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byteArray = stream.toByteArray();
		}
		args.putByteArray(IMAGE_BYTE_ARRAY, byteArray);
		fragment.setArguments(args);

		return fragment;
	}

	public RegisterFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
		Log.i("position", "in fragment oncreate");
		byte[] byteArray = getArguments().getByteArray(IMAGE_BYTE_ARRAY);
		if (byteArray != null && byteArray.length > 0) {
			image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		}
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.i("position", "in fragment oncreateview land");
			rootView = (ViewGroup) inflater.inflate(R.layout.activity_register_land, container, false);
		} else {
			Log.i("position", "in fragment oncreateview port");
			rootView = (ViewGroup) inflater.inflate(R.layout.activity_register_port, container, false);
		}

		Context context = getActivity();

		Spinner spinner = (Spinner) rootView.findViewById(R.id.register_department);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.text_department, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		Spinner spinner1 = (Spinner) rootView.findViewById(R.id.register_major);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(context, R.array.text_major, android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(adapter1);

		Spinner spinner2 = (Spinner) rootView.findViewById(R.id.register_degree);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(context, R.array.text_degree, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);

		Spinner spinner3 = (Spinner) rootView.findViewById(R.id.semester_spinner);
		ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(context, R.array.text_semester, android.R.layout.simple_spinner_item);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner3.setAdapter(adapter3);

		ImageView iv = (ImageView) rootView.findViewById(R.id.register_avatar);
		if (image != null) {
			iv.setImageBitmap(image);
		}

		iv.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent choosePhotointent = new Intent(Intent.ACTION_GET_CONTENT);
				choosePhotointent.setType("image/*");
				Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Intent chooserIntent = Intent.createChooser(choosePhotointent, "Select Picture or take a new one");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePhotoIntent });
				startActivityForResult(chooserIntent, SELECT_PICTURE);
			}
		});

		Button cancel = (Button) rootView.findViewById(R.id.register_no_btn);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				RegisterFragment.this.getActivity().finish();
			}
		});
		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Bitmap yourSelectedImage = null;
				if (imageReturnedIntent.getAction() != null) {
					// get picture from camera
					Bundle extras = imageReturnedIntent.getExtras();
					yourSelectedImage = (Bitmap) extras.get("data");
				} else {
					// get picture from gallery
					Uri imageUri = imageReturnedIntent.getData();
					try {
						yourSelectedImage = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), imageUri);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (yourSelectedImage != null) {

					image = yourSelectedImage;
					ImageView iv = (ImageView) rootView.findViewById(R.id.register_avatar);
					iv.setImageBitmap(yourSelectedImage);
				}
			}
		}
	}

	/**
	 * Returns the page number represented by this fragment object.
	 */
	public int getPageNumber() {
		return mPageNumber;
	}
}
