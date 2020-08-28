package com.someone.someone_v2.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import com.someone.someone_v2.R;
import com.someone.someone_v2.model.Model;

import java.util.ArrayList;

public class DashboardItemListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Model> models;

    public DashboardItemListAdapter(Context context, ArrayList<Model> models) {
        this.context = context;
        this.models = models;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView=View.inflate(context, R.layout.items,null);
        }

        ImageButton images=(ImageButton) convertView.findViewById(R.id.menuImage);
        Button title=(Button) convertView.findViewById(R.id.menuButton);
        Model model=models.get(position);
        images.setImageResource(model.getImageSrc());

        title.setText(model.getItem_name());
        return convertView;
    }
}
