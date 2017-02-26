package org.androidtown.mylistview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Stack;

public class ThirdActivity extends Activity {

    int width;
    private GestureDetector mGestures = null;
    String string;
    int Score;

    public Button menuBtn;
    public Button resetBtn;
    AlertDialog.Builder dialog;

    public EditText editText;

    public static final String TAG = "ThirdActivity";

    private static String DATABASE_NAME = "Score.db";
    private static String TABLE_NAME2 = "numberblocks";
    private static int DATABASE_VERSION = 1;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    boolean isOpen;

    MediaPlayer mPlayer;

    public boolean onTouchEvent(MotionEvent event){
        if(mGestures != null){
            return mGestures.onTouchEvent(event);
        } else{
            return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Display display = ((WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        menuBtn = new Button(this);
        menuBtn.setText("메뉴");

        width = display.getWidth();
        final game2 game = new game2(this);
        game.setBackgroundResource(R.drawable.background);
        setContentView(game);
        game.setStartBlock();

        LinearLayout scoreView = new LinearLayout(this);
        LinearLayout mainView = new LinearLayout(this);

        scoreView.setOrientation(LinearLayout.HORIZONTAL);
        mainView.setOrientation(LinearLayout.VERTICAL);

        menuBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.0F));

        mainView.addView(menuBtn);
        //scoreView.addView(menuBtn);
        //mainView.setGravity(Gravity.CENTER);

        string = Integer.toString(width);
        TextView scoreText = new TextView(this);
        scoreText.setText("Score : ");
        scoreText.setTextSize(30);
        scoreView.addView(scoreText);

        final TextView currentScore = new TextView(this);
        currentScore.setTextSize(30);
        currentScore.setText(Integer.toString(Score));
        scoreView.addView(currentScore);

        mainView.addView(scoreView);

        addContentView(mainView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));




        mGestures = new GestureDetector(
                new GestureDetector.SimpleOnGestureListener(){
                    public boolean onFling(MotionEvent e1,MotionEvent e2,float distanceX, float distanceY){


                        if(distanceX > 400 && distanceX > 0 ){
                            if(game.start1.x < 2 && game.start2.x < 2) {
                                game.start1.x += 1;
                                game.start2.x += 1;
                                game.invalidate();
                                game.scoreBlock = 0;
                                //Toast.makeText(getApplicationContext(),"Right!",Toast.LENGTH_SHORT).show();
                            }
                        } else if (distanceX < -400  && distanceX < 0){
                            if(game.start1.x > -2 && game.start2.x > -2){
                                game.start1.x -= 1;
                                game.start2.x -= 1;
                                game.invalidate();
                                game.scoreBlock = 0;
                                //Toast.makeText(getApplicationContext(),"Left!",Toast.LENGTH_SHORT).show();
                            }
                        }

                        return super.onScroll(e1,e2,distanceX,distanceY);
                    }
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if (game.start1.y > game.start2.y) {

                            for (int j = 0; j < 5; j++) {
                                if (game.map[j][game.start2.x + 2] == 0) {
                                    game.map[j][game.start2.x + 2] = game.start2.num;

                                    break;
                                } else if (game.map[j][game.start2.x + 2] != 0 && j == 4) {
                                    editText = null;
                                    editText = new EditText(ThirdActivity.this);
                                    editText.setHint("이름을 입력하시오. [4자 이하]");
                                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

                                    dialog.setView(editText);

                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String inputValue = editText.getText().toString();
                                            if (isOpen) {
                                                executeQuery(inputValue, Score);
                                            }
                                            Score = 0;
                                            game.scoreBlock = 0;
                                            game.scoreMatch = 0;
                                            currentScore.setText(Integer.toString(Score));
                                        }
                                    });
                                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Score = 0;
                                            game.scoreBlock = 0;
                                            game.scoreMatch = 0;
                                            currentScore.setText(Integer.toString(Score));
                                            dialog.cancel();
                                        }
                                    });

                                    dialog.show();
                                    System.out.println("Score1 : " + Score);
                                    Toast.makeText(getApplicationContext(), "Game Over!", Toast.LENGTH_SHORT).show();


