package com.alexc.doxy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>{

    private List<Transaction> relTransactionPList;
    private OnTransactionClickListener transactionClickListener;

    public TransactionAdapter(List<Transaction> relTransactionPList) {
        this.relTransactionPList = relTransactionPList;
    }

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.transactionClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameTextView;
        private TextView paymentGroupTextView;
        private TextView amountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTransaction);
            paymentGroupTextView = itemView.findViewById(R.id.paymentGroupTransaction);
            amountTextView = itemView.findViewById(R.id.amountTransaction);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && transactionClickListener != null) {
                        transactionClickListener.onTransactionClick(relTransactionPList.get(position));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_transaction, parent, false);
        return new TransactionAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        Transaction transactionDebtor = relTransactionPList.get(position);
        holder.usernameTextView.setText(transactionDebtor.getUser_to_pay().getUsername());
        holder.paymentGroupTextView.setText(transactionDebtor.getPayment_group().getTitle());
        holder.amountTextView.setText(Double.toString(transactionDebtor.getAmount()));
    }

    @Override
    public int getItemCount() {
        return relTransactionPList.size();
    }

}
