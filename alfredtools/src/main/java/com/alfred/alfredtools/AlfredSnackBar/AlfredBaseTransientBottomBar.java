package com.alfred.alfredtools.AlfredSnackBar;

/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.design.R;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.*;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.alfred.alfredtools.AlfredSnackBar.AlfredAnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR;
import static com.alfred.alfredtools.AlfredSnackBar.AlfredAnimationUtils.LINEAR_INTERPOLATOR;

/**
 * Base class for lightweight transient bars that are displayed along the bottom edge of the
 * application window.
 *
 * @param <B> The transient bottom bar subclass.
 */
public abstract class AlfredBaseTransientBottomBar<B extends AlfredBaseTransientBottomBar<B>> {
    /**
     * Base class for {@link AlfredBaseTransientBottomBar} callbacks.
     *
     * @param <B> The transient bottom bar subclass.
     * @see AlfredBaseTransientBottomBar#addCallback(AlfredBaseTransientBottomBar.BaseCallback)
     */
    public abstract static class BaseCallback<B> {
        /** Indicates that the Snackbar was dismissed via a swipe.*/
        public static final int DISMISS_EVENT_SWIPE = 0;
        /** Indicates that the Snackbar was dismissed via an action click.*/
        public static final int DISMISS_EVENT_ACTION = 1;
        /** Indicates that the Snackbar was dismissed via a timeout.*/
        public static final int DISMISS_EVENT_TIMEOUT = 2;
        /** Indicates that the Snackbar was dismissed via a call to {@link #dismiss()}.*/
        public static final int DISMISS_EVENT_MANUAL = 3;
        /** Indicates that the Snackbar was dismissed from a new Snackbar being shown.*/
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;

        /** @hide */
        @RestrictTo(LIBRARY_GROUP)
        @IntDef({DISMISS_EVENT_SWIPE, DISMISS_EVENT_ACTION, DISMISS_EVENT_TIMEOUT,
                DISMISS_EVENT_MANUAL, DISMISS_EVENT_CONSECUTIVE})
        @Retention(RetentionPolicy.SOURCE)
        public @interface DismissEvent {}

        /**
         * Called when the given {@link AlfredBaseTransientBottomBar} has been dismissed, either
         * through a time-out, having been manually dismissed, or an action being clicked.
         *
         * @param transientBottomBar The transient bottom bar which has been dismissed.
         * @param event The event which caused the dismissal. One of either:
         *              {@link #DISMISS_EVENT_SWIPE}, {@link #DISMISS_EVENT_ACTION},
         *              {@link #DISMISS_EVENT_TIMEOUT}, {@link #DISMISS_EVENT_MANUAL} or
         *              {@link #DISMISS_EVENT_CONSECUTIVE}.
         *
         * @see AlfredBaseTransientBottomBar#dismiss()
         */
        public void onDismissed(B transientBottomBar, @AlfredBaseTransientBottomBar.BaseCallback.DismissEvent int event) {
            // empty
        }

        /**
         * Called when the given {@link AlfredBaseTransientBottomBar} is visible.
         *
         * @param transientBottomBar The transient bottom bar which is now visible.
         * @see AlfredBaseTransientBottomBar#show()
         */
        public void onShown(B transientBottomBar) {
            // empty
        }
    }

    /**
     * Interface that defines the behavior of the main content of a transient bottom bar.
     */
    public interface ContentViewCallback {
        /**
         * Animates the content of the transient bottom bar in.
         *
         * @param delay Animation delay.
         * @param duration Animation duration.
         */
        void animateContentIn(int delay, int duration);

        /**
         * Animates the content of the transient bottom bar out.
         *
         * @param delay Animation delay.
         * @param duration Animation duration.
         */
        void animateContentOut(int delay, int duration);
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    @IntDef({LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG})
    @IntRange(from = 1)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {}

    /**
     * Show the Snackbar indefinitely. This means that the Snackbar will be displayed from the time
     * that is {@link #show() shown} until either it is dismissed, or another Snackbar is shown.
     *
     * @see #setDuration
     */
    public static final int LENGTH_INDEFINITE = -2;

    /**
     * Show the Snackbar for a short period of time.
     *
     * @see #setDuration
     */
    public static final int LENGTH_SHORT = -1;

    /**
     * Show the Snackbar for a long period of time.
     *
     * @see #setDuration
     */
    public static final int LENGTH_LONG = 0;

