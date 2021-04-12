package de.walhalla.app.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import de.walhalla.app.MainActivity;
import de.walhalla.app.User;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.models.Person;
import de.walhalla.app.utils.Variables;

public class CustomAuthListener extends Firebase.Auth {

    @Override
    void changer(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Variables.Firebase.FIRESTORE
                    .collection("Person")
                    .whereEqualTo("uid", user.getUid())
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if (!snapshot.isEmpty()) {
                                try {
                                    List<Person> p = snapshot.toObjects(Person.class);
                                    User.setData(p.get(0), user.getUid());
                                    MainActivity.authChange.onAuthChange();
                                    CustomFragment.authChange.onAuthChange();
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    });
        } else {
            try {
                MainActivity.authChange.onAuthChange();
                CustomFragment.authChange.onAuthChange();
            } catch (Exception ignored) {
            }
        }
    }

    public interface sendMain {
        void onAuthChange();
    }
}
