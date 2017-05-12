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

package ua.com.sofon.workoutlogger.ui.exercises.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import ua.com.sofon.workoutlogger.R;
import ua.com.sofon.workoutlogger.ui.exercises.models.ExerciseDataModel;
import ua.com.sofon.workoutlogger.ui.main.view.BaseActivity;

/**
 * Created on 07.03.2017.
 * @author Dimowner
 */
public class ExercisesActivity extends BaseActivity {

	public static final String EXTRAS_KEY_EXERCISE_ID = "exercise_id";

	public static final int REQ_CODE_ADD_EXE = 1;

	private ExercisesFragment exercisesFragment;
	private ExercisesFragment fevExercisesFragment;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercises);

		if (savedInstanceState == null) {
			initFragments();
			initViewpager();
		}
	}

	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_EXERCISES;
	}

	private void initFragments() {
		exercisesFragment = ExercisesFragment.newInstance(ExercisesFragment.VIEW_TYPE_ALL_EXERCISES);
		fevExercisesFragment = ExercisesFragment.newInstance(ExercisesFragment.VIEW_TYPE_FEV_EXERCISES);

		//Listeners needs to update fragment state when another fragment changes his state.
		exercisesFragment.setOnListItemUpdateListener(id -> fevExercisesFragment.buildList());
		fevExercisesFragment.setOnListItemUpdateListener(id -> exercisesFragment.updateListItem(id));
	}

	private void initViewpager() {
		ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
		if (viewPager != null) {
			CustomTabsAdapter adapter = new CustomTabsAdapter(getSupportFragmentManager());
			adapter.addFragment(exercisesFragment, getString(R.string.exe_activity_all_exercises));
			adapter.addFragment(fevExercisesFragment, getString(R.string.exe_activity_favorites));
			viewPager.setAdapter(adapter);
			TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
			if (tabLayout != null) {
				tabLayout.setupWithViewPager(viewPager);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("exercises_fragment_tag", exercisesFragment.getTag());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		exercisesFragment = (ExercisesFragment) getSupportFragmentManager()
				.findFragmentByTag(savedInstanceState.getString("exercises_fragment_tag"));
		initViewpager();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.exercises_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add_exercise:
				startActivityForResult(new Intent(getApplicationContext(), ExerciseEditActivity.class), REQ_CODE_ADD_EXE);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQ_CODE_ADD_EXE:
					ExerciseDataModel dataModel = data.getParcelableExtra("result");
					exercisesFragment.addItemToList(dataModel);
					break;
			}
		}
	}

	/**
	 * Pager adapter implementation with fragments
	 */
	private static class CustomTabsAdapter extends FragmentPagerAdapter {

		CustomTabsAdapter(FragmentManager fm) {
			super(fm);
		}

		void addFragment(Fragment fragment, String title) {
			mFragments.add(fragment);
			mFragmentTitles.add(title);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitles.get(position);
		}


		private final List<Fragment> mFragments = new ArrayList<>();
		private final List<String> mFragmentTitles = new ArrayList<>();
	}
}
