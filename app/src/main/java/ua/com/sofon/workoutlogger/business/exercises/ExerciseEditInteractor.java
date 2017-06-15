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

package ua.com.sofon.workoutlogger.business.exercises;

import io.reactivex.Single;
import ua.com.sofon.workoutlogger.data.repositories.exercises.IExercisesRepository;
import ua.com.sofon.workoutlogger.ui.exercises.models.ExerciseDataModel;

/**
 * Created on 02.04.2017.
 * @author Dimowner
 */
public class ExerciseEditInteractor implements IExerciseEditInteractor {

	IExercisesRepository iExercisesRepository;

	public ExerciseEditInteractor(IExercisesRepository iExercisesRepository) {
		this.iExercisesRepository = iExercisesRepository;
	}

	@Override
	public Single<ExerciseDataModel> loadData(long id) {
		return iExercisesRepository.loadExercise(id)
				.map(ExerciseDataModel::new);
	}

	@Override
	public Single<ExerciseDataModel> addExercise(ExerciseDataModel data) {
		//TODO: Fix data validation
		if (validateData(data)) {
			return iExercisesRepository.addExercise(data.toExerciseModel())
					.map(ExerciseDataModel::new);
		} else {
			return Single.error(new Exception("Validation error"));
		}
	}

	@Override
	public Single<ExerciseDataModel> updateExercise(ExerciseDataModel data) {
		//TODO: Fix data validation
		if (validateData(data)) {
			return iExercisesRepository.updateExercise(data.toExerciseModel())
					.map(ExerciseDataModel::new);
		} else {
			return Single.error(new Exception("Validation error"));
		}
	}

	private boolean validateData(ExerciseDataModel data) {
		if ((data.getName().trim().length() > 0)
				&& (data.getDescription().trim().length() > 0)
				&& (data.getGroups().length > 0)) {
			return true;
		} else {
			return false;
		}
	}
}
