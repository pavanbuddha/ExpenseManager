package com.pavanbuddha.expensemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static public ArrayList<Expenditure> obj;
    static public HashMap<Integer, Double> categoryWiseData;
    public MyListAdapter adapter;
    public static double total = 0;
    View header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        header = getLayoutInflater().inflate(R.layout.header, null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        obj = new ArrayList<>();
        categoryWiseData = new HashMap<>();

//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("abc");
//        DatabaseReference demoRef = rootRef.child("demo").child("qwe").child("asd");

        getStoredDataIfAny();

        calcTotalAndUpdate();

        createListViewAndSetHeader();

        piechartUpdation();

        timerForAutoUpdation();
    }

    public void fabButtonOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        startActivityForResult(intent, 1);
    }

    private void timerForAutoUpdation() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                handler.postDelayed(this, 60 * 1000);
            }
        }, 60 * 1000);
    }

    private void createListViewAndSetHeader() {
        adapter = new MyListAdapter(this);
        ListView listView = findViewById(R.id.listView);
        header.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Graph.class);
                startActivity(intent);
                }
                }
        );
        listView.addHeaderView(header, null, false);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra("key", position);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void getStoredDataIfAny() {
        SharedPreferences sharedPref = this.getSharedPreferences("PAVAN", Context.MODE_PRIVATE);
        String json = sharedPref.getString("key", null);

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Expenditure>>() {
        }.getType();
        if (json != null && !(json.equals(""))) obj = gson.fromJson(json, listType);
    }

    private void calcTotalAndUpdate() {
        total = 0;
        for (Expenditure e : obj) {
            int temp = e.getCategory();
            double amount = e.getAmount();
            total += amount;
            try {
                categoryWiseData.put(temp, categoryWiseData.get(temp) + amount);
            } catch (Exception e1) {
                categoryWiseData.put(temp, amount);
                // e1.printStackTrace();
            }
        }
        ((TextView) header.findViewById(R.id.total_amount)).setText(String.valueOf(total));
    }


    public static HashMap<Integer, Double> sortByValue(HashMap<Integer, Double> hm) {
        List<Map.Entry<Integer, Double>> list =
                new LinkedList<>(hm.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        HashMap<Integer, Double> temp = new LinkedHashMap<>();
        for (Map.Entry<Integer, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


    void piechartUpdation() {

        ProgressBar[] progressBars = new ProgressBar[]{
                header.findViewById(R.id.progressBar1),
                header.findViewById(R.id.progressBar2),
                header.findViewById(R.id.progressBar3),
                header.findViewById(R.id.progressBar4),
                header.findViewById(R.id.progressBar5),
                header.findViewById(R.id.progressBar6)};


        categoryWiseData = sortByValue(categoryWiseData);

        int i = 0;
        float tempAngle = 0;

        for (Map.Entry<Integer, Double> element : categoryWiseData.entrySet()) {

            tempAngle += (categoryWiseData.get(element.getKey()) * 100 * 3.6 / total);
            if (i != 5) progressBars[i + 1].setRotation(tempAngle);


            switch (element.getKey()) {

                case R.id.radio_red:
                    progressBars[i].setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    break;

                case R.id.radio_blue:
                    progressBars[i].setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                    break;

                case R.id.radio_gray:
                    progressBars[i].setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                    break;

                case R.id.radio_green:
                    progressBars[i].setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                    break;

                case R.id.radio_orange:
                    progressBars[i].setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
                    break;

                case R.id.radio_purple:
                    progressBars[i].setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple)));
                    break;
            }


            progressBars[i].setProgress((int) Math.ceil(categoryWiseData.get(element.getKey()) * 100 / total));
            i++;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        ((TextView) header.findViewById(R.id.total_amount)).setText(String.valueOf(total));
        Toast.makeText(MainActivity.this, "onActivityResult" + categoryWiseData, Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
        piechartUpdation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dummyData() {
        for (int i = 0; i < 23; i++)
            obj.add(new Expenditure("Title", 100, new Date(), true, 0, "description"));

    }

}

