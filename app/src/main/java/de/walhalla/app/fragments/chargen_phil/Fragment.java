package de.walhalla.app.fragments.chargen_phil;

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
import de.walhalla.app.firebase.Firebase;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.interfaces.ChosenSemesterListener;
import de.walhalla.app.models.Person;
import de.walhalla.app.models.Semester;
import de.walhalla.app.utils.Border;
import de.walhalla.app.utils.ImageDownload;
import de.walhalla.app.utils.Variables;

@SuppressLint("InflateParams")
@SuppressWarnings("StaticFieldLeak")
public class Fragment extends CustomFragment implements ChosenSemesterListener {
    protected static final String TAG = "Chargen-Phil-Fragment";
    protected static final float scale = App.getContext().getResources().getDisplayMetrics().density;
    protected static StorageReference image;
    private final ArrayList<Person> people = new ArrayList<>();
    private ListenerRegistration registration;
    private Toolbar toolbar;
    private TextView name, PoB, address, mobile, major, mail, title;
    private ImageView picture;

    public Fragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        people.clear();
        loadData(App.getChosenSemester());
    }

    public void loadData(Semester semester) {
        if (registration != null) {
            registration.remove();
        }
        registration = Variables.Firebase.FIRESTORE
                .collection("Semester")
                .document(String.valueOf(semester.getID()))
                .collection("Chargen_Phil")
                .limit(5)
                .orderBy("id")
                .addSnapshotListener(((value, error) -> {
                    if (value != null && !value.isEmpty()) {
                        people.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            try {
                                Person p = document.toObject(Person.class);
                                p.setId(document.getId());
                                people.add(p);
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
            for (int i = 0; i < people.size(); i++) {
                Person p = people.get(i);
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
        mail = view.findViewById(R.id.item_charge_mail);
        picture = view.findViewById(R.id.item_charge_image);
        title = view.findViewById(R.id.item_charge_title);
        try {
            if (!number.getFirst_Name().isEmpty()) {
                switch (number.getId()) {
                    case "x":
                        title.setText(R.string.charge_x_phil);
                        mail.setText(Variables.Walhalla.MAIL_SENIOR_PHIL);
                        fillPerson(number);
                        break;
                    case "xx":
                        title.setText(R.string.charge_vx_phil);
                        mail.setText(Variables.Walhalla.MAIL_CONSENIOR_PHIL);
                        fillPerson(number);
                        break;
                    case "xxx":
                        title.setText(R.string.charge_fm_phil);
                        mail.setText(Variables.Walhalla.MAIL_FUXMAJOR_PHIL);
                        fillPerson(number);
                        break;
                    case "HW":
                    case "hw":
                        title.setText(R.string.charge_phil_hw);
                        mail.setText(Variables.Walhalla.MAIL_WH_PHIL);
                        fillPerson(number);
                        break;
                    default:
                        title.setText(R.string.error_download);
                        break;
                }
            } else {
                setAllGone();
            }
        } catch (Exception e) {
            Log.d(TAG, "An error occurred at com.example.walhalla.fragments.chargen_phil.ui.Entry.", e);
            setAllGone();
        }

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        try {
            view.startAnimation(anim);
        } catch (Exception e) {
            Log.d(TAG, "An error occurred while animating an entry", e);
        }

        return view;
    }

    private void setAllGone() {
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

    private void fillPerson(Person person) {
        try {
            name.setText(person.getFullName());
            major.setText(person.getMajor());
            PoB.setVisibility(View.GONE);
            try {
                boolean test = !person.getMobile().isEmpty();
                mobile.setText(person.getMobile());
            } catch (Exception e) {
                mobile.setVisibility(View.GONE);
            }
            String addressStr;
            addressStr = person.getAddress().get(Person.ADDRESS_STREET).toString() + " " +
                    person.getAddress().get(Person.ADDRESS_NUMBER).toString() + "\n" +
                    person.getAddress().get(Person.ADDRESS_ZIP_CODE).toString() + " " +
                    person.getAddress().get(Person.ADDRESS_CITY).toString();
            address.setText(addressStr);
            picture.setBackground(Border.getBlack(R.color.colorPrimaryDark, 1, 1, 1, 1));

            //Download the profile picture if the person has one
            if (person.getPicture_path() != null) {
                final ImageView pictureFinal = picture;
                new Thread(new ImageDownload(pictureFinal::setImageBitmap, person.getPicture_path(), true)).start();
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

        return view;
    }

    private void toolbarContent() {
        toolbar.setTitle(R.string.menu_chargen_phil);
    }

    @Override
    public void changeSemesterDone() {
        people.clear();
        loadData(App.getChosenSemester());
    }

    @Override
    public void onAuthChange() {
        toolbarContent();
        loadData(App.getChosenSemester());
    }
}
