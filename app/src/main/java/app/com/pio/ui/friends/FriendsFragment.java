package app.com.pio.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.List;

import app.com.pio.R;
import app.com.pio.api.PioApiController;
import app.com.pio.api.PioApiResponse;
import app.com.pio.features.profiles.ProfileManager;
import app.com.pio.ui.friends.friendslist.FriendsListAdapter;
import app.com.pio.ui.friends.friendslist.FriendsListItem;
import app.com.pio.utility.PrefUtil;
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

    List<FriendsListItem> friendsListItems;
    FriendsListAdapter friendsListAdapter;

    private CallbackManager callbackManager;

    String facebookToken;

    public static FriendsFragment newInstance() {
        return new FriendsFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.inject(this, root);

        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        facebookToken = PrefUtil.getPref(getActivity(), PrefUtil.PREFS_FACEBOOK_TOKEN, null);

        if (facebookToken != null) {
            fbLayout.setVisibility(View.VISIBLE);


            GraphRequest request = GraphRequest.newMyFriendsRequest(
                    AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
                        @Override
                        public void onCompleted(JSONArray objects, GraphResponse response) {
                            if (objects == null) {
                                failureMessage.setVisibility(View.VISIBLE);
                                return;
                            }
                            failureMessage.setVisibility(View.GONE);
                            friendsListItems = new ArrayList<>();
                            for (int i = 0; i < objects.length(); i++) {
                                try {
                                    friendsListItems.add(new FriendsListItem(
                                            objects.getJSONObject(i).getString("name"),
                                            objects.getJSONObject(i).getString("id"),
                                            "https://graph.facebook.com/" + objects.getJSONObject(i).getString("id") + "/picture?type=large",
                                            0));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            friendsListItems.add(new FriendsListItem(
                                    "Selina Sampieri",
                                    "100000214688145",
                                    "https://graph.facebook.com/100000214688145/picture?type=large",
                                    0));
                            friendsListItems.add(new FriendsListItem(
                                    "Molly Michaud",
                                    "100002792053955",
                                    "https://graph.facebook.com/100002792053955/picture?type=large",
                                    0));
                            friendsListItems.add(new FriendsListItem(
                                    "Lauren Michaud",
                                    "626046334",
                                    "https://graph.facebook.com/626046334/picture?type=large",
                                    0));
                            friendsListAdapter = new FriendsListAdapter(getContext(), R.layout.adapter_friends_list_item, friendsListItems);
                            friendsList.setAdapter(friendsListAdapter);
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
                                PrefUtil.savePref(getActivity(), PrefUtil.PREFS_FACEBOOK_TOKEN, facebookToken);
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

        return root;
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
