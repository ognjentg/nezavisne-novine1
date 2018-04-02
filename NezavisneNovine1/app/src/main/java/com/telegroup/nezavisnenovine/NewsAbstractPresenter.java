/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.telegroup.nezavisnenovine;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.support.v17.leanback.R;
import android.support.v17.leanback.widget.Presenter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;


public abstract class NewsAbstractPresenter extends Presenter {


    public static class ViewHolder extends Presenter.ViewHolder {
        final TextView mTitle;
        final TextView mAuthor;
        //final TextView mDate;
        final TextView mLid;
         TextView mDesc;



        final int mTitleMargin;
        final int mUnderTitleBaselineMargin;
        final int mUnderSubtitleBaselineMargin;
        final int mTitleLineSpacing;
        final int mBodyLineSpacing;
        final int mBodyMaxLines;
        final int mBodyMinLines;
        final FontMetricsInt mTitleFontMetricsInt;
        final FontMetricsInt mSubtitleFontMetricsInt;
        final FontMetricsInt mBodyFontMetricsInt;
        final int mTitleMaxLines;
        private ViewTreeObserver.OnPreDrawListener mPreDrawListener;

        public TextView getmTitle() {
            return mTitle;
        }

        public TextView getmAuthor() {
            return mAuthor;
        }


        public TextView getmLid() {
            return mLid;
        }

        public TextView getmDesc() {
            return mDesc;
        }

        public ViewHolder(final View view) {
            super(view);
            mTitle = (TextView) view.findViewById(com.telegroup.nezavisnenovine.R.id.title);
            mAuthor = (TextView) view.findViewById(com.telegroup.nezavisnenovine.R.id.autordate);
            //mDate = (TextView) view.findViewById(com.telegroup.nezavisnenovine.R.id.datum);
            mLid = (TextView) view.findViewById(com.telegroup.nezavisnenovine.R.id.lid);
            mDesc =(TextView) view.findViewById(com.telegroup.nezavisnenovine.R.id.description);

            FontMetricsInt titleFontMetricsInt = getFontMetricsInt(mTitle);
            final int titleAscent = view.getResources().getDimensionPixelSize(
                    R.dimen.lb_details_description_title_baseline);
            // Ascent is negative
            mTitleMargin = titleAscent + titleFontMetricsInt.ascent;

            mUnderTitleBaselineMargin = view.getResources().getDimensionPixelSize(
                    R.dimen.lb_details_description_under_title_baseline_margin);
            mUnderSubtitleBaselineMargin = view.getResources().getDimensionPixelSize(
                    R.dimen.lb_details_description_under_subtitle_baseline_margin);

            mTitleLineSpacing = view.getResources().getDimensionPixelSize(
                    R.dimen.lb_details_description_title_line_spacing);
            mBodyLineSpacing = view.getResources().getDimensionPixelSize(
                    R.dimen.lb_details_description_body_line_spacing);

            mBodyMinLines = 200;
            mBodyMaxLines = 1000;
            mTitleMaxLines = mTitle.getMaxLines();

            mTitleFontMetricsInt = getFontMetricsInt(mTitle);
            mSubtitleFontMetricsInt = getFontMetricsInt(mLid);
            mBodyFontMetricsInt = getFontMetricsInt(mDesc);

            mTitle.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    addPreDrawListener();
                }
            });
        }

        void addPreDrawListener() {
            if (mPreDrawListener != null) {
                return;
            }
            mPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (mLid.getVisibility() == View.VISIBLE
                            && mLid.getTop() > view.getHeight()
                            && mTitle.getLineCount() > 1) {
                        mTitle.setMaxLines(mTitle.getLineCount() - 1);
                        return false;
                    }
                    final int titleLines = mTitle.getLineCount();
                    final int maxLines = titleLines > 1 ? mBodyMinLines : mBodyMaxLines;
                    if (mDesc.getMaxLines() != maxLines) {
                        mDesc.setMaxLines(maxLines);
                        return false;
                    } else {
                        removePreDrawListener();
                        return true;
                    }
                }
            };
            view.getViewTreeObserver().addOnPreDrawListener(mPreDrawListener);
        }

        void removePreDrawListener() {
            if (mPreDrawListener != null) {
                view.getViewTreeObserver().removeOnPreDrawListener(mPreDrawListener);
                mPreDrawListener = null;
            }
        }


        private FontMetricsInt getFontMetricsInt(TextView textView) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(textView.getTextSize());
            paint.setTypeface(textView.getTypeface());
            return paint.getFontMetricsInt();
        }
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(com.telegroup.nezavisnenovine.R.layout.lb_details_description, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public final void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        ViewHolder vh = (ViewHolder) viewHolder;
        onBindDescription(vh, item);

        boolean hasTitle = true;
        if (TextUtils.isEmpty(vh.mTitle.getText())) {
            vh.mTitle.setVisibility(View.GONE);
            hasTitle = false;
        } else {
            vh.mTitle.setVisibility(View.VISIBLE);
            vh.mTitle.setLineSpacing(vh.mTitleLineSpacing - vh.mTitle.getLineHeight()
                    + vh.mTitle.getLineSpacingExtra(), vh.mTitle.getLineSpacingMultiplier());
            vh.mTitle.setMaxLines(vh.mTitleMaxLines);
        }
        setTopMargin(vh.mTitle, vh.mTitleMargin);

        boolean hasSubtitle = true;
        if (TextUtils.isEmpty(vh.mLid.getText())) {
            vh.mLid.setVisibility(View.GONE);
            hasSubtitle = false;
        } else {
            vh.mLid.setVisibility(View.VISIBLE);
            if (hasTitle) {
                setTopMargin(vh.mLid, vh.mUnderTitleBaselineMargin
                        + vh.mSubtitleFontMetricsInt.ascent - vh.mTitleFontMetricsInt.descent);
            } else {
                setTopMargin(vh.mLid, 0);
            }
        }

        if (TextUtils.isEmpty(vh.mDesc.getText())) {
            vh.mDesc.setVisibility(View.GONE);
        } else {
            vh.mDesc.setVisibility(View.VISIBLE);
            vh.mDesc.setLineSpacing(vh.mBodyLineSpacing - vh.mDesc.getLineHeight()
                    + vh.mDesc.getLineSpacingExtra(), vh.mDesc.getLineSpacingMultiplier());

            if (hasSubtitle) {
                setTopMargin(vh.mDesc, vh.mUnderSubtitleBaselineMargin
                        + vh.mBodyFontMetricsInt.ascent - vh.mSubtitleFontMetricsInt.descent);
            } else if (hasTitle) {
                setTopMargin(vh.mDesc, vh.mUnderTitleBaselineMargin
                        + vh.mBodyFontMetricsInt.ascent - vh.mTitleFontMetricsInt.descent);
            } else {
                setTopMargin(vh.mDesc, 0);
            }
        }
    }

    protected abstract void onBindDescription(ViewHolder vh, Object item);

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {}

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder holder) {
        // In case predraw listener was removed in detach, make sure
        // we have the proper layout.
        ViewHolder vh = (ViewHolder) holder;
        vh.addPreDrawListener();
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(Presenter.ViewHolder holder) {
        ViewHolder vh = (ViewHolder) holder;
        vh.removePreDrawListener();
        super.onViewDetachedFromWindow(holder);
    }

    private void setTopMargin(TextView textView, int topMargin) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
        lp.topMargin = topMargin;
        textView.setLayoutParams(lp);
    }
}
