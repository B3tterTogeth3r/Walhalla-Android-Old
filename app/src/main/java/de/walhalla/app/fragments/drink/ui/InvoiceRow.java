package de.walhalla.app.fragments.drink.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.interfaces.InvoiceListener;
import de.walhalla.app.models.DrinkPrice;
import de.walhalla.app.utils.Border;
import de.walhalla.app.utils.Find;

public class InvoiceRow extends ArrayAdapter<DrinkPrice> {
    protected static final String TAG = "InvoiceRow";
    private final Context context;
    private final ArrayList<DrinkPrice> drinkPrices;
    protected static final float scale = App.getContext().getResources().getDisplayMetrics().density;
    protected static final int defaultMargin = (int) scale * 15;
    private final InvoiceListener listener;

    public InvoiceRow(Context context, ArrayList<DrinkPrice> drinkPrices, InvoiceListener listener) {
        super(context, R.layout.item_beer_invoice, drinkPrices);
        this.context = context;
        this.drinkPrices = drinkPrices;
        this.listener = listener;
    }

    @NotNull
    @Override
    @SuppressWarnings("ViewHolder")
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        final int[] count = {0};
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View rowView = inflater.inflate(R.layout.item_beer_invoice, parent, false);

        TextView title = rowView.findViewById(R.id.item_kind);
        RelativeLayout minus10 = rowView.findViewById(R.id.minus10);
        RelativeLayout minus1 = rowView.findViewById(R.id.minus1);
        RelativeLayout add1 = rowView.findViewById(R.id.plus1);
        RelativeLayout add10 = rowView.findViewById(R.id.plus10);
        TextView counter = rowView.findViewById(R.id.counter);

        rowView.setClickable(false);

        title.setTextSize((int) (5 * scale + 0.25f));

        RelativeLayout.LayoutParams minus10Params = (RelativeLayout.LayoutParams) minus10.getLayoutParams();
        minus10Params.setMargins(defaultMargin, defaultMargin, defaultMargin, defaultMargin);
        minus10.setLayoutParams(minus10Params);
        minus10.setBackground(Border.getBlack(getContext().getColor(R.color.background), 2, 2, 2, 2));

        RelativeLayout.LayoutParams minus1Params = (RelativeLayout.LayoutParams) minus1.getLayoutParams();
        minus1Params.setMargins(defaultMargin, defaultMargin, defaultMargin, defaultMargin);
        minus1.setLayoutParams(minus1Params);
        minus1.setBackground(Border.getBlack(getContext().getColor(R.color.background), 2, 2, 2, 2));

        RelativeLayout.LayoutParams add1Params = (RelativeLayout.LayoutParams) add1.getLayoutParams();
        add1Params.setMargins(defaultMargin, defaultMargin, defaultMargin, defaultMargin);
        add1.setLayoutParams(add1Params);
        add1.setBackground(Border.getBlack(getContext().getColor(R.color.background), 2, 2, 2, 2));

        RelativeLayout.LayoutParams add10Params = (RelativeLayout.LayoutParams) add10.getLayoutParams();
        add10Params.setMargins(defaultMargin, defaultMargin, defaultMargin, defaultMargin);
        add10.setLayoutParams(add10Params);
        add10.setBackground(Border.getBlack(getContext().getColor(R.color.background), 2, 2, 2, 2));

        RelativeLayout.LayoutParams contentParams = (RelativeLayout.LayoutParams) counter.getLayoutParams();
        contentParams.setMargins(defaultMargin, defaultMargin, defaultMargin, defaultMargin);
        counter.setLayoutParams(contentParams);
        counter.setBackground(Border.getBlack(getContext().getColor(R.color.background), 2, 2, 2, 2));
        counter.setWidth(defaultMargin * 5);

        minus10.setOnClickListener(v -> {
            if (count[0] >= 10) {
                count[0] = count[0] - 10;
            } else {
                count[0] = 0;
            }
            counter.setText(String.valueOf(count[0]));
            listener.amountChangeListener(count[0], drinkPrices.get(position));
        });

        minus1.setOnClickListener(v -> {
            if (count[0] > 0) {
                count[0] = count[0] - 1;
                counter.setText(String.valueOf(count[0]));
            }
            listener.amountChangeListener(count[0], drinkPrices.get(position));
        });

        add1.setOnClickListener(v -> {
            count[0] = count[0] + 1;
            counter.setText(String.valueOf(count[0]));
            listener.amountChangeListener(count[0], drinkPrices.get(position));
        });

        add10.setOnClickListener(v -> {
            count[0] = count[0] + 10;
            counter.setText(String.valueOf(count[0]));
            listener.amountChangeListener(count[0], drinkPrices.get(position));
        });

        String helper = Find.DrinkName(drinkPrices.get(position).getKind());
        title.setText(helper);

        return rowView;
    }
}
