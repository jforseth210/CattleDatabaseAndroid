package tech.jforseth.cattledatabase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

public class NetworkSniffTask extends AsyncTask<Void, Void, JSONObject> {

    private static final String TAG = "" + "nstask";

    public AsyncResponse delegate = null;
    private Context context = null;

    public NetworkSniffTask(AsyncResponse delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONObject output) {
        delegate.processFinish(output);
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
                    String testIp = prefix + String.valueOf(i);

                    InetAddress address = InetAddress.getByName(testIp);
                    boolean reachable = address.isReachable(500);
                    String hostName = address.getCanonicalHostName();

                    if (reachable) {
                        URL url = new URL("http://" + testIp + ":5000/api/get_server_info");
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
                        }
                    }
                }
                return null;
            }
        } catch (Throwable t) {
            Log.e(TAG, "Well that's not good.", t);
        }

        return null;
    }

    public interface AsyncResponse {
        void processFinish(JSONObject output);
    }
}