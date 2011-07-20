package com.gpl.rpg.AndorsTrail.activity;

import java.util.Collections;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.Savegames;
import com.gpl.rpg.AndorsTrail.Savegames.FileHeader;

public final class LoadSaveActivity extends Activity implements OnClickListener {
	private boolean isLoading = true;
	private static final int SLOT_NUMBER_CREATE_NEW_SLOT = -1;
	private static final int SLOT_NUMBER_FIRST_SLOT = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        String loadsave = getIntent().getData().getLastPathSegment().toString();
        isLoading = (loadsave.equalsIgnoreCase("load"));
                
        setContentView(R.layout.loadsave);
    	
        ImageView img = (ImageView) findViewById(R.id.loadsave_image);
        TextView tv = (TextView) findViewById(R.id.loadsave_title);
        if (isLoading) {
        	img.setImageResource(android.R.drawable.ic_menu_search);
        	tv.setText(R.string.loadsave_title_load);
        } else {
        	img.setImageResource(android.R.drawable.ic_menu_save);
        	tv.setText(R.string.loadsave_title_save);
        }
        
        ViewGroup slotList = (ViewGroup) findViewById(R.id.loadsave_slot_list);
        Button slotTemplateButton = (Button) findViewById(R.id.loadsave_slot_n);
        Button createNewSlot = (Button) findViewById(R.id.loadsave_save_to_new_slot);
        LayoutParams params = slotTemplateButton.getLayoutParams();
        slotList.removeView(slotTemplateButton);
        slotList.removeView(createNewSlot);
        
        addSavegameSlotButtons(slotList, params, Savegames.getUsedSavegameSlots(this));
        
        if (!isLoading) {
        	Button b = new Button(this);
			b.setLayoutParams(params);
			b.setTag(SLOT_NUMBER_CREATE_NEW_SLOT);
	    	b.setOnClickListener(this);
	        b.setText(R.string.loadsave_save_to_new_slot);
	        slotList.addView(b, params);
        }
    }
    
	private void addSavegameSlotButtons(ViewGroup parent, LayoutParams params, Set<Integer> usedSavegameSlots) {
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
			Set<Integer> usedSlots = Savegames.getUsedSavegameSlots(this);
			if (usedSlots.isEmpty()) slot = SLOT_NUMBER_FIRST_SLOT;
			else slot = Collections.max(usedSlots) + 1;
		}
		if (slot < SLOT_NUMBER_FIRST_SLOT) slot = SLOT_NUMBER_FIRST_SLOT;

		Intent i = new Intent();
    	i.putExtra("slot", slot);
    	setResult(Activity.RESULT_OK, i);
    	LoadSaveActivity.this.finish();
    }

	@Override
	public void onClick(View view) {
		loadsave((Integer) view.getTag());
	}
}
