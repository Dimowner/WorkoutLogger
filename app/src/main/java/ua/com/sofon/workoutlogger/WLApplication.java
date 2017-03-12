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

package ua.com.sofon.workoutlogger;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import timber.log.Timber;
import ua.com.sofon.workoutlogger.dagger.application.AppComponent;
import ua.com.sofon.workoutlogger.dagger.application.AppModule;
import ua.com.sofon.workoutlogger.dagger.application.DaggerAppComponent;

/**
 * Created on 07.03.2017.
 * @author Dimowner
 */
public class WLApplication extends Application {

	// dagger2 appComponent
	@SuppressWarnings("NullableProblems")
	@NonNull
	private AppComponent appComponent;

	@Override
	public void onCreate() {
		super.onCreate();

		appComponent = prepareAppComponent().build();

		if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree() {
				@Override
				protected String createStackElementTag(StackTraceElement element) {
					return super.createStackElementTag(element) + ":" + element.getLineNumber();
				}
			});
		} else {
			//Release mode
			//Crashlytics.start() //Init crash reporting lib
			Timber.plant(new ReleaseTree());
		}
	}

	@NonNull
	public static WLApplication get(@NonNull Context context) {
		return (WLApplication) context.getApplicationContext();
	}

	@NonNull
	private DaggerAppComponent.Builder prepareAppComponent() {
		return DaggerAppComponent.builder()
				.appModule(new AppModule(this));
	}

	@NonNull
	public AppComponent applicationComponent() {
		return appComponent;
	}
}
