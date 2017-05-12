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

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import ua.com.sofon.workoutlogger.IBaseView;
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
	public void bindView(@NonNull IBaseView view) {
		this.iAllExercisesView = (IExercisesView)view;
	}

	@Override
	public void unbindView() {
		this.iAllExercisesView = null;
	}

	@Override
	public void loadAllExercises() {
		iAllExercisesView.showProgress();
		iExercisesInteractor.getAllExercises()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(data -> {
					iAllExercisesView.showExercises(data);
					iAllExercisesView.hideProgress();
				}, throwable -> {
					Timber.e(throwable);
					iAllExercisesView.showLoadError(throwable.getMessage());
				});
	}

	@Override
	public void loadFavoritesExercises() {
		iAllExercisesView.showProgress();
		iExercisesInteractor.getFavoritesExercises()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(data -> {
					iAllExercisesView.showExercises(data);
					iAllExercisesView.hideProgress();
				}, throwable -> {
					Timber.e(throwable);
					iAllExercisesView.showLoadError(throwable.getMessage());
				});
	}

	@Override
	public void updateListItem(long id) {
		iAllExercisesView.showProgress();
		iExercisesInteractor.getExercise(id)
				.subscribe(listItem -> {
					iAllExercisesView.updateExercise(listItem);
					iAllExercisesView.hideProgress();
				}, throwable -> {
					Timber.e(throwable);
					iAllExercisesView.showLoadError(throwable.getMessage());
				});
	}

	@Override
	public Single<Boolean> reverseFavorite(int id) {
//		TODO: update view here
		return iExercisesInteractor.reverseFavorite(id);
	}
}
