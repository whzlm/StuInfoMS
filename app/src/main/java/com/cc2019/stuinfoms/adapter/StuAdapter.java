package com.cc2019.stuinfoms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cc2019.stuinfoms.R;
import com.cc2019.stuinfoms.entry.Stu;

import java.util.List;

public class StuAdapter extends RecyclerView.Adapter {
    private List<Stu> stus;
    private Context context;


    public StuAdapter(List<Stu> stus, Context context){
        this.stus = stus;
        this.context = context;
    }

    class StuHolder extends RecyclerView.ViewHolder{
        TextView textStuId;
        TextView textStuName;

        public StuHolder(@NonNull View itemView) {
            super(itemView);
            textStuId = itemView.findViewById(R.id.stu_id);
            textStuName = itemView.findViewById(R.id.stu_name);
        }
        public void setStuData(int position){
            Stu stu = stus.get(position);
            textStuId.setText(stu.getId()+"");
            textStuName.setText(stu.getName());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_stu_item,parent,false);
        StuHolder stuHolder = new StuHolder(view);
        return stuHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StuHolder stuHolder = (StuHolder) holder;
        stuHolder.setStuData(position);
    }

    @Override
    public int getItemCount() {
        return stus.size();
    }


    public interface OnClikStuItem{
        public void onclikitem(Stu stu);
    }

    public OnClikStuItem onClikStuItem;

    public void setOnClikStuItem(OnClikStuItem onClikStuItem){
        this.onClikStuItem = onClikStuItem;
    }

}
