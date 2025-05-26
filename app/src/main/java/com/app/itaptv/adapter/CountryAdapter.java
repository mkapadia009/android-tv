package com.app.itaptv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.CountryData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class CountryAdapter extends BaseAdapter {
    private Context context;
    private List<CountryData> countryDataList;

    public CountryAdapter(Context context, List<CountryData> countryDataList) {
        this.context = context;
        this.countryDataList = countryDataList;
    }

    @Override
    public int getCount() {
        return countryDataList != null ? countryDataList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView= LayoutInflater.from(context).inflate(R.layout.item_country,parent,false);


        ImageView ivCountryFlag=rootView.findViewById(R.id.ivCountryFlag);
        TextView tvCountryCode=rootView.findViewById(R.id.tvCountryCode);

        Glide.with(context)
                .load(countryDataList.get(position).countryImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivCountryFlag);

        tvCountryCode.setText(countryDataList.get(position).countryCode);

        return rootView;
    }
}
