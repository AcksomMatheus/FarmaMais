package br.edu.ifs.farmamais.model;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;

import br.edu.ifs.farmamais.activity.ConfiguracoesFarmaceuticaActivity;
import br.edu.ifs.farmamais.helper.ConfiguracaoFirebase;

public class Farmaceutica {

    private String idUsuario;
    private String urlImagem;
    private String nomeFarmaceutica;
    private String nomeFarmacia;
    private String horaEntrada;
    private String horaSaida;

    public Farmaceutica() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        DatabaseReference farmaceuticaRef = firebaseRef.child("farmaceuticas").child(getIdUsuario());
        farmaceuticaRef.setValue(this);
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getNomeFarmaceutica() {
        return nomeFarmaceutica;
    }

    public void setNomeFarmaceutica(String nomeFarmaceutica) {
        this.nomeFarmaceutica = nomeFarmaceutica;
    }

    public String getNomeFarmacia() {
        return nomeFarmacia;
    }

    public void setNomeFarmacia(String nomeFarmacia) {
        this.nomeFarmacia = nomeFarmacia;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(String horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public String getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(String horaSaida) {
        this.horaSaida = horaSaida;
    }
}
