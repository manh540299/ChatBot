package com.chatbotai.aichataiart.view.custom;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.smarttech.smarttechlibrary.utils.CommonUtils;

public class PopupMenuCustomLayout {
    private PopupWindow popupWindow;
    private View popupView;

    public PopupMenuCustomLayout(Context context, int rLayoutId, PopupMenuCustomOnClickListener onClickListener) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(rLayoutId, null);
        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setElevation(10);

        ConstraintLayout linearLayout = (ConstraintLayout) popupView;
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View v = linearLayout.getChildAt(i);
            v.setOnClickListener(v1 -> {
                onClickListener.onClick(v1.getId());
                popupWindow.dismiss();
            });
        }
    }

    public void setAnimationStyle(int animationStyle) {
        popupWindow.setAnimationStyle(animationStyle);
    }

    public void show() {
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    public void show(View anchorView) {
        popupWindow.showAsDropDown(anchorView, 0, -(anchorView.getHeight() + CommonUtils.INSTANCE.dpToPxInt(66)));
    }

    public void showMenuTop(View anchorView) {
        popupWindow.showAsDropDown(anchorView, 0, 0);
    }

    public interface PopupMenuCustomOnClickListener {
        public void onClick(int menuItemId);
    }
}
