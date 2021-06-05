package tech.jforseth.cattledatabase.ui.cows;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import tech.jforseth.cattledatabase.MainActivity;
import tech.jforseth.cattledatabase.databinding.FragmentCowsBinding;

import tech.jforseth.cattledatabase.makeHTTPRequest;

public class CowFragment extends Fragment implements makeHTTPRequest.AsyncResponse {

    private CowViewModel cowViewModel;
    private FragmentCowsBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cowViewModel =
                new ViewModelProvider(this).get(CowViewModel.class);

        binding = FragmentCowsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.addCowButton.setText("Lookup");
        binding.textViewTagNumber.setText("Tag Number: ");
        binding.textViewOwner.setText("Owner: ");
        binding.textViewSex.setText("Sex: ");
        binding.textViewDam.setText("Dam: ");
        binding.textViewSire.setText("Sire: ");
        binding.textViewCalves.setText("Calves: ");
        binding.editTextTagNumber.setHint("Tag Number");
        binding.addCowButton.setOnClickListener(view -> lookUPCow());
        cowViewModel.getText().observe(getViewLifecycleOwner(), s -> {
        });
        return root;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void lookUPCow(){
        makeHTTPRequest request = new makeHTTPRequest(this, getContext());
        SharedPreferences preferences = getActivity().getSharedPreferences("tech.jforseth.CattleDatabase", MainActivity.MODE_PRIVATE);
        String url = "";
        try{
            url = preferences.getString("serverIPLAN", "") + "/api/cow/"+ URLEncoder.encode(binding.editTextTagNumber.getText().toString(), StandardCharsets.UTF_8.toString());
        } catch (Exception e){
            e.printStackTrace();
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
        try {
            System.out.println(output);
            JSONObject object = new JSONObject(output);
            binding.textViewTagNumber.setText("Tag Number: " + object.getString("tag_number"));
            binding.textViewOwner.setText("Owner: " + object.getString("owner"));
            binding.textViewSex.setText("Sex: " + object.getString("sex"));
            binding.textViewDam.setText("Dam: " + object.getString("dam"));
            binding.textViewSire.setText("Sire: " + object.getString("sire"));
            binding.textViewSire.setText("Sire: " + object.getString("sire"));
            JSONArray calves = object.getJSONArray("calves");
            StringBuilder calvesString = new StringBuilder();
            for (int i = 0; i < calves.length(); i++){
                if (i > 0) {
                    calvesString.append(", ");
                }
                    calvesString.append(calves.getString(i));
            }
            binding.textViewCalves.setText("Calves: " + calvesString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

