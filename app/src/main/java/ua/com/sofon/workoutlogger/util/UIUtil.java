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

package ua.com.sofon.workoutlogger.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import ua.com.sofon.workoutlogger.R;

/**
 * User interface common methods, created on 01.03.2015.
 * @author Dimowner
 */
public class UIUtil {

	/**
	 * Constructor
	 * Is private to forbid creation of an object.
	 */
	private UIUtil() {
	}

	/**
	 * Show warning dialog with OK and Cancel buttons.
	 * @param context Application context.
	 * @param mesText Message to show in dialog.
	 * @param positiveBtnClickListener Listener for positive button click.
	 * @param negativeBtnClickListener Listener for negative button click.
	 */
	public static void showWarningDialog(
			Context context, String mesText,
			DialogInterface.OnClickListener positiveBtnClickListener,
			DialogInterface.OnClickListener negativeBtnClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.title_warning)
				.setMessage(mesText)
				.setIcon(R.drawable.alert)
				.setCancelable(false)
				.setPositiveButton(R.string.btn_ok, positiveBtnClickListener)
				.setNegativeButton(R.string.btn_cancel, negativeBtnClickListener);

		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Show warning dialog with OK and Cancel buttons.
	 * @param context Application context.
	 * @param mesRes Resource id of message to show in dialog.
	 * @param positiveBtnClickListener Listener for positive button click.
	 * @param negativeBtnClickListener Listener for negative button click.
	 */
	public static void showWarningDialog(
			Context context, int mesRes,
			DialogInterface.OnClickListener positiveBtnClickListener,
			DialogInterface.OnClickListener negativeBtnClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.title_warning)
				.setMessage(mesRes)
				.setIcon(R.drawable.alert)
				.setCancelable(false)
				.setPositiveButton(R.string.btn_ok, positiveBtnClickListener)
				.setNegativeButton(R.string.btn_cancel, negativeBtnClickListener);

		AlertDialog alert = builder.create();
		alert.show();
	}
}
