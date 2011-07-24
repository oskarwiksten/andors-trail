package com.gpl.rpg.AndorsTrail.activity;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.ItemController;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * @author ejwessel
 * Creates the BulkSelectionInterface dialog that allows for buy/drop/selling
 */
public class BulkSelectionInterface extends Activity implements TextWatcher {
	
	// class variables
	final public static int BULK_INTERFACE_BUY = 0;
	final public static int BULK_INTERFACE_SELL = 1;  
	final public static int BULK_INTERFACE_DROP = 2;

	// TODO: determine good values for the BUTTON_REPEAT constants / I currently cannot test them on the phone
	final private static int BUTTON_REPEAT_FIRST_TIME = 300;		// Delay after the touch before the counting starts
	final private static int BUTTON_REPEAT_FURTHER_TIMES = 50;		// Delay between two count events
	final private static int BUTTON_REPEAT_DOUBLE_AFTER = 10;       // after how many count events the countValue doubles?
		
	private WorldContext world;
	private int interfaceType; 					    				// the type of interface either: BULK_INTERFACE_BUY, BULK_INTERFACE_SELL or BULK_INTERFACE_DROP
	private ItemType itemType;
	private int totalAvailableAmount;
	private int pricePerUnit;
	
	private TextView bulkselection_amount_available;
	private TextView bulkselection_summary_totalgold;
	private EditText bulkselection_amount_taken;					// the amount we're going to take from the totalAmount
	private SeekBar bulkselection_slider;
	private Button okButton;
	
	private final Handler timedEventHandler = new Handler();		// variables to count up or down on long presses on the buttons
	private int countValue, countTime;
	private final Runnable countEvent = new Runnable() {
		public void run() {
			incrementValueAndRepeat(BUTTON_REPEAT_FURTHER_TIMES);
		 }
	};

