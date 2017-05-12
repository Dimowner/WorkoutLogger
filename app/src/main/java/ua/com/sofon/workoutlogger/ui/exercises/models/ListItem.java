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

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10.03.2017.
 * @author Dimowner
 */
public class ListItem {

	private long id;
	private int type;
	private String name;
	private String imagePath;
	private boolean isFavorite;

	private boolean isExpanded = false;

	private List<ListItem> children;

	public ListItem(long id, int type, @NonNull String name, String imagePath, boolean isFavorite) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.imagePath = imagePath;
		this.isFavorite = isFavorite;

		this.children = new ArrayList<>();
	}

	public long getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getImagePath() {
		return imagePath;
	}

	public ListItem getChild(int pos) {
		return children.get(pos);
	}

	public void setChildren(List<ListItem> children) {
		this.children = children;
	}

	public void addChild(ListItem item) {
		children.add(item);
	}

	public void removeChild(int id) {
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).getId() == id) {
				children.remove(i);
			}
		}
	}

	public void clearChildren() {
		this.children.clear();
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public int getChildrenCount() {
		return children.size();
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public void expandGroup() {
		isExpanded = true;
	}

	public void collapseGroup() {
		isExpanded = false;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean favorite) {
		isFavorite = favorite;
	}

	public void addToFavorites() {
		isFavorite = true;
	}

	public void removeFromFavorites() {
		isFavorite = false;
	}

	@Override
	public String toString() {
		return "ListItem{" +
				"id=" + id +
				", type=" + type +
				", name='" + name + '\'' +
				", imagePath='" + imagePath + '\'' +
				", isFavorite=" + isFavorite +
				", isExpanded=" + isExpanded +
				", children=" + children +
				'}';
	}
}
