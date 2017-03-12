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

import ua.com.sofon.workoutlogger.ui.exercises.models.ExerciseModel;

/**
 * Created on 08.03.2017.
 * @author Dimowner
 */
public class ExercisesRepositoryDemo implements IExercisesRepository {

	@Override
	public List<ExerciseModel> loadAllExercises() {
		List<ExerciseModel> exes = new ArrayList<>();
		ExerciseModel e1 = new ExerciseModel(1, new int[] {1}, "Pushes", "1Puahjsajd jshds");
		ExerciseModel e2 = new ExerciseModel(2, new int[] {3}, "Ass kicks", "2Fairchild lafslsf f");
		ExerciseModel e3 = new ExerciseModel(3, new int[] {2}, "Chest bulking", "3LALALfldsldl lfldl lsl");
		ExerciseModel e4 = new ExerciseModel(4, new int[] {6}, "Greed expand", "4Rk kkdkfdkk kdfkdk ff");
		ExerciseModel e5 = new ExerciseModel(5, new int[] {2}, "Retard improvements", "5fmsd msmdfms mmsdm m");
		ExerciseModel e6 = new ExerciseModel(6, new int[] {3}, "Yopytaa tata", "5fmsd msmdfms mmsdm m");
		ExerciseModel e7 = new ExerciseModel(7, new int[] {7}, "Mojjojp", "5fmsd msmdfms mmsdm m");
		ExerciseModel e8 = new ExerciseModel(8, new int[] {3}, "vasfa afasf", "5fmsd msmdfms mmsdm m");

		exes.add(e1);
		exes.add(e2);
		exes.add(e3);
		exes.add(e4);
		exes.add(e5);
		exes.add(e6);
		exes.add(e7);
		exes.add(e8);
		return exes;
	}
}
