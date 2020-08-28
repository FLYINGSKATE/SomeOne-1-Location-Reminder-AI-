package com.someone.someone_v2.model;

import com.someone.someone_v2.R;

import java.util.ArrayList;

public class DashBoardListDetails {
    public static ArrayList<Model> getItemList(){
        ArrayList<Model> itemList=new ArrayList<>();
        itemList.add(new Model("Profile",R.drawable.profileb));
        itemList.add(new Model("Add Locations",R.drawable.addlocclicked));
        itemList.add(new Model("Today List",R.drawable.diary));
        itemList.add(new Model("Counter",R.drawable.counter));
        return itemList;
    }
}
