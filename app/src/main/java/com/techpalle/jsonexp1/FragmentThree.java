package com.techpalle.jsonexp1;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentThree extends Fragment {
    Button button;
    ListView listView;
    MyDatabase myDatabase;
    Cursor cursor;
    SimpleCursorAdapter simpleCursorAdapter;
    Mytask mytask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDatabase=new MyDatabase(getActivity());
        myDatabase.open();
    }

    @Override
    public void onDestroy() {
        myDatabase.close();
        super.onDestroy();
    }

    public  boolean checkInternet(){
        //CHK FOR INTERNET CONNECTION
        //A.GET NETWORK MANAGER OBJECT
        ConnectivityManager manager=(ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        //b.from network manager,get activity information
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        //c.chk if network is conncted or not
        if(networkInfo==null || networkInfo.isConnected() == false){
            return false;
        }
        return  true;
    }


    //create a inner class for asynctask
    public class Mytask extends AsyncTask<String,Void,String>{
        URL myurl;
        HttpURLConnection connection;
        InputStream inputstream;
        InputStreamReader inputstreamreader;
        BufferedReader bufferedreader;
        String line;
        StringBuilder result;

        @Override
        protected String doInBackground(String... strings) {
            //write logic for connectiing to server and get json data
            try {
                myurl=new URL(strings[0]);
                connection= (HttpURLConnection) myurl.openConnection();
                inputstream=connection.getInputStream();
                inputstreamreader=new InputStreamReader(inputstream);
                bufferedreader=new BufferedReader(inputstreamreader);
                line=bufferedreader.readLine();
                result=new StringBuilder();
                while (line!=null){
                    result.append(line);
                    line=bufferedreader.readLine();
                }
                return result.toString();//return final result (json data) to onpost execute

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("b34","url is improper");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("b34","network problem");
            }
            return "something went worng";
        }

        @Override
        protected void onPostExecute(String s) {
            //reverse json parsing
            try {
                JSONObject j=new JSONObject(s);
                JSONArray k=j.getJSONArray("contacts");
                for (int i=0;i<k.length();i++) {
                    JSONObject m = k.getJSONObject(i);
                    String name = m.getString("name");
                    String email = m.getString("email");
                    JSONObject phone = m.getJSONObject("phone");
                    String mobile = phone.getString("mobile");

                    //now push contact object to database
                    myDatabase.insertStudent(name,email,mobile);


                }
                cursor.requery();

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("b34","json parsing error");
            }

            super.onPostExecute(s);
        }
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_main, container, false);
        button= (Button) v.findViewById(R.id.button1);
        listView= (ListView) v.findViewById(R.id.listview1);
        cursor=myDatabase.queryStudent();//this will read data from data
        simpleCursorAdapter=new SimpleCursorAdapter(getActivity(),R.layout.row,cursor,new String[]{"_id","name","email","mobile"},
                new int[]{R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4});
        mytask=new Mytask();
        listView.setAdapter(simpleCursorAdapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check internet connection
                if (checkInternet()){
                    mytask.execute("http://api.androidhive.info/contacts/");

                }else {
                    Toast.makeText(getActivity(), "no internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }
}