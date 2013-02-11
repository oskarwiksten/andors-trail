package com.gpl.rpg.AndorsTrail.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.view.ItemEffectsView;

import java.util.Collections;

public final class ItemInfoActivity extends Activity {
	
	public static int ITEMACTION_NONE = 1;
	public static int ITEMACTION_USE = 2;
	public static int ITEMACTION_EQUIP = 3;
	public static int ITEMACTION_UNEQUIP = 4;
	public static int ITEMACTION_BUY = 5;
	public static int ITEMACTION_SELL = 6;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        if (!app.isInitialized()) { finish(); return; }
		final WorldContext world = app.getWorld();
        
        app.setWindowParameters(this);
        
        final Intent intent = getIntent();
        Bundle params = intent.getExtras();
        String itemTypeID = params.getString("itemTypeID");
        final ItemType itemType = world.itemTypes.getItemType(itemTypeID);
        
        final String buttonText = params.getString("buttonText");
        boolean buttonEnabled = params.getBoolean("buttonEnabled");
        
        setContentView(R.layout.iteminfo);

        TextView tv = (TextView) findViewById(R.id.iteminfo_title);
        tv.setText(itemType.getName(world.model.player));
        world.tileManager.setImageViewTileForSingleItemType(tv, itemType, getResources());

        tv = (TextView) findViewById(R.id.iteminfo_description);
        String description = itemType.getDescription();
        if (description != null) {
            tv.setText(description);
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }

        tv = (TextView) findViewById(R.id.iteminfo_category);
        tv.setText(itemType.category.displayName);
        
        ((ItemEffectsView) findViewById(R.id.iteminfo_effects)).update(
        		itemType.effects_equip,
        		itemType.effects_use == null ? null : Collections.singletonList(itemType.effects_use),
				itemType.effects_hit == null ? null : Collections.singletonList(itemType.effects_hit),
				itemType.effects_kill == null ? null : Collections.singletonList(itemType.effects_kill),
				itemType.isWeapon()
    		);
        
        Button b = (Button) findViewById(R.id.iteminfo_close);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_CANCELED);
				ItemInfoActivity.this.finish();
			}
		});
        
        b = (Button) findViewById(R.id.iteminfo_action);
        if (buttonText != null && buttonText.length() > 0) {
        	b.setVisibility(View.VISIBLE);
        	b.setEnabled(buttonEnabled);
        	b.setText(buttonText);
        } else {
        	b.setVisibility(View.GONE);
        }
        
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent result = new Intent();
				result.putExtras(intent);
				setResult(RESULT_OK, result);
				ItemInfoActivity.this.finish();
			}
		});
        
        tv = (TextView) findViewById(R.id.iteminfo_displaytype);
        if (itemType.isOrdinaryItem()) {
        	tv.setVisibility(View.GONE);
        } else {
        	tv.setVisibility(View.VISIBLE);
        	final String diplayType = getDisplayTypeString(getResources(), itemType);
        	tv.setText(diplayType);
        }
    }
    
    public static String getDisplayTypeString(Resources res, ItemType itemType) {
    	return res.getStringArray(R.array.iteminfo_displaytypes)[itemType.displayType];
    }
}
