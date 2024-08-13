package com.example.apiestoque.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
//@SequenceGenerator(name = "produto_id_seq", sequenceName = "produto_id_seq", allocationSize=1)
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id único do produto", example = "1234")
//    @GeneratedValue(generator="produto_id_seq", strategy=GenerationType.SEQUENCE)
    private Long id;
    @NotNull(message = "O nome não pode ser nulo!")
    @Size(min = 2, message = "O nome deve ter pelo menos dois caracteres!")
    @Schema(description = "Nome do produto", example = "Hamburguer de frango")
    private String  nome;
    @Schema(description = "Descrição detalhada do produto", example = "Hamburguer de frando congelado 500g")
    private String  descricao;
    @NotNull(message = "O preço não pode ser nulo!")
    @Min(value = 0, message = "O preço deve ser pelo menos 0!")
    @Schema(description = "Preço do produto", example = "1999.99")
    private double  preco;
    @NotNull(message = "A quantidade não pode ser nula!")
    @Min(value = 0, message = "Quantidade deve ser pelo menos 0!")
    @Column(name="quantidadeestoque")
    @Schema(description = "Quantidade disponível em estoque", example = "50")
    private int quantidadeEstoque;

    public Produto(){}

    public Produto(String nome, String descricao, double preco, Integer quantidadeEstoque){
        this.nome              = nome;
        this.descricao         = descricao;
        this.preco             = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", preco=" + preco +
                ", quantidadeEstoque=" + quantidadeEstoque +
                '}';
    }

}