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

package ua.com.sofon.workoutlogger.ui.exercises.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;
import ua.com.sofon.workoutlogger.R;
import ua.com.sofon.workoutlogger.WLApplication;
import ua.com.sofon.workoutlogger.dagger.exercises.ExercisesModule;
import ua.com.sofon.workoutlogger.ui.exercises.models.ExerciseDataModel;
import ua.com.sofon.workoutlogger.ui.exercises.models.ListItem;
import ua.com.sofon.workoutlogger.ui.exercises.presenter.IExercisesPresenter;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created on 07.03.2017.
 * @author Dimowner
 */
public class ExercisesFragment extends Fragment implements IExercisesView {

	public static final int VIEW_TYPE_ALL_EXERCISES = 1;
	public static final int VIEW_TYPE_FEV_EXERCISES = 2;

	public static final int ADD_TO_FAVORITES_ANIMATION_DURATION = 500;

	public static final String EXTRAS_KEY_VIEW_TYPE = "view_type";

	public static final int REQ_CODE_EXERCISE_DETAILS = 3;

	@Inject
	IExercisesPresenter iExercisesPresenter;

	@BindView(R.id.progress_view) ProgressBar mProgressView;

	@BindView(R.id.recycler_view) RecyclerView mRecyclerView;

	private ExpandableRecyclerAdapter mAdapter;

	private OnListItemUpdateListener onListItemUpdateListener;

	private int mViewType;

	private long someId = -1;

	public static ExercisesFragment newInstance(int type) {
		ExercisesFragment fragment = new ExercisesFragment();
		Bundle args = new Bundle();
		if (type == VIEW_TYPE_FEV_EXERCISES) {
			args.putInt(EXTRAS_KEY_VIEW_TYPE, VIEW_TYPE_FEV_EXERCISES);
		} else {
			args.putInt(EXTRAS_KEY_VIEW_TYPE, VIEW_TYPE_ALL_EXERCISES);
		}
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WLApplication.get(getContext()).applicationComponent()
				.plus(new ExercisesModule()).injectAllExes(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
									 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_exercises, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (getArguments() != null) {
			mViewType = getArguments().getInt(EXTRAS_KEY_VIEW_TYPE, VIEW_TYPE_ALL_EXERCISES);
		}

		mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		mRecyclerView.setHasFixedSize(true);

		// use a linear layout manager
		RecyclerView.LayoutManager mLayoutManager = new MyLinearLayoutManager(getContext());
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setLayoutManager(mLayoutManager);

		DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
		divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.recycler_divider));
		mRecyclerView.addItemDecoration(divider);

