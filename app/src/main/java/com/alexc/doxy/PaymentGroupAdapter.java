package com.alexc.doxy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaymentGroupAdapter extends RecyclerView.Adapter<PaymentGroupAdapter.PaymentGroupViewHolder> {
    private List<PaymentGroup> paymentGroups;
    private OnItemClickListener onItemClickListener;

    public PaymentGroupAdapter(List<PaymentGroup> paymentGroups) {
        this.paymentGroups = paymentGroups;
    }

    @NonNull
    @Override
    public PaymentGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_payment_group, parent, false);
        return new PaymentGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentGroupViewHolder holder, int position) {
        PaymentGroup paymentGroup = paymentGroups.get(position);

        // Mostrar los datos del grupo de pago en la tarjeta
        holder.textViewTitle.setText(paymentGroup.getTitle());
        holder.textViewDescription.setText(paymentGroup.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(paymentGroup);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentGroups.size();
    }

    public static class PaymentGroupViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewDescription;

        public PaymentGroupViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.titleCardPaymentGroup);
            textViewDescription = itemView.findViewById(R.id.descriptionCardPaymentGroup);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PaymentGroup paymentGroup);
    }

    public void setOnItemClickListener(PaymentGroupAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}

