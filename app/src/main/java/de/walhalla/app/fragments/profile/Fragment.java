package de.walhalla.app.fragments.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;
import de.walhalla.app.MainActivity;
import de.walhalla.app.R;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.models.Person;
import de.walhalla.app.utils.ImageDownload;
import de.walhalla.app.utils.Variables;

@SuppressLint("StaticFieldLeak")
public class Fragment extends CustomFragment {
    private final static String TAG = "ProfileFragment";
    public static TextView nameTV, pobTV, joinedTV, dobTV, majorTV, passwordTV, mailTV, mobileTV, addressTV, rankTV, uid;
    public static CircleImageView imageView;
    public static Person userData;
    public static Context context;
    public static FragmentActivity activity;
    public static View view;
    private String uidStr, docId;
    private RelativeLayout nameLayout, dobLayout, pobLayout, majorLayout, passwordLayout, mailLayout, mobileLayout, addressLayout, rankLayout, joinedLayout, pictureLayout;

    public Fragment(@Nullable String uid) {
        if (uid == null) {
            if (Variables.Firebase.AUTHENTICATION.getCurrentUser() != null) {
                this.uidStr = Variables.Firebase.AUTHENTICATION.getUid();
            } else {
                //TODO Display error message, sign user out and go back to the home screen.
            }
        } else {
            this.uidStr = uid;
        }
    }

    /**
     * Fills all the TextViews inside the RelativeLayouts
     * with the data of the user account. If a field has
     * not been filled, it will stay empty. Because of
     * the NoSQL database every single field has to get caught .
     */
    public static void updateUI() {
        try {
            nameTV.setText(userData.getFullName());
        } catch (Exception e) {
            Log.d(TAG, "An error while filling nameTV", e);
        }
        try {
            dobTV.setText(userData.getDoBString());
        } catch (Exception e) {
            Log.d(TAG, "An error while filling dobTV", e);
        }
        try {
            pobTV.setText(userData.getPoB());
        } catch (Exception e) {
            Log.d(TAG, "An error while filling pobTV", e);
        }
        try {
            majorTV.setText(userData.getMajor());
        } catch (Exception e) {
            Log.d(TAG, "An error while filling majorTV", e);
        }
        //TODO think of a clever way :)  passwordTV.setText();
        try {
            mailTV.setText(userData.getMail());
        } catch (Exception e) {
            Log.d(TAG, "An error while filling mailTV", e);
        }
        try {
            mobileTV.setText(userData.getMobile());
        } catch (Exception e) {
            Log.d(TAG, "An error while filling mobileTV", e);
        }
        try {
            addressTV.setText(userData.getAddressString());
        } catch (Exception e) {
            Log.d(TAG, "An error while filling addressTV", e);
        }
        try {
            rankTV.setText(userData.getRank());
        } catch (Exception e) {
            Log.e(TAG, "An error while filling rankTV", e);
        }
        try {
            joinedTV.setText(Variables.SEMESTER_ARRAY_LIST.get(userData.getJoined() - 1).getLong());
        } catch (Exception e) {
            Log.e(TAG, "An error while filling joinedTV", e);
        }
        try {
            new Thread(new ImageDownload(imageBitmap -> imageView.setImageBitmap(imageBitmap), userData.getPicture_path())).start();
        } catch (Exception e) {
            Log.e(TAG, "updateUI: Image download error", e);
        }
        try {
            uid.setText(userData.getUid());
        } catch (Exception e) {
            Log.e(TAG, "An error while filling uid", e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //get user data
        Log.i(TAG, uidStr);
        Variables.Firebase.FIRESTORE
                .collection("Person")
                .whereEqualTo("uid", uidStr)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            for (DocumentSnapshot doc : snapshot) {
                                userData = doc.toObject(Person.class);
                                docId = doc.getId();
                                updateUI();
                            }
                        }
                    }
                });
    }

    /**
     * On leaving this site the changed userdata is getting uploaded.
     */
    @Override
    public void onStop() {
        super.onStop();
        //upload the data and show the user a message
        if (userData != null) {
            Variables.Firebase.FIRESTORE
                    .collection("Person")
                    .document(docId)
                    .set(userData)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "upload of new userdata successful"));

            Snackbar.make(MainActivity.parentLayout, R.string.upload_complete, Snackbar.LENGTH_SHORT)
                    .show();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_display, container, false);
        ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setTitle(R.string.menu_profile);
        ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setSubtitle("");
        context = getContext();
        activity = getActivity();

        //Find all the rows
        nameLayout = view.findViewById(R.id.profile_general_information_name);
        dobLayout = view.findViewById(R.id.profile_general_information_DoB);
        pobLayout = view.findViewById(R.id.profile_general_information_PoB);
        majorLayout = view.findViewById(R.id.profile_general_information_major);
        passwordLayout = view.findViewById(R.id.profile_general_information_password);
        mailLayout = view.findViewById(R.id.profile_contact_information_mail);
        mobileLayout = view.findViewById(R.id.profile_contact_information_mobile);
        addressLayout = view.findViewById(R.id.profile_contact_information_address);
        rankLayout = view.findViewById(R.id.profile_fraternity_information_rank);
        joinedLayout = view.findViewById(R.id.profile_fraternity_information_joined);
        pictureLayout = view.findViewById(R.id.profile_fraternity_information_picture);

        setOnClick();

        //Find all the fields
        nameTV = view.findViewById(R.id.profile_general_information_name_user);
        dobTV = view.findViewById(R.id.profile_general_information_DoB_user);
        pobTV = view.findViewById(R.id.profile_general_information_PoB_user);
        majorTV = view.findViewById(R.id.profile_general_information_major_user);
        passwordTV = view.findViewById(R.id.profile_general_information_password_user);
        mailTV = view.findViewById(R.id.profile_contact_information_mail_user);
        mobileTV = view.findViewById(R.id.profile_contact_information_mobile_user);
        addressTV = view.findViewById(R.id.profile_contact_information_address_user);
        rankTV = view.findViewById(R.id.profile_fraternity_information_rank_user);
        joinedTV = view.findViewById(R.id.profile_fraternity_information_joined_user);
        imageView = view.findViewById(R.id.profile_fraternity_information_picture_image);
        uid = view.findViewById(R.id.profile_uid);

        clearAllTV();

        return view;
    }

    /**
     * Set the content of all text views for user content
     * empty, so the user does not see the dummy text
     */
    private void clearAllTV() {
        nameTV.setText("");
        dobTV.setText("");
        pobTV.setText("");
        majorTV.setText("");
        passwordTV.setText("");
        mailTV.setText("");
        mobileTV.setText("");
        addressTV.setText("");
        rankTV.setText("");
        joinedTV.setText("");
        uid.setText("");
    }

    /**
     * If <b>no</b> user is logged in, don't show any data and remove the click listener<br>
     * If <u>a</u> user is signed in, the listeners get set again.
     */
    @Override
    public void onAuthChange() {
        if (Variables.Firebase.AUTHENTICATION.getCurrentUser() == null) {
            clearAllTV();
            nameLayout.setOnClickListener(null);
            dobLayout.setOnClickListener(null);
            pobLayout.setOnClickListener(null);
            majorLayout.setOnClickListener(null);
            passwordLayout.setOnClickListener(null);
            mailLayout.setOnClickListener(null);
            mobileLayout.setOnClickListener(null);
            addressLayout.setOnClickListener(null);
            rankLayout.setOnClickListener(null);
            joinedLayout.setOnClickListener(null);
            pictureLayout.setOnClickListener(null);
        } else {
            setOnClick();
        }
    }

    /**
     * Makes all the RelativeLayouts clickable in OnClick()
     */
    private void setOnClick() {
        nameLayout.setOnClickListener(new OnClick(getParentFragmentManager()));
        dobLayout.setOnClickListener(new OnClick(getParentFragmentManager()));
        pobLayout.setOnClickListener(new OnClick(getParentFragmentManager()));
        majorLayout.setOnClickListener(new OnClick(getParentFragmentManager()));
        passwordLayout.setOnClickListener(new OnClick(getParentFragmentManager()));
        mailLayout.setOnClickListener(new OnClick(getParentFragmentManager()));
        mobileLayout.setOnClickListener(new OnClick(getParentFragmentManager()));
        addressLayout.setOnClickListener(new OnClick(getParentFragmentManager()));
        rankLayout.setOnClickListener(new OnClick(getParentFragmentManager()));
        joinedLayout.setOnClickListener(new OnClick(getParentFragmentManager()));
        pictureLayout.setOnClickListener(new OnClick(getParentFragmentManager()));
    }
}