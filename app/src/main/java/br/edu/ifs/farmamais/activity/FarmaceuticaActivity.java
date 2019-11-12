package br.edu.ifs.farmamais.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.security.Principal;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifs.farmamais.R;
import br.edu.ifs.farmamais.adapter.AdapterProduto;
import br.edu.ifs.farmamais.helper.ConfiguracaoFirebase;
import br.edu.ifs.farmamais.helper.UsuarioFirebase;
import br.edu.ifs.farmamais.listener.RecyclerItemClickListener;
import br.edu.ifs.farmamais.model.Farmaceutica;
import br.edu.ifs.farmamais.model.Produto;

public class FarmaceuticaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerProdutos;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private Produto produto;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmaceutica);

        //Configurações iniciais
        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurações Toolbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("FarmaMais - Farmaceutica");
        setSupportActionBar(toolbar);

        //Configurações RecyclerView
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutos.setAdapter(adapterProduto);
        swipe();

        //Recuperar Produtos
        recuperarProdutos();

        /*Adiciona evento de clique no recyclerView
        recyclerProdutos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, recyclerProdutos, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Produto produtoSelecionado = produtos.get(position);
                        produtoSelecionado.remover();
                        Toast.makeText(FarmaceuticaActivity.this, "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
                )
        );

         */
    }

    private void recuperarProdutos() {

        DatabaseReference produtosRef = firebaseRef.child("produtos").child(idUsuarioLogado);
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                   /* produto.setChave(ds.getKey());

                    */
                    produtos.add(ds.getValue(Produto.class));
                }
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializarComponentes() {
        recyclerProdutos = findViewById(R.id.RecyclerProdutos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_farmaceutica, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair :
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes :
                abrirConfiguracoes();
                break;
            case R.id.menuNovoProduto :
                abrirNovoProduto();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Excluindo atráves do swipe
    public void swipe() {

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirProduto(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerProdutos);
    }

    public void excluirProduto (final RecyclerView.ViewHolder viewHolder) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        //Configura AlertDialog

        alertDialog.setTitle("Exclusão de Produto");
        alertDialog.setMessage("Tem certeza que deseja excluir esse produto?");
        alertDialog.setCancelable(false);

        //Confirmação de exclusão de produto
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                produto = produtos.get(position);
                Produto produtoSelecionado = produtos.get(position);
                produtoSelecionado.remover();

                Toast.makeText(FarmaceuticaActivity.this, "Produto Excluído com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });

        //Cancela a exclusão do produto
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                produto = produtos.get(position);

                Toast.makeText(FarmaceuticaActivity.this, "Exclusão de produto cancelada!", Toast.LENGTH_SHORT).show();
                adapterProduto.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), AutenticacaoActivity.class));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(FarmaceuticaActivity.this, ConfiguracoesFarmaceuticaActivity.class));
    }

    private void abrirNovoProduto(){
        startActivity(new Intent(FarmaceuticaActivity.this, NovoProdutoFarmaceuticaActivity.class));
    }
}
