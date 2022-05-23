package com.example.travel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LocationInfo extends AppCompatActivity {

    AlertDialog.Builder alertDialog;
    int editMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRemoveMarkerDialog();
        setContentView(R.layout.activity_location_info);
        TextView title = findViewById(R.id.Info_title);
        TextView date = findViewById(R.id.Info_Date);
        EditText context = findViewById(R.id.Info_context);

        Bundle bundle = this.getIntent().getExtras();
        System.out.println(bundle.getString("Title"));
        title.setText(bundle.getString("Title"));
        date.setText(bundle.getString("Date"));
        context.setEditableFactory(Editable.Factory.getInstance());
        context.setClickable(false);
        context.setEnabled(false);

        SharedPreferences pref =  getSharedPreferences("Context",MODE_PRIVATE);
        context.setText(pref.getString(bundle.getString("MarkerID"),"尚無資料."));

        FloatingActionButton del = findViewById(R.id.Delete);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setClass(LocationInfo.this,MapsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("delMarker",bundle.getString("MarkerID"));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        FloatingActionButton edit = findViewById(R.id.Edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editMode == 0){
                    context.setFocusable(true);
                    context.setEnabled(true);
                    context.setClickable(true);
                    context.setFocusableInTouchMode(true);
                    editMode = 1;
                }
                else{
                    context.setFocusable(false);
                    context.setEnabled(false);
                    context.setClickable(false);
                    context.setFocusableInTouchMode(false);

                    pref.edit().putString(bundle.getString("MarkerID"),context.getText().toString()).commit();

                    editMode = 0;
                }

            }
        });



    }

    public void initRemoveMarkerDialog(){
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("刪除內容 !");
        alertDialog.setMessage("是否確認刪除該點及內容?");
    }
}