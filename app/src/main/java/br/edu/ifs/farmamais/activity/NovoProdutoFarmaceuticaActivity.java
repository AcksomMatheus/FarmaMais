package br.edu.ifs.farmamais.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import br.edu.ifs.farmamais.R;
import br.edu.ifs.farmamais.helper.ConfiguracaoFirebase;
import br.edu.ifs.farmamais.helper.UsuarioFirebase;
import br.edu.ifs.farmamais.model.Farmaceutica;
import br.edu.ifs.farmamais.model.Produto;

public class NovoProdutoFarmaceuticaActivity extends AppCompatActivity {


    private EditText editProdutoNome, editProdutoCategoria, editProdutoDescricao, editProdutoPreco;
    private String idUsuarioLogado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_farmaceutica);
        //Configurações iniciais
        inicializarComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurações Toolbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                    if (!precoProduto.isEmpty()) {
                        Produto produto = new Produto();
                        produto.setIdUsuario(idUsuarioLogado);
                        produto.setNome(nomeProduto);
                        produto.setCategoria(categoriaProduto);
                        produto.setDescricao(descricaoProduto);
                        produto.setPreco(Double.parseDouble(precoProduto));
                        produto.salvar();
                        finish();
                        exibitMensagem("Produto salvo com sucesso");

                    } else {
                        exibitMensagem("Digite um Preço para o Produto");
                    }

                } else {
                    exibitMensagem("Digite uma Categoria para o Produto");
                }
            } else {
                exibitMensagem("Digite uma Descrição para o Produto");
            }
        } else {
            exibitMensagem("Digite um Nome para o Produto");
        }
    }

    private void exibitMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes() {
        editProdutoCategoria = findViewById(R.id.editProdutoCategoria);
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoPreco = findViewById(R.id.editProdutoPreco);
    }
}
