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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
import ua.com.sofon.workoutlogger.util.FileUtil;

/**
 * Created on 31.03.2017.
 * @author Dimowner
 */
public class ExerciseEditActivity extends AppCompatActivity implements IExerciseEditView {

	private static final int REQUEST_CODE_MAKE_PHOTO = 1;

	private static final int REQUEST_WRITE_CAMERA_PERMISSION = 2;

	private static final String FILE_PROVIDER_AUTHORITY = "ua.com.sofon.workoutlogger.fileprovider";

	/** Temp image path */
	String mCurrentPhotoPath;

	@Inject
	IExerciseEditPresenter iExerciseEditPresenter;

	private Realm realm;

	@BindView(R.id.exe_edit_image) ImageView ivImage;
	@BindView(R.id.exe_edit_muscle_groups) MultiSelectTextView txtMuscleGroups;
	@BindView(R.id.exe_edit_name) EditText txtName;
	@BindView(R.id.exe_edit_description) EditText txtDescription;
	@BindView(R.id.toolbar_progress) ProgressBar progressBar;

	private static final long ID_UNKNOWN = -1;
	private long id = ID_UNKNOWN;

	private boolean loadingError = false;

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

		ivImage.setOnClickListener(v -> makePhoto());
	}

	private void makePhoto() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			runCameraActivityLollipopAndBeyond();
		} else{
			runCameraActivityPreLollipop();
		}
	}

	private void runCameraActivityPreLollipop() {
		try {
			// do something for phones running an SDK before lollipop
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			Uri imageUri = Uri.fromFile(FileUtil.createTempImageFile(getApplicationContext()));
			mCurrentPhotoPath = imageUri.getPath();
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, REQUEST_CODE_MAKE_PHOTO);
		} catch (IOException e) {
			Timber.e(e);
		}
	}

	private void runCameraActivityLollipopAndBeyond() {
		if (hasPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
			dispatchTakePictureIntent();
		} else {
			requestWritePermission();
		}
	}

	public boolean hasPermissions(String... permissions) {
		for (String permission : permissions) {
			if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission)
					!= PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	private void dispatchTakePictureIntent() {
		Timber.v("dispatchTakePictureIntent");
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = FileUtil.createTempImageFile(getApplicationContext());
			} catch (IOException ex) {
				Timber.e(ex);
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				mCurrentPhotoPath = photoFile.getPath();
				Uri photoURI = FileProvider.getUriForFile(this,
						FILE_PROVIDER_AUTHORITY,
						photoFile);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				startActivityForResult(takePictureIntent, REQUEST_CODE_MAKE_PHOTO);
			}
		}
	}

	/**
	 * Ask permission write files into file system.
	 */
	private void requestWritePermission() {
		ActivityCompat.requestPermissions(
				ExerciseEditActivity.this,
				new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
				REQUEST_WRITE_CAMERA_PERMISSION
		);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
														@NonNull int[] grantResults) {
		if (requestCode == REQUEST_WRITE_CAMERA_PERMISSION) {
			if (grantResults.length > 0
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED
					&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
				dispatchTakePictureIntent();
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQUEST_CODE_MAKE_PHOTO:
					iExerciseEditPresenter.updateImage(mCurrentPhotoPath);
					break;
			}
		}
	}

	private void initMuscleGroupsView() {
		//Init muscle groups dialog.
		String[] names = getResources().getStringArray(R.array.exercises_types_array);
		int[] ids = getResources().getIntArray(R.array.exercises_num_types_array);
		txtMuscleGroups.setData(names, ids);
		txtMuscleGroups.setTitle(getString(R.string.title_muscle_groups));
		txtMuscleGroups.showNeutralButton(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Timber.v("onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.exercise_edit_menu, menu);
		if (loadingError) {
			menu.findItem(R.id.action_accept).setVisible(false);
			menu.findItem(R.id.action_add_youtube_video).setVisible(false);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Timber.v("onPrepareOptionsMenu");
		return super.onPrepareOptionsMenu(menu);
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
				if (id == ID_UNKNOWN) {
					iExerciseEditPresenter.addExercise(getExerciseData());
				} else {
//					TODO: when update image must delete previous image!
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
		Timber.e("showError");
//		Snackbar snack = Snackbar.make(coordinatorLayout, getString(R.string.common_error), Snackbar.LENGTH_LONG);
//		View view = snack.getView();
//		TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
//		tv.setTextColor(Color.WHITE);
//		snack.show();
		findViewById(R.id.exe_edit_name_container).setVisibility(View.GONE);
		findViewById(R.id.exe_edit_description_input).setVisibility(View.GONE);
		ivImage.setVisibility(View.GONE);
		txtMuscleGroups.setVisibility(View.GONE);
		txtDescription.setVisibility(View.GONE);
		findViewById(R.id.exe_edit_error).setVisibility(View.VISIBLE);
		loadingError = true;
		invalidateOptionsMenu();
	}

	@Override
	public void setImage(String path) {
		Timber.v("Path = " + path);
		ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
		Glide.with(ExerciseEditActivity.this)
				.load(path)
				.error(R.drawable.alert_circle)
				.into(ivImage);
	}

	@Override
	public void setName(String name) {
		Timber.v("setName = " + name);
		txtName.setText(name);
	}

	@Override
	public void selectGroup(int[] ids) {
		Timber.v("setGroup = " + Arrays.toString(ids));
		txtMuscleGroups.setSelectedIds(ids);
	}

	@Override
	public void setDescription(String description) {
		txtDescription.setText(description);
	}

	@Override
	public void exerciseUpdated() {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void exerciseAdded(ExerciseDataModel model) {
		Intent intent = new Intent();
		intent.putExtra(ExercisesActivity.EXTRAS_KEY_EXERCISE_MODEL, model);
		setResult(RESULT_OK, intent);
		finish();
	}

	private ExerciseDataModel getExerciseData() {
		return new ExerciseDataModel(
				id,
				txtMuscleGroups.getSelectedIds(),
				txtName.getText().toString(),
				txtDescription.getText().toString(),
				mCurrentPhotoPath,
				null,
				false);
	}
}
