package com.gpl.rpg.AndorsTrail.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.BufferType;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.ConversationController;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.conversation.Phrase;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reply;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;

public final class ConversationActivity extends Activity implements OnKeyListener {
	public static final int ACTIVITYRESULT_ATTACK = Activity.RESULT_FIRST_USER + 1;
	public static final int ACTIVITYRESULT_REMOVE = Activity.RESULT_FIRST_USER + 2;
	private static final int playerConversationColor = Color.argb(255, 0xbb, 0x22, 0x22);
	private static final int NPCConversationColor = Color.argb(255, 0xbb, 0xbb, 0x22);
	
	private WorldContext world;
	private ViewContext view;
	private Player player;
	private String phraseID;
	private Phrase phrase;
	private ArrayList<ConversationStatement> conversationHistory = new ArrayList<ConversationStatement>();
	private StatementContainerAdapter listAdapter;
	private Button nextButton;
    private ListView statementList;
	private Monster npc;
	private RadioGroup replyGroup;
	private OnCheckedChangeListener radioButtonListener;
	private boolean displayActors = true;
	private boolean applyPhraseRewards = true;
	private boolean hasResumed = false;
	
	private final ConversationCollection conversationCollection = new ConversationCollection();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        if (!app.isInitialized()) { finish(); return; }
        this.world = app.getWorld();
        this.view = app.getViewContext();
        this.player = world.model.player;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Uri uri = getIntent().getData();
        this.npc = Dialogs.getMonsterFromIntent(getIntent(), world);
        displayActors = (npc != null);

