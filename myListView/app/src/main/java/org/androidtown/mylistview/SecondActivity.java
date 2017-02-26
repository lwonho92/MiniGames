package org.androidtown.mylistview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by MY on 2016-06-24.
 */

public class SecondActivity extends Activity {
    public LinearLayout linearLayout1;
    public LinearLayout linearLayout2;
    public TextView scoreTextView;
    public MyView myView;
    public Button menuBtn;
    public Button resetBtn;
    AlertDialog.Builder dialog;

    public static final String TAG = "SecondActivity";

    private static String DATABASE_NAME = "Score.db";
    private static String TABLE_NAME1 = "mangame";
    private static int DATABASE_VERSION = 1;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    boolean isOpen;

    MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        linearLayout1 = new LinearLayout(this);
        linearLayout1.setOrientation(LinearLayout.VERTICAL);
        linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));

        linearLayout1.setBackgroundResource(R.drawable.background);

        setContentView(linearLayout1);

        linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.0F));

        menuBtn = new Button(this);
        menuBtn.setText("메뉴");
        menuBtn.setTextSize(20);
        resetBtn = new Button(this);
        resetBtn.setText("Reset");
        resetBtn.setTextSize(20);

        linearLayout2.addView(menuBtn);
        linearLayout2.addView(resetBtn);

        scoreTextView = new TextView(this);
        scoreTextView.setTextSize(30);

        myView = new MyView(this);

        linearLayout1.addView(linearLayout2);
        linearLayout1.addView(scoreTextView);
        linearLayout1.addView(myView);

        dialog = new AlertDialog.Builder(SecondActivity.this);
        dialog.setTitle("[ 랭킹 등록 ]");
        isOpen = openDatabase();

        mPlayer = MediaPlayer.create(SecondActivity.this, R.raw.dot);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mPlayer.setLooping(true);
