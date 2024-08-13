package com.example.apiestoque.services;

import com.example.apiestoque.models.Produto;
import com.example.apiestoque.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository){
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> buscarTodos(){
        return produtoRepository.findAll();
    }

    public Produto buscarProduto(Long id){
        return produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public Produto excluirProduto(Long id){
        Optional<Produto> prod = produtoRepository.findById(id);
        if(prod.isPresent()){
            produtoRepository.deleteById(id);
            return prod.get();
        }
        return null;
    }

    public Produto salvarProduto(Produto produto){
        return produtoRepository.save(produto);
    }

    public List<Produto> bucarPorNome(String nome){
        return produtoRepository.findByNomeLikeIgnoreCase(nome);
    }
    public List<Produto> bucarPorNomeEPreco(String nome, double preco){
        return produtoRepository.findByNomeLikeIgnoreCaseAndPrecoLessThan(nome, preco);
    }
}