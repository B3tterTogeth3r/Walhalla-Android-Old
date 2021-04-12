package de.walhalla.app.dialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.interfaces.UploadListener;
import de.walhalla.app.models.Accounting;
import de.walhalla.app.utils.Variables;

public class CheckboxDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener,
        View.OnClickListener, UploadListener {
    private final static String TAG = "MultiPersonPickerDialog";
    public static String kind = "";
    private final Activity activity;
    private final Accounting backup;
    private final UploadListener listener;
    private final Bitmap bitmap = null;
    private final View myView;
    private EditText income, expense, add, purpose;
    private Button date, event;
    private ImageButton showImg;
    private Accounting current = null;
    private String[] saveForUpload;

    @SuppressLint("InflateParams")
    public CheckboxDialog(@NotNull Activity activity, @Nullable Accounting current, @NotNull String kind, UploadListener listener) {
        super(activity);
        this.activity = activity;
        this.listener = listener;
        CheckboxDialog.kind = kind;

        if (current != null) {
            this.current = current;
            backup = new Accounting(current.getId(), current.getDateString(),
                    current.getExpense(), current.getIncome(), current.getEvent(),
                    current.getPurpose(), current.getAdd(), current.getRecipe());
        } else {
            backup = new Accounting();
        }
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        myView = layoutInflater.inflate(R.layout.dialog_account, null);
        setView(myView);
        float scale = this.getContext().getResources().getDisplayMetrics().density;
        if (kind.equals(Variables.DETAILS)) {
            assert current != null;
            String title = getContext().getString(R.string.account_dialog_title_details) + current.getId();
            setTitle(title);

            TextView dateTV = myView.findViewById(R.id.account_dialog_date);
            TextView incomeTV = myView.findViewById(R.id.account_dialog_income);
            TextView expenseTV = myView.findViewById(R.id.account_dialog_expense);
            TextView eventTV = myView.findViewById(R.id.account_dialog_event_name);
            TextView addTV = myView.findViewById(R.id.account_dialog_add);
            TextView recipeNumberTV = myView.findViewById(R.id.account_dialog_purpose);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams((int) (144 * scale + 0.5f), (int) (144 * scale + 0.5f));
            showImg = myView.findViewById(R.id.account_dialog_recipe);
            showImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
            showImg.setLayoutParams(imageParams);

            dateTV.setText(current.getDateFormat());
            incomeTV.setText(current.getIncomeFormat());
            expenseTV.setText(current.getExpenseFormat());
            eventTV.setText(current.getEvent());
            if (current.getIncome() == 0) { //is an expense
                addTV.setVisibility(View.GONE);
                recipeNumberTV.setText(current.getPurpose());
                /*TODO
                 * Set image of the recipe, is an image is uploaded.
                 * otherwise set ability to add an image.
                 * If an image is set, it can be shown enlarged, while it is clicked on.
                 */
                if (current.getRecipe() != 0) {
                    //Download the image and show it
                    /*
                    String onlinePath = App.getContext().getString(R.string.link_recipe_path);
                    final String pictureName = Find.PictureName(current.getRecipe());

                    DownloadPictureQue.addEntry(new DownloadPicture(onlinePath, pictureName, showImg));
                    DownloadPictureQue.startAll();
                    showImg.setOnClickListener(this);*/
                    //TODO Set the above to firebase
                }
            } else {
                recipeNumberTV.setVisibility(View.GONE);
                showImg.setVisibility(View.GONE);
                addTV.setText(current.getAdd());
            }
            setPositiveButton(R.string.done, this);
            setNeutralButton(R.string.edit, this);
        }
        if (kind.equals(Variables.EDIT)) {
            assert current != null;
            String title = getContext().getString(R.string.account_dialog_title_edit) + current.getId();
            setTitle(title);
            LinearLayout linearLayout = myView.findViewById(R.id.account_dialog_linearLayout);
            linearLayout.removeAllViewsInLayout();
            date = new Button(getContext());
            income = new EditText(getContext()); // Number-input
            income.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            expense = new EditText(getContext()); // Number-input
            expense.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            event = new Button(getContext());
            purpose = new EditText(getContext());
            add = new EditText(getContext());
            showImg = new ImageButton(getContext());

            date.setOnClickListener(this);
            event.setOnClickListener(this);
            showImg.setOnClickListener(this);

            date.setText(current.getDateFormat());
            //Force 2 decimals
            income.setText(String.format(Variables.LOCALE, "%.2f", current.getIncome()));
            income.setKeyListener(DigitsKeyListener.getInstance("0123456789,"));
            income.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            income.setHint(R.string.account_dialog_income);
            //Force two decimals
            expense.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            expense.setText(String.format(Variables.LOCALE, "%.2f", current.getExpense()));
            expense.setKeyListener(DigitsKeyListener.getInstance("0123456789,"));
            expense.setHint(R.string.account_dialog_expense);
            event.setText(current.getEvent());
            purpose.setHint(R.string.account_dialog_purpose);
            purpose.setText(current.getPurpose());
            add.setHint(R.string.account_dialog_add);
            add.setText(current.getAdd());

            showImg.setImageResource(R.drawable.wappen_2017);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (144 * scale + 0.5f));
            showImg.setScaleType(ImageButton.ScaleType.FIT_CENTER);
            showImg.setLayoutParams(imageParams);

            linearLayout.addView(date);
            linearLayout.addView(income);
            linearLayout.addView(expense);
            linearLayout.addView(event);
            linearLayout.addView(purpose);
            linearLayout.addView(add);
            linearLayout.addView(showImg);

            setPositiveButton(R.string.send, this);
            setNeutralButton(R.string.delete, this);
            setNegativeButton(R.string.abort, this);
        }
        if (kind.equals(Variables.ADD)) {
            String title = getContext().getString(R.string.account_dialog_title_add);
            setTitle(title);
            LinearLayout linearLayout = myView.findViewById(R.id.account_dialog_linearLayout);
            linearLayout.removeAllViewsInLayout();
            date = new Button(getContext());
            income = new EditText(getContext()); //Set it a numeric field
            income.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            income.setKeyListener(DigitsKeyListener.getInstance("0123456789,"));
            expense = new EditText(getContext()); //Set it a numeric field
            expense.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            expense.setKeyListener(DigitsKeyListener.getInstance("0123456789,"));
            event = new Button(getContext());
            purpose = new EditText(getContext());
            add = new EditText(getContext());
            showImg = new ImageButton(getContext());

            date.setText(R.string.program_dialog_date);
            income.setHint(R.string.account_dialog_income);
            expense.setHint(R.string.account_dialog_expense);
            event.setText(R.string.program_dialog_title);
            purpose.setHint(R.string.account_dialog_purpose);
            add.setHint(R.string.account_dialog_add);
            showImg.setOnClickListener(this);

            date.setOnClickListener(this);
            event.setOnClickListener(this);
            showImg.setOnClickListener(this);

            linearLayout.addView(date);
            linearLayout.addView(income);
            linearLayout.addView(expense);
            linearLayout.addView(event);
            linearLayout.addView(add);
            linearLayout.addView(showImg);

            setPositiveButton(R.string.send, this);
            setNegativeButton(R.string.abort, this);
        }
        showImg.setVisibility(View.GONE);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //User clicked OK button
        switch (which) {
            case (AlertDialog.BUTTON_POSITIVE):
                //TODO Upload data
                if (kind.equals(Variables.EDIT)) {
                    /* compare:
                     * date, income, expense, event, purpose, add, recipe
                     * if any of them isn't the same, update the db
                     * else do nothing
                     */
                    float ex, in;
                    try {
                        ex = Float.parseFloat(String.valueOf(expense.getText()).replace(',', '.'));
                    } catch (NumberFormatException e) {
                        ex = 0;
                    }
                    try {
                        in = Float.parseFloat(String.valueOf(income.getText()).replace(',', '.'));
                    } catch (NumberFormatException e) {
                        in = 0;
                    }
                    backup.setExpense(ex);
                    backup.setIncome(in);
                    backup.setPurpose(String.valueOf(purpose.getText()));
                    backup.setAdd(String.valueOf(add.getText()));

                    if (backup.getId() == current.getId()) {
                        if ((!backup.getDateFormat().equals("") && backup.getDate() != current.getDate()) |
                                backup.getIncome() != current.getIncome() |
                                backup.getExpense() != current.getExpense() |
                                backup.getEvent() != current.getEvent() |
                                !backup.getPurpose().equals(current.getPurpose()) |
                                !backup.getAdd().equals(current.getAdd()) |
                                backup.getRecipe() != current.getRecipe()) {
                            //Edit online and local
                            //TODO Upload them to the firebase realtime database
                        }
                    }
                    Log.i(TAG, "Edit an existing one");
                } else if (kind.equals(Variables.ADD)) {
                    /* Just Upload and sync with local
                     * check if an image was selected and upload it
                     */
                    float ex, in;
                    try {
                        ex = Float.parseFloat(String.valueOf(expense.getText()).replace(',', '.'));
                    } catch (NumberFormatException e) {
                        ex = 0;
                    }
                    try {
                        in = Float.parseFloat(String.valueOf(income.getText()).replace(',', '.'));
                    } catch (NumberFormatException e) {
                        in = 0;
                    }
                    backup.setExpense(ex);
                    backup.setIncome(in);
                    backup.setPurpose(String.valueOf(purpose.getText()));
                    backup.setAdd(String.valueOf(add.getText()));
                    if (bitmap != null) {
                        Date currentTime = Calendar.getInstance().getTime();
                        //An image was choosen to go with the Entry
                        Log.i(TAG, "Later send this to the online database");
                        //Check, if image is successfully uploaded, than set uploadcheck to true
                        Locale locale = new Locale("de", "DE");
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", locale);
                        String datumNeu = format.format(currentTime);
                        String pictureName = "Recipe_" + datumNeu;
                        //UploadPictureRunnable pictureRunnable = new UploadPictureRunnable(this, pictureName, imgString(bitmap));
                        //pictureRunnable.execute();
                    } else {
                        //String[] payload;
                        //payload = new String[]{saveForUpload[0], saveForUpload[1], "0", ""};
                        //TODO Upload them to the firebase realtime database
                        //UploadToDatabaseRunnable databaseRunnable = new UploadToDatabaseRunnable(this, backup, Variables.ADD);

                    }
                    Log.i(TAG, "Add a new one");
                }
                break;
            case (AlertDialog.BUTTON_NEGATIVE):
                Log.i(TAG, "Negative button pressed");
                if (kind.equals(Variables.EDIT)) {
                    CheckboxDialog checkboxDialog = new CheckboxDialog(activity, current, Variables.DETAILS, listener);
                    checkboxDialog.show();
                }
                break;
            case (AlertDialog.BUTTON_NEUTRAL):
                if (kind.equals(Variables.DETAILS)) {
                    CheckboxDialog checkboxDialog = new CheckboxDialog(activity, current, Variables.EDIT, listener);
                    checkboxDialog.show();
                } else if (kind.equals(Variables.EDIT)) {
                    kind = Variables.DELETE;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.program_delete_title)
                            .setNegativeButton(R.string.abort, (dialog2, which2) -> {
                            })
                            .setPositiveButton(R.string.send, (dialog2, which2) -> {
                                //DELETE entry
                                //TODO Upload them to the firebase realtime database
                                //UploadToDatabaseRunnable databaseRunnable = new UploadToDatabaseRunnable(this, current, Variables.DELETE);

                            });
                    builder.create().show();
                }
                Log.i(TAG, "Neutral button pressed");
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == showImg) {
            if (kind.equals(Variables.DETAILS)) {
                PictureZoomDialog dialog = new PictureZoomDialog(getContext(), showImg.getDrawable());
                dialog.show();
            }
            if (kind.equals(Variables.ADD) | kind.equals(Variables.EDIT)) {
                selectImage();
            }
        }
        if (v == date) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        String month = String.valueOf(monthOfYear + 1);
                        if (Integer.parseInt(month) < 10) {
                            month = "0" + month;
                        }
                        String day = String.valueOf(dayOfMonth);
                        if (Integer.parseInt(day) < 10) {
                            day = "0" + day;
                        }
                        String result = day + "." + month + "." + year;
                        date.setText(result);
                        result = year + "-" + month + "-" + day;
                        //Save result in backup
                        backup.setDate(result);
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == event) {
            Log.d(TAG, event.getText().toString());
            //Open numberpicker with the list an event can be accounted for.
        }
    }

    @Override
    public void onPictureUploadDone(boolean successful, String idOfPicture) {
        if (successful) {
            backup.setRecipe(Integer.parseInt(idOfPicture));
            //TODO Upload them to the firebase realtime database
            //UploadToDatabaseRunnable update = new UploadToDatabaseRunnable(listener, backup, Variables.EDIT);
        } else {
            Log.e(TAG, "Picture upload failed.");
        }
    }

    private void selectImage() {
        final CharSequence[] options = {App.getContext().getResources().getString(R.string.take_photo),
                App.getContext().getResources().getString(R.string.choose_photo),
                App.getContext().getResources().getString(R.string.abort)};
        //TODO Open Dialog
        try {
            //SelectImageDialog dialog = new SelectImageDialog(bitmap, showImg);
            //FragmentManager fm = null;
            //FragmentManager fm = new FragmentManager(){};
            //Fragment fragment = FragmentManager.findFragment(MainActivity.parentLayout);
            //fm.getFragment(this, "");
            //dialog.show(fragment.getParentFragmentManager(), null);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setItems(options, (dialog, item) -> {
                if (options[item].equals(App.getContext().getString(R.string.take_photo))) {
                    //TODO Start own things which can do that
                    if (ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        SelectImageDialog fragment = new SelectImageDialog(bitmap, showImg, takePicture, 0);
                        fragment.startActivityForResult(takePicture, 0);
                    } else {
                        ActivityCompat.requestPermissions((Activity) App.getContext(), new String[]{Manifest.permission.CAMERA}, 123);
                    }
                } else if (options[item].equals(App.getContext().getString(R.string.choose_photo))) {
                    //TODO Start own things which can do that
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    new SelectImageDialog(bitmap, showImg, intent, 1);
                } else if (options[item].equals(App.getContext().getString(R.string.abort))) {
                    dialog.dismiss();
                }
            });
            builder.setTitle(R.string.choose_picture);

            Dialog dialog = builder.create();
            dialog.show();
            /*

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
        }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onDatabaseUploadDone(boolean successful) {
        //download the database
        if (successful) {
            //TODO Create upload to firebase realtime database
        } else {
            Log.e(TAG, "Something went wrong with the upload");
        }
    }
}
