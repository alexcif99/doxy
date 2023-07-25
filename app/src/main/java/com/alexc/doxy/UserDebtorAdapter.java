package com.alexc.doxy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class UserDebtorAdapter extends RecyclerView.Adapter<UserDebtorAdapter.ViewHolder>{

    private List<UserDebtor> relUserPList;

    public UserDebtorAdapter(List<UserDebtor> relUserPList) {
        this.relUserPList = relUserPList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameTextView;
        private TextView amountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameCardDebtor);
            amountTextView = itemView.findViewById(R.id.textViewAmountDebtFromPayment);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_debtor, parent, false);
        return new UserDebtorAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDebtorAdapter.ViewHolder holder, int position) {
        UserDebtor userDebtor = relUserPList.get(position);
        holder.usernameTextView.setText(userDebtor.getUsername());
        holder.amountTextView.setText(Double.toString(userDebtor.getAmount()));
    }

    @Override
    public int getItemCount() {
        return relUserPList.size();
    }

}
