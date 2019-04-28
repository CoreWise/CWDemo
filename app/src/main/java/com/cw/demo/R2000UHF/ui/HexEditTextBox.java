package com.cw.demo.R2000UHF.ui;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import java.util.regex.Pattern;

public class HexEditTextBox extends EditText {
	
	private Context mContext;

	public HexEditTextBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		initContext(context);
	}
	
	public HexEditTextBox(Context context) {
        super(context);
        initContext(context);
    }  
      
    public HexEditTextBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initContext(context);
    }
    
	private void initContext(Context context) {
		mContext = context;
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
        if (!checkInput(keyCode, event))
        	return false;
        
		return super.onKeyDown(keyCode, event);
	};
	
	@Override
	protected void onTextChanged(CharSequence text, int start,
                                 int lengthBefore, int lengthAfter) {
		// TODO Auto-generated method stub
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		
		int nIndex = getSelectionStart();

		if (nIndex > 1) {
            if (text.charAt(nIndex - 1) != ' ' && text.charAt(nIndex - 2) != ' ') {
            	CharSequence sub1 = text.subSequence(0, nIndex);
            	CharSequence sub2 = text.subSequence(nIndex, text.length());
                setText(sub1 + " " + sub2);
                setSelection(nIndex + 1);
            }
        }
	}
	
    private boolean checkInput(int keyCode, KeyEvent event) {
        String inputStr = String.valueOf(keyCode);
        if (keyCode != KeyEvent.KEYCODE_DEL) {
            if (Pattern.compile("[a-fA-F0-9.\\s]+").matcher(inputStr).matches() == false) {
                return false;
            }
        } else {
            int nIndex = getSelectionStart();
            if (nIndex > 0) {
            	Editable text = getText();
                if (text.charAt(nIndex - 1) == ' ') {
                	text.delete(nIndex - 1, text.length());
                    setSelection(nIndex - 1);
                }
            }
        }
        
        return true;
    }
}
