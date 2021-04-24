package com.pharmacy.gts.kprogresshud;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pharmacy.gts.R;

public class KProgressHUD {

    public enum Style {
        SPIN_INDETERMINATE, PIE_DETERMINATE, ANNULAR_DETERMINATE, BAR_DETERMINATE
    }

    // To avoid redundant APIs, make the HUD as a wrapper class around a Dialog
    private ProgressDialog mProgressDialog;
    private float mDimAmount;
    private int mWindowColor;
    private float mCornerRadius;
    private Context mContext;

    private int mAnimateSpeed;

    private int mMaxProgress;
    private boolean mIsAutoDismiss;

    private int mGraceTimeMs;
    private Handler mGraceTimer;
    private boolean mFinished;

    public KProgressHUD(Context context) {
        mContext = context;
        mProgressDialog = new ProgressDialog(context);
        mDimAmount = 0;
        //noinspection deprecation
        mWindowColor = context.getResources().getColor(R.color.kprogresshud_default_color);
        mAnimateSpeed = 1;
        mCornerRadius = 10;
        mIsAutoDismiss = true;
        mGraceTimeMs = 0;
        mFinished = false;

        setStyle(Style.SPIN_INDETERMINATE);
    }

    public static KProgressHUD create(Context context) {
        return new KProgressHUD(context);
    }


    public static KProgressHUD create(Context context, Style style) {
        return new KProgressHUD(context).setStyle(style);
    }


    public KProgressHUD setStyle(Style style) {
        View view = null;
        switch (style) {
            case SPIN_INDETERMINATE:
                view = new SpinView(mContext);
                break;
            case PIE_DETERMINATE:
                view = new PieView(mContext);
                break;
            case ANNULAR_DETERMINATE:
                view = new AnnularView(mContext);
                break;
            case BAR_DETERMINATE:
                view = new BarView(mContext);
                break;
            // No custom view style here, because view will be added later
        }
        mProgressDialog.setView(view);
        return this;
    }


    public KProgressHUD setDimAmount(float dimAmount) {
        if (dimAmount >= 0 && dimAmount <= 1) {
            mDimAmount = dimAmount;
        }
        return this;
    }


    public KProgressHUD setSize(int width, int height) {
        mProgressDialog.setSize(width, height);
        return this;
    }

    @Deprecated
    public KProgressHUD setWindowColor(int color) {
        mWindowColor = color;
        return this;
    }

    public KProgressHUD setBackgroundColor(int color) {
        mWindowColor = color;
        return this;
    }


    public KProgressHUD setCornerRadius(float radius) {
        mCornerRadius = radius;
        return this;
    }


    public KProgressHUD setAnimationSpeed(int scale) {
        mAnimateSpeed = scale;
        return this;
    }

    public KProgressHUD setLabel(String label) {
        mProgressDialog.setLabel(label);
        return this;
    }

    public KProgressHUD setLabel(String label, int color) {
        mProgressDialog.setLabel(label, color);
        return this;
    }

    public KProgressHUD setDetailsLabel(String detailsLabel) {
        mProgressDialog.setDetailsLabel(detailsLabel);
        return this;
    }

    public KProgressHUD setDetailsLabel(String detailsLabel, int color) {
        mProgressDialog.setDetailsLabel(detailsLabel, color);
        return this;
    }

