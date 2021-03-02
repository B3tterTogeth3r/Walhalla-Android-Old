package de.walhalla.app.fragments.chargen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.User;
import de.walhalla.app.firebase.Firebase;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.fragments.chargen.ui.Entry;
import de.walhalla.app.interfaces.ChosenSemesterListener;
import de.walhalla.app.models.Person;
import de.walhalla.app.utils.Variables;

@SuppressWarnings("StaticFieldLeak")
public class Fragment extends CustomFragment implements ChosenSemesterListener, Firebase.board {
    protected static final String TAG = "ChargenFragment";
    protected static final float scale = App.getContext().getResources().getDisplayMetrics().density;
    protected static ImageButton add, edit;
    protected static Button title;
    protected static androidx.fragment.app.Fragment f;
    protected static StorageReference image;
    private ArrayAdapter<Person> adapter;
    private ArrayList<Person> numbers = new ArrayList<>();
    private ListenerRegistration registration;

    public Fragment() {
        numbers.clear();
        student(App.getChosenSemester().getID());
    }

    @Override
    public void onStart() {
        super.onStart();
        registration = Variables.Firebase.FIRESTORE
                .collection("Semester")
                .document(String.valueOf(App.getChosenSemester().getID()))
                .collection("Chargen")
                .limit(5)
                .addSnapshotListener(((value, error) -> {
                    if (value != null && !value.isEmpty()) {
                        numbers.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            try {
                                Person p = document.toObject(Person.class);
                                //noinspection ConstantConditions
                                p.setId(document.getId());
                                numbers.add(p);
                            } catch (Exception e) {
                                Log.d(TAG, "Something went wrong while fetching the person data.", e);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Something went wrong while fetching the student board.", error);
                    }
                }));
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        Fragment.f = this;

        //Button chooseSemester = new Button(getContext());
        RelativeLayout header = new RelativeLayout(getContext());

        TextView titleTV = new TextView(getContext());
        titleTV.setTextSize((int) (10 * scale + 0.75f));
        titleTV.setText(R.string.menu_chargen);
        header.addView(titleTV);
        titleTV.setId(R.id.chargen_title);

        add = new ImageButton(getContext());
        header.addView(add);
        add.setId(R.id.chargen_add);
        add.setBackgroundResource(R.color.background);
        add.setImageResource(R.drawable.ic_add);
        RelativeLayout.LayoutParams addParams = (RelativeLayout.LayoutParams) add.getLayoutParams();
        addParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addParams.setMargins(0, (int) scale * 5, (int) scale * 15, (int) scale * 5);
        add.setLayoutParams(addParams);

        edit = new ImageButton(getContext());
        header.addView(edit);
        edit.setId(R.id.chargen_edit);
        edit.setImageResource(R.drawable.ic_edit);
        edit.setBackgroundResource(R.color.background);
        RelativeLayout.LayoutParams editParams = (RelativeLayout.LayoutParams) edit.getLayoutParams();
        editParams.addRule(RelativeLayout.ALIGN_RIGHT);
        editParams.addRule(RelativeLayout.LEFT_OF, add.getId());
        editParams.setMargins(0, (int) scale * 5, (int) scale * 15, (int) scale * 5);
        edit.setLayoutParams(editParams);

        title = new Button(f.getContext());
        title.setText(App.getChosenSemester().getLong());
        //Open SelectSemesterDialog with setOnDismissListener
        title.setOnClickListener(new OnClick(title));

        buttonVisibility();

        LinearLayout linearLayout = view.findViewById(R.id.fragment_container);
        linearLayout.addView(header);
        linearLayout.addView(title);
        linearLayout.addView(display());

        add.setOnClickListener(new OnClick(add));
        edit.setOnClickListener(new OnClick(edit));


        return view;
    }


    @NotNull
    private ListView display() {
        ListView listView = new ListView(getContext());
        adapter = new Entry(getActivity(), numbers);
        listView.setAdapter(adapter);
        //listView.setDivider(null);
        listView.setClickable(false);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(null);

        return listView;
    }

    private void buttonVisibility() {
        //Edit only in the current semester or a admin is logged in
        int visibility;
        if (User.hasCharge() && App.isInternet) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.GONE;
        }
        edit.setVisibility(visibility);
        add.setVisibility(visibility);
    }

    @Override
    public void changeSemesterDone() {
        numbers.clear();
        student(App.getChosenSemester().getID());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (image != null) {
            outState.putString("reference", image.toString());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        // If there was a download in progress, get its reference and create a new StorageReference
        try {
            final String stringRef = savedInstanceState.getString("reference");
            if (stringRef == null) {
                return;
            }
            image = FirebaseStorage.getInstance().getReferenceFromUrl(stringRef);

            // Find all DownloadTasks under this StorageReference
            List<FileDownloadTask> tasks = image.getActiveDownloadTasks();
            if (tasks.size() > 0) {
                // Get the task monitoring the download

                for (FileDownloadTask t : tasks) {
                    t.cancel();
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void student(int semester_id) {
        Log.i(TAG, "Listener got activated");
    }

    @Override
    public void philister(int semester_id) {

    }

    @Override
    public void onAuthChange() {
        buttonVisibility();
        numbers.clear();
        student(App.getChosenSemester().getID());
    }
}
