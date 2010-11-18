package com.gpl.rpg.AndorsTrail.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.Controller;
import com.gpl.rpg.AndorsTrail.model.actor.Player;

public final class LevelUpActivity extends Activity {
	private WorldContext world;
	private Player player;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        this.world = app.world;
        this.player = world.model.player;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.levelup);
    	final Resources res = getResources();
    	
        ImageView img = (ImageView) findViewById(R.id.levelup_image);
        img.setImageBitmap(world.tileStore.bitmaps[player.traits.iconID]);
        TextView tv = (TextView) findViewById(R.id.levelup_description);
        tv.setText(res.getString(R.string.levelup_description, player.level+1));
        
        /*
        ArrayList<LevelUpSelection> items = new ArrayList<LevelUpSelection>();
        items.add(
    		new LevelUpSelection(
        		SELECT_HEALTH
        		, res.getString(R.string.levelup_add_health)
        		, res.getString(R.string.levelup_add_health_description, LEVELUP_EFFECT_HEALTH)
        	)
        );
        items.add(
    		new LevelUpSelection(
				SELECT_ATK_CH
        		, res.getString(R.string.levelup_add_attackchance)
        		, res.getString(R.string.levelup_add_attackchance_description, LEVELUP_EFFECT_ATK_CH)
        	)
        );
        items.add(
    		new LevelUpSelection(
				SELECT_ATK_DMG
        		, res.getString(R.string.levelup_add_attackdamage)
        		, res.getString(R.string.levelup_add_attackdamage_description, LEVELUP_EFFECT_ATK_DMG)
        	)
        );
        items.add(
    		new LevelUpSelection(
    				SELECT_DEF_CH
        		, res.getString(R.string.levelup_add_blockchance)
        		, res.getString(R.string.levelup_add_blockchance_description, LEVELUP_EFFECT_DEF_CH)
        	)
        );
        
        ListView lv = (ListView) findViewById(R.id.levelup_list);
        lv.setAdapter(new LevelUpSelectionAdapter(this, items));
        */
        
        ((Button) findViewById(R.id.levelup_add_health)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				levelup(SELECT_HEALTH);
			}
		});
        ((Button) findViewById(R.id.levelup_add_attackchance)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				levelup(SELECT_ATK_CH);
			}
		});
        ((Button) findViewById(R.id.levelup_add_attackdamage)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				levelup(SELECT_ATK_DMG);
			}
		});
        ((Button) findViewById(R.id.levelup_add_blockchance)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				levelup(SELECT_DEF_CH);
			}
		});
    }

    private static final int SELECT_HEALTH = 0;
    private static final int SELECT_ATK_CH = 1;
    private static final int SELECT_ATK_DMG = 2;
    private static final int SELECT_DEF_CH = 3;
    
    public void levelup(int selectionID) {
    	addLevelupEffect(player, selectionID);
    	LevelUpActivity.this.finish();
    }
    
    private static void addLevelupEffect(Player player, int selectionID) {
    	switch (selectionID) {
    	case SELECT_HEALTH:
    		player.health.max += Controller.LEVELUP_EFFECT_HEALTH;
    		player.traits.maxHP += Controller.LEVELUP_EFFECT_HEALTH;
    		player.health.current += Controller.LEVELUP_EFFECT_HEALTH;
    		break;
    	case SELECT_ATK_CH:
    		player.traits.baseCombatTraits.attackChance += Controller.LEVELUP_EFFECT_ATK_CH;
    		break;
    	case SELECT_ATK_DMG:
    		player.traits.baseCombatTraits.damagePotential.max += Controller.LEVELUP_EFFECT_ATK_DMG;
    		player.traits.baseCombatTraits.damagePotential.current += Controller.LEVELUP_EFFECT_ATK_DMG;
    		break;
    	case SELECT_DEF_CH:
    		player.traits.baseCombatTraits.blockChance += Controller.LEVELUP_EFFECT_DEF_CH;
    		break;
    	}
    	player.level++;
    	player.recalculateLevelExperience();
    	player.recalculateCombatTraits();
    }
    
    /*
    private static final class LevelUpSelection {
    	public final int id;
    	public final String title;
    	public final String description;
		public LevelUpSelection(int id, String title, String description) {
			this.id = id;
			this.title = title;
			this.description = description;
		}
    }
    
    private final class LevelUpSelectionAdapter extends ArrayAdapter<LevelUpSelection> {
    	public LevelUpSelectionAdapter(Context context, List<LevelUpSelection> items) {
    		super(context, 0, items);
    	}

    	@Override
    	public View getView(final int position, View convertView, ViewGroup parent) {
    		final LevelUpSelection item = getItem(position);
    		
    		View result = convertView;
    		if (result == null) {
    			result = View.inflate(getContext(), R.layout.levelupitemview, null);
    		}
    		
    		TextView tv = (TextView) result.findViewById(R.id.levelupitem_title);
    		tv.setText(item.title);
    		//tv = (TextView) result.findViewById(R.id.levelupitem_description);
    		//tv.setText(item.description);
    		
    		((Button) result.findViewById(R.id.levelupitem_button)).setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View arg0) {
    				levelup(item.id);
    			}
    		});
    		return result;
    	}
    	
    	@Override
    	public long getItemId(int position) {
    		return getItem(position).id;
    	}
    }
*/
}
