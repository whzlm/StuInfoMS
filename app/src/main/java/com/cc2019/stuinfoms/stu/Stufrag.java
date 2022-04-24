package com.cc2019.stuinfoms.stu;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cc2019.stuinfoms.R;
import com.cc2019.stuinfoms.adapter.StuAdapter;
import com.cc2019.stuinfoms.entry.Stu;
import com.ejlchina.okhttps.OkHttps;

import java.util.ArrayList;
import java.util.List;

public class Stufrag extends Fragment {

    private static final int TRUE = 1;
    private static final int FALSE = 0;

    private StuAdapter stuAdapter;
    private RecyclerView recyclerView;
    private Context context;

    public void getStuData(int flag){
        if (flag == TRUE){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //联网请求
                List<Stu> stuList = OkHttps.sync("http://10.0.2.2:8080/android/findByPage?pageNow=1&pageSize=4")
                    .bind(context).get().getBody().toList(Stu.class);
//                    System.out.println(stuList);

//                String str = OkHttps.sync("http://10.0.2.2:8080/android/findByPage?pageNow=1&pageSize=4")
//                        .bind(context).get().getBody().toString();
//                    System.out.println(str);

                    Message message = Message.obtain();
                    if (stuList != null){
                        message.what = TRUE;
                        message.obj = stuList;
                    }else {
                    message.what = FALSE;
                    }
                    handler.sendMessage(message);
                }
            }).start();
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case TRUE:
                    List<Stu> stuList = (List<Stu>)msg.obj;
                    System.out.println(stuList);
                    stuAdapter = new StuAdapter(stuList,context);
                    recyclerView.setAdapter(stuAdapter);
                    break;
                case FALSE:
                    Toast.makeText(context,"联网请求失败！",Toast.LENGTH_SHORT);
                    break;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stufrag, container, false);
        //找到recycleview
        recyclerView = view.findViewById(R.id.rv_stuinfo);
        context = this.getContext();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getStuData(TRUE);
//        stuAdapter = new StuAdapter(stuList,context);
//        recyclerView.setAdapter(stuAdapter);
        //这里可设置布局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,1);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

}