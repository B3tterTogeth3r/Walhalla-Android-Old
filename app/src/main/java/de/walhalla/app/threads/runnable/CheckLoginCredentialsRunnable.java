package de.walhalla.app.threads.runnable;

import android.os.Build;
import android.os.Process;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import de.walhalla.app.App;
import de.walhalla.app.MainActivity;
import de.walhalla.app.R;
import de.walhalla.app.User;
import de.walhalla.app.models.Person;

public class CheckLoginCredentialsRunnable implements Runnable {
    String TAG = "CheckLoginCredentialsRunnable";
    String username, password;
    private CheckLoginListener listener;

    public interface CheckLoginListener {
        void onDone();

        void onCheckLoginListenerStart();
    }

    public CheckLoginCredentialsRunnable(String username, String password) {
        this.listener = null;
        this.username = username;
        this.password = password;
    }

    public void setEndListener(CheckLoginListener listener) {
        this.listener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        listener.onCheckLoginListenerStart();
        Thread.currentThread().setName(TAG);
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Log.i("CheckLoginCredentials", "Check for login credentials");
        String link = "";//App.getContext().getString(R.string.link_login);
        try {
            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            String post_data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&"
                    + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            if (isNumeric(result.toString())) {
                System.out.println("The ID of the logged in user is: " + result);
                User.setData(new Person()/*TODO Find.Person(Integer.parseInt(result.toString()))*/, username);
                Snackbar.make(MainActivity.parentLayout, R.string.login_success, Snackbar.LENGTH_LONG).show();

            } else {
                User.logOut();
                Snackbar.make(MainActivity.parentLayout, R.string.login_error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.close, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setActionTextColor(App.getContext().getResources().getColor(R.color.colorPrimaryDark))
                        .show();

            }
            listener.onDone();
        } catch (IOException e) {
            e.printStackTrace();
            User.logOut();
            Snackbar.make(MainActivity.parentLayout, R.string.login_error, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.close, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .setActionTextColor(App.getContext().getResources().getColor(R.color.colorPrimaryDark))
                    .show();
            listener.onDone();
        }
    }


    static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
