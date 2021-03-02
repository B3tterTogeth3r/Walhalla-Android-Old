package de.walhalla.app.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

import de.walhalla.app.App;
import de.walhalla.app.models.AllEvents;
import de.walhalla.app.models.Cashbox;
import de.walhalla.app.models.Chargen;
import de.walhalla.app.models.Data;
import de.walhalla.app.models.Drink;
import de.walhalla.app.models.DrinkKind;
import de.walhalla.app.models.DrinkPrice;
import de.walhalla.app.models.Drive;
import de.walhalla.app.models.Event;
import de.walhalla.app.models.Helper;
import de.walhalla.app.models.HelperKind;
import de.walhalla.app.models.Meeting;
import de.walhalla.app.models.News;
import de.walhalla.app.models.Person;
import de.walhalla.app.models.Picture;
import de.walhalla.app.models.Rank;
import de.walhalla.app.models.Semester;

public class Database {
    static String TAG = "models.Database";
    public static ArrayList<Rank> rankArrayList;
    public static ArrayList<AllEvents> allEventsArrayList;
    public static ArrayList<Drink> drinkArrayList;
    public static ArrayList<DrinkPrice> drinkPriceArrayList;
    public static ArrayList<DrinkKind> drinkKindArrayList;
    public static ArrayList<Cashbox> cashboxArrayList;
    public static ArrayList<Chargen> chargenArrayList;
    public static ArrayList<Data> dataArrayList;
    public static ArrayList<Drive> driveArrayList;
    public static ArrayList<Event> eventArrayList;
    public static ArrayList<Helper> helperArrayList;
    public static ArrayList<Meeting> meetingArrayList;
    public static ArrayList<News> newsArrayList;
    public static ArrayList<Person> personArrayList;
    public static ArrayList<Picture> pictureArrayList;
    public static ArrayList<HelperKind> helperKindArrayList;
    public static ArrayList<Semester> semesterArrayList;

    public Database() {
    }

    public static ArrayList<Rank> getRankArrayList() {
        return rankArrayList;
    }

    public static ArrayList<AllEvents> getAllEventsArrayList() {
        return allEventsArrayList;
    }

    public static ArrayList<Drink> getDrinkArrayList() {
        return drinkArrayList;
    }

    public static ArrayList<DrinkPrice> getDrinkPriceArrayList() {
        return drinkPriceArrayList;
    }

    public static ArrayList<DrinkKind> getDrinkKindArrayList() {
        return drinkKindArrayList;
    }

    @NotNull
    public static ArrayList<DrinkPrice> getCurrentBeerPrice() {
        ArrayList<DrinkPrice> price = new ArrayList<>();
        int listSize = drinkPriceArrayList.size();
        price.sort((o1, o2) -> Integer.compare(o2.getFrom(), o1.getFrom()));
        for (int i = 0; i < listSize; i++) {
            if (drinkPriceArrayList.get(i).getEk() == 0) {
                price.add(drinkPriceArrayList.get(i));
                //Log.i(TAG, drinkPriceArrayList.get(i).getFrom() + " in row " + i);
            }
        }
        price.sort((o1, o2) -> Integer.compare(o1.getKind(), o2.getKind()));
        ArrayList<DrinkKind> kinds = drinkKindArrayList;
        ArrayList<DrinkPrice> rest = new ArrayList<>();

        int kindsSize = kinds.size();
        for (int i = 0; i < kindsSize; i++) {
            ArrayList<DrinkPrice> drinkPrices = new ArrayList<>();//TODO Find.justCurrentPrices(price, i);
            drinkPrices.sort((o1, o2) -> Integer.compare(o2.getFrom(), o1.getFrom()));
            if (drinkPrices.size() > 1) {
                int curSem = App.getCurrentSemester().getID();
                if (curSem <= drinkPrices.get(0).getOver() + drinkPrices.get(0).getFrom()) {
                    rest.add(drinkPrices.get(0));
                } else {
                    rest.add(drinkPrices.get(1));
                }
                //Log.i(TAG, drinkPrices.get(0).getID() + " <- 0 | 1 -> " + drinkPrices.get(1).getID());
                //Log.i(TAG, drinkPrices.get(0).getOver() + " <- 0 | 1 -> " + drinkPrices.get(1).getOver());
            }
            if (drinkPrices.size() == 1) {
                rest.add(drinkPrices.get(0));
            }
        }
        //Log.i(TAG, "kindsize: " + kindsSize + " price.size(): " + price.size() + " listSize: " + listSize);


        return rest;
    }

    public static ArrayList<Cashbox> getCashboxArrayList() {
        return cashboxArrayList;
    }

    public static ArrayList<Chargen> getChargenArrayList() {
        return chargenArrayList;
    }

    public static ArrayList<Data> getDataArrayList() {
        return dataArrayList;
    }

    public static ArrayList<Drive> getDriveArrayList() {
        return driveArrayList;
    }

    public static ArrayList<Event> getEventArrayList() {
        return eventArrayList;
    }

    public static ArrayList<Helper> getHelperArrayList() {
        return helperArrayList;
    }

    public static ArrayList<Meeting> getMeetingArrayList() {
        return meetingArrayList;
    }

    public static ArrayList<News> getNewsArrayList() {
        return newsArrayList;
    }

    public static ArrayList<Person> getPersonArrayList() {
        return personArrayList;
    }

    @NotNull
    public static ArrayList<Person> getAktiveMembersArrayList() {
        ArrayList<Person> arrayList = new ArrayList<>();
        for (Person total : personArrayList) {
            if (total.getRank().equals("Aktiver"))
                arrayList.add(total);
        }
        return arrayList;
    }

    @Nullable
    public static ArrayList<Cashbox> getCashboxArrayList(@NotNull Semester ChosenSemester) {
        ArrayList<Cashbox> arrayList = new ArrayList<>();
        if (cashboxArrayList != null) {
            int size = cashboxArrayList.size();
            for (int i = 0; i < size; i++) {
                if (Objects.requireNonNull(cashboxArrayList.get(i).getDate()).after(ChosenSemester.getBegin()) &&
                        Objects.requireNonNull(cashboxArrayList.get(i).getDate()).before(ChosenSemester.getEnd())) {
                    arrayList.add(cashboxArrayList.get(i));
                }
            }
            //Sort ArrayList by Date
            arrayList.sort((o1, o2) -> Objects.requireNonNull(o1.getDate()).compareTo(o2.getDate()));

            return arrayList;
        } else {
            return null;
        }
    }

    public static ArrayList<Picture> getPictureArrayList() {
        return pictureArrayList;
    }

    public static ArrayList<Semester> getSemesterArrayList() {
        return semesterArrayList;
    }

    public static ArrayList<HelperKind> getHelperKindArrayList() {
        return helperKindArrayList;
    }

}