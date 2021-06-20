package tech.jforseth.cattledatabase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import tech.jforseth.cattledatabase.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        tech.jforseth.cattledatabase.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        SharedPreferences preferences = getSharedPreferences("tech.jforseth.CattleDatabase", MODE_PRIVATE);
        /*
        SharedPreferences.Editor pref_editor = preferences.edit();
        pref_editor.putBoolean("logged_in", false);
        pref_editor.apply();
        */
        NavigationView navigationViewEditable = findViewById(R.id.nav_view);
        View headerView = navigationViewEditable.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.textViewLoginUsername);
        navUsername.setText(preferences.getString("username", ""));
        TextView navIP = headerView.findViewById(R.id.textViewLoginIP);
        navIP.setText(preferences.getString("server_LAN_address", ""));

        //This is probably bad practice, but then again, so is this whole project.
        if (preferences.getString("password", "").equals("") ||
                preferences.getString("username", "").equals("") ||
                preferences.getString("server_LAN_address", "").equals("") ||
                !preferences.getBoolean("logged_in", false)
        ) {
            Intent i = new Intent(this, LoginActivity.class);
            this.startActivity(i);
            this.finish();
        }
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_cows, R.id.nav_events, R.id.nav_transactions)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    public void goToLoginScreen() {
        Intent i = new Intent(this, LoginActivity.class);
        this.startActivity(i);
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log_out:
                SharedPreferences preferences = getSharedPreferences("tech.jforseth.CattleDatabase", MODE_PRIVATE);
                SharedPreferences.Editor pref_editor = preferences.edit();
                pref_editor.putString("username", "");
                pref_editor.putString("password", "");
                pref_editor.putString("server_LAN_address", "");
                pref_editor.putString("server_WAN_address", "");
                pref_editor.putBoolean("logged_in", false);
                pref_editor.apply();
                goToLoginScreen();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showInputDialog(Context c, String title, String message, String hint, String preference, SharedPreferences preferences) {
        final EditText taskEditText = new EditText(c);
        taskEditText.setHint(hint);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle(title)
                .setMessage(message)
                .setView(taskEditText)
                .setPositiveButton("Ok", (dialog1, which) -> {
                    SharedPreferences.Editor pref_editor = preferences.edit();
                    pref_editor.putString(preference, taskEditText.getText().toString());
                    pref_editor.apply();
                    NavigationView navigationViewEditable = findViewById(R.id.nav_view);
                    View headerView = navigationViewEditable.getHeaderView(0);
                    TextView navUsername = headerView.findViewById(R.id.textViewLoginUsername);
                    navUsername.setText(preferences.getString("username", ""));
                    TextView navIP = headerView.findViewById(R.id.textViewLoginIP);
                    navIP.setText(preferences.getString("serverIPLAN", ""));
                })
                .create();
        dialog.show();
    }
}