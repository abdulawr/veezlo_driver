package com.veezlo.veelzodriver.Driver_Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.veezlo.veelzodriver.Adapter.AboutLayoutAdapter;
import com.veezlo.veelzodriver.DataContainer.GetFAQ;
import com.veezlo.veelzodriver.DataContainer.HelpContainer;
import com.veezlo.veelzodriver.R;

import java.util.List;

public class FAQ extends AppCompatActivity {

    GetFAQ getFAQ;
    RecyclerView recyclerView;
    AboutLayoutAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


      getFAQ=new GetFAQ();
      getFAQ.AssignData();
        List<HelpContainer> list= getFAQ.getQuestion();
        recyclerView=findViewById(R.id.rec);
        recyclerView.hasFixedSize();
        adapter=new AboutLayoutAdapter(list, FAQ.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(FAQ.this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
           finish();
        }

        return true;

    }
}