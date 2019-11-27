package br.edu.ifs.farmamais.activity;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifs.farmamais.R;
import br.edu.ifs.farmamais.adapter.AdapterPedido;
import br.edu.ifs.farmamais.helper.ConfiguracaoFirebase;
import br.edu.ifs.farmamais.helper.UsuarioFirebase;
import br.edu.ifs.farmamais.listener.RecyclerItemClickListener;
import br.edu.ifs.farmamais.model.Pedido;
import dmax.dialog.SpotsDialog;

public class PedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresa;
    Pedido pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        //Configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        idEmpresa = UsuarioFirebase.getIdUsuario();

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configura recyclerview
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedidos.setAdapter( adapterPedido );
        swipe();

        recuperarPedidos();

        //Adiciona Evento de clique no recycleView
        recyclerPedidos.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerPedidos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

    }

    //Swipe
    public void swipe(){
        final ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags= ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
             entregarProduto(viewHolder);
            }
        };

        new ItemTouchHelper( itemTouch ).attachToRecyclerView( recyclerPedidos );
    }

    private void entregarProduto(final RecyclerView.ViewHolder viewHolder) {
        AlertDialog.Builder alerDialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        //Configura AlertDialog
        alerDialog.setTitle("Finalizar Pedido");
        alerDialog.setMessage("Você tem certeza que deseja finalizar esse produto?");
        alerDialog.setCancelable(false);
        alerDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                pedido = pedidos.get( position );
                pedido.setStatus("finalizado");
                pedido.atualizarStatus();
                Toast.makeText(PedidosActivity.this, "Produto Finalizado com sucesso!", Toast.LENGTH_SHORT).show();

            }
        });
        alerDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PedidosActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                adapterPedido.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alerDialog.create();
        alert.show();

    }

    private void recuperarPedidos() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable( false )
                .build();
        dialog.show();

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child(idEmpresa);

        Query pedidoPesquisa = pedidoRef.orderByChild("status")
                .equalTo("confirmado");

        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                pedidos.clear();
                if( dataSnapshot.getValue() != null ){
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterPedido.notifyDataSetChanged();
                    dialog.dismiss();
                }else{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 3000);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void inicializarComponentes(){
        recyclerPedidos = findViewById(R.id.recyclerPedidos);
    }

}
