package com.pavanbuddha.expensemanager;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pavanbuddha.expensemanager.Expenditure;
import com.pavanbuddha.expensemanager.R;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.pavanbuddha.expensemanager.MainActivity.obj;

public class MyListAdapter extends ArrayAdapter<Expenditure> {

    private final Activity context;


    public MyListAdapter(Activity context) {
        super(context, R.layout.custom_list,obj);
        this.context=context;
    }



    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.custom_list, null,true);

        final PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        final String myFormat = "d MMM yyyy h:mm aa"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat,Locale.getDefault());

        TextView titleText = (TextView) rowView.findViewById(R.id.large);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.medium);
        TextView date = (TextView) rowView.findViewById(R.id.subtitle3);

        long timeDiffInMilli= new Date().getTime()-obj.get(position).getDate().getTime();
        long _3Hours= 10800000;

        titleText.setText(obj.get(position).getTitle());
        switch (obj.get(position).getCategory())
        {

            case  R.id.radio_red: imageView.setImageResource(R.drawable.circle_red);break;

            case R.id.radio_blue: imageView.setImageResource(R.drawable.circle_blue);break;

            case R.id.radio_gray: imageView.setImageResource(R.drawable.circle_gray);break;

            case R.id.radio_green: imageView.setImageResource(R.drawable.circle_green);break;

            case R.id.radio_orange: imageView.setImageResource(R.drawable.circle_orange);break;

            case R.id.radio_purple: imageView.setImageResource(R.drawable.circle_purple);break;
        }

        subtitleText.setText(String.valueOf(obj.get(position).getAmount()));

        if(timeDiffInMilli<_3Hours)date.setText(prettyTime.format(obj.get(position).getDate()));
        else {
            date.setText(formatToYesterdayOrToday(obj.get(position).getDate()));
        }

        return rowView;

    }

    public static String formatToYesterdayOrToday(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        DateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
        DateFormat timeFormatter2 = new SimpleDateFormat("d MMM, h:mm aa");
        DateFormat timeFormatter3 = new SimpleDateFormat("d MMM yyyy h:mm aa");



        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return "Today, " + timeFormatter.format(date);
        } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday, " + timeFormatter.format(date);
        } else if(calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
            return timeFormatter2.format(date);
        }
        else return timeFormatter3.format(date);
    }
}