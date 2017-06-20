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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.reactivex.Single;
import io.reactivex.internal.util.VolatileSizeArrayList;
import io.reactivex.observers.TestObserver;
import ua.com.sofon.workoutlogger.data.network.models.ExerciseModel;
import ua.com.sofon.workoutlogger.data.repositories.exercises.IExercisesRepository;
import ua.com.sofon.workoutlogger.ui.exercises.models.ListItem;

/**
 * Created on 16.06.2017.
 * @author Dimowner
 */
public class ExercisesInteractorTest {

	private IExercisesRepository iExercisesRepository;
	private ExercisesInteractor exercisesInteractor;

	@Before
	public void beforeEachTest() {
		iExercisesRepository = mock(IExercisesRepository.class);
		exercisesInteractor = new ExercisesInteractor(iExercisesRepository);
	}

	@Test
	public void getAllExercises_success() {
		when(iExercisesRepository.loadAllExercises()).thenReturn(
				Single.fromCallable(() -> {
						List<ExerciseModel> exes = new ArrayList<>();
						for (int i = 0; i < 5; i++) {
							exes.add(new ExerciseModel(
									i,
									new int[]{i},
									String.valueOf(i),
									String.valueOf(i),
									String.valueOf(i),
									String.valueOf(i),
									(i % 2 > 0))
							);
						}
						return exes;
					}));

		// create TestSubscriber
		TestObserver<List<ListItem>> testObserver = TestObserver.create();
		// call method and get result

		exercisesInteractor.getAllExercises().subscribe(testObserver);
		testObserver.awaitTerminalEvent();
		testObserver.assertNoErrors();
		testObserver.assertComplete();
		List<Object> exeList = (ArrayList)((VolatileSizeArrayList) testObserver.getEvents().get(0)).get(0);
		for (int i = 0; i < 5; i++) {
			ListItem item = (ListItem) exeList.get(i);
			assertThat(item.getId()).isEqualTo(i);
			assertThat(item.getType()).isEqualTo(i);
			assertThat(item.getName()).isEqualTo(String.valueOf(i));
			assertThat(item.getImagePath()).isEqualTo(String.valueOf(i));
			assertThat(item.isFavorite()).isEqualTo(i % 2 > 0);
		}
	}

	@Test
	public void getExercise_success() {
		when(iExercisesRepository.loadExercise(1)).thenReturn(
				Single.fromCallable(() ->
					new ExerciseModel(
							1,
							new int[]{0, 2, 4},
							"name",
							"description",
							"imagePath",
							"videoPath",
							false)
			));

		// create TestSubscriber
		TestObserver<ListItem> testObserver = TestObserver.create();
		// call method and get result

		exercisesInteractor.getExercise(1).subscribe(testObserver);
		testObserver.awaitTerminalEvent();
		testObserver.assertNoErrors();
		testObserver.assertComplete();

		ListItem item = (ListItem) testObserver.getEvents().get(0).get(0);
		assertThat(item.getId()).isEqualTo(1);
		assertThat(item.getType()).isEqualTo(0);
		assertThat(item.getName()).isEqualTo("name");
		assertThat(item.getImagePath()).isEqualTo("imagePath");
		assertThat(item.isFavorite()).isEqualTo(false);
	}

	@Test
	public void getFavoritesExercises_success() {
		when(iExercisesRepository.loadFavoritesExercises()).thenReturn(
				Single.fromCallable(() -> {
					List<ExerciseModel> exes = new ArrayList<>();
					for (int i = 0; i < 5; i++) {
						exes.add(new ExerciseModel(
								i,
								new int[]{i},
								String.valueOf(i),
								String.valueOf(i),
								String.valueOf(i),
								String.valueOf(i),
								true)
						);
					}
					return exes;
				}));

		// create TestSubscriber
		TestObserver<List<ListItem>> testObserver = TestObserver.create();
		// call method and get result

		exercisesInteractor.getFavoritesExercises().subscribe(testObserver);
		testObserver.awaitTerminalEvent();
		testObserver.assertNoErrors();
		testObserver.assertComplete();
		List<Object> exeList = (ArrayList)((VolatileSizeArrayList) testObserver.getEvents().get(0)).get(0);
		for (int i = 0; i < 5; i++) {
			ListItem item = (ListItem) exeList.get(i);
			assertThat(item.getId()).isEqualTo(i);
			assertThat(item.getType()).isEqualTo(i);
			assertThat(item.getName()).isEqualTo(String.valueOf(i));
			assertThat(item.getImagePath()).isEqualTo(String.valueOf(i));
			assertThat(item.isFavorite()).isEqualTo(true);
		}
	}
}
