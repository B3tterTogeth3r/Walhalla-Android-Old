package de.walhalla.app.fragments.addNew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import de.walhalla.app.MainActivity;
import de.walhalla.app.R;
import de.walhalla.app.dialog.PersonPickerDialog;
import de.walhalla.app.firebase.Firebase;
import de.walhalla.app.interfaces.AddNewSemesterListener;
import de.walhalla.app.models.Person;
import de.walhalla.app.utils.Variables;

public class ChargenDialog extends DialogFragment implements View.OnClickListener, PersonPickerDialog.CustomDismissListener, CheckChargeDialog.CustomDismissListener {
    public static final String[] kinds = new String[]{"Aktiver", "Alter Herr"};
    private static final String TAG = "ChargenDialog";
    public static PersonPickerDialog.CustomDismissListener listener;
    public static CheckChargeDialog.CustomDismissListener customDismissListener;
    public static String kind;
    protected static ArrayList<String> persons = new ArrayList<>();
    protected static ArrayList<Person> personsComplete = new ArrayList<>();
    private final AddNewSemesterListener addNewSemesterListener;
    private ArrayList<Object> chargen = new ArrayList<>();
    private Button senior, consenior, fuxmajor, scriptor, kassier, seniorAH, scriptorAH, kassierAH, hv1, hv2;
    private Toolbar toolbar;
    private TextView seniorTV, conseniorTV, fuxmajorTV, scriptorTV, kassierTV, seniorAHTV, scriptorAHTV, kassierAHTV, hv1TV, hv2TV;
    private CircleImageView seniorIV, conseniorIV, fuxmajorIV, scriptorIV, kassierIV, seniorAHIV, scriptorAHIV, kassierAHIV, hv1IV, hv2IV;

    public ChargenDialog(AddNewSemesterListener addNewSemesterListener, ArrayList<Object> chargenList) {
        this.addNewSemesterListener = addNewSemesterListener;
        if (chargenList != null && !chargenList.isEmpty()) {
            Log.d(TAG, "Chargen found");
            this.chargen = chargenList;
        } else {
            this.chargen.clear();
            for (int i = 0; i < 5; i++) {
                chargen.add(null);
            }
        }
    }

