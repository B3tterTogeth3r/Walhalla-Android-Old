package de.walhalla.app.fragments.drink;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.FragmentTransaction;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import de.walhalla.app.R;
import de.walhalla.app.fragments.drink.ui.InvoiceRow;
import de.walhalla.app.interfaces.InvoiceListener;
import de.walhalla.app.interfaces.RunnableCompleteListener;
import de.walhalla.app.interfaces.UploadListener;
import de.walhalla.app.models.Drink;
import de.walhalla.app.models.DrinkPrice;
import de.walhalla.app.models.Person;
import de.walhalla.app.threads.NotifyingRunnable;
import de.walhalla.app.utils.Database;
import de.walhalla.app.utils.Find;

@SuppressLint("InflateParams")
public class InvoiceDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener,
        InvoiceListener, UploadListener, RunnableCompleteListener, DialogInterface.OnDismissListener {
    private static final String TAG = "InvoiceDialog";
    private final ArrayList<Drink> save = new ArrayList<>();
    private int i = 0;
    private DialogInterface dialog;
    private float totalCost = 0;
    private final Person person;

    public InvoiceDialog(Context context, @NotNull String person) {
        super(context);
        setTitle(person);
        this.person = Find.PersonByFullName(person);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment, null);
        setView(view);
        LinearLayout layout = view.findViewById(R.id.fragment_container);

        //only show the price for that person
        final ArrayList<DrinkPrice> drinkPrices = Database.getCurrentBeerPrice();

        final ListView listview = new ListView(getContext());
        listview.setAdapter(new InvoiceRow(getContext(), drinkPrices, this));
        listview.setClickable(false);
        layout.addView(listview);

        setNeutralButton(R.string.abort, this);
        setPositiveButton(R.string.send, this);
        setOnDismissListener(this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.dialog = dialog;
        switch (which) {
            case -1: //Positive Button
                //Read all the counter from the InvoiceRow and Upload the data
                if (save.size() != 0) {
                    //TODO Create upload to firebase realtime database
                    //UploadToDatabaseRunnable databaseRunnable = new UploadToDatabaseRunnable(this, save.get(0));

                    DrinkPrice price = Find.DrinkPrice(save.get(i).getKind());
                    float balance = person.getBalance() - (save.get(i).getAmount() * price.getPrice());
                    person.setBalance(balance);
                    //databaseRunnable = new UploadToDatabaseRunnable(null, person, Variables.EDIT);

                }
                break;
            case -2: //Negative Button
                Log.i(TAG, "-2 pressed");
                dialog.dismiss();
                break;
            case -3: //Neutral Button
                Log.i(TAG, "0 pressed");
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void amountChangeListener(int amount, @NotNull DrinkPrice kind) {
        //Find an object with the same kind
        //If there is none, add it
        //Otherwise: get position and replace amount
        //TODO Format data in the new way

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();

        //Drink drink = new Drink(0, dtf.format(now), person.getId(), amount, kind.getKind());
        int index = 0;
        boolean isIn = false;
        for (int i = 0; i < save.size(); i++) {
            if (save.get(i).getKind() == kind.getKind()) {
                isIn = true;
                index = i;
            }
        }
        if (isIn) {
            if (amount == 0) {
                save.remove(index);
            }
            //save.set(index, drink);
        } else {
            //save.add(drink);
        }
    }

    @Override
    public void onDatabaseUploadDone(boolean successful) {
        if (successful) {
            i++;
            if (i < this.save.size()) {
                //TODO Create upload to firebase realtime database
                //UploadToDatabaseRunnable databaseRunnable = new UploadToDatabaseRunnable(this, save.get(i));

                DrinkPrice price = Find.DrinkPrice(save.get(i).getKind());
                float balance = person.getBalance() - (save.get(i).getAmount() * price.getPrice());
                person.setBalance(balance);
                //databaseRunnable = new UploadToDatabaseRunnable(null, person, Variables.EDIT);

            } else {
                //TODO Create upload to Cloud FireStore
            }
        }
    }

    @Override
    public void notifyOfRunnableComplete(NotifyingRunnable runnable) {
        //TODO Send puch message to this person saing something like "Your account balance changed"
        dialog.dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        //Reload base-Fragment
        Fragment fragment = new Fragment();
        FragmentTransaction transaction = Fragment.f.getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
