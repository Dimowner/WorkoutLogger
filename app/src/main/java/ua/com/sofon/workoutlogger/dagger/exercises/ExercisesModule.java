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
import ua.com.sofon.workoutlogger.business.exercises.ExercisesInteractor;
import ua.com.sofon.workoutlogger.business.exercises.IExercisesInteractor;
import ua.com.sofon.workoutlogger.data.repositories.exercises.ExercisesRepositoryDemo;
import ua.com.sofon.workoutlogger.data.repositories.exercises.IExercisesRepository;
import ua.com.sofon.workoutlogger.ui.exercises.presenter.ExercisesPresenter;
import ua.com.sofon.workoutlogger.ui.exercises.presenter.IExercisesPresenter;

/**
 * Created on 08.03.2017.
 * @author Dimowner
 */
@Module
public class ExercisesModule {

	@Provides
	@ExercisesScope
	IExercisesRepository provideIExercisesRepository() {
		return new ExercisesRepositoryDemo();
	}

	@Provides
	@ExercisesScope
	IExercisesInteractor provideIExercisesInteractor(IExercisesRepository iExercisesRepository) {
		return new ExercisesInteractor(iExercisesRepository);
	}

	@Provides
	@ExercisesScope
	IExercisesPresenter provideIExercisesPresenter(IExercisesInteractor iExercisesInteractor) {
		return new ExercisesPresenter(iExercisesInteractor);
	}
}
