package com.malikbisic.recyclerviewfirestorepagination.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.malikbisic.recyclerviewfirestorepagination.R;

/**
 * Created by korisnik on 15/12/2017.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {


    public TextView title;
    public TextView desc;

    public ItemViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.titleItem);
        desc = (TextView) itemView.findViewById(R.id.descItem);
    }
}
