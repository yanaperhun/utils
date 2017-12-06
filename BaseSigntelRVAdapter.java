package com.singtel.community.ui.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.singtel.community.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by qati on 20.10.16.
 */

public abstract class BaseSigntelRVAdapter<T extends BaseAdapterItem> extends RecyclerView.Adapter<BaseSigntelRVAdapter<T>.ViewHolder> {

    private final ArrayList<T> items;
    private View.OnClickListener clickListener;

    public BaseSigntelRVAdapter(ArrayList<T> items, Context context) {
        this.items = items;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_broadcast, parent, false);
        ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(clickListener);
        return vh;
    }

    public BaseAdapterItem getItem(int pos) {
        return items.get(pos);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvDate.setText(items.get(position).getDate());
        holder.tvTitle.setText(items.get(position).getTitle());
        holder.tvSubtitle.setText(items.get(position).getDescription());
        holder.imageView.setImageDrawable(getIcon(items.get(position).isViewed()));

        if (items.get(position).isViewed()) {
            holder.tvTitle.setTypeface(holder.tvTitle.getTypeface(), Typeface.NORMAL);
            holder.tvSubtitle.setTextColor(holder.tvTitle.getResources().getColor(R.color.colorGreyForHint));
            holder.tvDate.setTextColor(holder.tvTitle.getResources().getColor(R.color.colorGreyForHint));
        } else {
            holder.tvTitle.setTypeface(holder.tvTitle.getTypeface(), Typeface.BOLD);
            holder.tvSubtitle.setTextColor(holder.tvTitle.getResources().getColor(R.color.colorDarkGreyText));
            holder.tvDate.setTextColor(holder.tvTitle.getResources().getColor(R.color.colorDarkGreyText));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivType) ImageView imageView;
        @BindView(R.id.tvBroadcastTitle) TextView tvTitle;
        @BindView(R.id.tvBroadcastCategory) TextView tvSubtitle;
        @BindView(R.id.tvDate) TextView tvDate;
        @BindView(R.id.tvDeadline) TextView tvDeadline;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public void remove(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public abstract Drawable getIcon(boolean isViewed);

}
