package com.gpl.rpg.AndorsTrail.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.savegames.Savegames;
import com.gpl.rpg.AndorsTrail.savegames.Savegames.FileHeader;

import java.util.Collections;
import java.util.List;

public final class LoadSaveActivity extends Activity implements OnClickListener {
	private boolean isLoading = true;
	private static final int SLOT_NUMBER_CREATE_NEW_SLOT = -1;
	private static final int SLOT_NUMBER_FIRST_SLOT = 1;
	private ModelContainer model;
	private AndorsTrailPreferences preferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
		app.setWindowParameters(this);
		this.model = app.getWorld().model;
		this.preferences = app.getPreferences();

		String loadsave = getIntent().getData().getLastPathSegment();
		isLoading = (loadsave.equalsIgnoreCase("load"));

		setContentView(R.layout.loadsave);

		TextView tv = (TextView) findViewById(R.id.loadsave_title);
		if (isLoading) {
			tv.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_search, 0, 0, 0);
			tv.setText(R.string.loadsave_title_load);
		} else {
			tv.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_save, 0, 0, 0);
			tv.setText(R.string.loadsave_title_save);
		}

		ViewGroup slotList = (ViewGroup) findViewById(R.id.loadsave_slot_list);
		Button slotTemplateButton = (Button) findViewById(R.id.loadsave_slot_n);
		Button createNewSlot = (Button) findViewById(R.id.loadsave_save_to_new_slot);
		LayoutParams params = slotTemplateButton.getLayoutParams();
		slotList.removeView(slotTemplateButton);
		slotList.removeView(createNewSlot);

		addSavegameSlotButtons(slotList, params, Savegames.getUsedSavegameSlots());

		if (!isLoading) {
			Button b = new Button(this);
			b.setLayoutParams(params);
			b.setTag(SLOT_NUMBER_CREATE_NEW_SLOT);
			b.setOnClickListener(this);
			b.setText(R.string.loadsave_save_to_new_slot);
			slotList.addView(b, params);
		}
	}

	private void addSavegameSlotButtons(ViewGroup parent, LayoutParams params, List<Integer> usedSavegameSlots) {
		for (int slot : usedSavegameSlots) {
			final FileHeader header = Savegames.quickload(this, slot);
			if (header == null) continue;

			Button b = new Button(this);
			b.setLayoutParams(params);
			b.setTag(slot);
			b.setOnClickListener(this);
			b.setText(slot + ". " + header.describe());
			parent.addView(b, params);
		}
	}

	public void loadsave(int slot) {
		if (slot == SLOT_NUMBER_CREATE_NEW_SLOT) {
			List<Integer> usedSlots = Savegames.getUsedSavegameSlots();
			if (usedSlots.isEmpty()) slot = SLOT_NUMBER_FIRST_SLOT;
			else slot = Collections.max(usedSlots) + 1;
		}
		if (slot < SLOT_NUMBER_FIRST_SLOT) slot = SLOT_NUMBER_FIRST_SLOT;

		Intent i = new Intent();
		i.putExtra("slot", slot);
		setResult(Activity.RESULT_OK, i);
		LoadSaveActivity.this.finish();
	}

	private String getConfirmOverwriteQuestion(int slot) {
		if (isLoading) return null;
		if (slot == SLOT_NUMBER_CREATE_NEW_SLOT) return null;					// if we're creating a new slot

		if (preferences.displayOverwriteSavegame == AndorsTrailPreferences.CONFIRM_OVERWRITE_SAVEGAME_ALWAYS) {
			return getString(R.string.loadsave_save_overwrite_confirmation_all);
		}
		if (preferences.displayOverwriteSavegame == AndorsTrailPreferences.CONFIRM_OVERWRITE_SAVEGAME_NEVER) {
			return null;
		}

		final String currentPlayerName = model.player.getName();
		final FileHeader header = Savegames.quickload(this, slot);
		if (header == null) return null;

		final String savedPlayerName = header.playerName;
		if (currentPlayerName.equals(savedPlayerName)) return null;			// if the names match

		return getString(R.string.loadsave_save_overwrite_confirmation, savedPlayerName, currentPlayerName);
	}

	@Override
	public void onClick(View view) {
		final int slot = (Integer) view.getTag();
		final String message = getConfirmOverwriteQuestion(slot);

		if (message != null) {
			final String title =
				getString(R.string.loadsave_save_overwrite_confirmation_title) + ' '
				+ getString(R.string.loadsave_save_overwrite_confirmation_slot, slot);
			new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						loadsave(slot);
					}
				})
				.setNegativeButton(android.R.string.no, null)
				.show();
		} else {
			loadsave(slot);
		}
	}
}
