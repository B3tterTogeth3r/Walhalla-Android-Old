package de.walhalla.app.fragments.drink.ui;

import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.R;
import de.walhalla.app.fragments.drink.Fragment;

public class Title extends Fragment {
    public Title() {
    }

    @NotNull
    public static TableLayout load() {
        TableLayout titleTL = new TableLayout(f.getContext());
        TableRow titleRow = new TableRow(f.getContext());
        titleTL.addView(titleRow);
        RelativeLayout topRow = new RelativeLayout(f.getContext());
        titleRow.addView(topRow, new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, //Width
                TableRow.LayoutParams.MATCH_PARENT, //Height
                1.0f
        ));

        title = new TextView(f.getContext());
        price = new ImageButton(f.getContext());
        invoice = new ImageButton(f.getContext());
        payment = new ImageButton(f.getContext());

        title.setId(R.id.beer_title);
        price.setId(R.id.beer_price);
        invoice.setId(R.id.beer_invoice);
        payment.setId(R.id.beer_payment);

        topRow.addView(title);
        topRow.addView(payment);
        topRow.addView(invoice);
        topRow.addView(price);

        title.setTextSize((int) (10 * scale + 0.75f));
        price.setImageResource(R.drawable.ic_euro);
        invoice.setImageResource(R.drawable.ic_request_quote);
        payment.setImageResource(R.drawable.ic_payment);

        price.setBackgroundColor(f.getResources().getColor(R.color.background, null));
        invoice.setBackgroundColor(f.getResources().getColor(R.color.background, null));
        payment.setBackgroundColor(f.getResources().getColor(R.color.background, null));

        RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) title.getLayoutParams();
        titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        titleParams.setMargins(defaultMargin, defaultMargin, defaultMargin, defaultMargin);
        title.setLayoutParams(titleParams);

        RelativeLayout.LayoutParams priceParams = (RelativeLayout.LayoutParams) price.getLayoutParams();
        priceParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        priceParams.setMargins(defaultMargin, defaultMargin, defaultMargin, defaultMargin);
        price.setLayoutParams(priceParams);

        RelativeLayout.LayoutParams invoiceParams = (RelativeLayout.LayoutParams) invoice.getLayoutParams();
        invoiceParams.addRule(RelativeLayout.LEFT_OF, price.getId());
        invoiceParams.setMargins(defaultMargin, defaultMargin, defaultMargin, defaultMargin);
        invoice.setLayoutParams(invoiceParams);

        RelativeLayout.LayoutParams paymentParams = (RelativeLayout.LayoutParams) payment.getLayoutParams();
        paymentParams.addRule(RelativeLayout.LEFT_OF, invoice.getId());
        paymentParams.setMargins(defaultMargin, defaultMargin, defaultMargin, defaultMargin);
        payment.setLayoutParams(paymentParams);

        title.setText(R.string.menu_beer);

        return titleTL;
    }
}
