package edu.iot.gallery1;

import android.Manifest;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.eqot.fontawesome.FontAwesome;

import java.security.Permission;

public class MainActivity extends PermissionActivity {
    final static String KEY_INDEX = "index";

    int[] images = {
            R.drawable.banana,
            R.drawable.candy,
            R.drawable.cream,
            R.drawable.loveme,
            R.drawable.strawberry
    };

    int currentImage = 0; //현재 출력 이미지 인덱스

    ImageView imageView;
    GestureDetector detector;
    ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_INSIDE;

    boolean back = false; //앱 종료할지 판단하기 위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //퍼미션 설정
        permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        super.onCreate(savedInstanceState); //부모(PermissionActivity의 onCreate()호출)
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

        FontAwesome.applyToAllViews(this, findViewById(R.id.activity_main));

        if(savedInstanceState != null){ //화면 회전 시 (앱 시작 시 x)
            currentImage = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        imageView.setScaleType(scaleType);
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                int direction = (int)(e2.getX()-e1.getX());
                if(direction > 0){
                    onBtnNextClicked(null);
                }else{
                    onBtnPrevClicked(null);
                }
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                if(scaleType==ImageView.ScaleType.CENTER_INSIDE){
                    scaleType = ImageView.ScaleType.CENTER_CROP;
                }else{
                    scaleType = ImageView.ScaleType.CENTER_INSIDE;
                }
                imageView.setScaleType(scaleType);
                return true;
            }
        });
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                detector.onTouchEvent(motionEvent);
                return true;
            }
        });
        changeImage(images[currentImage]);
    }

    //화면 회전 시 동작
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, currentImage);
    }

    public void onBtnNextClicked(View view) {
        currentImage = (currentImage + 1) % images.length;

        changeImage(images[currentImage]);
    }

    public void onBtnPrevClicked(View view) {
        currentImage = currentImage - 1;
        if(currentImage<0) currentImage = images.length-1;
        Log.d("Current_Image", ":"+currentImage); //태그, 메시지

        changeImage(images[currentImage]);
    }

    private void changeImage(int imageId) { //자원의 id는 integer 타입
        Resources res = getResources(); //자원에 접근할 수 있는 Resources 객체 생성. getResources()는 액티비티의 메서드
        BitmapDrawable bitmap = (BitmapDrawable)res.getDrawable(imageId);
        imageView.setImageDrawable(bitmap);
    }

    //시스템 back 버튼 클릭
    @Override
    public void onBackPressed() {
        if(back){
            super.onBackPressed(); //앱 종료
        } else{
            back = true;
            Toast.makeText(this, "한번더 누르면 종료합니다..", Toast.LENGTH_LONG).show();

            //스레드를 이용해 3초 후에 back=false로 변경
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(3000);
                        back = false;
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
