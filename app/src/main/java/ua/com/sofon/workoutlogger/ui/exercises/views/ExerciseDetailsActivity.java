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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import timber.log.Timber;
import ua.com.sofon.workoutlogger.R;
import ua.com.sofon.workoutlogger.WLApplication;
import ua.com.sofon.workoutlogger.dagger.exercises.ExerciseDetailsModule;
import ua.com.sofon.workoutlogger.ui.exercises.presenter.IExerciseDetailsPresenter;
import ua.com.sofon.workoutlogger.util.UIUtil;

/**
 * Created on 27.03.2017.
 * @author Dimowner
 */
public class ExerciseDetailsActivity extends AppCompatActivity implements IExerciseDetailsView {

	public static final int REQ_CODE_EDIT_EXERCISE = 5;

	public static final String EXTRAS_KEY_ACTION = "action";
	public static final String EXTRAS_KEY_EXERCISE_ID = "exercise_id";

	public static final int ACTION_UNKNOWN = 0;
	public static final int ACTION_UPDATED = 1;
	public static final int ACTION_DELETED = 2;
	public static final int ACTION_ADDED_TO_FAVORITES = 3;
	public static final int ACTION_REMOVED_FROM_FAVORITES = 4;

	@Inject
	IExerciseDetailsPresenter iExerciseDetailsPresenter;

