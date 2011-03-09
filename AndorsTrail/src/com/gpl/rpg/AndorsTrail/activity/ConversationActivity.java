package com.gpl.rpg.AndorsTrail.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.ConversationController;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.conversation.Phrase;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reply;
import com.gpl.rpg.AndorsTrail.model.actor.ActorTraits;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.resource.TileStore;

public final class ConversationActivity extends Activity {
	public static final int ACTIVITYRESULT_ATTACK = Activity.RESULT_FIRST_USER + 1;
	private static final int playerConversationColor = Color.argb(255, 0xbb, 0x22, 0x22);
	private static final int NPCConversationColor = Color.argb(255, 0xbb, 0xbb, 0x22);
	
	private WorldContext world;
	private Player player;
	private String phraseID;
	private Phrase phrase;
	private ArrayList<ConversationStatement> conversationHistory = new ArrayList<ConversationStatement>();
	private StatementContainerAdapter listAdapter;
	private Button nextButton;
	private Button leaveButton;
	private ListView statementList;
	private MonsterType monsterType;
	private RadioGroup replyGroup;
	private OnClickListener radioButtonListener;
	private boolean displayActors = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        this.world = app.world;
        this.player = world.model.player;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Uri uri = getIntent().getData();
        final int monsterTypeID = Integer.parseInt(uri.getQueryParameter("monsterTypeID"));
        if (monsterTypeID >= 0) {
        	displayActors = true;
        	monsterType = world.monsterTypes.getMonsterType(monsterTypeID);
        	assert(monsterType != null);
        } else {
        	displayActors = false;
        	monsterType = null;
        }
        String phraseID = uri.getLastPathSegment().toString(); 
        if (savedInstanceState != null) {
        	phraseID = savedInstanceState.getString("phraseID");
        	conversationHistory = savedInstanceState.getParcelableArrayList("conversationHistory");
        	if (conversationHistory == null) conversationHistory = new ArrayList<ConversationStatement>();
        	
        	// Remove the last item since it will be re-added inside setPhrase()
        	int lastIndex = conversationHistory.size() - 1;
        	if (lastIndex >= 0) {
        		conversationHistory.remove(lastIndex);
        	}
        }
		
        setContentView(R.layout.conversation);

        replyGroup = new RadioGroup(this);
        
        statementList = (ListView) findViewById(R.id.conversation_statements);
        statementList.addFooterView(replyGroup);
        listAdapter = new StatementContainerAdapter(this, conversationHistory, world.tileStore);
        statementList.setAdapter(listAdapter);
        
