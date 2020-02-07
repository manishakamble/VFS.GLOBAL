package com.vfs.fingerprint.base;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vfs.fingerprint.R;
import com.vfs.fingerprint.database.DBHandler;

import java.util.List;

public class AdminScreenActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminScreenAdapter adminScreenAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DBHandler dbHandler = DBHandler.Companion.getInstance(this);
        List<MrzDisplayModel> list = dbHandler.getData();
        if (adminScreenAdapter == null) {
            adminScreenAdapter = new AdminScreenAdapter(list);
        }
        recyclerView.setAdapter(adminScreenAdapter);

    }
}
