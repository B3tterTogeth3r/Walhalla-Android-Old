package de.walhalla.app.fragments.chargen.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.firebase.Firebase;
import de.walhalla.app.models.Person;
import de.walhalla.app.utils.Border;
import de.walhalla.app.utils.Variables;

public class Entry extends ArrayAdapter<Person> {
    protected static final float scale = App.getContext().getResources().getDisplayMetrics().density;
    private static final String TAG = "ChargenEntry";
    private final Context context;
    private final ArrayList<Person> chargenList;
    private TextView name, PoB, address, mobile, major;
    private ImageView picture;

    public Entry(Context context, ArrayList<Person> chargenList) {
        super(context, R.layout.item_person, chargenList);
        this.chargenList = chargenList;
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressLint("ViewHolder")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.item_person, parent, false);

        Person number = chargenList.get(position);
        name = view.findViewById(R.id.item_charge_name);
        major = view.findViewById(R.id.item_charge_major);
        PoB = view.findViewById(R.id.item_charge_birth_place);
        address = view.findViewById(R.id.item_charge_address);
        mobile = view.findViewById(R.id.item_charge_mobile);
        TextView mail = view.findViewById(R.id.item_charge_mail);
        picture = view.findViewById(R.id.item_charge_image);
        TextView title = view.findViewById(R.id.item_charge_title);

        if (!number.getFirst_Name().equals("")) {
            switch (position) {
                case 0:
                    title.setText(R.string.charge_x);
                    mail.setText(Variables.Walhalla.MAIL_SENIOR);
                    fillPerson(number);
                    break;
                case 1:
                    title.setText(R.string.charge_vx);
                    mail.setText(Variables.Walhalla.MAIL_CONSENIOR);
                    fillPerson(number);
                    break;
                case 2:
                    title.setText(R.string.charge_fm);
                    mail.setText(Variables.Walhalla.MAIL_FUXMAJOR);
                    fillPerson(number);
                    break;
                case 3:
                    title.setText(R.string.charge_xx);
                    mail.setText(Variables.Walhalla.MAIL_SCHRIFTFUEHRER);
                    fillPerson(number);
                    break;
                case 4:
                    title.setText(R.string.charge_xxx);
                    mail.setText(Variables.Walhalla.MAIL_KASSIER);
                    fillPerson(number);
                    break;
                default:
                    title.setText(R.string.error_download);
                    break;
            }
        } else {
            //Set everything Visibility.GONE
            title.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            major.setVisibility(View.GONE);
            PoB.setVisibility(View.GONE);
            address.setVisibility(View.GONE);
            mobile.setVisibility(View.GONE);
            mail.setVisibility(View.GONE);
            picture.setVisibility(View.GONE);
        }

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        try {
            view.startAnimation(anim);
        } catch (Exception e) {
            Log.d(TAG, "An error occurred while animating an entry", e);
        }

        return view;
    }

    private void fillPerson(Person person) {
        try {
            name.setText(person.getFullName());
            String majorStr = "stud. " + person.getMajor();
            major.setText(majorStr);
            String PoBStr = getContext().getString(R.string.charge_from) + " " + person.getPoB();
            PoB.setText(PoBStr);
            String addressStr;
            addressStr = person.getAddress().get(Person.ADDRESS_STREET).toString() + " " +
                    person.getAddress().get(Person.ADDRESS_NUMBER).toString() + "\n" +
                    person.getAddress().get(Person.ADDRESS_ZIP_CODE).toString() + " " +
                    person.getAddress().get(Person.ADDRESS_CITY).toString();
            address.setText(addressStr);
            mobile.setText(person.getMobile());
            picture.setBackground(Border.getBlack(R.color.colorPrimaryDark, 1, 1, 1, 1));

            //Download the profile picture if the person has one
            if (person.getPicture_path() != null) {
                synchronized (new Object()) {
                    Firebase.downloadImage(person.getPicture_path(), picture).run();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "No person filled that position", e);
        }
    }
}
