package app.com.pio.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.com.pio.R;
import app.com.pio.api.FriendsProfileResponse;
import app.com.pio.api.PioApiController;
import app.com.pio.api.PioApiResponse;
import app.com.pio.features.friends.FriendsDBHelper;
import app.com.pio.features.profiles.ProfileManager;
import app.com.pio.ui.FlowLayout;
import app.com.pio.ui.friends.friendslist.FriendsListAdapter;
import app.com.pio.ui.friends.friendslist.FriendsListItem;
import app.com.pio.utility.Util;
import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mmichaud on 7/13/15.
 */
public class FriendsFragment extends Fragment {

    private static final String TAG = FriendsFragment.class.getName();

    View root;
    @InjectView(R.id.friends_fb_layout)
    LinearLayout fbLayout;
    @InjectView(R.id.friends_fb_list)
    ListView friendsList;
    @InjectView(R.id.friends_no_fb_layout)
    LinearLayout noFbLayout;
    @InjectView(R.id.friends_no_fb_button_facebook)
    LoginButton noFbButtonFacebook;
    @InjectView(R.id.friends_failure_message)
    TextView failureMessage;
    @InjectView(R.id.friends_loading)
    ProgressBar loading;
    @InjectView(R.id.friends_fb_updating)
    LinearLayout fbUpdating;
    @InjectView(R.id.friends_fb_standing)
    LinearLayout fbStanding;
    @InjectView(R.id.friends_fb_standing_message)
    TextView fbStandingMessage;


    List<FriendsListItem> friendsListItems;
    FriendsListAdapter friendsListAdapter;

    public int loadingCount = 0;

    private CallbackManager callbackManager;

    private FriendsDBHelper dbHelper;

    String facebookToken;

