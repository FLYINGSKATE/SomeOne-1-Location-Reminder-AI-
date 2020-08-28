package com.someone.someone_v2.model;

import com.someone.someone_v2.R;

import java.util.ArrayList;

public class JournalListDetails {
    public static ArrayList<Model> getItemList(){
        ArrayList<Model> itemList=new ArrayList<>();
        itemList.add(new Model("Workout List", R.drawable.workout));
        itemList.add(new Model("Today List",R.drawable.diary));
        itemList.add(new Model("Routine List",R.drawable.routine));
        itemList.add(new Model("Bucket List",R.drawable.checklist));
        return itemList;
    }
}
