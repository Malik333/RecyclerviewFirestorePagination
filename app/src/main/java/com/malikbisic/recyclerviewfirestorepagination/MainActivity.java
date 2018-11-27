package com.malikbisic.recyclerviewfirestorepagination;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.malikbisic.recyclerviewfirestorepagination.Adapter.ItemAdapter;
import com.malikbisic.recyclerviewfirestorepagination.Model.Item;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ItemAdapter adapter;
    List<Item> itemList = new ArrayList<>();

    FirebaseFirestore db;
    Query query;
    DocumentSnapshot lastVisible;
    DocumentSnapshot prevItemVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ItemAdapter(itemList, MainActivity.this,MainActivity.this, recyclerView);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadData();

        adapter.setOnLoadMore(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        itemList.add(null);
                        adapter.notifyItemInserted(itemList.size() - 1);
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        loadMoreData();



                    }
                }, 4000);

            }
        });


    }

    public void loadData(){
        query = db.collection("Item");
        query.orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(7).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                for (DocumentSnapshot snapshot : querySnapshot.getDocuments()){
                    Item model = snapshot.toObject(Item.class);
                    itemList.add(model);
                    adapter.notifyDataSetChanged();

                    lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() -1);
                }
            }
        });
    }

    public void loadMoreData(){
        com.google.firebase.firestore.Query next = FirebaseFirestore.getInstance().collection("Item")
                .orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(7);
        next.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(final QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                if (e == null) {

                    for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {

                        Item model = snapshot.toObject(Item.class);
                        itemList.add(model);
                        adapter.notifyDataSetChanged();
                        lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                        adapter.setIsLoading(false);


                    }

                    com.google.firebase.firestore.Query next = FirebaseFirestore.getInstance().collection("Item")
                            .orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING);
                    next.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot querySnapshot2, FirebaseFirestoreException e) {
                            if (e == null) {

                                prevItemVisible = querySnapshot2.getDocuments().get(querySnapshot2.size() - 1);

                                if (prevItemVisible.getId().equals(lastVisible.getId())){
                                    adapter.isFullLoaded(true);
                                }



                                Log.i("postTOTALCOUNT", String.valueOf(lastVisible.getId()));
                                Log.i("postLIST", String.valueOf(prevItemVisible.getId()));
                            }
                        }

                    });


                } else {
                    Log.e("errorLoadMore", e.getLocalizedMessage());
                }
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.addingItem){
            Intent openAdd = new Intent(MainActivity.this,AddingItemActivity.class);
            startActivity(openAdd);
        }

        return super.onOptionsItemSelected(item);
    }
}
