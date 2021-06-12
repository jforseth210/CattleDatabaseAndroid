package tech.jforseth.cattledatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import tech.jforseth.cattledatabase.ui.cows.CowFragment;

public class cowAddCowActivity extends AppCompatActivity implements  makeHTTPRequest.AsyncResponse{
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_add_cow);

        Spinner sexSpinner = findViewById(R.id.sexSpinner);
        String[] sexes = new String[]{"Bull", "Cow", "Heifer", "Steer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sexes);
        sexSpinner.setAdapter(adapter);

        EditText ownerEditText = findViewById(R.id.editTextOwner);
        SharedPreferences preferences = getSharedPreferences("tech.jforseth.CattleDatabase", MODE_PRIVATE);
        ownerEditText.setHint(preferences.getString("username",""));
        get_possible_parents("dam");
        get_possible_parents("sire");

        Button submitButton = findViewById(R.id.addCowSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner damSpinner = (Spinner)findViewById(R.id.damSpinner);
                String dam = damSpinner.getSelectedItem().toString();
                Spinner sireSpinner = (Spinner)findViewById(R.id.sireSpinner);
                String sire = sireSpinner.getSelectedItem().toString();

                EditText editTextTagNumber = findViewById(R.id.addCowEditTextTagNumber);
                String tagNumber = editTextTagNumber.getText().toString();

                EditText editTextOwner= findViewById(R.id.editTextOwner);
                String owner = editTextOwner.getText().toString();

                Spinner sexSpinner = (Spinner)findViewById(R.id.sexSpinner);
                String sex = sexSpinner.getSelectedItem().toString();
                Map newCowDict = new HashMap();
                newCowDict.put("dam", dam);
                newCowDict.put("sire", sire);
                newCowDict.put("tag_number", tagNumber);
                newCowDict.put("owner", owner);
                newCowDict.put("sex", sex);

                JSONObject newCowJSON = new JSONObject(newCowDict);

                addCow(newCowJSON.toString());
                switchToMainActivity();
            }
        });
    }
    private void switchToMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
    }
    private void addCow(String newCowJSON){
        makeHTTPRequest request = new makeHTTPRequest(this, this);
        SharedPreferences preferences = getSharedPreferences("tech.jforseth.CattleDatabase", MainActivity.MODE_PRIVATE);
        String url = "";
        try{
            url = preferences.getString("server_LAN_address", "") + "/api/add_cow/"+newCowJSON;
        } catch (Exception d){
            try {
                url = preferences.getString("server_WAN_address", "") + "/api/add_cow/"+newCowJSON;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        request.execute(url);
    }
    private void get_possible_parents(String type){
        makeHTTPRequest request = new makeHTTPRequest(this, this);
        SharedPreferences preferences = getSharedPreferences("tech.jforseth.CattleDatabase", MainActivity.MODE_PRIVATE);
        String url = "";
        try{
            url = preferences.getString("server_LAN_address", "") + "/api/get_possible_parents/" + type;
        } catch (Exception d){
            try {
                url = preferences.getString("server_WAN_address", "") + "/api/get_possible_parents/"+ type;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println(url);
        request.execute(url);
    }

    @Override
    public void processFinish(String output) {
        try {
            System.out.println(output);
            JSONObject object = new JSONObject(output);
            Spinner sexSpinner = findViewById(R.id.sexSpinner);
            String parentType = object.getString("parent_type");
            JSONArray parents_json = object.getJSONArray("parents");
            String[] parents = new String[parents_json.length() + 1];
            parents[0] = "N/A";
            for (int i = 0; i < parents_json.length(); i++){
                parents[i + 1] = parents_json.getString(i);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, parents);
            if (parentType.equals("dam")){
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