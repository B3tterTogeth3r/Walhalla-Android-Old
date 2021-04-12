package de.walhalla.app.fragments.addNew;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.firebase.Firebase;
import de.walhalla.app.fragments.profile.DialogChange;
import de.walhalla.app.models.Person;
import de.walhalla.app.utils.Variables;

@SuppressLint("InflateParams")
public class CheckChargeDialog extends AlertDialog.Builder implements View.OnClickListener,
        DialogInterface.OnClickListener, DialogChange.CustomDismissListener {
    private final static String TAG = "CheckChargenDialog";
    public static DialogChange.CustomDismissListener customDismissListener;
    public final TextView nameTV, pobTV, majorTV, mobileTV, addressTV;
    private final RelativeLayout name, pob, major, mobile, address, image;
    private final String kind;
    private final View view;
    public CircleImageView imageIV;
    private Person person;
    private boolean newPersonToUpload = false;

    public CheckChargeDialog(Context context, @NotNull String kind, @Nullable Person person) {
        super(context);
        this.person = person;
        this.kind = kind;
        customDismissListener = this;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_charge, null);
        setView(view);
        setTitle(R.string.check_data);
        name = view.findViewById(R.id.item_charge_name);
        pob = view.findViewById(R.id.item_charge_pob);
        major = view.findViewById(R.id.item_charge_major);
        mobile = view.findViewById(R.id.item_charge_mobile);
        address = view.findViewById(R.id.item_charge_address);
        image = view.findViewById(R.id.item_charge_picture);

        name.setOnClickListener(this);
        pob.setOnClickListener(this);
        major.setOnClickListener(this);
        mobile.setOnClickListener(this);
        address.setOnClickListener(this);
        image.setOnClickListener(this);

        nameTV = view.findViewById(R.id.item_charge_name_content);
        pobTV = view.findViewById(R.id.item_charge_pob_content);
        majorTV = view.findViewById(R.id.item_charge_major_content);
        mobileTV = view.findViewById(R.id.item_charge_mobile_content);
        addressTV = view.findViewById(R.id.item_charge_address_content);
        imageIV = view.findViewById(R.id.item_charge_picture_content);
        if (this.person != null) {
            fillUi(this.person);
            Log.d(TAG, "Person != null with person named: " + this.person.getFullName());
        } else {
            this.person = new Person();
            this.person.setRank(ChargenDialog.kind);
            //Upload the new person to CloudFireStore
            newPersonToUpload = true;
        }
        if (this.kind.contains("h")) {
            pob.setVisibility(View.GONE);
        }
        setPositiveButton(R.string.yes, this);
        setCancelable(true);
    }

    @Override
    public void onClick(View v) {
        if (v == name) {
            DialogChange.display(getContext(), "name", this.person);
            Log.d(TAG, "name");
        } else if (v == pob) {
            DialogChange.display(getContext(), "pob", this.person);
            Log.d(TAG, "pob");
        } else if (v == major) {
            DialogChange.display(getContext(), "major", this.person);
            Log.d(TAG, "major");
        } else if (v == mobile) {
            DialogChange.display(getContext(), "mobile", this.person);
            Log.d(TAG, "mobile");
        } else if (v == address) {
            DialogChange.display(getContext(), "address", this.person);
            Log.d(TAG, "address");
        } else if (v == image) {
            Snackbar.make(view, R.string.toast_still_in_dev, Snackbar.LENGTH_LONG).show();
            Log.d(TAG, "image");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //Upload the new person to CloudFireStore
        if (newPersonToUpload) {
            if (person.getFullName() != null &&
                    person.getAddress() != null &&
                    person.getMajor() != null) {
                Variables.Firebase.FIRESTORE
                        .collection("Person")
                        .add(person)
                        .addOnSuccessListener(documentReference -> {
                            Log.d(TAG, "Upload of new person successful");
                            Toast.makeText(App.getContext(), R.string.upload_complete, Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Upload of new person unsuccessful", e));
            } else {
                Toast.makeText(App.getContext(), R.string.error_upload, Toast.LENGTH_LONG).show();
            }
        }
        ChargenDialog.customDismissListener.dismissChecker(kind, person);
    }

    @Override
    public void chargenDismiss(@NotNull Person person) {
        this.person = person;
        fillUi(this.person);
    }

    private void fillUi(@NotNull Person person) {
        try {
            nameTV.setText(person.getFullName());
        } catch (Exception ignored) {
        }
        try {
            pobTV.setText(person.getPoB());
        } catch (Exception ignored) {
        }
        try {
            majorTV.setText(person.getMajor());
        } catch (Exception ignored) {
        }
        try {
            mobileTV.setText(person.getMobile());
        } catch (Exception ignored) {
        }
        try {
            addressTV.setText(person.getAddressString());
        } catch (Exception ignored) {
        }
        try {
            Firebase.downloadImage(person.getPicture_path(), imageIV).run();
        } catch (Exception ignored) {
        }
    }

    public interface CustomDismissListener {
        void dismissChecker(@NotNull String kind, @Nullable Person person);
    }
}
