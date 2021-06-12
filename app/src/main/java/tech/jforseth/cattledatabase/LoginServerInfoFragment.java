package tech.jforseth.cattledatabase;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

import tech.jforseth.cattledatabase.databinding.FragmentLoginServerInfoBinding;

import static android.content.Context.MODE_PRIVATE;

public class LoginServerInfoFragment extends Fragment implements NetworkSniffTask.AsyncResponse {

    private FragmentLoginServerInfoBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentLoginServerInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.textViewWANAddressHeader.setVisibility(View.GONE);
        binding.editTextWANAddress.setVisibility(View.GONE);
        binding.textViewWANAddressHelperText.setVisibility(View.GONE);

        SharedPreferences preferences = getActivity().getSharedPreferences("tech.jforseth.CattleDatabase", MODE_PRIVATE);
        binding.editTextLANAddress.setText(preferences.getString("server_LAN_address",""));
        binding.editTextWANAddress.setText(preferences.getString("server_WAN_address",""));

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getActivity().getSharedPreferences("tech.jforseth.CattleDatabase", MODE_PRIVATE);
                SharedPreferences.Editor pref_editor = preferences.edit();
                pref_editor.putString("server_LAN_address", binding.editTextLANAddress.getText().toString());
                pref_editor.putString("server_WAN_address", binding.editTextWANAddress.getText().toString());
                pref_editor.apply();
                NavHostFragment.findNavController(LoginServerInfoFragment.this)
                        .navigate(R.id.action_First2Fragment_to_Second2Fragment);
            }
        });
        binding.WANModeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.WANModeCheckBox.isChecked()){
                    binding.textViewWANAddressHeader.setVisibility(View.VISIBLE);
                    binding.editTextWANAddress.setVisibility(View.VISIBLE);
                    binding.textViewWANAddressHelperText.setVisibility(View.VISIBLE);
                } else {
                    binding.textViewWANAddressHeader.setVisibility(View.GONE);
                    binding.editTextWANAddress.setVisibility(View.GONE);
                    binding.textViewWANAddressHelperText.setVisibility(View.GONE);
                }
            }
        });
        System.out.println("hmm");
        scan();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void scan(){
        NetworkSniffTask task = new NetworkSniffTask(this, getActivity());
        task.execute();
    }
    @Override
    public void processFinish(JSONObject output) {
        System.out.println("Saving preferences");
        SharedPreferences preferences = getActivity().getSharedPreferences("tech.jforseth.CattleDatabase", MODE_PRIVATE);
        SharedPreferences.Editor pref_editor = preferences.edit();
        try {
            pref_editor.putString("server_LAN_address", "http://" + output.getString("LAN_address") + ":5000");
            pref_editor.putString("server_WAN_address", "http://" + output.getString("WAN_address") + ":5000");
        } catch (JSONException e){
            e.printStackTrace();
        }
        pref_editor.apply();
        System.out.println("Going to next page:");
        NavHostFragment.findNavController(LoginServerInfoFragment.this)
                .navigate(R.id.action_First2Fragment_to_Second2Fragment);

    }
}
