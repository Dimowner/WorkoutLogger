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

package ua.com.sofon.workoutlogger.dagger.exercises;

import dagger.Module;
import dagger.Provides;
import ua.com.sofon.workoutlogger.business.exercises.ExerciseDetailsInteractor;
import ua.com.sofon.workoutlogger.business.exercises.IExerciseDetailsInteractor;
import ua.com.sofon.workoutlogger.data.repositories.exercises.ExercisesRepository;
import ua.com.sofon.workoutlogger.data.repositories.exercises.IExercisesRepository;
import ua.com.sofon.workoutlogger.ui.exercises.presenter.ExerciseDetailsPresenter;
import ua.com.sofon.workoutlogger.ui.exercises.presenter.IExerciseDetailsPresenter;

/**
 * Created on 04.04.2017.
 * @author Dimowner
 */
@Module
public class ExerciseDetailsModule {

	@Provides
	@ExerciseDetailsScope
	IExercisesRepository provideIExercisesRepository() {
		return new ExercisesRepository();
	}

	@Provides
	@ExerciseDetailsScope
	IExerciseDetailsInteractor provideIExerciseDetailsInteractor(IExercisesRepository iExercisesRepository) {
		return new ExerciseDetailsInteractor(iExercisesRepository);
	}

	@Provides
	@ExerciseDetailsScope
	IExerciseDetailsPresenter provideIExerciseDetailsPresenter(IExerciseDetailsInteractor iExerciseDetailsInteractor) {
		return new ExerciseDetailsPresenter(iExerciseDetailsInteractor);
	}
}
