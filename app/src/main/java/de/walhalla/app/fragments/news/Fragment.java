package de.walhalla.app.fragments.news;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.walhalla.app.MainActivity;
import de.walhalla.app.R;
import de.walhalla.app.User;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.models.News;
import de.walhalla.app.utils.ImageDownload;
import de.walhalla.app.utils.Variables;

@SuppressWarnings("StaticFieldLeak")
public class Fragment extends CustomFragment {
    protected static final String TAG = "MessagesFragment";
    private final ArrayList<News> newsList = new ArrayList<>();
    private Toolbar toolbar;
    private ListenerRegistration registration;
    private boolean draft = false;
    private boolean internal = false;
    private LinearLayout layout;
    private LayoutInflater inflater;

    public Fragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateList();
    }

    private void updateList() {
        registration = Variables.Firebase.FIRESTORE
                .collection("News")
                .whereEqualTo(News.DRAFT, draft)
                .whereEqualTo(News.INTERNAL, internal)
                .limit(20)
                .addSnapshotListener(MetadataChanges.INCLUDE, ((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "listen:error", error);
                        return;
                    }
                    if (value != null) {
                        formatResult(value);
                    }
                }));
    }

    @Contract(pure = true)
    private void formatResult(@NotNull QuerySnapshot value) {
        newsList.clear();
        for (DocumentSnapshot snapshot : value) {
            try {
                News n = snapshot.toObject(News.class);
                if (n != null) {
                    newsList.add(n);
                    //Order list descending by date.
                    try {
                        newsList.sort((o1, o2) -> Integer.compare(o2.getTime().compareTo(o1.getTime()), o1.getTime().compareTo(o2.getTime())));
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "formatResult:parse:error", e);
            }
        }
        //Display news-feed
        displayNews();
    }

    @SuppressLint("InflateParams")
    private void displayNews() {
        layout.removeAllViewsInLayout();
        for (News n : newsList) {
            Log.d(TAG, "News-element with title: " + n.getTitle());
            List<String> content = new ArrayList<>(n.getContent());
            String image = n.getImage();
            Timestamp time = n.getTime();
            String title = n.getTitle();
            View item = inflater.inflate(R.layout.item_news, null);

            TextView newsTitle = item.findViewById(R.id.item_news_title);
            TextView newsTime = item.findViewById(R.id.item_news_time);
            LinearLayout newsLayout = item.findViewById(R.id.item_news_content);
            ImageButton newsImage = item.findViewById(R.id.item_news_image);
            newsTitle.setText(title);
            //Format to dd.MM.yyyy hh:mm Uhr
            Date date = time.toDate();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", Variables.LOCALE);
            String timeStr = format.format(date) + " " + getString(R.string.clock);
            newsTime.setText(timeStr);
            for (String rowStr : content) {
                TextView rowTV = (TextView) inflater.inflate(R.layout.text_view_history_text, null);
                rowTV.setText(rowStr);
                newsLayout.addView(rowTV);
            }
            //display buttons with links, of there are some.
            try {
                Map<String, Object> link = new HashMap<>(n.getLink());
                Set<String> linkList = link.keySet();
                for (String linkStr : linkList) {
                    Button button = (Button) inflater.inflate(R.layout.text_view_history_button, null);
                    button.setText(linkStr);
                    button.setOnClickListener(v -> {
                        //Open browser with the set link
                        try {
                            String url = (String) link.get(linkStr);
                            MainActivity.listener.browser(url);
                        } catch (Exception ignored1) {
                            //No link, so Email to
                            MainActivity.listener.email();
                        }
                    });
                    newsLayout.addView(button);
                }
            } catch (Exception ignored) {
            }
            //Get image, if anyone is set, and set it
            if (image != null && !image.isEmpty()) {
                new ImageDownload(newsImage::setImageBitmap, n.getImage(), true).execute();
                newsImage.setClickable(false);
                newsImage.setVisibility(View.VISIBLE);
                newsImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            layout.addView(item);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        toolbar.getMenu().clear();
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
        this.inflater = inflater;
        toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbarContent();
        LinearLayout fragmentLayout = view.findViewById(R.id.fragment_container);
        ScrollView sc = new ScrollView(getContext());
        fragmentLayout.addView(sc);
        layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.removeAllViewsInLayout();
        sc.addView(layout);
        return view;
    }

    private void toolbarContent() {
        try {
            toolbar.setTitle(R.string.menu_messages);
            toolbar.setSubtitle("");

            buttonVisibility();

            toolbar.setOnMenuItemClickListener(item -> {
                try {
                    if (item.getItemId() == R.id.action_add) {
                        //Open a full screen dialog like the login one
                        Dialog.display(getParentFragmentManager());
                    } else if (item.getItemId() == R.id.action_draft) {
                        changeBooleans(true, false);
                    } else if (item.getItemId() == R.id.action_internal) {
                        changeBooleans(false, true);
                    } else if (item.getItemId() == R.id.action_public) {
                        changeBooleans(false, false);
                    }
                    return true;
                } catch (NullPointerException npe) {
                    Log.d(TAG, "Toolbar could not find the fitting menu item", npe);
                    return false;
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Setting the toolbar was unsuccessful", e);
        }
    }

    private void buttonVisibility() {
        toolbar.getMenu().clear();
        if (User.isLogIn()) {
            toolbar.inflateMenu(R.menu.news_signed_in);
            if (!User.hasCharge()) {
                toolbar.getMenu().removeItem(R.id.action_add);
                toolbar.getMenu().removeItem(R.id.action_draft);
            }
        }
    }

    @Override
    public void onAuthChange() {
        buttonVisibility();
        changeBooleans(false, false);
    }

    private void changeBooleans(boolean draft, boolean internal) {
        this.draft = draft;
        this.internal = internal;
        updateList();
    }
}
