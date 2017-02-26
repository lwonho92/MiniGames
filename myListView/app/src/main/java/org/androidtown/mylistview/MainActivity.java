package org.androidtown.mylistview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final int REQUEST_CODE_10000GAME = 1001;
    public static final int REQUEST_CODE_NUMBERBLOCK = 1002;

    Button button1, button2;
    ListView listView1, listView2;
    IconTextListAdapter adapter1, adapter2;

    Drawable[] mIcon;
    public static final String TAG = "MainActivity";

    private static String DATABASE_NAME = "Score.db";
    private static String TABLE_NAME1 = "mangame";
    private static String TABLE_NAME2 = "numberblocks";
    private static int DATABASE_VERSION = 1;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    boolean isOpen;

    MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        listView1 = (ListView) findViewById(R.id.listView1);
        listView2 = (ListView) findViewById(R.id.listView2);
        adapter1 = new IconTextListAdapter(this);
        adapter2 = new IconTextListAdapter(this);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                startActivityForResult(intent, REQUEST_CODE_10000GAME);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ThirdActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NUMBERBLOCK);
            }
        });

        //아이템 데이터 만들기
        Resources res = getResources();
        mIcon = new Drawable[3];
        mIcon[0] = res.getDrawable(R.drawable.gold);
        mIcon[1] = res.getDrawable(R.drawable.silver);
        mIcon[2] = res.getDrawable(R.drawable.bronze);

        isOpen = openDatabase();
        if (isOpen) {
            executeRawQuery();
        }

        listView1.setAdapter(adapter1);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IconTextItem curItem = (IconTextItem) adapter1.getItem(position);
                String[] curData = curItem.getData();
                Toast.makeText(getApplicationContext(), "Selected : " + curData[0], Toast.LENGTH_SHORT).show();
            }
        });

        listView2.setAdapter(adapter2);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IconTextItem curItem = (IconTextItem) adapter2.getItem(position);
                String[] curData = curItem.getData();
                Toast.makeText(getApplicationContext(), "Selected : " + curData[0], Toast.LENGTH_SHORT).show();
            }
        });

//        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.dot);
////        mPlayer.setLooping(true);
////        mPlayer.seekTo(8000);
//        mPlayer.start();
    }

    @Override
    protected void onDestroy() {
        button1 = null;
        button2 = null;
        listView1 = null;
        listView2 = null;
        adapter1 = null;
        adapter2 = null;

        if(mPlayer != null) {
            mPlayer.stop();
            mPlayer = null;
        }
        super.onDestroy();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == REQUEST_CODE_10000GAME) {
            Toast.makeText(getBaseContext(), "10000 Game 갱신", Toast.LENGTH_SHORT).show();

            if(resultCode == RESULT_OK) {
                executeRawQuery();
            }
        } else if(requestCode == REQUEST_CODE_NUMBERBLOCK) {
            Toast.makeText(getBaseContext(), "Number Blocks 갱신", Toast.LENGTH_SHORT).show();

            if(resultCode == RESULT_OK) {
                executeRawQuery();
            }
        }
    }

    private boolean openDatabase() {
        println("opening database [" + DATABASE_NAME + "].");

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        return true;
    }

    private void executeRawQuery() {
        println("\nexecuteRawQuery called.\n");

        adapter1.clearItems();
        adapter2.clearItems();

        String name;
        int score;

        Cursor c1 = db.rawQuery("select name, score from " + TABLE_NAME1, null);
        int recordCount1 = c1.getCount();

        for(int i = 0; i < recordCount1; i++) {
            c1.moveToNext();
            name = c1.getString(0);
            score = c1.getInt(1);

            adapter1.addItem(new IconTextItem(null, name, score + " 점"));

            println("Record1 #" + i + " : " + name + ", " + score);
        }
        c1.close();
        adapter1.sort();                                                                             // ListView를 등수 기준으로 정렬.
        adapter1.setImage(mIcon);                                                                    // Icon 재분배.

        Cursor c2 = db.rawQuery("select name, score from " + TABLE_NAME2, null);
        int recordCount2 = c2.getCount();

        for(int i = 0; i < recordCount2; i++) {
            c2.moveToNext();
            name = c2.getString(0);
            score = c2.getInt(1);

            adapter2.addItem(new IconTextItem(null, name, score + " 점"));

            println("Record2 #" + i + " : " + name + ", " + score);
        }
        c2.close();
        adapter2.sort();
        adapter2.setImage(mIcon);

        listView1.setAdapter(adapter1);
        listView2.setAdapter(adapter2);
    }

    private void println(String msg) {
        Log.d(TAG, msg);
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            println("creating table [" + TABLE_NAME1 + "].");
            println("creating table [" + TABLE_NAME2 + "].");

            try {
                String CREATE_SQL1 = "create table if not exists " + TABLE_NAME1 + "( _id integer PRIMARY KEY autoincrement, name text, score integer)";
                db.execSQL(CREATE_SQL1);
                String CREATE_SQL2 = "create table if not exists " + TABLE_NAME2 + "( _id integer PRIMARY KEY autoincrement, name text, score integer)";
                db.execSQL(CREATE_SQL2);
            } catch (Exception ex) {
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }

            println("inserting records.");
        }

        public void onOpen(SQLiteDatabase db) {
            println("opened database [" + DATABASE_NAME + "].");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ".");

        }
    }
}
