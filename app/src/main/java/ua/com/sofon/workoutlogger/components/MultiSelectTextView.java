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

package ua.com.sofon.workoutlogger.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import ua.com.sofon.workoutlogger.R;

/**
 * TextView with Multi select dialog.
 * @author Dimowner
 */
public class MultiSelectTextView extends AppCompatTextView {

	public MultiSelectTextView(Context context) {
		super(context);
		this.context = context;
	}

	public MultiSelectTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	/**
	 * View initialization
	 * @param context Application context.
	 */
	private void initDialog(final Context context, final List<String> names, List<Integer> ids) {
		setClickable(true);
		listView = new ListView(context);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		adapter = new CheckBoxListAdapter(names, ids);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener((parent, view, position, id) ->
				listView.setItemChecked(position, listView.isItemChecked(position)));
		alertDialog = new AlertDialog.Builder(context)
				.setView(listView)
				.setPositiveButton(R.string.btn_select, (dialog, whichButton) -> {
					long[] selectedIds = listView.getCheckedItemIds();

					String[] names1 = new String[selectedIds.length];
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < selectedIds.length; i++) {
						names1[i] = adapter.findNameById((int)selectedIds[i]);
						sb.append(names1[i]);
						if (i < selectedIds.length - 1) {
							sb.append(", ");
						}
					}
					setText(sb.toString());
				})
				.setNegativeButton(R.string.btn_cancel, (dialog, whichButton) -> {})
				.setNeutralButton(R.string.btn_clear, (dialog, whichButton) -> {
						setText("");
						listView.clearChoices();})
				.create();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP
				&& isEnabled()) {
			alertDialog.show();
			if (showNeutralButton) {
				alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
						.setVisibility(View.VISIBLE);
			} else {
				alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
						.setVisibility(View.GONE);
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * Set button "Clear" visible.
	 * @param showButton If true the button will be visible either not.
	 */
	public void showNeutralButton(boolean showButton) {
		showNeutralButton = showButton;
	}

	/**
	 * Set items data to show in dialog.
	 * @param names Items names to show.
	 * @param ids Items ids.
	 */
	public void setData(String[] names, int[] ids) {
		List<Integer> idList = new ArrayList<>();
		for (int i = 0; i < ids.length; i++) {
			idList.add(ids[i]);
		}
		initDialog(context, Arrays.asList(names), idList);
	}

	public int[] getSelectedIds() {
		long[] ids = listView.getCheckedItemIds();
		int[] intIds = new int[ids.length];
		for (int i = 0; i < ids.length; i++) {
			intIds[i] = (int) ids[i];
		}
		return intIds;
	}

	public void setSelectedIds(int[] selectedIds) {
		for (int i = 0; i < selectedIds.length; i++) {
			listView.setItemChecked(selectedIds[i], true);
		}
		String[] names = new String[selectedIds.length];
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < selectedIds.length; i++) {
			names[i] = adapter.findNameById(selectedIds[i]);
			sb.append(names[i]);
			if (i < selectedIds.length - 1) {
				sb.append(", ");
			}
		}
		setText(sb.toString());
	}

	/**
	 * Set the title text for this dialog's window.
	 * @param title The new text to display in the title.
	 */
	public void setTitle(CharSequence title) {
		alertDialog.setTitle(title);
	}

	/**
	 * Set the title using the given resource id.
	 * @param titleId the title's text resource identifier
	 */
	public void setTitle(int titleId) {
		alertDialog.setTitle(titleId);
	}

	/**
	 * Set the {@link android.graphics.drawable.Drawable Drawable} to be used in the title.
	 * @param icon Icon to display in dialog's title.
	 */
	public void setIcon(Drawable icon) {
		alertDialog.setIcon(icon);
	}

	/**
	 * Set the resource id of the {@link android.graphics.drawable.Drawable Drawable}
	 * to be used in the title.
	 * @param resId Resource of icon to display in dialog's title.
	 */
	public void setIcon(int resId) {
		alertDialog.setIcon(resId);
	}

	/** Dialog window for showing view */
	private AlertDialog alertDialog;

	/** Need or not show button "Clear". */
	private boolean showNeutralButton = true;

	/** Adapter shows items to select in dialogs list. */
	private CheckBoxListAdapter adapter;

	private ListView listView;

	/** Application context. */
	private Context context;


	/**
	 * Adapter contains checkable items.
	 */
	public class CheckBoxListAdapter extends BaseAdapter {

		public CheckBoxListAdapter(List<String> names, List<Integer> ids) {
			if (names != null) {
				this.names = names;
			} else {
				this.names = new ArrayList<>();
			}
			if (ids != null) {
				this.ids = ids;
			} else {
				this.ids = new ArrayList<>();
			}
		}

		@Override
		public int getCount() {
			return ids.size();
		}

		@Override
		public Object getItem(int position) {
			return names.get(position);
		}

		@Override
		public long getItemId(int position) {
			return ids.get(position);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final CheckedTextView rowView;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater)
						getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = (CheckedTextView)
						inflater.inflate(R.layout.list_item_checkbox, parent, false);
			} else {
				rowView = (CheckedTextView) convertView;
			}
			rowView.setText(names.get(position));
			return rowView;
		}

		/**
		 * Set adapters data.
		 * @param names Items names.
		 * @param ids Items ids.
		 */
		public void setData(List<String> names, List<Integer> ids) {
			if (names != null) {
				this.names = names;
			}
			if (ids != null) {
				this.ids = ids;
			}
		}

		/**
		 * Find item name by id in adapter.
		 * @param id Item id that name you want to find.
		 */
		public String findNameById(int id) {
			for (int i = 0; i < ids.size(); i++) {
				if (id == ids.get(i)) {
					return names.get(i);
				}
			}
			return null;
		}

		/** All items ids. */
		private List<Integer> ids;

		/** All items names. */
		private List<String> names;
	}
}
