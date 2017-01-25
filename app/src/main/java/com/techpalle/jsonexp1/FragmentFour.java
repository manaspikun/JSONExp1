package com.techpalle.jsonexp1;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class FragmentFour extends Fragment {

    Button button;
    Mytask mytask;
    MyDatabase myDatabase;
    Cursor cursor;
    RecyclerView recyclerView;
    MyRecycleViewAdpter myRecycleViewAdpter;

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
    public class Mytask extends AsyncTask<String,Void,String> {
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
                myRecycleViewAdpter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("b34","json parsing error");
            }

            super.onPostExecute(s);
        }
    }


    //create a inner class for recycleview adapter
    public class MyRecycleViewAdpter extends RecyclerView.Adapter<MyRecycleViewAdpter.ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //load row xml
            View v=getActivity().getLayoutInflater().inflate(R.layout.row,parent,false);
            //create a view holder
           ViewHolder viewHolder=new ViewHolder(v);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //get data from cursor based on position and apply data on view holder-using setter
         cursor.moveToPosition(position);
            int sno=cursor.getInt(0);
            String name=cursor.getString(1);
            String email=cursor.getString(2);
            String mobile=cursor.getString(3);
            holder.tv1.setText(""+sno);
            holder.tv2.setText(name);
            holder.tv3.setText(email);
            holder.tv4.setText(mobile);

        }


        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tv1,tv2,tv3,tv4;

            public ViewHolder(View itemView) {
                super(itemView);
                tv1= (TextView) itemView.findViewById(R.id.tv1);
                tv2= (TextView) itemView.findViewById(R.id.tv2);
                tv3= (TextView) itemView.findViewById(R.id.tv3);
                tv4= (TextView) itemView.findViewById(R.id.tv4);

            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_fragment_two, container, false);
        button= (Button) v.findViewById(R.id.button);
        recyclerView= (RecyclerView) v.findViewById(R.id.recycler1);
        cursor=myDatabase.queryStudent();
        myRecycleViewAdpter= new MyRecycleViewAdpter();
        recyclerView.setAdapter(myRecycleViewAdpter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        mytask=new Mytask();
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