    public KProgressHUD setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
        return this;
    }

    public void setProgress(int progress) {
        mProgressDialog.setProgress(progress);
    }

    public KProgressHUD setCustomView(View view) {
        if (view != null) {
            mProgressDialog.setView(view);
        } else {
            throw new RuntimeException("Custom view must not be null!");
        }
        return this;
    }

    public KProgressHUD setCancellable(boolean isCancellable) {
        mProgressDialog.setCancelable(isCancellable);
        mProgressDialog.setOnCancelListener(null);
        return this;
    }

    public KProgressHUD setCancellable(DialogInterface.OnCancelListener listener) {
        mProgressDialog.setCancelable(null != listener);
        mProgressDialog.setOnCancelListener(listener);
        return this;
    }

    public KProgressHUD setAutoDismiss(boolean isAutoDismiss) {
        mIsAutoDismiss = isAutoDismiss;
        return this;
    }

    public KProgressHUD setGraceTime(int graceTimeMs) {
        mGraceTimeMs = graceTimeMs;
        return this;
    }

    public KProgressHUD show() {
        if (!isShowing()) {
            mFinished = false;
            if (mGraceTimeMs == 0) {
                mProgressDialog.show();
            } else {
                mGraceTimer = new Handler();
                mGraceTimer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgressDialog != null && !mFinished) {
                            mProgressDialog.show();
                        }
                    }
                }, mGraceTimeMs);
            }
        }
        return this;
    }

    public boolean isShowing() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    public void dismiss() {
        mFinished = true;
        if (mContext != null && mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (mGraceTimer != null) {
            mGraceTimer.removeCallbacksAndMessages(null);
            mGraceTimer = null;
        }
    }

    private class ProgressDialog extends Dialog {

        private Determinate mDeterminateView;
        private Indeterminate mIndeterminateView;
        private View mView;
		private TextView mLabelText;
        private TextView mDetailsText;
        private String mLabel;
        private String mDetailsLabel;
        private FrameLayout mCustomViewContainer;
        private BackgroundLayout mBackgroundLayout;
        private int mWidth, mHeight;
        private int mLabelColor = Color.WHITE;
        private int mDetailColor = Color.WHITE;
		
        public ProgressDialog(Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.kprogresshud_hud);

            Window window = getWindow();
            window.setBackgroundDrawable(new ColorDrawable(0));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.dimAmount = mDimAmount;
            layoutParams.gravity = Gravity.CENTER;
            window.setAttributes(layoutParams);

            setCanceledOnTouchOutside(false);

            initViews();
        }

        private void initViews() {
            mBackgroundLayout = (BackgroundLayout) findViewById(R.id.background);
            mBackgroundLayout.setBaseColor(mWindowColor);
            mBackgroundLayout.setCornerRadius(mCornerRadius);
            if (mWidth != 0) {
                updateBackgroundSize();
            }

            mCustomViewContainer = (FrameLayout) findViewById(R.id.container);
            addViewToFrame(mView);

            if (mDeterminateView != null) {
                mDeterminateView.setMax(mMaxProgress);
            }
            if (mIndeterminateView != null) {
                mIndeterminateView.setAnimationSpeed(mAnimateSpeed);
            }

            mLabelText = (TextView) findViewById(R.id.label);
            setLabel(mLabel, mLabelColor);
            mDetailsText = (TextView) findViewById(R.id.details_label);
            setDetailsLabel(mDetailsLabel, mDetailColor);
        }

        private void addViewToFrame(View view) {
            if (view == null) return;
            int wrapParam = ViewGroup.LayoutParams.WRAP_CONTENT;
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(wrapParam, wrapParam);
            mCustomViewContainer.addView(view, params);
        }

        private void updateBackgroundSize() {
            ViewGroup.LayoutParams params = mBackgroundLayout.getLayoutParams();
            params.width = Helper.dpToPixel(mWidth, getContext());
            params.height = Helper.dpToPixel(mHeight, getContext());
            mBackgroundLayout.setLayoutParams(params);
        }

        public void setProgress(int progress) {
            if (mDeterminateView != null) {
                mDeterminateView.setProgress(progress);
                if (mIsAutoDismiss && progress >= mMaxProgress) {
                    dismiss();
                }
            }
        }

        public void setView(View view) {
            if (view != null) {
                if (view instanceof Determinate) {
                    mDeterminateView = (Determinate) view;
                }
                if (view instanceof Indeterminate) {
                    mIndeterminateView = (Indeterminate) view;
                }
                mView = view;
                if (isShowing()) {
                    mCustomViewContainer.removeAllViews();
                    addViewToFrame(view);
                }
            }
        }

        public void setLabel(String label) {
            mLabel = label;
            if (mLabelText != null) {
                if (label != null) {
                    mLabelText.setText(label);
                    mLabelText.setVisibility(View.VISIBLE);
                } else {
                    mLabelText.setVisibility(View.GONE);
                }
            }
        }

        public void setDetailsLabel(String detailsLabel) {
            mDetailsLabel = detailsLabel;
            if (mDetailsText != null) {
                if (detailsLabel != null) {
                    mDetailsText.setText(detailsLabel);
                    mDetailsText.setVisibility(View.VISIBLE);
                } else {
                    mDetailsText.setVisibility(View.GONE);
                }
            }
        }

        public void setLabel(String label, int color) {
            mLabel = label;
            mLabelColor = color;
            if (mLabelText != null) {
                if (label != null) {
                    mLabelText.setText(label);
                    mLabelText.setTextColor(color);
                    mLabelText.setVisibility(View.VISIBLE);
                } else {
                    mLabelText.setVisibility(View.GONE);
                }
            }
        }

        public void setDetailsLabel(String detailsLabel, int color) {
            mDetailsLabel = detailsLabel;
            mDetailColor = color;
            if (mDetailsText != null) {
                if (detailsLabel != null) {
                    mDetailsText.setText(detailsLabel);
                    mDetailsText.setTextColor(color);
                    mDetailsText.setVisibility(View.VISIBLE);
                } else {
                    mDetailsText.setVisibility(View.GONE);
                }
            }
        }

        public void setSize(int width, int height) {
            mWidth = width;
            mHeight = height;
            if (mBackgroundLayout != null) {
                updateBackgroundSize();
            }
        }
    }
}