		mAdapter = new ExpandableRecyclerAdapter();
		mAdapter.setClickListener((view1, position) -> {
			Intent intent = new Intent(getActivity(), ExerciseDetailsActivity.class);
			someId = mAdapter.getItemId(position);
			intent.putExtra(ExercisesActivity.EXTRAS_KEY_EXERCISE_ID, mAdapter.getItemId(position));
			startActivityForResult(intent, REQ_CODE_EXERCISE_DETAILS);
		});
		mAdapter.setOnFavoriteClickListener((view12, position, id, action) -> {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
				//Add or remove from favorites with animation
				view12.setImageResource(R.drawable.avd_favorite_progress);
				Animatable animatable = ((Animatable) view12.getDrawable());
				animatable.start();
				iExercisesPresenter.reverseFavorite(id)
						.delay(ADD_TO_FAVORITES_ANIMATION_DURATION, TimeUnit.MILLISECONDS)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(b -> {
									animatable.stop();
									mAdapter.updateFavorites(id, b);
									mAdapter.notifyItemChanged(position);
									if (onListItemUpdateListener != null) {
										onListItemUpdateListener.onListItemUpdate(id);
									}
								},
								throwable -> {
									animatable.stop();
									Timber.e("", throwable);
								});
			} else {
				//Add or remove from favorites without animation
				iExercisesPresenter.reverseFavorite(id)
						.subscribe(b -> {
									mAdapter.updateFavorites(id, b);
									mAdapter.notifyItemChanged(position);
									if (onListItemUpdateListener != null) {
										onListItemUpdateListener.onListItemUpdate(id);
									}
								},
								throwable -> Timber.e("", throwable));
			}
		});
		mRecyclerView.setAdapter(mAdapter);

		iExercisesPresenter.bindView(this);
		buildList();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Timber.v("activityResult req = " + requestCode + " res = " + resultCode);
		int action = ExerciseDetailsActivity.ACTION_UNKNOWN;
		if (data != null && data.hasExtra(ExerciseDetailsActivity.EXTRAS_KEY_ACTION)) {
			action = data.getIntExtra(ExerciseDetailsActivity.EXTRAS_KEY_ACTION, ExerciseDetailsActivity.ACTION_UNKNOWN);
		}
		if ((resultCode == RESULT_OK || resultCode == RESULT_CANCELED)
				&& requestCode == REQ_CODE_EXERCISE_DETAILS) {
			switch (action) {
				case ExerciseDetailsActivity.ACTION_DELETED:
					onListItemUpdateListener.onListItemUpdate(someId);
					mAdapter.removeItem(someId);
					break;
				case ExerciseDetailsActivity.ACTION_ADDED_TO_FAVORITES:
					onListItemUpdateListener.onListItemUpdate(someId);
					mAdapter.updateFavorites(someId, true);
					break;
				case ExerciseDetailsActivity.ACTION_REMOVED_FROM_FAVORITES:
					onListItemUpdateListener.onListItemUpdate(someId);
					mAdapter.updateFavorites(someId, false);
					break;
				case ExerciseDetailsActivity.ACTION_UPDATED:
					onListItemUpdateListener.onListItemUpdate(someId);
					iExercisesPresenter.updateListItem(someId);
					break;
				case ExerciseDetailsActivity.ACTION_UNKNOWN:
					//Do nothing
					break;
			}
		}
	}

	public void buildList() {
		if (mViewType == VIEW_TYPE_ALL_EXERCISES) {
			iExercisesPresenter.loadAllExercises();
		} else {
			iExercisesPresenter.loadFavoritesExercises();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		iExercisesPresenter.unbindView();
	}

	public void addItemToList(ExerciseDataModel item) {
		mAdapter.addItem(new ListItem(item.getId(), item.getGroups()[0], item.getName(), item.getImagePath(), item.isFavorite()));
	}

	public void removeItemFromList(long id) {
		mAdapter.removeItem(id);
	}

	public void updateListItem(ExerciseDataModel item) {
		mAdapter.updateListItem(new ListItem(item.getId(), item.getGroups()[0], item.getName(), item.getImagePath(), item.isFavorite()));
	}

	public void updateListItem(long id) {
		iExercisesPresenter.updateListItem(id);
	}

	public void updateListFavorites(long id, int action) {
		mAdapter.updateFavorites(id, action);
	}

	public void setOnListItemUpdateListener(OnListItemUpdateListener onListItemUpdateListener) {
		this.onListItemUpdateListener = onListItemUpdateListener;
	}

	@Override
	public void showProgress() {
//		TODO:implement showing progress
		Timber.v("Show progress");
		mProgressView.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideProgress() {
		Timber.v("Hide progress");
		mProgressView.setVisibility(View.GONE);
	}

	@Override
	public void showLoadError(String error) {
		Timber.v("Show Load error");
	}

	@Override
	public void showExercises(List<ListItem> list) {
		Timber.v("Show all exercises" + list.size());
		mAdapter.setData(list);
	}

	@Override
	public void updateExercise(ListItem item) {
		mAdapter.updateListItem(item);
	}

	@Override
	public void switchToFevExercises() {
		Timber.v("Switch to favorite exercises");
	}

	@Override
	public void expandGroup() {
		Timber.v("Expand group");
	}

	@Override
	public void collapseGroup() {
		Timber.v("Collapse group");
	}


	public class ExpandableRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

		private final static int TYPE_GROUP = 1;
		private final static int TYPE_CHILD = 2;

		public final static int ACTION_ADD_TO_FAVORITES = 10;
		public final static int ACTION_REMOVE_FROM_FAVORITES = 11;

		private List<ListItem> mShowingData;
		private ClickListener mClickListener;
		private OnFavoriteClickListener onFavoriteClickListener;

		public class GroupViewHolder extends RecyclerView.ViewHolder {
			public View mView;
			public TextView txtName;
			public ImageView ivImage;
			public ImageView ivIcon;

			public GroupViewHolder(View v) {
				super(v);
				mView = v;
				txtName = (TextView) v.findViewById(R.id.exe_group_name);
				ivImage = (ImageView) v.findViewById(R.id.exe_group_image);
				ivIcon = (ImageView) v.findViewById(R.id.exe_group_icon);
			}
		}

		public class ChildViewHolder extends RecyclerView.ViewHolder {
			public View mView;
			public TextView txtName;
			public ImageView ivImage;
			public ImageView ivIcon;

			public ChildViewHolder(View v) {
				super(v);
				mView = v;
				txtName = (TextView) v.findViewById(R.id.exe_group_name);
				ivImage = (ImageView) v.findViewById(R.id.exe_group_image);
				ivIcon = (ImageView) v.findViewById(R.id.exe_group_icon);
			}
		}

		public ExpandableRecyclerAdapter() {
			mShowingData = new ArrayList<>();
		}

		public void setData(List<ListItem> data) {
			Timber.v("setData = " + data.size());
			mShowingData = data;
			notifyDataSetChanged();
		}

		public void addItem(ListItem item) {
			mShowingData.add(item);
			notifyDataSetChanged();
		}

		public void removeItem(long id) {
			for (int i = 0; i < mShowingData.size(); i++) {
				if (mShowingData.get(i).getId() == id) {
					mShowingData.remove(i);
					notifyDataSetChanged();
					break;
				}
			}
		}

		public void updateListItem(ListItem item) {
			for (int i = 0; i < mShowingData.size(); i++) {
				if (mShowingData.get(i).getId() == item.getId()) {
					mShowingData.set(i, item);
					notifyItemChanged(i);
					break;
				}
			}
		}

		public void updateFavorites(long id, boolean fav) {
			for (int i = 0; i < mShowingData.size(); i++) {
				if (mShowingData.get(i).getId() == id) {
					mShowingData.get(i).setFavorite(fav);
					notifyItemChanged(i);
					break;
				}
			}
		}

		public void updateFavorites(long id, int action) {
			for (int i = 0; i < mShowingData.size(); i++) {
				if (mShowingData.get(i).getId() == id) {
					if (action == ACTION_ADD_TO_FAVORITES) {
						mShowingData.get(i).setFavorite(true);
					} else {
						mShowingData.get(i).setFavorite(false);
					}
					notifyItemChanged(i);
					break;
				}
			}
		}

		@Override
		public int getItemViewType(int position) {
			if (mShowingData.get(position).hasChildren()) {
				return TYPE_GROUP;
			} else {
				return TYPE_CHILD;
			}
		}

		@Override
		public long getItemId(int position) {
			return mShowingData.get(position).getId();
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			switch (viewType) {
				case TYPE_GROUP:
					View v = LayoutInflater.from(parent.getContext())
							.inflate(R.layout.list_item_group, parent, false);
					return new GroupViewHolder(v);

				case TYPE_CHILD:
					View v1 = LayoutInflater.from(parent.getContext())
							.inflate(R.layout.list_item_child, parent, false);
					return new ChildViewHolder(v1);
				default:
					return null;
			}
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

			switch (holder.getItemViewType()) {
				case TYPE_GROUP:
					final GroupViewHolder groupViewHolder = (GroupViewHolder) holder;
					final int posGroup = groupViewHolder.getAdapterPosition();
					final ListItem group = mShowingData.get(posGroup);
					groupViewHolder.mView.setOnClickListener(v -> {
						if (group.isExpanded()) {
							hideChildren(v);
							if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
								groupViewHolder.ivIcon.setImageResource(R.drawable.vector_anim_chevron_up_to_down);
								((Animatable) groupViewHolder.ivIcon.getDrawable()).start();
							} else{
								groupViewHolder.ivIcon.setImageResource(R.drawable.chevron_down);
							}
						} else {
							showChildren(v);
							if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
								groupViewHolder.ivIcon.setImageResource(R.drawable.vector_anim_chevron_down_to_up);
								((Animatable) groupViewHolder.ivIcon.getDrawable()).start();
							} else {
								groupViewHolder.ivIcon.setImageResource(R.drawable.chevron_up);
							}
						}
					});
					groupViewHolder.txtName.setText(mShowingData.get(posGroup).getName());
					if (group.isExpanded()) {
						groupViewHolder.ivIcon.setImageResource(R.drawable.chevron_up);
					} else {
						groupViewHolder.ivIcon.setImageResource(R.drawable.chevron_down);
					}

//						Glide.with(mContext)
//								.load(R.drawable.fitness1).fitCenter().into(groupViewHolder.ivImage);

					break;
				case TYPE_CHILD:
					final ChildViewHolder childViewHolder = (ChildViewHolder) holder;
					final int posChild = childViewHolder.getAdapterPosition();
					childViewHolder.mView.setOnClickListener(v -> {
						if (mClickListener != null) {
							mClickListener.onClick(v, posChild);
						}
					});

					childViewHolder.ivIcon.setOnClickListener(v -> {
						if (onFavoriteClickListener != null) {
							int action = mShowingData.get(posChild).isFavorite()
									? ACTION_REMOVE_FROM_FAVORITES : ACTION_ADD_TO_FAVORITES;
							onFavoriteClickListener.onFavoriteClick(
									(ImageView) v, posChild, (int)mShowingData.get(posChild).getId(), action);
						}
					});

					childViewHolder.txtName.setText(mShowingData.get(posChild).getName());
					if (mShowingData.get(childViewHolder.getAdapterPosition()).isFavorite()) {
						childViewHolder.ivIcon.setImageResource(R.drawable.star);
					} else {
						childViewHolder.ivIcon.setImageResource(R.drawable.star_outline);
					}
//						Glide.with(mContext)
//								.load(R.drawable.fitness1).fitCenter().into(childViewHolder.ivImage);
					break;
			}
		}

		private void showChildren(View view) {
			int position = mRecyclerView.getChildAdapterPosition(view);
			if (position != RecyclerView.NO_POSITION) {
				ListItem group = mShowingData.get(position);
				group.expandGroup();
				int count = group.getChildrenCount();
				for (int i = 0 ; i < count; i++) {
					mShowingData.add(position + i + 1, group.getChild(i));
				}
				notifyItemRangeInserted(position + 1, count);
			}
		}

		private void hideChildren(View view) {
			int position = mRecyclerView.getChildAdapterPosition(view);
			if (position != RecyclerView.NO_POSITION) {
				ListItem group = mShowingData.get(position);
				group.collapseGroup();
				int count = group.getChildrenCount();
				for (int i = count; i > 0; i--) {
					mShowingData.remove(position + i);
				}
				notifyItemRangeRemoved(position + 1, count);
			}
		}

		public void setClickListener(ClickListener mClickListener) {
			this.mClickListener = mClickListener;
		}

		public void setOnFavoriteClickListener(OnFavoriteClickListener onFavoriteClickListener) {
			this.onFavoriteClickListener = onFavoriteClickListener;
		}

		@Override
		public int getItemCount() {
			return mShowingData.size();
		}
	}

	public interface ClickListener{
		void onClick(View view, int position);
	}

	public interface OnFavoriteClickListener {
		void onFavoriteClick(ImageView view, int position, int id, int action);
	}

	/**
	 * Simple extension of LinearLayoutManager for the sole purpose of showing what happens
	 * when predictive animations (which are enabled by default in LinearLayoutManager) are
	 * not enabled. This behavior is toggled via a checkbox in the UI.
	 */
	private class MyLinearLayoutManager extends LinearLayoutManager {
		public MyLinearLayoutManager(Context context) {
			super(context);
		}

		@Override
		public boolean supportsPredictiveItemAnimations() {
			return true;
		}
	}

	public interface OnListItemUpdateListener {
		void onListItemUpdate(long id);
	}
}
