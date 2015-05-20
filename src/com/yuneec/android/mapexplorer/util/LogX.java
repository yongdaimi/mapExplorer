package com.yuneec.android.mapexplorer.util;


import com.yuneec.android.mapexplorer.settings.AppConfig;

import android.util.Log;

public final class LogX {

	public static void i(String tag, String message) {
		if (AppConfig.developerMode) {
			Log.i(tag, message);
		}
	}
}