    static final int ANIMATION_DURATION = 250;
    static final int ANIMATION_FADE_DURATION = 180;

    static final Handler sHandler;
    static final int MSG_SHOW = 0;
    static final int MSG_DISMISS = 1;

    // On JB/KK versions of the platform sometimes View.setTranslationY does not
    // result in layout / draw pass, and CoordinatorLayout relies on a draw pass to
    // happen to sync vertical positioning of all its child views
    private static final boolean USE_OFFSET_API = (Build.VERSION.SDK_INT >= 16)
            && (Build.VERSION.SDK_INT <= 19);

    static {
        sHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_SHOW:
                        ((AlfredBaseTransientBottomBar) message.obj).showView();
                        return true;
                    case MSG_DISMISS:
                        ((AlfredBaseTransientBottomBar) message.obj).hideView(message.arg1);
                        return true;
                }
                return false;
            }
        });
    }

    private final ViewGroup mTargetParent;
    private final Context mContext;
    final AlfredBaseTransientBottomBar.SnackbarBaseLayout mView;
    private final AlfredBaseTransientBottomBar.ContentViewCallback mContentViewCallback;
    private int mDuration;

    private List<AlfredBaseTransientBottomBar.BaseCallback<B>> mCallbacks;

    private final AccessibilityManager mAccessibilityManager;

    /**
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    interface OnLayoutChangeListener {
        void onLayoutChange(View view, int left, int top, int right, int bottom);
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    interface OnAttachStateChangeListener {
        void onViewAttachedToWindow(View v);
        void onViewDetachedFromWindow(View v);
    }

    /**
     * Constructor for the transient bottom bar.
     *
     * @param parent The parent for this transient bottom bar.
     * @param content The content view for this transient bottom bar.
     * @param contentViewCallback The content view callback for this transient bottom bar.
     */
    protected AlfredBaseTransientBottomBar(@NonNull ViewGroup parent, @NonNull View content,
                                           @NonNull ContentViewCallback contentViewCallback) {
        if (parent == null) {
            throw new IllegalArgumentException("Transient bottom bar must have non-null parent");
        }
        if (content == null) {
            throw new IllegalArgumentException("Transient bottom bar must have non-null content");
        }
        if (contentViewCallback == null) {
            throw new IllegalArgumentException("Transient bottom bar must have non-null callback");
        }

        mTargetParent = parent;
        mContentViewCallback = contentViewCallback;
        mContext = parent.getContext();

        AlfredThemeUtils.checkAppCompatTheme(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        // Note that for backwards compatibility reasons we inflate a layout that is defined
        // in the extending Snackbar class. This is to prevent breakage of apps that have custom
        // coordinator layout behaviors that depend on that layout.
        mView = (AlfredBaseTransientBottomBar.SnackbarBaseLayout) inflater.inflate(
                com.alfred.alfredtools.R.layout.alfred_design_layout_snackbar, mTargetParent, false);
        mView.addView(content);
//android.support.design.   R.layout.design_layout_snackbar
        ViewCompat.setAccessibilityLiveRegion(mView,
                ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE);
        ViewCompat.setImportantForAccessibility(mView,
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);

        // Make sure that we fit system windows and have a listener to apply any insets
        ViewCompat.setFitsSystemWindows(mView, true);
        ViewCompat.setOnApplyWindowInsetsListener(mView,
                new android.support.v4.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v,
                                                                  WindowInsetsCompat insets) {
                        // Copy over the bottom inset as padding so that we're displayed
                        // above the navigation bar
                        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(),
                                v.getPaddingRight(), insets.getSystemWindowInsetBottom());
                        return insets;
                    }
                });

        mAccessibilityManager = (AccessibilityManager)
                mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    /**
     * Set how long to show the view for.
     *
     * @param duration either be one of the predefined lengths:
     *                 {@link #LENGTH_SHORT}, {@link #LENGTH_LONG}, or a custom duration
     *                 in milliseconds.
     */
    @NonNull
    public B setDuration(@Duration int duration) {
        mDuration = duration;
        return (B) this;
    }

    /**
     * Return the duration.
     *
     * @see #setDuration
     */
    @Duration
    public int getDuration() {
        return mDuration;
    }

    /**
     * Returns the {@link AlfredBaseTransientBottomBar}'s context.
     */
    @NonNull
    public Context getContext() {
        return mContext;
    }

    /**
     * Returns the {@link AlfredBaseTransientBottomBar}'s view.
     */
    @NonNull
    public View getView() {
        return mView;
    }

    /**
     * Show the {@link AlfredBaseTransientBottomBar}.
     */
    public void show() {
        AlfredSnackbarManager.getInstance().show(mDuration, mManagerCallback);
    }

    /**
     * Dismiss the {@link AlfredBaseTransientBottomBar}.
     */
    public void dismiss() {
        dispatchDismiss(AlfredBaseTransientBottomBar.BaseCallback.DISMISS_EVENT_MANUAL);
    }

    void dispatchDismiss(@AlfredBaseTransientBottomBar.BaseCallback.DismissEvent int event) {
        AlfredSnackbarManager.getInstance().dismiss(mManagerCallback, event);
    }

    /**
     * Adds the specified callback to the list of callbacks that will be notified of transient
     * bottom bar events.
     *
     * @param callback Callback to notify when transient bottom bar events occur.
     * @see #removeCallback(AlfredBaseTransientBottomBar.BaseCallback)
     */
    @NonNull
    public B addCallback(@NonNull AlfredBaseTransientBottomBar.BaseCallback<B> callback) {
        if (callback == null) {
            return (B) this;
        }
        if (mCallbacks == null) {
            mCallbacks = new ArrayList<AlfredBaseTransientBottomBar.BaseCallback<B>>();
        }
        mCallbacks.add(callback);
        return (B) this;
    }

    /**
     * Removes the specified callback from the list of callbacks that will be notified of transient
     * bottom bar events.
     *
     * @param callback Callback to remove from being notified of transient bottom bar events
     * @see #addCallback(AlfredBaseTransientBottomBar.BaseCallback)
     */
    @NonNull
    public B removeCallback(@NonNull AlfredBaseTransientBottomBar.BaseCallback<B> callback) {
        if (callback == null) {
            return (B) this;
        }
        if (mCallbacks == null) {
            // This can happen if this method is called before the first call to addCallback
            return (B) this;
        }
        mCallbacks.remove(callback);
        return (B) this;
    }

    /**
     * Return whether this {@link AlfredBaseTransientBottomBar} is currently being shown.
     */
    public boolean isShown() {
        return AlfredSnackbarManager.getInstance().isCurrent(mManagerCallback);
    }

    /**
     * Returns whether this {@link AlfredBaseTransientBottomBar} is currently being shown, or is queued
     * to be shown next.
     */
    public boolean isShownOrQueued() {
        return AlfredSnackbarManager.getInstance().isCurrentOrNext(mManagerCallback);
    }

    final AlfredSnackbarManager.Callback mManagerCallback = new AlfredSnackbarManager.Callback() {
        @Override
        public void show() {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_SHOW, AlfredBaseTransientBottomBar.this));
        }

        @Override
        public void dismiss(int event) {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_DISMISS, event, 0,
                    AlfredBaseTransientBottomBar.this));
        }
    };

    final void showView() {
        if (mView.getParent() == null) {
            final ViewGroup.LayoutParams lp = mView.getLayoutParams();

            if (lp instanceof CoordinatorLayout.LayoutParams) {
                // If our LayoutParams are from a CoordinatorLayout, we'll setup our Behavior
                final CoordinatorLayout.LayoutParams clp = (CoordinatorLayout.LayoutParams) lp;

                final AlfredBaseTransientBottomBar.Behavior behavior = new AlfredBaseTransientBottomBar.Behavior();
                behavior.setStartAlphaSwipeDistance(0.1f);
                behavior.setEndAlphaSwipeDistance(0.6f);
                behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END);
                behavior.setListener(new SwipeDismissBehavior.OnDismissListener() {
                    @Override
                    public void onDismiss(View view) {
                        view.setVisibility(View.GONE);
                        dispatchDismiss(AlfredBaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE);
                    }

                    @Override
                    public void onDragStateChanged(int state) {
                        switch (state) {
                            case SwipeDismissBehavior.STATE_DRAGGING:
                            case SwipeDismissBehavior.STATE_SETTLING:
                                // If the view is being dragged or settling, pause the timeout
                                AlfredSnackbarManager.getInstance().pauseTimeout(mManagerCallback);
                                break;
                            case SwipeDismissBehavior.STATE_IDLE:
                                // If the view has been released and is idle, restore the timeout
                                AlfredSnackbarManager.getInstance()
                                        .restoreTimeoutIfPaused(mManagerCallback);
                                break;
                        }
                    }
                });
                clp.setBehavior(behavior);
                // Also set the inset edge so that views can dodge the bar correctly
                clp.insetEdge = Gravity.BOTTOM;
            }

            mTargetParent.addView(mView);
        }

        mView.setOnAttachStateChangeListener(
                new AlfredBaseTransientBottomBar.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {}

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        if (isShownOrQueued()) {
                            // If we haven't already been dismissed then this event is coming from a
                            // non-user initiated action. Hence we need to make sure that we callback
                            // and keep our state up to date. We need to post the call since
                            // removeView() will call through to onDetachedFromWindow and thus overflow.
                            sHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onViewHidden(AlfredBaseTransientBottomBar.BaseCallback.DISMISS_EVENT_MANUAL);
                                }
                            });
                        }
                    }
                });

        if (ViewCompat.isLaidOut(mView)) {
            if (shouldAnimate()) {
                // If animations are enabled, animate it in
                animateViewIn();
            } else {
                // Else if anims are disabled just call back now
                onViewShown();
            }
        } else {
            // Otherwise, add one of our layout change listeners and show it in when laid out
            mView.setOnLayoutChangeListener(new AlfredBaseTransientBottomBar.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int left, int top, int right, int bottom) {
                    mView.setOnLayoutChangeListener(null);

                    if (shouldAnimate()) {
                        // If animations are enabled, animate it in
                        animateViewIn();
                    } else {
                        // Else if anims are disabled just call back now
                        onViewShown();
                    }
                }
            });
        }
    }

    void animateViewIn() {
        if (Build.VERSION.SDK_INT >= 12) {
            final int viewHeight = mView.getHeight();
            if (USE_OFFSET_API) {
                ViewCompat.offsetTopAndBottom(mView, viewHeight);
            } else {
                mView.setTranslationY(viewHeight);
            }
            final ValueAnimator animator = new ValueAnimator();
            animator.setIntValues(viewHeight, 0);
            animator.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            animator.setDuration(ANIMATION_DURATION);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animator) {
                    mContentViewCallback.animateContentIn(
                            ANIMATION_DURATION - ANIMATION_FADE_DURATION,
                            ANIMATION_FADE_DURATION);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    onViewShown();
                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private int mPreviousAnimatedIntValue = viewHeight;

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    int currentAnimatedIntValue = (int) animator.getAnimatedValue();
                    if (USE_OFFSET_API) {
                        ViewCompat.offsetTopAndBottom(mView,
                                currentAnimatedIntValue - mPreviousAnimatedIntValue);
                    } else {
                        mView.setTranslationY(currentAnimatedIntValue);
                    }
                    mPreviousAnimatedIntValue = currentAnimatedIntValue;
                }
            });
            animator.start();
        } else {
            final Animation anim = AnimationUtils.loadAnimation(mView.getContext(),
                    R.anim.design_snackbar_in);
            anim.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            anim.setDuration(ANIMATION_DURATION);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    onViewShown();
                }

                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            mView.startAnimation(anim);
        }
    }

    private void animateViewOut(final int event) {
        if (Build.VERSION.SDK_INT >= 12) {
            final ValueAnimator animator = new ValueAnimator();
            animator.setIntValues(0, mView.getHeight());
            animator.setInterpolator(LINEAR_INTERPOLATOR);
            animator.setDuration(ANIMATION_DURATION);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animator) {
                    mContentViewCallback.animateContentOut(0, ANIMATION_FADE_DURATION);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    onViewHidden(event);
                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private int mPreviousAnimatedIntValue = 0;

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    int currentAnimatedIntValue = (int) animator.getAnimatedValue();
                    if (USE_OFFSET_API) {
                        ViewCompat.offsetTopAndBottom(mView,
                                currentAnimatedIntValue - mPreviousAnimatedIntValue);
                    } else {
                        mView.setTranslationY(currentAnimatedIntValue);
                    }
                    mPreviousAnimatedIntValue = currentAnimatedIntValue;
                }
            });
            animator.start();
        } else {
            final Animation anim = AnimationUtils.loadAnimation(mView.getContext(),
                    R.anim.design_snackbar_out);
            anim.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            anim.setDuration(ANIMATION_DURATION);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    onViewHidden(event);
                }

                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            mView.startAnimation(anim);
        }
    }

    final void hideView(@AlfredBaseTransientBottomBar.BaseCallback.DismissEvent final int event) {
        if (shouldAnimate() && mView.getVisibility() == View.VISIBLE) {
            animateViewOut(event);
        } else {
            // If anims are disabled or the view isn't visible, just call back now
            onViewHidden(event);
        }
    }

    void onViewShown() {
        AlfredSnackbarManager.getInstance().onShown(mManagerCallback);
        if (mCallbacks != null) {
            // Notify the callbacks. Do that from the end of the list so that if a callback
            // removes itself as the result of being called, it won't mess up with our iteration
            int callbackCount = mCallbacks.size();
            for (int i = callbackCount - 1; i >= 0; i--) {
                mCallbacks.get(i).onShown((B) this);
            }
        }
    }

    void onViewHidden(int event) {
        // First tell the SnackbarManager that it has been dismissed
        AlfredSnackbarManager.getInstance().onDismissed(mManagerCallback);
        if (mCallbacks != null) {
            // Notify the callbacks. Do that from the end of the list so that if a callback
            // removes itself as the result of being called, it won't mess up with our iteration
            int callbackCount = mCallbacks.size();
            for (int i = callbackCount - 1; i >= 0; i--) {
                mCallbacks.get(i).onDismissed((B) this, event);
            }
        }
        if (Build.VERSION.SDK_INT < 11) {
            // We need to hide the Snackbar on pre-v11 since it uses an old style Animation.
            // ViewGroup has special handling in removeView() when getAnimation() != null in
            // that it waits. This then means that the calculated insets are wrong and the
            // any dodging views do not return. We workaround it by setting the view to gone while
            // ViewGroup actually gets around to removing it.
            mView.setVisibility(View.GONE);
        }
        // Lastly, hide and remove the view from the parent (if attached)
        final ViewParent parent = mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mView);
        }
    }

    /**
     * Returns true if we should animate the Snackbar view in/out.
     */
    boolean shouldAnimate() {
        return !mAccessibilityManager.isEnabled();
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    static class SnackbarBaseLayout extends FrameLayout {
        private AlfredBaseTransientBottomBar.OnLayoutChangeListener mOnLayoutChangeListener;
        private AlfredBaseTransientBottomBar.OnAttachStateChangeListener mOnAttachStateChangeListener;

        SnackbarBaseLayout(Context context) {
            this(context, null);
        }

        SnackbarBaseLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SnackbarLayout);
            if (a.hasValue(R.styleable.SnackbarLayout_elevation)) {
                ViewCompat.setElevation(this, a.getDimensionPixelSize(
                        R.styleable.SnackbarLayout_elevation, 0));
            }
            a.recycle();

            setClickable(true);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (mOnLayoutChangeListener != null) {
                mOnLayoutChangeListener.onLayoutChange(this, l, t, r, b);
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (mOnAttachStateChangeListener != null) {
                mOnAttachStateChangeListener.onViewAttachedToWindow(this);
            }

            ViewCompat.requestApplyInsets(this);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (mOnAttachStateChangeListener != null) {
                mOnAttachStateChangeListener.onViewDetachedFromWindow(this);
            }
        }

        void setOnLayoutChangeListener(
                AlfredBaseTransientBottomBar.OnLayoutChangeListener onLayoutChangeListener) {
            mOnLayoutChangeListener = onLayoutChangeListener;
        }

        void setOnAttachStateChangeListener(
                AlfredBaseTransientBottomBar.OnAttachStateChangeListener listener) {
            mOnAttachStateChangeListener = listener;
        }
    }

    final class Behavior extends SwipeDismissBehavior<AlfredBaseTransientBottomBar.SnackbarBaseLayout> {
        @Override
        public boolean canSwipeDismissView(View child) {
            return child instanceof AlfredBaseTransientBottomBar.SnackbarBaseLayout;
        }

        @Override
        public boolean onInterceptTouchEvent(CoordinatorLayout parent, AlfredBaseTransientBottomBar.SnackbarBaseLayout child,
                                             MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    // We want to make sure that we disable any Snackbar timeouts if the user is
                    // currently touching the Snackbar. We restore the timeout when complete
                    if (parent.isPointInChildBounds(child, (int) event.getX(),
                            (int) event.getY())) {
                        AlfredSnackbarManager.getInstance().pauseTimeout(mManagerCallback);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    AlfredSnackbarManager.getInstance().restoreTimeoutIfPaused(mManagerCallback);
                    break;
            }
            return super.onInterceptTouchEvent(parent, child, event);
        }
    }
}
