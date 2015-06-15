/*
 * MaxLock, an Xposed applock module for Android
 * Copyright (C) 2014-2015  Maxr1998
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.Maxr1998.xposed.hellscode;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class HellsCodeSetupDialog extends DialogFragment implements View.OnClickListener {

    ViewGroup rootView;
    private String mFirstKey;
    private SharedPreferences prefs;
    private StringBuilder key;
    private String mUiStage;
    private TextView mDescView;
    private ProgressBar bar;
    private Button[] buttons;

    @SuppressLint("WorldReadableFiles")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // Prefs
        //noinspection deprecation
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_WORLD_READABLE);

        // Strings
        key = new StringBuilder("");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Views
        rootView = (ViewGroup) inflater.inflate(R.layout.dialog_hells_code_setup, container, false);
        mDescView = (TextView) rootView.findViewById(R.id.description);
        buttons = new Button[]{
                (Button) rootView.findViewById(R.id.button_1),
                (Button) rootView.findViewById(R.id.button_2),
                (Button) rootView.findViewById(R.id.button_3),
                (Button) rootView.findViewById(R.id.button_4)
        };
        for (Button button : buttons) {
            button.setOnClickListener(this);
        }
        bar = (ProgressBar) rootView.findViewById(R.id.progress);

        if (prefs.getString("KEY", null) != null) {
            mUiStage = "ask";
        } else {
            mUiStage = "first";
            mDescView.setText(R.string.enter_code);
        }
        updateUi();

        return rootView;
    }

    public void onClick(View v) {
        int nr = 0;
        boolean knockButton = false;
        switch (v.getId()) {
            case R.id.button_1:
                nr = 1;
                knockButton = true;
                break;
            case R.id.button_2:
                nr = 2;
                knockButton = true;
                break;
            case R.id.button_3:
                nr = 3;
                knockButton = true;
                break;
            case R.id.button_4:
                nr = 4;
                knockButton = true;
                break;
        }
        if (knockButton) key.append(nr);
        updateUi();
    }

    public void updateUi() {
        bar.setProgress(key.length());
        if ((mUiStage.equals("ask") || mUiStage.equals("first")) && key.length() == 4) {
            View reveal = rootView.findViewById(R.id.reveal);
            Helpers.animateView(reveal);
            nextStage();
        } else if (mUiStage.equals("second")) {
            if (!key.toString().equals(mFirstKey.substring(0, key.length()))) {
                bar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.progress_no_match)));
                setButtonsState(false);
                bar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        key.setLength(key.length() - 1);
                        updateUi();
                    }
                }, 1000);
            } else {
                if (key.length() == 4) {
                    nextStage();
                    return;
                }
                bar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.progress_ok)));
                setButtonsState(true);
            }
        }
    }

    public void nextStage() {
        switch (mUiStage) {
            case "ask":
                if (Helpers.shaHash(key.toString()).equals(prefs.getString("KEY", "EMPTY"))) {
                    key.setLength(0);
                    mUiStage = "first";
                    mDescView.setText(R.string.enter_code);
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(getActivity().getSupportFragmentManager().findFragmentByTag("dialog")).commit();
                    Toast.makeText(getActivity(), "Error, PIN incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case "first":
                mFirstKey = key.toString();
                key.setLength(0);
                mUiStage = "second";
                mDescView.setText(R.string.retype_code);
                break;
            case "second":
                prefs.edit().putString("KEY", Helpers.shaHash(key.toString())).apply();
                getActivity().getSupportFragmentManager().beginTransaction().remove(getActivity().getSupportFragmentManager().findFragmentByTag("dialog")).commit();
                Toast.makeText(getActivity(), "Done.", Toast.LENGTH_SHORT).show();
                return;
        }
        updateUi();
    }

    public void setButtonsState(boolean enabled) {
        for (Button b : buttons) {
            b.setEnabled(enabled);
        }
    }
}