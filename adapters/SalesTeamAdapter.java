package com.zinier.entel.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.zinier.base.ui.fragments.SearchableRVAdapter;
import com.zinier.entel.R;
import com.zinier.entel.mvp.models.goals.DashboardTeamList;
import com.zinier.entel.mvp.models.goals.DashboardUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zinier.entel.ui.fragment.dashboard.DashboardTeamFragment.TYPE_HOME;
import static com.zinier.entel.ui.fragment.dashboard.DashboardTeamFragment.TYPE_MOBILE;

/**
 * Created by janaperhun on 19.04.17.
 */

public class SalesTeamAdapter extends SearchableRVAdapter<RecyclerView.ViewHolder> {

    private final int HEADER = 0;
    private final int NORMAL = 1;


    ArrayList<DashboardUser> homeSales;
    ArrayList<DashboardUser> filteredHomeSales;
    ArrayList<DashboardUser> mobileSales;
    ArrayList<DashboardUser> filteredMobileSales;

    private int salesType = TYPE_MOBILE;

    public SalesTeamAdapter(DashboardTeamList users) {
        this.homeSales = users.getUsers().getHomeSales();
        this.filteredHomeSales = users.getUsers().getHomeSales();
        this.mobileSales = users.getUsers().getMobileSales();
        this.filteredMobileSales = users.getUsers().getMobileSales();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;

        if (viewType == HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_team_dashbord, parent, false);
            vh = new VHHeader(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_dashbord, parent, false);
            vh = new VHNormal(v);
        }

        return vh;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position == HEADER) {
            final VHHeader vhHeader = ((VHHeader) holder);
            vhHeader.btnMobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (ToggleButton btn : vhHeader.btns) {
                        btn.setChecked(btn.getId() == v.getId());
                        changeSalesType(TYPE_MOBILE);
                    }
                }
            });
            vhHeader.btnHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (ToggleButton btn : vhHeader.btns) {
                        btn.setChecked(btn.getId() == v.getId());
                        changeSalesType(TYPE_HOME);
                    }
                }
            });
        } else {
            final DashboardUser user;//header
            VHNormal vh = ((VHNormal) holder);
            if (salesType == TYPE_HOME) {
                user = filteredHomeSales.get(position - 1);
                vh.tvSalesCount.setText(String.valueOf(user.getHomeSales()));
            } else {
                user = filteredMobileSales.get(position - 1);
                vh.tvSalesCount.setText(String.valueOf(user.getMobileSales()));
            }

            vh.tvProfileName.setText(user.getName());
            vh.tvNumber.setText(String.valueOf(position));
            String url = user.getAvatarUrl();
            if (!TextUtils.isEmpty(url)) {
                Glide.with(vh.itemView.getContext()).load(url).into(vh.ivProfilePhoto);
            }

            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUserClick(position - 1, user);
                }
            });
        }
    }

    public void onUserClick(int pos, DashboardUser user) {

    }

    public void changeSalesType(int newType) {
        this.salesType = newType;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (salesType == TYPE_HOME) {
            return filteredHomeSales.size() + 1;
        } else {
            return filteredMobileSales.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return HEADER;
        } else {
            return NORMAL;
        }
    }

    public class VHHeader extends RecyclerView.ViewHolder {

        @BindView(R.id.btnMobile)
        ToggleButton btnMobile;
        @BindView(R.id.btnHome)
        ToggleButton btnHome;

        ArrayList<ToggleButton> btns = new ArrayList<>();

        public VHHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btns.add(btnMobile);
            btns.add(btnHome);
        }
    }

    public class VHNormal extends RecyclerView.ViewHolder {

        @BindView(R.id.tvNumberTitle) TextView tvNumber;
        @BindView(R.id.tvProfileName) TextView tvProfileName;
        @BindView(R.id.tvSalesCount) TextView tvSalesCount;
        @BindView(R.id.ivProfilePhoto) ImageView ivProfilePhoto;
//        @BindView(R.id.ivIndicator) ImageView ivIndicator;

        public VHNormal(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null && constraint.length() > 0) {
                    ArrayList<DashboardUser> tempList = new ArrayList<>();


                    if (salesType == TYPE_HOME) {
                        // search content in friend list
                        for (DashboardUser user : homeSales) {
                            if (user.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                tempList.add(user);
                            }
                        }
                    } else {
                        for (DashboardUser user : mobileSales) {
                            if (user.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                tempList.add(user);
                            }
                        }
                    }

                    filterResults.count = tempList.size();
                    filterResults.values = tempList;
                } else {

                    if (salesType == TYPE_HOME) {
                        filterResults.count = homeSales.size();
                        filterResults.values = homeSales;
                    } else {
                        filterResults.count = mobileSales.size();
                        filterResults.values = mobileSales;
                    }


                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (salesType == TYPE_HOME) {
                    filteredHomeSales = (ArrayList<DashboardUser>) results.values;
                } else {
                    filteredMobileSales = (ArrayList<DashboardUser>) results.values;
                }
                notifyDataSetChanged();
            }
        };

    }
}
