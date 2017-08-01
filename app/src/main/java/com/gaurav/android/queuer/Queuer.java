package com.gaurav.android.queuer;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;



public class Queuer extends AppCompatActivity {

    private Toolbar mToolbar;
    private FloatingActionButton mFAB;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private AdapterRV arv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queuer);


        mToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(mToolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.appRecyclerView);
        GetArrayList gal = ((GetArrayList) getApplicationContext());
        arv = new AdapterRV(this, gal.GetFromSP());

        mRecyclerView.setAdapter(arv);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


        mFAB = (FloatingActionButton) findViewById(R.id.FAB);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Queuer.this, FormActivity2.class);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        arv.notifyDataSetChanged();
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.alert_menu) {
            Toast.makeText(this, "Alert Selected", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    // this method is override to close the app on back press when user in queuer activity
    //without this the app will go back to Form fragment2 of formActivity and will lead to data corruption by certain user action
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        
    }
}
