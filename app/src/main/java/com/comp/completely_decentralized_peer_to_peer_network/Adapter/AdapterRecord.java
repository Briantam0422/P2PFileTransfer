package com.comp.completely_decentralized_peer_to_peer_network.Adapter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.comp.completely_decentralized_peer_to_peer_network.MainActivity;
import com.comp.completely_decentralized_peer_to_peer_network.Model.Record;
import com.comp.completely_decentralized_peer_to_peer_network.R;

import java.util.ArrayList;

public class AdapterRecord extends RecyclerView.Adapter<AdapterRecord.ViewHolder> {

    MainActivity activity;
    ArrayList<Record> arrayRecords;

    public AdapterRecord(MainActivity mainActivity, ArrayList<Record> arrRecords) {
        this.activity = mainActivity;
        this.arrayRecords = arrRecords;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //set the view layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_records, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = arrayRecords.get(position);

        //initial setup
        if (record.getRecordType().equals("initialRecord")){
            holder.imageRecordsMessageIcon.setImageResource(R.drawable.ic_cell_phone_1);
            holder.tvRecordsMessageTitle.setText("Your IP Address");
            holder.tvRecordsMessageIP.setText(record.getLocalHostIpAddress());
            holder.tvRecordsMessagePort.setText(String.valueOf(record.getLocalPortNumber()));
        }

        //Bootstrap request
        if (record.getRecordType().equals("BootstrapRequestSent")){
            holder.imageRecordsMessageIcon.setImageResource(R.drawable.ic_cell_phone_1);
            holder.tvRecordsMessageTitle.setText("Send request");
            holder.tvRecordsFileName.setText(record.getFilename());
            holder.tvRecordsFileName.setVisibility(View.VISIBLE);
            holder.tvRecordsMessageIP.setText(record.getServerIpAddress());
            holder.tvRecordsMessagePort.setText(String.valueOf(record.getServerIpPortNumber()));
        }

        //Bootstrap peer list downloaded
        if (record.getRecordType().equals("BootstrapPeerListDownloaded")){
            holder.imageRecordsMessageIcon.setImageResource(R.drawable.ic_cell_phone_1);
            holder.tvRecordsMessageTitle.setText("Downloaded file");
            holder.tvRecordsFileName.setText(record.getFilename());
            holder.tvRecordsFileName.setVisibility(View.VISIBLE);
            holder.tvRecordsMessageIP.setText(record.getServerIpAddress());
            holder.tvRecordsMessagePort.setText(String.valueOf(record.getServerIpPortNumber()));
        }

        //bootstrap response
        if (record.getRecordType().equals("BootstrapResponse")){
            holder.imageRecordsMessageIcon.setImageResource(R.drawable.ic_cell_phone_1);
            holder.tvRecordsMessageTitle.setText("File sent 100%");
            holder.tvRecordsFileName.setText(record.getFilename());
            holder.tvRecordsFileName.setVisibility(View.VISIBLE);
            holder.tvRecordsMessageIP.setText("");
            holder.tvRecordsMessagePort.setText("");
        }

        // pong response
        if (record.getRecordType().equals("PongResponse")){
            holder.imageRecordsMessageIcon.setImageResource(R.drawable.ic_cell_phone_1);
            holder.tvRecordsMessageTitle.setText("Found Peer contains file");
            holder.tvRecordsFileName.setText(record.getFilename());
            holder.tvRecordsFileName.setVisibility(View.VISIBLE);
            holder.tvRecordsMessageIP.setText(record.getServerIpAddress());
            holder.tvRecordsMessagePort.setText(String.valueOf(record.getServerIpPortNumber()));
        }

        // pong response
        if (record.getRecordType().equals("PongResponseFileNotFound")){
            holder.imageRecordsMessageIcon.setImageResource(R.drawable.ic_cell_phone_1);
            holder.tvRecordsMessageTitle.setText("File not found in the network");
            holder.tvRecordsFileName.setText(record.getFilename());
            holder.tvRecordsFileName.setVisibility(View.VISIBLE);
            holder.tvRecordsMessageIP.setText("");
            holder.tvRecordsMessagePort.setText("");
        }



    }

    @Override
    public int getItemCount() {
        return arrayRecords.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgRecordsIcon, imageRecordsMessageIcon;
        TextView tvRecordsFileName, tvRecordsMessageTitle, tvRecordsMessageIP, tvRecordsMessagePort;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //find Item ID
            imgRecordsIcon = itemView.findViewById(R.id.img_records_icon);
            imageRecordsMessageIcon = itemView.findViewById(R.id.img_records_message_icon);
            tvRecordsFileName = itemView.findViewById(R.id.tv_text_file);
            tvRecordsMessageTitle = itemView.findViewById(R.id.tv_records_title);
            tvRecordsMessageIP = itemView.findViewById(R.id.tv_ip);
            tvRecordsMessagePort = itemView.findViewById(R.id.tv_port);

        }
    }
}
