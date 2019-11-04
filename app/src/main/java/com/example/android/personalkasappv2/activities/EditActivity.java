package com.example.android.personalkasappv2.activities;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.android.personalkasappv2.MainActivity;
import com.example.android.personalkasappv2.R;
import com.example.android.personalkasappv2.dbHelper.Config;
import com.example.android.personalkasappv2.dbHelper.SqliteHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity {

    MainActivity M = new MainActivity();

    @BindView(R.id.et_jumlah)EditText et_jumlah;
    @BindView(R.id.et_keterangan)EditText et_keterangan;
    @BindView(R.id.et_tanggal)EditText et_tanggal;
    @BindView(R.id.radio_status) RadioGroup radio_status;
    @BindView(R.id.radio_income) RadioButton radio_income;
    @BindView(R.id.radio_outcome) RadioButton radio_outcome;

    RippleView rip_simpan;

    DatePickerDialog datePickerDialog;
    String tanggal, status;

    SqliteHelper sqliteHelper;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        status =""; tanggal = ""; // input tanggal di SQLite YYYY-mm-dd
        /*radio_status  =(RadioGroup)findViewById(R.id.radio_status);
        radio_income  =(RadioButton)findViewById(R.id.radio_income);
        radio_outcome  =(RadioButton)findViewById(R.id.radio_outcome);

        et_jumlah     =(EditText) findViewById(R.id.et_jumlah);
        et_keterangan =(EditText) findViewById(R.id.et_keterangan);
        rip_simpan    =(RippleView) findViewById(R.id.rip_simpan);

        et_tanggal= (EditText)findViewById(R.id.et_tanggal);
*/
        editOnMysql();
        //editOnSQLite();
        //set title
        getSupportActionBar().setTitle("Edit Data");
        //return to Home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void editOnSQLite(){
        sqliteHelper = new SqliteHelper(this);

        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery(
                "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi WHERE transaksi_id='" + M.transaksi_id + "'",
                null);
        cursor.moveToFirst();

        status = cursor.getString(1);
        switch (status){
            case "MASUK": radio_income.setChecked(true);
                break;
            case "KELUAR": radio_outcome.setChecked(true);
                break;
        }

        radio_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.radio_income:
                        status= "MASUK";
                        break;
                    case R.id.radio_outcome:
                        status= "KELUAR";
                        break;
                }
                Log.d("Log Status", status);
            }
        });

        et_jumlah.setText( cursor.getString(2) );
        et_keterangan.setText( cursor.getString(3) );

        tanggal = cursor.getString(4);
        et_tanggal.setText( cursor.getString(5) );

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        et_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat formatNumber = new DecimalFormat("00");
                        tanggal= formatNumber.format(year)+ "/" + formatNumber.format(month + 1)+ "/" +formatNumber.format(dayOfMonth);

                        et_tanggal.setText(formatNumber.format(dayOfMonth) + "/" + formatNumber.format(month + 1) + "/" +
                                formatNumber.format(year));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        rip_simpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if(status.equals("") || et_jumlah.getText().toString().equals("") || et_keterangan.getText().toString().equals("")){
                    Toast.makeText(EditActivity.this, "Isi data dengan benar",
                            Toast.LENGTH_LONG).show();
                }else{
                    SQLiteDatabase database = sqliteHelper.getWritableDatabase();
                    database.execSQL(
                            "UPDATE transaksi SET status='"+ status +"', jumlah='"+ et_jumlah.getText().toString() +
                                    "', keterangan='"+ et_keterangan.getText().toString() + "', tanggal='"+ tanggal +
                                    "' WHERE transaksi_id='" + M.transaksi_id + "' "
                    );
                    Toast.makeText(EditActivity.this, "Perubahan data transaksi berhasil disimpan",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void editOnMysql(){

        status = M.status;
        switch (status){
            case "MASUK": radio_income.setChecked(true);
                break;
            case "KELUAR": radio_outcome.setChecked(true);
                break;
        }

        radio_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.radio_income:
                        status= "MASUK";
                        break;
                    case R.id.radio_outcome:
                        status= "KELUAR";
                        break;
                }
                Log.d("Log Status", status);
            }
        });

        et_jumlah.setText( M.jumlah);
        et_keterangan.setText( M.keterangan);

        tanggal = M.tanggal2;
        et_tanggal.setText(M.tanggal);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        et_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat formatNumber = new DecimalFormat("00");
                        tanggal= formatNumber.format(year)+ "/" + formatNumber.format(month + 1)+ "/" +formatNumber.format(dayOfMonth);

                        et_tanggal.setText(formatNumber.format(dayOfMonth) + "/" + formatNumber.format(month + 1) + "/" +
                                formatNumber.format(year));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        rip_simpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if(status.equals("") || et_jumlah.getText().toString().equals("") || et_keterangan.getText().toString().equals("")){
                    Toast.makeText(EditActivity.this, "Isi data dengan benar",
                            Toast.LENGTH_LONG).show();
                }else{
                    AndroidNetworking.post(Config.HOST+"update.php")
                            .addBodyParameter("transaksi_id", M.transaksi_id)
                            .addBodyParameter("status", status)
                            .addBodyParameter("jumlah", et_jumlah.getText().toString())
                            .addBodyParameter("keterangan", et_keterangan.getText().toString())
                            .addBodyParameter("tanggal", tanggal)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // do anything with response
                                    try {
                                        if (response.getString("response").equals("success")){
                                            //startActivity(new Intent(AddActivity.this,MainActivity.class));
                                            Toast.makeText(EditActivity.this, "Perubahan data transaksi berhasil disimpan",
                                                    Toast.LENGTH_LONG).show();
                                            finish();
                                        }else {
                                            Toast.makeText(EditActivity.this, response.getString("response"),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onError(ANError error) {
                                    // handle error
                                    error.printStackTrace();
                                }
                            });
                }
            }
        });
    }
    //ketika icon back diclick
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}

