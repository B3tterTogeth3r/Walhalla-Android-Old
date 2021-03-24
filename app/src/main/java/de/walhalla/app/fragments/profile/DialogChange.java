package de.walhalla.app.fragments.profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import de.walhalla.app.R;
import de.walhalla.app.models.Person;

@SuppressLint("StaticFieldLeak")
public class DialogChange extends Dialog implements View.OnClickListener {
    private static final String TAG = "DialogChange";
    private final String kind;
    private Person saveData;
    private Map<String, Object> saveAddress;
    private ViewStub stub;

    public DialogChange(@NonNull Context context, String kind) {
        super(context);
        this.kind = kind;
        try {
            saveData = (Person) Fragment.userData.clone();
            saveAddress = Fragment.userData.getAddress();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public static void display(String kind) {
        try {
            new DialogChange(Fragment.context, kind).show();
        } catch (Exception e) {
            Log.d(TAG, "Building the dialog did not work", e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_profile_change);

        Toolbar toolbar = findViewById(R.id.profile_edit_close);
        setTitle(R.string.edit);
        toolbar.setTitle(R.string.edit);
        toolbar.setNavigationOnClickListener(this);
        toolbar.inflateMenu(R.menu.send_only);
        toolbar.setOnMenuItemClickListener(item -> {
            dismiss();
            return true;
        });
        setCanceledOnTouchOutside(false);

        stub = findViewById(R.id.layout_stub);
        switch (kind) {
            case "name":
                name();
                break;
            case "pob":
                pob();
                break;
            case "major":
                major();
                break;
            case "password":
                password();
                break;
            case "mail":
                mail();
                break;
            case "address":
                address();
                break;
            case "mobile":
                mobile();
                break;
            default:
                Log.d(TAG, "Dialog got called unexpected with \"" + kind + "\"");
                this.dismiss();
                break;
        }
    }

    private void address() {
        stub.setLayoutResource(R.layout.change_address);
        stub.inflate();
        EditText street, number, zip, city;
        street = findViewById(R.id.profile_address_street);
        number = findViewById(R.id.profile_address_number);
        zip = findViewById(R.id.profile_address_zip);
        city = findViewById(R.id.profile_address_city);
        Map<String, Object> address = Fragment.userData.getAddress();
        street.setText(address.get(Person.ADDRESS_STREET).toString());
        number.setText(address.get(Person.ADDRESS_NUMBER).toString());
        zip.setText(address.get(Person.ADDRESS_ZIP_CODE).toString());
        city.setText(address.get(Person.ADDRESS_CITY).toString());
        street.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                address.put(Person.ADDRESS_STREET, s.toString());
                Fragment.userData.setAddress(address);
                Fragment.addressTV.setText(Fragment.userData.getAddressString());
            }
        });
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                address.put(Person.ADDRESS_NUMBER, s.toString());
                Fragment.userData.setAddress(address);
                Fragment.addressTV.setText(Fragment.userData.getAddressString());
            }
        });
        zip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                address.put(Person.ADDRESS_ZIP_CODE, s.toString());
                Fragment.userData.setAddress(address);
                Fragment.addressTV.setText(Fragment.userData.getAddressString());
            }
        });
        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                address.put(Person.ADDRESS_CITY, s.toString());
                Fragment.userData.setAddress(address);
                Fragment.addressTV.setText(Fragment.userData.getAddressString());
            }
        });
    }

    private void mail() {
        //TODO save mail directly to FirebaseAuth after user clicked on save
        // also save it to the profile directly
        stub.setLayoutResource(R.layout.change_mail);
        stub.inflate();
    }

    private void password() {
        //TODO Save pw to directly FirebaseAuth after user clicked on save
        stub.setLayoutResource(R.layout.change_password);
        stub.inflate();
    }

    private void major() {
        stub.setLayoutResource(R.layout.change_major);
        stub.inflate();
        EditText major = findViewById(R.id.profile_major);
        major.setText(Fragment.userData.getMajor());
        major.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Fragment.userData.setMajor(s.toString());
                Fragment.majorTV.setText(s.toString());
            }
        });
    }

    private void pob() {
        stub.setLayoutResource(R.layout.change_pob);
        stub.inflate();
        EditText pob = findViewById(R.id.profile_pob);
        pob.setText(Fragment.userData.getPoB());
        pob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Fragment.userData.setPoB(s.toString());
                Fragment.pobTV.setText(s.toString());
            }
        });
    }

    private void mobile() {
        stub.setLayoutResource(R.layout.change_mobile);
        stub.inflate();
        EditText mobile = findViewById(R.id.profile_mobile);
        mobile.setText(Fragment.userData.getMobile());
        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 5 && Patterns.PHONE.matcher(s.toString()).matches()) {
                    Fragment.userData.setMobile(s.toString());
                    Fragment.mobileTV.setText(s.toString());
                }
            }
        });
    }

    /**
     * Add the view for changing the name and fill it with the current name.
     * On change of a value, the new one is immediately saved.
     */
    private void name() {
        stub.setLayoutResource(R.layout.change_name);
        stub.inflate();
        EditText first_Name = findViewById(R.id.profile_firstName);
        EditText last_Name = findViewById(R.id.profile_lastName);
        first_Name.setText(Fragment.userData.getFirst_Name());
        last_Name.setText(Fragment.userData.getLast_Name());

        first_Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Fragment.userData.setFirst_Name(s.toString());
                Fragment.nameTV.setText(Fragment.userData.getFullName());
            }
        });

        last_Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Fragment.userData.setLast_Name(s.toString());
                Fragment.nameTV.setText(Fragment.userData.getFullName());
            }
        });
    }

    @Override
    public void onClick(@NotNull View v) {
        if (v.getId() == -1) {
            //TODO if user did not hit save, the address does not get set to the previous state.
            Fragment.userData.setAddress(saveAddress);
            Fragment.userData = saveData;
            Fragment.updateUI();
            this.dismiss();
        }
    }

    private void upload() {
        Log.i(TAG, "uploading changed data");
        this.dismiss();
    }
}
