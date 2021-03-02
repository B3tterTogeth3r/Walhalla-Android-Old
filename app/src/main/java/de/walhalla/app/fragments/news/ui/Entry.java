package de.walhalla.app.fragments.news.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.firebase.Firebase;
import de.walhalla.app.models.News;

@SuppressWarnings("StaticFieldLeak")
public class Entry extends ArrayAdapter<News> {
    private static final String TAG = "NewsEntry";
    protected static final float scale = App.getContext().getResources().getDisplayMetrics().density;
    private final Context context;
    private final ArrayList<News> newsList;
    private TextView title, time, content;
    private ImageButton picture;

    public Entry(Context context, ArrayList<News> newsList) {
        super(context, R.layout.item_news, newsList);
        this.newsList = newsList;
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressLint("ViewHolder")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.item_news, parent, false);

        title = view.findViewById(R.id.item_news_title);
        time = view.findViewById(R.id.item_news_time);
        content = view.findViewById(R.id.item_news_content);
        picture = view.findViewById(R.id.item_news_image);
        News n = newsList.get(position);

        loadContent(view, n);

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        try {
            view.startAnimation(anim);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void loadContent(View view, News n) {
        try {
            title.setText(n.getTitle());
            time.setText(n.getDate_Output());
            content.setText(n.getMatter());
            if (n.getPicture() != null) {
                picture.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (144 * scale + 0.5f));
                picture.setScaleType(ImageView.ScaleType.FIT_CENTER);
                synchronized (this) {
                    Firebase.downloadImage(n.getPicture(), picture).run();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Image at with id " + n.getId() + " couldn't be displayed");
        }
    }
}
