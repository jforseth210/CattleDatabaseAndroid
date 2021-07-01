package tech.jforseth.cattledatabase;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class cowInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_information);

        Intent intent = getIntent();
        String tagNumber = intent.getExtras().getString("tagNumber", "");
        String response = intent.getExtras().getString("jsonData", "");
        JSONObject parsedResponse = null;
        try {
            parsedResponse = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView tagNumberTextView = findViewById(R.id.cowInformationTagNumberTextView);
        tagNumberTextView.setText(tagNumber);

        TextView ownerTextView = findViewById(R.id.cowInformationOwnerTextview);
        TextView sexTextView = findViewById(R.id.cowInformationSexTextView);
        TextView damTextView = findViewById(R.id.cowInformationDamTextView);
        TextView sireTextView = findViewById(R.id.cowInformationSireTextView);
        try {
            ownerTextView.setText(parsedResponse.getString("owner"));
            sexTextView.setText(parsedResponse.getString("sex"));
            damTextView.setText(parsedResponse.getString("dam"));
            sireTextView.setText(parsedResponse.getString("sire"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!damTextView.getText().equals("N/A")) {
            damTextView.setPaintFlags(damTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            damTextView.setOnClickListener(damView -> {
                final String damTagNumber = ((TextView) damView).getText().toString();
                new makeHTTPRequest(
                        "cow",
                        damTagNumber,
                        dam_response -> {
                            Intent new_intent = new Intent(this, cowInformation.class);
                            new_intent.putExtra("tagNumber", damTagNumber);
                            /*
                             * As far is I can tell, there is no way to send a JSONObject
                             * directly, so we send it as a string instead and parse it
                             * back on the other end.
                             */

                            new_intent.putExtra("jsonData", dam_response.toString());
                            this.startActivity(new_intent);

                        }, error -> {

                },
                        this
                );
            });
        }
        if (!sireTextView.getText().equals("N/A")) {
            sireTextView.setPaintFlags(sireTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            sireTextView.setOnClickListener(sireView -> {
                System.out.println(((TextView) sireView).getHint().toString());
                final String sireTagNumber = ((TextView) sireView).getText().toString();
                new makeHTTPRequest(
                        "cow",
                        sireTagNumber,
                        sire_response -> {
                            Intent new_intent = new Intent(this, cowInformation.class);
                            new_intent.putExtra("tagNumber", sireTagNumber);
                            /*
                             * As far is I can tell, there is no way to send a JSONObject
                             * directly, so we send it as a string instead and parse it
                             * back on the other end.
                             */

                            new_intent.putExtra("jsonData", sire_response.toString());
                            this.startActivity(new_intent);

                        }, error -> {

                },
                        this
                );
            });
        }
        TabLayout cowInformationTabs = findViewById(R.id.cowInformationTabs);

        LinearLayout offSpringScrollViewLinearLayout = findViewById(R.id.offspringScrollViewLinearLayout);
        TextView nyiTextView = findViewById(R.id.nyiTextView);

        JSONArray calves = null;
        try {
            calves = parsedResponse.getJSONArray("calves");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < calves.length(); i++) {
            try {
                TextView calfTextView = new TextView(this);
                calfTextView.setText("• " + calves.getString(i));
                calfTextView.setHint(calves.getString(i));
                calfTextView.setTextSize(20);
                calfTextView.setPadding(0, 25, 0, 0);
                calfTextView.setPaintFlags(calfTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                calfTextView.setOnClickListener(view -> {
                    System.out.println(((TextView) view).getHint().toString());
                    final String calfTagNumber = ((TextView) view).getHint().toString();
                    new makeHTTPRequest(
                            "cow",
                            calfTagNumber,
                            calf_response -> {
                                Intent new_intent = new Intent(this, cowInformation.class);
                                new_intent.putExtra("tagNumber", calfTagNumber);
                                /*
                                 * As far is I can tell, there is no way to send a JSONObject
                                 * directly, so we send it as a string instead and parse it
                                 * back on the other end.
                                 */

                                new_intent.putExtra("jsonData", calf_response.toString());
                                this.startActivity(new_intent);

                            }, error -> {

                    },
                            this
                    );
                });
                offSpringScrollViewLinearLayout.addView(calfTextView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        cowInformationTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                                        @Override
                                                        public void onTabSelected(TabLayout.Tab tab) {
                                                            if (tab.getText().equals("Offspring")) {
                                                                offSpringScrollViewLinearLayout.setVisibility(View.VISIBLE);
                                                            } else {
                                                                nyiTextView.setVisibility(View.VISIBLE);
                                                            }
                                                        }

                                                        @Override
                                                        public void onTabUnselected(TabLayout.Tab tab) {
                                                            if (tab.getText().equals("Offspring")) {
                                                                offSpringScrollViewLinearLayout.setVisibility(View.GONE);
                                                            } else {
                                                                nyiTextView.setVisibility(View.GONE);
                                                            }

                                                        }

                                                        @Override
                                                        public void onTabReselected(TabLayout.Tab tab) {

                                                        }
                                                    }
        );
    }
}