                                    game.max_num = 1;
                                    game.reset = true;
                                    break;

                                }
                            }

                            for (int j = 0; j < 5; j++) {
                                if (game.map[j][game.start1.x + 2] == 0) {
                                    game.map[j][game.start1.x + 2] = game.start1.num;

                                    break;
                                } else if (game.map[j][game.start1.x + 2] != 0 && j == 4) {
                                    editText = null;
                                    editText = new EditText(ThirdActivity.this);
                                    editText.setHint("이름을 입력하시오. [4자 이하]");
                                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

                                    dialog.setView(editText);

                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String inputValue = editText.getText().toString();
                                            if (isOpen) {
                                                executeQuery(inputValue, Score);
                                                Score = 0;
                                                game.scoreBlock = 0;
                                                game.scoreMatch = 0;
                                                currentScore.setText(Integer.toString(Score));
                                            }
                                        }
                                    });
                                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Score = 0;
                                            game.scoreBlock = 0;
                                            game.scoreMatch = 0;
                                            currentScore.setText(Integer.toString(Score));
                                            dialog.cancel();
                                        }

                                    });

                                    dialog.show();
                                    System.out.println("Score2 : " + Score);
                                    Toast.makeText(getApplicationContext(), "Game Over!", Toast.LENGTH_SHORT).show();
                                    game.max_num = 1;
                                    game.reset = true;
                                    break;

                                }
                            }

                            game.searchNode();
                            game.setStartBlock();
                            game.invalidate();
                            Score = game.scoreBlock + game.scoreMatch;
                            string = Integer.toString(Score);
                            currentScore.setText(string);
                            game.scoreBlock = 0;

                        } else if (game.start1.y < game.start2.y) {
                            for (int j = 0; j < 5; j++) {
                                if (game.map[j][game.start1.x + 2] == 0) {
                                    game.map[j][game.start1.x + 2] = game.start1.num;

                                    break;
                                } else if (game.map[j][game.start1.x + 2] != 0 && j == 4) {
                                    editText = null;
                                    editText = new EditText(ThirdActivity.this);
                                    editText.setHint("이름을 입력하시오. [4자 이하]");
                                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

                                    dialog.setView(editText);

                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String inputValue = editText.getText().toString();
                                            if (isOpen) {
                                                executeQuery(inputValue, Score);
                                                Score = 0;
                                                game.scoreBlock = 0;
                                                game.scoreMatch = 0;
                                                currentScore.setText(Integer.toString(Score));
                                            }
                                        }
                                    });
                                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Score = 0;
                                            game.scoreBlock = 0;
                                            game.scoreMatch = 0;
                                            currentScore.setText(Integer.toString(Score));
                                            dialog.cancel();
                                        }
                                    });

                                    dialog.show();
                                    System.out.println("Score3 : " + Score);
                                    Toast.makeText(getApplicationContext(), "Game Over!", Toast.LENGTH_SHORT).show();
                                    game.max_num = 1;
                                    game.reset = true;
                                    break;

                                }
                            }

                            for (int j = 0; j < 5; j++) {
                                if (game.map[j][game.start2.x + 2] == 0) {
                                    game.map[j][game.start2.x + 2] = game.start2.num;

                                    break;
                                } else if (game.map[j][game.start2.x + 2] != 0 && j == 4) {
                                    if (game.reset) break;
                                    editText = null;
                                    editText = new EditText(ThirdActivity.this);
                                    editText.setHint("이름을 입력하시오. [4자 이하]");
                                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

                                    dialog.setView(editText);

                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String inputValue = editText.getText().toString();
                                            if (isOpen) {
                                                executeQuery(inputValue, Score);
                                                Score = 0;
                                                game.scoreBlock = 0;
                                                game.scoreMatch = 0;
                                                currentScore.setText(Integer.toString(Score));
                                            }
                                        }
                                    });
                                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Score = 0;
                                            game.scoreBlock = 0;
                                            game.scoreMatch = 0;
                                            currentScore.setText(Integer.toString(Score));
                                            dialog.cancel();
                                        }
                                    });

                                    dialog.show();
                                    System.out.println("Score4 : " + Score);
                                    Toast.makeText(getApplicationContext(), "Game Over!", Toast.LENGTH_SHORT).show();
                                    game.max_num = 1;
                                    game.reset = true;
                                    break;

                                }
                            }
                            game.searchNode();
                            game.setStartBlock();
                            game.invalidate();
                            Score = game.scoreBlock + game.scoreMatch;
                            string = Integer.toString(Score);
                            currentScore.setText(string);
                            game.scoreBlock = 0;
                        } else {
                            for (int j = 0; j < 5; j++) {
                                if (game.map[j][game.start1.x + 2] == 0) {
                                    game.map[j][game.start1.x + 2] = game.start1.num;
                                    game.start1.x = 0;
                                    game.start1.y = 0;
                                    break;
                                } else if (game.map[j][game.start1.x + 2] != 0 && j == 4) {
                                    editText = null;
                                    editText = new EditText(ThirdActivity.this);
                                    editText.setHint("이름을 입력하시오. [4자 이하]");
                                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

                                    dialog.setView(editText);

                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String inputValue = editText.getText().toString();
                                            if (isOpen) {
                                                executeQuery(inputValue, Score);
                                                Score = 0;
                                                game.scoreBlock = 0;
                                                game.scoreMatch = 0;
                                                currentScore.setText(Integer.toString(Score));
                                            }
                                        }
                                    });
                                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Score = 0;
                                            game.scoreBlock = 0;
                                            game.scoreMatch = 0;
                                            currentScore.setText(Integer.toString(Score));
                                            dialog.cancel();
                                        }
                                    });

                                    dialog.show();
                                    System.out.println("Score5 : " + Score);
                                    Toast.makeText(getApplicationContext(), "Game Over!", Toast.LENGTH_SHORT).show();
                                    game.max_num = 1;
                                    game.reset = true;
                                    break;

                                }
                            }
                            for (int j = 0; j < 5; j++) {
                                if (game.map[j][game.start2.x + 2] == 0) {
                                    game.map[j][game.start2.x + 2] = game.start2.num;
                                    game.start2.x = 0;
                                    game.start2.y = 0;
                                    break;
                                } else if (game.map[j][game.start2.x + 2] != 0 && j == 4) {
                                    editText = null;
                                    editText = new EditText(ThirdActivity.this);
                                    editText.setHint("이름을 입력하시오. [4자 이하]");
                                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

                                    dialog.setView(editText);

                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String inputValue = editText.getText().toString();
                                            if (isOpen) {
                                                executeQuery(inputValue, Score);
                                                Score = 0;
                                                game.scoreBlock = 0;
                                                game.scoreMatch = 0;
                                                currentScore.setText(Integer.toString(Score));
                                            }
                                        }
                                    });
                                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Score = 0;
                                            game.scoreBlock = 0;
                                            game.scoreMatch = 0;
                                            currentScore.setText(Integer.toString(Score));
                                            dialog.cancel();
                                        }
                                    });

                                    dialog.show();
                                    System.out.println("Score6 : " + Score);
                                    Toast.makeText(getApplicationContext(), "Game Over!", Toast.LENGTH_SHORT).show();
                                    game.max_num = 1;
                                    game.reset = true;
                                    break;

                                }
                            }
                            game.searchNode();
                            game.setStartBlock();
                            game.invalidate();
                            Score = game.scoreBlock + game.scoreMatch;
                            string = Integer.toString(Score);
                            currentScore.setText(string);
                            game.scoreBlock = 0;
                        }
                        return super.onSingleTapConfirmed(e);
                    }
                    public boolean onDoubleTap(MotionEvent e){
                        if(game.start1.x == game.start2.x && game.start1.y < game.start2.y){
                            if(game.start2.x == 2){
                                game.start1.x--;
                                game.start2.y -=1;
                            } else if(game.start2.x < 1){
                                game.start2.x++;
                                game.start2.y -=1;
                            }
                        }else if(game.start1.y == game.start2.y && game.start1.x < game.start2.x){
                            game.start1.y += 1;
                            game.start2.x -=1;
                        } else if(game.start1.x == game.start2.x && game.start1.y > game.start2.y){
                            if(game.start2.x == -2){
                                game.start1.x += 1;
                                game.start1.y -=1;
                            }else{
                                game.start2.x -=1;
                                game.start1.y -=1;
                            }
                        }else{
                            game.start1.x -=1;
                            game.start2.y +=1;
                        }
                        game.invalidate();
                        game.scoreBlock = 0;
                        return super.onDoubleTap(e);
                    }
                }
        );

        dialog = new AlertDialog.Builder(ThirdActivity.this);
        dialog.setTitle("[ 랭킹 등록 ]");
        isOpen = openDatabase();

        mPlayer = MediaPlayer.create(ThirdActivity.this, R.raw.dot);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onDestroy() {
        if(mPlayer != null) {
            mPlayer.release();
        }
        super.onDestroy();
    }

    public class game2 extends View {
        int[][] map = {
                {1, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };
        boolean reset = false;
        Stack<SearchBlock> stack = new Stack<SearchBlock>();

        private Bitmap block1 = BitmapFactory.decodeResource(getResources(), R.drawable.bitmap2);
        private Bitmap block2 = BitmapFactory.decodeResource(getResources(), R.drawable.bitmap3);
        private Bitmap block3 = BitmapFactory.decodeResource(getResources(), R.drawable.bmp00001);
        private Bitmap block4 = BitmapFactory.decodeResource(getResources(), R.drawable.bmp00002);
        private Bitmap block5 = BitmapFactory.decodeResource(getResources(), R.drawable.bmp00003);
        private Bitmap block6 = BitmapFactory.decodeResource(getResources(), R.drawable.bmp00004);
        private Bitmap block7 = BitmapFactory.decodeResource(getResources(), R.drawable.bmp00005);
        private Bitmap block8 = BitmapFactory.decodeResource(getResources(), R.drawable.bmp00006);
        private Bitmap block9 = BitmapFactory.decodeResource(getResources(), R.drawable.bmp00007);
        private Bitmap block10 = BitmapFactory.decodeResource(getResources(), R.drawable.bmp00008);

        int blockWidth = block1.getWidth();
        int blockHeight = block1.getHeight();

        int scoreMatch=0;
        int scoreBlock =0;
        private Canvas cacheCanvas;
        private Paint mPaint;

        StartBlock start1 = new StartBlock();
        StartBlock start2 = new StartBlock();

        int max_num = 1;
        public int count =0;
        public int width;
        int height;


        Random random = new Random();

        public game2(Context context) {
            super(context);
            mPaint = new Paint();

            menuBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }

        public void printMap(Canvas canvas){

            Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();


            cacheCanvas = canvas;
            width = display.getWidth()/2;
            height = display.getHeight()/2;

            // canvas.drawLine(width - 310,height - 430,width - 310,height + 420,mPaint);
            //canvas.drawLine(width - 310,height + 420,width + 310,height + 420,mPaint);
            // canvas.drawLine(width + 310,height - 430,width + 310,height + 420,mPaint);
            // canvas.drawLine(width - 310,height - 430,width + 310,height - 430,mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(1.0F);
            canvas.drawLine(width - (2 * blockWidth + blockWidth / 2 + 10),height - (blockHeight/2 + 2 * blockHeight - 20),width + 3 * blockWidth,height - (blockHeight/2 + 2 * blockHeight - 20),mPaint);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(5.0F);
            mPaint.setColor(Color.BLACK);
            canvas.drawRect(width - (2 * blockWidth + blockWidth / 2 + 10), height - (4 * blockHeight + 20 + blockHeight / 2), width + 3 * blockWidth + 10, height + (3 * blockHeight + 10 + blockHeight / 2), mPaint);

            int widthTemp = (2*blockWidth + blockWidth / 2);
            int widthTemp2 = blockWidth + 10;

            int heightTemp = 2 * blockHeight + 30;
            int heightTemp2 = blockHeight + 10;

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    switch (map[i][j]) {
                        case 1:
                            canvas.drawBitmap(block1, width - widthTemp + (j * widthTemp2), height + heightTemp - (i * heightTemp2), null);
                            scoreBlock += 1;
                            break;
                        case 2:
                            canvas.drawBitmap(block2, width - widthTemp + (j * widthTemp2), height + heightTemp - (i * heightTemp2), null);
                            scoreBlock += 5;
                            break;
                        case 3:
                            canvas.drawBitmap(block3, width - widthTemp + (j * widthTemp2), height + heightTemp - (i * heightTemp2), null);
                            scoreBlock += 20;
                            break;
                        case 4:
                            canvas.drawBitmap(block4, width - widthTemp + (j * widthTemp2), height + heightTemp - (i * heightTemp2), null);
                            scoreBlock += 50;
                            break;
                        case 5:
                            canvas.drawBitmap(block5, width - widthTemp + (j * widthTemp2), height + heightTemp - (i * heightTemp2), null);
                            scoreBlock += 100;
                            break;
                        case 6:
                            canvas.drawBitmap(block6, width - widthTemp + (j * widthTemp2), height + heightTemp - (i * heightTemp2), null);
                            scoreBlock += 500;
                            break;
                        case 7:
                            canvas.drawBitmap(block7, width - widthTemp + (j * widthTemp2), height + heightTemp - (i * heightTemp2), null);
                            scoreBlock += 1000;
                            break;
                        case 8:
                            canvas.drawBitmap(block8, width - widthTemp + (j * widthTemp2), height + heightTemp - (i * heightTemp2), null);
                            scoreBlock += 2000;
                            break;
                        case 9:
                            canvas.drawBitmap(block9, width - widthTemp + (j * widthTemp2), height + heightTemp - (i * heightTemp2), null);
                            scoreBlock += 5000;
                            break;
                        case 10:
                            canvas.drawBitmap(block10, width - widthTemp + (j * widthTemp2), height + heightTemp - (i * heightTemp2), null);
                            scoreBlock += 10000;
                            break;
                    }

                    if (max_num < map[i][j])
                        max_num = map[i][j];
                }
            }



            switch (start1.num)
            {
                case 1:
                    canvas.drawBitmap(block1, width - blockWidth/2 + 20+ (start1.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start1.y * (blockHeight+10)), null);
                    break;
                case 2:
                    canvas.drawBitmap(block2, width - blockWidth/2+ 20 + (start1.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start1.y * (blockHeight+10)), null);
                    break;
                case 3:
                    canvas.drawBitmap(block3, width - blockWidth/2+ 20 + (start1.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start1.y * (blockHeight+10)), null);
                    break;
                case 4:
                    canvas.drawBitmap(block4, width - blockWidth/2+ 20 + (start1.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start1.y * (blockHeight+10)), null);
                    break;
                case 5:
                    canvas.drawBitmap(block5, width - blockWidth/2+ 20 + (start1.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start1.y * (blockHeight+10)), null);
                    break;
                case 6:
                    canvas.drawBitmap(block6, width - blockWidth/2+ 20 + (start1.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start1.y * (blockHeight+10)), null);
                    break;
                case 7:
                    canvas.drawBitmap(block7, width - blockWidth/2+ 20 + (start1.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start1.y * (blockHeight+10)), null);
                    break;
                case 8:
                    canvas.drawBitmap(block8, width - blockWidth/2+ 20 + (start1.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start1.y * (blockHeight+10)), null);
                    break;
                case 9:
                    canvas.drawBitmap(block9, width - blockWidth/2+ 20 + (start1.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start1.y * (blockHeight+10)), null);
                    break;
                case 10:
                    canvas.drawBitmap(block10, width - blockWidth/2+ 20 + (start1.x *(blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start1.y * (blockHeight+10)), null);
                    break;
            }

            switch (start2.num)
            {
                case 1:
                    canvas.drawBitmap(block1, width - blockWidth/2 + 20 + (start2.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start2.y * (blockHeight+10)), null);
                    break;
                case 2:
                    canvas.drawBitmap(block2, width - blockWidth/2+ 20 + (start2.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start2.y * (blockHeight+10)), null);
                    break;
                case 3:
                    canvas.drawBitmap(block3, width - blockWidth/2+ 20 + (start2.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start2.y * (blockHeight+10)), null);
                    break;
                case 4:
                    canvas.drawBitmap(block4, width - blockWidth/2+ 20 + (start2.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start2.y * (blockHeight+10)), null);
                    break;
                case 5:
                    canvas.drawBitmap(block5, width - blockWidth/2+ 20 + (start2.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start2.y * (blockHeight+10)), null);
                    break;
                case 6:
                    canvas.drawBitmap(block6, width - blockWidth/2+ 20 + (start2.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start2.y * (blockHeight+10)), null);
                    break;
                case 7:
                    canvas.drawBitmap(block7, width - blockWidth/2+ 20 + (start2.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start2.y * (blockHeight+10)), null);
                    break;
                case 8:
                    canvas.drawBitmap(block8, width - blockWidth/2+ 20 + (start2.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start2.y * (blockHeight+10)), null);
                    break;
                case 9:
                    canvas.drawBitmap(block9, width - blockWidth/2+ 20 + (start2.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start2.y * (blockHeight+10)), null);
                    break;
                case 10:
                    canvas.drawBitmap(block10, width - blockWidth/2+ 20 + (start2.x * (blockWidth+10)), height - (blockHeight/2 + 3*blockHeight) - (start2.y * (blockHeight+10)), null);
                    break;
            }
        }

        public void setStartBlock(){
            start1.x = 0;
            start1.y =0;
            start2.x = 0;
            start2.y = 0;
            start1.num = random.nextInt(max_num)+1;
            start2.num = random.nextInt(max_num)+1;

            if((start1.y == start2.y) && (start1.x == start2.x))
                start2.y+=1;


        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);



            if(reset) {
                mapReset();
                reset = false;
            }
            printMap(canvas);


        }

        public void searchNode(){
            for(int i =0;i<5;i++){
                for(int j=0;j<5;j++){
                    if(map[i][j] == 0)
                        continue;

                    insertNode(j,i,map[i][j]);
                    cmpBlock(j,i,stack.peek().num);
                    while(!stack.isEmpty()) {
                        //System.out.println("count : "+count);

                        deleteNode();

                    }
                    map_sort();
                    if(count > 2){
                        i= 0;
                        j=-1;
                    }
                    count=0;
                }
            }
        }
        public void insertNode(int x, int y,int num){


            //System.out.println("x : "+x+" y : "+y+" count : "+count);
            SearchBlock node = new SearchBlock(x,y,num,count);
            count++;
            map[y][x] = -1;
            stack.push(node);


        }

        public void deleteNode(){
            while(!stack.isEmpty()){
                if(count>2 && stack.peek().count == 0) {
                    mPlayer.start();
                    scoreMatch += 100;
                    map[stack.peek().y][stack.peek().x] = stack.peek().num + 1;
                    invalidate();
                }
                else if (count > 2 && count != 0) {
                    scoreMatch += 100;
                    map[stack.peek().y][stack.peek().x] = 0;
                    invalidate();
                }
                else
                    map[stack.peek().y][stack.peek().x] = stack.peek().num;

                stack.pop();

            }
        }

        public boolean cmpBlock(int x,int y,int num){
            boolean r_block = false;
            boolean l_block = false;
            boolean u_block = false;
            boolean d_block = false;

            // 왼쪽 검사

            // System.out.println("Pass!");
            if( x > 0) {
                if (num == map[y][x - 1]) {
                    // System.out.println("Left Pass!");
                    insertNode(x - 1, y, num);
                    cmpBlock(x - 1, y, num);
                }
            }
            //오른쪽 검사
            if(x < 4) {
                //  System.out.println("Right Pass!");
                if (num == map[y][x + 1]) {
                    System.out.println("Right Pass!");
                    insertNode(x + 1, y, num);
                    cmpBlock(x + 1, y, num);
                }
            }
            // 위쪽 검사
            if( y < 4) {
                if (num == map[y + 1][x]) {
                    //   System.out.println("Top Pass!");
                    insertNode(x, y + 1, num);
                    cmpBlock(x, y + 1, num);
                }
            }
            // 아래쪽 검사
            if( y > 0) {
                if (num == map[y - 1][x]) {
                    //   System.out.println("Bottom Pass!");
                    insertNode(x, y - 1, num);
                    cmpBlock(x, y - 1, num);
                }
            }

            return true;
        }

        void map_sort(){
            for (int i = 0; i < 5; i++)
            {
                for (int j = 0; j < 5; j++)
                {
                    if (i  > 0)
                    {
                        if (map[i][j] > 0 && map[i - 1][j] == 0)
                        {
                            map[i - 1][j] = map[i][j];
                            map[i][j] = 0;
                        }
                    }
                }
            }
        }

        void mapReset() {
            for(int i=0;i<5;i++) {
                for(int j=0;j<5;j++){
                    if(i == 0 && j== 0)
                        map[i][j] = 1;
                    else map[i][j] = 0;
                }
            }
        }
        public class SearchBlock {
            int x;
            int y;
            int num;
            int count;

            SearchBlock(int x,int y,int num,int count){
                this.x = x;
                this.y = y;
                this.num = num;
                this.count = count;
            }
            SearchBlock(){}
        }
        public class StartBlock {
            int num;
            int x=0;
            int y=0;
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
            db.execSQL("insert into " + TABLE_NAME2 + "(name, score) values ('" + name + "', " + score + ");");
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
            println("creating table [" + TABLE_NAME2 + "].");

            try {
                String CREATE_SQL1 = "create table if not exists " + TABLE_NAME2 + "( _id integer PRIMARY KEY autoincrement, name text, score integer)";
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