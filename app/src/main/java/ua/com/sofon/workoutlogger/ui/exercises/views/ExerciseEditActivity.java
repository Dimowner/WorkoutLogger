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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import timber.log.Timber;
import ua.com.sofon.workoutlogger.R;
import ua.com.sofon.workoutlogger.WLApplication;
import ua.com.sofon.workoutlogger.components.MultiSelectTextView;
import ua.com.sofon.workoutlogger.dagger.exercises.ExerciseEditModule;
import ua.com.sofon.workoutlogger.ui.exercises.models.ExerciseDataModel;
import ua.com.sofon.workoutlogger.ui.exercises.presenter.IExerciseEditPresenter;

/**
 * Created on 31.03.2017.
 * @author Dimowner
 */
public class ExerciseEditActivity extends AppCompatActivity implements IExerciseEditView {

	@Inject
	IExerciseEditPresenter iExerciseEditPresenter;

	private Realm realm;

	@BindView(R.id.exe_edit_image) ImageView ivImage;
	@BindView(R.id.exe_edit_muscle_groups) MultiSelectTextView txtMuscleGroups;
	@BindView(R.id.exe_edit_name) EditText txtName;
	@BindView(R.id.exe_edit_description) EditText txtDescription;

	private static final long ID_UNKNOWN = -1;
	private long id = ID_UNKNOWN;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise_edit);
		WLApplication.get(getApplicationContext()).applicationComponent()
				.plus(new ExerciseEditModule()).injectExeEdit(this);
		realm = Realm.getDefaultInstance();
		ButterKnife.bind(this);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		initMuscleGroupsView();

		id = getIntent().getLongExtra(ExercisesActivity.EXTRAS_KEY_EXERCISE_ID, ID_UNKNOWN);
		iExerciseEditPresenter.bindView(this);
		if (id != ID_UNKNOWN) {
			iExerciseEditPresenter.loadExerciseData(id);
		}
	}

	private void initMuscleGroupsView() {
		//Init muscle groups dialog.
		String[] names = getResources().getStringArray(R.array.exercises_types_array);
		int[] ids = getResources().getIntArray(R.array.exercises_num_types_array);
		txtMuscleGroups.setData(names, ids);
		txtMuscleGroups.setTitle(getString(R.string.title_muscle_groups));
		txtMuscleGroups.showNeutralButton(true);
		txtMuscleGroups.setOnItemsSelectedListener(new MultiSelectTextView.OnItemsSelectedListener() {
			@Override
			public void OnItemsSelected(int[] ids, String[] names) {
//				mExercise.setGroups(ids);
//				TODO: update groups
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.exercise_edit_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.action_add_youtube_video:
//				TODO: Start youtube app for choose exercise video;
				break;
			case R.id.action_accept:
//				TODO: Accept changes
				if (id == ID_UNKNOWN) {
					iExerciseEditPresenter.addExercise(getExerciseData());
				} else {
					iExerciseEditPresenter.updateExercise(getExerciseData());
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		iExerciseEditPresenter.unbindView();
		realm.close();
	}

	@Override
	public void showProgress() {

	}

	@Override
	public void hideProgress() {

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
	public void exerciseUpdated() {
		finish();
	}

	@Override
	public void exerciseAdded() {
		finish();
	}

	private ExerciseDataModel getExerciseData() {
		return new ExerciseDataModel(
				id,
				txtMuscleGroups.getSelectedIds()[0],
				txtName.getText().toString(),
				txtDescription.getText().toString(),
				null,
				null,
				false);
	}
}
