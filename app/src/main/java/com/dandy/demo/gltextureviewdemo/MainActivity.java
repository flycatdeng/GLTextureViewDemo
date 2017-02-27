package com.dandy.demo.gltextureviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dandy.gldemo.glestextureview.DemoGlesTextureView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DemoGlesTextureView view=new DemoGlesTextureView(this);
//        setContentView(R.layout.activity_main);
        setContentView(view);
    }
}
