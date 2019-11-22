package br.edu.ifs.farmamais.model;

import com.google.firebase.database.DatabaseReference;

import br.edu.ifs.farmamais.helper.ConfiguracaoFirebase;

public class Cliente {

    private String nome;
    private String idUsuario;
    private String rua;
    private String numero;
    private String telefone;

    public Cliente() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void salvar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        DatabaseReference clienteRef = firebaseRef.child("clientes").child(getIdUsuario());
        clienteRef.setValue(this);
    }
}
