package br.edu.ifs.farmamais.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import br.edu.ifs.farmamais.R;
import br.edu.ifs.farmamais.helper.ConfiguracaoFirebase;
import br.edu.ifs.farmamais.helper.UsuarioFirebase;
import br.edu.ifs.farmamais.model.Farmaceutica;

public class ConfiguracoesFarmaceuticaActivity extends AppCompatActivity {

    private EditText editNomeFarmaceutica, editNomeCRM, editHoraEntrada, editHoraSaida;
    private ImageView imagePerfilFarmaceutica;

    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_farmaceutica);

        //Configurações iniciais
        inicializarComponentes();
        storageReference = ConfiguracaoFirebase.getReferenciaStorage();
        firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurações Toolbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configurando Imagem Perfil
        imagePerfilFarmaceutica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        /*Recuperar dados da farmaceutica*/
        recuperarDadosFarmaceutica();

    }

    private void recuperarDadosFarmaceutica(){
        DatabaseReference farmaceuticaRef = firebaseRef.child("farmaceuticas").child( idUsuarioLogado );
        farmaceuticaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if( dataSnapshot.getValue() != null){
                    Farmaceutica farmaceutica = dataSnapshot.getValue(Farmaceutica.class);
                    editNomeFarmaceutica.setText(farmaceutica.getNomeFarmaceutica());
                    editNomeCRM.setText(farmaceutica.getNomeCRM());
                    editHoraEntrada.setText((farmaceutica.getHoraEntrada()));
                    editHoraSaida.setText((farmaceutica.getHoraSaida()));

                    urlImagemSelecionada = farmaceutica.getUrlImagem();
                    if(urlImagemSelecionada != ""){
                        Picasso.get().load(urlImagemSelecionada).into(imagePerfilFarmaceutica);
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void validarDadosFarmaceutica(View view){
        //Valida se os campos foram preenchidos
        String nomeFarmaceutica = editNomeFarmaceutica.getText().toString();
        String nomeFarmacia = editNomeCRM.getText().toString();
        String horaEntrada = editHoraEntrada.getText().toString();
        String horaSaida = editHoraSaida.getText().toString();

        if (!nomeFarmaceutica.isEmpty()) {
            if (!nomeFarmacia.isEmpty()) {
                if (!horaEntrada.isEmpty()) {
                    if (!horaSaida.isEmpty()) {
                        Farmaceutica farmaceutica = new Farmaceutica();
                        farmaceutica.setIdUsuario(idUsuarioLogado);
                        farmaceutica.setNomeFarmaceutica(nomeFarmaceutica);
                        farmaceutica.setNomeCRM(nomeFarmacia);
                        farmaceutica.setHoraEntrada(horaEntrada);
                        farmaceutica.setHoraSaida(horaSaida);
                        farmaceutica.setUrlImagem(urlImagemSelecionada);
                        farmaceutica.salvar();
                        finish();

                    }else{
                        exibitMensagem("Digite uma hora de saida");
                    }
                }else{
                    exibitMensagem("Digite uma hora de entrada");
                }
            }else{
                exibitMensagem("Digite um nome para a Farmacia");
            }
        }else{
            exibitMensagem("Digite um nome para Farmaceutica");
        }
    }
    private void exibitMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;
            try {
                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(), localImagem
                                );
                        break;
                }
                if(imagem != null){
                    imagePerfilFarmaceutica.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();
                    StorageReference imagemRef = storageReference.child("imagens").child("farmaceuticas").child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesFarmaceuticaActivity.this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            urlImagemSelecionada = taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(ConfiguracoesFarmaceuticaActivity.this, "Sucesso ao carregar imagem", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }catch(Exception e){
                    e.printStackTrace();
                }
        }
    }

    private void inicializarComponentes() {
        editHoraEntrada = findViewById(R.id.editHoraEntrada);
        editHoraSaida = findViewById(R.id.editHoraSaida);
        editNomeFarmaceutica = findViewById(R.id.editProdutoNome);
        editNomeCRM = findViewById(R.id.editProdutoCategoria);
        imagePerfilFarmaceutica = findViewById(R.id.imagePerfilFarmaceutica);
    }
}
