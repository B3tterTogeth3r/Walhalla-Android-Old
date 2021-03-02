package de.walhalla.app.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.Objects;

import de.walhalla.app.App;
import de.walhalla.app.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SelectImageDialog extends Fragment {
    final CharSequence[] options = {App.getContext().getResources().getString(R.string.take_photo),
            App.getContext().getResources().getString(R.string.choose_photo),
            App.getContext().getResources().getString(R.string.abort)};
    private Bitmap bitmap;
    private final ImageButton showImg;
    private final Intent intent;
    private final int requestCode;

    public SelectImageDialog(Bitmap bitmap, ImageButton showImg, Intent intent, int requestCode) {
        super();
        this.intent = intent;
        this.requestCode = requestCode;
        this.bitmap = bitmap;
        this.showImg = showImg;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        startActivityForResult(intent, requestCode);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(null, container, false);

        startActivityForResult(intent, requestCode);
        return view;
    }

    public void onClick(DialogInterface dialog, int item) {
        if (options[item].equals(App.getContext().getString(R.string.take_photo))) {
            if (ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            } else {
                ActivityCompat.requestPermissions((Activity) App.getContext(), new String[]{Manifest.permission.CAMERA}, 123);
            }
        } else if (options[item].equals(App.getContext().getString(R.string.choose_photo))) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        } else if (options[item].equals(App.getContext().getString(R.string.abort))) {
            dialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        bitmap = (Bitmap) Objects.requireNonNull(data.getExtras())
                                .get("data");
                        showImg.setImageBitmap(bitmap);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri path = data.getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(requireActivity()
                                    .getApplicationContext().getContentResolver(), path);
                            showImg.setImageBitmap(bitmap);
                            showImg.setVisibility(View.VISIBLE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }
}
