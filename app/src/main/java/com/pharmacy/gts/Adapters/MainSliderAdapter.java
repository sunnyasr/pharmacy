package com.pharmacy.gts.Adapters;

import android.content.Context;
import com.pharmacy.gts.App.APIConfig;
import java.util.ArrayList;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainSliderAdapter extends SliderAdapter {
    private Context context;
    private ArrayList<String> arrayList;


    public MainSliderAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {


        viewHolder.bindImageSlide(APIConfig.BASE_IMAGE + "uploads/" + arrayList.get(position));

//        switch (position) {
//            case 0:
//                viewHolder.bindImageSlide("https://cashselfie.com/assets/images/slider4.png");
//                break;
//            case 1:
//                viewHolder.bindImageSlide("https://cashselfie.com/assets/images/slider1.jpg");
//                break;
//            case 2:
//                viewHolder.bindImageSlide("https://cashselfie.com/assets/images/slider2.jpg");
//                break;
//        }
    }

}
