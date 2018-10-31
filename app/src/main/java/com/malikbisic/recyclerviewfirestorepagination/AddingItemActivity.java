package com.malikbisic.recyclerviewfirestorepagination;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by korisnik on 15/12/2017.
 */

public class AddingItemActivity extends AppCompatActivity {

    EditText titleText;
    EditText descText;
    Button submitBtn;
//firestore db
    FirebaseFirestore db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding_item);

        titleText = (EditText) findViewById(R.id.titleAdd);
        descText = (EditText) findViewById(R.id.descAdd);
        submitBtn = (Button) findViewById(R.id.buttonSubmit);

        db = FirebaseFirestore.getInstance();
        getSupportActionBar().setHomeButtonEnabled(true);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
    }

    public void addItem(){
        String title = titleText.getText().toString().trim();
        String desc = descText.getText().toString().trim();

        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("title", title);
        itemMap.put("desc", desc);
        itemMap.put("time", FieldValue.serverTimestamp());

        db.collection("Item").add(itemMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(AddingItemActivity.this, "Posted", Toast.LENGTH_LONG).show();
                titleText.setText("");
                descText.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("errorPosting", e.getLocalizedMessage());
            }
        });

    }
}
