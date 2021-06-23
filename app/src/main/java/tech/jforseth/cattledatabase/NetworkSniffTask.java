package tech.jforseth.cattledatabase;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;

import static android.content.Context.MODE_PRIVATE;

public class NetworkSniffTask extends AsyncTask<Void, String, JSONObject> {

    private static final String TAG = "" + "nstask";
    private static boolean success = false;
    public Fragment fragment = null;
    private Context context = null;

    public NetworkSniffTask(Fragment fragment, Context context) {
        this.fragment = fragment;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONObject output) {}

    @Override
    protected void onProgressUpdate(String... values) {
        try {
            TextView textViewScanning = fragment.getView().findViewById(R.id.textViewScanning);
            textViewScanning.setText(values[0]);
        } catch (NullPointerException e){

        }
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        Log.d(TAG, "Let's sniff the network");

        try {
            Context context = this.context;

            if (context != null) {

                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                WifiInfo connectionInfo = wm.getConnectionInfo();
                int ipAddress = connectionInfo.getIpAddress();
                String ipString = Formatter.formatIpAddress(ipAddress);


                Log.d(TAG, "activeNetwork: " + String.valueOf(activeNetwork));
                Log.d(TAG, "ipString: " + String.valueOf(ipString));

                String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
                Log.d(TAG, "prefix: " + prefix);

                for (int i = 0; i < 255; i++) {
                    if (getSuccess())
                        break;
                    String testIp = prefix + String.valueOf(i);

                    InetAddress address = InetAddress.getByName(testIp);
                    boolean reachable = address.isReachable(100);
                    System.out.println("SCAN - Trying: " + testIp);

                    publishProgress("Scanning: " + testIp);
                    if (reachable) {
                        System.out.println("\n\nSCAN - " + testIp + " is up!");
                        String url = "http://" + testIp + ":5000/api/get_server_info";
                        new makeHTTPRequest(url,
                                response -> {
                                    setSuccess(true);
                                    System.out.println("SCAN - " + testIp + " IS a CattleDB server");
                                    System.out.println("Saving preferences");
                                    SharedPreferences preferences = context.getSharedPreferences("tech.jforseth.CattleDatabase", MODE_PRIVATE);
                                    SharedPreferences.Editor pref_editor = preferences.edit();
                                    try {
                                        pref_editor.putString("server_LAN_address", "http://" + response.getString("LAN_address") + ":5000");
                                        pref_editor.putString("server_WAN_address", "http://" + response.getString("WAN_address") + ":5000");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    pref_editor.apply();
                                    System.out.println("Going to next page:");
                                    NavHostFragment.findNavController(fragment)
                                            .navigate(R.id.action_First2Fragment_to_Second2Fragment);
                                }, error -> {
                            System.out.println("SCAN - " + testIp + " is NOT a CattleDB server.");
                        },
                                context);
                        /*
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();

                        con.setConnectTimeout(500);
                        try {
                            System.out.println("SCAN> Trying: " + testIp);
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(con.getInputStream()));
                            String inputLine;
                            StringBuffer content = new StringBuffer();
                            while ((inputLine = in.readLine()) != null) {
                                content.append(inputLine);
                            }
                            in.close();
                            con.disconnect();
                            JSONObject object = new JSONObject(content.toString());
                            System.out.println("SCAN> " + testIp + " is a CattleDB server");
                            System.out.println(object.toString());
                            return object;
                        } catch (Exception e) {
                            System.out.println("SCAN> " + testIp + " is not a CattleDB server: " + e.toString());
                        } */
                    }
                }
                publishProgress("Scan failed. Make sure CattleDB is running on the host computer and restart the app.");
            }
        } catch (Throwable t) {
            Log.e(TAG, "Well that's not good.", t);
        }

        return null;
    }
    private void setSuccess(boolean s){
        success = s;
    }
    private boolean getSuccess(){
        return success;
    }
}