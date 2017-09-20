package com.pchmn.materialchips.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pchmn.materialchips.R;
import com.pchmn.materialchips.model.ChipInterface;
import com.pchmn.materialchips.util.ColorUtil;
import com.pchmn.materialchips.util.LetterTileProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailedChipView extends RelativeLayout {

    private static final String TAG = DetailedChipView.class.toString();
    // context
    private Context mContext;

    private RelativeLayout mContentLayout;
    private CircleImageView mAvatarIconImageView;
    private TextView mNameTextView;
    private TextView mInfoTextView;
    private ImageButton mDeleteButton;
    // letter tile provider
    private static LetterTileProvider mLetterTileProvider;
    // attributes
    @ColorInt
    private int mBackgroundColor;

    public DetailedChipView(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public DetailedChipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    /**
     * Inflate the view according to attributes
     *
     * @param attrs
     *         the attributes
     */
    private void init(AttributeSet attrs) {
        // inflate layout
        View rootView = inflate(getContext(), R.layout.detailed_chip_view, this);

        mContentLayout = rootView.findViewById(R.id.content);
        mAvatarIconImageView = rootView.findViewById(R.id.avatar_icon);
        mNameTextView = rootView.findViewById(R.id.name);
        mInfoTextView = rootView.findViewById(R.id.info);
        mDeleteButton = rootView.findViewById(R.id.delete_button);

        // letter tile provider
        mLetterTileProvider = new LetterTileProvider(mContext);

        // hide on first
        setVisibility(GONE);
        // hide on touch outside
        hideOnTouchOutside();
    }

    /**
     * Hide the view on touch outside of it
     */
    private void hideOnTouchOutside() {
        // set focusable
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);
    }

    /**
     * Fade in
     */
    public void fadeIn() {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200);
        startAnimation(anim);
        setVisibility(VISIBLE);
        // focus on the view
        requestFocus();
    }

    /**
     * Fade out
     */
    public void fadeOut() {
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(200);
        startAnimation(anim);
        setVisibility(GONE);
        // fix onclick issue
        clearFocus();
        setClickable(false);
    }

    public void setAvatarIcon(Drawable icon) {
        mAvatarIconImageView.setImageDrawable(icon);
    }

    public void setAvatarIcon(Bitmap icon) {
        mAvatarIconImageView.setImageBitmap(icon);
    }

    public void setAvatarIcon(Uri icon) {
        mAvatarIconImageView.setImageURI(icon);
    }

    public void setName(String name) {
        mNameTextView.setText(name);
    }

    public void setInfo(String info) {
        if (info != null) {
            mInfoTextView.setVisibility(VISIBLE);
            mInfoTextView.setText(info);
        } else {
            mInfoTextView.setVisibility(GONE);
        }
    }

    public void setTextColor(ColorStateList color) {
        setTextColor(color.getDefaultColor());
    }

    public void setTextColor(@ColorInt int color) {
        mNameTextView.setTextColor(color);
        mInfoTextView.setTextColor(ColorUtil.alpha(color, 150));
    }

    @Override
    public void setBackgroundTintList(@Nullable ColorStateList tint) {
        if (tint == null) return;
        setBackgroundColor(tint.getDefaultColor());
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        mBackgroundColor = color;
        mContentLayout.setBackgroundColor(color);
    }

    @ColorInt
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setDeleteIconColor(ColorStateList color) {
        if (color == null) return;
        setDeleteIconColor(color.getDefaultColor());
    }

    public void setDeleteIconColor(@ColorInt int color) {
        mDeleteButton.getDrawable().mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public void setOnDeleteClicked(OnClickListener onClickListener) {
        mDeleteButton.setOnClickListener(onClickListener);
    }

    public void alignLeft() {
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) mContentLayout.getLayoutParams();
        params.leftMargin = 0;
        mContentLayout.setLayoutParams(params);
    }

    public void alignRight() {
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) mContentLayout.getLayoutParams();
        params.rightMargin = 0;
        mContentLayout.setLayoutParams(params);
    }

    public static class Builder {
        private Context context;
        private Uri avatarUri;
        private Drawable avatarDrawable;
        private String name;
        private String info;
        @ColorInt
        private int textColor;
        @ColorInt
        private int backgroundColor;
        @ColorInt
        private int deleteIconColor;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder avatar(Uri avatarUri) {
            this.avatarUri = avatarUri;
            return this;
        }

        public Builder avatar(Drawable avatarDrawable) {
            this.avatarDrawable = avatarDrawable;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder info(String info) {
            this.info = info;
            return this;
        }

        public Builder chip(ChipInterface chip) {
            this.avatarUri = chip.getAvatarUri();
            this.avatarDrawable = chip.getAvatarDrawable();
            this.name = chip.getLabel();
            this.info = chip.getInfo();
            return this;
        }

        public Builder textColor(@ColorInt int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder backgroundColor(@ColorInt int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder deleteIconColor(@ColorInt int deleteIconColor) {
            this.deleteIconColor = deleteIconColor;
            return this;
        }

        public DetailedChipView build() {
            return DetailedChipView.newInstance(this);
        }
    }

    private static DetailedChipView newInstance(Builder builder) {
        DetailedChipView detailedChipView = new DetailedChipView(builder.context);
        // avatar
        if (builder.avatarUri != null)
            detailedChipView.setAvatarIcon(builder.avatarUri);
        else if (builder.avatarDrawable != null)
            detailedChipView.setAvatarIcon(builder.avatarDrawable);
        else
            detailedChipView.setAvatarIcon(mLetterTileProvider.getLetterTile(builder.name));

        // background color
        if (builder.backgroundColor != 0)
            detailedChipView.setBackgroundColor(builder.backgroundColor);

        // text color
        if (builder.textColor != 0)
            detailedChipView.setTextColor(builder.textColor);
        else if (ColorUtil.isColorDark(detailedChipView.getBackgroundColor()))
            detailedChipView.setTextColor(ColorStateList.valueOf(Color.WHITE));
        else
            detailedChipView.setTextColor(ColorStateList.valueOf(Color.BLACK));

        // delete icon color
        if (builder.deleteIconColor != 0)
            detailedChipView.setDeleteIconColor(builder.deleteIconColor);
        else if (ColorUtil.isColorDark(detailedChipView.getBackgroundColor()))
            detailedChipView.setDeleteIconColor(ColorStateList.valueOf(Color.WHITE));
        else
            detailedChipView.setDeleteIconColor(ColorStateList.valueOf(Color.BLACK));

        detailedChipView.setName(builder.name);
        detailedChipView.setInfo(builder.info);
        return detailedChipView;
    }
}
