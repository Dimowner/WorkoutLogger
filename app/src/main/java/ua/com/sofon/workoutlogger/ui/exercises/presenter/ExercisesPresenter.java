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

import rx.subscriptions.CompositeSubscription;
import ua.com.sofon.workoutlogger.business.exercises.IExercisesInteractor;
import ua.com.sofon.workoutlogger.ui.exercises.views.IExercisesView;

/**
 * Created on 08.03.2017.
 * @author Dimowner
 */
public class ExercisesPresenter implements IExercisesPresenter {

	private IExercisesInteractor iExercisesInteractor;

	private IExercisesView iAllExercisesView;

	private CompositeSubscription compositeSubscription = new CompositeSubscription();


	public ExercisesPresenter(IExercisesInteractor iExercisesInteractor) {
		this.iExercisesInteractor = iExercisesInteractor;
	}

	@Override
	public void bindView(@NonNull IExercisesView iAllExercisesView) {
		this.iAllExercisesView = iAllExercisesView;
	}

	@Override
	public void unbindView() {
		this.iAllExercisesView = null;
	}

	@Override
	public void loadAllExercises() {
		iAllExercisesView.showProgress();
		iAllExercisesView.showExercises(iExercisesInteractor.getAllExercises());
//		iAllExercisesView.buildList(iExercisesInteractor.getAllExercises())

	}

	@Override
	public void loadFavoritesExercises() {
		iAllExercisesView.showProgress();
		iAllExercisesView.showExercises(iExercisesInteractor.getFavoritesExercises());
	}

	@Override
	public boolean reverseFavorite(int id) {
//		TODO: update view here
		return iExercisesInteractor.reverseFavorite(id);
	}
}
