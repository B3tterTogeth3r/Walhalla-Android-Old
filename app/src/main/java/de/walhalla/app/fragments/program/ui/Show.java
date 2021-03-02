package de.walhalla.app.fragments.program.ui;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.walhalla.app.App;
import de.walhalla.app.firebase.EventChangeNotifier;
import de.walhalla.app.firebase.Firebase;
import de.walhalla.app.fragments.program.Fragment;
import de.walhalla.app.models.Event;
import de.walhalla.app.utils.Variables;

@SuppressLint("StaticFieldLeak")
public class Show extends Fragment implements EventChangeNotifier, Firebase.Event {
    public static EventChangeNotifier listener;
    private static ArrayAdapter<Event> adapter;
    private static ArrayList<Event> arrayList = new ArrayList<>();
    private ListenerRegistration registration;

    public Show() {
        listener = this;
        arrayList.clear();
        oneSemester(App.getChosenSemester().getID());
    }

    @NotNull
    public static ListView load() {
        ListView listView = new ListView(f.getContext());
        adapter = new Entry(f.getActivity(), arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) ->
                Details.display(f.getParentFragmentManager(), arrayList.get(position))
        );

        return listView;
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            registration.remove();
        } catch (Exception e) {
            Log.d(TAG, "Something went wrong in onStop()", e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ArrayList<Event> data = new ArrayList<>();
        registration = Variables.Firebase.FIRESTORE
                .collection("Semester")
                .document(String.valueOf(App.getChosenSemester().getID()))
                .collection("Event")
                .whereNotEqualTo("start", null)
                .addSnapshotListener((value, error) -> {
                    if (value != null && !value.isEmpty()) {
                        arrayList.clear();
                        List<DocumentSnapshot> task = value.getDocuments();
                        for (DocumentSnapshot document : task) {
                            Event e = document.toObject(Event.class);
                            try {
                                e.setId(document.getId());
                                arrayList.add(e);
                                arrayList.sort((o1, o2) -> Integer.compare(o1.getStart().compareTo(o2.getStart()), o2.getStart().compareTo(o1.getStart())));
                            } catch (Exception ignored) {
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Something went wrong.", error);
                    }
                });
    }

    @Override
    public void eventChanged() {
        arrayList.clear();
        oneSemester(App.getChosenSemester().getID());
    }

    @Override
    public void oneSemester(int semester_id) {
        Variables.Firebase.FIRESTORE.collection("Semester")
                .document(String.valueOf(semester_id))
                .collection("Event")
                .whereNotEqualTo("start", null)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> task = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : task) {
                            Event e = document.toObject(Event.class);
                            arrayList.add(e);
                            arrayList.sort((o1, o2) -> Integer.compare(o1.getStart().compareTo(o2.getStart()), o2.getStart().compareTo(o1.getStart())));
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Something with the download went wrong."));
    }
}
