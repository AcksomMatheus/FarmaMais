package br.edu.ifs.farmamais.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.edu.ifs.farmamais.R;
import br.edu.ifs.farmamais.model.Produto;

/**
 * Created by Acksom Matheus
 */

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder>{

    private List<Produto> produtos;
    private Context context;

    public AdapterProduto(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Produto produto = produtos.get(i);
        holder.nome.setText(produto.getNome());
        holder.categoria.setText("Categoria: " + produto.getCategoria());
        holder.descricao.setText("Descrição: " + produto.getDescricao());
        holder.preco.setText("R$ " + produto.getPreco());
        holder.precoDesc.setText(" - R$ " + produto.getPrecoDesc());
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView descricao;
        TextView preco;
        TextView categoria;
        TextView precoDesc;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomeProduto);
            descricao = itemView.findViewById(R.id.textDescricaoProduto);
            categoria = itemView.findViewById(R.id.textCategoriaProduto);
            preco = itemView.findViewById(R.id.textPreco);
            precoDesc = itemView.findViewById(R.id.textPrecoDesc);
            preco.setPaintFlags(preco.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}
