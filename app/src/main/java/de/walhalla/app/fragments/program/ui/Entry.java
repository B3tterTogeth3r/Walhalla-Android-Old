package de.walhalla.app.fragments.program.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.models.Event;
import de.walhalla.app.utils.Variables;

public class Entry extends ArrayAdapter<Event> {
    private static final String TAG = "EventEntry";
    private static final float scale = App.getContext().getResources().getDisplayMetrics().density;
    private final Context context;
    private final ArrayList<Event> eventList;


    public Entry(Context context, ArrayList<Event> eventList) {
        super(context, R.layout.item_event_default, eventList);
        this.context = context;
        this.eventList = eventList;
    }


    @NonNull
    @Override
    @SuppressLint("ViewHolder")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        Event event = eventList.get(position);
        View view = inflater.inflate(R.layout.item_event_default, parent, false);

        RelativeLayout date = view.findViewById(R.id.item_event_date);
        TextView year = view.findViewById(R.id.item_event_year);
        TextView day = view.findViewById(R.id.item_event_day);
        TextView month = view.findViewById(R.id.item_event_month);
        TextView time = view.findViewById(R.id.item_event_time);
        TextView punkt = view.findViewById(R.id.item_event_punkt);
        TextView collar = view.findViewById(R.id.item_event_collar);
        TextView title = view.findViewById(R.id.item_event_title);
        TextView description = view.findViewById(R.id.item_event_description);

        //Switch bewteen the kinds of events
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(event.getStart().toDate());
            if (event.getPunctuality().contains("after")) {
                date.setVisibility(View.INVISIBLE);
                //TODO time.setText(event.getTimeProgram());
                collar.setText(event.getCollar());
                punkt.setVisibility(View.GONE);
                title.setText(event.getTitle());
                description.setText(event.getDescription());
            } else if (event.getPunctuality().contains("later")) {
                date.setVisibility(View.INVISIBLE);
                time.setText(getTime(calendar));
                punkt.setVisibility(View.GONE);
                collar.setVisibility(View.GONE);
                title.setText(event.getTitle());
                description.setText(event.getDescription());
            } else if (event.getPunctuality().contains("total")) {
                //what to show?
                date.setVisibility(View.VISIBLE);
                year.setText(String.valueOf(calendar.get(Calendar.YEAR)));
                day.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                month.setText(Variables.MONTHS[calendar.get(Calendar.MONTH)]);
                //TODO time.setText(event.getTimeProgram());
                collar.setVisibility(View.GONE);
                punkt.setVisibility(View.GONE);
                title.setText(event.getTitle());
                description.setText(event.getDescription());
            } else if (event.getPunctuality().contains("info")) {
                //TODO what to show?
                date.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                collar.setVisibility(View.GONE);
                punkt.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                String helper = event.getTitle() + "\\n" + event.getDescription();
                description.setText(helper);
            } else {
                date.setVisibility(View.VISIBLE);
                year.setText(String.valueOf(calendar.get(Calendar.YEAR)));
                day.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                month.setText(Variables.MONTHS[calendar.get(Calendar.MONTH)]);
                punkt.setText(event.getPunctuality());
                time.setText(getTime(calendar));
                //punkt.setVisibility(View.INVISIBLE);
                collar.setText(event.getCollar());
                title.setText(event.getTitle());
                description.setText(event.getDescription());
            }
        } catch (Exception format) {
            Log.e(TAG, "Something went wrong while formatting data", format);
        }

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        try {
            view.startAnimation(anim);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @NotNull
    private String getTime(@NotNull Calendar calendar) {
        float hourFL = calendar.get(Calendar.HOUR_OF_DAY);
        float minuteFL = calendar.get(Calendar.MINUTE);
        String minute, hour;
        if (hourFL < 10) {
            hour = "0" + String.format(Variables.LOCALE, "%.0f", hourFL);
        } else {
            hour = String.format(Variables.LOCALE, "%.0f", hourFL);
        }
        if (minuteFL < 10) {
            minute = "0" + String.format(Variables.LOCALE, "%.0f", minuteFL);
        } else {
            minute = String.format(Variables.LOCALE, "%.0f", minuteFL);
        }
        return hour + "." + minute + " " + context.getResources().getString(R.string.clock);
    }
}
