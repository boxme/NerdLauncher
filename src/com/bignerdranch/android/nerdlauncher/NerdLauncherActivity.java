package com.bignerdranch.android.nerdlauncher;

import android.support.v4.app.Fragment;


public class NerdLauncherActivity extends SingleFragmentActivity {
	@Override
	public Fragment getFragment() {
		return new NerdLauncherFragment();
	}
}