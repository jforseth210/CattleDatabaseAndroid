package tech.jforseth.cattledatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class cowTransferOwnershipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String tagNumber = i.getExtras().getString("tagNumber", "");

        setContentView(R.layout.activity_cow_transfer_ownership);
        EditText newOwner = findViewById(R.id.transferOwnershipNewOwner);
        EditText priceInput = findViewById(R.id.transferOwnershipPriceInput);
        DatePicker date = findViewById(R.id.transferOwnershipDate);
        EditText description = findViewById(R.id.transferOwnershipDetailsInput);

        Button submitButton = findViewById(R.id.transferOwnershipSubmitButton);
        submitButton.setOnClickListener(view -> {

            String year = String.valueOf(date.getYear());
            String month = String.valueOf(date.getMonth() + 1);
            String day = String.valueOf(date.getDayOfMonth());

            if (month.length() < 2)
                month = "0" + month;
            if (day.length() < 2)
                day = "0" + day;

            String iso_date = year + "-" + month + "-" + day;

            Map transferOwnershipDict = new HashMap<String, String>();
            transferOwnershipDict.put("tag_number", tagNumber);
            transferOwnershipDict.put("new_owner", newOwner.getText().toString().trim());
            transferOwnershipDict.put("price", priceInput.getText().toString().trim());
            transferOwnershipDict.put("date", iso_date);
            transferOwnershipDict.put("description", description.getText().toString().trim());

            JSONObject transferOwnershipJSON = new JSONObject(transferOwnershipDict);

            new makeHTTPRequest(
                    "cows/transfer_ownership",
                    transferOwnershipJSON.toString(),
                    response -> {
                        this.startActivity(new Intent(this, MainActivity.class));
                        this.finish();
                    },
                    error -> {
                        error.printStackTrace();
                    },
                    this
            );
        });

        TextView tagNumberTextView = findViewById(R.id.transferOwnershipTagNumber);
        tagNumberTextView.setText("Transferring "+tagNumber);
    }
}