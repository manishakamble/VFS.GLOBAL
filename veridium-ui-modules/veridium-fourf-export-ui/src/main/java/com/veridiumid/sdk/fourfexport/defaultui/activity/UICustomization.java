package com.veridiumid.sdk.fourfexport.defaultui.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by josiah on 27/02/2017.
 */

public class UICustomization {

    private static int mBackgroundColor = 0xff38C0CC;
    private static int mForegroundColor = Color.WHITE;
    private static int mDialogColour = Color.WHITE;
    private static int mDialogTextColor = Color.BLACK;

    public static final int defaultBackgroundColor = 0xff38C0CC;
    public static final int defaultForegroundColor = Color.WHITE;
    public static final int defaultDialogColor = Color.WHITE;
    public static final int defaultDialogTextColor = Color.BLACK;

    private static Drawable mLogo = null;

    private static Drawable mBackgroundImage = null;

    public static void setBackgroundImage(Drawable backgroundImage){
        mBackgroundImage = backgroundImage;
    }

    public static Drawable getBackgroundImage(){
        return mBackgroundImage;
    }

    public static void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = (backgroundColor);
    }

    public static void setForegroundColor(int foregroundColor) {
        mForegroundColor = (foregroundColor);
    }

    public static void setDialogColor(int dialogColor){
        mDialogColour = dialogColor;
    }

    public static void setDialogTextColor(int dialogTextColor){
        mDialogTextColor = dialogTextColor;
    }

    @Deprecated
    public static void setFingerColor(int fingerGuideColor) {
        return;
    }

    public static void setLogo(Drawable logo) {
        mLogo = logo;
    }

    public static int getBackgroundColor() {
        return mBackgroundColor;
    }

    public static int getDialogColor() {
        return mDialogColour;
    }

    public static int getDialogTextColor(){
        return mDialogTextColor;
    }

    public static int getForegroundColor() {
        return mForegroundColor;
    }

    public static Drawable getImageWithBackgroundColor(Drawable img) {
        img.setColorFilter(mBackgroundColor, PorterDuff.Mode.MULTIPLY);
        return img;
    }
    public static Drawable getImageWithForegroundColor(Drawable img) {
        img.setColorFilter(mForegroundColor, PorterDuff.Mode.MULTIPLY);
        return img;
    }

    protected static Drawable getImageWithDialogColor(Drawable img) {
        img.setColorFilter(mDialogColour, PorterDuff.Mode.MULTIPLY);
        return img;
    }

    public static void applyBackgroundColorMask(ImageView view) {
        view.setColorFilter(mBackgroundColor, PorterDuff.Mode.MULTIPLY);
    }

    public static void applyForegroundColorMask(ImageView view) {
        view.setColorFilter(mForegroundColor, PorterDuff.Mode.MULTIPLY);
    }

    protected static void applyDialogColorMask(ImageView view) {
        view.setColorFilter(mDialogColour, PorterDuff.Mode.MULTIPLY);
    }

    @Deprecated
    public static void applyFingerColorMask(ImageView view) {
        return;
    }

    public static Drawable getLogo() {
        return mLogo;
    }

}
