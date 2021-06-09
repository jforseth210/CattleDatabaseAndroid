package tech.jforseth.cattledatabase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
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

        NavigationView navigationViewEditable = findViewById(R.id.nav_view);
        View headerView = navigationViewEditable.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.textViewLoginUsername);
        navUsername.setText(preferences.getString("username", ""));
        TextView navIP= headerView.findViewById(R.id.textViewLoginIP);
        navIP.setText(preferences.getString("serverIPLAN", ""));

        //This is probably bad practice, but then again, so is this whole project.
        if (preferences.getString("password", "").equals("")) {
            showInputDialog(this, "Enter password", "Enter the password you use to access your CattleDB", "averysecurepassword", "password", preferences);

        }
        if (preferences.getString("username", "").equals("")) {
            showInputDialog(this, "Enter username", "Enter the username you use to access your CattleDB", "Your Name", "username", preferences);
        }
        if (preferences.getString("serverIPLAN", "").equals("")) {
            showInputDialog(this, "Enter local link", "Enter the link you use to access your CattleDB", "http://192.168.1.__:5000", "serverIPLAN", preferences);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
                    TextView navIP= headerView.findViewById(R.id.textViewLoginIP);
                    navIP.setText(preferences.getString("serverIPLAN", ""));
                })
                .create();
        dialog.show();
    }
}