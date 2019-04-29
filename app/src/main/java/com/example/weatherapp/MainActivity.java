package com.example.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // Object declaration
    RelativeLayout mainLayout;

    // variable declaration
    TextView weatherInfoText;

    /*
    * class allows for the downloading of data from online using background thread
    */
    public class DownloadWeatherData extends AsyncTask<String, Void, String>{

        // task to execute
        @Override
        protected String doInBackground(String... urls) {

            // The api call to access weather from OpenWeatherMap
            URL url;

            // result data
            String result = "";

            // declaration of url connection
            HttpURLConnection urlConnection = null;

            try {

                // get the passed url
                url = new URL(urls[0]);

                // open the connection to the url
                urlConnection = (HttpURLConnection) url.openConnection();

                // get the input stream
                InputStream input = urlConnection.getInputStream();

                // create a new Input Stream Reader with input
                InputStreamReader reader = new InputStreamReader(input);

                // read the first character
                int data = reader.read();

                // loop through all the information in the JSON file
                while(data != -1){

                    result += (char) data;

                    data = reader.read();
                }

                return result;


            }
            // handle malformed URL's
            catch (MalformedURLException e) {
                e.printStackTrace();

            }
            // handle I/O exceptions for connecting to the internet
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                // Get result of doInBackground and type cast to JSONObject
                JSONObject jsonObject = new JSONObject(result);

                //
                String weatherInfo = jsonObject.getString("weather");

                //
                weatherInfoText.setText(weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                String weather = "";

                for (int i = 0; i < arr.length(); i++){

                    JSONObject jsonPart = arr.getJSONObject(i);

                    weather += jsonPart.getString("main") + ":" +jsonPart.getString("description") + "\n";

                }

                weatherInfoText.setText(weather);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /* code to be run on creation of the application */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find weather info text on creation of application
        weatherInfoText = (TextView) findViewById(R.id.weatherInfoText);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);

    }

    /* method gets weather information of the input city  */
    public void checkWeather(View view){

        // find city nameView
        TextView cityName = (TextView) findViewById(R.id.cityName);

        // instantiate DownloadWeatherData
        DownloadWeatherData weather = new DownloadWeatherData();

        // execute task with given city
        weather.execute("https://api.openweathermap.org/data/2.5/weather?q="+ cityName.getText().toString()+"&appid=886705b4c1182eb1c69f28eb8c520e20");

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

    }
}
