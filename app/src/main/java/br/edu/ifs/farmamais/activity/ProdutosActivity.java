package br.edu.ifs.farmamais.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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
import br.edu.ifs.farmamais.model.Farmaceutica;
import br.edu.ifs.farmamais.model.Produto;

public class ProdutosActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutos;
    private ImageView imageFarmaceuticaProdutos;
    private TextView textNomeFarmaceuticaProdutos;
    private Farmaceutica farmaceuticaSelecionada;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idFarmaceutica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        //Inicializar Componentes
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();

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

        //Recuperar Produtos
        recuperarProdutos();
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

    private void inicializarComponentes() {
        recyclerProdutos = findViewById(R.id.recyclerProdutos);
        imageFarmaceuticaProdutos = findViewById(R.id.imageFarmaceuticaProdutos);
        textNomeFarmaceuticaProdutos = findViewById(R.id.textNomeFarmaceuticaProdutos);
    }
}
