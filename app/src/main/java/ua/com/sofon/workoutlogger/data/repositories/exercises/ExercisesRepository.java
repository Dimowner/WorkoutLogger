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

package ua.com.sofon.workoutlogger.data.repositories.exercises;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import ua.com.sofon.workoutlogger.data.realm.ExeGroup;
import ua.com.sofon.workoutlogger.data.realm.Exercise;
import ua.com.sofon.workoutlogger.data.network.models.ExerciseModel;

/**
 * Created on 08.03.2017.
 * @author Dimowner
 */
public class ExercisesRepository implements IExercisesRepository {

	@Override
	public List<ExerciseModel> loadAllExercises() {
//		TODO: load exercise async
		List<ExerciseModel> exes = new ArrayList<>();
		Realm realm = Realm.getDefaultInstance();
		try {
			RealmResults<Exercise> realmResults = realm.where(Exercise.class).findAll();
			for (Exercise e : realmResults) {
				ExerciseModel model = new ExerciseModel((int)e.getId(), e.getGroupsArray(), e.getName(), e.getDescription(), e.isFavorite());
				exes.add(model);
			}
		} finally {
			realm.close();
		}
		return exes;
	}

	@Override
	public List<ExerciseModel> loadFavoritesExercises() {
//		TODO: fix load fev
		List<ExerciseModel> exes = new ArrayList<>();
		Realm realm = Realm.getDefaultInstance();
		try {
			RealmResults<Exercise> realmResults = realm.where(Exercise.class).equalTo(Exercise.IS_FAVORITE, true).findAll();
			for (Exercise e : realmResults) {
				ExerciseModel model = new ExerciseModel((int)e.getId(), e.getGroupsArray(), e.getName(), e.getDescription(), e.isFavorite());
				exes.add(model);
			}
		} finally {
			realm.close();
		}
		return exes;
	}

	@Override
	public ExerciseModel loadExercise(long id) {
//		TODO: load exercise async use RxJava for asynk
		Realm realm = Realm.getDefaultInstance();
		try {
			RealmResults<Exercise> realmResults = realm.where(Exercise.class).equalTo(Exercise.ID, id).findAll();
			return exerciseToExerciseModel(realmResults.get(0));

		} finally {
			realm.close();
		}
	}

	@Override
	public long addExercise(final ExerciseModel data) {
		Realm realm = Realm.getDefaultInstance();
		try {
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					Exercise exercise = new Exercise();
					exercise.setId(getNextId(realm));

					RealmList<ExeGroup> list = new RealmList<>();
					int[] groups = data.getGroups();
					for (int i = 0; i < groups.length; i++) {
						ExeGroup exeGroup = realm.createObject(ExeGroup.class);
						exeGroup.setGroup(groups[i]);
						list.add(exeGroup);
					}
					exercise.setGroups(list);

					exercise.setName(data.getName());
					exercise.setDescription(data.getDescription());
					exercise.setFavorite(false);

					realm.copyToRealmOrUpdate(exercise);
				}
			});
		} finally {
			realm.close();
		}
		return 0;
	}

	@Override
	public void updateExercise(final ExerciseModel data) {
		Realm realm = Realm.getDefaultInstance();
		try {
			final RealmResults<Exercise> results = realm.where(Exercise.class)
					.equalTo(Exercise.ID, data.getId()).findAll();
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					Exercise e = results.get(0);

					int[] groups = data.getGroups();
					//delete unselected groups
					for (int i = e.getGroups().size() - 1; i >= 0; i--) {
						boolean contains = false;
						for (int j = 0; j < groups.length; j++) {
							if (e.getGroups().get(i).getGroup() == groups[j]) {
								contains = true;
								break;
							}
						}
						if (!contains) {
							e.getGroups().get(i).deleteFromRealm();
						}
					}

					//add new selected groups
					for (int i = 0; i < groups.length; i++) {
						boolean contains = false;
						for (int j = 0; j < e.getGroups().size(); j++) {
							if (groups[i] == e.getGroups().get(j).getGroup()) {
								contains = true;
								break;
							}
						}
						if (!contains) {
							ExeGroup exeGroup = realm.createObject(ExeGroup.class);
							exeGroup.setGroup(groups[i]);
							e.getGroups().add(exeGroup);
						}
					}

					if (!e.getName().equals(data.getName())) {
						e.setName(data.getName());
					}
					if (!e.getDescription().equals(data.getDescription())) {
						e.setDescription(data.getDescription());
					}
				}
			});
		} finally {
			realm.close();
		}
	}

	@Override
	public void deleteExercise(long id) {
		Realm realm = Realm.getDefaultInstance();
		try {
			final RealmResults<Exercise> results = realm.where(Exercise.class).equalTo(Exercise.ID, id).findAll();
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					results.deleteAllFromRealm();
				}
			});
		} finally {
			realm.close();
		}
	}

	@Override
	public boolean reverseFavorite(long id) {
		Realm realm = Realm.getDefaultInstance();
		try {
			final RealmResults<Exercise> results = realm.where(Exercise.class).equalTo(Exercise.ID, id).findAll();
			final Exercise e = results.get(0);
			final boolean fav = !e.isFavorite();
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					e.setFavorite(fav);
				}
			});
			return fav;
		} finally {
			realm.close();
		}
	}

	private ExerciseModel exerciseToExerciseModel(Exercise e) {
		return new ExerciseModel(e.getId(), e.getGroupsArray(),
				e.getName(), e.getDescription(), e.isFavorite());
	}

	/**
	 * Get next unique Id for table {@link Exercise}
	 * @param realm Realm database object
	 * @return new unique Id value.
	 */
	private long getNextId(Realm realm) {
		Number n = realm.where(Exercise.class).max(Exercise.ID);
		if (n != null) {
			return n.longValue() + 1;
		} else {
			return 1;
		}
	}
}
