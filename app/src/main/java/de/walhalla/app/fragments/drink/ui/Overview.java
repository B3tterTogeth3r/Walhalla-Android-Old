package de.walhalla.app.fragments.drink.ui;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.R;
import de.walhalla.app.User;
import de.walhalla.app.fragments.drink.Fragment;
import de.walhalla.app.utils.Variables;

public class Overview extends Fragment {
    public Overview() {
    }

    @NotNull
    public static TableLayout load() {
        TableLayout tableLayout = new TableLayout(f.getContext());
        TableRow rowTwo = new TableRow(f.getContext());
        TableRow rowThree = new TableRow(f.getContext());
        TableRow rowFour = new TableRow(f.getContext());
        TextView remaining = new TextView(f.getContext());
        TextView remainingAmount = new TextView(f.getContext());
        TextView total = new TextView(f.getContext());
        TextView totalAmount = new TextView(f.getContext());
        TextView place = new TextView(f.getContext());
        TextView position = new TextView(f.getContext());

        tableLayout.addView(rowTwo, params);
        tableLayout.addView(rowThree, params);
        tableLayout.addView(rowFour, params);

        rowTwo.addView(remaining);
        rowTwo.addView(remainingAmount);
        rowThree.addView(total);
        rowThree.addView(totalAmount);
        rowFour.addView(place);
        rowFour.addView(position);

        remaining.setText(R.string.profile_balance);
        total.setText(R.string.drink_total);
        String amount = String.format(Variables.LOCALE, "%.2f", User.getData().getBalance()) + " €";
        remainingAmount.setText(amount);
        int numberOfDrinks = 0;//TODO Find.AmountOfDrinksInCurrent(User.getData().getId());
        totalAmount.setText(String.valueOf(numberOfDrinks));

        place.setText(R.string.drink_place);
        //TODO Find place in current semester


        return tableLayout;
    }
}
