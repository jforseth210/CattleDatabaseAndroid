package tech.jforseth.cattledatabase.ui.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import tech.jforseth.cattledatabase.R;
import tech.jforseth.cattledatabase.databinding.FragmentTransactionsBinding;
import tech.jforseth.cattledatabase.makeHTTPRequest;

public class TransactionFragment extends Fragment {

    RequestQueue requestQueue;
    String currenttransactionTagNumber;
    private FragmentTransactionsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TransactionViewModel transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        binding = FragmentTransactionsBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        transactionViewModel.getText().observe(getViewLifecycleOwner(), s -> {
        });

        requestQueue = Volley.newRequestQueue(requireActivity());
        loadtransactions();
        ImageView refreshButton = binding.refreshButton;
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Reloading!", Toast.LENGTH_SHORT).show();
                loadtransactions();
            }
        });

        FloatingActionButton mAddtransactionFab = binding.addtransactionFab;
        mAddtransactionFab.setOnClickListener(
                view -> {
                    //Intent i = new Intent(getActivity(), TransactionAddTransactionActivity.class);
                    //this.startActivity(i);
                });

        return root;
    }

    public void loadtransactions() {
        new makeHTTPRequest(
                "transactions/get_list",
                "",
                response -> {
                    JSONArray transactions = new JSONArray();
                    try {
                        transactions = response.getJSONArray("transactions");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < transactions.length(); i++) {
                        CardView newCardView = new CardView(requireActivity());
                        TextView newTextView = new TextView(requireActivity());
                        newCardView.addView(newTextView);
                        TextView something = new TextView(requireActivity());
                        try {
                            something.setText(transactions.getJSONArray(i).getString(2).replace("+"," "));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        something.setPadding(30, 95, 0, 0);
                        something.setTextColor(getActivity().getColor(R.color.design_default_color_on_primary));
                        newCardView.addView(something);
                        binding.transactionList.addView(newCardView);

                        newCardView.setCardBackgroundColor(getActivity().getColor(R.color.blue));
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) newCardView.getLayoutParams();
                        params.height = 160;
                        params.topMargin = 50;
                        newCardView.setRadius(25);
                        newTextView.setTextColor(getActivity().getColor(R.color.design_default_color_on_primary));
                        try {
                            newTextView.setText(transactions.getJSONArray(i).getString(1).replace("+"," "));
                            newTextView.setHint(transactions.getJSONArray(i).getString(0).replace("+"," "));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        newTextView.setTextSize(20);
                        newTextView.setPadding(30, 30, 0, 0);
                        newTextView.setOnClickListener(view -> {
                            TextView textView = (TextView) view;
                            currenttransactionTagNumber = ((TextView) view).getHint().toString();
                            Toast.makeText(getActivity(), "Loading: " + currenttransactionTagNumber, Toast.LENGTH_SHORT).show();
                            lookuptransaction();
                        });
                    }

                },
                error -> {
                    /*
                    Snackbar.make(getView(), "Error loading transactions: " + error.getMessage().substring(error.getMessage().indexOf(":") + 2), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).setDuration(5000).show();
                */
                },
                requireActivity()
        );
    }

    public void lookuptransaction() {
        String endpoint = "transactions/transaction";
        String data = currenttransactionTagNumber;
        System.out.println(data);
        new makeHTTPRequest(
                endpoint,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //updatetransactionInformationUI(response);
                        //Intent i = new Intent(getActivity(), TransactionInformation.class);
                        //i.putExtra("tagNumber", currenttransactionTagNumber);
                        /*
                         * As far is I can tell, there is no way to send a JSONObject
                         * directly, so we send it as a string instead and parse it
                         * back on the other end.
                         */

                        //i.putExtra("jsonData", response.toString());
                        //getActivity().startActivity(i);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

