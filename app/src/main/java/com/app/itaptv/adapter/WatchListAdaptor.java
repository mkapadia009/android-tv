package com.app.itaptv.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.app.itaptv.R;
import com.app.itaptv.activity.BuyDetailActivity;
import com.app.itaptv.activity.MyWatchList;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WatchListAdaptor extends RecyclerView.Adapter<WatchListAdaptor.WatchListHolder> {
    public static String CONTENT_DATA = "contentData";
    public static int REQUEST_CODE = 2;
    public static String KEY_ACTION_TYPE = "actionType";
    public static String KEY_CONTENT_DATA = "contentData";
    public static final String VIEW_PURCHASES = "viewPurchases";
    public static final String PLAY_NOW = "playNow";
    AlertDialog alertDialog;
    private ArrayList itemList = null;
    private Context mContext = null;

    public WatchListAdaptor(ArrayList list, Context context) {
        itemList = list;
        mContext = context;
    }

    public void updateWatchListAdaptor(ArrayList list) {
        Log.e("avinash", "updateWatchListAdaptor");
        itemList = list;
    }

    @NonNull
    @Override
    public WatchListAdaptor.WatchListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.row_watchlist_item, viewGroup, false);
        return new WatchListAdaptor.WatchListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchListAdaptor.WatchListHolder holder, int i) {

        FeedContentData item = (FeedContentData) itemList.get(i);
        holder.txtTitle.setText(item.postTitle);

        String imageURL = "";
        imageURL = item.imgUrl;
        Log.e("avinash", "Duration = " + item.duration);
        if (!(imageURL.trim().isEmpty())) {
            Picasso.get().load(item.imgUrl).into(holder.imageView);
        }
        if (item.duration == null || item.duration.isEmpty()) {
            holder.txtDuration.setVisibility(View.INVISIBLE);
        } else {
            holder.txtDuration.setText(item.duration);
        }
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class WatchListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView txtTitle;
        TextView txtDuration;

        public WatchListHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivImage);
            txtTitle = itemView.findViewById(R.id.tvStoreName);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            itemView.setOnClickListener(this);
            Utility.textFocusListener(itemView);
        }

        public void onClick(View view) {
            int position = getLayoutPosition(); // gets item position
            handleBuyNow(position);
        }

        private void handleBuyNow(int itemPosition) {
            Log.e("avinash", "handleBuyNow");
            FeedContentData feedContentData = (FeedContentData) itemList.get(itemPosition);
            Log.e("avinash", "feedContentData.canIUse = " + feedContentData.canIUse);
            Log.e("avinash", "feedContentData.postExcerpt = " + feedContentData.postExcerpt);
            if (feedContentData.postExcerpt.equalsIgnoreCase("paid") && feedContentData.canIUse == false) {
                showBuyDialog(itemPosition);
            } else {
                playNow(itemPosition);
            }
        }

        private void playNow(int position) {
            Log.e("avinash", "playNow");
            FeedContentData feedContentData = (FeedContentData) itemList.get(position);
            Intent returnIntent = new Intent();
            returnIntent.putExtra(KEY_ACTION_TYPE, PLAY_NOW);
            returnIntent.putExtra(KEY_CONTENT_DATA, feedContentData);
            ((MyWatchList) mContext).setResult(Activity.RESULT_OK, returnIntent);
            ((MyWatchList) mContext).finish();
        }

        private void buyNow(int itemPosition) {
            Log.e("avinash", "buyNow");
            FeedContentData feedContentData = (FeedContentData) itemList.get(itemPosition);
            Intent intent = new Intent(mContext, BuyDetailActivity.class);
            intent.putExtra(MyWatchList.CONTENT_DATA, feedContentData);
            ((MyWatchList) mContext).startActivityForResult(intent, MyWatchList.REQUEST_CODE);

        }

        public void showBuyDialog(int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setView(R.layout.dialog_custom);
            alertDialog = builder.create();
            alertDialog.setCancelable(true);
            alertDialog.show();

            TextView tvTitle = alertDialog.findViewById(R.id.tvStoreName);
            TextView btPositive = alertDialog.findViewById(R.id.btPositive);
            TextView btNegative = alertDialog.findViewById(R.id.btNegative);
            TextView btNeutral = alertDialog.findViewById(R.id.btNeutral);
            tvTitle.setText(R.string.msg_item_expired);
            btPositive.setText(R.string.buy_now);
            btNeutral.setText(R.string.ok);
            btPositive.setVisibility(View.VISIBLE);
            btNeutral.setVisibility(View.VISIBLE);

            btNeutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            btPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    buyNow(position);
                }
            });
        }
    }
}
