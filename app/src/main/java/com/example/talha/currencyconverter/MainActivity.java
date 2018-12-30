package com.example.talha.currencyconverter;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button button;
    EditText editText;
    TextView textView;
    String jsonObj;
    String curRate;
    Double answer;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        editText = findViewById(R.id.editTextDolar);
        textView = findViewById(R.id.textViewTL);
    }

    void ChangeToTL(View view){
        String dolar = editText.getText().toString();
        new JsonTask().execute("https://free.currencyconverterapi.com/api/v6/convert?q=USD_TRY&compact=y");
        if(curRate!=null && editText != null){
            String amount = editText.getText().toString();
            answer = Double.parseDouble(amount) * Double.parseDouble(curRate);
            String formattedString = String.format("%.02f", answer);
            textView.setText(formattedString + "TL");
        }
    }

    private class JsonTask extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            jsonObj = s;
            JSONObject mainObject = null;
            try {
                mainObject = new JSONObject(jsonObj);
                JSONObject curObject = mainObject.getJSONObject("USD_TRY");
                curRate = curObject.getString("val");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
