package com.singtel.community.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.singtel.community.R;
import com.zinier.base.data.community.Member;
import com.zinier.base.ui.activities.MerlinActivity;
import com.zinier.base.utils.SystemUtils;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by janaperhun on 07.07.17.
 */

public class MemberActivity extends MerlinActivity {

    public static final String MEMBER = "member";

    @BindView(R.id.btnSend) TextView btnSend;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvJobTitle) TextView tvJobTitle;
    @BindView(R.id.ivIcon) ImageView ivIcon;

    private Member member;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        bindUI(this);
        if (savedInstanceState == null) {
            member = Parcels.unwrap(getIntent().getExtras().getParcelable(MEMBER));
        } else {
            member = Parcels.unwrap(savedInstanceState.getParcelable(MEMBER));
        }
        initUI();
    }

    private void initUI() {

        btnClose = findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(R.animator.no_change, R.animator.slide_out_right);
                }
            });
        }
        setToolBarText(getString(R.string.members));
        btnSend.setText(member.getEmail());
        tvName.setText(member.getTitle());
        tvJobTitle.setText(member.getJobTitle());
        showImage(member.getPictureUrl(), ivIcon);
    }

    @OnClick(R.id.btnSend)
    public void btnSend(View v) {
        if (!TextUtils.isEmpty(member.getEmail())) {
            SystemUtils.composeEmail(member.getEmail(), this);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MEMBER, Parcels.wrap(member));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.no_change, R.animator.slide_out_right);
    }
}
