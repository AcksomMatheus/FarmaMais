package br.edu.ifs.farmamais.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import br.edu.ifs.farmamais.R;
import br.edu.ifs.farmamais.helper.ConfiguracaoFirebase;
import br.edu.ifs.farmamais.helper.UsuarioFirebase;
import br.edu.ifs.farmamais.model.Produto;

public class EditProdutoFarmaceuticaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoCategoria, editProdutoDescricao, editProdutoPreco;
    private String idUsuarioLogado;
    private String idProduto;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_produto_farmaceutica);

        //Configurações Toolbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editando Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent it = getIntent();


        //Configurações iniciais
        inicializarComponentes();
        storageReference = ConfiguracaoFirebase.getReferenciaStorage();
        firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        idProduto = it.getStringExtra("Posicao");
        recuperarProdutos();
    }

    private void inicializarComponentes() {
        editProdutoCategoria = findViewById(R.id.editProdutoCategoria);
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoPreco = findViewById(R.id.editProdutoPreco);
    }

    //Recuperar Produtos
    private void recuperarProdutos() {
        DatabaseReference produtoRef = firebaseRef.child("produtos").child(idUsuarioLogado).child(idProduto);
        produtoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Produto produto = dataSnapshot.getValue(Produto.class);
                    editProdutoNome.setText(produto.getNome());
                    editProdutoCategoria.setText(produto.getCategoria());
                    editProdutoDescricao.setText(produto.getDescricao());
                    editProdutoPreco.setText(String.valueOf(produto.getPreco()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void validarDadosProduto(View view) {
        //Valida se os campos foram preenchidos
        String nomeProduto = editProdutoNome.getText().toString();
        String descricaoProduto = editProdutoDescricao.getText().toString();
        String categoriaProduto = editProdutoCategoria.getText().toString();
        String precoProduto = editProdutoPreco.getText().toString();

        if (!nomeProduto.isEmpty()) {
            if (!descricaoProduto.isEmpty()) {
                if (!categoriaProduto.isEmpty()) {
                    if(!precoProduto.isEmpty()){
                        Produto produto = new Produto();
                        produto.setIdUsuario(idUsuarioLogado);
                        produto.setIdProduto(idProduto);
                        produto.setNome(nomeProduto);
                        produto.setCategoria(categoriaProduto);
                        produto.setDescricao(descricaoProduto);
                        produto.setPreco(Double.parseDouble(precoProduto));
                        produto.salvar();
                        finish();
                        exibitMensagem("Produto salvo com sucesso");

                    }else{
                        exibitMensagem("Digite um Preço para o Produto");
                    }

                }else{
                    exibitMensagem("Digite uma Categoria para o Produto");
                }
            } else {
                exibitMensagem("Digite uma Descrição para o Produto");
            }
        } else {
            exibitMensagem("Digite um Nome para o Produto");
        }
    }
    private void exibitMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }



}
