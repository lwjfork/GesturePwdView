package com.lwjfork.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lwjfork.widget.GestureLockView;
import com.lwjfork.widget.GestureLockViewIndicator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GestureLockViewIndicator lockViewIndicator = (GestureLockViewIndicator) findViewById(R.id.lock_indicator);
        final GestureLockView gesture_lock_view = (GestureLockView) findViewById(R.id.gesture_lock_view);

        gesture_lock_view.setOnCodeConvertAdapter(new OnCodeAdapter());
        lockViewIndicator.setOnDecodeAdapter(new OnCodeAdapter());

        gesture_lock_view.setOnGestureCallBackListener(new GestureLockView.OnGestureCallBackListener<String>() {

            @Override
            public void onGestureCodeInput(ArrayList<Integer> code, String inputCode) {
                Log.e("MainActivity", "111");
                lockViewIndicator.setPath(inputCode);
            }

            @Override
            public void onCheckedSuccess(ArrayList<Integer> code, String rightCode) {
                Log.e("MainActivity", "2222");
            }

            @Override
            public void onCheckedFail(ArrayList<Integer> code, String errorCode) {
                Log.e("MainActivity", "44444");
            }
        });
    }


    private class OnCodeAdapter implements GestureLockView.OnCodeConvertAdapter<String, String>, GestureLockViewIndicator.OnDecodeAdapter<String> {

        @Override
        public ArrayList<Integer> convertObj2Code(String oldCode) {
            ArrayList<Integer> codes = new ArrayList<>();
            String[] array = oldCode.split(",");
            for (String s : array) {
                codes.add(Integer.valueOf(s));
            }
            return codes;
        }

        @Override
        public String convertCode2Obj(ArrayList<Integer> code) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Integer integer : code) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(integer);
            }
            return stringBuilder.toString();
        }

        @Override
        public ArrayList<Integer> decodePath(String object) {
            return convertObj2Code(object);
        }
    }


}
