package com.pavanbuddha.expensemanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.pavanbuddha.expensemanager.MainActivity.categoryWiseData;
import static com.pavanbuddha.expensemanager.MainActivity.obj;
import static com.pavanbuddha.expensemanager.MainActivity.total;

public class AddActivity extends AppCompatActivity {

    private EditText title_field;
    private EditText amount_field;
    private EditText date_field;
    private Switch switch_field ;
    private Calendar myCalendar;
    private int categoryRadioRow1;
    private int categoryRadioRow2;

    private SimpleDateFormat sdf;

    private Expenditure exp ;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        IntializationOfToolBarAndActionBar();

        title_field =  findViewById(R.id.title_field);
        amount_field = findViewById(R.id.amount_field);
        date_field = findViewById(R.id.calender_field);
        switch_field = findViewById(R.id.switch_expense);


        myCalendar = Calendar.getInstance();
        String myFormat = "d MMM yyyy h:mm aa";
        sdf = new SimpleDateFormat(myFormat,Locale.getDefault());


        Intent intent = getIntent();
        pos = intent.getIntExtra("key", -1);

        if (pos != -1)
        {
            exp = obj.get(pos-1);
            Toast toast = Toast.makeText(this, exp.toString(), Toast.LENGTH_SHORT);
            toast.show();
            title_field.setText(exp.getTitle());
            amount_field.setText(String.valueOf(exp.getAmount()));
            date_field.setText(sdf.format(exp.getDate()));
            radioBtn(null,exp.getCategory());

            ((RadioButton)findViewById(exp.getCategory())).setChecked(true);


            if (exp.isExpense()) switch_field.setChecked(true);
            else switch_field.setChecked(false);
        }
        else date_field.setText(sdf.format(new Date()));


        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                date_field.setText(sdf.format(myCalendar.getTime()));
            }
        };

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                new TimePickerDialog(AddActivity.this, time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),
                        true).show();
            }
        };

        date_field.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void IntializationOfToolBarAndActionBar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.add_activity_title);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }

    }

    public void expense_switchOnClick(View view){
        switch_field.toggle();
    }

    public void saveButtonOnClick(View view) {

        String title = title_field.getText().toString();
        if(title.equals(""))title_field.setError("Please Enter Title");

        String amount_field_text=amount_field.getText().toString();
        if(amount_field_text.equals(""))
        {
            amount_field.setError("Please Enter Amount");
            return;
        }
        double amount = Double.parseDouble(amount_field_text);

        boolean expense = switch_field.isChecked();
        String date = date_field.getText().toString();

        int category = getCategory();
        Date dateObject = new Date();
        try {
            dateObject = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (exp != null) {
            ifObjectExistsUpdateIt(title, amount, expense, category, dateObject,pos);

        } else {
            Expenditure newExpense = new Expenditure(title, amount, dateObject, expense, category, "");
            total+=amount;
            obj.add(0, newExpense);
        }

        if(categoryWiseData.get(category)!=null)
            categoryWiseData.put(category,categoryWiseData.get(category)+amount);
        else categoryWiseData.put(category,amount);

        Collections.sort(obj,Collections.reverseOrder());
        saveObjectOnDevice();
        finish();
    }

    private int getCategory() {
        categoryRadioRow1=((RadioGroup)findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        categoryRadioRow2=((RadioGroup)findViewById(R.id.radioGroup2)).getCheckedRadioButtonId();
        Log.i("categoryRadio1",""+categoryRadioRow1);
        Log.i("categoryRadio2",""+categoryRadioRow2);
        int category;

        if(categoryRadioRow1!=-1)category=categoryRadioRow1;
        else if(categoryRadioRow2!=-1)category=categoryRadioRow2;
        else category=R.id.radio_red;
        return category;
    }

    private void ifObjectExistsUpdateIt(String title, double amount, boolean expense, int category, Date d, int pos) {
        double prevAmountValue=exp.getAmount();
        int prevCategory =exp.getCategory();
        total=total+amount-prevAmountValue;
        categoryWiseData.put(prevCategory,categoryWiseData.get(prevCategory)-prevAmountValue);
        exp.setAmount(amount);
        exp.setTitle(title);
        exp.setExpense(expense);
        exp.setDate(d);
        exp.setCategory(category);
        obj.set(pos-1, exp);
    }

    private void saveObjectOnDevice() {
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        SharedPreferences sharedPref = AddActivity.this.getSharedPreferences("PAVAN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("key", json);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(AddActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void radioBtn1(View view)
    {
        int id=((RadioGroup)findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        ((RadioGroup)findViewById(R.id.radioGroup2)).clearCheck();
        radioBtn(view,id);
    }

    public void radioBtn2(View view)
    {
        int id=((RadioGroup)findViewById(R.id.radioGroup2)).getCheckedRadioButtonId();
        ((RadioGroup)findViewById(R.id.radioGroup)).clearCheck();
        radioBtn(view,id);
    }

    private void radioBtn(View view, int id) {

        RadioButton food_rd=findViewById(R.id.radio_red);
        RadioButton entertainment_rd=findViewById(R.id.radio_blue);
        RadioButton radio_gray=findViewById(R.id.radio_gray);
        RadioButton radio_green=findViewById(R.id.radio_green);
        RadioButton radio_orange=findViewById(R.id.radio_orange);
        RadioButton radio_purple=findViewById(R.id.radio_purple);

        setAllRadioButtonsUnchecked(food_rd, entertainment_rd, radio_gray, radio_green, radio_orange, radio_purple);

        switch(id)
        {
            case R.id.radio_red: food_rd.setButtonDrawable(R.drawable.circle_red);break;
            case R.id.radio_blue: entertainment_rd.setButtonDrawable(R.drawable.circle_blue);break;
            case R.id.radio_gray: radio_gray.setButtonDrawable(R.drawable.circle_gray);break;
            case R.id.radio_green: radio_green.setButtonDrawable(R.drawable.circle_green);break;
            case R.id.radio_orange: radio_orange.setButtonDrawable(R.drawable.circle_orange);break;
            case R.id.radio_purple: radio_purple.setButtonDrawable(R.drawable.circle_purple);break;

        }

    }

    private void setAllRadioButtonsUnchecked(RadioButton food_rd, RadioButton entertainment_rd, RadioButton radio_gray, RadioButton radio_green, RadioButton radio_orange, RadioButton radio_purple) {
        food_rd.setButtonDrawable(R.drawable.circle_red_unchecked);
        entertainment_rd.setButtonDrawable(R.drawable.circle_blue_unchecked);
        radio_gray.setButtonDrawable(R.drawable.circle_gray_unchecked);
        radio_green.setButtonDrawable(R.drawable.circle_green_unchecked);
        radio_orange.setButtonDrawable(R.drawable.circle_orange_unchecked);
        radio_purple.setButtonDrawable(R.drawable.circle_purple_unchecked);
    }



}
