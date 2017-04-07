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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import ua.com.sofon.workoutlogger.R;
import ua.com.sofon.workoutlogger.WLApplication;
import ua.com.sofon.workoutlogger.dagger.exercises.ExercisesModule;
import ua.com.sofon.workoutlogger.ui.exercises.models.ListItem;
import ua.com.sofon.workoutlogger.ui.exercises.presenter.IExercisesPresenter;

/**
 * Created on 07.03.2017.
 * @author Dimowner
 */
public class ExercisesFragment extends Fragment implements IExercisesView {

	public static final int VIEW_TYPE_ALL_EXERCISES = 1;
	public static final int VIEW_TYPE_FEV_EXERCISES = 2;

	public static final String EXTRAS_KEY_VIEW_TYPE = "view_type";
	@Inject
	IExercisesPresenter iExercisesPresenter;

//	private MyAdapter mAdapter;
	private ExpandableRecyclerAdapter mAdapter;
	private RecyclerView mRecyclerView;

	private int mViewType;

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
		WLApplication.get(getContext()).applicationComponent().plus(new ExercisesModule()).injectAllExes(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
									 Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_exercises, container, false);

		return rootView;
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

//		List<ExerciseModel> data = new ArrayList<>();
//		data.add(new ExerciseModel(12, new int[] {2}, "Dobriy sila", "Valu s nogi ya poebaly"));
//		mAdapter = new MyAdapter(data);
//		exeListView.setAdapter(mAdapter);

		List<ListItem> data = new ArrayList<>();

//		ListItem expItem1 = new ListItem(1, 2, "Privet", null, false);
//		expItem1.addChild(new ListItem(8, 2, "Poka", null, false));
//		expItem1.addChild(new ListItem(9, 2, "Doroga", null, true));
//		expItem1.addChild(new ListItem(10, 2, "Jdet", null, true));
//		data.add(expItem1);
//		ListItem expItem2 = new ListItem(11, 5, "Kak dela?", null, false);
//		expItem2.addChild(new ListItem(12, 5, "Zaezdjay", null, false));
//		expItem2.addChild(new ListItem(13, 5, "Esli budet vremya", null, true));
//		data.add(expItem2);
//		data.add(new ListItem(3, 3, "Y meniya zbs!", null, false));
//		data.add(new ListItem(4, 7, "Roskazi pro sebiya", null, false));
//		data.add(new ListItem(5, 1, "Ny ladno mne pora", null, false));
//		data.add(new ListItem(6, 2, "Davai bratisha", null, false));
//		ListItem expItem3 = new ListItem(14, 5, "ddKak dela?", null, true);
//		expItem3.addChild(new ListItem(15, 5, "sssZaezdjay", null, false));
//		expItem3.addChild(new ListItem(16, 5, "dasEsli budet vremya", null, true));
//		data.add(expItem3);
//		data.add(new ListItem(7, 5, "Oki doki", null, false));

		mAdapter = new ExpandableRecyclerAdapter(data);
		mAdapter.setClickListener(new ClickListener() {
			@Override
			public void onClick(View view, int position) {
				Intent intent = new Intent(getActivity(), ExerciseDetailsActivity.class);
				intent.putExtra("position", position);
				Timber.v("ID = " + mAdapter.getItemId(position));
				intent.putExtra(ExercisesActivity.EXTRAS_KEY_EXERCISE_ID, mAdapter.getItemId(position));
				startActivity(intent);
			}
		});
		mAdapter.setOnFavoriteClickListener(new OnFavoriteClickListener() {
			@Override
			public void onFavoriteClick(View view, int position, int id, int action) {
				if (action == ExpandableRecyclerAdapter.ACTION_ADD_TO_FAVORITES) {
					Timber.v("Item id = " + id + ", added to favorites");
				} else {
					Timber.v("Item id = " + id + ", removed from favorites");
				}
				iExercisesPresenter.reverseFavorite(id);
			}
		});
		mRecyclerView.setAdapter(mAdapter);

		iExercisesPresenter.bindView(this);
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

	@Override
	public void showProgress() {
		Timber.v("Show progress");
	}

	@Override
	public void hideProgress() {
		Timber.v("Hide progress");
	}

	@Override
	public void showLoadError(String error) {
		Timber.v("Show Load error");
	}

	@Override
	public void showExercises(List<ListItem> list) {
		Timber.v("Show all exercises");

//		List<ListItem> data = new ArrayList<>();
//		for (ExerciseModel e : list) {
//			ListItem i = new ListItem(e.getId(), e.getGroups()[0], e.getName(), null, e.isFavorite());
//			data.add(i);
//		}

		mAdapter.setData(list);
		mAdapter.notifyDataSetChanged();
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

		public ExpandableRecyclerAdapter(List<ListItem> myDataset) {
			this.mShowingData = myDataset;
		}

		public void setData(List<ListItem> data) {
			mShowingData = data;
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
					GroupViewHolder vh = new GroupViewHolder(v);
					return vh;

				case TYPE_CHILD:
					View v1 = LayoutInflater.from(parent.getContext())
							.inflate(R.layout.list_item_child, parent, false);
					ChildViewHolder vh1 = new ChildViewHolder(v1);
					return vh1;
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
					groupViewHolder.mView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
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
					childViewHolder.mView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (mClickListener != null) {
								mClickListener.onClick(v, posChild);
							}
						}
					});

					childViewHolder.ivIcon.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (onFavoriteClickListener != null) {
								int action = mShowingData.get(posChild).isFavorite()
										? ACTION_REMOVE_FROM_FAVORITES : ACTION_ADD_TO_FAVORITES;
								onFavoriteClickListener.onFavoriteClick(v, posChild, (int)mShowingData.get(posChild).getId(), action);
								if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
									childViewHolder.ivIcon.setImageResource(R.drawable.avd_favorite_progress);
									((Animatable) childViewHolder.ivIcon.getDrawable()).start();
								} else{
									childViewHolder.ivIcon.setImageResource(R.drawable.star);
								}
							}
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
		void onFavoriteClick(View view, int position, int id, int action);
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
}
