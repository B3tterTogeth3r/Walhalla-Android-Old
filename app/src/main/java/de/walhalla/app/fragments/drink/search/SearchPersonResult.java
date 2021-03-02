package de.walhalla.app.fragments.drink.search;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.fragments.drink.Fragment;
import de.walhalla.app.fragments.drink.InvoiceDialog;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;

public class SearchPersonResult extends Fragment implements SearchResultListener<Searchable> {
    private final View view;

    public SearchPersonResult(View view) {
        this.view = view;
    }

    @Override
    public void onSelected(@NotNull BaseSearchDialogCompat dialog, @NotNull Searchable item, int position) {
        dialog.dismiss();

        if (view == invoice) {
            InvoiceDialog invoiceDialog = new InvoiceDialog(f.getContext(), item.getTitle());
            invoiceDialog.show();
            Snackbar.make(f.requireView(), "A new invoice is being added to " + item.getTitle(), Snackbar.LENGTH_SHORT).show();
        }
        if (view == payment) {
            Snackbar.make(f.requireView(), item.getTitle() + " wants to load something into his account", Snackbar.LENGTH_SHORT).show();
        }
    }
}
