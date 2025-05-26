package com.app.itaptv.fragment;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.holder.CouponsHolder;
import com.app.itaptv.structure.CouponsData;
import com.app.itaptv.structure.vouchers.AllCoupons;
import com.app.itaptv.structure.vouchers.AllCouponsFeedsData;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class AllCouponsTabFragment extends Fragment {

    //UI
    ProgressBar progressBar;
    RecyclerView rvCoupon;
    //Variables
    View view;
    ArrayList<CouponsData> couponsDataArrayList = new ArrayList<>();
    AllCoupons allCoupons;
    public ArrayList<AllCouponsFeedsData> feedsDataArrayList = new ArrayList<>();
    public ArrayList<AllCouponsFeedsData> arrayListPageWiseData = new ArrayList<>();

    String GET_ALL_COUPONS_DATA = "";
    KRecyclerViewAdapter adapterCoupons;
    // Pagination
    private static final int GRID_SPAN = 1;
    private boolean loading = false;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 2000;
    protected boolean customLoadingListItem = false;
    int nextPageNo = 1;
    int currentIndex = 0;

    public AllCouponsTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_all_coupons, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        setCouponRecyclerView();
        initializeContentPagination();
        return view;
    }

    private void setCouponRecyclerView() {
        rvCoupon = view.findViewById(R.id.rvAllCoupons);
        rvCoupon.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        adapterCoupons = new KRecyclerViewAdapter(getActivity(), feedsDataArrayList, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_coupons, viewGroup, false);
                return new CouponsHolder(layoutView);
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {

            }
        });

        rvCoupon.setAdapter(adapterCoupons);
        adapterCoupons.notifyDataSetChanged();
    }

    /************************ PAGINATION METHODS -- START **********************/

    public void initializeContentPagination() {
        customLoadingListItem = false;
        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(rvCoupon, callbacks)
                .setLoadingTriggerThreshold(0)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(customLoadingListItem ? new CustomLoadingListItemCreator() : null)
                /* .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                     @Override
                     public int getSpanSize() {
                         return GRID_SPAN;
                     }
                 })*/
                .build();
    }

    Paginate.Callbacks callbacks = new Paginate.Callbacks() {
        @Override
        public void onLoadMore() {
            // Load next page of data (e.g. network or database)
            loading = true;
            handler.postDelayed(fakeCallback, networkDelay);

        }

        @Override
        public boolean isLoading() {
            // Indicate whether new page loading is in progress or not
            return loading; // Return boolean weather data is already loading or not
        }

        @Override
        public boolean hasLoadedAllItems() {
            // Indicate whether all data (pages) are loaded or not
            return nextPageNo == 0; // If all pages are loaded return true
        }
    };

    private Runnable fakeCallback = new Runnable() {
        @Override
        public void run() {
            if (nextPageNo != 0) {
                getAllCouponsData();
            }

        }
    };

    private class CustomLoadingListItemCreator implements LoadingListItemCreator {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VH vh = (VH) holder;
            vh.tvLoading.setText(String.format(getString(R.string.total_items_loaded), adapterCoupons.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (rvCoupon.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) vh.itemView.getLayoutParams();
                params.setFullSpan(true);
            }
        }

    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvLoading;

        public VH(View itemView) {
            super(itemView);
            tvLoading = itemView.findViewById(R.id.tv_loading_text);
        }

    }

    /************************ PAGINATION METHODS -- END **********************/

    private void getAllCouponsData() {
        String url = Url.GET_ALL_COUPONS_DATA + "&f_page_no=" + nextPageNo;
        APIRequest apiRequest = new APIRequest(url, Request.Method.GET, null, null, getActivity());
        APIManager.request(apiRequest, new APIResponse() {
            @Override
            public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                progressBar.setVisibility(View.GONE);
                if (response != null) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.has("msg")) {
                            // currentIndex = currentIndex + arrayListPageWiseData.size();
                            allCoupons = new AllCoupons(object.getJSONObject("msg"));
                            JSONObject jsonObjectMessage = object.getJSONObject("msg");
                            nextPageNo = jsonObjectMessage.has("next_page") ? jsonObjectMessage.getInt("next_page") : 0;
                            feedsDataArrayList.addAll(adapterCoupons.getItemCount(), allCoupons.feedsDataArrayList);
                        }
                        adapterCoupons.notifyDataSetChanged();
                        loading = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

}