    public static FriendsFragment newInstance() {
        return new FriendsFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.inject(this, root);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (AccessToken.getCurrentAccessToken() != null) {
            facebookToken = AccessToken.getCurrentAccessToken().getToken();
        }

        dbHelper = new FriendsDBHelper(getActivity());

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        beginDraw();
                    }
                });
            }
        }, 350);

        return root;
    }

    @Override
    public void onPause() {
        dbHelper.storeFriends(friendsListItems);
        super.onPause();
    }

    private void beginDraw() {
        loading.setVisibility(View.VISIBLE);
        friendsListItems = dbHelper.retrieveFriends();
        friendsListAdapter = new FriendsListAdapter(this, R.layout.adapter_friends_list_item, friendsListItems);
        friendsList.setAdapter(friendsListAdapter);
        friendsList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final RelativeLayout monumentsParent = (RelativeLayout) view.findViewById(R.id.friends_list_monuments_parent);
                monumentsParent.measure(FlowLayout.LayoutParams.MATCH_PARENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                if (monumentsParent.getVisibility() == View.GONE) {

                    final int targetHeight = monumentsParent.getMeasuredHeight();
                    monumentsParent.getLayoutParams().height = 1;
                    monumentsParent.setVisibility(View.VISIBLE);
                    Animation animation = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            monumentsParent.getLayoutParams().height = interpolatedTime == 1
                                    ? FlowLayout.LayoutParams.WRAP_CONTENT
                                    : (int) (targetHeight * interpolatedTime);
                            monumentsParent.requestLayout();
                        }

                        @Override
                        public boolean willChangeBounds() {
                            return true;
                        }
                    };
                    animation.setDuration(350);
                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
                    monumentsParent.startAnimation(animation);
                } else {
                    final int initialHeight = monumentsParent.getMeasuredHeight();
                    monumentsParent.setVisibility(View.VISIBLE);
                    Animation animation = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            if (interpolatedTime == 1) {
                                monumentsParent.setVisibility(View.GONE);
                            } else {
                                monumentsParent.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                                monumentsParent.requestLayout();
                            }
                        }

                        @Override
                        public boolean willChangeBounds() {
                            return true;
                        }
                    };
                    animation.setDuration(350);
                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
                    monumentsParent.startAnimation(animation);
                }
            }
        });
        if (friendsListItems.size() != 0) {
            loading.setVisibility(View.GONE);
        }

        if (facebookToken != null) {
            boolean addYou = true;
            for (FriendsListItem friendsListItem : friendsListItems) {
                if (friendsListItem.getUserId().equals(ProfileManager.activeProfile.getFacebook().getUserId())) {
                    addYou = false;
                    break;
                }
            }
            if (friendsListItems.size() != 0 && addYou) {
                friendsListItems.add(new FriendsListItem(
                        "You",
                        AccessToken.getCurrentAccessToken().getUserId(),
                        "https://graph.facebook.com/" + AccessToken.getCurrentAccessToken().getUserId() + "/picture?type=small",
                        ProfileManager.activeProfile.getMonumentsString(),
                        ProfileManager.activeProfile.getXp()));
            }
            // get all your friends from facebook
            GraphRequest request = GraphRequest.newMyFriendsRequest(
                    AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
                        @Override
                        public void onCompleted(JSONArray objects, GraphResponse response) {
                            if (objects == null || objects.length() == 0) {
                                fbLayout.setVisibility(View.GONE);
                                failureMessage.setVisibility(View.VISIBLE);
                                return;
                            }
                            loading.setVisibility(View.GONE);
                            fbLayout.setVisibility(View.VISIBLE);

                            for (int i = 0; i < objects.length(); i++) {
                                try {
                                    final FriendsListItem friendsListItem = new FriendsListItem(
                                            objects.getJSONObject(i).getString("name"),
                                            objects.getJSONObject(i).getString("id"),
                                            "https://graph.facebook.com/" + objects.getJSONObject(i).getString("id") + "/picture?type=small",
                                            "",
                                            0);

                                    int e = -1;
                                    for (int r = 0; r < friendsListItems.size(); r++) {
                                        if (friendsListItems.get(r).getUserId().equals(friendsListItem.getUserId())) {
                                            e = r;
                                            break;
                                        }
                                    }
                                    if (e != -1) {
                                        FriendsListItem oldItem = friendsListItems.get(e);
                                        oldItem.setName(friendsListItem.getName());
                                        oldItem.setUserId(friendsListItem.getUserId());
                                        oldItem.setProfileImageUrl(friendsListItem.getProfileImageUrl());
                                        friendsListItems.set(e, oldItem);
                                    } else {
                                        friendsListItems.add(friendsListItem);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            // TODO remove this when testing is done
//                            if (friendsListItems.size() == 1) {
//                                friendsListItems.add(new FriendsListItem(
//                                        "Selina Sampieri",
//                                        "100000214688145",
//                                        "https://graph.facebook.com/100000214688145/picture?type=large",
//                                        "",
//                                        0));
//                                friendsListItems.add(new FriendsListItem(
//                                        "Molly Michaud",
//                                        "100002792053955",
//                                        "https://graph.facebook.com/100002792053955/picture?type=large",
//                                        "",
//                                        0));
//                                friendsListItems.add(new FriendsListItem(
//                                        "Lauren Michaud",
//                                        "626046334",
//                                        "https://graph.facebook.com/626046334/picture?type=large",
//                                        "",
//                                        0));
//                            }

                            for (final FriendsListItem item : friendsListItems) {
                                loadingCount++;
                                if (loadingCount == 1) {
                                    fbUpdating.setVisibility(View.VISIBLE);
                                }
                                PioApiController.getFriendsProfile(item.getUserId(), new Callback<FriendsProfileResponse>() {
                                    @Override
                                    public void success(FriendsProfileResponse friendsProfileResponse, Response response) {
                                        item.setXp(friendsProfileResponse.getFriendsProfile().getXp());
                                        ArrayList<String> monumentsList = friendsProfileResponse.getFriendsProfile().getMonuments();
                                        String[] monumentsArray = new String[monumentsList.size()];
                                        for (int r = 0; r < monumentsList.size(); r++) {
                                            monumentsArray[r] = monumentsList.get(r);
                                        }
                                        item.setMonuments(monumentsArray);
                                        updateFriendsListItem(item);
                                        loadingCount--;
                                        if (loadingCount == 0) {
                                            fbUpdating.setVisibility(View.GONE);
                                            showStanding();
                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        loadingCount--;
                                        if (loadingCount == 0) {
                                            fbUpdating.setVisibility(View.GONE);
                                            showStanding();
                                        }
                                    }
                                });
                            }
                            if (FriendsFragment.this.isAdded()) {
                                friendsListAdapter = new FriendsListAdapter(FriendsFragment.this, R.layout.adapter_friends_list_item, friendsListItems);
                                friendsList.setAdapter(friendsListAdapter);
                            }
                        }
                    });
            request.executeAsync();
        } else {
            noFbLayout.setVisibility(View.VISIBLE);

            noFbButtonFacebook.setReadPermissions("user_friends");
            callbackManager = CallbackManager.Factory.create();
            noFbButtonFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    loading.setVisibility(View.VISIBLE);
                    PioApiController.fbUserIdInUse(loginResult.getAccessToken().getUserId(), new Callback<PioApiResponse>() {
                        @Override
                        public void success(PioApiResponse pioApiResponse, Response response) {
                            loading.setVisibility(View.GONE);
                            if (pioApiResponse.getMsg().equals("false")) {
                                facebookToken = loginResult.getAccessToken().getToken();
                                ProfileManager.activeProfile.setFacebook(loginResult.getAccessToken().getUserId());
                                ProfileManager.saveActiveProfile();
                                noFbLayout.setVisibility(View.GONE);
                                fbLayout.setVisibility(View.VISIBLE);
                            } else {
                                Util.makeCroutonText("This Facebook account is already in use", getActivity());
                                LoginManager.getInstance().logOut();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            loading.setVisibility(View.GONE);
                            Log.e(TAG, error.getResponse().toString());
                        }
                    });

                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.d(TAG, "Could not log in with facebook. onError: " + exception.getMessage());
                    Util.makeCroutonText("Could not sign in with Facebook, please try again later", getActivity());
                }
            });
        }
    }

    public void showStanding() {
        fbStanding.setVisibility(View.VISIBLE);
        if (friendsListItems.size() == 0) {
            fbStanding.setVisibility(View.GONE);
            return;
        }
        int position = -1;
        for (int i = 0; i < friendsListItems.size(); i++) {
            if (friendsListItems.get(i).getUserId().equals(ProfileManager.activeProfile.getFacebook().getUserId())) {
                position = i;
                break;
            }
        }
        String positionString = "nth";
        if (position == 0) {
            positionString = "";
        } else if (position == 1) {
            positionString = " 2nd";
        } else if (position == 2) {
            positionString = " 3rd";
        } else {
            positionString = " "+(position+1)+"th";
        }
        String message = "You are the"+positionString+" most adventurous player among your friends!";
        fbStandingMessage.setText(message);
    }

    public void updateFriendsListItem(FriendsListItem item) {
        for (int i = 0; i < friendsListItems.size(); i++) {
            if (item.getUserId().equals(friendsListItems.get(i).getUserId())) {
                friendsListItems.set(i, item);
            }
        }
        Collections.sort(friendsListItems, new Comparator<FriendsListItem>() {
            @Override
            public int compare(FriendsListItem item1, FriendsListItem item2) {
                return item2.getXp() - item1.getXp();
            }
        });
        friendsListAdapter.notifyDataSetChanged();
    }

    public void updateFriendsListItem(FriendsProfileResponse friendsProfileResponse, String userId) {
        for (int i = 0; i < friendsListItems.size(); i++) {
            if (userId.equals(friendsListItems.get(i).getUserId())) {
                FriendsListItem oldItem = friendsListItems.get(i);
                oldItem.setName(friendsProfileResponse.getFriendsProfile().getName());
                oldItem.setProfileImageUrl(friendsProfileResponse.getFriendsProfile().getImage());
                oldItem.setXp(friendsProfileResponse.getFriendsProfile().getXp());
                ArrayList<String> monumentsList = friendsProfileResponse.getFriendsProfile().getMonuments();
                String[] monumentsArray = new String[monumentsList.size()];
                for (int r = 0; r < monumentsList.size(); r++) {
                    monumentsArray[r] = monumentsList.get(r);
                }
                oldItem.setMonuments(monumentsArray);
                friendsListItems.set(i, oldItem);
                break;
            }
        }
        Collections.sort(friendsListItems, new Comparator<FriendsListItem>() {
            @Override
            public int compare(FriendsListItem item1, FriendsListItem item2) {
                return item1.getXp() - item2.getXp();
            }
        });
        friendsListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Friends");
    }
}
