package tech.jforseth.cattledatabase.ui.cows;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tech.jforseth.cattledatabase.R;
import tech.jforseth.cattledatabase.cowAddCowActivity;
import tech.jforseth.cattledatabase.cowAddParentActivity;
import tech.jforseth.cattledatabase.cowInformation;
import tech.jforseth.cattledatabase.cowTransferOwnershipActivity;
import tech.jforseth.cattledatabase.databinding.FragmentCowsBinding;
import tech.jforseth.cattledatabase.makeHTTPRequest;

public class CowFragment extends Fragment{

    private FragmentCowsBinding binding;
    RequestQueue requestQueue;
    String currentCowTagNumber;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CowViewModel cowViewModel = new ViewModelProvider(this).get(CowViewModel.class);

        binding = FragmentCowsBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        cowViewModel.getText().observe(getViewLifecycleOwner(), s -> {
        });

        requestQueue = Volley.newRequestQueue(requireActivity());
        loadCows();
        ImageView refreshButton = binding.refreshButton;
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Reloading!", Toast.LENGTH_SHORT).show();
                loadCows();
            }
        });

        return root;
    }
    public void loadCows(){
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
                        CardView newCardView = new CardView(requireActivity());
                        TextView newTextView = new TextView(requireActivity());
                        newCardView.addView(newTextView);
                        TextView something = new TextView(requireActivity());
                        something.setText("Justin");
                        something.setPadding(30, 95, 0, 0);
                        something.setTextColor(getActivity().getColor(R.color.design_default_color_on_primary));
                        newCardView.addView(something);
                        binding.cowList.addView(newCardView);

                        newCardView.setCardBackgroundColor(getActivity().getColor(R.color.green));
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) newCardView.getLayoutParams();
                        params.height = 160; params.topMargin = 50;
                        newCardView.setRadius(25);
                        newTextView.setTextColor(getActivity().getColor(R.color.design_default_color_on_primary));
                        try {
                            newTextView.setText(cows.getString(i));
                            newTextView.setHint(cows.getString(i));
                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                        newTextView.setTextSize(20);
                        newTextView.setPadding(30,30,0,0);
                        newTextView.setOnClickListener(view -> {
                            TextView textView = (TextView)view;
                            currentCowTagNumber = ((TextView) view).getHint().toString();
                            Toast.makeText(getActivity(), "Loading: "+ currentCowTagNumber, Toast.LENGTH_SHORT).show();
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
        FloatingActionButton mAddCowFab = binding.addCowFab;
        mAddCowFab.setOnClickListener(
                view -> {
                    Intent i = new Intent(getActivity(), cowAddCowActivity.class);
                    this.startActivity(i);
                    //this.finish();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

