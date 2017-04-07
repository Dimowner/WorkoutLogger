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

import ua.com.sofon.workoutlogger.data.network.models.ExerciseModel;
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
	public ExerciseDataModel loadData(long id) {
		ExerciseModel e = iExercisesRepository.loadExercise(id);
		return new ExerciseDataModel(e.getId(), e.getGroups(), e.getName(), e.getDescription(), null, null, e.isFavorite());
	}

//	@Override
//	public long saveData(ExerciseDataModel data) {
////		TODO: save exercise
//		return 0;
//	}

	@Override
	public boolean validateData(ExerciseDataModel data) {
//		TODO: do some validation stuff;
		return true;
	}

	@Override
	public long addExercise(ExerciseDataModel data) {
		return iExercisesRepository.addExercise(data.toExerciseModel());
	}

	@Override
	public void updateExercise(ExerciseDataModel data) {
		iExercisesRepository.updateExercise(data.toExerciseModel());
	}
}
