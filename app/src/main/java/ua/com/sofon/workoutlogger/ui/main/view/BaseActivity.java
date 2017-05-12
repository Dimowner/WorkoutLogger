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

package ua.com.sofon.workoutlogger.ui.main.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import timber.log.Timber;
import ua.com.sofon.workoutlogger.R;
import ua.com.sofon.workoutlogger.WLApplication;
import ua.com.sofon.workoutlogger.dagger.main.MainModule;
import ua.com.sofon.workoutlogger.ui.exercises.views.ExercisesActivity;
//import ua.com.sofon.workoutlogger.ui.home.views.HomeActivity;
import ua.com.sofon.workoutlogger.ui.home.views.HomeActivity;
import ua.com.sofon.workoutlogger.ui.main.presenter.IMainPresenter;

/**
 * Base activity with base functionality and drawer layout.
 * @author Dimowner
 */
public class BaseActivity extends AppCompatActivity implements IMainView {

	// symbols for navdrawer items (indices must correspond to array below). This is
	// not a list of items that are necessarily *present* in the Nav Drawer; rather,
	// it's a list of all possible items.
	protected static final int NAVDRAWER_ITEM_HOME 			= R.id.nav_home;
	protected static final int NAVDRAWER_ITEM_HISTORY 		= R.id.nav_history;
	protected static final int NAVDRAWER_ITEM_WORKOUTS		= R.id.nav_workouts;
	protected static final int NAVDRAWER_ITEM_EXERCISES	= R.id.nav_exercises;
	protected static final int NAVDRAWER_ITEM_STATISTICS  = R.id.nav_statistics;
	protected static final int NAVDRAWER_ITEM_MESUREMENTS = R.id.nav_measurements;
	protected static final int NAVDRAWER_ITEM_SETTINGS		= R.id.nav_settings;
	protected static final int NAVDRAWER_ITEM_INVALID		= -1;

	// Primary toolbar and drawer toggle
	private Toolbar mActionBarToolbar;

	// Navigation drawer:
	protected DrawerLayout mDrawerLayout;
	protected NavigationView mNavigationView;
	protected ActionBarDrawerToggle mDrawerToggle;

	@Inject
	IMainPresenter iMainPresenter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WLApplication.get(getApplicationContext()).applicationComponent().plus(new MainModule()).inject(this);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setupNavDrawer();
		iMainPresenter.bindView(this);
	}

	@Override
	protected void onDestroy() {
		iMainPresenter.unbindView();
		super.onDestroy();
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		getActionBarToolbar();
	}

	/**
	 * Returns the navigation drawer item that corresponds to this Activity. Subclasses
	 * of BaseActivity override this to indicate what nav drawer item corresponds to them
	 * Return NAVDRAWER_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
	 */
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_INVALID;
	}

	/**
	 * Sets up the navigation drawer as appropriate. Note that the nav drawer will be
	 * different depending on whether the attendee indicated that they are attending the
	 * event on-site vs. attending remotely.
	 */
	protected void setupNavDrawer() {
		// What nav drawer item should be selected?
		int selfItem = getSelfNavDrawerItem();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (mDrawerLayout == null) {
			return;
		}
		mNavigationView = (NavigationView) findViewById(R.id.nav_view);
		if (mNavigationView != null) {
			mNavigationView.setNavigationItemSelectedListener(
					new NavigationView.OnNavigationItemSelectedListener() {
						@Override
						public boolean onNavigationItemSelected(MenuItem menuItem) {
							menuItem.setChecked(true);
							mDrawerLayout.closeDrawers();
							if (getSelfNavDrawerItem() != menuItem.getItemId()) {
								goToNavDrawerItem(menuItem.getItemId());
							}
							return true;
						}
					});
			if (selfItem > NAVDRAWER_ITEM_INVALID) {
				mNavigationView.getMenu().findItem(selfItem).setChecked(true);
			}
		}

		if (mActionBarToolbar != null) {
			mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mDrawerLayout.openDrawer(Gravity.LEFT);
				}
			});

			mDrawerToggle = new ActionBarDrawerToggle(
					this,                  /* host Activity */
					mDrawerLayout,         /* DrawerLayout object */
					mActionBarToolbar,
					R.string.drawer_open,  /* "open drawer" description for accessibility */
					R.string.drawer_closed  /* "close drawer" description for accessibility */
			);

			mDrawerToggle.syncState();
			mDrawerLayout.setDrawerListener(mDrawerToggle);
		}
	}

	protected boolean isNavDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.LEFT);
	}

	protected void closeNavDrawer() {
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(Gravity.LEFT);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mNavigationView != null && getSelfNavDrawerItem() > NAVDRAWER_ITEM_INVALID) {
			mNavigationView.getMenu().findItem(getSelfNavDrawerItem()).setChecked(true);
		}
	}

	@Override
	public void onBackPressed() {
		if (isNavDrawerOpen()) {
			closeNavDrawer();
		} else {
			super.onBackPressed();
		}
	}

	private void goToNavDrawerItem(int itemID) {
		switch (itemID) {
			case NAVDRAWER_ITEM_HOME:
				iMainPresenter.clickToHomeMenuItem();
				break;
			case NAVDRAWER_ITEM_HISTORY:
				break;
			case NAVDRAWER_ITEM_WORKOUTS:
				break;
			case NAVDRAWER_ITEM_EXERCISES:
				iMainPresenter.clickToExercisesMenuItem();
				break;
			case NAVDRAWER_ITEM_STATISTICS:
				break;
			case NAVDRAWER_ITEM_MESUREMENTS:
				break;
			case NAVDRAWER_ITEM_SETTINGS:
				break;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Get toolbar and init if need.
	 */
	protected Toolbar getActionBarToolbar() {
		if (mActionBarToolbar == null) {
			mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
			if (mActionBarToolbar != null) {
				setSupportActionBar(mActionBarToolbar);
			}
		}
		return mActionBarToolbar;
	}

	@Override
	public void startHomeActivity() {
		startActivity(new Intent(this, HomeActivity.class));
		finish();
	}

	@Override
	public void startExercisesActivity() {
		startActivity(new Intent(this, ExercisesActivity.class));
		finish();
	}

	@Override
	public void showProgress() {
		Timber.v("showProgress");
	}

	@Override
	public void hideProgress() {
		Timber.v("hideProgress");
	}
}
