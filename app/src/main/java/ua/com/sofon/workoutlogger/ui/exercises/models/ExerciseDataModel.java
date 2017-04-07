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

package ua.com.sofon.workoutlogger.ui.exercises.models;

import ua.com.sofon.workoutlogger.data.network.models.ExerciseModel;

/**
 * Created on 02.04.2017.
 * @author Dimowner
 */
public class ExerciseDataModel {

	private long id;

	private int group; //TODO: fix into multi groups

	private String name;

	private String description;

	private String imagePath;

	private String videoPath;

	private boolean isFavorite;

	public ExerciseDataModel(long id, int group, String name, String description, String imagePath, String videoPath, boolean isFavorite) {
		this.id = id;
		this.group = group;
		this.name = name;
		this.description = description;
		this.imagePath = imagePath;
		this.videoPath = videoPath;
		this.isFavorite = isFavorite;
	}

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

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean favorite) {
		isFavorite = favorite;
	}

	public ExerciseModel toExerciseModel() {
		return new ExerciseModel(id, new int[] {group}, name, description, isFavorite);
	}

	@Override
	public String toString() {
		return "ExerciseDataModel{" +
				"id=" + id +
				", group=" + group +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", imagePath='" + imagePath + '\'' +
				", videoPath='" + videoPath + '\'' +
				", isFavorite=" + isFavorite +
				'}';
	}
}
