package com.gpl.rpg.AndorsTrail.activity;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.WorldMapController;
import com.gpl.rpg.AndorsTrail.model.map.WorldMapSegment;
import com.gpl.rpg.AndorsTrail.model.map.WorldMapSegment.WorldMapSegmentMap;
import com.gpl.rpg.AndorsTrail.util.L;

public class DisplayWorldMapActivity extends Activity {
	private WorldContext world;
	
	private WebView displayworldmap_webview;
	private String worldMapSegmentName;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        if (!app.isInitialized()) { finish(); return; }
        this.world = app.world;
        
        AndorsTrailApplication.setWindowParameters(this, app.preferences);
        
        setContentView(R.layout.displayworldmap);
        
        displayworldmap_webview = (WebView) findViewById(R.id.displayworldmap_webview);
        displayworldmap_webview.setBackgroundColor(getResources().getColor(android.R.color.black));
        displayworldmap_webview.getSettings().setBuiltInZoomControls(true);
        enableJavascript();
        
        worldMapSegmentName = getIntent().getStringExtra("worldMapSegmentName");
    }
    
    @SuppressLint("SetJavaScriptEnabled")
	public void enableJavascript() {
    	displayworldmap_webview.getSettings().setJavaScriptEnabled(true);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        update();
    }
	
	private void update() {
		File worldmap = WorldMapController.getCombinedWorldMapFile(worldMapSegmentName);
		
		if (!worldmap.exists()) {
			Toast.makeText(this, getResources().getString(R.string.menu_button_worldmap_failed), Toast.LENGTH_LONG).show();
			this.finish();
		}
		
		WorldMapSegment segment = world.maps.worldMapSegments.get(worldMapSegmentName);
		WorldMapSegmentMap map = segment.maps.get(world.model.currentMap.name);
		if (map == null) this.finish();

		String url = "file://" + worldmap.getAbsolutePath() + "?"
				+ (world.model.player.position.x + map.worldPosition.x) * WorldMapController.WORLDMAP_DISPLAY_TILESIZE 
				+ "," 
				+ (world.model.player.position.y + map.worldPosition.y-1) * WorldMapController.WORLDMAP_DISPLAY_TILESIZE;
		L.log("Showing " + url);
		displayworldmap_webview.loadUrl(url);
	}
}
