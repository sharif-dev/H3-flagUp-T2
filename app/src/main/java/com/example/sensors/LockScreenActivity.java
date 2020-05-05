package com.example.sensors;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.example.sensors.lockscreen.AdminReceiver;
import com.example.sensors.lockscreen.LockScreenService;

public class LockScreenActivity extends AppCompatActivity implements
		PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

	private static final String TITLE_TAG = "settingsActivityTitle";
	private static Context context;

	public static Context getContext() {
		return context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		setContentView(R.layout.settings_activity);
		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.settings, new HeaderFragment())
					.commit();
		} else {
			setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
		}
		getSupportFragmentManager().addOnBackStackChangedListener(
				new FragmentManager.OnBackStackChangedListener() {
					@Override
					public void onBackStackChanged() {
						if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
							setTitle(R.string.title_activity_lock_screen);
						}
					}
				});
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save current activity title so we can set it again after a configuration change
		outState.putCharSequence(TITLE_TAG, getTitle());
	}

	@Override
	public boolean onSupportNavigateUp() {
		if (getSupportFragmentManager().popBackStackImmediate()) {
			return true;
		}
		return super.onSupportNavigateUp();
	}

	@Override
	public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
		// Instantiate the new Fragment
		final Bundle args = pref.getExtras();
		final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
				getClassLoader(),
				pref.getFragment());
		fragment.setArguments(args);
		fragment.setTargetFragment(caller, 0);
		// Replace the existing Fragment with the new Fragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.settings, fragment)
				.addToBackStack(null)
				.commit();
		setTitle(pref.getTitle());
		return true;
	}

	public static class HeaderFragment extends PreferenceFragmentCompat {

		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
			setPreferencesFromResource(R.xml.header_preferences, rootKey);
		}
	}

	public static class AlarmFragment extends PreferenceFragmentCompat {

		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
			setPreferencesFromResource(R.xml.alarm_preferences, rootKey);
		}
	}

	public static class ShakeFragment extends PreferenceFragmentCompat {

		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
			setPreferencesFromResource(R.xml.shake_preferences, rootKey);
			SwitchPreferenceCompat status = findPreference("status");
			if (status != null)
			{
				status.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						boolean isChecked = (boolean)newValue;
						if (isChecked) {
							Intent intent = new Intent(getContext(), ShakeDetectionService.class);
							getContext().startService(intent);
							Toast.makeText(getContext(), getResources().getString(R.string.shakeDetectionEnabled), Toast.LENGTH_SHORT).show();
						} else {
							Intent intent = new Intent(getContext(), ShakeDetectionService.class);
							getContext().stopService(intent);
							Toast.makeText(getContext(), getResources().getString(R.string.shakeDetectionDisabled), Toast.LENGTH_SHORT).show();
						}
						return true;
					}
				});
			}
		}
	}

	public static class LockScreenFragment extends PreferenceFragmentCompat {
		private static final String TAG = "LockScreenFragment";
		private static final int ADMIN_SUCCESS = 1;
		private ComponentName componentName;
		private DevicePolicyManager deviceManager;
		private SwitchPreferenceCompat status;
		private SeekBarPreference angleBar;
		private int angle;
		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
			setPreferencesFromResource(R.xml.lock_screen_preferences, rootKey);
			componentName = new ComponentName(LockScreenActivity.getContext(), AdminReceiver.class);
			deviceManager = (DevicePolicyManager)LockScreenActivity.getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
			status = findPreference("status");
			angleBar = findPreference("angle");
			angle = angleBar.getValue();
			angleBar.setSummary(LockScreenActivity.getContext().getResources().getString(R.string.current_angle, angle));
			status.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					boolean isChecked = (boolean)newValue;
					if (isChecked) {
						if (!deviceManager.isAdminActive(componentName))
						{
							Intent intent = new Intent(DevicePolicyManager
									.ACTION_ADD_DEVICE_ADMIN);
							intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
									componentName);
							intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getResources().getString(R.string.needAdminRights));
							startActivityForResult(intent, ADMIN_SUCCESS);
						}
						else
							startLockScreenService();
					}
					else
						stopLockScreenService();
					return true;
				}
			});
			angleBar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					int newAngle = (int)newValue;
					angle = newAngle;
					if (status.isChecked())
					{
						stopLockScreenService();
						startLockScreenService();
					}
					angleBar.setSummary(LockScreenActivity.getContext().getResources().getString(R.string.current_angle, angle));
					return true;
				}
			});
		}

		private static void stopLockScreenService() {
			Log.i(TAG, "onCheckedChanged: stopping lock screen service");
			Intent intent = new Intent(LockScreenActivity.getContext(), LockScreenService.class);
			if (LockScreenActivity.getContext().stopService(intent))
				Toast.makeText(LockScreenActivity.getContext(), LockScreenActivity.getContext().getResources().getString(R.string.lockScreenDisabled), Toast.LENGTH_SHORT).show();
		}

		private void startLockScreenService() {
			Log.i(TAG, "onCheckedChanged: starting lock screen service");
			Intent intent = new Intent(LockScreenActivity.getContext(), LockScreenService.class);
			intent.putExtra("angle", angle);
			LockScreenActivity.getContext().startService(intent);
			Toast.makeText(LockScreenActivity.getContext(), getResources().getString(R.string.lockScreenEnabled), Toast.LENGTH_SHORT).show();
		}
		@Override
		public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
			if (resultCode == Activity.RESULT_OK)
				startLockScreenService();
			else
				status.setChecked(false);
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
