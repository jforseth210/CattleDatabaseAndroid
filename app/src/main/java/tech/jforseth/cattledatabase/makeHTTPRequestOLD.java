package tech.jforseth.cattledatabase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class makeHTTPRequestOLD extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate;
    private Context context;

    public makeHTTPRequestOLD(AsyncResponse delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String output) {
        delegate.processFinish(output);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected String doInBackground(String... urls_as_strings) {
        try {
            for (String url_as_string : urls_as_strings) {
                SharedPreferences preferences = this.context.getSharedPreferences("tech.jforseth.CattleDatabase", MainActivity.MODE_PRIVATE);
                String plain_auth = preferences.getString("username", "") + ":" + preferences.getString("password", "");
                String encoded_auth = Base64.getEncoder().encodeToString(plain_auth.getBytes());
                System.out.println(plain_auth);
                System.out.println(encoded_auth);
                URL url = new URL(url_as_string);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Authorization", "Basic " + encoded_auth);
                con.setInstanceFollowRedirects(true);
                con.setRequestMethod("POST");
                System.out.println("HTTP Code:" + con.getResponseCode());
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                System.out.println("Response:");
                System.out.println(content.toString());
                return content.toString();
            }
            return "No URLs provided";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.toString();
        }
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }
}