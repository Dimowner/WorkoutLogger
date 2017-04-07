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

package ua.com.sofon.workoutlogger.data.network.models;

import android.support.annotation.NonNull;

/**
 * Created on 08.03.2017.
 * @author Dimowner
 */
public class ExerciseModel {

	protected long id;
	protected int[] groups;
	protected String name;
	protected String description;
	protected boolean isFavorite;
//	TODO: Add photos

	public ExerciseModel(long id, @NonNull int[] groups, @NonNull String name, @NonNull String description, boolean isFavorite) {
		this.id = id;
		this.groups = groups;
		this.name = name;
		this.description = description;
		this.isFavorite = isFavorite;
	}

	public long getId() {
		return id;
	}

	public int[] getGroups() {
		return groups;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	@Override
	public String toString() {
		return "ExerciseModel{ id = " + id
				+ ", groups size = " + groups.length
				+ ", name = " + name + ", description = " + description + "}";
	}
}