//        mPlayer.seekTo(8000);
    }

    @Override
    protected void onDestroy() {
        if(mPlayer != null) {
            mPlayer.release();
        }
        super.onDestroy();
    }

    public class MyView extends View {
        public int widthSize;
        public double coef1 = 0.86602540378;
        public double coef2 = 0.725;

        public class D {
            public int a, b, c, d;
            public int[][] round = new int[6][4];
            public int flag = 0;
            public int color;
            public int count = 0;

            public void init(int row, int col) {
                if (col % 2 == 1) {                                          //짝수 열일때 (col : 홀수)
                    a = windowWidth + (int)Math.ceil(col * coef2 * widthSize);
                    b = windowHeight + (int)Math.ceil(row * coef1 * widthSize);
                    c = windowWidth + (int)Math.ceil((col * coef2 + 0.5) * widthSize);
                    d = windowHeight + (int)Math.ceil((row * coef1 + 0.5) * widthSize);

                    if (5 <= row && row <= 9) {
                        if (5 <= (row - 1)) {
                            round[count][0] = row - 1;                           //row - 1, col
                            round[count++][1] = col;
                            if (0 <= (col - 1)) {                              //row - 1, col - 1
                                round[count][0] = row - 1;
                                round[count++][1] = col - 1;
                            }
                            if ((col + 1) <= 8)   {                              //row - 1, col + 1
                                round[count][0] = row - 1;
                                round[count++][1] = col + 1;
                            }
                        }
                        if (0 <= (col - 1))   {                                 //row, col - 1
                            round[count][0] = row;
                            round[count++][1] = col - 1;
                        }
                        if ((col + 1) <= 8)   {                                 //row, col + 1
                            round[count][0] = row;
                            round[count++][1] = col + 1;
                        }
                        if ((row + 1) <= 9) {
                            round[count][0] = row + 1;
                            round[count++][1] = col;
                        }
                    }
                }
                else {                                                //홀수 열일때 (col : 짝수)
                    a = windowWidth + (int)Math.ceil(col * coef2 * widthSize);
                    b = windowHeight + (int)Math.ceil((row + 0.5) * coef1 * widthSize);
                    c = windowWidth + (int)Math.ceil((col * coef2 + 0.5) * widthSize);
                    d = windowHeight + (int)Math.ceil(((row + 0.5) * coef1 + 0.5) * widthSize);

                    if (5 <= row && row <= 9) {
                        if ((row + 1) <= 9) {
                            round[count][0] = row + 1;                           //row + 1, col
                            round[count++][1] = col;
                            if (0 <= (col - 1)) {                              //row + 1, col - 1
                                round[count][0] = row + 1;
                                round[count++][1] = col - 1;
                            }
                            if ((col + 1) <= 8)   {                              //row + 1, col + 1
                                round[count][0] = row + 1;
                                round[count++][1] = col + 1;
                            }
                        }
                        if (0 <= (col - 1))   {                                 //row, col - 1
                            round[count][0] = row;
                            round[count++][1] = col - 1;
                        }
                        if ((col + 1) <= 8)   {                                 //row, col + 1
                            round[count][0] = row;
                            round[count++][1] = col + 1;
                        }
                        if (5 <= (row - 1))   {                                 //row - 1, col
                            round[count][0] = row - 1;
                            round[count++][1] = col;
                        }
                    }
                }
            }
        }
        public class data {
            public int x, y;
            public data next;

            data(int a, int b) {
                x = a;
                y = b;
                next = null;
            }
        }

        public class stack {
            public data head = null;
            public int color;
            public int count = 0;

            public boolean is_empty() {
                if (head == null)
                    return true;
                else
                    return false;
            }
            public void PUSH(int a, int b) {
                data tmp = new data(a, b);

                tmp.next = st.head;

                st.head = tmp;
                st.count++;
            }
            public data POP() {
                data tmp = new data(-1, -1);
                data p = null;

                if (!is_empty()) {
                    p = st.head;
                    st.head = (st.head).next;

                    tmp.x = p.x;
                    tmp.y = p.y;

                    st.count--;

                    p = null;
                }

                return tmp;
            }
            public boolean is_include(int a, int b) {
                data p = st.head;

                while (p != null) {
                    if (p.x == a && p.y == b)
                        return true;
                    p = p.next;
                }
                return false;
            }
        }

        public D[][] Block = new D[10][9];
        private int mImageWidth;
        private int mImageHeight;
        private Bitmap[] bmp = new Bitmap[19];
        public Point mouse = new Point();
        public boolean m_flag;
        public stack st = new stack();
        public int score, max = 0;
        public int windowWidth, windowHeight;
        public EditText editText;

        public MyView(Context context) {
            super(context);

            int i, j, value;

            // To do...  FMOD Init.
            m_flag = false;
            score = 0;

            Resources res = getResources();

            bmp[0] = BitmapFactory.decodeResource(res, R.drawable.one);
            bmp[1] = BitmapFactory.decodeResource(res, R.drawable.five);
            bmp[2] = BitmapFactory.decodeResource(res, R.drawable.ten);
            bmp[3] = BitmapFactory.decodeResource(res, R.drawable.fifty);
            bmp[4] = BitmapFactory.decodeResource(res, R.drawable.h);
            bmp[5] = BitmapFactory.decodeResource(res, R.drawable.fiveh);
            bmp[6] = BitmapFactory.decodeResource(res, R.drawable.onet);
            bmp[7] = BitmapFactory.decodeResource(res, R.drawable.fivet);
            bmp[8] = BitmapFactory.decodeResource(res, R.drawable.tent);
            bmp[9] = BitmapFactory.decodeResource(res, R.drawable.emp);
            bmp[10] = BitmapFactory.decodeResource(res, R.drawable.one_e);
            bmp[11] = BitmapFactory.decodeResource(res, R.drawable.five_e);
            bmp[12] = BitmapFactory.decodeResource(res, R.drawable.ten_e);
            bmp[13] = BitmapFactory.decodeResource(res, R.drawable.fifty_e);
            bmp[14] = BitmapFactory.decodeResource(res, R.drawable.h_e);
            bmp[15] = BitmapFactory.decodeResource(res, R.drawable.fiveh_e);
            bmp[16] = BitmapFactory.decodeResource(res, R.drawable.onet_e);
            bmp[17] = BitmapFactory.decodeResource(res, R.drawable.fivet_e);
            bmp[18] = BitmapFactory.decodeResource(res, R.drawable.tent_e);

            mImageWidth = bmp[0].getWidth();
            widthSize = mImageWidth;
            mImageHeight = bmp[0].getHeight();

            Display display = ((WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            windowWidth = display.getWidth() / 2 - widthSize * 7 / 2;
            windowHeight = display.getHeight() / 2 - (int)(mImageHeight * 5.25) - 120;
            getWindow().getAttributes().format = android.graphics.PixelFormat.RGBA_8888;

            int[] pixels = new int[mImageWidth * mImageHeight];

            for(i = 0; i < 19; i++) {
                bmp[i].getPixels(pixels, 0, mImageWidth, 0, 0, mImageWidth, mImageHeight);
                for (j = 0; j < pixels.length; j++) {
                    if (pixels[j] == Color.WHITE)
                        pixels[j] = Color.TRANSPARENT;
                }
                bmp[i] = Bitmap.createBitmap(pixels, 0, mImageWidth, mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);
            }

            for (i = 0; i < 10; i++) {
                for (j = 0; j < 9; j++) {
                    Block[i][j] = new D();
                    Block[i][j].init(i, j);

                    value = (int)(Math.random() * 100);
                    if (value < 20)
                        Block[i][j].color = 0;
                    else if (value < 40)
                        Block[i][j].color = 1;
                    else if (value < 60)
                        Block[i][j].color = 2;
                    else if (value < 80)
                        Block[i][j].color = 3;
                    else
                        Block[i][j].color = 4;
                }
            }

            menuBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_OK);
                    finish();
                }
            });

            resetBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i, j, value;

                    for (i = 0; i < 10; i++) {
                        for (j = 0; j < 9; j++) {
                            value = (int) (Math.random() * 100);
                            if (value < 20)
                                Block[i][j].color = 0;
                            else if (value < 40)
                                Block[i][j].color = 1;
                            else if (value < 60)
                                Block[i][j].color = 2;
                            else if (value < 80)
                                Block[i][j].color = 3;
                            else
                                Block[i][j].color = 4;
                        }
                    }

                    score = 0;

                    invalidate();
                }
            });
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int i, j = 0;
            String test = null;

            if(canvas != null) {
                for (i = 9; i >= 0; i--) {
                    for (j = 0; j < 9; j++) {
                        if (i >= 5) {
                            canvas.drawBitmap(bmp[Block[i][j].color], Block[i][j].a, Block[i][j].b, null);
                        } else {
                            canvas.drawBitmap(bmp[Block[i][j].color + 10], Block[i][j].a, Block[i][j].b, null);
                        }
                    }
                }
                // 점수와 score 세팅.
                scoreTextView.setText("score : " + score);
            }
        }

        public boolean is_end() {                                               //index: 1, 3, 5, 7 먼저 실행 후 0, 2, 4, 6, 8 실행.
            int row, col, color;

            for (row = 5; row <= 9; row++) {
                for (col = 0; col <= 8; col++) {
                    color = Block[row][col].color;

                    if (color % 2 == 1)   {                              // 색상의 앞자리가 5인 경우.
                        if (way(row, col, color, 0) == 2)
                            return false;
                    }
                    else {                                          // 색상의 앞자리가 1인 경우.
                        if (way(row, col, color, 0) == 5)
                            return false;
                    }
                }
            }
            return true;
        }
        public int way(int row, int col, int color, int count) {
            int i, round;

            if (Block[row][col].flag == 1 || Block[row][col].color != color)   // 이미 지나온 블럭 || 시작 색상과 블럭의 색상이 다른 경우 리턴.
                return count;

            count++;                                             // 같은 색상의 블럭으로 counting.

            if ((color % 2) == 1 && (count == 2))                         // 색상이 앞자리가 5인 경우 && count가 2가 된 경우.
                return count;
            else if ((color % 2 == 0) && (count == 5))                     // 색상의 앞자리가 1인 경우 && count가 5가 된 경우.
                return count;

            Block[row][col].flag = 1;                                 // row, col의 블럭을 지나온 것을 체크.
            round = Block[row][col].count;                              // count를 주위 블럭의 개수로 초기화.

            if (color % 2 == 1)   {                                    // 색상이 앞자리가 5인 경우.
                for (i = 0; i < round; i++) {
                    if (way(Block[row][col].round[i][0], Block[row][col].round[i][1], color, count) == 2) {
                        Block[row][col].flag = 0;
                        return 2;
                    }
                }
            }
            else {                                                // 색상이 앞자리가 1인 경우.
                for (i = 0; i < round; i++) {
                    if (way(Block[row][col].round[i][0], Block[row][col].round[i][1], color, count) == 5) {
                        Block[row][col].flag = 0;
                        return 5;
                    }
                }
            }

            Block[row][col].flag = 0;
            return count;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                int i, j, a = 0, b = 5;
                double distance = Math.sqrt(Math.pow((int) event.getX() - Block[5][0].c, 2) + Math.pow((int) event.getY() - Block[5][0].d, 2)), tmp;

                for (i = 0; i <= 9; i++) {
                    for (j = 0; j <= 8; j++) {
                        tmp = Math.sqrt(Math.pow((int) event.getX() - Block[i][j].c, 2) + Math.pow((int) event.getY() - Block[i][j].d, 2));

                        if (distance >= tmp) {
                            distance = tmp;

                            a = j;
                            b = i;
                        }
                    }
                }

                if (b >= 5) {
                    mouse.x = a;
                    mouse.y = b;

                    st.PUSH(mouse.x, mouse.y);
                    st.count = 1;

                    st.color = Block[b][a].color;
                    m_flag = true;
                }
            } else if(event.getAction() == MotionEvent.ACTION_MOVE) {
                int i, a = -1, b, c;
                double distance = Math.sqrt(Math.pow((int) event.getX() - Block[mouse.y][mouse.x].c, 2) + Math.pow((int) event.getY() - Block[mouse.y][mouse.x].d, 2)), tmp;
                data p = new data(-1, -1);

                if (m_flag) {
                    for (i = 0; i < Block[mouse.y][mouse.x].count; i++) {
                        tmp = Math.sqrt(Math.pow((int) event.getX() - Block[Block[mouse.y][mouse.x].round[i][0]][Block[mouse.y][mouse.x].round[i][1]].c, 2) + Math.pow((int) event.getY() - Block[Block[mouse.y][mouse.x].round[i][0]][Block[mouse.y][mouse.x].round[i][1]].d, 2));

                        if (distance > tmp && (st.color == Block[Block[mouse.y][mouse.x].round[i][0]][Block[mouse.y][mouse.x].round[i][1]].color)) {
                            distance = tmp;

                            a = i;
                        }
                    }

                    if (a != -1) {
                        b = Block[mouse.y][mouse.x].round[a][1];
                        c = Block[mouse.y][mouse.x].round[a][0];

                        if (c >= 5) {
                            if (st.is_include(b, c) == false) {               // 다시 지나왔던 블럭을 선택했는지 확인.
                                mouse.x = b;
                                mouse.y = c;
                                st.PUSH(mouse.x, mouse.y);
                            }
                            else if (st.head != null && st.head.next != null && st.head.next.x == b && st.head.next.y == c) {
                                p = st.POP();
                                mouse.x = b;
                                mouse.y = c;
                            }
                        }
                    }
                }
            } else if(event.getAction() == MotionEvent.ACTION_UP) {
                int i, j, k, value;
                data tmp = new data(-1, -1);
                data LAST = new data(-1, -1);

                if (m_flag) {
                    if (st.count >= 2 && st.color % 2 == 1)
                        score += (st.color + 1) * 10 * st.count;
                    else if (st.count >= 5 && st.color % 2 == 0)
                        score += (st.color + 1) * 10 * st.count;

                    if (st.color % 2 == 1) {
                        if (st.count >= 2 && !st.is_empty()) {
                            LAST = st.POP();

                            while (!st.is_empty()) {
                                tmp = st.POP();
                                Block[tmp.y][tmp.x].color = 9;
                            }

                            Block[LAST.y][LAST.x].color = st.color + 1;
                            if (max < st.color + 1)
                                max = st.color + 1;
                            mPlayer.start();
                        }
                    }
                    else {
                        if (st.count >= 5 && !st.is_empty()) {
                            LAST = st.POP();
                            Block[LAST.y][LAST.x].color = 9;

                            while (!st.is_empty()) {
                                tmp = st.POP();
                                Block[tmp.y][tmp.x].color = 9;
                            }

                            Block[LAST.y][LAST.x].color = st.color + 1;
                            if (max < st.color + 1)
                                max = st.color + 1;
                            mPlayer.start();
                        }
                    }

                    while (!st.is_empty())
                        st.POP();


                    /////////////////////////////////////////////////////////////////////// 줄 재생성 모듈.

                    for (k = 0; k <= 8; k++) {
                        for (i = 9; i >= 0; i--) {
                            if (Block[i][k].color == 9) {
                                for (j = i; j >= 0; j--) {
                                    if (Block[j][k].color != 9)
                                        break;
                                }
                                if (j != -1) {
                                    Block[i][k].color = Block[j][k].color;
                                    Block[j][k].color = 9;
                                }
                            }
                        }
                    }

                    for (i = 0; i <= 9; i++) {
                        for (j = 0; j <= 8; j++) {
                            if (Block[i][j].color == 9) {
                                value = (int)(Math.random() * 100);
                                if (value < 20)
                                    Block[i][j].color = 0;
                                else if (value < 40)
                                    Block[i][j].color = 1;
                                else if (value < 60)
                                    Block[i][j].color = 2;
                                else if (value < 80)
                                    Block[i][j].color = 3;
                                else
                                    Block[i][j].color = 4;
                            }
                        }
                    }
                    ///////////////////////////////////////////////////////////////////////

                    invalidate();
                }
                m_flag = false;

                if (is_end() == true) {
//                    Toast.makeText(getContext(), "Game Over !", Toast.LENGTH_SHORT).show();
                    editText = null;
                    editText = new EditText(SecondActivity.this);
                    editText.setHint("이름을 입력하시오. [4자 이하]");
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

                    dialog.setView(editText);

                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String inputValue = editText.getText().toString();
                            if (isOpen) {
                                executeQuery(inputValue, score);
                            }
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                    Toast.makeText(getContext(), "10000 LOSE !", Toast.LENGTH_SHORT).show();
                }
                else if (max >= 8) {
                    editText = null;
                    editText = new EditText(SecondActivity.this);
                    editText.setHint("이름을 입력하시오. [4자 이하]");
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

                    dialog.setView(editText);

                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String inputValue = editText.getText().toString();
                            if (isOpen) {
                                executeQuery(inputValue, score);
                            }
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                    Toast.makeText(getContext(), "10000 WIN !", Toast.LENGTH_SHORT).show();
                }
            }

//        return super.onTouchEvent(event);
            return true;
        }
    }

    private boolean openDatabase() {
        println("opening database [" + DATABASE_NAME + "].");

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        return true;
    }

    private void executeQuery(String name, int score) {
        try {
            db.execSQL("insert into " + TABLE_NAME1 + "(name, score) values ('" + name + "', " + score + ");");
        } catch(Exception ex) {
            Log.e(TAG, "Exception in insert SQL", ex);
        }
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

            try {
                String CREATE_SQL1 = "create table if not exists " + TABLE_NAME1 + "( _id integer PRIMARY KEY autoincrement, name text, score integer)";
                db.execSQL(CREATE_SQL1);
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