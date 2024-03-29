package de.walhalla.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app.dialog.LoginDialog;
import de.walhalla.app.firebase.CustomAuthListener;
import de.walhalla.app.firebase.Firebase;
import de.walhalla.app.fragments.BalanceFragment;
import de.walhalla.app.fragments.Sites;
import de.walhalla.app.fragments.addNew.NewSemesterDialog;
import de.walhalla.app.fragments.home.Fragment;
import de.walhalla.app.models.SocialMedia;
import de.walhalla.app.utils.Variables;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        CustomAuthListener.sendMain, Sites.start {
    private final static String TAG = "MainActivity";
    public static CustomAuthListener.sendMain authChange;
    public static View parentLayout;
    public static Sites.start listener;
    private final ArrayList<SocialMedia> webLinks = new ArrayList<>();
    private MenuItem lastItem;
    private DrawerLayout drawerlayout;
    private AppBarConfiguration appBarConfiguration;
    private boolean doubleBackToExitPressedOnce = false;
    private NavigationView navigationView;
    private ListenerRegistration registration;
    private BottomNavigationItemView share;

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            try {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideKeyboard(getCurrentFocus());
        authChange = this;
        listener = this;
        try {
            registration = Variables.Firebase.FIRESTORE
                    .collection("SocialMedia")
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.w(TAG, "Listen to SocialMedia failed", error);
                            return;
                        }
                        if (value != null && !value.isEmpty()) {
                            for (DocumentSnapshot snapshot : value) {
                                SocialMedia sm = snapshot.toObject(SocialMedia.class);
                                if (sm != null) {
                                    webLinks.add(sm);
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, "Listen to social media changes crashed", e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            registration.remove();
        } catch (Exception e) {
            Log.d(TAG, "Something went wrong while removing the snapshot listener", e);
        }
        hideKeyboard(getCurrentFocus());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set user data if any is still signed in
        try {
            if (Variables.Firebase.AUTHENTICATION.getCurrentUser() != null) {
                Variables.Firebase.setAuth();
                Variables.Firebase.isUserLogin = true;
            } else {
                User.logOut();
                Variables.Firebase.isUserLogin = false;
            }
        } catch (Exception e) {
            User.logOut();
            Variables.Firebase.isUserLogin = false;
        }
        parentLayout = findViewById(android.R.id.content);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerlayout = findViewById(R.id.drawer_layout);

        appBarConfiguration = new AppBarConfiguration.Builder().build();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fillSideNav();

        //ButtonNavigationView
        BottomNavigationViewEx bnve = findViewById(R.id.bottom_nav_view);
        bnve.enableShiftingMode(false);
        bnve.enableItemShiftingMode(false);
        bnve.setTextVisibility(true);
        bnve.setOnNavigationItemSelectedListener(this);
        share = findViewById(R.id.menu_share);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        /* Open Fragment on Start */
        if (savedInstanceState == null) {
            //DEFAULT: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment()).commit();
        }
    }

    private void fillSideNav() {
        boolean isLogin = false;
        try {
            isLogin = (Variables.Firebase.AUTHENTICATION.getCurrentUser() != null);
        } catch (Exception ignored) {
        }
        Log.e(TAG, "isLogin: " + isLogin);

        //Navigation Head
        View view = navigationView.getHeaderView(0);
        ImageView image = view.findViewById(R.id.nav_headder);
        TextView title = view.findViewById(R.id.nav_title);
        TextView street = view.findViewById(R.id.nav_street);
        TextView city = view.findViewById(R.id.nav_city);
        if (isLogin) {
            try {
                image.setImageResource(R.drawable.wappen_round);
                title.setText(User.getData().getFullName());
                street.setText(User.getData().getMobile());
                city.setVisibility(View.VISIBLE);
                city.setText(User.getData().getMail());
            } catch (Exception ignored) {
            }
        } else {
            image.setImageResource(R.drawable.wappen_2017);
            title.setText(Variables.Walhalla.NAME);
            street.setText(Variables.Walhalla.ADH_ADDRESS);
            city.setVisibility(View.GONE);
        }

        //Navigation Body
        navigationView.getMenu().clear();
        Menu menu = navigationView.getMenu();
        //Public area
        menu.add(0, R.string.menu_home, 0, R.string.menu_home)
                .setChecked(true)
                .setIcon(R.drawable.ic_home);
        menu.add(0, R.string.menu_about_us, 0, R.string.menu_about_us)
                .setIcon(R.drawable.ic_info);
        menu.add(0, R.string.menu_rooms, 0, R.string.menu_rooms)
                .setIcon(R.drawable.ic_rooms);
        menu.add(0, R.string.menu_program, 0, R.string.menu_program)
                .setIcon(R.drawable.ic_calendar);
        menu.add(0, R.string.menu_messages, 0, R.string.menu_messages)
                .setIcon(R.drawable.ic_message);
        menu.add(0, R.string.menu_chargen, 0, R.string.menu_chargen)
                .setIcon(R.drawable.ic_group);
        menu.add(0, R.string.menu_chargen_phil, 0, R.string.menu_chargen_phil)
                .setIcon(R.drawable.ic_group_line);

        /* Login/Sign up, Logout */
        Menu loginMenu = menu.addSubMenu(R.string.menu_user_editing);
        if (isLogin) {
            loginMenu.add(1, R.string.menu_logout, 0, R.string.menu_logout)
                    .setIcon(R.drawable.ic_exit)
                    .setCheckable(false);
            loginMenu.add(0, R.string.menu_profile, 0, R.string.menu_profile)
                    .setIcon(R.drawable.ic_person);

            /*loginMenu.add(0, R.string.menu_beer, 0, R.string.menu_beer) //Change appearance depending on who is logged in
                    .setIcon(R.drawable.ic_beer);

            //Only visible to members of the fraternity
            Menu menuLogin = menu.addSubMenu(R.string.menu_intern);
            menuLogin.add(0, R.string.menu_transcript, 0, R.string.menu_transcript)
                    .setIcon(R.drawable.ic_scriptor);
            menuLogin.add(0, R.string.menu_kartei, 0, R.string.menu_kartei)
                    .setIcon(R.drawable.ic_contacts);
             */
            //Only visible to a active board member of the current semester
            if (User.hasCharge()) {
                Menu menuCharge = menu.addSubMenu(R.string.menu_board_only);
                /*menuCharge.add(0, R.string.menu_new_person, 0, R.string.menu_new_person)
                        .setIcon(R.drawable.ic_person_add);
                menuCharge.add(0, R.string.menu_user, 0, R.string.menu_user)
                        .setIcon(R.drawable.ic_home);
                menuCharge.add(0, R.string.menu_account, 0, R.string.menu_account)
                        .setIcon(R.drawable.ic_account);*/
                menuCharge.add(0, R.string.menu_new_semester, 0, R.string.menu_new_semester);
            }
        } else {
            loginMenu.add(1, R.string.menu_login, 0, R.string.menu_login)
                    ///.setCheckable(false)
                    .setIcon(R.drawable.ic_exit);
        }

        loginMenu.setGroupCheckable(1, false, true);

        Menu moreMenu = menu.addSubMenu(R.string.menu_more);
        moreMenu.add(1, R.string.menu_more_board, 1, R.string.menu_more_board)
                .setContentDescription(getString(R.string.menu_more_board_description));
        moreMenu.add(1, R.string.menu_more_history, 1, R.string.menu_more_history);
        moreMenu.add(1, R.string.menu_more_frat_wue, 1, R.string.menu_more_frat_wue);
        moreMenu.add(1, R.string.menu_more_frat_organisation, 1, R.string.menu_more_frat_organisation);

        Menu menuEnd = menu.addSubMenu(R.string.menu_other);
        menuEnd.add(0, R.string.menu_settings, 0, R.string.menu_settings)
                .setIcon(R.drawable.ic_settings)
                .setCheckable(false);
        menuEnd.add(0, R.string.menu_donate, 0, R.string.menu_donate)
                .setCheckable(false)
                .setIcon(R.drawable.ic_donate);

        navigationView.invalidate();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (lastItem != null) {
            lastItem.setChecked(false);
        } else {
            lastItem = navigationView.getMenu().getItem(0);
            lastItem.setChecked(true);
        }
        //Set selected Item as checked
        item.setChecked(true);
        switch (item.getItemId()) {
            //SiteNav Left
            case R.string.menu_login:
                LoginDialog.display(getSupportFragmentManager());
                lastItem.setChecked(true);
                break;
            case R.string.menu_logout:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment()).commit();
                Variables.Firebase.AUTHENTICATION.signOut();
                Firebase.Messaging.UnsubscribeTopic(Firebase.Messaging.TOPIC_INTERNAL);

                User.logOut();
                Snackbar.make(parentLayout, R.string.login_logout_successful, Snackbar.LENGTH_LONG)
                        .setAction(R.string.close, v -> {
                        })
                        .setActionTextColor(App.getContext().getResources().getColor(R.color.colorPrimaryDark, null))
                        .show();
                lastItem.setChecked(true);
                break;
            case R.id.menu_home:
            case R.string.menu_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment()).commit();
                break;
            case R.string.menu_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BalanceFragment()).commit();
                break;
            case R.string.menu_donate:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.donate.Fragment()).commit();
                break;
            case R.id.menu_program:
            case R.string.menu_program:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.program.Fragment()).commit();
                break;
            case R.id.menu_messages:
            case R.string.menu_messages:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.news.Fragment()).commit();
                break;
            case R.id.menu_board:
            case R.string.menu_chargen:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.chargen.Fragment()).commit();
                break;
            case R.string.menu_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.profile.Fragment(null)).commit();
                break;
            case R.string.menu_beer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.drink.Fragment()).commit();
                break;
            case R.string.menu_kartei:
                Toast.makeText(this, R.string.menu_kartei, Toast.LENGTH_SHORT).show();
                break;
            case R.string.menu_chargen_phil:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.chargen_phil.Fragment()).commit();
                break;
            case R.string.menu_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.settings.Fragment()).commit();
                break;
            case R.string.menu_register:
                Toast.makeText(this, R.string.menu_register, Toast.LENGTH_SHORT).show();
                break;
            case R.string.menu_transcript:
                Toast.makeText(this, R.string.menu_transcript, Toast.LENGTH_SHORT).show();
                break;
            case R.string.menu_user:
                Toast.makeText(this, R.string.menu_user, Toast.LENGTH_SHORT).show();
                break;
            case R.string.menu_new_person:
                Toast.makeText(this, R.string.menu_new_person, Toast.LENGTH_SHORT).show();
                break;
            case R.string.menu_more_board:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.more.BoardFragment()).commit();
                break;
            case R.string.menu_more_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.more.HistoryFragment()).commit();
                break;
            case R.string.menu_more_frat_wue:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.more.FratWueFragment()).commit();
                break;
            case R.string.menu_more_frat_organisation:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.more.FratOrgaFragment()).commit();
                break;
            case R.string.menu_about_us:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.more.AboutUsFragment()).commit();
                break;
            case R.string.menu_rooms:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.more.RoomsFragment()).commit();
                break;
            case R.string.menu_new_semester:
                //Open dialog which enables the user to create the whole semester with every necessary field
                NewSemesterDialog.display(getSupportFragmentManager());
                break;
            //BottomToolBar
            case R.id.menu_share:
                //Open the social Media as a kind of dialog / tooltip
                try {
                    lastItem.setChecked(true);
                } catch (Exception ignored) {
                }
                @SuppressLint("InflateParams")
                View view = getLayoutInflater().inflate(R.layout.social_media, null);
                SimpleTooltip socialMedia = new SimpleTooltip.Builder(App.getContext())
                        .anchorView(share)
                        .showArrow(false)
                        .contentView(view, 0)
                        .gravity(Gravity.TOP)
                        .animated(false)
                        .modal(true)
                        .dismissOnInsideTouch(false)
                        .dismissOnOutsideTouch(true)
                        .transparentOverlay(true)
                        .build();

                ImageButton instagram = view.findViewById(R.id.icon_instagram);
                ImageButton facebook = view.findViewById(R.id.icon_facebook);
                ImageButton web = view.findViewById(R.id.icon_website);
                ImageButton mail = view.findViewById(R.id.icon_email);

                instagram.setOnClickListener(v -> {
                    for (SocialMedia sm : webLinks) {
                        if (sm.getName().equals("instagram")) {
                            browser(sm.getLink());
                            socialMedia.dismiss();
                        }
                    }
                });
                facebook.setOnClickListener(v -> {
                    for (SocialMedia sm : webLinks) {
                        if (sm.getName().equals("facebook")) {
                            browser(sm.getLink());
                            socialMedia.dismiss();
                        }
                    }
                });
                web.setOnClickListener(v -> {
                    browser("");
                    socialMedia.dismiss();
                });
                mail.setOnClickListener(v -> {
                    email();
                    socialMedia.dismiss();
                });
                socialMedia.show();
                break;
            default:
                Snackbar.make(parentLayout, R.string.error_site_messages, Snackbar.LENGTH_LONG)
                        .setAction(R.string.close, v -> {
                        })
                        .setActionTextColor(App.getContext().getResources().getColor(R.color.colorPrimaryDark, null))
                        .show();
                Log.i(TAG, "nothing checked");
                break;
        }
        lastItem = item;
        //Close drawer
        drawerlayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        //If drawer is open, show possibility to close the app via the back-button.
        if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
            if (doubleBackToExitPressedOnce) {
                //Button pressed a second time within half a second.
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.exit_app_via_back, Toast.LENGTH_LONG).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 1000);
        } else { //Otherwise open the left menu
            drawerlayout.open();
        }
    }

    @Override
    public void onAuthChange() {
        runOnUiThread(this::fillSideNav);
    }

    /**
     * Via the listener open the web browser
     *
     * @param url the link the browser will open
     */
    @Override
    public void browser(@NotNull String url) {
        if (url.length() != 0) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } else {
            Log.e(TAG, "browser: no link given");
        }
    }

    /**
     * Via a listener open the default mail program and write an e mail
     */
    @Override
    public void email() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        String[] recipients = new String[]{Variables.Walhalla.MAIL_SENIOR};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}