package de.walhalla.app.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import de.walhalla.app.fragments.news.Fragment;
import de.walhalla.app.models.News;
import de.walhalla.app.utils.Database;
import de.walhalla.app.utils.Variables;

public class NewsArrayList {
    public NewsArrayList() {
        Database.newsArrayList = new ArrayList<>();
        DatabaseReference databaseReference = Variables.Firebase.Reference.NEWS;
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    News load = snapshot.getValue(News.class);
                    load.setId(Integer.parseInt(snapshot.getKey()));
                    Database.newsArrayList.add(load);
                    try {
                        Fragment.listener.newsChanged();
                    } catch (Exception ignored) {
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    News load = snapshot.getValue(News.class);
                    load.setId(Integer.parseInt(snapshot.getKey()));
                    int size = Database.newsArrayList.size();
                    for (int i = 0; i < size; i++) {
                        if (load.getId() == Database.newsArrayList.get(i).getId()) {
                            Database.newsArrayList.set(i, load);
                        }
                    }
                    try {
                        Fragment.listener.newsChanged();
                    } catch (Exception ignored) {
                    }
                    Log.i("newsArrayList", "entry no. " + load.getId() + " got changed");
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    News load = snapshot.getValue(News.class);
                    load.setId(Integer.parseInt(snapshot.getKey()));
                    int size = Database.newsArrayList.size();
                    int findRemove = -1;
                    for (int i = 0; i < size; i++) {
                        if (load.getId() == Database.newsArrayList.get(i).getId()) {
                            findRemove = i;
                        }
                    }
                    if (findRemove != -1) {
                        Database.newsArrayList.remove(findRemove);
                    }
                    try {
                        Fragment.listener.newsChanged();
                    } catch (Exception ignored) {
                    }
                    Log.i("newsArrayList", "entry no. " + load.getId() + " got deleted");
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
