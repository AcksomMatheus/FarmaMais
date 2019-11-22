package br.edu.ifs.farmamais.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import br.edu.ifs.farmamais.R;
import br.edu.ifs.farmamais.helper.ConfiguracaoFirebase;
import br.edu.ifs.farmamais.helper.UsuarioFirebase;
import br.edu.ifs.farmamais.model.Cliente;
import br.edu.ifs.farmamais.model.Farmaceutica;

public class ConfiguracoesClienteActivity extends AppCompatActivity {

    private EditText editClienteNome, editClienteRua, editClienteNumero, editClienteTelefone;
    private String idUsuario;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_cliente);

        //Configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        idUsuario = UsuarioFirebase.getIdUsuario();

        //Configurações Toolbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações Cliente");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recupera dados cliente
        recuperarDadosCliente();
    }

    private void recuperarDadosCliente(){
        DatabaseReference clienteRef = firebaseRef.child("clientes").child( idUsuario );
        clienteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if( dataSnapshot.getValue() != null){
                    Cliente cliente = dataSnapshot.getValue(Cliente.class);
                    editClienteNome.setText(cliente.getNome());
                    editClienteRua.setText(cliente.getRua());
                    editClienteNumero.setText((cliente.getNumero()));
                    editClienteTelefone.setText((cliente.getTelefone()));
                                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializarComponentes() {
        editClienteNome = findViewById(R.id.editNomeCliente);
        editClienteRua = findViewById(R.id.editRua);
        editClienteNumero = findViewById(R.id.editNumero);
        editClienteTelefone = findViewById(R.id.editTelefone);
    }

    public void validarDadosCliente(View view){
        //Valida se os campos foram preenchidos
        String nomeCliente = editClienteNome.getText().toString();
        String nomeRua = editClienteRua.getText().toString();
        String numeroCasa = editClienteNumero.getText().toString();
        String numeroTelefone = editClienteTelefone.getText().toString();


        if (!nomeCliente.isEmpty()) {
            if (!nomeRua.isEmpty()) {
                if (!numeroCasa.isEmpty()) {
                    if (!numeroTelefone.isEmpty()) {

                        Cliente cliente = new Cliente();
                        cliente.setIdUsuario( idUsuario);
                        cliente.setNome(nomeCliente);
                        cliente.setRua(nomeRua);
                        cliente.setNumero(numeroCasa);
                        cliente.setTelefone(numeroTelefone);
                        cliente.salvar();
                        exibitMensagem("Dados Atualizados com sucesso!");
                        finish();
                    }else{
                        exibitMensagem("Digite um numero Telefone");
                    }
                }else{
                    exibitMensagem("Digite um numero para casa");
                }
            }else{
                exibitMensagem("Digite uma Rua");
            }
        }else{
            exibitMensagem("Digite um Nome");
        }
    }
    private void exibitMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}
