package com.example.android.personalkasappv2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.andexert.library.RippleView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

public class FilterActivity extends AppCompatActivity {
    MainActivity M = new MainActivity();

    EditText et_from, et_to;
    RippleView rip_simpan;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        et_from = (EditText)findViewById(R.id.et_from);
        et_to = (EditText)findViewById(R.id.et_to);
        rip_simpan =(RippleView)findViewById(R.id.rip_simpan);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        et_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat formatNumber = new DecimalFormat("00");
                        M.tgl_dari = formatNumber.format(year)+ "/" + formatNumber.format(month + 1)+ "/" +formatNumber.format(dayOfMonth);

                        et_from.setText(formatNumber.format(dayOfMonth) + "/" + formatNumber.format(month + 1) + "/" +
                                formatNumber.format(year));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        et_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat formatNumber = new DecimalFormat("00");
                        M.tgl_ke = formatNumber.format(year)+ "/" + formatNumber.format(month + 1)+ "/" +formatNumber.format(dayOfMonth);

                        et_to.setText(formatNumber.format(dayOfMonth) + "/" + formatNumber.format(month + 1) + "/" +
                                formatNumber.format(year));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        rip_simpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if(M.tgl_ke.equals("") || M.tgl_dari.equals("")){
                    Toast.makeText(FilterActivity.this, "Isilah data dengan benar", Toast.LENGTH_LONG).show();
                }else{
                    M.filter = true;
                    M.txt_filter.setText(et_from.getText().toString() + " - "+et_to.getText().toString());
                    finish();
                }
            }
        });

        //set title
        getSupportActionBar().setTitle("Filter Data");
        //return to Home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //ketika icon back diclick
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
