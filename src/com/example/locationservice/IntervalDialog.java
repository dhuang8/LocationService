package com.example.locationservice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;


public class IntervalDialog extends DialogFragment{
 //   public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.intervaldialog, null);
        builder.setView(view);
        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker1);
        numberPicker.setMaxValue(60);
        numberPicker.setMinValue(1);
        numberPicker.setValue(10);
        numberPicker.setWrapSelectorWheel(true);
        builder.setMessage("Set update interval")
               .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       ((MainActivity) getActivity()).setInterval(numberPicker.getValue()*60000+1);
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
