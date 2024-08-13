package com.example.apiestoque.repository;

import com.example.apiestoque.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

//@Modifying indica que a consulta JPQL (Java Persistence Query Language) irá modificar o banco de dados. @Query especifica a consulta JPQL em si, que neste caso é uma instrução de exclusão que remove um objeto Produto com o ID especificado.
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    @Modifying
    @Query("DELETE FROM Produto e WHERE e.id = ?1")
    void deleteById(Long id);
    List<Produto> findByNomeLikeIgnoreCase(String nome);

    List<Produto> findByNomeLikeIgnoreCaseAndPrecoLessThan(String nome, double preco);
}
