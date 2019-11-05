package br.edu.ifs.farmamais.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import br.edu.ifs.farmamais.R;
import br.edu.ifs.farmamais.helper.ConfiguracaoFirebase;
import br.edu.ifs.farmamais.helper.UsuarioFirebase;

public class AutenticacaoActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso, tipoUsuario;
    private LinearLayout linearTipoUsuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);


        inicializaComponentes();

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.signOut();
        //Verifica usuario logado
        verificaUsuarioLogado();

        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//farmaceutica
                    linearTipoUsuario.setVisibility(View.VISIBLE);
                } else {//balconista
                    linearTipoUsuario.setVisibility(View.GONE);
                }
            }
        });

    botaoAcessar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = campoEmail.getText().toString();
            String senha = campoSenha.getText().toString();

            if(!email.isEmpty()){
                if(!senha.isEmpty()){

                    //Verifica estado do switch
                    if(tipoAcesso.isChecked()){//Cadastro

                       autenticacao.createUserWithEmailAndPassword(
                               email, senha
                       ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {

                               if(task.isSuccessful()){
                                   Toast.makeText(AutenticacaoActivity.this,
                                           "Cadastro Realizado com Sucesso!" ,
                                           Toast.LENGTH_SHORT).show();
                                   String tipoUsuario = getTipoUsuario();
                                   UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);
                                  abrirTelaPrincipal(tipoUsuario);
                               }else{
                                   String erroExcecao = "";

                                   try {
                                       throw task.getException();
                                   }catch (FirebaseAuthWeakPasswordException e){
                                       erroExcecao = "Digite uma senha mais forte!";
                                   } catch (FirebaseAuthInvalidCredentialsException e) {
                                       erroExcecao = "Por favor, digite um e-mail válido";
                                   } catch (FirebaseAuthUserCollisionException e) {
                                       erroExcecao = "Esta conta já foi cadastrada";
                                   } catch (Exception e) {
                                       erroExcecao = "ao cadastrar usuário: "+ e.getMessage();
                                       e.printStackTrace();
                                   }

                                   Toast.makeText(AutenticacaoActivity.this,
                                           "Atenção: " + erroExcecao ,
                                           Toast.LENGTH_SHORT).show();

                               }
                           }
                       });

                    }else{//Login
                        autenticacao.signInWithEmailAndPassword(
                                email, senha
                        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                             if(task.isSuccessful()){
                                 Toast.makeText(AutenticacaoActivity.this, "Logado com sucesso", Toast.LENGTH_SHORT).show();
                                    String tipoUsuario = task.getResult().getUser().getDisplayName();
                                 abrirTelaPrincipal(tipoUsuario);
                             }else{
                                 Toast.makeText(AutenticacaoActivity.this, "Erro ao fazer login", Toast.LENGTH_SHORT).show();
                             }
                            }
                        });
                    }
                }else{
                    Toast.makeText(AutenticacaoActivity.this,
                            "Preencha a Senha",
                            Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(AutenticacaoActivity.this,
                        "Preencha o E-mail!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    });
    }

    private void verificaUsuarioLogado(){
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if(usuarioAtual != null){
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
    }

    private String getTipoUsuario(){
        return tipoUsuario.isChecked() ? "F" : "B";
    }

    private void abrirTelaPrincipal(String tipoUsuario){
        if(tipoUsuario.equals("F")) {//Farmaceutica
            startActivity(new Intent(getApplicationContext(), FarmaceuticaActivity.class));
        }else{//Balconista
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

    private void inicializaComponentes(){
        campoEmail = findViewById(R.id.editFarmaceuticaNome);
        campoSenha = findViewById(R.id.editFarmaciaNome);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoAcesso = findViewById(R.id.switchAcesso);
        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);

    }
}
