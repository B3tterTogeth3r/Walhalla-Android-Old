package de.walhalla.app.fragments.addNew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.walhalla.app.MainActivity;
import de.walhalla.app.R;
import de.walhalla.app.interfaces.AddNewSemesterListener;
import de.walhalla.app.models.Person;

public class GreetingDialog extends DialogFragment {
    private static final String TAG = "Greeting dialog";
    private final AddNewSemesterListener addNewSemesterListener;
    private final List<Object> text;
    private TextInputEditText content;
    private Toolbar toolbar;
    private TextView senior, phil_senior;

    public GreetingDialog(List<Object> text, AddNewSemesterListener addNewSemesterListener) {
        this.addNewSemesterListener = addNewSemesterListener;
        this.text = text;
    }

    public static void display(FragmentManager fragment, @Nullable List<Object> text, @NotNull AddNewSemesterListener addNewSemesterListener) {
        try {
            GreetingDialog dialog = new GreetingDialog(text, addNewSemesterListener);
            dialog.show(fragment, TAG);
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
        View view = inflater.inflate(R.layout.dialog_new_greeting, container, false);
        content = view.findViewById(R.id.greeting_content);
        toolbar = view.findViewById(R.id.new_greeting);
        senior = view.findViewById(R.id.charge_x);
        phil_senior = view.findViewById(R.id.philister_x);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        if (text != null && text.size() != 0) {
            //Format content into string with line break for every new entry
            StringBuilder greetingText = new StringBuilder();
            int size = text.size() - 2;
            for (int i = 1; i < size; i++) {
                greetingText.append(text.get(i)).append("\n");
            }
            int length = greetingText.length();
            greetingText.delete(length - 1, length);
            this.content.setText(greetingText.toString());
        }
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_send) {
                Log.d(TAG, "send");
                //Format content
                String content = this.content.getText().toString();
                ArrayList<Object> contentList = new ArrayList<>();
                //Add first line
                String firstLine = getString(R.string.greeting_first_line);
                contentList.add(firstLine);
                contentList.addAll(Arrays.asList(content.split("\n")));
                addNewSemesterListener.greetingDone(contentList);
                this.dismiss();
            }
            return true;
        });
        try {
            String name = ((Person) NewSemesterDialog.chargenList.get(0)).getFullName();
            senior.setText(name);
            name = ((Person) NewSemesterDialog.philChargenList.get(0)).getFullName();
            phil_senior.setText(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
