package com.darshan.tutorial10;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    JSONArray itemJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Users List");
        listView = findViewById(R.id.lstUserListView);
        new MyAsyncTask().execute();
    }

    class MyAsyncTask extends AsyncTask
    {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog=new ProgressDialog(MainActivity.this);
            dialog.setTitle("Fetching Data...");
            dialog.show();
        }


        @Override
        protected Object doInBackground(Object[] objects)
        {
            StringBuffer response=new StringBuffer();

            try
            {
                URL url = new URL(MyUtil.USER_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStreamReader ir = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(ir);
                String inputLine = null;
                while ((inputLine = br.readLine()) != null)
                {
                    response.append(inputLine);
                }
                br.close();
                ir.close();
                itemJsonArray = new JSONArray(response.toString());
            }
            catch (UnknownHostException e)
            {
                if(dialog.isShowing())
                    dialog.dismiss();
                cancel(true);
            }
            catch (Exception e)
            {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            CustomAdapter myAdapter=new CustomAdapter(MainActivity.this,itemJsonArray);
            listView.setAdapter(myAdapter);
            if(dialog.isShowing())
                dialog.dismiss();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    try {
                        MyUtil.userJSONObj = itemJsonArray.getJSONObject(position);
                        Intent intent = new Intent(MainActivity.this, UserData.class);
                        startActivity(intent);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}