package tech.jforseth.cattledatabase.ui.cows;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import tech.jforseth.cattledatabase.MainActivity;
import tech.jforseth.cattledatabase.R;
import tech.jforseth.cattledatabase.cowAddCowActivity;
import tech.jforseth.cattledatabase.cowAddParentActivity;
import tech.jforseth.cattledatabase.cowInformation;
import tech.jforseth.cattledatabase.cowTransferOwnershipActivity;
import tech.jforseth.cattledatabase.databinding.FragmentCowsBinding;
import tech.jforseth.cattledatabase.makeHTTPRequest;

public class CowFragment extends Fragment{

    private FragmentCowsBinding binding;

    // Make sure to use the FloatingActionButton
    // for all the FABs
    FloatingActionButton mAddFab, mAddCowFab, mAddParentFab, mAddCalfFab, mDeleteCowFab, mTransferOwnershipFab, mChangeTagNumberFab;

    // These are taken to make visible and invisible along
    // with FABs
    TextView mAddCowActionText, mAddParentActionText, mAddCalfActionText, mDeleteCowActionText, mTransferOwnershipActionText, mChangeTagNumberActionText;

    // to check whether sub FAB buttons are visible or not.
    Boolean isAllFabsVisible;
    RequestQueue requestQueue;
    String currentCowTagNumber;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CowViewModel cowViewModel = new ViewModelProvider(this).get(CowViewModel.class);

        binding = FragmentCowsBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        cowViewModel.getText().observe(getViewLifecycleOwner(), s -> {
        });

        // Register all the FABs with their IDs
        // This FAB button is the Parent
        mAddFab = binding.addFab;
        // FAB button
        mAddCowFab = binding.addCowFab;
        mAddParentFab = binding.addParentFab;
        mAddCalfFab = binding.addCalfFab;
        mDeleteCowFab = binding.deleteCowFab;
        mTransferOwnershipFab = binding.transferOwnershipFab;
        mChangeTagNumberFab = binding.changeTagNumberFab;

        // Also register the action name text, of all the FABs.
        mAddCowActionText = binding.addCowActionText;
        mAddParentActionText = binding.addParentActionText;
        mAddCalfActionText = binding.addCalfActionText;
        mDeleteCowActionText = binding.deleteCowActionText;
        mTransferOwnershipActionText = binding.transferOwnershipActionText;
        mChangeTagNumberActionText = binding.changeTagNumberActionText;

        // Now set all the FABs and all the action name
        // texts as GONE
        mAddCowFab.setVisibility(View.GONE);
        mAddParentFab.setVisibility(View.GONE);
        mAddCalfFab.setVisibility(View.GONE);
        mDeleteCowFab.setVisibility(View.GONE);
        mTransferOwnershipFab.setVisibility(View.GONE);
        mChangeTagNumberFab.setVisibility(View.GONE);
        mAddCowActionText.setVisibility(View.GONE);
        mAddParentActionText.setVisibility(View.GONE);
        mAddCalfActionText.setVisibility(View.GONE);
        mDeleteCowActionText.setVisibility(View.GONE);
        mChangeTagNumberActionText.setVisibility(View.GONE);
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
                        mAddCowFab.show();
                        mAddParentFab.show();
                        mAddCalfFab.show();
                        mDeleteCowFab.show();
                        mTransferOwnershipFab.show();
                        mChangeTagNumberFab.show();
                        mAddCowActionText.setVisibility(View.VISIBLE);
                        mAddParentActionText.setVisibility(View.VISIBLE);
                        mAddCalfActionText.setVisibility(View.VISIBLE);
                        mDeleteCowActionText.setVisibility(View.VISIBLE);
                        mTransferOwnershipActionText.setVisibility(View.VISIBLE);
                        mChangeTagNumberActionText.setVisibility(View.VISIBLE);

