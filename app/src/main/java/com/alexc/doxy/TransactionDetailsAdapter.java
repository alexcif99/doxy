package com.alexc.doxy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionDetailsAdapter extends RecyclerView.Adapter<TransactionDetailsAdapter.ViewHolder>{

    private List<Transaction> relTransactionPList;
    private OnTransactionClickListener transactionClickListener;

    public TransactionDetailsAdapter(List<Transaction> relTransactionPList) {
        this.relTransactionPList = relTransactionPList;
    }

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.transactionClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameDebtor;
        private TextView usernameCreditor;
        private TextView amountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameDebtor = itemView.findViewById(R.id.usernameDebtor);
            usernameCreditor = itemView.findViewById(R.id.usernameCreditor);
            amountTextView = itemView.findViewById(R.id.amountTransactionDetails);
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
    public TransactionDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_transaction_details, parent, false);
        return new TransactionDetailsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionDetailsAdapter.ViewHolder holder, int position) {
        Transaction transactionDebtor = relTransactionPList.get(position);
        holder.usernameDebtor.setText(transactionDebtor.getUser().getUsername());
        holder.usernameCreditor.setText(transactionDebtor.getUser_to_pay().getUsername());
        holder.amountTextView.setText(Double.toString(transactionDebtor.getAmount()));
    }

    @Override
    public int getItemCount() {
        return relTransactionPList.size();
    }

}
