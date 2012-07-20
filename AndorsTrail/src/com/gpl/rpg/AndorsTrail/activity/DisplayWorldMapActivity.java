package com.gpl.rpg.AndorsTrail.activity;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.WorldMapController;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.L;

public class DisplayWorldMapActivity extends Activity {
	private WorldContext world;
	
	private WebView displayworldmap_webview;
	
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
        displayworldmap_webview.getSettings().setJavaScriptEnabled(true);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        update();
    }
	
	private void update() {
		File worldmap = WorldMapController.getCombinedWorldMapFile();
		
		if (!worldmap.exists()) {
			Toast.makeText(this, getResources().getString(R.string.menu_button_worldmap_failed), Toast.LENGTH_LONG).show();
			this.finish();
		}

		String url = "file://" + worldmap.getAbsolutePath();
		Coord playerWorldPosition = WorldMapController.getPlayerWorldPosition(world);
		if (playerWorldPosition != null) {
			url += "?" 
				+ playerWorldPosition.x * WorldMapController.WORLDMAP_DISPLAY_TILESIZE 
				+ "," 
				+ (playerWorldPosition.y-1) * WorldMapController.WORLDMAP_DISPLAY_TILESIZE;
		}
		L.log("Showing " + url);
		displayworldmap_webview.loadUrl(url);
	}
}
