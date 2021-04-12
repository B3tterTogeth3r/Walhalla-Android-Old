package de.walhalla.app.firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.utils.Variables;

public class Firebase {
    private final static String TAG = "FirebaseDownloader";

    @NotNull
    @Contract(pure = true)
    public static Runnable downloadImage(String reference, ImageView imageView) {
        return () -> {
            StorageReference image = FirebaseStorage.getInstance().getReference(reference);
            image.getBytes(Variables.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(addWatermark(bmp));
            }).addOnFailureListener(e ->
                    Log.e(TAG, "image download unsuccessful", e));
        };
    }

    @NotNull
    @Contract(pure = true)
    public static Runnable downloadImage(String reference, CircleImageView imageView) {
        return () -> {
            StorageReference image = FirebaseStorage.getInstance().getReference(reference);
            image.getBytes(Variables.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                try {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap imageBitMap = addWatermark(bmp);
                    imageView.setImageBitmap(imageBitMap);
                } catch (Exception ignored) {
                }
            }).addOnFailureListener(e ->
                    Log.e(TAG, "image download unsuccessful", e));
        };
    }

    @NotNull
    @Contract(pure = true)
    public static Runnable downloadImage(String reference, ImageButton imageView) {
        return () -> {
            StorageReference image = FirebaseStorage.getInstance().getReference(reference);
            image.getBytes(Variables.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(addWatermark(bmp));
            }).addOnFailureListener(e ->
                    Log.e(TAG, "image download unsuccessful", e));
        };
    }

    public static Bitmap addWatermark(@NotNull Bitmap source) {
        float ratio = 0.3f;
        Canvas canvas;
        Paint paint;
        Bitmap bmp;
        Matrix matrix;
        RectF r;

        Bitmap watermark = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.wappen_zirkel);

        int width, height;
        float scale;

        width = source.getWidth();
        height = source.getHeight();

        // Create the new bitmap
        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);

        // Copy the original bitmap into the new one
        canvas = new Canvas(bmp);
        canvas.drawBitmap(source, 0, 0, paint);

        // Scale the watermark to be approximately to the ratio given of the source image height
        scale = ((float) height * ratio) / (float) watermark.getHeight();

        // Create the matrix
        matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Determine the post-scaled size of the watermark
        r = new RectF(0, 0, watermark.getWidth(), watermark.getHeight());
        matrix.mapRect(r);

        // Move the watermark to the bottom right corner
        matrix.postTranslate(width - r.width(), height - r.height());

        // Draw the watermark
        canvas.drawBitmap(watermark, matrix, paint);

        return bmp;
    }

    public interface Chargen {
        void currentChargen();
    }

    public interface Event {
        void oneSemester(int semester_id);
    }

    public interface board {
        void student(int semester_id);

        void philister(int semester_id);
    }

    public abstract static class Auth implements FirebaseAuth.AuthStateListener, FirebaseAuth.IdTokenListener {

        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            changer(firebaseAuth);
        }

        @Override
        public void onIdTokenChanged(@NonNull FirebaseAuth firebaseAuth) {
            //changer(firebaseAuth);
        }

        abstract void changer(@NonNull FirebaseAuth firebaseAuth);
    }
}
