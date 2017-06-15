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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import ua.com.sofon.workoutlogger.data.network.models.ExerciseModel;
import ua.com.sofon.workoutlogger.data.repositories.exercises.IExercisesRepository;
import ua.com.sofon.workoutlogger.ui.exercises.models.ListItem;

/**
 * Created on 08.03.2017.
 * @author Dimowner
 */
public class ExercisesInteractor implements IExercisesInteractor {

	private IExercisesRepository iExercisesRepository;

	public ExercisesInteractor(IExercisesRepository iExercisesRepository) {
		this.iExercisesRepository = iExercisesRepository;
	}

	@Override
	public Single<List<ListItem>> getAllExercises() {
		return iExercisesRepository.loadAllExercises()
				.delay(500, TimeUnit.MILLISECONDS)
				.map(data -> {
						List<ListItem> listData = new ArrayList<>();
						for (ExerciseModel e : data) {
							ListItem item = new ListItem(e.getId(), e.getGroups()[0], e.getName(), e.getImagePath(), e.isFavorite());
							listData.add(item);
						}
						return listData;
				});
	}

	@Override
	public Single<ListItem> getExercise(long id) {
		return iExercisesRepository.loadExercise(id)
				.map(e -> new ListItem(e.getId(), e.getGroups()[0], e.getName(), e.getImagePath(), e.isFavorite()));
	}

	@Override
	public Single<List<ListItem>> getFavoritesExercises() {
		return iExercisesRepository.loadFavoritesExercises()
				.delay(500, TimeUnit.MILLISECONDS)
				.map(data -> {
						List<ListItem> listData = new ArrayList<>();

						for (ExerciseModel e : data) {
							ListItem item = new ListItem(e.getId(), e.getGroups()[0], e.getName(), e.getImagePath(), e.isFavorite());
							listData.add(item);
						}
						return listData;
				});
	}

	@Override
	public Single<Boolean> reverseFavorite(long id) {
		return iExercisesRepository.reverseFavorite(id);
	}
}