	/*
	 * constructor
	 * @param context - the activity it is displayed upon
	 * @param p_interfaceType - the type of interface: BULK_INTERFACE_BUY, BULK_INTERFACE_SELL or BULK_INTERFACE_DROP
	 * @param p_itemName - the name of the item currently used - example: meat, rock, fish
	 * @param p_totalAmount - the total amount available of that item
	 * @param p_price - the price of the item - not necessary when dropping items
	 * @param p_money - the total amount of money available - only necessary when buying
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        this.world = app.world;
        AndorsTrailApplication.setWindowParameters(this, app.preferences);
        
        
        final Intent intent = getIntent();
        Bundle params = intent.getExtras();
        int itemTypeID = params.getInt("itemTypeID");
        itemType = world.itemTypes.getItemType(itemTypeID);
        totalAvailableAmount = params.getInt("totalAvailableAmount");
        interfaceType = params.getInt("interfaceType");
        
		int intialSelection = 1;
		
        setContentView(R.layout.bulkselection);
		
		// initialize UI variables
        TextView bulkselection_action_type = (TextView)findViewById(R.id.bulkselection_action_type);
		bulkselection_amount_taken = (EditText)findViewById(R.id.bulkselection_amount_taken);
		bulkselection_amount_available = (TextView)findViewById(R.id.bulkselection_amount_available);
		bulkselection_slider = (SeekBar)findViewById(R.id.bulkselection_slider);
		bulkselection_summary_totalgold = (TextView)findViewById(R.id.bulkselection_summary_totalgold);
		okButton = (Button)findViewById(R.id.bulkselection_finalize_button);
		Button cancelButton = (Button)findViewById(R.id.bulkselection_cancel_button);
		final Button decrementButton = (Button)findViewById(R.id.bulkselection_decrement_button);
		final Button incrementButton = (Button)findViewById(R.id.bulkselection_increment_button);
		final Button selectAllButton = (Button)findViewById(R.id.bulkselection_select_all_button);
		
        int actionTextResourceID = 0;
		if (interfaceType == BULK_INTERFACE_BUY) {
        	pricePerUnit = ItemController.getBuyingPrice(world.model.player, itemType);
        	actionTextResourceID = R.string.shop_buy;
        } else if (interfaceType == BULK_INTERFACE_SELL) {
        	pricePerUnit = ItemController.getSellingPrice(world.model.player, itemType);
        	actionTextResourceID = R.string.shop_sell;
        } else if (interfaceType == BULK_INTERFACE_DROP) {
        	pricePerUnit = 0;
        	actionTextResourceID = R.string.inventory_drop;
        	bulkselection_summary_totalgold.setVisibility(View.GONE);
        }
		String actionText = getResources().getString(actionTextResourceID);
		
		// initialize the visual components visuals
		okButton.setText(actionText);
		bulkselection_action_type.setText(actionText + " ");
		bulkselection_amount_available.setText(Integer.toString(totalAvailableAmount));
		bulkselection_slider.setMax(totalAvailableAmount - 1);
		updateControls(intialSelection);

		OnTouchListener incrementDecrementListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					countTime = 0;
					if (v == decrementButton) countValue = -1;
					if (v == incrementButton) countValue = +1;
					incrementValueAndRepeat(BUTTON_REPEAT_FIRST_TIME);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_OUTSIDE: 
					timedEventHandler.removeCallbacks(countEvent);
					break;
			}
			return false;
			}
		};
		
		// setup decrement button
		decrementButton.setOnTouchListener(incrementDecrementListener);

		// setup increment button
		incrementButton.setOnTouchListener(incrementDecrementListener);
		
		// setup EditText listeners
		bulkselection_amount_taken.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ENTER){
					updateControls(getTextboxAmount());
				}
				return false;
			}
		});
		
		bulkselection_amount_taken.addTextChangedListener(this);
		
		// setup slider event listeners
		bulkselection_slider.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				updateControls(bulkselection_slider.getProgress() + 1);
				return false;
			}
		});
		
		// setup OK button
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
	 		public void onClick(View v) {
				Intent result = new Intent();
				result.putExtras(intent);
				result.putExtra("selectedAmount", getTextboxAmount());
				setResult(RESULT_OK, result);
				BulkSelectionInterface.this.finish();
	 		}
	 	});
		
		// setup cancel button
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		selectAllButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateControls(totalAvailableAmount);
			}
		});
	}
	
	private void incrementValueAndRepeat(int repeatAfterInterval) {
		if(++countTime % BUTTON_REPEAT_DOUBLE_AFTER == 0) countValue *= 2;
		int newAmount = getTextboxAmount() + countValue;
		updateControls(newAmount);
		if (newAmount <= 1 || newAmount >= totalAvailableAmount) return; // Do not repeat if we have reached the end of the scale. 
		timedEventHandler.postDelayed(countEvent, repeatAfterInterval);
	}
	
	private boolean canSelectFinalizeButton() {
		int amount = getTextboxAmount();
		if (amount <= 0) return false;
		if (amount > totalAvailableAmount) return false;
		
		if (interfaceType == BULK_INTERFACE_BUY) {
			if (amount * pricePerUnit > world.model.player.inventory.gold) return false;
		}
		
		return true;
	}
	
	// adjusts the amount to the possible interval / synchronizes changes between controls
	private void updateControls(int newAmount) {
		int oldSliderAmount = bulkselection_slider.getProgress() + 1;
		int oldEditboxAmount = getTextboxAmount();
		
		// adjust amount
		if (newAmount < 1) newAmount = 1;
		if (newAmount > totalAvailableAmount) newAmount = totalAvailableAmount;
		
		// update controls
		if (newAmount != oldEditboxAmount) bulkselection_amount_taken.setText(Integer.toString(newAmount)); // change the amount taken/text
		if (newAmount != oldSliderAmount) bulkselection_slider.setProgress(newAmount - 1);                  // change the amount taken/text

		// display buying/selling information if not dropping
		if (interfaceType == BULK_INTERFACE_BUY) {
			bulkselection_summary_totalgold.setText(getResources().getString(R.string.bulkselection_totalcost_buy, newAmount * pricePerUnit));
		} else if (interfaceType == BULK_INTERFACE_SELL) {
			bulkselection_summary_totalgold.setText(getResources().getString(R.string.bulkselection_totalcost_sell, newAmount * pricePerUnit));
		} 
		
		okButton.setEnabled(canSelectFinalizeButton());
	}

	private int getTextboxAmount() {
		final String s = bulkselection_amount_taken.getText().toString();
		if (s.equals("")) return 0;
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {	}
		return 0;
	}
	
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) { }

	@Override
	public void afterTextChanged(Editable s) {
		if (bulkselection_amount_taken.getText().toString().equals("")) return;
		int newAmount = getTextboxAmount();
		updateControls(newAmount);
    }
}
