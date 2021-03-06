package com.example.android.personalkasappv2;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.android.personalkasappv2.dbHelper.Config;
import com.example.android.personalkasappv2.dbHelper.SqliteHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class AddActivity extends AppCompatActivity {

    RadioGroup radio_status;
    EditText et_jumlah, et_keterangan;
    Button btn_simpan;
    RippleView rip_simpan;
    String status;
    SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        status="";
        sqliteHelper= new SqliteHelper(this);

        radio_status  =(RadioGroup)findViewById(R.id.radio_status);
        et_jumlah     =(EditText) findViewById(R.id.et_jumlah);
        et_keterangan =(EditText) findViewById(R.id.et_keterangan);
        btn_simpan    =(Button) findViewById(R.id.btn_simpan);
        rip_simpan    =(RippleView) findViewById(R.id.rip_simpan);

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

        rip_simpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if(status.equals("") || et_jumlah.getText().toString().equals("") || et_keterangan.getText().toString().equals("")){
                    Toast.makeText(AddActivity.this, "Isi data dengan benar",
                            Toast.LENGTH_LONG).show();
                }else{
                    queryMysql();
                    _createSQLite();
                    finish();
                }
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // CONTOH TOAST
//                Toast.makeText(AddActivity.this, "Jumlah : " + et_jumlah.getText().toString()
//                                + " Keterangan : "+ et_keterangan.getText().toString(),
//                        Toast.LENGTH_LONG).show();
            }
        });

        //set title
        getSupportActionBar().setTitle("Tambahan Data");
        //return to Home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //ketika icon back diclick
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void queryMysql(){
        AndroidNetworking.post(Config.HOST+"create.php")
                .addBodyParameter("status", status)
                .addBodyParameter("jumlah", et_jumlah.getText().toString())
                .addBodyParameter("keterangan", et_keterangan.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            if (response.getString("response").equals("success")){
                                //startActivity(new Intent(AddActivity.this,MainActivity.class));
                                Toast.makeText(AddActivity.this, "Data transaksi berhasil disimpan", Toast.LENGTH_LONG).show();
                                finish();
                            }else {
                                Toast.makeText(AddActivity.this, response.getString("response"),
                                        Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    private void _createSQLite(){
        SQLiteDatabase database = sqliteHelper.getWritableDatabase();
        database.execSQL(
                "INSERT INTO transaksi(status, jumlah, keterangan) VALUES('" + status +
                        "','"+ et_jumlah.getText().toString() + "','" + et_keterangan.getText().toString() + "')"
        );
        Toast.makeText(AddActivity.this, "Data transaksi berhasil disimpan",
                Toast.LENGTH_LONG).show();
        finish();
    }
}
