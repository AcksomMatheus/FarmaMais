package br.edu.ifs.farmamais.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifs.farmamais.R;
import br.edu.ifs.farmamais.adapter.AdapterFarmaceutica;
import br.edu.ifs.farmamais.helper.ConfiguracaoFirebase;
import br.edu.ifs.farmamais.listener.RecyclerItemClickListener;
import br.edu.ifs.farmamais.model.Farmaceutica;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private RecyclerView recyclerFarmaceutica;
    private List<Farmaceutica> farmaceuticas = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterFarmaceutica adapterFarmaceutica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Configurações Toolbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("FarmaMais");
        setSupportActionBar(toolbar);

        //Configurações RecyclerView
        recyclerFarmaceutica.setLayoutManager(new LinearLayoutManager(this));
        recyclerFarmaceutica.setHasFixedSize(true);
        adapterFarmaceutica = new AdapterFarmaceutica(farmaceuticas);
        recyclerFarmaceutica.setAdapter(adapterFarmaceutica);

        //Recuperar Farmaceuticas
        recuperarFarmaceuticas();

        //Configurações SearchView
        searchView.setHint("Pesquisar Farmaceuticos");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarFarmaceuticas(newText);
                return true;
            }
        });

        //Evento de Clique
        recyclerFarmaceutica.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerFarmaceutica, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Farmaceutica farmaceuticaSelecionada = farmaceuticas.get((position));
                Intent i = new Intent(HomeActivity.this, ProdutosActivity.class);

                i.putExtra("farmaceutica", farmaceuticaSelecionada);

                startActivity(i);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }

    private void pesquisarFarmaceuticas(String pesquisa) {
        DatabaseReference farmaceuticasRef = firebaseRef.child("farmaceuticas");
        Query query = farmaceuticasRef.orderByChild("nomeFarmaceutica").startAt(pesquisa).endAt(pesquisa + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                farmaceuticas.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    farmaceuticas.add(ds.getValue(Farmaceutica.class));
                }
                adapterFarmaceutica.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recuperarFarmaceuticas() {
        DatabaseReference farmaceuticaRef = firebaseRef.child("farmaceuticas");
        farmaceuticaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                farmaceuticas.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    farmaceuticas.add(ds.getValue(Farmaceutica.class));
                }
                adapterFarmaceutica.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_balconista, menu);

        //Configurar botão de pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuSair:
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponentes() {
        searchView = findViewById(R.id.materialSearchView);
        recyclerFarmaceutica = findViewById(R.id.recyclerProdutos);
    }

    private void deslogarUsuario() {
        try {
            autenticacao.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), AutenticacaoActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirConfiguracoes() {
        startActivity(new Intent(HomeActivity.this, ConfiguracoesClienteActivity.class));
    }
}