                        // make the boolean variable true as
                        // we have set the sub FABs
                        // visibility to GONE
                        isAllFabsVisible = true;
                    } else {

                        // when isAllFabsVisible becomes
                        // true make all the action name
                        // texts and FABs GONE.
                        mAddCowFab.hide();
                        mAddParentFab.hide();
                        mAddCalfFab.hide();
                        mDeleteCowFab.hide();
                        mTransferOwnershipFab.hide();
                        mChangeTagNumberFab.hide();
                        mAddCowActionText.setVisibility(View.GONE);
                        mAddParentActionText.setVisibility(View.GONE);
                        mAddCalfActionText.setVisibility(View.GONE);
                        mDeleteCowActionText.setVisibility(View.GONE);
                        mTransferOwnershipActionText.setVisibility(View.GONE);
                        mChangeTagNumberActionText.setVisibility(View.GONE);

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
                    Intent i = new Intent(getActivity(), cowAddParentActivity.class);
                    i.putExtra("tagNumber", currentCowTagNumber);
                    requireActivity().startActivity(i);
                    //getActivity().finish();
                });

        // below is the sample action to handle add alarm
        // FAB. Here it shows simple Toast msg The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        mAddCowFab.setOnClickListener(
                view -> {
                    Intent i = new Intent(requireActivity(), cowAddCowActivity.class);
                    requireActivity().startActivity(i);
                    //getActivity().finish();
                });
        mTransferOwnershipFab.setOnClickListener(
                view -> {
                    Intent i = new Intent(getActivity(), cowTransferOwnershipActivity.class);
                    i.putExtra("tagNumber", currentCowTagNumber);
                    requireActivity().startActivity(i);
                });
        mDeleteCowFab.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                    .setTitle("Delete Cow")
                    .setMessage("Are you sure you want to delete " + currentCowTagNumber + "? This action is IRREVERSIBLE")
                    .setNegativeButton("No", (dialog1, which) -> {
                        //Do nothing
                    })
                    .setPositiveButton("Yes", (dialog1, which) -> {
                        deleteCow();
                        AlertDialog success_dialog = new AlertDialog.Builder(requireActivity())
                                .setTitle("Deleted")
                                .setMessage(currentCowTagNumber + " has been deleted")
                                .setPositiveButton("Ok", null)
                                .create();
                        success_dialog.show();
                    })
                    .create();
            dialog.show();
        });
        mChangeTagNumberFab.setOnClickListener(v -> {
            final EditText input = new EditText(requireActivity());
            AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                    .setTitle("Change Tag Number")
                    .setView(input)
                    .setMessage("Enter a new tag number for " + currentCowTagNumber + ":")
                    .setNegativeButton("Cancel", (dialog1, which) -> {
                        //Do nothing
                    })
                    .setPositiveButton("Change", (dialog1, which) -> {
                        String new_tag = input.getText().toString().trim();
                        String old_tag = currentCowTagNumber;
                        Map newTagDict = new HashMap();
                        newTagDict.put("old_tag", old_tag);
                        newTagDict.put("new_tag", new_tag);
                        JSONObject newTagJSON = new JSONObject(newTagDict);

                        new makeHTTPRequest(
                                "change_tag",
                                newTagJSON.toString(),
                                response -> {
                                    AlertDialog success_dialog = new AlertDialog.Builder(requireActivity())
                                            .setTitle("Tag Number Changed")
                                            .setMessage(currentCowTagNumber + " is now " + new_tag)
                                            .setPositiveButton("Ok", null)
                                            .create();
                                    success_dialog.show();
                                },
                                error -> {
                                    error.printStackTrace();
                                },
                                requireActivity()
                        );
                    })
                    .create();
            dialog.show();

        });

        requestQueue = Volley.newRequestQueue(requireActivity());
        new makeHTTPRequest(
                "get_cow_list",
                "",
                response -> {
                    JSONArray cows = new JSONArray();
                    try {
                         cows = response.getJSONArray("cows");
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                        for (int i = 0; i < cows.length(); i++) {
                            TextView ntext = new TextView(requireActivity());
                            binding.cowList.addView(ntext);

                            try {
                                ntext.setText("• " + cows.getString(i));
                                ntext.setHint(cows.getString(i));
                            } catch (JSONException e){
                                e.printStackTrace();
                            }

                            ntext.setTextSize(20);
                            ntext.setPadding(0,25,0,0);
                            ntext.setPaintFlags(ntext.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                            ntext.setOnClickListener(view -> {
                                TextView textView = (TextView)view;
                                currentCowTagNumber = ((TextView) view).getHint().toString();
                                lookupCow();
                            });
                        }

                },
                error -> {
                    /*
                    Snackbar.make(getView(), "Error loading cows: " + error.getMessage().substring(error.getMessage().indexOf(":") + 2), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).setDuration(5000).show();
                */
                },
                requireActivity()
        );

        return root;
    }

    public void lookupCow() {
        String endpoint = "cow";
        String data = currentCowTagNumber;
        System.out.println(data);
        new makeHTTPRequest(
                endpoint,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //updateCowInformationUI(response);
                        Intent i = new Intent(getActivity(), cowInformation.class);
                        i.putExtra("tagNumber", currentCowTagNumber);
                        /*
                         * As far is I can tell, there is no way to send a JSONObject
                         * directly, so we send it as a string instead and parse it
                         * back on the other end.
                         */

                        i.putExtra("jsonData", response.toString());
                        getActivity().startActivity(i);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
                getActivity()
        );
    }

    public void deleteCow() {
        new makeHTTPRequest(
                "delete_cow",
                currentCowTagNumber,
                response -> {

                },
                error -> {
                },
                getActivity()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

