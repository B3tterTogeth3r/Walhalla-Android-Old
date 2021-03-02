package de.walhalla.app.fragments.profile;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.widget.EditText;
import android.widget.Toast;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.models.Person;

public class AfterTextChange implements android.text.TextWatcher {
    EditText et;
    Person p;

    public AfterTextChange(EditText editText, Person person) {
        this.et = editText;
        this.p = person;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void afterTextChanged(Editable s) {
        switch (et.getId()) {
            case R.id.profile_firstName:
                p.setFirst_Name(s.toString());
                break;
            case R.id.profile_lastName:
                p.setLast_Name(s.toString());
                break;
            case R.id.profile_address:
                //TODO p.setAddress(s.toString());
                break;
            case R.id.profile_address_2:
                //TODO p.setAddress_2(s.toString());
                break;
            case R.id.profile_mobile:
                if (validPhone(s.toString()))
                    p.setMobile(s.toString());
                else {
                    Toast.makeText(App.getContext(), R.string.error_profile_mobile, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.profile_mail:
                if (!isEmailValid(s.toString())) {
                    Toast.makeText(App.getContext(), R.string.error_profile_email, Toast.LENGTH_SHORT).show();
                } else {
                    p.setMail(s.toString());
                }
                break;
            default:
                break;
        }
    }

    private boolean validPhone(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
