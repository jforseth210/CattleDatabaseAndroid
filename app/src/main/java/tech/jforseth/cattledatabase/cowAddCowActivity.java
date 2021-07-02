package tech.jforseth.cattledatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class cowAddCowActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_add_cow);

        Spinner sexSpinner = findViewById(R.id.sexSpinner);
        String[] sexes = new String[]{"Bull", "Bull (AI)", "Steer", "Cow", "Heifer", "Heifer (Replacement)", "Heifer (Market)", "Free-Martin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sexes);
        sexSpinner.setAdapter(adapter);

        EditText ownerEditText = findViewById(R.id.editTextOwner);
        SharedPreferences preferences = getSharedPreferences("tech.jforseth.CattleDatabase", MODE_PRIVATE);
        ownerEditText.setText(preferences.getString("username", ""));
        get_possible_parents("dam");
        get_possible_parents("sire");

        Button submitButton = findViewById(R.id.addCowSubmitButton);
        submitButton.setOnClickListener(v -> {
            Spinner damSpinner = (Spinner) findViewById(R.id.damSpinner);
            String dam = damSpinner.getSelectedItem().toString();
            Spinner sireSpinner = (Spinner) findViewById(R.id.sireSpinner);
            String sire = sireSpinner.getSelectedItem().toString();

            EditText editTextTagNumber = findViewById(R.id.addCowEditTextTagNumber);
            String tagNumber = editTextTagNumber.getText().toString();

            EditText editTextOwner = findViewById(R.id.editTextOwner);
            String owner = editTextOwner.getText().toString();

            Spinner sexSpinner1 = (Spinner) findViewById(R.id.sexSpinner);
            String sex = sexSpinner1.getSelectedItem().toString();
            Map newCowDict = new HashMap();
            newCowDict.put("dam", dam);
            newCowDict.put("sire", sire);
            newCowDict.put("tag_number", tagNumber);
            newCowDict.put("owner", owner);
            newCowDict.put("sex", sex);

            JSONObject newCowJSON = new JSONObject(newCowDict);

            addCow(newCowJSON.toString());
            switchToMainActivity();
        });
    }

    private void switchToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
        this.finish();
    }

    private void addCow(String newCowJSON) {
        /*
        makeHTTPRequestOLD request = new makeHTTPRequestOLD(this, this);
        SharedPreferences preferences = getSharedPreferences("tech.jforseth.CattleDatabase", MainActivity.MODE_PRIVATE);
        String url = "";
        try {
            url = preferences.getString("server_LAN_address", "") + "/api/add_cow/" + URLEncoder.encode(newCowJSON, StandardCharsets.UTF_8.toString());
        } catch (Exception d) {
            try {
                url = preferences.getString("server_WAN_address", "") + "/api/add_cow/" + URLEncoder.encode(newCowJSON, StandardCharsets.UTF_8.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        request.execute(url);
        */
        new makeHTTPRequest(
                "add_cow",
                newCowJSON,
                response -> {

                },
                error -> {
                    error.printStackTrace();
                },
                this
        );
    }

    private void get_possible_parents(String type) {
        /*
        makeHTTPRequestOLD request = new makeHTTPRequestOLD(this, this);
        SharedPreferences preferences = getSharedPreferences("tech.jforseth.CattleDatabase", MainActivity.MODE_PRIVATE);
        String url = "";
        try {
            url = preferences.getString("server_LAN_address", "") + "/api/get_possible_parents/" + type;
        } catch (Exception d) {
            try {
                url = preferences.getString("server_WAN_address", "") + "/api/get_possible_parents/" + type;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        request.execute(url);
    */
        new makeHTTPRequest(
                "get_possible_parents",
                type,
                response -> {
                    updateSpinners(response);
                },
                error -> {
                    error.printStackTrace();
                },
                this
        );
    }
    public void updateSpinners(JSONObject object){
        try {
            Spinner sexSpinner = findViewById(R.id.sexSpinner);
            String parentType = object.getString("parent_type");
            JSONArray parents_json = object.getJSONArray("parents");
            String[] parents = new String[parents_json.length() + 1];
            parents[0] = "Not Applicable";
            for (int i = 0; i < parents_json.length(); i++) {
                parents[i + 1] = parents_json.getString(i);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, parents);
            if (parentType.equals("dam")) {
                Spinner damSpinner = findViewById(R.id.damSpinner);
                damSpinner.setAdapter(adapter);
            } else {
                Spinner sireSpinner = findViewById(R.id.sireSpinner);
                sireSpinner.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}