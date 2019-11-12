package br.edu.ifs.farmamais.model;

import com.google.firebase.database.DatabaseReference;

import br.edu.ifs.farmamais.helper.ConfiguracaoFirebase;

public class Produto {

    private String idUsuario;
    private String idProduto;

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    private String nome;
    private String descricao;
    private String categoria;
    private Double preco;

    public Produto() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        DatabaseReference produtoRef = firebaseRef.child("produtos");
        setIdProduto(produtoRef.push().getKey());
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public void salvar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        DatabaseReference produtoRef = firebaseRef.child("produtos").child(getIdUsuario()).child(getIdProduto());
        produtoRef.setValue(this);
    }

    public void remover() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        DatabaseReference produtoRef = firebaseRef.child("produtos").child(getIdUsuario()).child(getIdProduto());
        produtoRef.removeValue();

    }

    public String recuperarDados(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        DatabaseReference produtoRef = firebaseRef.child("produtos").child(getIdUsuario()).child(getIdProduto());
        return getIdProduto();
    }
}
