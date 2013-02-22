package com.gpl.rpg.AndorsTrail.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.BufferType;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.ConversationController;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reply;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;

import java.util.ArrayList;

public final class ConversationActivity extends Activity implements OnKeyListener
		, ConversationController.ConversationStatemachine.ConversationStateListener {
	public static final int ACTIVITYRESULT_ATTACK = Activity.RESULT_FIRST_USER + 1;
	public static final int ACTIVITYRESULT_REMOVE = Activity.RESULT_FIRST_USER + 2;
	private static final int playerConversationColor = Color.argb(255, 0xbb, 0x22, 0x22);
	private static final int NPCConversationColor = Color.argb(255, 0xbb, 0xbb, 0x22);
	
	private Player player;
	private ArrayList<ConversationStatement> conversationHistory = new ArrayList<ConversationStatement>();
	private ConversationController.ConversationStatemachine conversationState;

	private StatementContainerAdapter listAdapter;
	private Button nextButton;
    private ListView statementList;
	private RadioGroup replyGroup;
	private OnCheckedChangeListener radioButtonListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
		if (!app.isInitialized()) { finish(); return; }
		WorldContext world = app.getWorld();
		this.player = world.model.player;
		this.conversationState = new ConversationController.ConversationStatemachine(world, app.getControllerContext(), getResources(), this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

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
        nextButton.setEnabled(false);

        statementList.setOnKeyListener(this);

		statementList.setSelected(false);
		statementList.setFocusable(false);
		statementList.setFocusableInTouchMode(false);

		if (savedInstanceState != null) {
			conversationState.setCurrentNPC(Dialogs.getMonsterFromBundle(savedInstanceState, world));
			conversationHistory = savedInstanceState.getParcelableArrayList("conversationHistory");
			if (conversationHistory == null) conversationHistory = new ArrayList<ConversationStatement>();
			final String phraseID = savedInstanceState.getString("phraseID");
			conversationState.proceedToRestoredState(phraseID);
		} else {
			conversationState.setCurrentNPC(Dialogs.getMonsterFromIntent(getIntent(), world));
			final String phraseID = getIntent().getData().getLastPathSegment();
			conversationState.proceedToPhrase(phraseID);
		}
	}
    
    @Override
	protected void onResume() {
		super.onResume();
    	nextButton.requestFocus();
	}

	private int getSelectedReplyIndex() {
    	for (int i = 0; i < replyGroup.getChildCount(); ++i) {
			final View v = replyGroup.getChildAt(i);
			if (v == null) continue;
			final RadioButton rb = (RadioButton) v;
			if (rb.isChecked()) return i;
    	}
    	return -1;
    }

	private void setSelectedReplyIndex(int i) {
		int replyCount = replyGroup.getChildCount();
		if (replyCount <= 0) return;
		if (i < 0) i = 0;
		else if (i >= replyCount) i = replyCount - 1;

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
	
	private RadioButton getSelectedReplyButton() {
		for (int i = 0; i < replyGroup.getChildCount(); ++i) {
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
        RadioButton rb = getSelectedReplyButton();
		replyGroup.removeAllViews();
        nextButton.setEnabled(false);
        if (conversationState.hasOnlyOneNextReply()) {
			conversationState.playerSelectedNextStep();
		} else {
			if (rb == null) return;
			Reply r = (Reply) rb.getTag();
			addConversationStatement(player, rb.getText().toString());
			conversationState.playerSelectedReply(r);
		}
	}

	private void addConversationStatement(Actor actor, String text) {
    	ConversationStatement s = new ConversationStatement();
    	if (actor != null) {
	    	s.iconID = actor.iconID;
	    	s.actorName = actor.getName();
    	} else {
    		s.iconID = ConversationStatement.NO_ICON;
    	}
    	s.text = text;
    	s.color = actor == player ? playerConversationColor : NPCConversationColor;
    	s.isPlayerActor = actor != null && actor == player;
    	conversationHistory.add(s);
    	statementList.clearFocus();
		listAdapter.notifyDataSetChanged();
		statementList.requestLayout();
	}
    
    @Override
	public void onSaveInstanceState(Bundle outState) {
    	outState.putString("phraseID", conversationState.getCurrentPhraseID());
    	outState.putParcelableArrayList("conversationHistory", conversationHistory);
		Dialogs.addMonsterIdentifiers(outState, conversationState.getCurrentNPC());
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

	@Override
	public void onTextPhraseReached(String message, Actor actor) {
		addConversationStatement(actor, message);
	}

	@Override
	public void onPlayerReceivedRewards(ConversationController.PhraseRewards phraseRewards) {
		Loot loot = phraseRewards.loot;
		if (!loot.hasItemsOrExp()) return;

		if (loot.exp > 0) {
			addRewardMessage(getString(R.string.conversation_rewardexp, loot.exp));
		}
		if (loot.gold > 0) {
			addRewardMessage(getString(R.string.conversation_rewardgold, loot.gold));
		} else if (loot.gold < 0) {
			addRewardMessage(getString(R.string.conversation_lostgold, -loot.gold));
		}
		if (!loot.items.isEmpty()) {
			final int len = loot.items.countItems();
			if (len == 1) {
				addRewardMessage(getString(R.string.conversation_rewarditem));
			} else {
				addRewardMessage(getString(R.string.conversation_rewarditems, len));
			}
		}
	}

	private void addRewardMessage(String text) {
		addConversationStatement(null, text);
	}

	@Override
	public void onConversationEnded() {
		ConversationActivity.this.finish();
	}

	@Override
	public void onConversationEndedWithShop(Monster npc) {
		Intent intent = new Intent(this, ShopActivity.class);
		Dialogs.addMonsterIdentifiers(intent, npc);
		startActivity(intent);
		ConversationActivity.this.finish();
	}

	@Override
	public void onConversationEndedWithCombat(Monster npc) {
		ConversationActivity.this.setResult(ACTIVITYRESULT_ATTACK);
		ConversationActivity.this.finish();
	}

	@Override
	public void onConversationEndedWithRemoval(Monster npc) {
		ConversationActivity.this.setResult(ACTIVITYRESULT_REMOVE);
		ConversationActivity.this.finish();
	}

	@Override
	public void onConversationCanProceedWithNext() {
		nextButton.setEnabled(true);
	}

	@Override
	public void onConversationHasReply(Reply r, String message) {
		RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
		RadioButton rb = new RadioButton(this);
		rb.setLayoutParams(layoutParams);
		rb.setText(message);
		rb.setOnCheckedChangeListener(radioButtonListener);
		rb.setTag(r);
		rb.setShadowLayer(1, 1, 1, Color.BLACK);
		rb.setFocusable(false);
		rb.setFocusableInTouchMode(false);
		replyGroup.addView(rb, layoutParams);
	}
}
