package apt.ToDoList;

import android.preference.PreferenceActivity;
import android.os.Bundle;

public class EditPreferences extends PreferenceActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
	}
	
}