        phraseID = uri.getLastPathSegment();
        if (savedInstanceState != null) {
        	applyPhraseRewards = false;
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
        replyGroup.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
        
        statementList = (ListView) findViewById(R.id.conversation_statements);
        statementList.addFooterView(replyGroup);
        listAdapter = new StatementContainerAdapter(this, conversationHistory, world.tileManager);
        statementList.setAdapter(listAdapter);
        
        nextButton = (Button) findViewById(R.id.conversation_next);
        Button leaveButton = (Button) findViewById(R.id.conversation_leave);
        leaveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversationActivity.this.finish();
            }
        });
        
        radioButtonListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				nextButton.setEnabled(true);
			}
		};
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nextButtonClicked();
			}
		});
        
        statementList.setOnKeyListener(this);
        
    	statementList.setSelected(false);
    	statementList.setFocusable(false);
    	statementList.setFocusableInTouchMode(false);
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		if (!hasResumed) {
			setPhrase(phraseID);
			hasResumed = true;
		}
        applyPhraseRewards = true;
    	nextButton.requestFocus();
	}

	private int getSelectedReplyIndex() {
    	for (int i = 0; i < phrase.replies.length; ++i) {
			final View v = replyGroup.getChildAt(i);
			if (v == null) continue;
			final RadioButton rb = (RadioButton) v;
			if (rb.isChecked()) return i;
    	}
    	return -1;
    }
    
    private void setSelectedReplyIndex(int i) {
    	if (phrase.replies == null) return;
    	if (phrase.replies.length <= 0) return;
    	if (i < 0) i = 0;
    	else if (i >= phrase.replies.length) i = phrase.replies.length - 1;
    	
    	View v = replyGroup.getChildAt(i);
		if (v == null) return;
		RadioButton rb = (RadioButton) v;
		rb.setChecked(true);
    }
 
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (handleKeypress(keyCode, event)) return true;
		else return super.onKeyDown(keyCode, event);
    }
    
	@Override
	public boolean onKey(View arg0, int keyCode, KeyEvent event) {
		if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
		return handleKeypress(keyCode, event);
	}

	public boolean handleKeypress(int keyCode, KeyEvent event) {
		int selectedReplyIndex = getSelectedReplyIndex();
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			--selectedReplyIndex;
			setSelectedReplyIndex(selectedReplyIndex);
			return true;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			++selectedReplyIndex;
			setSelectedReplyIndex(selectedReplyIndex);
			return true;
		case KeyEvent.KEYCODE_SPACE:
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if (nextButton.isEnabled()) nextButton.performClick();
			return true;
		case KeyEvent.KEYCODE_1: setSelectedReplyIndex(0); return true;
		case KeyEvent.KEYCODE_2: setSelectedReplyIndex(1); return true;
		case KeyEvent.KEYCODE_3: setSelectedReplyIndex(2); return true;
		case KeyEvent.KEYCODE_4: setSelectedReplyIndex(3); return true;
		case KeyEvent.KEYCODE_5: setSelectedReplyIndex(4); return true;
		case KeyEvent.KEYCODE_6: setSelectedReplyIndex(5); return true;
		case KeyEvent.KEYCODE_7: setSelectedReplyIndex(6); return true;
		case KeyEvent.KEYCODE_8: setSelectedReplyIndex(7); return true;
		case KeyEvent.KEYCODE_9: setSelectedReplyIndex(8); return true;
		default: return false;
		}
	}
    
    private void setPhrase(String phraseID) {
		this.phraseID = phraseID;
    	if (phraseID.equalsIgnoreCase(ConversationCollection.PHRASE_CLOSE)) {
    		ConversationActivity.this.finish();
    		return;
    	} else if (phraseID.equalsIgnoreCase(ConversationCollection.PHRASE_SHOP)) {
    		assert(npc != null);
    		assert(npc.getDropList() != null);
    		Intent intent = new Intent(this, ShopActivity.class);
    		Dialogs.addMonsterIdentifiers(intent, npc);
    		startActivity(intent);
    		ConversationActivity.this.finish();
    		return;
    	} else if (phraseID.equalsIgnoreCase(ConversationCollection.PHRASE_ATTACK)) {
    		ConversationActivity.this.setResult(ACTIVITYRESULT_ATTACK);
    		ConversationActivity.this.finish();
    		return;
    	} else if (phraseID.equalsIgnoreCase(ConversationCollection.PHRASE_REMOVE)) {
    		ConversationActivity.this.setResult(ACTIVITYRESULT_REMOVE);
    		ConversationActivity.this.finish();
    		return;
    	}
    	
    	phrase = world.conversationLoader.loadPhrase(phraseID, conversationCollection, getResources());
    	if (AndorsTrailApplication.DEVELOPMENT_DEBUGMESSAGES) {
    		if (phrase == null) phrase = new Phrase("(phrase \"" + phraseID + "\" not implemented yet)", null, null);
    	}

		ConversationController.PhraseRewards phraseRewards = null;
    	if (applyPhraseRewards) {
			phraseRewards = view.conversationController.applyPhraseRewards(player, phrase);
    	}
    	
    	if (phrase.message == null) {
    		for (Reply r : phrase.replies) {
    			if (!ConversationController.canSelectReply(player, r)) continue;
    			ConversationController.applyReplyEffect(player, r);
    			setPhrase(r.nextPhrase);
    			return;
    		}
    	}
    	
    	String message = ConversationController.getDisplayMessage(phrase, player);
    	
    	if (applyPhraseRewards && phraseRewards != null) {
			Loot loot = phraseRewards.loot;
	    	if (loot.hasItemsOrExp()) {
	    		message += "\n";
		    	if (loot.exp > 0) {
		    		message += "\n" + getString(R.string.conversation_rewardexp, loot.exp);
		    	}
		    	if (loot.gold > 0) {
		    		message += "\n" + getString(R.string.conversation_rewardgold, loot.gold);
		    	} else if (loot.gold < 0) {
		    		message += "\n" + getString(R.string.conversation_lostgold, -loot.gold);
		    	}
		    	if (!loot.items.isEmpty()) {
		    		final int len = loot.items.countItems();
		    		if (len == 1) {
		    			message += "\n" + getString(R.string.conversation_rewarditem);
		    		} else {
		    			message += "\n" + getString(R.string.conversation_rewarditems, len);
		    		}
		    	}
	    	}
    	}

    	addConversationStatement(npc, message, NPCConversationColor);
    	
    	if (isPhraseOnlyNextReply(phrase)) {
    		nextButton.setEnabled(true);
    	} else {
			replyGroup.removeAllViews();
    		for (Reply r : phrase.replies) {
	    		addReply(phrase, r);
	    	}
	    	nextButton.setEnabled(false);
    	}
    }
    
	private void addReply(final Phrase p, final Reply r) {
		if (!ConversationController.canSelectReply(player, r)) return;

		RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
		RadioButton rb = new RadioButton(this);
		rb.setLayoutParams(layoutParams);
		rb.setText(ConversationController.getDisplayMessage(r, player));
		rb.setOnCheckedChangeListener(radioButtonListener);
		rb.setTag(r);
		rb.setShadowLayer(1, 1, 1, Color.BLACK);
    	rb.setFocusable(false);
    	rb.setFocusableInTouchMode(false);
		replyGroup.addView(rb, layoutParams);
    }
	
	private static boolean isPhraseOnlyNextReply(Phrase p) {
		if (p.replies.length != 1) return false;
		if (p.replies[0].text.equals(ConversationCollection.REPLY_NEXT)) return true;
		return false;
	}
	
	
	private RadioButton getSelectedReplyButton() {
		for (int i = 0; i < phrase.replies.length; ++i) {
			final View v = replyGroup.getChildAt(i);
			if (v == null) continue;
			final RadioButton rb = (RadioButton) v;
			if (rb.isChecked()) {
				return rb;
			}
		}
		return null; // No reply was found. This is probably an error.
	}
	
	private void nextButtonClicked() {
		Reply r;
		if (isPhraseOnlyNextReply(phrase)) {
			// If there is only a "Next" as reply, we don't need to add it to the conversation history.
			r = phrase.replies[0];
		} else {
			RadioButton rb = getSelectedReplyButton();
			if (rb == null) return;
			r = (Reply) rb.getTag();
			addConversationStatement(player, rb.getText().toString(), playerConversationColor);
		}
		replyGroup.removeAllViews();
		
		ConversationController.applyReplyEffect(player, r);
		setPhrase(r.nextPhrase);
	}

	private void addConversationStatement(Actor actor, String text, int color) {
    	ConversationStatement s = new ConversationStatement();
    	if (displayActors) {
    		assert(actor != null);
	    	s.iconID = actor.iconID;
	    	s.actorName = actor.getName();
    	} else {
    		s.iconID = ConversationStatement.NO_ICON;
    	}
    	s.text = text;
    	s.color = color;
    	s.isPlayerActor = actor != null && actor == player;
    	conversationHistory.add(s);
    	statementList.clearFocus();
		listAdapter.notifyDataSetChanged();
		statementList.requestLayout();
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
		public boolean isPlayerActor;
		
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
			dest.writeByte((byte) (isPlayerActor ? 1 : 0));
		}
		
		@SuppressWarnings("unused")
		public static final Parcelable.Creator<ConversationStatement> CREATOR = new Parcelable.Creator<ConversationStatement>() {
		    public ConversationStatement createFromParcel(Parcel in) {
		    	ConversationStatement result = new ConversationStatement();
		    	result.actorName = in.readString();
		    	result.text = in.readString();
		    	result.iconID = in.readInt();
		    	result.color = in.readInt();
		    	result.isPlayerActor = in.readByte() == 1;
		        return result;
		    }
		
		    public ConversationStatement[] newArray(int size) {
		        return new ConversationStatement[size];
		    }
		};
	}

    private static final class StatementContainerAdapter extends ArrayAdapter<ConversationStatement> {

    	private final TileManager tileManager;
    	
		public StatementContainerAdapter(Context context, ArrayList<ConversationStatement> items, TileManager tileManager) {
			super(context, 0, items);
			this.tileManager = tileManager;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ConversationStatement statement = getItem(position);
			View result = convertView;
			if (result == null) {
				result = View.inflate(getContext(), R.layout.conversation_statement, null);
			}
			
			final TextView tv = (TextView) result.findViewById(R.id.conversation_text);
	        if (statement.hasActor()) {
                final Resources res = getContext().getResources();
                if (statement.isPlayerActor) tileManager.setImageViewTileForPlayer(res, tv, statement.iconID);
	        	else tileManager.setImageViewTileForMonster(res, tv, statement.iconID);
				
	    		tv.setText(statement.actorName + ": " + statement.text, BufferType.SPANNABLE);
		        Spannable sp = (Spannable) tv.getText();
		        sp.setSpan(new ForegroundColorSpan(statement.color), 0, statement.actorName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        } else {
	        	tv.setCompoundDrawables(null, null, null, null);
	    		tv.setText(statement.text);
		    }
			
			return result;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}
    }
}
