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

package ua.com.sofon.workoutlogger.ui.exercises.presenter;

import android.support.annotation.NonNull;
import java.util.Arrays;

import timber.log.Timber;
import ua.com.sofon.workoutlogger.IBaseView;
import ua.com.sofon.workoutlogger.business.exercises.IExerciseEditInteractor;
import ua.com.sofon.workoutlogger.ui.exercises.models.ExerciseDataModel;
import ua.com.sofon.workoutlogger.ui.exercises.views.IExerciseEditView;

/**
 * Created on 31.03.2017.
 * @author Dimowner
 */
public class ExerciseEditPresenter implements IExerciseEditPresenter {

	private IExerciseEditInteractor iExerciseEditInteractor;
	private IExerciseEditView iExerciseEditView;

	public ExerciseEditPresenter(IExerciseEditInteractor iExerciseEditInteractor) {
		this.iExerciseEditInteractor = iExerciseEditInteractor;
	}

	@Override
	public void bindView(@NonNull IBaseView view) {
		this.iExerciseEditView = (IExerciseEditView) view;
	}

	@Override
	public void unbindView() {
		iExerciseEditView = null;
	}

	@Override
	public void clickAddYoutubeVideo() {

	}

	@Override
	public void clickAddImage() {

	}

	@Override
	public void addExercise(ExerciseDataModel data) {
//		ToDO: do validation
		iExerciseEditInteractor.addExercise(data).subscribe(this::handleSuccess, this::handleError);
	}

	private void handleSuccess(ExerciseDataModel dataModel) {
		Timber.v("Opa result = " + dataModel.toString());
		iExerciseEditView.exerciseAdded(dataModel);
	}

	private void handleError(Throwable throwable) {
		Timber.e(throwable);
	}

	@Override
	public void updateExercise(ExerciseDataModel data) {
//		ToDO: do validation
		iExerciseEditInteractor.updateExercise(data);
		iExerciseEditView.exerciseUpdated();
	}

	@Override
	public void loadExerciseData(long id) {
		iExerciseEditView.showProgress();
		iExerciseEditInteractor.loadData(id).subscribe(
				this::handleSuccessLoadExerciseData,
				this::handleErrorLoadExerciseData
		);
	}

	private void handleSuccessLoadExerciseData(@NonNull ExerciseDataModel data) {
		if (data.getImagePath() != null && !data.getImagePath().isEmpty()) {
			iExerciseEditView.setImage(data.getImagePath());
		}
		iExerciseEditView.setName(data.getName());
		iExerciseEditView.setDescription(data.getDescription());
		iExerciseEditView.selectGroup(data.getGroups());
		iExerciseEditView.hideProgress();
	}

	private void handleErrorLoadExerciseData(Throwable throwable) {
//		TODO: handle error
		Timber.e(throwable);
//		iExerciseEditView.hideProgress();
//		iExerciseEditView.showError();
	}
}
