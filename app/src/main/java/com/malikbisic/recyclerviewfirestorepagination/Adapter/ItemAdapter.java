package com.malikbisic.recyclerviewfirestorepagination.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.recyclerviewfirestorepagination.Model.Item;
import com.malikbisic.recyclerviewfirestorepagination.OnLoadMoreListener;
import com.malikbisic.recyclerviewfirestorepagination.R;
import com.malikbisic.recyclerviewfirestorepagination.ViewHolder.ItemViewHolder;
import com.malikbisic.recyclerviewfirestorepagination.ViewHolder.ProgressBarViewHolder;

import java.util.List;

/**
 * Created by korisnik on 15/12/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter {

    List<Item> itemList;
    Activity activity;
    Context ctx;
    LayoutInflater mLInflater;

    private static final int ITEM_VIEW = 0;
    private static final int LOADING_VIEW = 1;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    private OnLoadMoreListener onLoadMoreListener;
    boolean isLoading;
    boolean isAllLoaded = false;


    public ItemAdapter(List<Item> itemList, Activity activity, Context ctx, RecyclerView recyclerView) {
        this.itemList = itemList;
        this.activity = activity;
        this.ctx = ctx;

        mLInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();

                    lastVisibleItem = llm.findLastVisibleItemPosition();
                    totalItemCount = llm.getItemCount();

                    Log.i("isLoading", String.valueOf(isLoading));
                    Log.i("isAllLoaded", String.valueOf(isAllLoaded));
                    Log.i("isLast", String.valueOf(lastVisibleItem));


                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && !isAllLoaded) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }

                }
            });


        }
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position) != null ? ITEM_VIEW : LOADING_VIEW;
    }

    public void setOnLoadMore(OnLoadMoreListener onLoadMore) {
        onLoadMoreListener = onLoadMore;
    }


    public void setIsLoading(boolean param) {
        isLoading = param;
    }

    public void isFullLoaded(boolean param) {
        isAllLoaded = param;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == ITEM_VIEW) {
            View v = mLInflater.inflate(R.layout.item_layout, parent, false);
            return new ItemViewHolder(v);
        } else if (viewType == LOADING_VIEW) {
            View v1 = mLInflater.inflate(R.layout.progressbar_item, parent, false);
            return new ProgressBarViewHolder(v1);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int getViewType = holder.getItemViewType();
        final Item model = itemList.get(position);

        if (getViewType == ITEM_VIEW) {

            ((ItemViewHolder) holder).title.setText(model.getTitle());
            ((ItemViewHolder) holder).desc.setText(model.getDesc());
        } else if (getViewType == LOADING_VIEW) {

            if (isLoading && !isAllLoaded) {

                ((ProgressBarViewHolder) holder).progressBar.setVisibility(
                        View.VISIBLE);
                ((ProgressBarViewHolder) holder).progressBar.setIndeterminate(true);

            } else if (!isLoading || isAllLoaded) {

                ((ProgressBarViewHolder) holder).progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