        nextButton = (Button) findViewById(R.id.conversation_next);
        leaveButton = (Button) findViewById(R.id.conversation_leave);
        leaveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ConversationActivity.this.finish();
			}
		});
        
        radioButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				nextButton.setEnabled(true);
			}
		};
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nextButtonClicked();
			}
		});
		
        setPhrase(phraseID);
    }
    
	
    private void markMonsterAsAgressive() {
    	Monster m = world.model.currentMap.getMonsterAt(world.model.player.nextPosition);
    	assert (m != null);
		assert (m.monsterType.id == monsterType.id);
    	m.forceAggressive = true;
    }
    
	public void setPhrase(String phraseID) {
		this.phraseID = phraseID;
    	if (phraseID.equalsIgnoreCase(ConversationCollection.PHRASE_CLOSE)) {
    		ConversationActivity.this.finish();
    		return;
    	} else if (phraseID.equalsIgnoreCase(ConversationCollection.PHRASE_SHOP)) {
    		assert(monsterType != null);
    		assert(monsterType.dropList != null);
    		Intent intent = new Intent(this, ShopActivity.class);
    		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/shop/" + monsterType.id));
    		startActivityForResult(intent, MainActivity.INTENTREQUEST_SHOP);
    		return;
    	} else if (phraseID.equalsIgnoreCase(ConversationCollection.PHRASE_ATTACK)) {
    		markMonsterAsAgressive();
			ConversationActivity.this.setResult(ACTIVITYRESULT_ATTACK);
    		ConversationActivity.this.finish();
    		return;
    	}
    	
    	phrase = world.conversations.getPhrase(phraseID);
    	Loot loot = ConversationController.applyPhraseEffect(player, phrase, world.quests);
    	
    	if (phrase.message == null || phrase.message.length() <= 0) {
    		for (Reply r : phrase.replies) {
    			if (!ConversationController.canSelectReply(player, r)) continue;
    			setPhrase(r.nextPhrase);
    			return;
    		}
    	}
    	
    	String message = phrase.message;
    	if (loot != null && loot.hasItemsOrExp()) {
    		message += "\n";
	    	if (loot.exp > 0) {
	    		message += "\n" + getResources().getString(R.string.conversation_rewardexp, loot.exp);
	    	}
	    	if (loot.gold > 0) {
	    		message += "\n" + getResources().getString(R.string.conversation_rewardgold, loot.gold);
	    	} else if (loot.gold < 0) {
	    		message += "\n" + getResources().getString(R.string.conversation_lostgold, -loot.gold);
	    	}
	    	if (!loot.items.isEmpty()) {
	    		final int len = loot.items.countItems();
	    		if (len == 1) {
	    			message += "\n" + getResources().getString(R.string.conversation_rewarditem);
	    		} else {
	    			message += "\n" + getResources().getString(R.string.conversation_rewarditems, len);
	    		}
	    	}
    	}

    	addConversationStatement(monsterType, message, NPCConversationColor);
    	
    	if (isPhraseOnlyNextReply(phrase)) {
    		nextButton.setEnabled(true);
    	} else {
			replyGroup.removeAllViews();
    		for (Reply r : phrase.replies) {
	    		addReply(phrase, r);
	    	}
	    	replyGroup.setVisibility(View.VISIBLE);
    		nextButton.setEnabled(false);
    	}
    }
    
	private void addReply(final Phrase p, final Reply r) {
		if (!ConversationController.canSelectReply(player, r)) return;

		RadioButton rb = new RadioButton(this);
		rb.setText(r.text);
		rb.setOnClickListener(radioButtonListener);
		rb.setTag(r);
		replyGroup.addView(rb);
    }
	
	private static boolean isPhraseOnlyNextReply(Phrase p) {
		if (p.replies.length != 1) return false;
		if (p.replies[0].text.equals(ConversationCollection.REPLY_NEXT)) return true;
		return false;
	}
	
	private Reply getSelectedReply() {
		for (int i = 0; i < phrase.replies.length; ++i) {
			final View v = replyGroup.getChildAt(i);
			if (v == null) continue;
			final RadioButton rb = (RadioButton) v;
			if (rb.isChecked()) {
				return (Reply) rb.getTag();
			}
		}
		return null; // No reply was found. This is probably an error.
	}
	
	private void nextButtonClicked() {
		replyGroup.setVisibility(View.GONE);
		
		Reply r;
		if (isPhraseOnlyNextReply(phrase)) {
			// If there is only a "Next" as reply, we don't need to add it to the conversation history.
			r = phrase.replies[0];
		} else {
			r = getSelectedReply();
			if (r == null) return;
			addConversationStatement(player.traits, r.text, playerConversationColor);
		}
		replyGroup.removeAllViews();
		
		ConversationController.applyReplyEffect(player, r);
		setPhrase(r.nextPhrase);
	}

	private void addConversationStatement(ActorTraits traits, String text, int color) {
    	ConversationStatement s = new ConversationStatement();
    	if (displayActors) {
    		assert(traits != null);
	    	s.iconID = traits.iconID;
	    	s.actorName = traits.name;
    	} else {
    		s.iconID = ConversationStatement.NO_ICON;
    	}
    	s.text = text;
    	s.color = color;
    	conversationHistory.add(s);
		listAdapter.notifyDataSetChanged();
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
    
    @Override
	public void onSaveInstanceState(Bundle outState) {
    	outState.putString("phraseID", phraseID);
    	outState.putParcelableArrayList("conversationHistory", conversationHistory);
    }

	private static final class ConversationStatement implements Parcelable {
		public static final int NO_ICON = -1;
		
		public String actorName;
		public String text;
		public int iconID;
		public int color;
		
		public boolean hasActor() {
			return iconID != NO_ICON;
		}

		@Override
		public int describeContents() { return 0; }

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(actorName);
			dest.writeString(text);
			dest.writeInt(iconID);
			dest.writeInt(color);
		}
	}

    private static final class StatementContainerAdapter extends ArrayAdapter<ConversationStatement> {

    	private final TileStore tileStore;
    	
		public StatementContainerAdapter(Context context, ArrayList<ConversationStatement> items, TileStore tileStore) {
			super(context, 0, items);
			this.tileStore = tileStore;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ConversationStatement statement = getItem(position);
			
			View result = convertView;
			if (result == null) {
				result = View.inflate(getContext(), R.layout.conversation_statement, null);
			}
			
			final ImageView iv = (ImageView) result.findViewById(R.id.conversation_image);
			final TextView tv = (TextView) result.findViewById(R.id.conversation_text);
	        if (statement.hasActor()) {
				iv.setImageBitmap(tileStore.getBitmap(statement.iconID));
				iv.setVisibility(View.VISIBLE);
				
	    		tv.setText(statement.actorName + ": " + statement.text, BufferType.SPANNABLE);
		        Spannable sp = (Spannable) tv.getText();
		        sp.setSpan(new ForegroundColorSpan(statement.color), 0, statement.actorName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        } else {
	        	iv.setVisibility(View.GONE);
	    		tv.setText(statement.text);
		    }
			
			return result;
		}
    }
}
