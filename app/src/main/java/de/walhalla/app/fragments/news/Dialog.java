package de.walhalla.app.fragments.news;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.walhalla.app.MainActivity;
import de.walhalla.app.R;
import de.walhalla.app.models.News;
import de.walhalla.app.utils.Variables;

import static android.app.Activity.RESULT_OK;

public class Dialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "NewsDialog";
    private TextView title, content;
    private View view;
    private ImageButton image;
    private Button links, send;
    private SwitchCompat switchCompat;
    private Toolbar toolbar;
    private Uri selectedImage;
    private boolean internal = false;

    public Dialog() {
    }

    public static void display(FragmentManager fragmentManager) {
        try {
            Dialog dialog = new Dialog();
            dialog.show(fragmentManager, TAG);
        } catch (Exception ignored) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.dialog_news, container, false);

        toolbar = view.findViewById(R.id.news_toolbar);
        title = view.findViewById(R.id.news_title);
        content = view.findViewById(R.id.news_content);
        image = view.findViewById(R.id.news_image);
        send = view.findViewById(R.id.news_send);
        switchCompat = view.findViewById(R.id.news_switch);
        links = view.findViewById(R.id.news_links);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setNavigationOnClickListener(v -> {
            AlertDialog.Builder draftSaver = new AlertDialog.Builder(getContext());
            draftSaver.setTitle(R.string.abort)
                    .setMessage(R.string.program_abort_sure)
                    .setPositiveButton(R.string.yes, (dialog, which) -> sendDraft())
                    .setNegativeButton(R.string.no, (dialog, which) -> dismiss());
            draftSaver.show();
        });
        toolbar.setTitle(R.string.messages_new_head);
        image.setOnClickListener(this);
        send.setOnClickListener(this);
        switchCompat.setOnClickListener(this);
        links.setOnClickListener(this);
    }

    private void sendDraft() {
        News news = new News();
        news.setInternal(internal);
        news.setDraft(true);
        upload(news);
    }

    private void sendPublic() {
        News news = new News();
        news.setInternal(internal);
        news.setDraft(false);
        upload(news);
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        android.app.Dialog DIALOG = getDialog();
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

    @Override
    public void onClick(@NotNull View v) {
        if (v.getId() == send.getId()) {
            sendPublic();
        } else if (v.getId() == links.getId()) {
            Snackbar.make(view, R.string.toast_still_in_dev, Snackbar.LENGTH_SHORT).show();
            Map<String, Object> links = new HashMap<>();
            if(links.size() != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.messages_add_link)
                        .setView(R.layout.dialog_links)
                        .setNeutralButton(R.string.abort, null) //Discard input
                        .setNegativeButton(R.string.add_more, null) //Save data and open dialog again
                        .setPositiveButton(R.string.send, null); //Save data, toast amount of links
                builder.show();
            }
        } else if (v.getId() == switchCompat.getId()) {
            if (internal) {
                send.setText(R.string.send_public);
                internal = false;
            } else {
                send.setText(R.string.send_private);
                internal = true;
            }
        } else if (v.getId() == image.getId()) {
            String[] items = new String[]{getString(R.string.choose_photo), getString(R.string.choose_picture)};
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.select_image)
                    .setItems(items, (dialog, which) -> {
                        switch (which) {
                            case 0:
                                /*Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takePicture, 0);*/
                                Snackbar.make(view, R.string.toast_still_in_dev, Snackbar.LENGTH_LONG)
                                        .setTextColor(Color.RED)
                                        .show();
                                break;
                            case 1:
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto, 1);
                                break;
                            default:
                                dialog.dismiss();
                                break;
                        }
                    })
                    .setNeutralButton(R.string.abort, null);
            builder.show();
        }
    }

    private void upload(@NotNull News news) {
        boolean error = false;
        String title = this.title.getText().toString();
        String content = this.content.getText().toString();
        Calendar timeStamp = Calendar.getInstance();
        news.setTime(new Timestamp(timeStamp.getTime()));
        news.setTitle(title);
        ArrayList<String> contentList = new ArrayList<>(Arrays.asList(content.split("\n")));
        news.setContent(contentList);
        if (title.isEmpty())
            error = true;
        if (content.isEmpty() || news.getContent().isEmpty())
            error = true;

        //Upload data
        if (!error) {
            if (selectedImage != null) {
                String imageName = (title + " " + timeStamp.getTime().toString().replaceAll(":", "-") + ".jpg").replaceAll(" ", "_");
                StorageReference imageRef = Variables.Firebase.IMAGES.child(imageName);
                UploadTask uploadTask = imageRef.putFile(selectedImage);
                uploadTask.addOnFailureListener(e -> Log.d(TAG, "Image available: upload error", e))
                        .addOnSuccessListener(taskSnapshot -> Log.d(TAG, Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getName())));
                news.setImage("/pictures/" + imageName);
            }
            Variables.Firebase.FIRESTORE
                    .collection("News")
                    .add(news)
                    .addOnSuccessListener(documentReference -> Snackbar.make(MainActivity.parentLayout, R.string.upload_complete, Snackbar.LENGTH_LONG).show());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
            case 1:
                if (resultCode == RESULT_OK) {
                    try {
                        assert imageReturnedIntent != null;
                        selectedImage = imageReturnedIntent.getData();
                        image.setImageURI(selectedImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
