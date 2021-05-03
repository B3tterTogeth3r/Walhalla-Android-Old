package de.walhalla.app.fragments.chargen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.dialog.ChangeSemesterDialog;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.interfaces.ChosenSemesterListener;
import de.walhalla.app.models.Person;
import de.walhalla.app.models.Semester;
import de.walhalla.app.utils.Border;
import de.walhalla.app.utils.ImageDownload;
import de.walhalla.app.utils.Variables;

@SuppressLint("InflateParams")
@SuppressWarnings({"StaticFieldLeak"})
public class Fragment extends CustomFragment implements ChosenSemesterListener {
    protected static final String TAG = "ChargenFragment";
    protected static final float scale = App.getContext().getResources().getDisplayMetrics().density;
    protected static StorageReference image;
    private final ArrayList<Person> numbers = new ArrayList<>();
    protected Toolbar toolbar;
    private ListenerRegistration registration;
    private TextView name;
    private TextView PoB;
    private TextView address;
    private TextView mobile;
    private TextView major;
    private ImageView picture;
    private ChosenSemesterListener listener;

    public Fragment() {
        numbers.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData(App.getChosenSemester());
    }

    public void loadData(Semester semester) {
        if (registration != null) {
            registration.remove();
        }
        registration = Variables.Firebase.FIRESTORE
                .collection("Semester")
                .document(String.valueOf(semester.getID()))
                .collection("Chargen")
                .limit(5)
                .addSnapshotListener(((value, error) -> {
                    if (value != null && !value.isEmpty()) {
                        numbers.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            try {
                                Person p = document.toObject(Person.class);
                                p.setId(document.getId());
                                numbers.add(p);
                            } catch (Exception e) {
                                Log.d(TAG, "Something went wrong while fetching the person data.", e);
                            }
                        }
                        formatData();
                    } else {
                        Log.d(TAG, "Something went wrong while fetching the student board.", error);
                    }
                }));
    }

    private void formatData() {
        try {
            LinearLayout linearLayout = requireView().findViewById(R.id.fragment_container);
            ScrollView sc = new ScrollView(getContext());
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            for (int i = 0; i < numbers.size(); i++) {
                Person p = numbers.get(i);
                layout.addView(onePerson(p, i));
            }
            sc.addView(layout);
            linearLayout.addView(sc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private View onePerson(@NotNull Person number, int position) {
        View view = getLayoutInflater().inflate(R.layout.item_person, null);

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

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
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
                Log.d(TAG, "fillPerson: " + person.getPicture_path());
                Log.d(TAG, "fillPerson: " + picture.getId());
                final ImageView pictureFinal = picture;
                new Thread(new ImageDownload(pictureFinal::setImageBitmap, person.getPicture_path())).start();
            }
        } catch (Exception e) {
            Log.d(TAG, "No person filled that position", e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            registration.remove();
            toolbar.findViewById(R.id.custom_title).setVisibility(View.GONE);
            toolbar.findViewById(R.id.custom_title).setOnClickListener(null);
        } catch (Exception e) {
            Log.d(TAG, "Something went wrong while removing the snapshot listener", e);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        try {
            toolbar = requireActivity().findViewById(R.id.toolbar);
            toolbarContent();
        } catch (Exception e) {
            Log.d(TAG, "Something went wrong while creating the fragment.", e);
        }
        listener = this;

        return view;
    }

    private void toolbarContent() {
        LinearLayout subtitle = toolbar.findViewById(R.id.custom_title);
        subtitle.setVisibility(View.VISIBLE);
        subtitle.setOnClickListener(v -> Log.d(TAG, "toolbar got clicked"));
        TextView title = subtitle.findViewById(R.id.action_bar_title);
        title.setText(String.format("%s %s", getString(R.string.charge_aktive), App.getChosenSemester().getShort()));
        toolbar.setOnClickListener(v -> ChangeSemesterDialog.load(getChildFragmentManager(), listener));
    }

    @Override
    public void changeSemesterDone() {
        numbers.clear();
        loadData(App.getChosenSemester());
    }

    @Override
    public void onAuthChange() {
        toolbarContent();
        loadData(App.getChosenSemester());
    }
}