	private Realm realm;

	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.fab) FloatingActionButton fab;

	@BindView(R.id.exe_details_image) ImageView ivImage;
	@BindView(R.id.exe_details_groups) TextView txtMuscleGroups;
	@BindView(R.id.exe_details_name) TextView txtName;
	@BindView(R.id.exe_details_description) TextView txtDescription;
	@BindView(R.id.toolbar_progress) ProgressBar progressBar;

	private static final long ID_UNKNOWN = -1;
	private long id = ID_UNKNOWN;

	private int action = ACTION_UNKNOWN;

	private boolean isFavorite = false;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise_details);
		WLApplication.get(getApplicationContext()).applicationComponent()
				.plus(new ExerciseDetailsModule()).injectExeDetails(this);

		realm = Realm.getDefaultInstance();
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		showFab();

		iExerciseDetailsPresenter.bindView(this);
		id = getIntent().getLongExtra(ExercisesActivity.EXTRAS_KEY_EXERCISE_ID, ID_UNKNOWN);
		iExerciseDetailsPresenter.bindView(this);
		if (id != ID_UNKNOWN) {
			iExerciseDetailsPresenter.loadExerciseData(id);
		}
	}

	@OnClick(R.id.fab)
	void addExeIntoDatabase(View view) {
//		realm.executeTransactionAsync(new Realm.Transaction() {
//			@Override
//			public void execute(Realm realm) {
//				Exercise exercise = realm.createObject(Exercise.class);
//				exercise.setId(1);
//				exercise.setName("Back push");
//				exercise.setDescription("121213 Back push, Back push, Back push");
//				exercise.setGroup(3);
//				exercise.setFavorite(true);
//			}
//		}, new Realm.Transaction.OnSuccess() {
//			@Override
//			public void onSuccess() {
//				Log.v("Details", "onSuccess!!");
//			}
//		}, new Realm.Transaction.OnError() {
//			@Override
//			public void onError(Throwable error) {
//				Log.e("Details", "onError!!", error);
//			}
//		});
	}

	//TODO: move to animation Utils
	void showFab() {
		fab.setAlpha(0f);
		fab.setScaleX(0f);
		fab.setScaleY(0f);
		fab.setTranslationY(fab.getHeight() / 2);
		fab.animate()
				.alpha(1f)
				.scaleX(1f)
				.scaleY(1f)
				.translationY(0f)
				.setDuration(350L)
				.setInterpolator(AnimationUtils.loadInterpolator(getApplicationContext(),
						android.R.interpolator.accelerate_decelerate))
				.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.exercise_details_menu, menu);
		if (id == ID_UNKNOWN) {
			menu.findItem(R.id.action_add_to_fev).setVisible(false);
			menu.findItem(R.id.action_youtube_play).setVisible(false);
			menu.findItem(R.id.action_edit).setVisible(false);
			menu.findItem(R.id.action_delete).setVisible(false);
		}
		if (isFavorite) {
			menu.findItem(R.id.action_add_to_fev).setIcon(R.drawable.star_white);
		} else {
			menu.findItem(R.id.action_add_to_fev).setIcon(R.drawable.star_outline_white);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
			case android.R.id.home:
				intent = new Intent();
				intent.putExtra(EXTRAS_KEY_ACTION, action);
				setResult(RESULT_CANCELED, intent);
				finish();
				break;
			case R.id.action_add_to_fev:
				iExerciseDetailsPresenter.reverseFavorite(id);
				break;
			case R.id.action_youtube_play:
				break;
			case R.id.action_edit:
				intent = new Intent(getApplicationContext(), ExerciseEditActivity.class);
				intent.putExtra(ExercisesActivity.EXTRAS_KEY_EXERCISE_ID, id);
				startActivityForResult(intent, REQ_CODE_EDIT_EXERCISE);
				break;
			case R.id.action_delete:
				UIUtil.showWarningDialog(this, R.string.exer_delete_this_exe,
						(dialog, which) -> iExerciseDetailsPresenter.clickDeleteExercise(id),
						(dialog, which) -> dialog.cancel()
				);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		realm.close();
		iExerciseDetailsPresenter.unbindView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Timber.v("onActivityResult req = " + requestCode + " res = " + requestCode);
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK
				&& requestCode == REQ_CODE_EDIT_EXERCISE
				&& id != ID_UNKNOWN) {
			Timber.v("LoadExerciseData id = " + id);
			action = ACTION_UPDATED;
			iExerciseDetailsPresenter.loadExerciseData(id);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Timber.v("onBackPressed");
		Intent intent = new Intent();
		intent.putExtra(EXTRAS_KEY_ACTION, action);
		setResult(RESULT_CANCELED, intent);
	}

	@Override
	public void showProgress() {
		Timber.v("ShowProgress");
		progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideProgress() {
		Timber.v("HideProgress");
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void showError() {
		Timber.v("showError");
		toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
		fab.setVisibility(View.GONE);
		ivImage.setVisibility(View.GONE);
		txtName.setVisibility(View.GONE);
		txtMuscleGroups.setVisibility(View.GONE);
		txtDescription.setVisibility(View.GONE);
		findViewById(R.id.exe_details_group_content).setVisibility(View.GONE);

		findViewById(R.id.exe_details_error).setVisibility(View.VISIBLE);
		id = ID_UNKNOWN;
		invalidateOptionsMenu();
	}

	@Override
	public void setImage(String path) {
		Timber.v("Path = " + path);
		Glide.with(getApplicationContext()).load(path).fitCenter().into(ivImage);
	}

	@Override
	public void setName(String name) {
		Timber.v("setName = " + name);
		txtName.setText(name);
	}

	@Override
	public void selectGroup(String group) {
		Timber.v("setGroup = " + group);
//		TODO: fix groups, replays group names by ids
		txtMuscleGroups.setText(group);
	}

	@Override
	public void setDescription(String description) {
		txtDescription.setText(description);
	}

	@Override
	public void setFavorite(boolean fav) {
		isFavorite = fav;
		invalidateOptionsMenu();
	}

	@Override
	public void exerciseDeleted() {
		Timber.v("exerciseDeleted");
		Intent intent = new Intent();
		intent.putExtra(EXTRAS_KEY_ACTION, ACTION_DELETED);
		intent.putExtra(EXTRAS_KEY_EXERCISE_ID, id);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void favoritesUpdated(boolean fav) {
		Timber.v("favoritesUpdated bool = " + fav);
		isFavorite = fav;
		invalidateOptionsMenu();
		if (action == ACTION_UNKNOWN
				|| action == ACTION_ADDED_TO_FAVORITES
				|| action == ACTION_REMOVED_FROM_FAVORITES) {
			action = fav ? ACTION_ADDED_TO_FAVORITES : ACTION_REMOVED_FROM_FAVORITES;
		}
	}
}
