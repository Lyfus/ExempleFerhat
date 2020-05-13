package fr.insa_cvl.ciohttprequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements JSONTransmitter.OnJsonTransmitionCompleted {

    private Gson gson = new Gson();
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getToken = (Button) findViewById(R.id.button);
        getToken.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    JSONTransmitter transmitter = new JSONTransmitter(MainActivity.this, MainActivity.this);
                    Log.d("AVANT GETTOKEN", "GETTOKEN");
                    transmitter.execute("http://10.0.2.2:8080/token?app=mobile", "GetExceptToken", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onTransmitionCompleted(String json, String option) {
        // Now you have access to your JSON data in your activity

        if (option == "token") {
            // Save the flag
            SharedPreferences settings = getSharedPreferences("temp.txt", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            editor.putString("token", gson.fromJson(json, tokenJSON.class).token);
            // sauvegarde dans les préférences
            editor.apply();

            Log.d("SECONDE REQUETE", gson.fromJson(json, tokenJSON.class).token);
            JSONTransmitter transmitter2 = new JSONTransmitter(MainActivity.this, MainActivity.this);

            transmitter2.execute("http://10.0.2.2:8080/api/account/login?login=Admin&password=Admin20@", "GET", gson.fromJson(json, tokenJSON.class).token);
        }
    }
}
