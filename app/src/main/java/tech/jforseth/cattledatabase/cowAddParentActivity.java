package tech.jforseth.cattledatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class cowAddParentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        String tagNumber = i.getExtras().getString("tagNumber", "");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_add_parent);

        get_possible_parents("dam");
        get_possible_parents("sire");
        TextView modifyingCowsParentsLabel = (TextView) findViewById(R.id.modifyingCowsParentsLabel);
        modifyingCowsParentsLabel.setText("Modifying " + tagNumber + "'s parents");
        Button addParentSubmitButton = findViewById(R.id.addParentSubmitButton);
        addParentSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner damSpinner = (Spinner) findViewById(R.id.addParentDamSpinner);
                String dam = damSpinner.getSelectedItem().toString();
                Spinner sireSpinner = (Spinner) findViewById(R.id.addParentSireSpinner);
                String sire = sireSpinner.getSelectedItem().toString();

                Map newParentDict = new HashMap();
                newParentDict.put("dam", dam);
                newParentDict.put("sire", sire);
                newParentDict.put("tag_number", tagNumber);

                JSONObject newParentJSON = new JSONObject(newParentDict);

                addParent(newParentJSON.toString());
                switchToMainActivity();
            }
        });
    }

    private void switchToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
    }

    private void addParent(String newParentJSON) {
        new makeHTTPRequest(
                "add_parent",
                newParentJSON,
                response -> {

                },
                error -> {
                    error.printStackTrace();
                },
                this
        );
    }

    private void get_possible_parents(String type) {
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

    public void updateSpinners(JSONObject object) {
        try {
            String parentType = object.getString("parent_type");
            JSONArray parents_json = object.getJSONArray("parents");
            String[] parents = new String[parents_json.length() + 1];
            parents[0] = "N/A";
            for (int i = 0; i < parents_json.length(); i++) {
                parents[i + 1] = parents_json.getString(i);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, parents);
            if (parentType.equals("dam")) {
                Spinner damSpinner = findViewById(R.id.addParentDamSpinner);
                damSpinner.setAdapter(adapter);
            } else {
                Spinner sireSpinner = findViewById(R.id.addParentSireSpinner);
                sireSpinner.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }