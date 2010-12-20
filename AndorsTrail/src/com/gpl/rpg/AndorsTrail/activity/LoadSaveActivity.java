package com.gpl.rpg.AndorsTrail.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.Savegames;
import com.gpl.rpg.AndorsTrail.Savegames.FileHeader;

public final class LoadSaveActivity extends Activity {
	private boolean isLoading = true;
	
    /** Called when the activity is first created. */
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
        
        handleSlotButton(R.id.loadsave_slot_1, 1);
        handleSlotButton(R.id.loadsave_slot_2, 2);
        handleSlotButton(R.id.loadsave_slot_3, 3);
        handleSlotButton(R.id.loadsave_slot_4, 4);
    }
    
    private void handleSlotButton(int resId, final int slot) {
    	final Button b = (Button) findViewById(resId);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				loadsave(slot);
			}
		});
        final FileHeader header = Savegames.quickload(this, slot);
        final boolean exists = (header != null);
        if (isLoading) {
        	b.setEnabled(exists);
        } else {
        	b.setEnabled(true);
        }
        String s = slot + ". ";
        if (header != null) {
        	s += header.describe();
        } else {
        	s += getString(R.string.loadsave_slot_empty);
        }
        b.setText(s);
	}
    
	public void loadsave(int slot) {
    	Intent i = new Intent();
    	i.putExtra("slot", slot);
    	setResult(Activity.RESULT_OK, i);
    	LoadSaveActivity.this.finish();
    }
}
