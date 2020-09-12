package com.waslabank.wasslabank.ui.ride_details;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import com.squareup.picasso.Picasso;
import com.waslabank.wasslabank.R;
import com.waslabank.wasslabank.databinding.ListOfferItemBinding;
import com.waslabank.wasslabank.models.Offer;
import com.waslabank.wasslabank.models.OfferModel;
import com.waslabank.wasslabank.models.SingleRequestModel.User;
import com.waslabank.wasslabank.networkUtils.Connector;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class OffersListAdapter extends RecyclerView.Adapter<OffersListAdapter.OfferViewHolder> {


    List<Offer> offers;
    String addressFrom, addressTo;
    RequestListner requestListner;

    public OffersListAdapter(List<Offer> offers, String addressFrom, String addressTo, RequestListner requestListner) {
        this.offers = offers;
        this.addressFrom = addressFrom;
        this.addressTo = addressTo;
        this.requestListner = requestListner;
    }


    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new OfferViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.list_offer_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder offerViewHolder, int i) {
        OfferModel offer = offers.get(i).getOffer();
        User from = offers.get(i).getClient();
        offerViewHolder.binding.name.setText(from.getName());
        offerViewHolder.binding.car.setText(from.getCarName());
        offerViewHolder.binding.fromPlace.setText(offer.getAddress());
        offerViewHolder.binding.toPlace.setText(addressTo);
        offerViewHolder.binding.seatsNumber.setText(offer.getSeats());
        if (URLUtil.isValidUrl(from.getImage())) {
            Picasso.get().load(from.getImage()).into(offerViewHolder.binding.profileImage);
        } else {
            Picasso.get().load(Connector.ConnectionServices.BaseURL + "waslabank/prod_img/" + from.getImage()).into(offerViewHolder.binding.profileImage);
        }
        offerViewHolder.binding.accept.setOnClickListener(v -> {
            requestListner.onAccept(from,offer,i);
        });
        offerViewHolder.binding.reject.setOnClickListener(v -> requestListner.onReject(from,offer,i));
    }


    class OfferViewHolder extends RecyclerView.ViewHolder {

        ListOfferItemBinding binding;

        OfferViewHolder(ListOfferItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    public interface RequestListner {
        void onAccept(User from, OfferModel offerModel, int position);

        void onReject(User from, OfferModel offerModel, int position);
    }


    @Override
    public int getItemCount() {
        return offers.size();
    }


}
