package app.com.pio.utility;

import android.animation.Animator;

/**
 * Created by mmichaud on 5/29/15.
 */
public class AnimUtil {

    public static Animator.AnimatorListener blankAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };
}
