package com.example.nobetcieczane;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.nobetcieczane.modals.EczaneDetay;

import org.w3c.dom.Text;

import java.util.List;

public class EczaneAdapter extends BaseAdapter {
    List<EczaneDetay> list;
    Context context;
    Activity activity;

    public EczaneAdapter(List<EczaneDetay> list, Context context,Activity activity) {
        this.list = list;
        this.context = context;
        this.activity=activity;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(R.layout.layout,parent,false);
        TextView eczaneIsim, eczaneAdres, eczaneFax, eczaneTel, eczaneAdresTarfi;
        Button haritadaGoster, aramaYap;
        eczaneIsim = (TextView) convertView.findViewById(R.id.eczaneIsmı);
        eczaneAdres = (TextView) convertView.findViewById(R.id.eczaneAdres);
        eczaneTel = (TextView) convertView.findViewById(R.id.eczaneTelefon);
        eczaneFax = (TextView) convertView.findViewById(R.id.eczaneFaks);
        eczaneAdresTarfi = (TextView) convertView.findViewById(R.id.eczaneAdresTarif);

        haritadaGoster = (Button) convertView.findViewById(R.id.eczaneHaritadaGoster);
        aramaYap = (Button) convertView.findViewById(R.id.aramaYap);

        eczaneIsim.setText(list.get(position).getEczaneIsmi());
        eczaneAdres.setText(list.get(position).getAdres());
        eczaneTel.setText(list.get(position).getTelefon());
        eczaneFax.setText(list.get(position).getFax());
        eczaneAdresTarfi.setText(list.get(position).getTarif());

        aramaYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.parse("tel"+list.get(position).getTelefon()));
                activity.startActivity(intent);

            }
        });

        return convertView;
    }
}
