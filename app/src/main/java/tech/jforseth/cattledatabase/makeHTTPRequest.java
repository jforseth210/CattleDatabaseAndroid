package tech.jforseth.cattledatabase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

    public makeHTTPRequest(String endpoint, String data,
                           Response.Listener<org.json.JSONObject> listener,
                           Response.ErrorListener errorListener,
                           Context context) {
        this.context = context;
        this.endpoint = endpoint;
        this.data = data;
        this.listener = listener;
        this.errorListener = errorListener;
        this.mode = "LAN";
        requestQueue = Volley.newRequestQueue(this.context);
        generateUrl();
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
        System.out.println(plain_auth);
        System.out.println(encoded_auth);
        return encoded_auth;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
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
                    if (getMode().equals("LAN")) {
                        setMode("WAN");
                        generateUrl();
                        sendRequest();
                    }
                    Response.ErrorListener errorListener = getErrorListener();
                    errorListener.onErrorResponse(error);
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
