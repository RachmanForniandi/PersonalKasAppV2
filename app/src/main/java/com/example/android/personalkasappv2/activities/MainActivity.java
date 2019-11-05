package com.example.android.personalkasappv2.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.android.personalkasappv2.R;
import com.example.android.personalkasappv2.dbHelper.Config;
import com.example.android.personalkasappv2.dbHelper.SqliteHelper;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.txt_masuk_main) TextView txt_masuk;
    @BindView(R.id.txt_keluar_main) TextView txt_keluar;
    @BindView(R.id.txt_saldo_main) TextView txt_saldo;
    @BindView(R.id.txt_filter)
    public TextView txt_filter;
    @BindView(R.id.list_anggaran) ListView list_anggaran;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipe_refresh;
    String query_kas, query_total;
    SqliteHelper sqliteHelper;
    Cursor cursor;

    ArrayList<HashMap<String, String>> arraykas = new ArrayList<>();


    public static String transaksi_id, tgl_dari, tgl_ke,status,keterangan, jumlah,tanggal,tanggal2,linkFilter;


    public static boolean filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        startActivity(new Intent(MainActivity.this,IntroSlide.class));

        transaksi_id = ""; tgl_dari=""; tgl_ke="";
        filter = false;


        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query_kas =
                        "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi ORDER BY transaksi_id DESC";

                query_total =
                        "SELECT SUM(jumlah) AS total, (SELECT SUM(jumlah) FROM transaksi WHERE status='MASUK') as masuk," +
                                "(SELECT SUM(jumlah) FROM transaksi WHERE status='KELUAR')as keluar FROM transaksi";

                //KasAdapter();
                linkFilter = Config.HOST+"read.php";
                readQueryMySql();
                txt_filter.setText("SEMUA");
            }
        });

        sqliteHelper = new SqliteHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //intent ke AddActivity dgn bentuk lain
                startActivity(new Intent(MainActivity.this, AddActivity.TabActivity.class));

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        query_kas =
                "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi ORDER BY transaksi_id DESC";

        query_total =
                "SELECT SUM(jumlah) AS total, (SELECT SUM(jumlah) FROM transaksi WHERE status='MASUK') as masuk," +
                        "(SELECT SUM(jumlah) FROM transaksi WHERE status='KELUAR')as keluar FROM transaksi";
        if(filter){
            query_kas =
                    "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi " +
            "WHERE(tanggal >= '"+ tgl_dari +"') AND (tanggal <= '"+ tgl_ke +"')ORDER BY transaksi_id ASC";

            query_total =
                    "SELECT SUM(jumlah) AS total, "+
                    "(SELECT SUM(jumlah) FROM transaksi WHERE status='MASUK' AND (tanggal >= '"+ tgl_dari +"') AND (tanggal <= '"+ tgl_ke +"'))," +
                    "(SELECT SUM(jumlah) FROM transaksi WHERE status='KELUAR' AND (tanggal >= '"+ tgl_dari +"') AND (tanggal <= '"+ tgl_ke +"'))" +
                    "FROM transaksi WHERE (tanggal >= '"+ tgl_dari +"') AND (tanggal <= '"+ tgl_ke +"')";

            linkFilter = Config.HOST+"filter.php?dari="+tgl_dari+"&ke="+tgl_ke;
        }else{
            linkFilter = Config.HOST+"read.php";
        }
        //KasAdapter();

        readQueryMySql();
    }

    private void readQueryMySql(){

        swipe_refresh.setRefreshing(false);
        arraykas.clear();
        list_anggaran.setAdapter(null);
        Log.d("testLink",linkFilter);
        //AndroidNetworking.post(Config.HOST+"read.php")
        AndroidNetworking.post(linkFilter)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                           /* if (response.getString("response").equals("success")){

                            }else {

                            }*/
                            NumberFormat rupiah = NumberFormat.getInstance(Locale.GERMANY);

                            txt_masuk.setText( "Rp " + rupiah.format(response.getDouble("masuk")) );
                            txt_keluar.setText( "Rp " + rupiah.format(response.getDouble("keluar")) );

                            txt_saldo.setText(
                                    "Rp " + rupiah.format(response.getDouble("masuk") - response.getDouble("keluar"))

                            );
                            JSONArray jsonArray = response.getJSONArray("hasil");
                            for (int i= 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                HashMap<String, String>map = new HashMap<>();
                                map.put("transaksi_id", jsonObject.getString("transaksi_id"));
                                map.put("status",       jsonObject.getString("status"));
                                map.put("jumlah",       jsonObject.getString("jumlah"));
                                map.put("keterangan",   jsonObject.getString("keterangan"));
                                map.put("tanggal",      jsonObject.getString("tanggal"));
                                map.put("tanggal2",      jsonObject.getString("tanggal2"));

                                arraykas.add(map);
                            }
                            readAdapter();

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

    private void readAdapter(){
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arraykas, R.layout.list_anggaran,
                new String[]{"transaksi_id","status","jumlah","keterangan","tanggal","tanggal2"},
                new int[] {R.id.txt_transaksi_id, R.id.txt_status, R.id.txt_jumlah, R.id.txt_keterangan, R.id.txt_tanggal,R.id.txt_tanggal_2} );

        list_anggaran.setAdapter(simpleAdapter);
        list_anggaran.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                transaksi_id = ((TextView) view.findViewById(R.id.txt_transaksi_id)).getText().toString();
                status = ((TextView) view.findViewById(R.id.txt_status)).getText().toString();
                jumlah = ((TextView) view.findViewById(R.id.txt_jumlah)).getText().toString();
                keterangan = ((TextView) view.findViewById(R.id.txt_keterangan)).getText().toString();
                tanggal = ((TextView) view.findViewById(R.id.txt_tanggal)).getText().toString();
                tanggal2 = ((TextView) view.findViewById(R.id.txt_tanggal_2)).getText().toString();
                Log.d("transaksi_id", transaksi_id);
                ListMenu();
            }
        });
    }

    private void KasAdapter() {

        swipe_refresh.setRefreshing(false);
        arraykas.clear();list_anggaran.setAdapter(null);

        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery(query_kas, null);
        cursor.moveToFirst();

        //utk penulisan format mata uang
        NumberFormat formatRupiah = NumberFormat.getInstance(Locale.GERMANY);

        for (int i=0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);
            Log.d("status", cursor.getString(1));

            HashMap<String, String>map = new HashMap<>();
            map.put("transaksi_id", cursor.getString(0));
            map.put("status",       cursor.getString(1));
            map.put("jumlah",       "Rp " + formatRupiah.format(cursor.getDouble(2)));
            map.put("keterangan",   cursor.getString(3));
            //            map.put("tanggal",      cursor.getString(4));
            map.put("tanggal",      cursor.getString(5));

            arraykas.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arraykas, R.layout.list_anggaran,
                new String[]{"transaksi_id","status","jumlah","keterangan","tanggal"},
                new int[] {R.id.txt_transaksi_id, R.id.txt_status, R.id.txt_jumlah, R.id.txt_keterangan, R.id.txt_tanggal} );

        list_anggaran.setAdapter(simpleAdapter);
        list_anggaran.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                transaksi_id = ((TextView) view.findViewById(R.id.txt_transaksi_id)).getText().toString();
                Log.d("transaksi_id", transaksi_id);
                ListMenu();
            }
        });

        KasTotal();
    }

    private void KasTotal(){

        NumberFormat rupiah = NumberFormat.getInstance(Locale.GERMANY);

        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery(query_total, null);
        cursor.moveToFirst();

        txt_masuk.setText( "Rp " + rupiah.format(cursor.getDouble(1)) );
        txt_keluar.setText( "Rp " + rupiah.format(cursor.getDouble(2)) );
        txt_saldo.setText(
                "Rp " + rupiah.format(cursor.getDouble(1) - cursor.getDouble(2) )
        );
        if(!filter){ txt_filter.setText("SEMUA"); }
        filter = false;

    }

    private void ListMenu(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.list_menu);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        TextView txt_edit = (TextView)dialog.findViewById(R.id.txt_edit);
        TextView txt_hapus = (TextView)dialog.findViewById(R.id.txt_hapus);
        dialog.show();


        txt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, EditActivity.class));
            }
        });
        txt_hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteOnMysql();
            }
        });
    }

    private void deleteOnMysql(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("apakah anda yakin untuk menghapus data transaksi ini?");
        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        /*SQLiteDatabase database = sqliteHelper.getWritableDatabase();
                        database.execSQL(
                                "DELETE FROM transaksi WHERE transaksi_id='" + transaksi_id +"'"
                        );*/
                        AndroidNetworking.post(Config.HOST+"delete.php")
                                .addBodyParameter("transaksi_id", transaksi_id)
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // do anything with response
                                        try {
                                            if (response.getString("response").equals("success")){
                                                //startActivity(new Intent(AddActivity.this,MainActivity.class));
                                                Toast.makeText(MainActivity.this, "Data transaksi berhasil dihapus",
                                                        Toast.LENGTH_LONG).show();
                                                readQueryMySql();
                                            }else {
                                                Toast.makeText(MainActivity.this, response.getString("response"),
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
                });
        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void Hapus(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("apakah anda yakin untuk menghapus data transaksi ini?");
        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SQLiteDatabase database = sqliteHelper.getWritableDatabase();
                        database.execSQL(
                                "DELETE FROM transaksi WHERE transaksi_id='" + transaksi_id +"'"
                        );
                        Toast.makeText(MainActivity.this, "Data transaksi berhasil dihapus",
                                Toast.LENGTH_LONG).show();
                        KasAdapter();
                    }
                });
        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
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
        if (id == R.id.action_filter) {
            //startActivity(new Intent(MainActivity.this, FilterActivity.class));
            filterDataMysql();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void filterDataMysql(){
        SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                    @Override
                    public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                               int yearStart, int monthStart,
                                               int dayStart, int yearEnd,
                                               int monthEnd, int dayEnd) {
                        // grab the date range, do what you want
                        tgl_dari = String.valueOf(yearStart)+ "-" + (monthStart+1)+ "-" + dayStart;
                        Log.d("_DARI_",tgl_dari );

                        tgl_ke = yearEnd+ "-" + (monthEnd+1) + "-" + dayEnd;
                        Log.d("_KE_" , tgl_ke);

                        txt_filter.setText(dayStart + "/" + (monthStart+1) + "/" + yearStart + " - " + dayEnd + "/" + (monthEnd+1) + "/" + yearEnd );

                        linkFilter = Config.HOST+"filter.php?dari="+tgl_dari+"&ke="+tgl_ke;
                        readQueryMySql();
                    }
                });

        smoothDateRangePickerFragment.show(getFragmentManager(), "smoothDateRangePicker");
    }
}
