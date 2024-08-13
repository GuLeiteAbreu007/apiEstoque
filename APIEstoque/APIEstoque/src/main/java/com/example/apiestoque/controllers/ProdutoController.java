package com.example.apiestoque.controllers;

import com.example.apiestoque.models.Produto;
import com.example.apiestoque.repository.ProdutoRepository;
import com.example.apiestoque.services.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.*;
import jdk.jfr.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutoService produtoService;
    //dá até para colocar o autowired no atributo, mas aí fica ruim para acoplamento (passando para a classe a responsabilidade de criar o objeto tbm)
    @Autowired
    public ProdutoController(ProdutoService produtoService){
        this.produtoService = produtoService;
    }

    @GetMapping("/selecionar")
    @Operation(summary = "Lista todos os produtos",
    description = "Retorna uma lista de todos os produtos disponíveis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos retonada com sucesso",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    public List<Produto> listarProdutos(){
        return produtoService.buscarTodos();
    }

//    o request body vai transformar o json em objeto (requisição post vem em json) o corpo  da rquisição vai ser de produtp
    @PostMapping("/inserir")
    @Operation(summary = "Insere um novo produto", description = "Adiciona um novo produto ao estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto inserido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro na requisição", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    public ResponseEntity<?> inserirProdutos(@Valid @RequestBody Produto produto, BindingResult resultado){
        Map<String, String> erros = new HashMap<>();

        if (resultado.hasErrors()) {
            for (FieldError error : resultado.getFieldErrors()) {
                erros.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
        }

        try {
//            String retorno = verificaProduto("geral", produto);
//            if (retorno != null) return ResponseEntity.status(400).body(retorno);

            Produto produto1 = produtoService.salvarProduto(produto);

            return produto1.getId() != 0 ? ResponseEntity.ok("Produto inserido com sucesso") :
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro na requisição.");
        }catch(ClassCastException cce) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Os campos preco e quantidadeEstoque devem ser numéricos.");
        }
    }

    @DeleteMapping("excluir/{id}")
    @Operation(summary = "Exclui um produto ID", description = "Remove um produto por sistema pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto excluido com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
    })
    public ResponseEntity<String> excluirProdutos(@PathVariable Long id){
        try{
            if(verificaId(id)) ResponseEntity.status(400).body("ID não pode ser menor ou igual a zero.");


            Produto produtoExistente = produtoService.buscarProduto(id);
            produtoService.excluirProduto(id);
            return ResponseEntity.ok("Produto excluido com sucesso");
        }catch (RuntimeException re){
            return ResponseEntity.status(404).body("ID não encontrado.");
        }
    }

//    resquest body json em objeto/ optional pode ser nulo

    @PatchMapping("/atualizarParcial/{id}")
    @Operation(summary = "Atualiza parcialmente um produto pelo ID", description = "Atualiza um produto existente parcialmente pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro na requisição", content = @Content),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
    })
    public ResponseEntity<?> atualizarProdutoParcial(@Parameter(description = "ID do produto a ser atualizado") @PathVariable Long id, @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                                                                                                                                                                description = "Mapeamento de campos a serem atualizados com os novos valores",
                                                                                                                                                                                                content = @Content(mediaType = "application/json",
                                                                                                                                                                                                schema = @Schema(type = "object", example = "{ \"nome\": \"Novo nome\", "+"\"descricao\": \"Nova descricao\", \"preco\": 10.0, \"quantidadeEstoque\": 100 }"))
    )@RequestBody Map<String, Object> updates){
        try{
            // verificacao id
            if(verificaId(id)) ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID não pode ser zero ou negativo.");

            Produto produto = produtoService.buscarProduto(id);
            //String result = "";
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            List<String> campos = new ArrayList<>();

            if(updates.containsKey("nome")){
                produto.setNome((String)(updates.get("nome")));
                campos.add("nome");
                //String temp = verificaProduto("nome", produto);
                //result += temp != null ? temp : "";
            }
            if(updates.containsKey("descricao")){
                produto.setDescricao((String)(updates.get("descricao")));
                campos.add("descricao");
                //String temp = verificaProduto("descricao", produto);
                //result += temp != null ? temp : "";
            }
            if(updates.containsKey("preco")){
                try {
                    produto.setPreco((Double)(updates.get("preco")));
                    //String temp = verificaProduto("preco", produto);
                    //result += temp != null ? temp : "";
                }catch (ClassCastException cce){
                    produto.setPreco((Integer)(updates.get("preco")));
                }
                campos.add("preco");
            }
            if(updates.containsKey("quantidadeEstoque")){
                campos.add("quantidadeEstoque");
                produto.setQuantidadeEstoque((Integer)(updates.get("quantidadeEstoque")));
                //String temp = verificaProduto("quantidadeEstoque", produto);
                //result += temp != null ? temp : "";
            }

            //if(!result.equals("")){
            //    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            //}

            boolean hasError = false;
            Map<String, String> retorno = new HashMap<>();
            for(String campo : campos){
                Set<ConstraintViolation<Produto>> violations = validator.validateProperty(produto, campo);
                if (!violations.isEmpty()) {
                    retorno.put(campo, violations.iterator().next().getMessage());
                    hasError = true;
                }
            }
            if(hasError) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(retorno);
            produtoService.salvarProduto(produto);
            return ResponseEntity.ok("Produto atualizado com sucesso");
        }catch(ClassCastException cce){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Os campos preco e quantidadeEstoque devem ser numéricos.");
        }catch(RuntimeException re){
            re.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto com ID " + id + " não encontrado.");
        }
    }

    @PutMapping("atualizar/{id}")
    @Operation(summary = "Atualiza um produto pelo ID", description = "Atualiza um produto existente pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro na requisição", content = @Content),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
    })
    public ResponseEntity<?> atualizarProduto(@Parameter @PathVariable Long id, @Valid @RequestBody Produto produtoAtualizado, BindingResult resultado){
        Map<String, String> erros = new HashMap<>();

        if (resultado.hasErrors()) {
            for (FieldError error : resultado.getFieldErrors()) {
                erros.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
        }

        try{
            if(verificaId(id)) ResponseEntity.status(400).body("ID não pode ser menor ou igual a zero.");

//            String retorno = verificaProduto("geral",produtoAtualizado);
//            if(retorno != null) return ResponseEntity.status(400).body(retorno);

            Produto produto = produtoService.buscarProduto(id);
            produto.setNome(produtoAtualizado.getNome());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            produtoService.salvarProduto(produto);
            return ResponseEntity.ok("Produto atualizado com sucesso");
        } catch(ClassCastException cce) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Os campos preco e quantidadeEstoque devem ser numéricos.");
        }
        catch (RuntimeException re){
            return ResponseEntity.notFound().build();
        }
    }

    public static String verificaProduto(String nomeCampo, Produto produto){
        String retorno = "";

        if(nomeCampo.equalsIgnoreCase("nome")||nomeCampo.equalsIgnoreCase("geral")){
            if(produto.getNome() == null || produto.getNome().length() == 0) retorno += "nome não pode ser vazio;";
        }
        if(nomeCampo.equalsIgnoreCase("descricao")||nomeCampo.equalsIgnoreCase("geral")){
            if(produto.getDescricao() == null || produto.getDescricao().length() == 0) retorno += "descricao não pode ser vazio;";
        }
        if(nomeCampo.equalsIgnoreCase("preco")||nomeCampo.equalsIgnoreCase("geral")){
            if(produto.getPreco() <= 0) retorno += "preco não pode ser 0 ou menor que zero;";
        }
        if(nomeCampo.equalsIgnoreCase("quantidade")||nomeCampo.equalsIgnoreCase("geral")){
            if(produto.getQuantidadeEstoque() <= 0) retorno += "quantidadeestoque não pode ser 0 ou menor que zero;";
        }

        return retorno.isEmpty() ? null : retorno;
    }

    public static boolean verificaId(Long id){
        return id < 1;
    }

    @GetMapping("/buscarPorNome")
    @Operation(summary = "Busca produtos por nome", description = "Retorna uma lista de produtos que correspondem ao nome fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
    })
    public ResponseEntity<?> buscarPorNome(@RequestParam String nome){
        List<Produto> listaProdutos = produtoService.bucarPorNome(nome);
        if(!listaProdutos.isEmpty()){
            return ResponseEntity.ok(listaProdutos);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado!");
        }
    }

    //requestparam
    @GetMapping("/buscarPorNomeEPreco/{nome}/{preco}")
    @Operation(summary = "Busca produtos por nome e preço", description = "Retorna uma lista de produtos que correspondem ao nome e preço fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
    })
    public ResponseEntity<?> buscarPorNome(@PathVariable String nome, @PathVariable double preco){
        List<Produto> listaProdutos = produtoService.bucarPorNomeEPreco(nome, preco);
        if(!listaProdutos.isEmpty()){
            return ResponseEntity.ok(listaProdutos);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado!");
        }
    }


//    public static ResponseEntity<Map<String, String>> gerarResponseError(Produto produto, List<String> campos){
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        Validator validator = factory.getValidator();
//
//        boolean hasError = false;
//        Map<String, String> retorno = new HashMap<>();
//        for(String campo : campos){
//            Set<ConstraintViolation<Produto>> violations = validator.validateProperty(produto, campo);
//            if (!violations.isEmpty()) {
//                retorno.put(campo, violations.iterator().next().getMessage());
//                hasError = true;
//            }
//        }
//        return hasError ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(retorno) : null;
//    }
}
