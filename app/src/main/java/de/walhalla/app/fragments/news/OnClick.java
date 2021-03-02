package de.walhalla.app.fragments.news;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.walhalla.app.App;
import de.walhalla.app.MainActivity;
import de.walhalla.app.R;
import de.walhalla.app.models.News;
import de.walhalla.app.models.Picture;
import de.walhalla.app.utils.Database;
import de.walhalla.app.utils.Variables;

public class OnClick extends Fragment implements View.OnClickListener {

    public OnClick() {
    }

    @Override
    public void onClick(View v) {
        if (v == ShowImg) {
            selectImage();
        }
        if (v == add) {
            androidx.fragment.app.Fragment fragment = new Fragment(Variables.ADD);
            FragmentTransaction transaction = f.getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        if (v == abort) {
            androidx.fragment.app.Fragment fragment = new Fragment(Variables.SHOW);
            FragmentTransaction transaction = f.getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        if (v == done) {
            String tvTitle = title.getText().toString();
            String tvContent = content.getText().toString();
            Date currentTime = Calendar.getInstance().getTime();
            if (!(tvTitle.equals("")) && !(tvContent.equals(""))) {
                if (bitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    byte[] pictureData = baos.toByteArray();

                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Variables.LOCALE);
                    String datumNeu = format.format(currentTime);
                    String pictureName = tvTitle + "_" + datumNeu;
                    StorageReference image = Variables.Firebase.IMAGES.child(pictureName + ".jpg");

                    UploadTask uploadTask = image.putBytes(pictureData);
                    uploadTask.addOnFailureListener(e -> uploadError())
                            .addOnCompleteListener(task -> {
                                //Set the name into the picture-list
                                ArrayList<Picture> pal = Database.getPictureArrayList();
                                int id = pal.get(pal.size() - 1).getID() + 1;
                                //Start text upload
                                Variables.Firebase.Reference.PICTURE.child(String.valueOf(id)).setValue(pictureName)
                                        .addOnSuccessListener(aVoid -> uploadText(tvTitle, tvContent, String.valueOf(id)))
                                        .addOnFailureListener(e -> uploadError());
                            });
                } else {
                    uploadText(tvTitle, tvContent, null);
                }
            }
        }
    }

    private void uploadError() {
        Snackbar.make(f.requireView(), R.string.error_upload, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.close, v -> {
                })
                .setActionTextColor(App.getContext().getResources().getColor(R.color.colorPrimaryDark, null))
                .show();
    }

    private void uploadText(String tvTitle, String tvContent, @Nullable String imageID) {
        ArrayList<News> nal = Database.getNewsArrayList();
        int id = nal.get(nal.size() - 1).getId() + 1;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Variables.LOCALE);
        String date = format.format(Calendar.getInstance().getTime());
        Variables.Firebase.Reference.NEWS.child(String.valueOf(id)).child("date").setValue(date);
        Variables.Firebase.Reference.NEWS.child(String.valueOf(id)).child("matter").setValue(tvContent);
        Variables.Firebase.Reference.NEWS.child(String.valueOf(id)).child("title").setValue(tvTitle);
        if (imageID == null) {
            imageID = "0";
        }
        int picID = Integer.parseInt(imageID);
        Variables.Firebase.Reference.NEWS.child(String.valueOf(id)).child("picture").setValue(picID)
                .addOnFailureListener(e -> uploadError())
                .addOnSuccessListener(aVoid -> {
                    //TODO Send push message to all
                    Snackbar.make(MainActivity.parentLayout, R.string.upload_complete, Snackbar.LENGTH_LONG).show();
                    androidx.fragment.app.Fragment fragment = new Fragment(Variables.SHOW);
                    FragmentTransaction transaction = f.getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                });
    }

    public void selectImage() {
        final CharSequence[] options = {f.getString(R.string.take_photo), f.getString(R.string.choose_photo)};

        AlertDialog.Builder builder = new AlertDialog.Builder(f.getContext());
        builder.setTitle(R.string.choose_picture);

        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals(f.getString(R.string.take_photo))) {
                if (ContextCompat.checkSelfPermission(f.requireContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    f.startActivityForResult(takePicture, 0);
                } else {
                    ActivityCompat.requestPermissions(f.requireActivity(), new String[]{Manifest.permission.CAMERA}, 123);
                }
            } else if (options[item].equals(f.getString(R.string.choose_photo))) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                f.startActivityForResult(intent, 1);
            }
        })
                .setNegativeButton(R.string.abort, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private String imgString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] imgBytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imgBytes, Base64.DEFAULT);
        }
        return null;
    }
}
