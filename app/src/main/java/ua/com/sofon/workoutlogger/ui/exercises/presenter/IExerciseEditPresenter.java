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

import ua.com.sofon.workoutlogger.ui.exercises.models.ExerciseDataModel;
import ua.com.sofon.workoutlogger.ui.exercises.views.IExerciseEditView;

/**
 * Created on 02.04.2017.
 * @author Dimowner
 */
public interface IExerciseEditPresenter {

	void bindView(@NonNull IExerciseEditView iExerciseEditView);
	void unbindView();

	void clickAddYoutubeVideo();
//	void clickAcceptChanges(ExerciseDataModel data);
	void clickAddImage();
//	void setExerciseData(ExerciseDataModel dat);

	void addExercise(ExerciseDataModel data);
	void updateExercise(ExerciseDataModel data);

	void loadExerciseData(long id);
}
