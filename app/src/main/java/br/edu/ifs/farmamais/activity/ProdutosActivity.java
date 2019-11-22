package br.edu.ifs.farmamais.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifs.farmamais.R;
import br.edu.ifs.farmamais.adapter.AdapterProduto;
import br.edu.ifs.farmamais.helper.ConfiguracaoFirebase;
import br.edu.ifs.farmamais.helper.UsuarioFirebase;
import br.edu.ifs.farmamais.listener.RecyclerItemClickListener;
import br.edu.ifs.farmamais.model.Cliente;
import br.edu.ifs.farmamais.model.Farmaceutica;
import br.edu.ifs.farmamais.model.ItemPedido;
import br.edu.ifs.farmamais.model.Pedido;
import br.edu.ifs.farmamais.model.Produto;
import dmax.dialog.SpotsDialog;

public class ProdutosActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutos;
    private ImageView imageFarmaceuticaProdutos;
    private TextView textNomeFarmaceuticaProdutos;
    private Farmaceutica farmaceuticaSelecionada;
    private AlertDialog dialog;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idFarmaceutica;
    private String idUsuarioLogado;
    private Cliente usuario;
    private Pedido pedidoRecuperado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        //Inicializar Componentes
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Recuperar Empresa Selecionada
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            farmaceuticaSelecionada = (Farmaceutica) bundle.getSerializable("farmaceutica");

            textNomeFarmaceuticaProdutos.setText(farmaceuticaSelecionada.getNomeFarmaceutica());
            idFarmaceutica = farmaceuticaSelecionada.getIdUsuario();

            String url = farmaceuticaSelecionada.getUrlImagem();
            Picasso.get().load(url).into(imageFarmaceuticaProdutos);
        }

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("FarmaMais - Produtos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configurações RecyclerView
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutos.setAdapter(adapterProduto);

        //Configura evento de clique
        recyclerProdutos.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerProdutos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                confirmarQuantidade(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        //Recuperar Produtos
        recuperarProdutos();
        recuperarDadosUsuario();
    }

    private void confirmarQuantidade(final int posicao) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");

        final EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");

        builder.setView( editQuantidade );

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String quantidade = editQuantidade.getText().toString();

                Produto produtoSelecionado = produtos.get(posicao);
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
                itemPedido.setNomeProduto(produtoSelecionado.getNome());
                itemPedido.setPreco(produtoSelecionado.getPrecoDesc());
                itemPedido.setQuantidade( Integer.parseInt(quantidade) );
                //Fazer validação 537
                itensCarrinho.add( itemPedido );

                if( pedidoRecuperado == null ){
                    pedidoRecuperado = new Pedido(idUsuarioLogado, idFarmaceutica);
                }

                pedidoRecuperado.setNome(usuario.getNome());
                pedidoRecuperado.setEndereco(usuario.getRua());
                pedidoRecuperado.setNumeroCasa(usuario.getNumero());
                pedidoRecuperado.setTelefone(usuario.getTelefone());
                pedidoRecuperado.setItens( itensCarrinho );
                pedidoRecuperado.salvar();

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog =  builder.create();
        dialog.show();

    }

    private void recuperarDadosUsuario() {

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando dados").setCancelable(false).build();

        dialog.show();

        DatabaseReference usuariosRef = firebaseRef.child("clientes").child(idUsuarioLogado);

        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if( dataSnapshot.getValue() != null){
                    usuario = dataSnapshot.getValue(Cliente.class);
                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recuperarPedido() {

        dialog.dismiss();

    }

    private void recuperarProdutos() {

        DatabaseReference produtosRef = firebaseRef.child("produtos").child(idFarmaceutica);
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_produtos, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuPedido :
                break;

        }

        return super.onOptionsItemSelected(item);
    }
    private void inicializarComponentes() {
        recyclerProdutos = findViewById(R.id.recyclerProdutos);
        imageFarmaceuticaProdutos = findViewById(R.id.imageFarmaceuticaProdutos);
        textNomeFarmaceuticaProdutos = findViewById(R.id.textNomeFarmaceuticaProdutos);
    }
}
