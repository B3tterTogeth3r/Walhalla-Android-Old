package de.walhalla.app.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

public class Firebase {
    private final static String TAG = "Firebase";

    public interface Chargen {
        void currentChargen();
    }

    public interface Event {
        void oneSemester(int semester_id);
    }

    public interface board {
        void student(int semester_id);

        void philister(int semester_id);
    }

    public abstract static class Auth implements FirebaseAuth.AuthStateListener, FirebaseAuth.IdTokenListener {

        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            changer(firebaseAuth);
        }

        @Override
        public void onIdTokenChanged(@NonNull FirebaseAuth firebaseAuth) {
            //changer(firebaseAuth);
        }

        abstract void changer(@NonNull FirebaseAuth firebaseAuth);
    }
}
