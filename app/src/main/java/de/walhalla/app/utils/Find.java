package de.walhalla.app.utils;

import android.util.Log;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import de.walhalla.app.App;
import de.walhalla.app.User;
import de.walhalla.app.models.Drink;
import de.walhalla.app.models.DrinkKind;
import de.walhalla.app.models.DrinkPrice;
import de.walhalla.app.models.Event;
import de.walhalla.app.models.Helper;
import de.walhalla.app.models.HelperKind;
import de.walhalla.app.models.Person;
import de.walhalla.app.models.Picture;
import de.walhalla.app.models.Semester;

public class Find {
    private static final String TAG = "Find";

    public static int PlaceInDrinks() {
        int position = 1;
        ArrayList<Person> allPersons = Database.getPersonArrayList();
        Person user = User.getData();
        int userDrinks = 0;//TODO AmountOfDrinksInCurrent(user.getId());
        for (Person onePerson : allPersons) {
            int otherDrinks = 0;//TODO AmountOfDrinksInCurrent(onePerson.getId());
            if (otherDrinks > userDrinks) {
                position = position + 1;
            }
        }
        return position;
    }

    public static int AmountOfDrinksInCurrent(int Person_ID) {
        ArrayList<Drink> drinks = AllDrinksOfOnInCurrent(Person_ID);
        int numberOfDrinks = 0;
        for (Drink drink : drinks) {
            numberOfDrinks = numberOfDrinks + drink.getAmount();
        }

        return numberOfDrinks;
    }

    @NotNull
    public static ArrayList<Drink> AllDrinksOfOnInCurrent(int Person_ID) {
        ArrayList<Drink> drinks = new ArrayList<>();
        ArrayList<Drink> allDrinks = AllDrinksOfOnePerson(Person_ID);
        if (allDrinks.size() != 0) {
            for (Drink ad : allDrinks) {
                if (ad.getDate_Value().after(App.getCurrentSemester().getBegin())) {
                    drinks.add(ad);
                }
            }
        }
        return drinks;
    }

    @NotNull
    public static ArrayList<Drink> AllDrinksOfOnePerson(int Person_ID) {
        ArrayList<Drink> drinks = new ArrayList<>();
        ArrayList<Drink> allDrinks = Database.getDrinkArrayList();
        if (allDrinks.size() != 0) {
            for (Drink ad : allDrinks) {
                if (ad.getWho() == Person_ID) {
                    drinks.add(ad);
                }
            }
        }
        return drinks;
    }

    @NotNull
    public static DrinkPrice DrinkPrice(int id) {
        return Database.getCurrentBeerPrice().get(id);
    }

    @NotNull
    public static Person PersonByFullName(String Name) {
        ArrayList<Person> personArrayList = Database.getPersonArrayList();
        int listSize = personArrayList.size();
        if (listSize != 0) {
            for (int i = 0; listSize > i; i++) {
                if (personArrayList.get(i).getFullName().equals(Name)) {
                    return personArrayList.get(i);
                }
            }
        }
        return new Person();
    }

    @NotNull
    public static String PictureName(int id) {
        ArrayList<Picture> all = Database.getPictureArrayList();
        String name = "";
        int listSize = all.size();
        try {
            for (int i = 0; i < listSize; i++) {
                if (all.get(i).getID() == id) {
                    name = all.get(i).getName();
                }
            }
        } catch (Exception ignored) {
        }
        return name;
    }

    public static Event Event(int id) {
        ArrayList<Event> events = Database.getEventArrayList();
        int listSize = events.size();
        for (int i = 0; i < listSize; i++) {
            //TODO if (events.get(i).getId() == id)
            //    return events.get(i);
        }
        return new Event();
    }

    @NotNull
    public static ArrayList<ArrayList<Helper>> tasks(@NotNull ArrayList<Helper> helper) {
        ArrayList<ArrayList<Helper>> tasks = new ArrayList<>();
        ArrayList<HelperKind> kind = Database.getHelperKindArrayList();
        helper.sort((o1, o2) -> Integer.compare(o1.getKind(), o2.getKind()));
        int kindSize = kind.size();
        for (int i = 0; i < kindSize; i++) {
            ArrayList<Helper> helpers = new ArrayList<>();
            int helperSize = helper.size();
            for (int j = 0; j < helperSize; j++) {
                if (kind.get(i).getId() == helper.get(j).getKind()) {
                    helpers.add(helper.get(j));
                }
            }
            tasks.add(helpers);
        }
        return tasks;
    }

    public static Semester Semester(int id) {
        return Variables.SEMESTER_ARRAY_LIST.get(id);
    }

    public static String DrinkName(int id) {
        ArrayList<DrinkKind> kinds = Database.getDrinkKindArrayList();
        for (DrinkKind k : kinds) {
            if (k.getId() == id) {
                return k.getName();
            }
        }
        return "";
    }

    public static void PersonByUID(String uid, String mail) {
        final Person[] person = {new Person()};
        Variables.Firebase.FIRESTORE.collection("User")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Person p = document.toObject(Person.class);
                            /*Log.e(TAG, "Reference: " + document.getReference() + " -> " +
                                    "\nId: " + onlineId + " ?=? " +
                                    "\nuid: " + uid + " => " +
                                    "\nData: " + document.getData() + " -> " +
                                    "\nPersonID: " + personId);
                             */
                            if (p.getUid().equals(uid)) {
                                User.setData(new Person(), mail);
                            }
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    @Nullable
    public static Person PersonByEmail(String email) {
        ArrayList<Person> persons = Database.getPersonArrayList();
        for (Person p : persons) {
            if (p.getMail().equals(email))
                return p;
        }
        return null;
    }
}
