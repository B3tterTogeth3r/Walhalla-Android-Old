package de.walhalla.app.fragments.news;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.User;
import de.walhalla.app.firebase.NewsChangeNotifier;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.fragments.news.ui.Add;
import de.walhalla.app.fragments.news.ui.Entry;
import de.walhalla.app.fragments.news.ui.Title;
import de.walhalla.app.models.News;
import de.walhalla.app.utils.Database;
import de.walhalla.app.utils.Variables;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@SuppressWarnings("StaticFieldLeak")
public class Fragment extends CustomFragment implements NewsChangeNotifier {
    protected static final String TAG = "MessagesFragment";
    protected static final float scale = App.getContext().getResources().getDisplayMetrics().density;
    protected static ImageButton done, abort, add;
    protected static androidx.fragment.app.Fragment f;
    protected static String whichOne;
    protected static EditText content;
    protected static ImageButton ShowImg;
    protected static Bitmap bitmap;
    protected static StorageReference image;
    public static NewsChangeNotifier listener;
    private ArrayAdapter<News> adapter;

    public Fragment() {
        Fragment.whichOne = Variables.SHOW;
        listener = this;
    }

    public Fragment(String whichOne) {
        listener = this;
        Fragment.whichOne = whichOne;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);;
        toolbar.setTitle(R.string.menu_messages);
        toolbar.setSubtitle("");
        //TODO add the "add" button into the Toolbar
        f = this;
        ArrayList<News> arrayList = Database.getNewsArrayList();
        arrayList.sort((o1, o2) -> o1.getDate_Date().after(o2.getDate_Date()) ? -1 : o1.getDate_Date().before(o2.getDate_Date()) ? 1 : 0);


        Thread.currentThread().setName(TAG);

        LinearLayout linearLayout = view.findViewById(R.id.fragment_container);
        linearLayout.setLayoutTransition(new LayoutTransition());
        linearLayout.addView(Title.load());
        switch (whichOne) {
            case Variables.SHOW:
                buttonVisibility();
                linearLayout.addView(display(arrayList));
                break;
            case Variables.ADD:
                linearLayout.addView(Add.load());
                break;
        }

        try {
            abort.setOnClickListener(new OnClick());
            done.setOnClickListener(new OnClick());
            add.setOnClickListener(new OnClick());
            ShowImg.setOnClickListener(new OnClick());
        } catch (Exception ignored) {
        }

        return view;
    }

    private void buttonVisibility() {
        if (User.isLogIn() && App.isInternet &&
                (User.isX() | User.isVX() | User.isFM())) {
            ((Toolbar) f.getActivity().findViewById(R.id.toolbar)).inflateMenu(R.menu.add);
        }
    }

    @NotNull
    private ListView display(ArrayList<News> arrayList) {
        ListView listView = new ListView(getContext());
        adapter = new Entry(getActivity(), arrayList);
        listView.setAdapter(adapter);
        listView.setDivider(null);
        //TODO if Charge is logged in, make it editable
        listView.setOnItemClickListener(null);

        return listView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        bitmap = (Bitmap) Objects.requireNonNull(data.getExtras())
                                .get("data");
                        ShowImg.setImageBitmap(bitmap);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri path = data.getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(requireActivity()
                                    .getApplicationContext().getContentResolver(), path);
                            ShowImg.setImageBitmap(bitmap);
                            ShowImg.setVisibility(View.VISIBLE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
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
    public void newsChanged() {
        //Edit/add changes of the realtime database animated in here
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAuthChange() {
        buttonVisibility();
    }
}
