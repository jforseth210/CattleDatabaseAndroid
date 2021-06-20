package tech.jforseth.cattledatabase.ui.cows;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import tech.jforseth.cattledatabase.cowAddCowActivity;
import tech.jforseth.cattledatabase.cowAddParentActivity;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CowViewModel cowViewModel = new ViewModelProvider(this).get(CowViewModel.class);

        binding = FragmentCowsBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        resetLookupText();
        binding.addCowButton.setOnClickListener(view -> lookupCow());
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
                    i.putExtra("tagNumber", binding.editTextTagNumber.getText().toString().trim());
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
        mDeleteCowFab.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                    .setTitle("Delete Cow")
                    .setMessage("Are you sure you want to delete " + binding.editTextTagNumber.getText().toString().trim() + "? This action is IRREVERSIBLE")
                    .setNegativeButton("No", (dialog1, which) -> {
                        //Do nothing
                    })
                    .setPositiveButton("Yes", (dialog1, which) -> {
                        deleteCow();
                        AlertDialog success_dialog = new AlertDialog.Builder(requireActivity())
                                .setTitle("Deleted")
                                .setMessage(binding.editTextTagNumber.getText().toString().trim() + " has been deleted")
                                .setPositiveButton("Ok", null)
                                .create();
                        success_dialog.show();
                        resetLookupText();
                    })
                    .create();
            dialog.show();
        });
        mChangeTagNumberFab.setOnClickListener(v -> {
            final EditText input = new EditText(requireActivity());
            AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                    .setTitle("Change Tag Number")
                    .setView(input)
                    .setMessage("Enter a new tag number for " + binding.editTextTagNumber.getText().toString().trim() + ":")
                    .setNegativeButton("Cancel", (dialog1, which) -> {
                        //Do nothing
                    })
                    .setPositiveButton("Change", (dialog1, which) -> {
                        String new_tag = input.getText().toString().trim();
                        String old_tag = binding.editTextTagNumber.getText().toString().trim();
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
                                            .setMessage(binding.editTextTagNumber.getText().toString().trim() + " is now " + new_tag)
                                            .setPositiveButton("Ok", null)
                                            .create();
                                    success_dialog.show();
                                    resetLookupText();
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
        return root;
    }

    public void lookupCow() {
        String endpoint = "cow";
        String data = binding.editTextTagNumber.getText().toString().trim();
        System.out.println(data);
        new makeHTTPRequest(
                endpoint,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        updateCowInformationUI(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getCause());
                        System.out.println(error.getClass());
                        error.printStackTrace();
                    }
                },
                getActivity()
        );
    }

    public void updateCowInformationUI(JSONObject object) {
        try {
            binding.textViewTagNumber.setText("Tag Number: " + object.getString("tag_number"));
            binding.textViewOwner.setText("Owner: " + object.getString("owner"));
            binding.textViewSex.setText("Sex: " + object.getString("sex"));
            binding.textViewDam.setText("Dam: " + object.getString("dam"));
            binding.textViewSire.setText("Sire: " + object.getString("sire"));
            binding.textViewSire.setText("Sire: " + object.getString("sire"));
            JSONArray calves = object.getJSONArray("calves");
            StringBuilder calvesString = new StringBuilder();
            for (int i = 0; i < calves.length(); i++) {
                if (i > 0) {
                    calvesString.append(", ");
                }
                calvesString.append(calves.getString(i));
            }
            binding.textViewCalves.setText("Calves: " + calvesString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void resetLookupText() {
        binding.addCowButton.setText("Lookup");
        binding.textViewTagNumber.setText("Tag Number: ");
        binding.textViewOwner.setText("Owner: ");
        binding.textViewSex.setText("Sex: ");
        binding.textViewDam.setText("Dam: ");
        binding.textViewSire.setText("Sire: ");
        binding.textViewCalves.setText("Calves: ");
        binding.editTextTagNumber.setHint("Tag Number");
        binding.editTextTagNumber.setText("");
    }

    public void deleteCow() {
        new makeHTTPRequest(
                "delete_cow",
                binding.editTextTagNumber.getText().toString().trim(),
                response -> resetLookupText(),
                error -> {
                    System.out.println("Error class:" + error.getClass());
                    //error.printStackTrace();
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

