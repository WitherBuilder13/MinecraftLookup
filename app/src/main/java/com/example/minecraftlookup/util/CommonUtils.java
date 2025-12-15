package com.example.minecraftlookup.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minecraftlookup.activities.MainActivity;
import com.example.minecraftlookup.objects.MCObject;
import com.example.minecraftlookup.objects.Toggleable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommonUtils {

    public static String DUMMY_USER = "";

    public static void clearEditTexts(EditText... editTexts) {
        for (EditText editText : editTexts)
            editText.setText("");
    }

    public static void resetCheckboxes(CheckBox... checkBoxes) {
        for (CheckBox checkBox : checkBoxes)
            checkBox.setChecked(true);
    }

    public static <T> ArrayList<T> getAllToggleEntries(ArrayList<Toggleable<T>> tAL, boolean toggle) {
        ArrayList<T> toggleEntries = new ArrayList<>();

        for (Toggleable<T> t : tAL)
            if (t.isToggled() == toggle)
                toggleEntries.add(t.getEntry());

        return toggleEntries;
    }

    public static ArrayList<Toggleable<MCObject>> getCheckedList(ArrayList<MCObject> objectList, ArrayList<Toggleable<MCObject>> checkedList) {
        ArrayList<Toggleable<MCObject>> finalList = new ArrayList<>();
        Map<Integer, Boolean> toggleMap = new HashMap<>();

        // Store all objectIDs and their corresponding toggles from 'checkedList'
        for (Toggleable<MCObject> toggleObject : checkedList)
            toggleMap.put(toggleObject.getEntry().getId(), toggleObject.isToggled());

        // Loop through every 'object' in 'objectList' and assign it either its toggle from 'toggleMap', or false if a matching id is not found in 'toggleMap'
        for (MCObject object : objectList) {
            boolean toggled = toggleMap.getOrDefault(object.getId(), false);
            finalList.add(new Toggleable<>(object, toggled));
        }

        return finalList;
    }

    public static <T> ArrayList<Toggleable<T>> createCheckedList(ArrayList<T> arrayList, boolean checked) {
        ArrayList<Toggleable<T>> checkboxList = new ArrayList<>();

        for (T entry : arrayList) {
            Toggleable<T> t = new Toggleable<>(entry, checked);
            checkboxList.add(t);
        }

        return checkboxList;
    }

    public static <T> ArrayList<Toggleable<T>> createCheckedListFromCheckbox(ArrayList<T> arrayList, CheckBox cb) {
        ArrayList<Toggleable<T>> checkboxList = new ArrayList<>();

        for (T entry : arrayList) {
            Toggleable<T> t = new Toggleable<>(entry, cb.isChecked());
            checkboxList.add(t);
        }

        return checkboxList;
    }

    public static  <T> void resetCheckboxAdapter(BaseAdapter adapter, ArrayList<T> adapterList, ArrayList<T> cbList) {
        resetCheckboxAdapter(adapter, adapterList, cbList, false);
    }

    public static  <T> void resetCheckboxAdapter(BaseAdapter adapter, ArrayList<T> adapterList, ArrayList<T> cbList, boolean contributorAdapter) {
        adapterList.clear();
        adapterList.addAll(cbList);

        if (contributorAdapter)
            adapterList.remove(0);

        adapter.notifyDataSetChanged();
    }

    public static void hideErrors(TextView... errors) {
        for (TextView error : errors)
            error.setVisibility(View.INVISIBLE);
    }

    public static void showError(TextView error) {
        error.setVisibility(View.VISIBLE);
    }

    public static View viewSetup(View view, Context context, int RLayoutResource) {
        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(RLayoutResource, null);
        }
        return view;
    }
}
