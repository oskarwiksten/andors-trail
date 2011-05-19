package com.gpl.rpg.AndorsTrail.view;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.widget.ImageButton;

public class QuickButton extends ImageButton {
	private final ColorFilter grayScaleFilter = new ColorMatrixColorFilter(
			new float[] { 0.30f, 0.59f, 0.11f, 0.0f, 0.0f,
                          0.30f, 0.59f, 0.11f, 0.0f, 0.0f,
                          0.30f, 0.59f, 0.11f, 0.0f, 0.0f,
                          0.00f, 0.00f, 0.00f, 1.0f, 0.0f
			});
	private boolean empty;
	private QuickButtonContextMenuInfo menuInfo;
	
	public QuickButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		menuInfo = new QuickButtonContextMenuInfo();
	}
	
	public void setIndex(int index){
		menuInfo.index = index;
	}
	
	public int getIndex(){
		return menuInfo.index;
	}
	
	@Override
	protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
		return menuInfo;
	}
	
	public void setEmpty(boolean empty) {
		this.empty = empty;
		if(empty){
			setColorFilter(grayScaleFilter);
		} else {
			setColorFilter(null);
		}
	}
	
	public boolean isEmpty() {
		return empty;
	}

	public static class QuickButtonContextMenuInfo implements ContextMenu.ContextMenuInfo{
		public int index;
	}
}
