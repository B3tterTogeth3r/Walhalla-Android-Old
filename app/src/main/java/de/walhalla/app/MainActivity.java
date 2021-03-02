package de.walhalla.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import de.walhalla.app.dialog.LoginDialog;
import de.walhalla.app.firebase.CustomAuthListener;
import de.walhalla.app.fragments.BalanceFragment;
import de.walhalla.app.fragments.home.Fragment;
import de.walhalla.app.utils.Find;
import de.walhalla.app.utils.Variables;

@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        CustomAuthListener.sendMain {
    private final static String TAG = "MainActivity";
    public static View parentLayout;
    private MenuItem lastItem;
    private DrawerLayout drawerlayout;
    private AppBarConfiguration appBarConfiguration;
    private boolean doubleBackToExitPressedOnce = false;
    private NavigationView navigationView;
    public static CustomAuthListener.sendMain authChange;

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            try {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } catch (NullPointerException ignored) {
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideKeyboard(getCurrentFocus());
        authChange = this;
    }

    @Override
    public void onStop() {
        super.onStop();
        hideKeyboard(getCurrentFocus());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set user data if any is still signed in
        if (Variables.Firebase.user != null) {
            Variables.Firebase.isUserLogin = true;
            Find.PersonByUID(Variables.Firebase.user.getUid(), Variables.Firebase.user.getEmail());
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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        /* Open Fragment on Start */
        if (savedInstanceState == null) {
            //DEFAULT: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment()).commit();
        }
    }

    private void fillSideNav() {
        boolean isLogin = (Variables.Firebase.AUTHENTICATION.getCurrentUser() != null);
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
        menu.add(0, R.string.menu_program, 0, R.string.menu_program)
                .setIcon(R.drawable.ic_calendar);
        menu.add(0, R.string.menu_messages, 0, R.string.menu_messages)
                .setIcon(R.drawable.ic_message);
        menu.add(0, R.string.menu_chargen, 0, R.string.menu_chargen)
                .setIcon(R.drawable.ic_group);
        menu.add(0, R.string.menu_chargen_phil, 0, R.string.menu_chargen_phil)
                .setIcon(R.drawable.ic_group_line);

        //Login, Sign up, Logout and delete pages
        /*Menu loginMenu = menu.addSubMenu(R.string.menu_user_editing);
        if (!isLogin) {
            loginMenu.add(1, R.string.menu_login, 0, R.string.menu_login)
                    //.setCheckable(false)
                    .setIcon(R.drawable.ic_exit);
        } else {
            loginMenu.add(1, R.string.menu_logout, 0, R.string.menu_logout)
                    .setIcon(R.drawable.ic_exit)
                    .setCheckable(false);
            loginMenu.add(0, R.string.menu_profile, 0, R.string.menu_profile)
                    .setIcon(R.drawable.ic_person);

            loginMenu.add(0, R.string.menu_beer, 0, R.string.menu_beer) //Change appearance depending on who is logged in
                    .setIcon(R.drawable.ic_beer);

            //Only visible to members of the fraternity
            Menu menuLogin = menu.addSubMenu(R.string.menu_intern);
            menuLogin.add(0, R.string.menu_transcript, 0, R.string.menu_transcript)
                    .setIcon(R.drawable.ic_scriptor);
            menuLogin.add(0, R.string.menu_kartei, 0, R.string.menu_kartei)
                    .setIcon(R.drawable.ic_contacts);

            //Only visible to a active board member of the current semester
            if (User.hasCharge()) {
                Menu menuCharge = menu.addSubMenu(R.string.menu_board_only);
                menuCharge.add(0, R.string.menu_new_person, 0, R.string.menu_new_person)
                        .setIcon(R.drawable.ic_person_add);
                menuCharge.add(0, R.string.menu_user, 0, R.string.menu_user)
                        .setIcon(R.drawable.ic_home);
                menuCharge.add(0, R.string.menu_account, 0, R.string.menu_account)
                        .setIcon(R.drawable.ic_account);
            }
        }*/

        Menu menuEnd = menu.addSubMenu(R.string.menu_other);
        /*menuEnd.add(0, R.string.menu_settings, 0, R.string.menu_settings)
                .setIcon(R.drawable.ic_settings)
                .setCheckable(false);*/
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
            navigationView.getMenu().getItem(0).setChecked(false);
        }
        switch (item.getItemId()) {
            case R.string.menu_login:
                LoginDialog.display(getSupportFragmentManager());
                Snackbar.make(parentLayout, R.string.login_success, Snackbar.LENGTH_LONG)
                        .setAction(R.string.close, v -> {
                        })
                        .setActionTextColor(App.getContext().getResources().getColor(R.color.colorPrimaryDark, null))
                ;//.show();
                break;
            case R.string.menu_logout:
                Variables.Firebase.AUTHENTICATION.signOut();
                Snackbar.make(parentLayout, R.string.login_logout_successful, Snackbar.LENGTH_LONG)
                        .setAction(R.string.close, v -> {
                        })
                        .setActionTextColor(App.getContext().getResources().getColor(R.color.colorPrimaryDark, null))
                        .show();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Fragment()).commit();
                break;
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
            case R.string.menu_program:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.program.Fragment()).commit();
                break;
            case R.string.menu_messages:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new de.walhalla.app.fragments.news.Fragment()).commit();
                break;
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
                Toast.makeText(this, R.string.menu_settings, Toast.LENGTH_SHORT).show();
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
            default:
                Snackbar.make(parentLayout, R.string.error_site_messages, Snackbar.LENGTH_LONG).show();
                Log.i(TAG, "nothing checked");
                break;
        }
        //Set selected Item as checked
        item.setChecked(true);
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
            Toast.makeText(this, R.string.exit_app_via_back, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 500);

        } else { //Otherwise open the left menu
            drawerlayout.open();
        }
    }

    @Override
    public void onAuthChange() {
        runOnUiThread(this::fillSideNav);
    }
}