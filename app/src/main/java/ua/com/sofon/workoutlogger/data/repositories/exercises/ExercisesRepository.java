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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import timber.log.Timber;
import ua.com.sofon.workoutlogger.data.realm.ExeGroup;
import ua.com.sofon.workoutlogger.data.realm.Exercise;
import ua.com.sofon.workoutlogger.data.network.models.ExerciseModel;
import ua.com.sofon.workoutlogger.util.FileUtil;

/**
 * Created on 08.03.2017.
 * @author Dimowner
 */
public class ExercisesRepository implements IExercisesRepository {

	@Override
	public Single<List<ExerciseModel>> loadAllExercises() {
		List<ExerciseModel> exes = new ArrayList<>();
		Realm realm = Realm.getDefaultInstance();
		try {
			RealmResults<Exercise> realmResults = realm.where(Exercise.class).findAllAsync();
			for (Exercise e : realmResults) {
				ExerciseModel model = new ExerciseModel(e);
				exes.add(model);
			}
		} finally {
			realm.close();
		}
		return Single.just(exes);
	}

	@Override
	public Single<List<ExerciseModel>> loadFavoritesExercises() {
		List<ExerciseModel> exes = new ArrayList<>();
		Realm realm = Realm.getDefaultInstance();
		try {
			RealmResults<Exercise> realmResults = realm.where(Exercise.class).equalTo(Exercise.IS_FAVORITE, true).findAllAsync();
			for (Exercise e : realmResults) {
				ExerciseModel model = new ExerciseModel(e);
				exes.add(model);
			}
		} finally {
			realm.close();
		}
		return Single.just(exes);
	}

	@Override
	public Single<ExerciseModel> loadExercise(long id) {
		Realm realm = Realm.getDefaultInstance();
		try {
			RealmResults<Exercise> r = realm.where(Exercise.class).equalTo(Exercise.ID, id).findAllAsync();
			return Single.just(new ExerciseModel(r.get(0)));
		} finally {
			realm.close();
		}
	}

	@Override
	public Single<ExerciseModel> addExercise(final ExerciseModel data) {
		Timber.v("addExercise = " + data.toString());
		Realm realm = Realm.getDefaultInstance();
		try {
			realm.beginTransaction();
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

			if (data.getImagePath() != null) {
				exercise.setImagePath(FileUtil.moveImageIntoAppDir(data.getImagePath(), FileUtil.PICTURES_DIR));
			}

			exercise.setFavorite(false);

			Exercise e = realm.copyToRealmOrUpdate(exercise);
			realm.commitTransaction();

			return Single.just(e).map(ExerciseModel::new);
		} finally {
			realm.close();
		}
	}

	@Override
	public Single<ExerciseModel> updateExercise(final ExerciseModel data) {
		Realm realm = Realm.getDefaultInstance();
		try {
			final RealmResults<Exercise> results = realm.where(Exercise.class)
					.equalTo(Exercise.ID, data.getId()).findAll();
			realm.beginTransaction();
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
			//Update image path in exercise.
			if ((data.getImagePath() != null && e.getImagePath() == null)
					|| (data.getImagePath() == null && e.getImagePath() != null)
					|| (data.getImagePath() != null && e.getImagePath() != null && !data.getImagePath().equals(e.getImagePath()))) {

				if (e.getImagePath() != null) {
					//Delete not needed image after replace it by new one.
					FileUtil.deleteFile(new File(e.getImagePath()));
				}
				if (data.getImagePath() != null) {
					//Set new image.
					e.setImagePath(FileUtil.moveImageIntoAppDir(data.getImagePath(), FileUtil.PICTURES_DIR));
				} else {
					e.setImagePath(null);
				}
			}

			realm.commitTransaction();
			return Single.just(e).map(ExerciseModel::new);
		} finally {
			realm.close();
		}
	}

	@Override
	public Single<Boolean> deleteExercise(long id) {
		Realm realm = Realm.getDefaultInstance();
		try {
			final RealmResults<Exercise> results = realm.where(Exercise.class).equalTo(Exercise.ID, id).findAll();
			realm.beginTransaction();
			final Boolean result = results.deleteAllFromRealm();
			realm.commitTransaction();
			return Single.just(result);
		} finally {
			realm.close();
		}
	}

	@Override
	public Single<Boolean> reverseFavorite(long id) {
		Realm realm = Realm.getDefaultInstance();
		try {
			final RealmResults<Exercise> results = realm.where(Exercise.class).equalTo(Exercise.ID, id).findAll();
			final Exercise e = results.get(0);
			final boolean fav = !e.isFavorite();
			realm.executeTransaction(realm1 -> e.setFavorite(fav));
			return Single.just(fav);
		} finally {
			realm.close();
		}
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
