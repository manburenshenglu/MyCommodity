package com.xjl.commodity_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author xiejianlong
 * @describe TODO 选择商品数量控件
 * @date 2018/4/3 11:16
 */
public class CommodityView extends LinearLayout implements View.OnClickListener, TextWatcher {
    //库存
    private int mInventory = Integer.MAX_VALUE;
    //最大购买数，默认无限制
    private int mBuyMax = Integer.MAX_VALUE;
    private EditText mCount;
    private OnWarnListener mOnWarnListener;
    private NumberChangeListener mNumberChangeListener;

    public CommodityView(Context context) {
        super(context);

    }

    public CommodityView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.commodity_view, this);

        TextView addButton = findViewById(R.id.button_add);
        addButton.setOnClickListener(this);
        TextView subButton = findViewById(R.id.button_sub);
        subButton.setOnClickListener(this);

        mCount = findViewById(R.id.text_count);
        mCount.addTextChangedListener(this);
        mCount.setOnClickListener(this);


        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommodityView);
        boolean editable = typedArray.getBoolean(R.styleable.CommodityView_editable, true);
        int buttonWidth = typedArray.getDimensionPixelSize(R.styleable.CommodityView_buttonWidth, -1);
        int textWidth = typedArray.getDimensionPixelSize(R.styleable.CommodityView_textWidth, -1);
        int textSize = typedArray.getDimensionPixelSize(R.styleable.CommodityView_textSize, -1);
        int textColor = typedArray.getColor(R.styleable.CommodityView_textColor, 0xff000000);
        int rightTextColor = typedArray.getColor(R.styleable.CommodityView_rightTextColor, 0xff000000);
        int leftTextColor = typedArray.getColor(R.styleable.CommodityView_leftTextColor, 0xff000000);
        int buttonMinWH = typedArray.getDimensionPixelSize(R.styleable.CommodityView_buttonMinWidthAndHeight, -1);
        Drawable commodityViewBg = typedArray.getDrawable(R.styleable.CommodityView_commodityViewBg);
        Drawable leftButtonBg = typedArray.getDrawable(R.styleable.CommodityView_leftButtonBg);
        Drawable rightButtonBg = typedArray.getDrawable(R.styleable.CommodityView_rightButtonBg);
        typedArray.recycle();
        if (commodityViewBg != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                setBackground(commodityViewBg);
            } else {
                setBackgroundDrawable(commodityViewBg);
            }
        }
        if (leftButtonBg != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                subButton.setBackground(leftButtonBg);
            } else {
                subButton.setBackgroundDrawable(leftButtonBg);
            }
        }
        if (rightButtonBg != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                addButton.setBackground(rightButtonBg);
            } else {
                addButton.setBackgroundDrawable(rightButtonBg);
            }
        }

        setEditable(editable);
        mCount.setTextColor(textColor);
        subButton.setTextColor(leftTextColor);
        addButton.setTextColor(rightTextColor);
        if (buttonMinWH > 0) {
            subButton.setMinHeight(buttonMinWH);
            subButton.setMinWidth(buttonMinWH);
            addButton.setMinWidth(buttonMinWH);
            addButton.setMinHeight(buttonMinWH);
        }
        if (textSize > 0)
            mCount.setTextSize(textSize);

        if (buttonWidth > 0) {
            LayoutParams textParams = new LayoutParams(buttonWidth, buttonWidth);
            textParams.gravity = Gravity.CENTER;
            addButton.setLayoutParams(textParams);
            subButton.setLayoutParams(textParams);
        }
        if (textWidth > 0) {
            LayoutParams textParams = new LayoutParams(textWidth, LayoutParams.MATCH_PARENT);
            mCount.setLayoutParams(textParams);
        }
    }

    public int getNumber() {
        try {
            return Integer.parseInt(mCount.getText().toString());
        } catch (NumberFormatException e) {
        }
        mCount.setText("1");
        return 1;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
//        int count = getNumber();
        int count = Integer.parseInt(mCount.getText().toString());
        if (id == R.id.button_sub) {
            if (count >= 1) {
                //正常减
                mCount.setText("" + (count - 1));
            }

        } else if (id == R.id.button_add) {
            if (count < Math.min(mBuyMax, mInventory)) {
                //正常添加
                mCount.setText("" + (count + 1));
            } else if (mInventory < mBuyMax) {
                //库存不足
                warningForInventory();
            } else {
                //超过最大购买数
                warningForBuyMax();
            }

        } else if (id == R.id.text_count) {
            mCount.setSelection(mCount.getText().toString().length());
        }
    }

    private boolean isZero = false;

    private void onNumberInput() {
        //当前数量
        int count = getNumber();
//        if (count <= 0) {
//            //手动输入
//            isZero = true;
//            if (!isZero)
//                mCount.setText("0");
//            return;
//        } else {
//            isZero = false;
//        }

        int limit = Math.min(mBuyMax, mInventory);
        if (count > limit) {
            //超过了数量
            mCount.setText(limit + "");
            if (mInventory < mBuyMax) {
                //库存不足
                warningForInventory();
            } else {
                //超过最大购买数
                warningForBuyMax();
            }
        }

    }

    /**
     * 超过的库存限制
     * Warning for inventory.
     */
    private void warningForInventory() {
        if (mOnWarnListener != null) mOnWarnListener.onWarningForOutOfInventory(mInventory);
    }

    /**
     * 超过的最大购买数限制
     * Warning for buy max.
     */
    private void warningForBuyMax() {
        if (mOnWarnListener != null) mOnWarnListener.onWarningForOutOfBuyMax(mBuyMax);
    }


    private void setEditable(boolean editable) {
        if (editable) {
            mCount.setFocusable(true);
            mCount.setKeyListener(new DigitsKeyListener());
        } else {
            mCount.setFocusable(false);
            mCount.setKeyListener(null);
        }
    }

    public CommodityView setCurrentNumber(int currentNumber) {
        if (currentNumber < 1) mCount.setText("1");
        mCount.setText("" + Math.min(Math.min(mBuyMax, mInventory), currentNumber));
        return this;
    }

    public int getInventory() {
        return mInventory;
    }

    public CommodityView setInventory(int inventory) {
        mInventory = inventory;
        return this;
    }

    public int getBuyMax() {
        return mBuyMax;
    }

    public CommodityView setBuyMax(int buyMax) {
        mBuyMax = buyMax;
        return this;
    }

    public CommodityView setOnWarnListener(OnWarnListener onWarnListener) {
        mOnWarnListener = onWarnListener;
        return this;
    }

    public CommodityView setNumberChangeListener(NumberChangeListener numberChangeListener) {
        mNumberChangeListener = numberChangeListener;
        return this;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onNumberInput();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mNumberChangeListener != null) {
            String str = s.toString().trim();
            int amount = TextUtils.isEmpty(str) ? 0 : Integer.valueOf(str);
            mNumberChangeListener.afterNumberChanged(amount);
        }
    }

    public void setInitCount(String initCount) {
        if (mNumberChangeListener != null) {
            int amount = TextUtils.isEmpty(initCount) ? 0 : Integer.valueOf(initCount);
            mNumberChangeListener.afterNumberChanged(amount);
        }
    }
    public interface OnWarnListener {
        void onWarningForOutOfInventory(int inventory);

        void onWarningForOutOfBuyMax(int max);
    }

    public interface NumberChangeListener {
        void afterNumberChanged(int amount);
    }
}
