package de.walhalla.app.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

import de.walhalla.app.models.Accounting;
import de.walhalla.app.models.News;
import de.walhalla.app.models.Person;
import de.walhalla.app.models.Semester;

public class Database {
    private final static String TAG = "models.Database";
    public static ArrayList<Accounting> cashboxArrayList;
    public static ArrayList<News> newsArrayList;
    public static ArrayList<Person> personArrayList;
    public static ArrayList<Semester> semesterArrayList;

    public static ArrayList<News> getNewsArrayList() {
        return newsArrayList;
    }

    public static ArrayList<Person> getPersonArrayList() {
        return personArrayList;
    }

    @Nullable
    public static ArrayList<Accounting> getCashboxArrayList(@NotNull Semester ChosenSemester) {
        ArrayList<Accounting> arrayList = new ArrayList<>();
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

    public static ArrayList<Semester> getSemesterArrayList() {
        return semesterArrayList;
    }

}