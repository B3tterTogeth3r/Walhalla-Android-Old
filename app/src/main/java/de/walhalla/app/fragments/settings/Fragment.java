package de.walhalla.app.fragments.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.R;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.utils.Variables;

import static de.walhalla.app.firebase.Firebase.Messaging.SubscribeTopic;
import static de.walhalla.app.firebase.Firebase.Messaging.TOPIC_DEFAULT;
import static de.walhalla.app.firebase.Firebase.Messaging.TOPIC_INTERNAL;
import static de.walhalla.app.firebase.Firebase.Messaging.UnsubscribeTopic;
import static de.walhalla.app.firebase.Firebase.Messaging.isSubscribed;

@SuppressLint("InflateParams")
public class Fragment extends CustomFragment {
    private static final String TAG = "SettingsFragment";
    private LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        this.inflater = inflater;
        ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setTitle(R.string.app_name);
        ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setSubtitle("");

        LinearLayout layout = view.findViewById(R.id.fragment_container);

        layout.addView(getPublicPush());
        if (Variables.Firebase.isUserLogin) {
            layout.addView(getInternalPush());
        }

        return view;
    }

    @NotNull
    private View getPublicPush() {
        View view = inflater.inflate(R.layout.item_settings, null);
        CheckBox publicPush = view.findViewById(R.id.item_settings_checkbox);
        TextView textView = view.findViewById(R.id.item_settings_text);
        textView.setText(R.string.settings_subscribe_public);

        publicPush.setChecked(isSubscribed(TOPIC_DEFAULT));

        //If user is subscribed to public set checkbox. if not, don't set.
        //change subscription depending on CB state.
        publicPush.setOnClickListener(v -> {
            if (publicPush.isChecked()) {
                SubscribeTopic(TOPIC_DEFAULT);
            } else {
                UnsubscribeTopic(TOPIC_DEFAULT);
            }
        });
        return view;
    }

    @NotNull
    private View getInternalPush() {
        View view = inflater.inflate(R.layout.item_settings, null);
        CheckBox internalPush = view.findViewById(R.id.item_settings_checkbox);
        TextView textView = view.findViewById(R.id.item_settings_text);
        textView.setText(R.string.settings_subscribe_internal);

        internalPush.setChecked(isSubscribed(TOPIC_INTERNAL));

        //If user is subscribed to internal set checkbox. if not, don't set.
        //change subscription depending on CB state.
        internalPush.setOnClickListener(v -> {
            if (internalPush.isChecked()) {
                SubscribeTopic(TOPIC_INTERNAL);
            } else {
                UnsubscribeTopic(TOPIC_INTERNAL);
            }
        });
        return view;
    }
}
