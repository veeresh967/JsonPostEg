package com.veeresh.b36_jsonposteg;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText et1, et2, et3;
    Button b;
    TextView tv;
    MyTask myTask;

    //take an async task to connect to server
    public class MyTask extends AsyncTask<String, Void, Integer>{
        URL url;
        HttpURLConnection connection;
        OutputStream outputStream;
        OutputStreamWriter outputStreamWriter;
        //NO NEED OF BUFFERED WRITER - AS DATA IS SMALL AMOUNT OF DATA WE ARE POSTING TO SERVER
        String name, city, twitter;

        @Override
        protected void onPreExecute() {
            Toast.makeText(MainActivity.this, "ABOUT TO POST", Toast.LENGTH_SHORT).show();
            name = et1.getText().toString(); //I want to send name - b35
            city = et2.getText().toString(); //I want to send city - bangalore
            twitter = et3.getText().toString(); //I want to send twitter details - b35@twitter.com
        }

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();//OPENS ONLY FOR READING PURPOSE
                //extra steps for posting data to server
                //BELOW LINE TELLS TO SERVER THAT I WANT CONNECTION FOR POST REQST
                connection.setDoOutput(true);
                //BELOW LINE TELLS TO SERVER THAT I AM SENDING JSON DATA TO YOU.
                connection.setRequestProperty("Content-type","application/json");
                //WHAT DATA WE WANT TO SEND TO SERVER?
                //IT IS ALREADY AVAILABLE IN ON PRE EXECUTE
                //NOW LET US CONVERT NAME, CITY, AND TWITTER - TO - JSON DATA
                JSONObject j = new JSONObject();
                j.accumulate("name", name);
                j.accumulate("country",city);
                j.accumulate("twitter",twitter);
                //extra steps done
                outputStream = connection.getOutputStream();
                outputStreamWriter = new OutputStreamWriter(outputStream);
                //now post json-data to server using above output stream writer
                outputStreamWriter.write(j.toString()); //json object to string
                //tell to server that you are done with writing, so that server can read
                outputStreamWriter.flush(); // --this is where server starts reading your data
                //now ask server for the response
                int result = connection.getResponseCode(); //if everything is fine, server gives 200
                //now return this server response to onpost execute
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return -2; //WE ASSUME THAT -2 MEANS WRONG URL
            } catch (IOException e) {
                e.printStackTrace();
                return -1; //WE ASSUME THAT -1 MEANS IO EXCEPTION
            } catch (JSONException e) {
                e.printStackTrace();
                return 0; //WE ASSUME THAT 0 MEANS JSON EXCEPTION
            }
            //return null;
        }
        @Override
        protected void onPostExecute(Integer integer) {
            switch (integer){
                case -2:
                    tv.setText("WRONG URL");
                    break;
                case -1:
                    tv.setText("CHECK INTERNET - THERE IS AN IO ERROR");
                    break;
                case 0:
                    tv.setText("JSON EXCEPTION - GIVE VALUES PROPERLY TO SERVER");
                    break;
                case HttpURLConnection.HTTP_OK: //for 200
                    tv.setText("SERVER RESPONSE  SUCCESS");
                    break;
                default:
                    tv.setText("SERVER RESPONSE FAILURE : "+integer);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et1 = (EditText) findViewById(R.id.editText1);
        et2 = (EditText) findViewById(R.id.editText2);
        et3 = (EditText) findViewById(R.id.editText3);
        b = (Button) findViewById(R.id.button1);
        tv = (TextView) findViewById(R.id.textView1);

        myTask = new MyTask();

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTask.execute("http://hmkcode.appspot.com/jsonservlet");
                b.setVisibility(View.GONE);
            }
        });
    }
}
