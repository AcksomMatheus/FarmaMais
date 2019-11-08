package br.edu.ifs.farmamais.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.edu.ifs.farmamais.R;
import br.edu.ifs.farmamais.model.Farmaceutica;

/**
 * Created by Acksom Matheus
 */

public class AdapterFarmaceutica extends RecyclerView.Adapter<AdapterFarmaceutica.MyViewHolder> {

    private List<Farmaceutica> farmaceuticas;

    public AdapterFarmaceutica(List<Farmaceutica> farmaceuticas) {
        this.farmaceuticas = farmaceuticas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_farmaceutica, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Farmaceutica farmaceutica = farmaceuticas.get(i);
        holder.nomeFarmaceutica.setText(farmaceutica.getNomeFarmaceutica());
        holder.nomeFarmacia.setText(farmaceutica.getNomeFarmacia() + " -");
        holder.horaEntrada.setText(farmaceutica.getHoraEntrada() + "");
        holder.horaSaida.setText("- " + farmaceutica.getHoraSaida());

        //Carregar imagem
        String urlImagem = farmaceutica.getUrlImagem();
        Picasso.get().load( urlImagem ).into( holder.imagemFarmaceutica );

    }

    @Override
    public int getItemCount() {
        return farmaceuticas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemFarmaceutica;
        TextView nomeFarmaceutica;
        TextView nomeFarmacia;
        TextView horaEntrada;
        TextView horaSaida;

        public MyViewHolder(View itemView) {
            super(itemView);

            nomeFarmaceutica = itemView.findViewById(R.id.textNomeFarmaceutica);
            nomeFarmacia = itemView.findViewById(R.id.textNomeFarmacia);
            horaEntrada = itemView.findViewById(R.id.textHoraEntrada);
            horaSaida = itemView.findViewById(R.id.textHoraSaida);
            imagemFarmaceutica = itemView.findViewById(R.id.imageFarmaceutica);
        }
    }
}
