package com.bignerdranch.android.nerdlauncher;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class NerdLauncherFragment extends ListFragment {
	private static final String TAG = "NerdLauncherFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent startUpIntent = new Intent(Intent.ACTION_MAIN);			//This will get the OS manager to return all apps that 
		startUpIntent.addCategory(Intent.CATEGORY_LAUNCHER);			//have action.MAIN and category.LAUNCHER in their intent filter
		
		PackageManager pm = getActivity().getPackageManager();			//PackageManager contains all the info of all the apps installed
		List<ResolveInfo> activities = pm.queryIntentActivities(		//Query the packageManager directly with the intent
										startUpIntent, 0);
		
		Log.i(TAG, "I've found " + activities.size() + " activities.");
		
		Collections.sort(activities, new Comparator<ResolveInfo>() {
			@Override
			public int compare(ResolveInfo a, ResolveInfo b) {
				PackageManager pm = getActivity().getPackageManager();
				return String.CASE_INSENSITIVE_ORDER.compare(			//Ignore case sensitivity
						a.loadLabel(pm).toString(), 					//loadLabel(..) returns the label of the activity
						b.loadLabel(pm).toString());
			}
		});
		
		ArrayAdapter<ResolveInfo> adapter = new ArrayAdapter<ResolveInfo>(
				getActivity(), android.R.layout.activity_list_item, android.R.id.text1, activities) {
			//Create an inner anonymous class, a subclass of ArrayAdapter
			@Override
			public View getView(int pos, View convertView, ViewGroup parent) {	//Your own implementation of getView(..)
				PackageManager pm = getActivity().getPackageManager();
				
				View view = super.getView(pos, convertView, parent);			//Call your superclass's implementation of getView()
				// Documentation says that simple_list_item_1 is a TextView,
				// so cast it that you can set its text value
//				TextView tv = (TextView) view;
				
				//Documentations says activity_list_item is a LinearLayout
				LinearLayout ly = (LinearLayout) view;
				
				ResolveInfo ri = getItem(pos);
				
				TextView tv = (TextView) ly.findViewById(android.R.id.text1);
				tv.setText(ri.loadLabel(pm));
				
				ImageView iv = (ImageView) ly.findViewById(android.R.id.icon);
				iv.setImageDrawable(ri.loadIcon(pm));

				return view;
			}
		};
		
		setListAdapter(adapter);
	}
	
	/*
	 * Select items on the list
	 */
	@Override
	public void onListItemClick(ListView listview, View view, int pos, long id) {
		ResolveInfo resolveInfo = (ResolveInfo) listview.getAdapter().getItem(pos);		//resolveInfo of the item at pos
		ActivityInfo activityInfo = resolveInfo.activityInfo;							//activityInfo of that item
		
		if (activityInfo == null) return;
		
		/*
		 * Challenge: Switches between tasks that are already running
		 * Keep in mind you will need to declare the following permission in your AndroidManifest.xml
		 * android.Manifest.permission.GET_TASKS	 
		 */
//		ActivityManager am = (ActivityManager) getActivity()
//				.getSystemService(Activity.ACTIVITY_SERVICE);
//		
//		int taskID = 0;
//		
//		for (RunningTaskInfo task : am.getRunningTasks(10)) {
//			taskID = task.id;									//For example, find a task to switch
//		}
//		am.moveTaskToFront(taskID, 0);							//Can add flags MOVE_TASK_WITH_HOME, MOVE_TASK_NO_USER_ACTION.
	
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name); //Create explicit intent
																						  //Similar to public Intent(Context PackageContext, Class<?> cls)		
		
		//Intent.FLAG_ACTIVITY_NEW_TASK creates only 1 task per activity. So it wont double create
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);		//Start an activity as a new task instead of on top of the current task
		startActivity(intent);								//Start activity with explicit intent
	}
	
}
