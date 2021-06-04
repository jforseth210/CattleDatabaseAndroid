package tech.jforseth.cattledatabase;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class makeHTTPRequest extends AsyncTask<String, Void, String> {
    public interface AsyncResponse {
        void processFinish(String output);
    }
    public AsyncResponse delegate = null;
    public makeHTTPRequest(AsyncResponse delegate){
        this.delegate = delegate;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String output) {
        delegate.processFinish(output);
    }

    @Override
    protected String doInBackground(String... urls_as_strings) {
        try {
            for (String url_as_string: urls_as_strings) {
                URL url = new URL(url_as_string);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                return content.toString();
            }
            return "No URLs provided";
        } catch (Exception e){
            e.printStackTrace();
            return "Error!";
        }
    }
}