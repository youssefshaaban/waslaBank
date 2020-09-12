package com.waslabank.wasslabank.ui;

import android.app.Activity;
import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.waslabank.wasslabank.R;
import com.waslabank.wasslabank.adapters.FriendsAdapter;
import com.waslabank.wasslabank.models.BasicObject;
import com.waslabank.wasslabank.models.UserModel;
import com.waslabank.wasslabank.networkUtils.Connector;
import com.waslabank.wasslabank.networkUtils.NetworkClient;
import com.waslabank.wasslabank.utils.Helper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFriendsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private final String TAG = FriendsActivity.class.getSimpleName();
    @BindView(R.id.friends_recycler)
    RecyclerView mFriendsRecycler;
    @BindView(R.id.done)
    ImageView mDone;
    @BindView(R.id.search)
    EditText mSearch;

    ArrayList<UserModel> mUserModels;

    Connector mConnector;
    ProgressDialog mProgressDialog;

    FriendsAdapter mFriendsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        ButterKnife.bind(this);
        mUserModels = new ArrayList<>();

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mToolbar.setNavigationOnClickListener(v -> finish());

        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)){
                    mUserModels.clear();
                    mUserModels.addAll(Connector.getUsersAdd(response));
                    mFriendsAdapter.notifyDataSetChanged();
                } else {
                    Helper.showSnackBarMessage(getString(R.string.no_results),SearchFriendsActivity.this);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error),SearchFriendsActivity.this);

            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog = Helper.showProgressDialog(SearchFriendsActivity.this,getString(R.string.loading),false);
                mConnector.getRequest(TAG,"https://code-grow.com/waslabank/api/search_users?mobile=" + mSearch.getText().toString() + "&user_id=" + Helper.getUserSharedPreferences(SearchFriendsActivity.this).getId());

            }
        });


        mFriendsAdapter = new FriendsAdapter(mUserModels, this, new FriendsAdapter.OnItemClicked() {
            @Override
            public void setOnItemClicked(int position) {

            }

            @Override
            public void refreshData() {

            }
        }, 1);
        mFriendsRecycler.setHasFixedSize(true);
        mFriendsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mFriendsRecycler.setAdapter(mFriendsAdapter);



    }

    private void addFriendRequest(UserModel userModel){
        mProgressDialog=Helper.showProgressDialog(this,getString(R.string.loading),false);
        NetworkClient.getApiService().addFriend(Helper.getUserSharedPreferences(this).getId(),userModel.getId())
                .enqueue(new Callback<BasicObject>() {
                    @Override
                    public void onResponse(Call<BasicObject> call, Response<BasicObject> response) {
                        mProgressDialog.dismiss();
                        if (response.body().getStatus()){
                            //Toast.makeText(SearchFriendsActivity.this,getString(R.string.addSuccess),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicObject> call, Throwable t) {
                        mProgressDialog.dismiss();
                        Toast.makeText(SearchFriendsActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        super.onBackPressed();
    }
}
