package tech.jforseth.cattledatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;

public class cowAddCalfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_add_calf);

        Spinner sexSpinner = findViewById(R.id.addCalfSexSpinner);
        String[] sexes = new String[]{"Bull", "Bull (AI)", "Steer", "Cow", "Heifer", "Heifer (Replacement)", "Heifer (Market)", "Free-Martin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sexes);
        sexSpinner.setAdapter(adapter);

        new makeHTTPRequest(
                "get_possible_parents",
                "sire",
                response -> {
                    JSONArray parents_json = null;
                    try {
                        parents_json = response.getJSONArray("parents");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String[] parents = new String[parents_json.length() + 1];
                    parents[0] = "Not Applicable";
                    for (int i = 0; i < parents_json.length(); i++) {
                        try {
                            parents[i + 1] = parents_json.getString(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> sire_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, parents);
                    Spinner sireSpinner = findViewById(R.id.sireSpinner);
                    sireSpinner.setAdapter(sire_adapter);
                },
                error -> {
                    error.printStackTrace();
                },
                this
        );

        EditText ownerEditText = findViewById(R.id.editTextOwner);
        SharedPreferences preferences = getSharedPreferences("tech.jforseth.CattleDatabase", MODE_PRIVATE);
        ownerEditText.setText(preferences.getString("username", ""));
    }
}