package br.edu.ifs.farmamais.model;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;

import br.edu.ifs.farmamais.activity.ConfiguracoesFarmaceuticaActivity;
import br.edu.ifs.farmamais.helper.ConfiguracaoFirebase;

public class Pedido {

    private String idUsuario;
    private String idFarmaceutica;
    private String idPedido;
    private String nome;
    private String endereco;
    private String numeroCasa;
    private String telefone;
    private List<ItemPedido> itens;
    private Double total;
    private String status = "pendente";
    private int metodoPagamento;
    private String observacao;

    public Pedido() {
    }

    public Pedido(String idUsuario, String idFarmaceutica) {
        this.idUsuario = idUsuario;
        this.idFarmaceutica = idFarmaceutica;

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_cliente").child(idFarmaceutica).child(idUsuario);
        setIdPedido(pedidoRef.push().getKey());
    }
    public void salvar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_cliente").child(getIdFarmaceutica()).child(getIdUsuario());
        pedidoRef.setValue(this);
    }
    public void confirmar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos").child(getIdFarmaceutica()).child(getIdPedido());
        pedidoRef.setValue(this);
    }

    public void remover() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos_cliente").child(getIdFarmaceutica()).child(getIdUsuario());
        pedidoRef.removeValue();

    }
    public void atualizarStatus() {

        HashMap<String, Object> status = new HashMap<>();
        status.put("status", getStatus());

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidos").child(getIdFarmaceutica()).child(getIdPedido());
        pedidoRef.updateChildren( status );

    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdFarmaceutica() {
        return idFarmaceutica;
    }

    public void setIdFarmaceutica(String idFarmaceutica) {
        this.idFarmaceutica = idFarmaceutica;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumeroCasa() {
        return numeroCasa;
    }

    public void setNumeroCasa(String numeroCasa) {
        this.numeroCasa = numeroCasa;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }



}