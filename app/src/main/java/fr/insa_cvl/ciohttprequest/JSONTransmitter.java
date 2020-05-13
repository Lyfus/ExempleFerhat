package fr.insa_cvl.ciohttprequest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JSONTransmitter extends AsyncTask<Object, Integer, String> {

    private AppCompatActivity myActivity;
    private OnJsonTransmitionCompleted mCallback;
    private String option;
    private String token;

    public JSONTransmitter(OnJsonTransmitionCompleted callback, AppCompatActivity mainActivity) {
        this.myActivity = mainActivity;
        this.mCallback = callback;
    }

    @Override
    protected String doInBackground(Object... params) {
        // params[0] -> URL
        // params[1] -> choix (GET, POST, token etc)
        // params[2] -> token
        // params[3] -> data

        HttpURLConnection urlConnection = null;
        String result = null;
        URL url = null;
        option = (String) params[1];
        if (params[2] != null) {
            token = (String) params[2];
        }

        Log.d("TEST doInBackground", (String) params[1]);

        switch ((String) params[1]) {
            case "GetExceptToken":
                // GET TOKEN
                try {
                    url = new URL((String) params[0]);
                    Log.d("URL", url.toString());

                    urlConnection = (HttpURLConnection) url.openConnection(); // Open

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream()); // Stream
                    result = readStream(in);
                    Log.d("VERIF", result);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }

            case "GET":
                // RECUPERATION DE DONNEES SANS MODIFICATION
                try {
                    Log.d("GET", (String) params[0]);

                    url = new URL((String) params[0]);
                    Log.d("URL", url.toString());
                    urlConnection = (HttpURLConnection) url.openConnection(); // Open
                    Log.d("STRINGS2", (String) params[2]);

                    urlConnection.setRequestProperty("token", (String) params[2]);


                    InputStream in = new BufferedInputStream(urlConnection.getInputStream()); // Stream
                    result = readStream(in);
                    Log.d("VERIF", result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            case "POST":
                // CREER UN NOUVEL OBJET -> New User, New Product
                // Pour envoyer DATA de type JSON, cast params[3] en objet JSON (GSON, JsonObject)
                //
            case "PUT":
                // MODIFICATION D'UN OBJET -> UPDATE
            case "DELETE":
                // DELETE
            default:
        }

        return result;
    }

    @Override
    protected void onPostExecute(String json) {

        super.onPostExecute(json);
        if (option == "GetExceptToken") {
            this.mCallback.onTransmitionCompleted(json, "token");
        } else {
            this.mCallback.onTransmitionCompleted(json, "data");
        }
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    public interface OnJsonTransmitionCompleted {
        void onTransmitionCompleted(String json, String option);
    }
}
