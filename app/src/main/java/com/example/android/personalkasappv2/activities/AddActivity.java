package com.example.android.personalkasappv2.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.example.android.personalkasappv2.fragments.IncomeFragment;
import com.example.android.personalkasappv2.fragments.OutcomeFragment;
import com.example.android.personalkasappv2.R;
import com.example.android.personalkasappv2.dbHelper.Config;
import com.example.android.personalkasappv2.dbHelper.SqliteHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddActivity extends AppCompatActivity {

    @BindView(R.id.radio_status) RadioGroup radio_status;
    @BindView(R.id.et_jumlah)EditText et_jumlah;
    @BindView(R.id.et_keterangan)EditText et_keterangan;
    @BindView(R.id.btn_simpan)Button btn_simpan;
    @BindView(R.id.rip_simpan)RippleView rip_simpan;
    String status;
    SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);

        status="";
        sqliteHelper= new SqliteHelper(this);


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

    public static class TabActivity extends AppCompatActivity {

        private final String[] TITLE_PAGES = new String[]{"INCOME","OUTCOME"};

        private final Fragment[] PAGES = new Fragment[]{new IncomeFragment(),
                new OutcomeFragment()};

        @BindView(R.id.tab_main)
        TabLayout tab_main;

        @BindView(R.id.view_pager_main)
        ViewPager view_pager_main;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_tab);
            ButterKnife.bind(this);

            view_pager_main.setAdapter(new PagerAdapter(getSupportFragmentManager()));
            tab_main.setupWithViewPager(view_pager_main);


            getSupportActionBar().setTitle("Tambah Baru");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        @Override
        public boolean onSupportNavigateUp(){
            finish();
            return true;
        }

        private class PagerAdapter extends FragmentPagerAdapter {


            public PagerAdapter(FragmentManager fragmentManager) {
                super(fragmentManager);
            }

            @Override
            public Fragment getItem(int position) {
                return PAGES[position];
            }

            @Override
            public int getCount() {
                return PAGES.length;
            }

            @Override
            public CharSequence getPageTitle(int position){
                return TITLE_PAGES[position];
            }
        }
    }
}
