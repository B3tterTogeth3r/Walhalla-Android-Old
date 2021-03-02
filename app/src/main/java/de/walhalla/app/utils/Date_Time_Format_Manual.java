package de.walhalla.app.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Date_Time_Format_Manual {

    // Format Dates from the online Database
    @NotNull
    public static String dob(@NotNull String date) {
        return date.substring(8, 10) + "." + date.substring(5, 7) + "." + date.substring(0, 4);
    }

    @NotNull
    public static String date(@NotNull String date) {
        return date.substring(8, 10) + "-" + date.substring(5, 7) + "-" + date.substring(0, 4);
    }

    @Nullable
    public static Date parseDate(String dd_MM_yyyy) {
        try {
            Locale locale = new Locale("de", "DE");
            return new SimpleDateFormat("dd-MM-yyyy", locale).parse(dd_MM_yyyy);
        } catch (ParseException e) {
            return null;
        }
    }

}
