package com.alexc.doxy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>{
    private List<Payment> payments;
    private OnItemClickListener onItemClickListener;
    private Context context;
    private DatabaseHelper databaseHelper;

    public PaymentAdapter(List<Payment> payments, Context context) {
        this.payments = payments;
        this.context = context;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    // todo: al Balance posar totes les transaccions en un sol reciclerview pero amb import negatius o positius (o diferents colors) i ordenats pel que mes deus al que mes et deuen)

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        Payment payment = payments.get(position);

        databaseHelper = new DatabaseHelper(context);
        User user = databaseHelper.getUser(payment.getOwnerUserId());

        String amount = String.valueOf(payment.getAmount());
        // Mostrar los datos del grupo de pago en la tarjeta
        holder.textViewTitle.setText(payment.getTitle());
        holder.textViewOwner.setText("Pagador: " + user.getUsername());
        holder.textViewAmount.setText(amount + "€");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(payment);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public static class PaymentViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewOwner;
        public TextView textViewAmount;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.titleCardPayment);
            textViewOwner = itemView.findViewById(R.id.ownerCardPayment);
            textViewAmount = itemView.findViewById(R.id.amountCardPayment);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Payment payment);
    }

    public void setOnItemClickListener(PaymentAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