    public static void display(FragmentManager fragmentManager, @NotNull String kind, ArrayList<Object> chargenList, AddNewSemesterListener listener) {
        ChargenDialog.kind = kind;
        try {
            ChargenDialog dialog = new ChargenDialog(listener, chargenList);
            dialog.show(fragmentManager, TAG);
            if (kind.equals(kinds[0]) || kind.equals(kinds[1])) {
                Variables.Firebase.FIRESTORE
                        .collection("Person")
                        .whereEqualTo("rank", kind)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                persons.clear();
                                personsComplete.clear();
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    personsComplete.add(Objects.requireNonNull(snapshot.toObject(Person.class)));
                                    persons.add(Objects.requireNonNull(snapshot.toObject(Person.class)).getFullName());
                                }
                                persons.sort(String::compareTo);
                                personsComplete.sort((o1, o2) -> o1.getFullName().compareToIgnoreCase(o2.getFullName()));
                            } else {
                                Exception e = new Exception();
                                try {
                                    throw e;
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }
                        });
            }
        } catch (Exception e) {
            Snackbar.make(MainActivity.parentLayout, R.string.error_display, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog DIALOG = getDialog();
        if (DIALOG != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(DIALOG.getWindow()).setLayout(width, height);
            DIALOG.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_new_chargen, container, false);
        if (kind.equals(kinds[0])) {
            (view.findViewById(R.id.aktive_layout)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.phil_layout)).setVisibility(View.GONE);
        } else {
            (view.findViewById(R.id.aktive_layout)).setVisibility(View.GONE);
            (view.findViewById(R.id.phil_layout)).setVisibility(View.VISIBLE);
        }
        //Initialize all the variables which have to respond with the ui
        senior = view.findViewById(R.id.x);
        consenior = view.findViewById(R.id.vx);
        fuxmajor = view.findViewById(R.id.fm);
        scriptor = view.findViewById(R.id.xx);
        kassier = view.findViewById(R.id.xxx);
        seniorAH = view.findViewById(R.id.ahx);
        scriptorAH = view.findViewById(R.id.ahxx);
        kassierAH = view.findViewById(R.id.ahxxx);
        hv1 = view.findViewById(R.id.hbv1);
        hv2 = view.findViewById(R.id.hbv2);
        toolbar = view.findViewById(R.id.new_greeting);
        seniorTV = view.findViewById(R.id.x_name);
        conseniorTV = view.findViewById(R.id.vx_name);
        fuxmajorTV = view.findViewById(R.id.fm_name);
        scriptorTV = view.findViewById(R.id.xx_name);
        kassierTV = view.findViewById(R.id.xxx_name);
        seniorAHTV = view.findViewById(R.id.ahx_name);
        scriptorAHTV = view.findViewById(R.id.ahxx_name);
        kassierAHTV = view.findViewById(R.id.ahxxx_name);
        hv1TV = view.findViewById(R.id.hbv1_name);
        hv2TV = view.findViewById(R.id.hbv2_name);
        seniorIV = view.findViewById(R.id.x_image);
        conseniorIV = view.findViewById(R.id.vx_image);
        fuxmajorIV = view.findViewById(R.id.fm_image);
        scriptorIV = view.findViewById(R.id.xx_image);
        kassierIV = view.findViewById(R.id.xxx_image);
        seniorAHIV = view.findViewById(R.id.ahx_image);
        scriptorAHIV = view.findViewById(R.id.ahxx_image);
        kassierAHIV = view.findViewById(R.id.ahxxx_image);
        hv1IV = view.findViewById(R.id.hbv1_image);
        hv2IV = view.findViewById(R.id.hbv2_image);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //do stuff
        toolbar.setNavigationOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.attention)
                    .setMessage(R.string.abort_sure)
                    .setCancelable(true)
                    .setNeutralButton(R.string.abort, null)
                    .setPositiveButton(R.string.yes, ((dialog, which) -> this.dismiss()));
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        });
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_send) {
                Log.d(TAG, "send");
                //send content
                boolean allGood = true;
                for (Object p : chargen) {
                    if (p == null) {
                        allGood = false;
                        break;
                    }
                }
                if (allGood) {
                    if (kind.equals(kinds[0])) {
                        addNewSemesterListener.chargenDone(chargen);
                    } else if (kind.equals(kinds[1])) {
                        addNewSemesterListener.philChargenDone(chargen);
                    }
                    this.dismiss();
                } else {
                    Snackbar.make(requireView(), "Alle Chargen müssen gesetzt werden.", Snackbar.LENGTH_LONG).show();
                }
            }
            return true;
        });
        //active members
        if (kind.equals(kinds[0])) {
            senior.setOnClickListener(this);
            consenior.setOnClickListener(this);
            fuxmajor.setOnClickListener(this);
            scriptor.setOnClickListener(this);
            kassier.setOnClickListener(this);
            Person p;
            try {
                p = (Person) chargen.get(0);
                seniorTV.setText(p.getFullName());
                Firebase.downloadImage(p.getPicture_path(), seniorIV).run();
            } catch (Exception ignored) {
            }
            try {
                p = (Person) chargen.get(1);
                conseniorTV.setText(p.getFullName());
                Firebase.downloadImage(p.getPicture_path(), conseniorIV).run();
            } catch (Exception ignored) {
            }
            try {
                p = (Person) chargen.get(2);
                fuxmajorTV.setText(p.getFullName());
                Firebase.downloadImage(p.getPicture_path(), fuxmajorIV).run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                p = (Person) chargen.get(3);
                scriptorTV.setText(p.getFullName());
                Firebase.downloadImage(p.getPicture_path(), scriptorIV).run();
            } catch (Exception ignored) {
            }
            try {
                p = (Person) chargen.get(4);
                kassierTV.setText(p.getFullName());
                Firebase.downloadImage(p.getPicture_path(), kassierIV).run();
            } catch (Exception ignored) {
            }
        }
        //AH
        else if (kind.equals(kinds[1])) {
            seniorAH.setOnClickListener(this);
            scriptorAH.setOnClickListener(this);
            kassierAH.setOnClickListener(this);
            hv1.setOnClickListener(this);
            hv2.setOnClickListener(this);
            Person p;
            try {
                p = (Person) chargen.get(0);
                p.setId("0");
                seniorAHTV.setText(p.getFullName());
                Firebase.downloadImage(p.getPicture_path(), seniorAHIV).run();
            } catch (Exception ignored) {
            }
            try {
                p = (Person) chargen.get(1);
                p.setId("1");
                scriptorAHTV.setText(p.getFullName());
                Firebase.downloadImage(p.getPicture_path(), scriptorAHIV).run();
            } catch (Exception ignored) {
            }
            try {
                p = (Person) chargen.get(2);
                p.setId("2");
                kassierAHTV.setText(p.getFullName());
                Firebase.downloadImage(p.getPicture_path(), kassierAHIV).run();
            } catch (Exception ignored) {
            }
            try {
                p = (Person) chargen.get(3);
                p.setId("3");
                hv1TV.setText(p.getFullName());
                Firebase.downloadImage(p.getPicture_path(), hv1IV).run();
            } catch (Exception ignored) {
            }
            try {
                p = (Person) chargen.get(4);
                p.setId("4");
                hv2TV.setText(p.getFullName());
                Firebase.downloadImage(p.getPicture_path(), hv2IV).run();
            } catch (Exception ignored) {
            }
        }
        listener = this;
        customDismissListener = this;
    }

    @Override
    public void onClick(View v) {
        if (senior == v) {
            Log.d(TAG, senior.getText().toString());
            PersonPickerDialog personPickerDialog = new PersonPickerDialog(requireContext(), persons, "x");
            personPickerDialog.show();
        } else if (v == consenior) {
            Log.d(TAG, consenior.getText().toString());
            PersonPickerDialog personPickerDialog = new PersonPickerDialog(requireContext(), persons, "vx");
            personPickerDialog.show();
        } else if (v == fuxmajor) {
            Log.d(TAG, fuxmajor.getText().toString());
            PersonPickerDialog personPickerDialog = new PersonPickerDialog(requireContext(), persons, "fm");
            personPickerDialog.show();
        } else if (v == scriptor) {
            Log.d(TAG, scriptor.getText().toString());
            PersonPickerDialog personPickerDialog = new PersonPickerDialog(requireContext(), persons, "xx");
            personPickerDialog.show();
        } else if (v == kassier) {
            Log.d(TAG, kassier.getText().toString());
            PersonPickerDialog personPickerDialog = new PersonPickerDialog(requireContext(), persons, "xxx");
            personPickerDialog.show();
        } else if (seniorAH == v) {
            Log.d(TAG, seniorAH.getText().toString());
            PersonPickerDialog personPickerDialog = new PersonPickerDialog(requireContext(), persons, "ahx");
            personPickerDialog.show();
        } else if (v == scriptorAH) {
            Log.d(TAG, scriptorAH.getText().toString());
            PersonPickerDialog personPickerDialog = new PersonPickerDialog(requireContext(), persons, "ahxx");
            personPickerDialog.show();
        } else if (v == kassierAH) {
            Log.d(TAG, kassierAH.getText().toString());
            PersonPickerDialog personPickerDialog = new PersonPickerDialog(requireContext(), persons, "ahxxx");
            personPickerDialog.show();
        } else if (v == hv1) {
            Log.d(TAG, hv1.getText().toString());
            PersonPickerDialog personPickerDialog = new PersonPickerDialog(requireContext(), persons, "hbv1");
            personPickerDialog.show();
        } else if (v == hv2) {
            Log.d(TAG, hv2.getText().toString());
            PersonPickerDialog personPickerDialog = new PersonPickerDialog(requireContext(), persons, "hbv2");
            personPickerDialog.show();
        } else {
            Log.d(TAG, requireContext().getString(R.string.error_site_messages));
            Snackbar.make(requireView(), R.string.error_site_messages, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void dismissSelector(@NotNull String kind, int position) {
        CheckChargeDialog dialog = null;
        if (position < 0) {
            //Add new Person with only the necessary data (Fist and last name, address, major, mobile and picture)
            if (position != -3) {
                Log.d(TAG, "Adding a new person" + position);
                dialog = new CheckChargeDialog(getContext(), kind, null);
            } else {
                dismissChecker(kind, null);
            }
        } else {
            //Check the saved data and TODO upload a new image
            Log.d(TAG, kind + " at " + position + " resulted in " + personsComplete.get(position).getFullName());
            Person person = new Person();
            person.setFirst_Name(personsComplete.get(position).getFirst_Name());
            person.setLast_Name(personsComplete.get(position).getLast_Name());
            if (!kind.contains("h")) {
                person.setPoB(personsComplete.get(position).getPoB());
            }
            person.setMajor(personsComplete.get(position).getMajor());
            person.setMobile(personsComplete.get(position).getMobile());
            person.setAddress(personsComplete.get(position).getAddress());
            person.setPicture_path(personsComplete.get(position).getPicture_path());
            dialog = new CheckChargeDialog(getContext(), kind, person);
        }
        try {
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Displaying the check dialog failed.");
        }
    }

    @Override
    public void dismissChecker(@NotNull String kind, @Nullable Person person) {
        try {
            if (!kind.contains("h")) {
                switch (kind) {
                    case "x":
                        chargen.set(0, person);
                        seniorTV.setText(person.getFullName());
                        break;
                    case "vx":
                        chargen.set(1, person);
                        conseniorTV.setText(person.getFullName());
                        break;
                    case "fm":
                        chargen.set(2, person);
                        fuxmajorTV.setText(person.getFullName());
                        break;
                    case "xx":
                        chargen.set(3, person);
                        scriptorTV.setText(person.getFullName());
                        break;
                    case "xxx":
                        chargen.set(4, person);
                        kassierTV.setText(person.getFullName());
                        break;
                }
            } else if (kind.contains("h")) {
                switch (kind) {
                    case "ahx":
                        chargen.set(0, person);
                        seniorAHTV.setText(person.getFullName());
                        break;
                    case "ahxx":
                        chargen.set(1, person);
                        scriptorAHTV.setText(person.getFullName());
                        break;
                    case "ahxxx":
                        chargen.set(2, person);
                        kassierAHTV.setText(person.getFullName());
                        break;
                    case "hbv1":
                        chargen.set(3, person);
                        hv1TV.setText(person.getFullName());
                        break;
                    case "hbv2":
                        chargen.set(4, person);
                        hv2TV.setText(person.getFullName());
                        break;
                }
            } else {
                Log.d(TAG, "No fitting kind.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
