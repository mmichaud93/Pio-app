package app.com.pio.ui.welcome;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import app.com.pio.R;
import app.com.pio.api.PioApiController;
import app.com.pio.api.PioApiResponse;
import app.com.pio.models.WelcomePageModel;
import app.com.pio.ui.main.MainActivity;
import app.com.pio.utility.AnimUtil;
import app.com.pio.utility.PrefUtil;
import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static app.com.pio.utility.Util.*;

/**
 * Created by mmichaud on 5/28/15.
 */
public class WelcomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    View root;
    @InjectView(R.id.sign_in_parent)
    LinearLayout signInParent;
    @InjectView(R.id.sign_in_button_google)
    SignInButton signInButtonGoogle;
    @InjectView(R.id.sign_in_button_email)
    Button signInButtonEmail;
    @InjectView(R.id.welcome_pager)
    ViewPager viewPager;
    @InjectView(R.id.welcome_bullet_parent)
    LinearLayout welcomeBulletParent;
    @InjectView(R.id.welcome_bullet_one)
    ImageView bulletOne;
    @InjectView(R.id.welcome_bullet_two)
    ImageView bulletTwo;
    @InjectView(R.id.welcome_bullet_three)
    ImageView bulletThree;
    @InjectView(R.id.welcome_email_sign_in_parent)
    RelativeLayout emailSignIn;
    @InjectView(R.id.welcome_email_back_button)
    Button emailBackButton;
    @InjectView(R.id.welcome_email_submit_button)
    Button emailSubmitButton;
    @InjectView(R.id.welcome_email_edit_email)
    EditText emailEditEmail;
    @InjectView(R.id.welcome_email_edit_password)
    EditText emailEditPassword;

    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mSignInClicked;
    private boolean mIntentInProgress;

    private boolean emailIsGood = false;

    WelcomePagerAdapter welcomePagerAdapter;

    int animationSpeed = 375;
    int animationSpeedLong = 550;

    public static WelcomeFragment newInstance() {

        return new WelcomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.inject(this, root);

        welcomePagerAdapter = new WelcomePagerAdapter(this, new WelcomePageModel[]{
                new WelcomePageModel("Pio tracks your location over time to show where you've been before.", R.drawable.pio_screen_1),
                new WelcomePageModel("Use Pio to explore new parts of the world.", R.drawable.pio_screen_2),
                new WelcomePageModel("Unlock achievements and compare stats with your friends.", 0)
        });

        viewPager.setAdapter(welcomePagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bulletOne.setImageResource(R.drawable.bullet_selected);
                        bulletTwo.setImageResource(R.drawable.bullet);
                        bulletThree.setImageResource(R.drawable.bullet);
                        break;
                    case 1:
                        bulletOne.setImageResource(R.drawable.bullet);
                        bulletTwo.setImageResource(R.drawable.bullet_selected);
                        bulletThree.setImageResource(R.drawable.bullet);
                        break;
                    case 2:
                        bulletOne.setImageResource(R.drawable.bullet);
                        bulletTwo.setImageResource(R.drawable.bullet);
                        bulletThree.setImageResource(R.drawable.bullet_selected);

                        if (signInParent.getVisibility() == View.GONE) {
                            // animate the buttons up

                            signInParent.animate().translationYBy(dpToPx(80, getActivity())).setDuration(1).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                    signInParent.setAlpha(0);
                                    signInParent.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    signInParent.setAlpha(1);
                                    welcomeBulletParent.animate().setInterpolator(new AccelerateDecelerateInterpolator()).translationYBy(dpToPx(-80, getActivity())).setDuration(animationSpeed).start();
                                    signInParent.animate().setInterpolator(new AccelerateDecelerateInterpolator()).translationYBy(dpToPx(-80, getActivity())).setDuration(animationSpeed).setListener(AnimUtil.blankAnimationListener).start();
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            }).start();
                        }

                        break;
                    default:
                        bulletOne.setImageResource(R.drawable.bullet);
                        bulletTwo.setImageResource(R.drawable.bullet);
                        bulletThree.setImageResource(R.drawable.bullet);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        signInButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGoogleApiClient.isConnecting()) {
                    mSignInClicked = true;
                    mGoogleApiClient.connect();
                }
            }
        });

        signInButtonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandEmailSignIn();
            }
        });

        emailBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapseEmailSignIn();
            }
        });

        emailEditEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (validateText(editable.toString(), ValidateType.EMAIL)) {
                    PioApiController.userExists(editable.toString(), new Callback<PioApiResponse>() {
                        @Override
                        public void success(PioApiResponse pioApiResponse, Response response) {
                            if (pioApiResponse.getMsg().equals("false")) {
                                emailIsGood = true;
                                emailEditEmail.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_check_mark), null);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            emailIsGood = false;
                            emailEditEmail.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_x_mark), null);
                        }
                    });
                } else {
                    emailIsGood = false;
                    emailEditEmail.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_x_mark), null);
                }
                unlockSubmit();
            }
        });

        emailEditPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(validateText(editable.toString(), ValidateType.PASSWORD)) {
                    emailEditPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_check_mark), null);
                } else {
                    emailEditPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_x_mark), null);
                }
                unlockSubmit();
            }
        });

        emailSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefUtil.savePrefs(getActivity(), PrefUtil.PREFS_LOGIN_TYPE_KEY, PrefUtil.LoginTypes.EMAIL.name());
                PrefUtil.savePrefs(getActivity(), PrefUtil.PREFS_LOGIN_EMAIL_KEY, emailEditEmail.getText().toString());
                PrefUtil.savePrefs(getActivity(), PrefUtil.PREFS_LOGIN_PASSWORD_KEY, emailEditPassword.getText().toString());

                PioApiController.sendNewUser(emailEditEmail.getText().toString(),
                        emailEditPassword.getText().toString(), PrefUtil.LoginTypes.EMAIL.name(), new Callback<PioApiResponse>() {
                            @Override
                            public void success(PioApiResponse pioApiResponse, Response response) {
                                if(pioApiResponse.getCode()==200) {
                                    ((MainActivity) getActivity()).initRegularApp(null);
                                    getActivity().supportInvalidateOptionsMenu();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope("profile"))
                .addScope(new Scope("email"))
                .build();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;

        // do things with the login info
        PrefUtil.savePrefs(getActivity(), PrefUtil.PREFS_LOGIN_TYPE_KEY, PrefUtil.LoginTypes.GOOGLE.name());
        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        if (email != null) {
            PrefUtil.savePrefs(getActivity(), PrefUtil.PREFS_LOGIN_EMAIL_KEY, email);
        }

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).hasId()) {
                PrefUtil.savePrefs(getActivity(), PrefUtil.PREFS_LOGIN_PASSWORD_KEY, Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getId());
            }
        }

        ((MainActivity) getActivity()).initRegularApp(null);
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIntentInProgress) {
            if (mSignInClicked && connectionResult.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    connectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != Activity.RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.reconnect();
            }
        }
    }

    private void expandEmailSignIn() {

        emailSignIn.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(0).y(root.getHeight()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                emailSignIn.setAlpha(0);
                emailSignIn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                emailSignIn.setAlpha(1);
                emailSignIn.animate().y(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(animationSpeedLong).setListener(AnimUtil.blankAnimationListener).start();
                signInParent.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(animationSpeedLong).translationYBy(-1 * root.getHeight()).start();
                welcomeBulletParent.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(animationSpeedLong).translationYBy(-1 * root.getHeight()).start();

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();
    }

    private void collapseEmailSignIn() {
        signInParent.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(animationSpeedLong).translationYBy(root.getHeight()).start();
        welcomeBulletParent.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(animationSpeedLong).translationYBy(root.getHeight()).start();

        emailSignIn.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(animationSpeedLong).y(root.getHeight()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                emailSignIn.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();
    }

    private void unlockSubmit() {

        emailSubmitButton.setEnabled(
                emailIsGood &&
                validateText(emailEditPassword.getText().toString(), ValidateType.PASSWORD));
        emailSubmitButton.setTextColor((
                validateText(emailEditEmail.getText().toString(), ValidateType.EMAIL) &&
                validateText(emailEditPassword.getText().toString(), ValidateType.PASSWORD))? Color.WHITE:getResources().getColor(R.color.disabled_gray_text));
    }
}
