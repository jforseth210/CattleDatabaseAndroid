package tech.jforseth.cattledatabase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class makeHTTPRequest {
    Response.Listener<org.json.JSONObject> listener;
    Response.ErrorListener errorListener;
    RequestQueue requestQueue;
    private String endpoint;
    private Context context;
    private String mode;
    private String url;
    private String data;
    private int failures;
    public makeHTTPRequest(String endpoint, String data,
                           Response.Listener<org.json.JSONObject> listener,
                           Response.ErrorListener errorListener,
                           Context context) {
        this.context = context;
        this.endpoint = endpoint;
        this.data = data;
        this.listener = listener;
        this.errorListener = errorListener;
        this.failures = 0;
        Cache cache = new DiskBasedCache(this.context.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        this.mode = "LAN";
        generateUrl();
        sendRequest();
        this.mode = "WAN";
        generateUrl();
        sendRequest();
    }
    public makeHTTPRequest(String url, Response.Listener<org.json.JSONObject> listener,
                           Response.ErrorListener errorListener, Context context){
        this.url = url;
        this.context = context;
        this.listener = listener;
        this.errorListener = errorListener;
        Cache cache = new DiskBasedCache(this.context.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        sendRequest();
    }

    public void generateUrl() {
        SharedPreferences preferences = this.context.getSharedPreferences("tech.jforseth.CattleDatabase", MainActivity.MODE_PRIVATE);
        try {
            this.url = preferences.getString("server_" + this.mode + "_address", "")
                    + "/api/"
                    + this.endpoint
                    + "/"
                    + URLEncoder.encode(
                    this.data,
                    StandardCharsets.UTF_8.toString()
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            this.url = "";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String generateAuthEncoding() {
        SharedPreferences preferences = this.context.getSharedPreferences("tech.jforseth.CattleDatabase", MainActivity.MODE_PRIVATE);
        String plain_auth = preferences.getString("username", "") + ":" + preferences.getString("password", "");
        String encoded_auth = Base64.getEncoder().encodeToString(plain_auth.getBytes());
        return encoded_auth;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getFailures(){
        return this.failures;
    }
    public void incrementFailures(){
        this.failures++;
    }

    public Response.ErrorListener getErrorListener() {
        return errorListener;
    }

    private void sendRequest() {
        JsonObjectRequest
                jsonObjectRequest
                = new JsonObjectRequest(
                Request.Method.POST,
                this.url,
                null,
                listener,
                error -> {
                    // Only display a toast if both requests fail:
                    if (getFailures() >= 1) {
                        Response.ErrorListener errorListener = getErrorListener();
                        errorListener.onErrorResponse(error);
                        error.printStackTrace();
                        try {
                            Toast.makeText(this.context, "Error: " + error.getMessage().substring(error.getMessage().indexOf(":") + 2), Toast.LENGTH_SHORT).show();
                        } catch (NullPointerException e){
                            if (error.getClass().getName().equals("com.android.volley.AuthFailureError"))
                                Toast.makeText(this.context, "Error: Authentication Failed", Toast.LENGTH_SHORT).show();
                            else if (error.getClass().getName().equals("com.android.volley.TimeoutError"))
                                Toast.makeText(this.context, "Error: Connection timed out", Toast.LENGTH_SHORT).show();
                            else if (error.getClass().getName().equals("com.android.volley.ServerError"))
                                Toast.makeText(this.context, "Server Error: Something is probably wrong with the host computer", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(this.context, "Error: Unknown error", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        incrementFailures();
                    }
                }) {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Basic " + generateAuthEncoding());
                params.put("content-type", "application/json");
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}
