package com.example.android.personalkasappv2;

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

import com.example.android.personalkasappv2.dbHelper.SqliteHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    TextView txt_masuk, txt_keluar, txt_saldo;
    ListView list_anggaran;
    SwipeRefreshLayout swipe_refresh;
    String query_kas, query_total;
    SqliteHelper sqliteHelper;
    Cursor cursor;

    ArrayList<HashMap<String, String>> arraykas = new ArrayList<>();

    public static TextView txt_filter;
    public static String transaksi_id, tgl_dari, tgl_ke;
    public static boolean filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        transaksi_id = ""; tgl_dari=""; tgl_ke="";
        filter = false;
        sqliteHelper = new SqliteHelper(this);

        txt_masuk  =(TextView)findViewById(R.id.txt_masuk);
        txt_keluar =(TextView)findViewById(R.id.txt_keluar);
        txt_saldo  =(TextView)findViewById(R.id.txt_saldo);

        list_anggaran =(ListView)findViewById(R.id.list_anggaran);

        swipe_refresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);

        txt_filter =(TextView)findViewById(R.id.txt_filter);

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query_kas =
                        "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi ORDER BY transaksi_id DESC";

                query_total =
                        "SELECT SUM(jumlah) AS total, (SELECT SUM(jumlah) FROM transaksi WHERE status='MASUK') as masuk," +
                                "(SELECT SUM(jumlah) FROM transaksi WHERE status='KELUAR')as keluar FROM transaksi";

                KasAdapter();
            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //intent ke AddActivity dgn bentuk lain
                startActivity(new Intent(MainActivity.this, AddActivity.class));

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
        }

        KasAdapter();

    }

    private void KasAdapter() {

        swipe_refresh.setRefreshing(false);
        arraykas.clear();list_anggaran.setAdapter(null);

        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery(query_kas, null);
        cursor.moveToFirst();

        //utk penulisan format mata uang
        NumberFormat formatRupiah = NumberFormat.getInstance(Locale.GERMANY);

        int i;
        for (i=0; i < cursor.getCount(); i++){
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
                Hapus();
            }
        });
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
            startActivity(new Intent(MainActivity.this, FilterActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
