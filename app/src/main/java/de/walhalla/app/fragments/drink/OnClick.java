package de.walhalla.app.fragments.drink;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app.R;
import de.walhalla.app.fragments.drink.search.SearchModel;
import de.walhalla.app.fragments.drink.search.SearchPersonResult;
import de.walhalla.app.models.Person;
import de.walhalla.app.utils.Database;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;

@SuppressWarnings("ALL")
public class OnClick extends Fragment implements View.OnClickListener {
    private final String title = f.getString(R.string.search);
    private final String results = f.getString(R.string.search);

    public OnClick() {
    }

    @Override
    public void onClick(View v) {
        if (v == price) {
            Snackbar.make(f.requireView(), "Show/Change prices of all available drinks.", Snackbar.LENGTH_SHORT).show();
        }
        if (v == invoice) {
            new SimpleSearchDialogCompat(f.getActivity(), title, results, null, initData(), new SearchPersonResult(v)).show();
        }
        if (v == payment) {
            new SimpleSearchDialogCompat(f.getActivity(), title, results, null, initData(), new SearchPersonResult(v)).show();
        }
    }

    @NotNull
    private ArrayList<SearchModel> initData() {
        ArrayList<SearchModel> items = new ArrayList<>();
        ArrayList<Person> activePersons = Database.getPersonArrayList();
        //Replace admin with "Couleur"
        Person newPerson = new Person();
        newPerson.setFirst_Name("Couleur");
        newPerson.setId("-1");
        newPerson.setRank("Gast");
        newPerson.setJoined(2);
        activePersons.set(0, newPerson);
        for (Person p : activePersons) {
            items.add(new SearchModel(p.getFullName()));
        }

        return items;
    }
}
