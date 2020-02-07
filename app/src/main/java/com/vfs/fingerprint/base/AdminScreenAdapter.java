package com.vfs.fingerprint.base;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vfs.fingerprint.R;

import java.util.List;

public class AdminScreenAdapter extends RecyclerView.Adapter<AdminScreenAdapter.ViewHolder> {

    private static final String TAG = AdminScreenAdapter.class.getSimpleName();
    private final List<MrzDisplayModel> list;

    public AdminScreenAdapter(List<MrzDisplayModel> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_admin_screen, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "getItemCount: called ak check one ce 2:"+list.size());
        holder.txtViewFirstName.setText(list.get(position).getNAME());
        holder.txtViewSurName.setText(list.get(position).getSURNAME());

        holder.txtViewAppId.setText(list.get(position).getAPPLICATION_ID());
        holder.txtViewTxnId.setText(list.get(position).getTXN_ID());
        holder.txtViewStartDateTime.setText(list.get(position).getTXN_START_DATE_TIME());
        holder.txtViewEndDateTime.setText(list.get(position).getTXN_END_DATE_TIME());
        holder.txtViewGender.setText(list.get(position).getGENDER());
        holder.txtViewDateOfBirth.setText(list.get(position).getDATE_OF_BIRTTH());
        holder.txtViewDocumentType.setText(list.get(position).getDOCUMENT_TYPE());
        holder.txtViewDocumentNumber.setText(list.get(position).getDOCUMENT_NUMBER());
        holder.txtViewDateOfExpiry.setText(list.get(position).getDATE_OF_EXPIRY());
        holder.txtViewNationality.setText(list.get(position).getNATIONALITY());
        holder.txtViewSignatureFilePath.setText(list.get(position).getSIGNATURE_FILE_PATH());
        holder.txtViewNistFilePath.setText(list.get(position).getNIST_FILE_PATH());
        holder.txtViewDocumentFilePath.setText(list.get(position).getDOCUMENT_FILE_PATH());
        holder.txtViewBiometricStatus.setText(list.get(position).getBIOMETRIC_STATUS());
        holder.txtViewTransferStatus.setText(list.get(position).getTRANSFER_STATUS());
        holder.txtViewTransferDateTime.setText(list.get(position).getTRANSFER_DATE_TIME());
        holder.txtViewPersonalNumber.setText(list.get(position).getPERSONAL_NUMBER());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called ak check one ce 1:"+list.size());
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewFirstName, txtViewSurName,
                txtViewAppId, txtViewTxnId, txtViewStartDateTime, txtViewEndDateTime, txtViewGender,
                txtViewDateOfBirth, txtViewDocumentType, txtViewDocumentNumber, txtViewDateOfExpiry, txtViewNationality, txtViewSignatureFilePath, txtViewNistFilePath,
                txtViewDocumentFilePath, txtViewBiometricStatus, txtViewTransferStatus, txtViewTransferDateTime, txtViewPersonalNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtViewFirstName = itemView.findViewById(R.id.txtViewFirstName);
            txtViewSurName = itemView.findViewById(R.id.txtViewSurName);

            txtViewAppId = itemView.findViewById(R.id.txtViewAppId);
            txtViewTxnId = itemView.findViewById(R.id.txtViewTxnId);
            txtViewStartDateTime = itemView.findViewById(R.id.txtViewStartDateTime);
            txtViewEndDateTime = itemView.findViewById(R.id.txtViewEndDateTime);
            txtViewGender = itemView.findViewById(R.id.txtViewGender);
            txtViewDateOfBirth = itemView.findViewById(R.id.txtViewDateOfBirth);
            txtViewDocumentType = itemView.findViewById(R.id.txtViewDocumentType);
            txtViewDocumentNumber = itemView.findViewById(R.id.txtViewDocumentNumber);
            txtViewDateOfExpiry = itemView.findViewById(R.id.txtViewDateOfExpiry);
            txtViewNationality = itemView.findViewById(R.id.txtViewNationality);
            txtViewSignatureFilePath = itemView.findViewById(R.id.txtViewSignatureFilePath);
            txtViewNistFilePath = itemView.findViewById(R.id.txtViewNistFilePath);
            txtViewDocumentFilePath = itemView.findViewById(R.id.txtViewDocumentFilePath);
            txtViewBiometricStatus = itemView.findViewById(R.id.txtViewBiometricStatus);
            txtViewTransferStatus = itemView.findViewById(R.id.txtViewTransferStatus);
            txtViewTransferDateTime = itemView.findViewById(R.id.txtViewTransferDateTime);
            txtViewPersonalNumber = itemView.findViewById(R.id.txtViewPersonalNumber);
        }
    }
}
