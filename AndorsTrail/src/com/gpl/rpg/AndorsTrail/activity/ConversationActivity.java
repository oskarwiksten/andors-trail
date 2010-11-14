package com.gpl.rpg.AndorsTrail.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.gpl.rpg.AndorsTrail.controller.ConversationController;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.conversation.Phrase;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reply;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.model.actor.Player;

public final class ConversationActivity extends Activity {
	private WorldContext world;
	private Player player;
	
	private TextView text;
	private Button reply1;
	private Button reply2;
	private Button reply3;
	private MonsterType monsterType;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        this.world = app.world;
        this.player = world.model.player;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Uri uri = getIntent().getData();
        final int monsterTypeID = Integer.parseInt(uri.getQueryParameter("monsterTypeID"));
        
        monsterType = world.monsterTypes.getMonsterType(monsterTypeID);
        
        setContentView(R.layout.conversation);

        ImageView iw = (ImageView) findViewById(R.id.conversation_image);
        iw.setImageBitmap(world.tileStore.bitmaps[monsterType.iconID]);
        TextView tv = (TextView) findViewById(R.id.conversation_title);
        tv.setText(getResources().getString(R.string.conversation_title, monsterType.name));
        
        text = (TextView) findViewById(R.id.conversation_text);
        reply1 = (Button) findViewById(R.id.conversation_reply1);
        reply2 = (Button) findViewById(R.id.conversation_reply2);
        reply3 = (Button) findViewById(R.id.conversation_reply3);
        
        setPhrase(uri.getLastPathSegment().toString());
    }
    
    public void setPhrase(String phraseID) {
    	if (phraseID.equalsIgnoreCase(ConversationCollection.PHRASE_CLOSE)) {
    		ConversationActivity.this.finish();
    		return;
    	} else if (phraseID.equalsIgnoreCase(ConversationCollection.PHRASE_SHOP)) {
    		assert(monsterType.dropList != null);
    		Intent intent = new Intent(this, ShopActivity.class);
    		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/shop/" + monsterType.id));
    		startActivityForResult(intent, MainActivity.INTENTREQUEST_SHOP);
    		return;
    	}
    	
    	final Phrase phrase = world.conversations.getPhrase(phraseID);
    	ConversationController.applyPhraseEffect(player, phrase);
    	
    	if (phrase.message == null || phrase.message.length() <= 0) {
    		for (Reply r : phrase.replies) {
    			if (!ConversationController.canSelectReply(player, r)) continue;
    			setPhrase(r.nextPhrase);
    			return;
    		}
    	}
    	
    	String message = phrase.message;
    	if (phrase.rewardGold > 0 || phrase.rewardExperience > 0) {
    		message += "\n";
	    	if (phrase.rewardExperience > 0) {
	    		message += "\n" + getResources().getString(R.string.conversation_rewardexp, phrase.rewardExperience);
	    	}
	    	if (phrase.rewardGold > 0) {
	    		message += "\n" + getResources().getString(R.string.conversation_rewardgold, phrase.rewardGold);
	    	}
    	}

    	text.setText(message);
    	handleReply(phrase, 0, reply1);
    	handleReply(phrase, 1, reply2);
    	handleReply(phrase, 2, reply3);
    }
    
    private void handleReply(Phrase p, int replyIndex, Button b) {
    	if (p.replies.length <= replyIndex) {
    		b.setVisibility(View.GONE);
    		return;
    	} 

    	final Reply r = p.replies[replyIndex];
		if (!ConversationController.canSelectReply(player, r)) {
			b.setVisibility(View.GONE);
			return;
    	} 

		b.setVisibility(View.VISIBLE);
		b.setText(r.text);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ConversationController.applyReplyEffect(player, r);
				ConversationActivity.this.setPhrase(r.nextPhrase);
			}
        });
    }
    	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MainActivity.INTENTREQUEST_SHOP:
			ConversationActivity.this.finish();
			break;
		}
	}
}
