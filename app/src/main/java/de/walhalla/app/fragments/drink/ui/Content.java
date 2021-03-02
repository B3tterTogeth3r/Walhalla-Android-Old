package de.walhalla.app.fragments.drink.ui;

import android.annotation.SuppressLint;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app.R;
import de.walhalla.app.User;
import de.walhalla.app.fragments.drink.Fragment;
import de.walhalla.app.models.Person;
import de.walhalla.app.utils.Database;

@SuppressLint("StaticFieldLeak")
public class Content extends Fragment {
    private static TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1.0f);

    public Content() {
        params.setMargins(30, 0, 0, 0);
        rowParams.setMargins(30, 0, 0, 0);
    }

    @NotNull
    public static TableLayout load() {
        TableLayout tableLayout = new TableLayout(f.getContext());
        if (User.hasCharge()) {
            //Show all people with a balance below 0
            tableLayout.addView(hasCharge());
        }
        //TODO Fix that
        /*ArrayList<Drink> drinks = Find.AllDrinksOfOnInCurrent(User.getData().getId());
        for (Drink d : drinks) {
            TableRow row = new TableRow(f.getContext());
            TextView amount = new TextView(f.getContext());
            TextView name = new TextView(f.getContext());

            String string = Find.DrinkName(d.getKind());
            name.setText(string);
            amount.setText(String.valueOf(d.getAmount()));

            row.addView(amount, rowParams);
            row.addView(name, rowParams);
            tableLayout.addView(row, params);
        }
         */

        return tableLayout;
    }

    @NotNull
    private static TableLayout hasCharge() {
        TableLayout tableLayout = new TableLayout(f.getContext());
        tableLayout.setPadding(0, 5, 0, 5);
        ArrayList<Person> allPersons = Database.getPersonArrayList();
        TableRow row = new TableRow(f.getContext());
        TextView name = new TextView(f.getContext());
        TextView balance = new TextView(f.getContext());

        name.setText(R.string.drink_explain_extra);
        tableLayout.addView(name, params);

        name = new TextView(f.getContext());
        name.setText(R.string.profile_name);
        balance.setText(R.string.profile_balance);

        row.addView(name, rowParams);
        row.addView(balance, rowParams);
        tableLayout.addView(row, params);

        for (Person p : allPersons) {
            if (p.getBalance() < 0) {
                row = new TableRow(f.getContext());
                name = new TextView(f.getContext());
                balance = new TextView(f.getContext());

                name.setText(p.getFullName());
                String string = p.getBalance() + " €";
                balance.setText(string);

                row.addView(name, rowParams);
                row.addView(balance, rowParams);
                tableLayout.addView(row, params);
            }
        }

        return tableLayout;
    }

}
