/*
 * Copyright 2017 Dmitriy Ponomarenko, sofon.com.ua
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua.com.sofon.workoutlogger.ui.home.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ua.com.sofon.workoutlogger.R;
import ua.com.sofon.workoutlogger.ui.main.view.BaseActivity;
import ua.com.sofon.workoutlogger.util.AnimationUtil;

/**
 * Created on 29.03.2017.
 *
 * @author Dimowner
 */
public class HomeActivity extends BaseActivity {

	@BindView(R.id.fab) FloatingActionButton fab;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ButterKnife.bind(this);

		AnimationUtil.fabRevealAnimation(fab);
	}

	@OnClick(R.id.fab)
	void onFabClick(View view) {
//		Intent intent = new Intent(Intent.ACTION_SEARCH);
////				, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//		intent.setPackage("com.google.android.youtube");
//		intent.putExtra(SearchManager.QUERY, "Становая тяга");
////		intent.setType("video/*");
////		intent.setAction(Intent.ACTION_GET_CONTENT);
////		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivityForResult(intent, 201);
	}

	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_HOME;
	}
}
