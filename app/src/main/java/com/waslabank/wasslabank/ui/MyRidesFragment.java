package com.waslabank.wasslabank.ui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

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
public class MyRidesFragment extends Fragment {

    private final String TAG = MyRidesFragment.class.getSimpleName();
    @BindView(R.id.my_rides_recycler)
    RecyclerView mMyRidesRecycler;

    MyRidesAdapter mMyRidesAdapter;
    ArrayList<MyRideModel> mMyRidesModels;

    Connector mConnector;
    Connector mConnectorDeleteRequest;
    ProgressDialog mProgressDialog;

    int mPos;

    public MyRidesFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_rides, container, false);
        ButterKnife.bind(this,v);
        mMyRidesModels = new ArrayList<>();

        mConnector = new Connector(getContext(), new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)){
                    mMyRidesModels.addAll(Connector.getMyRequests(response,getContext()));
                    mMyRidesAdapter.notifyDataSetChanged();
                } else {
                    if (getActivity() != null)
                        Helper.showSnackBarMessage(getString(R.string.no_rides),(AppCompatActivity) getActivity());
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                if (getActivity() != null)
                    Helper.showSnackBarMessage(getString(R.string.error),(AppCompatActivity) getActivity());
            }
        });

        mConnectorDeleteRequest = new Connector(getContext(), new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)){
                    mMyRidesModels.remove(mPos);
                    mMyRidesAdapter.notifyDataSetChanged();
                } else {
                    if (getActivity() != null)
                        Helper.showSnackBarMessage(getString(R.string.error),(AppCompatActivity) getActivity());
                    mMyRidesAdapter.notifyDataSetChanged();
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                if (getActivity() != null)
                    Helper.showSnackBarMessage(getString(R.string.error),(AppCompatActivity) getActivity());
                mMyRidesAdapter.notifyDataSetChanged();
            }
        });


        mMyRidesAdapter = new MyRidesAdapter(mMyRidesModels,getContext(),position -> {
//            startActivity(new Intent(getContext(), ConfirmRideRequestActivity.class)
//                    .putExtra("type","show")
//                    .putExtra("from_id",mMyRidesModels.get(position).getFromId())
//                    .putExtra("user_id",mMyRidesModels.get(position).getUserId())
//                    .putExtra("request",mMyRidesModels.get(position))
//                    .putExtra("Status",mMyRidesModels.get(position).getStatus())
//                    .putExtra("RequestID",mMyRidesModels.get(position).getId()));
            startActivityForResult(new Intent(getActivity(), RideDetailsActivity.class)
            .putExtra("request_id",mMyRidesModels.get(position).getId())
            ,1);
        });
        mMyRidesRecycler.setHasFixedSize(true);
        mMyRidesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mMyRidesRecycler.setAdapter(mMyRidesAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                final int pos = (int) viewHolder.itemView.getTag();
                mPos = pos;
                mProgressDialog.show();
                mConnectorDeleteRequest.getRequest(TAG,"https://code-grow.com/waslabank/api/delete_request?user_id=" + Helper.getUserSharedPreferences(getContext()).getId() + "&id=" + mMyRidesModels.get(pos).getId());

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Paint p = new Paint();
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX / 4, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_swipe);
                        RectF icon_dest = new RectF((float) itemView.getRight() * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        p.setColor(Color.parseColor("#FFFFFF"));
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getLeft() + dX / 4, (float) itemView.getTop(), (float) itemView.getLeft(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_swipe);
                        RectF icon_dest = new RectF((float) itemView.getLeft() * width, (float) itemView.getTop() + width, (float) itemView.getLeft() + width, (float) itemView.getBottom() - width);
                        p.setColor(Color.parseColor("#FFFFFF"));
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(mMyRidesRecycler);

        mProgressDialog = Helper.showProgressDialog(getContext(),getString(R.string.loading),false);
        mConnector.getRequest(TAG,"https://code-grow.com/waslabank/api/get_requests?user_id=" + Helper.getUserSharedPreferences(getContext()).getId());

        return v;
    }

}
