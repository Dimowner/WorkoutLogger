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

import java.util.List;

import rx.Single;
import ua.com.sofon.workoutlogger.ui.exercises.models.ListItem;

/**
 * Created on 08.03.2017.
 * @author Dimowner
 */
public interface IExercisesInteractor {

	Single<List<ListItem>> getAllExercises();
	Single<ListItem> getExercise(long id);
	Single<List<ListItem>> getFavoritesExercises();

	Single<Boolean> reverseFavorite(long id);
}
