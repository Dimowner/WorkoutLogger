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

import ua.com.sofon.workoutlogger.business.exercises.IExerciseDetailsInteractor;
import ua.com.sofon.workoutlogger.ui.exercises.models.ExerciseDataModel;
import ua.com.sofon.workoutlogger.ui.exercises.views.IExerciseDetailsView;

/**
 * Created on 04.04.2017.
 *
 * @author Dimowner
 */
public class ExerciseDetailsPresenter implements IExerciseDetailsPresenter {

	IExerciseDetailsInteractor iExerciseDetailsInteractor;
	IExerciseDetailsView iExerciseDetailsView;

	public ExerciseDetailsPresenter(IExerciseDetailsInteractor iExerciseDetailsInteractor) {
		this.iExerciseDetailsInteractor = iExerciseDetailsInteractor;
	}

	@Override
	public void bindView(@NonNull IExerciseDetailsView iExerciseDetailsView) {
		this.iExerciseDetailsView = iExerciseDetailsView;
	}

	@Override
	public void unbindView() {
		this.iExerciseDetailsView = null;
	}

	@Override
	public void clickShowYoutubeVideo() {

	}

	@Override
	public void reverseFavorite(long id) {
//		todo: say update favorites -> after update view
		iExerciseDetailsInteractor.reverseFavorite(id);
	}

	@Override
	public void clickEditExercise(ExerciseDataModel data) {

	}

	@Override
	public void clickDeleteExercise(long id) {
		iExerciseDetailsInteractor.deleteExercise(id);
		iExerciseDetailsView.deleteExerciseClicked();
	}

	@Override
	public void loadExerciseData(long id) {
		ExerciseDataModel data = iExerciseDetailsInteractor.loadData(id);
		if (data != null) {
			if (data.getImagePath() != null && !data.getImagePath().isEmpty()) {
				iExerciseDetailsView.setImage(data.getImagePath());
			}
			iExerciseDetailsView.setName(data.getName());
			iExerciseDetailsView.setDescription(data.getDescription());
			iExerciseDetailsView.selectGroup(data.getGroup() + "");
		}
	}
}
