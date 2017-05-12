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

import java.util.concurrent.TimeUnit;

import rx.Single;
import rx.schedulers.Schedulers;
import ua.com.sofon.workoutlogger.data.repositories.exercises.IExercisesRepository;
import ua.com.sofon.workoutlogger.ui.exercises.models.ExerciseDataModel;

/**
 * Created on 04.04.2017.
 * @author Dimowner
 */
public class ExerciseDetailsInteractor implements IExerciseDetailsInteractor {

	IExercisesRepository iExercisesRepository;

	public ExerciseDetailsInteractor(IExercisesRepository iExercisesRepository) {
		this.iExercisesRepository = iExercisesRepository;
	}

	@Override
	public Single<ExerciseDataModel> loadData(long id) {
		return iExercisesRepository.loadExercise(id)
				.delay(500, TimeUnit.MILLISECONDS)
				.map(ExerciseDataModel::new);
	}

	@Override
	public Single<Boolean> reverseFavorite(long id) {
		return iExercisesRepository.reverseFavorite(id)
				.delay(500, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io());
	}

	@Override
	public  Single<Boolean> deleteExercise(long id) {
		return iExercisesRepository.deleteExercise(id)
				.delay(500, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io());
	}
}
