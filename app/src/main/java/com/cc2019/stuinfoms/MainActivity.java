package com.cc2019.stuinfoms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cc2019.stuinfoms.stu.Stufrag;
import com.cc2019.stuinfoms.user.Userfrag;
import com.ejlchina.okhttps.OkHttps;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    private List<Fragment> frags = new ArrayList<>();
    private int position;
    private int nowFragIndex = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stufrag stufrag = new Stufrag();
        Userfrag userfrag = new Userfrag();
        frags.add(stufrag);
        frags.add(userfrag);

        radioGroup = findViewById(R.id.rgroup);

        radioChecked();
        //设置默认显示首页布局
        radioGroup.check(R.id.radio_search);



    }

    //点击导航时触发---切换fragment
    public void radioChecked() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_search:
                        position = 0;
                        break;
                    case R.id.radio_user:
                        position = 1;
                        break;
                }
                if (nowFragIndex != position) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frag_position, frags.get(position)).commit();
                    nowFragIndex = position;
                }
            }
        });
    }
}