package edu.iot.gallery1;

import android.Manifest;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.eqot.fontawesome.FontAwesome;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Permission;
import java.util.List;

public class MainActivity extends PermissionActivity {
    final static String KEY_INDEX = "index";

    File[] images; //SD카드의 이미지 파일(jpg)들을 저장할 배열

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

        FontAwesome.applyToAllViews(this, findViewById(R.id.activity_main)); //font-awesome 적용하기

        if(savedInstanceState != null){ //화면 회전 시 (앱 시작 시 x)
            currentImage = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        imageView.setScaleType(scaleType);

        //제스처 이벤트를 위한 제스처 디텍터 준비
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                int direction = (int)(e2.getX()-e1.getX());
                if(direction > 0){
                    onNext(null);
                }else{
                    onPrevious(null);
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
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

        //이미지 파일 목록 구성
        File extFile = Environment.getExternalStorageDirectory(); //SD 카드 폴더의 절대 경로 얻어옴
        images = extFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String fname = pathname.getName().toLowerCase();
                return fname.endsWith(".jpg");
            }
        });

        setImageView(images[currentImage]);
    }

    public void setImageView(File file){
        //파일로부터 이미지 데이터(byte[]) 읽기
        byte[] image = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(image);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        }
        catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }

        //바이트 배열로부터 비트맵 생성
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        imageView.setImageBitmap(bitmap);
    }

    //화면 회전 시 현재 이미지 인덱스 저장
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, currentImage);
    }

    public void onNext(View view) {
        currentImage = (currentImage + 1) % images.length;

        setImageView(images[currentImage]);
    }

    public void onPrevious(View view) {
        currentImage = currentImage - 1;
        if(currentImage<0) currentImage = images.length-1;

        setImageView(images[currentImage]);
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
