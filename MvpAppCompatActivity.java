package com.zinier.base.ui.activities;

import android.os.Bundle;

import com.arellomobile.mvp.MvpDelegate;

public class MvpAppCompatActivity extends BaseActivity {
	private MvpDelegate<? extends MvpAppCompatActivity> mMvpDelegate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getMvpDelegate().onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();

		getMvpDelegate().onAttach();
	}

	@Override
	protected void onResume() {
		super.onResume();

		getMvpDelegate().onAttach();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		getMvpDelegate().onSaveInstanceState(outState);
		getMvpDelegate().onDetach();
	}

	@Override
	protected void onStop() {
		super.onStop();

		getMvpDelegate().onDetach();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (isFinishing()) {
			getMvpDelegate().onDestroy();
		}
	}

	/**
	 * @return The {@link MvpDelegate} being used by this Activity.
	 */
	public MvpDelegate getMvpDelegate() {
		if (mMvpDelegate == null) {
			mMvpDelegate = new MvpDelegate<>(this);
		}
		return mMvpDelegate;
	}
}