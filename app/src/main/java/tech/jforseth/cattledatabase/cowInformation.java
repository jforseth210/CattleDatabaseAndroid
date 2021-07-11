package tech.jforseth.cattledatabase;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class cowInformation extends AppCompatActivity {
    // Make sure to use the FloatingActionButton
    // for all the FABs
    FloatingActionButton mAddFab, mAddParentFab, mAddCalfFab, mDeleteCowFab, mTransferOwnershipFab, mChangeTagNumberFab, mChangeSexFab;

    // These are taken to make visible and invisible along
    // with FABs
    TextView mAddParentActionText, mAddCalfActionText, mDeleteCowActionText, mTransferOwnershipActionText, mChangeTagNumberActionText, mChangeSexActionText;

    // to check whether sub FAB buttons are visible or not.
    Boolean isAllFabsVisible;

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


        // Register all the FABs with their IDs
        // This FAB button is the Parent
        mAddFab = findViewById(R.id.add_fab);
        // FAB button
        mAddParentFab = findViewById(R.id.add_parent_fab);
        mAddCalfFab = findViewById(R.id.add_calf_fab);
        mDeleteCowFab = findViewById(R.id.delete_cow_fab);
        mTransferOwnershipFab = findViewById(R.id.transfer_ownership_fab);
        mChangeTagNumberFab = findViewById(R.id.change_tag_number_fab);
        mChangeSexFab = findViewById(R.id.change_sex_fab);

        // Also register the action name text, of all the FABs.
        mAddParentActionText = findViewById(R.id.add_parent_action_text);
        mAddCalfActionText = findViewById(R.id.add_calf_action_text);
        mDeleteCowActionText = findViewById(R.id.delete_cow_action_text);
        mTransferOwnershipActionText = findViewById(R.id.transfer_ownership_action_text);
        mChangeTagNumberActionText = findViewById(R.id.change_tag_number_action_text);
        mChangeSexActionText = findViewById(R.id.change_sex_action_text);

        // Now set all the FABs and all the action name
        // texts as GONE
        mAddParentFab.setVisibility(View.GONE);
        mAddCalfFab.setVisibility(View.GONE);
        mDeleteCowFab.setVisibility(View.GONE);
        mTransferOwnershipFab.setVisibility(View.GONE);
        mChangeSexFab.setVisibility(View.GONE);
        mChangeTagNumberFab.setVisibility(View.GONE);
        mAddParentActionText.setVisibility(View.GONE);
        mAddCalfActionText.setVisibility(View.GONE);
        mDeleteCowActionText.setVisibility(View.GONE);
        mChangeTagNumberActionText.setVisibility(View.GONE);
        mChangeSexActionText.setVisibility(View.GONE);
        mTransferOwnershipActionText.setVisibility(View.GONE);

        // make the boolean variable as false, as all the
        // action name texts and all the sub FABs are invisible
        isAllFabsVisible = false;

        // We will make all the FABs and action name texts
        // visible only when Parent FAB button is clicked So
        // we have to handle the Parent FAB button first, by
        // using setOnClickListener you can see below
        mAddFab.setOnClickListener(
                view -> {
                    if (!isAllFabsVisible) {
                        // when isAllFabsVisible becomes
                        // true make all the action name
                        // texts and FABs VISIBLE.
                        mAddParentFab.show();
                        mAddCalfFab.show();
                        mDeleteCowFab.show();
                        mTransferOwnershipFab.show();
                        mChangeTagNumberFab.show();
                        mChangeSexFab.show();
                        mAddParentActionText.setVisibility(View.VISIBLE);
                        mAddCalfActionText.setVisibility(View.VISIBLE);
                        mDeleteCowActionText.setVisibility(View.VISIBLE);
                        mTransferOwnershipActionText.setVisibility(View.VISIBLE);
                        mChangeTagNumberActionText.setVisibility(View.VISIBLE);
                        mChangeSexActionText.setVisibility(View.VISIBLE);

                        // make the boolean variable true as
                        // we have set the sub FABs
                        // visibility to GONE
                        isAllFabsVisible = true;
                    } else {

                        // when isAllFabsVisible becomes
                        // true make all the action name
                        // texts and FABs GONE.
                        mAddParentFab.hide();
                        mAddCalfFab.hide();
                        mDeleteCowFab.hide();
                        mTransferOwnershipFab.hide();
                        mChangeTagNumberFab.hide();
                        mChangeSexFab.hide();
                        mAddParentActionText.setVisibility(View.GONE);
                        mAddCalfActionText.setVisibility(View.GONE);
                        mDeleteCowActionText.setVisibility(View.GONE);
                        mTransferOwnershipActionText.setVisibility(View.GONE);
                        mChangeSexActionText.setVisibility(View.GONE);

                        // make the boolean variable false
                        // as we have set the sub FABs
                        // visibility to GONE
                        isAllFabsVisible = false;
                    }
                });

        // below is the sample action to handle add person
        // FAB. Here it shows simple Toast msg. The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        mAddParentFab.setOnClickListener(
                view -> {
                    Intent i = new Intent(this, cowAddParentActivity.class);
                    i.putExtra("tagNumber", tagNumber);
                    this.startActivity(i);
                    //this.finish();
                });

        // below is the sample action to handle add alarm
        // FAB. Here it shows simple Toast msg The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        mTransferOwnershipFab.setOnClickListener(
                view -> {
                    Intent i = new Intent(this, cowTransferOwnershipActivity.class);
                    i.putExtra("tagNumber", tagNumber);
                    this.startActivity(i);
                });
        mDeleteCowFab.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Delete Cow")
                    .setMessage("Are you sure you want to delete " + tagNumber + "? This action is IRREVERSIBLE")
                    .setNegativeButton("No", (dialog1, which) -> {
                        //Do nothing
                    })
                    .setPositiveButton("Yes", (dialog1, which) -> {
                        deleteCow(tagNumber);
                        AlertDialog success_dialog = new AlertDialog.Builder(this)
                                .setTitle("Deleted")
                                .setMessage(tagNumber + " has been deleted")
                                .setPositiveButton("Ok", null)
                                .create();
                        success_dialog.show();
                    })
                    .create();
            dialog.show();
        });
        mChangeTagNumberFab.setOnClickListener(v -> {
            final EditText input = new EditText(this);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Change Tag Number")
                    .setView(input)
                    .setMessage("Enter a new tag number for " + tagNumber + ":")
                    .setNegativeButton("Cancel", (dialog1, which) -> {
                        //Do nothing
                    })
                    .setPositiveButton("Change", (dialog1, which) -> {
                        String new_tag = input.getText().toString().trim();
                        String old_tag = tagNumber;
                        Map newTagDict = new HashMap();
                        newTagDict.put("old_tag", old_tag);
                        newTagDict.put("new_tag", new_tag);
                        JSONObject newTagJSON = new JSONObject(newTagDict);

                        new makeHTTPRequest(
                                "cows/change_tag",
                                newTagJSON.toString(),
                                chang_tag_response -> {
                                    AlertDialog success_dialog = new AlertDialog.Builder(this)
                                            .setTitle("Tag Number Changed")
                                            .setMessage(tagNumber + " is now " + new_tag)
                                            .setPositiveButton("Ok", null)
                                            .create();
                                    success_dialog.show();
                                },
                                error -> {
                                    error.printStackTrace();
                                },
                                this
                        );
                    })
                    .create();
            dialog.show();

        });
        mChangeSexFab.setOnClickListener(v -> {
            final Spinner sexSpinner = new Spinner(this);

            new makeHTTPRequest(
                    "cows/sex_list",
                    "",
                    sex_list_response -> {
                        try {
                            JSONArray sexes_json = sex_list_response.getJSONArray("sexes");
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
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Change Sex")
                    .setView(sexSpinner)
                    .setNegativeButton("Cancel", (dialog1, which) -> {
                        //Do nothing
                    })
                    .setPositiveButton("Change", (dialog1, which) -> {
                        String sex = sexSpinner.getSelectedItem().toString();
                        String tag_number = tagNumber;
                        Map newTagDict = new HashMap();
                        newTagDict.put("tag_number", tag_number);
                        newTagDict.put("sex", sex);
                        JSONObject newTagJSON = new JSONObject(newTagDict);

                        new makeHTTPRequest(
                                "cows/change_sex",
                                newTagJSON.toString(),
                                chang_tag_response -> {
                                    AlertDialog success_dialog = new AlertDialog.Builder(this)
                                            .setTitle("Sex Changed")
                                            .setMessage(tagNumber + " is now a " + sex)
                                            .setPositiveButton("Ok", null)
                                            .create();
                                    success_dialog.show();
                                },
                                error -> {
                                    error.printStackTrace();
                                },
                                this
                        );
                    })
                    .create();
            dialog.show();

        });
        mAddCalfFab.setOnClickListener(
            view -> {
                Intent i = new Intent(this, cowAddCalfActivity.class);
                i.putExtra("tagNumber", tagNumber);
                this.startActivity(i);
        });

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
                Toast.makeText(this, "Loading: " + damTagNumber, Toast.LENGTH_SHORT).show();
                new makeHTTPRequest(
                        "cows/cow",
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
                            this.finish();
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
                Toast.makeText(this, "Loading: " + sireTagNumber, Toast.LENGTH_SHORT).show();
                new makeHTTPRequest(
                        "cows/cow",
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
                            this.finish();

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
            CardView newCardView = new CardView(this);
            TextView newTextView = new TextView(this);
            newCardView.addView(newTextView);
            TextView something = new TextView(this);
            try {
                something.setText(calves.getJSONArray(i).getString(1).replace("+"," "));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            something.setPadding(30, 95, 0, 0);
            something.setTextColor(this.getColor(R.color.design_default_color_on_primary));
            newCardView.addView(something);
            LinearLayout calfList = findViewById(R.id.offspringScrollViewLinearLayout);
            calfList.addView(newCardView);

            newCardView.setCardBackgroundColor(this.getColor(R.color.blue));
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) newCardView.getLayoutParams();
            params.height = 160;
            params.topMargin = 50;
            newCardView.setRadius(25);
            newTextView.setTextColor(this.getColor(R.color.design_default_color_on_primary));
            try {
                newTextView.setText(calves.getJSONArray(i).getString(0).replace("+"," "));
                newTextView.setHint(calves.getJSONArray(i).getString(0).replace("+"," "));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            newTextView.setTextSize(20);
            newTextView.setPadding(30, 30, 0, 0);
            newTextView.setOnClickListener(view -> {
                System.out.println(((TextView) view).getHint().toString());
                final String calfTagNumber = ((TextView) view).getHint().toString();
                Toast.makeText(this, "Loading: " + calfTagNumber, Toast.LENGTH_SHORT).show();
                new makeHTTPRequest(
                        "cows/cow",
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
                            this.finish();

                        }, error -> {

                },
                        this
                );
            });
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
    public void deleteCow(String tagNumber) {
        new makeHTTPRequest(
                "cows/delete",
                tagNumber,
                response -> {

                },
                error -> {
                },
                this
        );
    }


}
