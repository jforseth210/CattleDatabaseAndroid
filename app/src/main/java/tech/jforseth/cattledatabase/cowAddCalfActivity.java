package tech.jforseth.cattledatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class cowAddCalfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String parentTagNumber = intent.getExtras().getString("tagNumber", "");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_add_calf);

        Spinner sexSpinner = findViewById(R.id.addCalfSexSpinner);
        new makeHTTPRequest(
                "cows/sex_list",
                "",
                response -> {
                    try {
                        JSONArray sexes_json = response.getJSONArray("sexes");
                        String[] sexes = new String[sexes_json.length()];
                        for (int i = 0; i < sexes_json.length(); i++){
                            sexes[i] = sexes_json.getString(i);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sexes);
                        sexSpinner.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                },
                this
        );

        new makeHTTPRequest(
                "cows/possible_parents",
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
                    Spinner sireSpinner = findViewById(R.id.addCalfSireSpinner);
                    sireSpinner.setAdapter(sire_adapter);
                },
                error -> {
                    error.printStackTrace();
                },
                this
        );

        EditText ownerEditText = findViewById(R.id.addCalfOwnerEditText);
        SharedPreferences preferences = getSharedPreferences("tech.jforseth.CattleDatabase", MODE_PRIVATE);
        ownerEditText.setText(preferences.getString("username", ""));

        Button submitButton = findViewById(R.id.addCalfSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker date = findViewById(R.id.addCalfDatePicker);
                String year = String.valueOf(date.getYear());
                String month = String.valueOf(date.getMonth() + 1);
                String day = String.valueOf(date.getDayOfMonth());

                if (month.length() < 2)
                    month = "0" + month;
                if (day.length() < 2)
                    day = "0" + day;

                String iso_date = year + "-" + month + "-" + day;

                Switch bornEvent = findViewById(R.id.bornEventSwitch);
                Switch calvedEvent = findViewById(R.id.calvedEventSwitch);

                Boolean bornEventEnabled = bornEvent.isChecked();
                Boolean calvedEventEnabled = calvedEvent.isChecked();

                Spinner sireSpinner = findViewById(R.id.addCalfSireSpinner);
                String sire = sireSpinner.getSelectedItem().toString();

                EditText tagNumberEditText = findViewById(R.id.addCalfTagNumberEditText);
                String tagNumber = tagNumberEditText.getText().toString().trim();

                EditText addCalfOwnerEditText = findViewById(R.id.addCalfOwnerEditText);
                String owner = addCalfOwnerEditText.getText().toString().trim();

                Spinner sexSpinner = findViewById(R.id.addCalfSexSpinner);
                String sex = sexSpinner.getSelectedItem().toString();

                Map newCalfDict = new HashMap();
                newCalfDict.put("dam", parentTagNumber);
                newCalfDict.put("sire", sire);
                newCalfDict.put("tag_number", tagNumber);
                newCalfDict.put("owner", owner);
                newCalfDict.put("sex", sex);
                newCalfDict.put("born_event", bornEventEnabled);
                newCalfDict.put("calved_event", calvedEventEnabled);
                newCalfDict.put("date", iso_date);

                JSONObject newCalfJSON = new JSONObject(newCalfDict);
                addCalf(newCalfJSON);
            }
        });
    }
    public void addCalf(JSONObject newCalfJSON){
        new makeHTTPRequest(
                "cows/add",
                newCalfJSON.toString(),
                response -> {
                    try {
                        Toast.makeText(this, "Added " + newCalfJSON.getString("tag_number"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Added calf", Toast.LENGTH_SHORT).show();
                    }
                    switchToMainActivity();
                },
                error -> {
                    error.printStackTrace();
                },
                this
        );
    }
    public void switchToMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
        this.finish();
    }
}