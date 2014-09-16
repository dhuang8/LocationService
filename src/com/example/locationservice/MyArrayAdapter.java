package com.example.locationservice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter<String> {
	
	private final MainActivity context;
	private final String[] values;
	public boolean switchcheck=false;
	public int intervaltime=0;
	
	//HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	public MyArrayAdapter(MainActivity context, int textViewResourceId,
			String[] objects) {
		super(context, textViewResourceId, objects);
		this.context=context;
		values=objects;
		/*
		for (int i = 0; i < objects.size(); ++i) {
			mIdMap.put(objects.get(i), i);
		}
		*/
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);		 
		 if (position==0){
			 View rowView = inflater.inflate(R.layout.switchlist, parent, false);
			 Switch switchView = (Switch) rowView.findViewById(R.id.switch1);
			 switchView.setText(values[position]);
			 switchView.setChecked(switchcheck);
			 switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		            @Override
		            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		                /*View v = (View) buttonView.getParent();
		                TextView t = null;
		                t = (TextView) v.findViewById(R.id.textview_ProfileName);*/
		                if(isChecked){
		                    context.startLocationService();
		                }
		                else {
		                    context.stopLocationService();
		                }
		            }
		        });
			 
			 return rowView;
		 }
		 else if (position==2){
			 View rowView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
			 TextView textView = (TextView) rowView.findViewById(android.R.id.text1);
			 textView.setText(values[position]);
			 TextView textView2 = (TextView) rowView.findViewById(android.R.id.text2);
			 textView2.setText(""+intervaltime/60000 + " min");
			 return rowView;
		 }
		 else if (position==4){
			 View rowView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
			 TextView textView = (TextView) rowView.findViewById(android.R.id.text1);
			 textView.setText(values[position]);
			 TextView textView2 = (TextView) rowView.findViewById(android.R.id.text2);
			 textView2.setText("name");
			 return rowView;
		 }
		 else if (position==5){
			 View rowView = inflater.inflate(R.layout.checkboxlist, parent, false);
			 CheckBox textView = (CheckBox) rowView.findViewById(R.id.check1);
			 textView.setText(values[position]);
			 return rowView;
		 }
		 else {
			 View rowView = inflater.inflate(R.layout.textviewlist, parent, false);
			 TextView textView = (TextView) rowView.findViewById(R.id.textView1);
			 textView.setText(values[position]);
			 return rowView;
		 }
	}
	/*
	@Override
	public void getItemId(int position) {
		String item = getItem(position);
		//return mIdMap.get(item);
	}
	*/

	@Override
	public boolean hasStableIds() {
		return true;
	}	
}