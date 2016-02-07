package com.harrykristi.hangapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.harrykristi.hangapp.Adapters.ProfileDialogAdapter;
import com.harrykristi.hangapp.Adapters.ProfileListAdapter;
import com.harrykristi.hangapp.Interfaces.AuthenticatedActivityCallbacks;
import com.harrykristi.hangapp.Models.UserProfileResponse;
import com.harrykristi.hangapp.Models.VenueHangApp;
import com.harrykristi.hangapp.events.DataLoadedPreviousMatchesEvent;
import com.harrykristi.hangapp.events.DataLoadedUserEvent;
import com.harrykristi.hangapp.events.GeneralInfoEvent;
import com.harrykristi.hangapp.events.GetUserPictureEvent;
import com.harrykristi.hangapp.events.LoadPreviousMatchesEvent;
import com.harrykristi.hangapp.helpers.BusProvider;
import com.harrykristi.hangapp.Models.User;
import com.harrykristi.hangapp.helpers.Config;
import com.harrykristi.hangapp.helpers.UploadFileToServer;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.parse.ParseUser;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 200;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 2;


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;


    private OnFragmentInteractionListener mListener;

    private ListView mListView;
    private ProgressBar mProgressBar;
    private TextView mTotalCheckins;
    private TextView mTotalMatches;
    private CircleImageView mProfilePicture;
    private FrameLayout mProfileRootView;

    private ProfileListAdapter mAdapter;

    private List<User> mDatasetUser;
    private List<VenueHangApp> mDatasetVenue;

    private Uri fileUri;

    private Bus mBus;
    private int mAnimationDuration;

    private AuthenticatedActivityCallbacks callbacks;
    private Activity mActivity;

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mDatasetUser = null;
        mDatasetVenue = null;

        getBus().register(this);
        getBus().post(new LoadPreviousMatchesEvent(ParseUser.getCurrentUser().getObjectId(), null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.profile_progress_spinner);

        mAdapter = new ProfileListAdapter(getContext(), mDatasetUser, mDatasetVenue);

        mListView = (ListView) view.findViewById(R.id.previous_check_ins_list);
        mListView.setAdapter(mAdapter);

        mTotalCheckins = (TextView) view.findViewById(R.id.total_checkins);
        mTotalCheckins.setText("...");

        mTotalMatches = (TextView) view.findViewById(R.id.total_matches);
        mTotalMatches.setText("...");

        mProfilePicture = (CircleImageView) view.findViewById(R.id.profile_picture);
        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraWithPermissionCheck();
            }
        });

        SharedPreferences myPrefs = mActivity.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String url = myPrefs.getString("profile_picture_url","");
        if(url.equals("")){
            getBus().post(new GetUserPictureEvent(ParseUser.getCurrentUser().getObjectId()));
        } else {
            Picasso.with(getContext()).load(url).placeholder(R.drawable.default_profile_icon).noFade().into(mProfilePicture);
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onProfileFragmentInteraction(uri);
        }
    }

    @Subscribe
    public void onDataLoaded(DataLoadedPreviousMatchesEvent event) {
        mTotalCheckins.setText(Integer.toString(event.getResponse().getTotal_checkins()));
        mTotalMatches.setText(Integer.toString(event.getResponse().getTotal_matches()));

        mDatasetUser = event.getResponse().getMatch_user();
        mDatasetVenue = event.getResponse().getMatch_venue();
        mAdapter.updateDataset(mDatasetUser, mDatasetVenue);
        animateResultsScreen();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        try {
            callbacks = (AuthenticatedActivityCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AuthenticatedActivityCallbacks");
        }

        mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onProfileFragmentInteraction(Uri uri);
    }

    private Bus getBus() {
        if (mBus == null) {
            mBus = BusProvider.getInstance();
        }
        return mBus;
    }

    private void animateResultsScreen() {

        mListView.setVisibility(View.VISIBLE);
        mListView.setAlpha(0f);

        mListView.animate()
                .alpha(1f)
                .setDuration(mAnimationDuration)
                .setListener(null);

        mProgressBar.animate()
                .alpha(0f)
                .setDuration(mAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    protected void startCameraWithPermissionCheck() {
        int permissionCheckCamera = ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.CAMERA);
        int permissionCheckRead = ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheckWrite = ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheckCamera != PackageManager.PERMISSION_GRANTED
                || permissionCheckRead != PackageManager.PERMISSION_GRANTED
                || permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                    || shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            getImage();
        }
    }


    private void getImage() {
        DialogPlus dialog = DialogPlus.newDialog(getContext())
                .setAdapter(new ProfileDialogAdapter(getContext()))
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        switch (position) {
                            case 0:
                                launchCameraIntent();
                                dialog.dismiss();
                                break;
                            case 1:
                                launchGalleryIntent();
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create();
        dialog.show();
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_IMAGE_REQUEST_CODE);
    }

    private void launchCameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }


    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadProcess(true);
                Toast.makeText(getContext(), "Image captured successfully.", Toast.LENGTH_SHORT).show();


            } else if (resultCode == Activity.RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if(requestCode == GALLERY_IMAGE_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {

                fileUri = data.getData();
                launchUploadProcess(false);


            } else if (resultCode == Activity.RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getContext(),
                        "User cancelled image picking", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getContext(),
                        "Sorry! Failed to obtain image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void launchUploadProcess(boolean isCamera) {
        new UploadFileToServer(getContext(), fileUri, ParseUser.getCurrentUser().getObjectId(), !isCamera).execute();
        mProfilePicture.setAlpha(0.5f);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImage();
                } else {
                    Toast.makeText(mActivity, "Permissions denied :(", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Subscribe
    public void onImageUpload(final GeneralInfoEvent event) {
        if (!event.isError()) {
            Picasso.with(getContext()).load(fileUri).placeholder(R.drawable.default_profile_icon).noFade().into(mProfilePicture);
            mProfilePicture.setAlpha(1.0f);

            try {
                callbacks.OnImageUpdated(fileUri.toString());
                SharedPreferences myPrefs = mActivity.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.putString("profile_picture_url", fileUri.toString());
                editor.commit();

                View tmpView = getView();
                if (tmpView != null) {
                    final Snackbar snackbar = Snackbar.make(getView(), event.getMessage(), Snackbar.LENGTH_SHORT);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    });

                    snackbar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.limeGreen));
                    snackbar.show();
                }
            } catch (Exception e){
                callbacks.DisplaySnackBarWith("Image upload failed.");
            }
        } else {
            mProfilePicture.setAlpha(1.0f);

            final Snackbar snackbar = Snackbar.make(getView(), event.getMessage(), Snackbar.LENGTH_LONG);
            snackbar.setAction("RETRY", new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    launchUploadProcess(true);
                }
            });
        }
    }

    @Subscribe
    public void onDataLoaded(DataLoadedUserEvent event) {
        UserProfileResponse response = event.getResponse();
        int status = event.getStatus();
        if (response.isError()) {
            if (status != 404) {
                Toast.makeText(getContext(), "Something went wrong while getting the profile picture" +
                        response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            String url = response.getProfilePictureUrl();
            Picasso.with(getContext()).setIndicatorsEnabled(true);
            Picasso.with(getContext()).setLoggingEnabled(true);
            Picasso.with(getContext()).load(url).placeholder(R.drawable.default_profile_icon).noFade().into(mProfilePicture);
        }

    }

}
