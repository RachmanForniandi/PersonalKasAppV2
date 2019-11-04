package com.example.android.personalkasappv2;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class OutcomeFragment extends Fragment {

    @BindView(R.id.et_jumlah_out) EditText et_jumlah_out;
    @BindView(R.id.et_keterangan_out)EditText et_keterangan_out;
    @BindView(R.id.rip_simpan_out) RippleView rip_simpan_out;

    SqliteHelper sqliteHelper;


    public OutcomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_outcome, container, false);
        ButterKnife.bind(this,view);
        sqliteHelper= new SqliteHelper(getActivity());

        rip_simpan_out.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if(et_jumlah_out.getText().toString().equals("") || et_keterangan_out.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Isi data dengan benar",
                            Toast.LENGTH_LONG).show();
                }else{
                    queryMysql();
                    //_createSQLite();
                    getActivity().finish();
                }
            }
        });


        return view;
    }

    private void queryMysql(){
        AndroidNetworking.post(Config.HOST+"create.php")
                .addBodyParameter("status", "KELUAR")
                .addBodyParameter("jumlah", et_jumlah_out.getText().toString())
                .addBodyParameter("keterangan", et_keterangan_out.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            if (response.getString("response").equals("success")){
                                //startActivity(new Intent(AddActivity.this,MainActivity.class));
                                Toast.makeText(getActivity(), "Data transaksi berhasil disimpan", Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            }else {
                                Toast.makeText(getActivity(), response.getString("response"),
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

    private void _createSQLite(){
        SQLiteDatabase database = sqliteHelper.getWritableDatabase();
        database.execSQL(
                "INSERT INTO transaksi(status, jumlah, keterangan) VALUES('" + "MASUK" +
                        "','"+ et_jumlah_out.getText().toString() + "','" + et_keterangan_out.getText().toString() + "')"
        );
        Toast.makeText(getActivity(), "Data transaksi berhasil disimpan",
                Toast.LENGTH_LONG).show();
        getActivity().finish();
    }
}
