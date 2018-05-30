package com.example.android.personalkasappv2;

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
import com.example.android.personalkasappv2.dbHelper.SqliteHelper;

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

    private void insertMysql(){

    }
    //ketika icon back diclick
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
