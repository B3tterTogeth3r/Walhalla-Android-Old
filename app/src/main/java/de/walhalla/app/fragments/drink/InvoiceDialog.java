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

import de.walhalla.app.R;
import de.walhalla.app.interfaces.InvoiceListener;
import de.walhalla.app.models.Drink;

@SuppressLint("InflateParams")
public class InvoiceDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener,
        InvoiceListener, DialogInterface.OnDismissListener {
    private static final String TAG = "InvoiceDialog";
    private final String person;
    private DialogInterface dialog;

    public InvoiceDialog(Context context, @NotNull String person) {
        super(context);
        setTitle(person);
        this.person = person;//Find.PersonByFullName(person);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment, null);
        setView(view);
        LinearLayout layout = view.findViewById(R.id.fragment_container);

        //only show the price for that person
        //TODO final ArrayList<DrinkPrice> drinkPrices = Database.getCurrentBeerPrice();

        final ListView listview = new ListView(getContext());
        //TODO listview.setAdapter(new InvoiceRow(getContext(), drinkPrices, this));
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
    public void amountChangeListener(int amount, @NotNull Drink kind) {
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
