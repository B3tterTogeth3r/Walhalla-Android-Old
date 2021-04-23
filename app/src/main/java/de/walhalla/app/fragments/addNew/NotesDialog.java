package de.walhalla.app.fragments.addNew;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import de.walhalla.app.MainActivity;
import de.walhalla.app.R;
import de.walhalla.app.interfaces.AddNewSemesterListener;

public class NotesDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "ChargenDialog";
    private final AddNewSemesterListener addNewSemesterListener;
    private final ArrayList<Object> notes;
    private LinearLayout viewLayout;
    private ImageButton add;
    private Toolbar toolbar;
    private LayoutInflater layoutInflater;

    public NotesDialog(AddNewSemesterListener addNewSemesterListener, ArrayList<Object> notes) {
        this.addNewSemesterListener = addNewSemesterListener;
        if (notes != null && notes.size() != 0) {
            this.notes = notes;
        } else {
            this.notes = new ArrayList<>();
        }
    }

    public static void display(FragmentManager fragmentManager, ArrayList<Object> notes, AddNewSemesterListener addNewSemesterListener) {
        try {
            NotesDialog dialog = new NotesDialog(addNewSemesterListener, notes);
            dialog.show(fragmentManager, TAG);
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
        View view = inflater.inflate(R.layout.dialog_new_notes, container, false);
        //Initialize all the variables which have to respond with the ui
        layoutInflater = getLayoutInflater();
        viewLayout = view.findViewById(R.id.new_notes_dialog);
        add = view.findViewById(R.id.new_notes_row_add);
        toolbar = view.findViewById(R.id.new_notes);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //do stuff
        viewLayout.removeAllViewsInLayout();

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
                //Format content
                addNewSemesterListener.notesDone(notes);
                this.dismiss();
            }
            return true;
        });
        add.setOnClickListener(this);
        if (notes != null && notes.size() != 0) {
            int size = notes.size();
            for (int i = 0; i < size; i++) {
                if (notes.get(i).toString().length() != 0) {
                    addView(((String) notes.get(i)));
                }
            }
        }
    }

    @Override
    public void onClick(@NotNull View v) {
        if (v.getId() == add.getId()) {
            Log.d(TAG, "add clicked");
            addView(null);
        }
        viewLayout.invalidate();
    }

    @SuppressLint("InflateParams")
    private void addView(@Nullable String existingNote) {
        View view = layoutInflater.inflate(R.layout.new_notes_row, null);
        viewLayout.addView(view);
        int index = notes.size();
        notes.add(null);
        final String[] note = new String[1];
        note[0] = "";
        TextView content = view.findViewById(R.id.new_notes_row_text);
        TextInputEditText input = view.findViewById(R.id.new_notes_row_text_input);
        TextInputLayout inputLayout = view.findViewById(R.id.new_notes_row_text_input_outline);
        ImageButton edit = view.findViewById(R.id.new_notes_row_edit);
        ImageButton remove = view.findViewById(R.id.new_notes_row_remove);
        ImageButton save = view.findViewById(R.id.new_notes_row_add);
        ImageButton abort = view.findViewById(R.id.new_notes_row_abort);
        if (existingNote != null && existingNote.length() != 0) {
            note[0] = existingNote;
            content.setText(note[0]);
            inputLayout.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            abort.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            remove.setVisibility(View.VISIBLE);
        }
        remove.setOnClickListener(v1 -> {
            try {
                notes.remove(index);
            } catch (Exception ignored) {
            }
            viewLayout.removeView(view);
            viewLayout.invalidate();
        });
        save.setOnClickListener(v2 -> {
            if (input.getText().toString().length() >= 20) {
                note[0] = input.getText().toString();
                notes.set(index, note[0]);
                content.setText(note[0]);
                inputLayout.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                abort.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                remove.setVisibility(View.VISIBLE);
            } else {
                viewLayout.removeView(view);
                viewLayout.invalidate();
            }
        });
        edit.setOnClickListener(v3 -> {
            input.setText(note[0]);
            inputLayout.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            abort.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            remove.setVisibility(View.GONE);
        });
        abort.setOnClickListener(v4 -> {
            inputLayout.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            abort.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            remove.setVisibility(View.VISIBLE);
        });
    }
}
