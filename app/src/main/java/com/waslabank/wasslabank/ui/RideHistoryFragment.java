package com.waslabank.wasslabank.ui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.waslabank.wasslabank.R;
import com.waslabank.wasslabank.adapters.MyRidesAdapter;
import com.waslabank.wasslabank.models.MyRideModel;
import com.waslabank.wasslabank.networkUtils.Connector;
import com.waslabank.wasslabank.ui.ride_details.RideDetailsActivity;
import com.waslabank.wasslabank.utils.Helper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class RideHistoryFragment extends Fragment {

    private final String TAG = RideHistoryFragment.class.getSimpleName();
    @BindView(R.id.my_rides_recycler)
    RecyclerView mMyRidesRecycler;

    MyRidesAdapter mMyRidesAdapter;
    ArrayList<MyRideModel> mMyRidesModels;

    Connector mConnector;
    ProgressDialog mProgressDialog;

    int mPos;

    public RideHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ride_history, container, false);
        ButterKnife.bind(this, v);
        mMyRidesModels = new ArrayList<>();

        mConnector = new Connector(getContext(), new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)) {
                    mMyRidesModels.addAll(Connector.getMyRequestsHistory(response, getContext()));
                    mMyRidesAdapter.notifyDataSetChanged();
                } else {
                    if (getActivity() != null)
                        Helper.showSnackBarMessage(getString(R.string.no_rides), (AppCompatActivity) getActivity());
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                if (getActivity() != null)
                    Helper.showSnackBarMessage(getString(R.string.error), (AppCompatActivity) getActivity());
            }
        });

        mMyRidesAdapter = new MyRidesAdapter(mMyRidesModels, getContext(), position -> {
//            startActivity(new Intent(getContext(), ConfirmRideRequestActivity.class)
//                    .putExtra("type", "show")
//                    .putExtra("from_id", mMyRidesModels.get(position).getFromId())
//                    .putExtra("user_id", mMyRidesModels.get(position).getUserId())
//                    .putExtra("request", mMyRidesModels.get(position))
//                    .putExtra("Status", mMyRidesModels.get(position).getStatus())
//                    .putExtra("RequestID", mMyRidesModels.get(position).getId()));
            startActivityForResult(new Intent(getActivity(), RideDetailsActivity.class)
                            .putExtra("request_id",mMyRidesModels.get(position).getId())
                    ,1);
        });
        mMyRidesRecycler.setHasFixedSize(true);
        mMyRidesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mMyRidesRecycler.setAdapter(mMyRidesAdapter);
        mProgressDialog = Helper.showProgressDialog(getContext(), getString(R.string.loading), false);
        mConnector.getRequest(TAG, "https://code-grow.com/waslabank/api/get_requests?user_id="
                + Helper.getUserSharedPreferences(getContext()).getId());

        return v;
    }

}
