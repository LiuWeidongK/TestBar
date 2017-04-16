package com.example.misaya.testbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misaya.utils.NetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String keyfrom = "TYNsWord";
    private static final String key = "92609876";
    private static final String type = "data";
    private static final String doctype = "json";
    private static final String version = "1.1";
    private static final String baseUrl = "http://katarinar.top/tt/server/";
    private static final String initUnit = "unit1";

    private Toolbar toolbar;
    private ImageView imgBefore,imgAfter;
    private RelativeLayout layoutHide,layoutAudio,layoutShow;
    private TextView tvNowNum,tvTotalNum,tvWord,tvPhonetic,tvExplain;

    private JSONArray arr;
    JSONObject jsonObject = null;
    private Activity mActivity;
    private Context mContext;
    private int index = 0;
    Handler handler = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);           //隐藏ToolBar
        setContentView(R.layout.activity_main);

        mActivity = this;
        mContext = getApplication();
        init();
        chooseUnit(initUnit);

        imgBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index--;
                imgAfter.setVisibility(View.VISIBLE);
                if(index<=0){
                    imgBefore.setVisibility(View.INVISIBLE);
                }
                setWord(index);
            }
        });

        imgAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                imgBefore.setVisibility(View.VISIBLE);
                if(index>=arr.length()-1){
                    imgAfter.setVisibility(View.INVISIBLE);
                }
                setWord(index);
            }
        });

        layoutShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContent();
            }
        });

        layoutAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    audioService(arr.getString(index));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        layoutHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideContent();
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 1:
                        setTitle((String) msg.obj);
                        imgBefore.setVisibility(View.INVISIBLE);
                        if(arr.length()==1)
                            imgAfter.setVisibility(View.INVISIBLE);
                        else imgAfter.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        tvExplain.setText("");
                        tvNowNum.setText(String.valueOf(index + 1));
                        tvTotalNum.setText(String.valueOf(arr.length()));
                        tvPhonetic.setVisibility(View.INVISIBLE);
                        tvExplain.setVisibility(View.INVISIBLE);
                        try {
                            tvWord.setText(arr.getString(index));
                            String phonetic = jsonObject != null ? jsonObject.getString("phonetic") : null;
                            if(phonetic!=null){
                                if(phonetic.contains("[")||phonetic.contains("]"))
                                    tvPhonetic.setText(phonetic);
                                else tvPhonetic.setText("[ " + phonetic + " ]");
                            }
                            JSONArray explains = jsonObject != null ? jsonObject.getJSONArray("explains") : null;
                            for(int i = 0; i< (explains != null ? explains.length() : 0); i++){
                                tvExplain.append(explains.getString(i) + "\n\n");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        Toast.makeText(mContext,"暂未录入...",Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.mToolBar);
        imgBefore = (ImageView) findViewById(R.id.img_before);
        imgAfter = (ImageView) findViewById(R.id.img_after);
        layoutHide = (RelativeLayout) findViewById(R.id.layout_hide);
        layoutAudio = (RelativeLayout) findViewById(R.id.layout_audio);
        layoutShow = (RelativeLayout) findViewById(R.id.layout_show);
        tvNowNum = (TextView) findViewById(R.id.tv_nowNum);
        tvTotalNum = (TextView) findViewById(R.id.tv_totalNum);
        tvWord = (TextView) findViewById(R.id.tv_word);
        tvPhonetic = (TextView) findViewById(R.id.tv_phonetic);
        tvExplain = (TextView) findViewById(R.id.tv_explain);
    }

    private void chooseUnit(final String unit) {
        final String url = baseUrl + "getWords.php?unit=" + unit;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray tempArr = new JSONArray(NetUtils.get(url));
                    if (tempArr.length() != 0) {
                        arr = tempArr;
                        index = 0;
                        Log.e("WordList", arr.toString());
                        setWord(index);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = unit;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = 3;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setWord(final int index) {
        String word = null;
        try {
            word = arr.getString(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "http://fanyi.youdao.com/openapi.do?keyfrom=" + keyfrom +
                "&key=" + key +
                "&type=" + type +
                "&doctype=" + doctype +
                "&version=" + version +
                "&q=" + word;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    jsonObject = new JSONObject(NetUtils.get(url)).getJSONObject("basic");
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void audioService(String word) {
        Intent intent = new Intent(MainActivity.this, AudioService.class);
        intent.putExtra("query", word);
        startService(intent);
    }

    private void setTitle(String unit) {
        toolbar.setTitle(unit);
        setSupportActionBar(toolbar);
    }

    private void showContent() {
        tvWord.setVisibility(View.VISIBLE);
        tvPhonetic.setVisibility(View.VISIBLE);
        tvExplain.setVisibility(View.VISIBLE);
    }

    private void hideContent() {
        tvWord.setVisibility(View.INVISIBLE);
        tvPhonetic.setVisibility(View.INVISIBLE);
        tvExplain.setVisibility(View.INVISIBLE);
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
