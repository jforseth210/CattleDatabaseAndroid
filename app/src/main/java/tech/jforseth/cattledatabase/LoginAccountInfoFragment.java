package tech.jforseth.cattledatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import tech.jforseth.cattledatabase.databinding.FragmentLoginAccountInfoBinding;

import static android.content.Context.MODE_PRIVATE;

public class LoginAccountInfoFragment extends Fragment implements makeHTTPRequest.AsyncResponse {

    private FragmentLoginAccountInfoBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentLoginAccountInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences("tech.jforseth.CattleDatabase", MODE_PRIVATE);
        binding.editTextUsername.setText(preferences.getString("username",""));
        binding.buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getActivity().getSharedPreferences("tech.jforseth.CattleDatabase", MODE_PRIVATE);
                SharedPreferences.Editor pref_editor = preferences.edit();
                pref_editor.putString("username", binding.editTextUsername.getText().toString());
                pref_editor.putString("password", binding.editTextPassword.getText().toString());
                pref_editor.apply();
                NavHostFragment.findNavController(LoginAccountInfoFragment.this)
                        .navigate(R.id.action_Second2Fragment_to_First2Fragment);
            }
        });
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences("tech.jforseth.CattleDatabase", MODE_PRIVATE);
                SharedPreferences.Editor pref_editor = preferences.edit();
                pref_editor.putString("username", binding.editTextUsername.getText().toString());
                pref_editor.putString("password", binding.editTextPassword.getText().toString());
                pref_editor.apply();
                testCredentials();
            }
        });
    }
    public void testCredentials(){
        makeHTTPRequest request = new makeHTTPRequest(this, getActivity());
        SharedPreferences preferences = getActivity().getSharedPreferences("tech.jforseth.CattleDatabase", MainActivity.MODE_PRIVATE);
        String url = "";
        try{
            url = preferences.getString("server_LAN_address", "") + "/api/test_credentials";
        } catch (Exception d){
            try {
                url = preferences.getString("server_WAN_address", "") + "/api/test_credentials";
            } catch (Exception e){
                e.printStackTrace();
            }
            }
        System.out.println(url);
        request.execute(url);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void processFinish(String output) {
        if (output.equals("True")){
            SharedPreferences preferences = getActivity().getSharedPreferences("tech.jforseth.CattleDatabase", MODE_PRIVATE);
            SharedPreferences.Editor pref_editor = preferences.edit();
            pref_editor.putBoolean("logged_in", true);
            pref_editor.apply();
            Intent i = new Intent(getActivity(), MainActivity.class);
            getActivity().startActivity(i);
            getActivity().finish();
        } else{
            Snackbar.make(getView(), "Unable to login. Double check information, host computer", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

}