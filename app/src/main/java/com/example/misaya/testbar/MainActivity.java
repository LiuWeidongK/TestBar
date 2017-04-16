package com.example.misaya.testbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);           //隐藏ToolBar
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mToolBar);
        toolbar.setTitle("Unit1");
        setSupportActionBar(toolbar);

        RelativeLayout show = (RelativeLayout) findViewById(R.id.layout_show);
        RelativeLayout hide = (RelativeLayout) findViewById(R.id.layout_hide);

        final TextView word = (TextView) findViewById(R.id.tv_word);
        final TextView phonetic = (TextView) findViewById(R.id.tv_phonetic);
        final TextView explain = (TextView) findViewById(R.id.tv_explain);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                word.setVisibility(View.VISIBLE);
                phonetic.setVisibility(View.VISIBLE);
                explain.setVisibility(View.VISIBLE);
            }
        });

        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                word.setVisibility(View.INVISIBLE);
                phonetic.setVisibility(View.INVISIBLE);
                explain.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set:
                Toast.makeText(MainActivity.this, "Setting", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return true;
    }
}
