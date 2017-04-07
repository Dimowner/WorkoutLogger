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

package ua.com.sofon.workoutlogger.data.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created on 29.03.2017.
 * @author Dimowner
 */
public class Exercise extends RealmObject {

	//TODO:check or delete this field
	public static final String ID = "id";
	public static final String IS_FAVORITE = "isFavorite";

	@PrimaryKey
	private long id;

	private int group; //TODO: fix into multi groups

	private String name;

	private String description;

	private boolean isFavorite;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean favorite) {
		isFavorite = favorite;
	}

	@Override
	public String toString() {
		return "Exercise{" +
				"id=" + id +
				", group=" + group +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", isFavorite=" + isFavorite +
				'}';
	}
}
