package com.alexc.doxy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaymentGroupAdapter extends RecyclerView.Adapter<PaymentGroupAdapter.PaymentGroupViewHolder> {
    private List<PaymentGroup> paymentGroups;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private DatabaseHelper databaseHelper;

    public PaymentGroupAdapter(List<PaymentGroup> paymentGroups, Context context) {
        this.paymentGroups = paymentGroups;
        this.context = context;
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

        databaseHelper = new DatabaseHelper(context);

        // Aquí obtienes el recurso de imagen desde el DatabaseHelper
        int imageResource = databaseHelper.getPaymentGroupImageResource(paymentGroup.getId());

        // Decodificas el recurso de imagen en un Bitmap
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), imageResource);

        holder.imageView.setImageBitmap(imageBitmap);
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
        public ImageView imageView;

        public PaymentGroupViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.titleCardPaymentGroup);
            textViewDescription = itemView.findViewById(R.id.descriptionCardPaymentGroup);
            imageView = itemView.findViewById(R.id.iconCard);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PaymentGroup paymentGroup);
    }

    public void setOnItemClickListener(PaymentGroupAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}

