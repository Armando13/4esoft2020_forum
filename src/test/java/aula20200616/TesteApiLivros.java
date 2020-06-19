package aula20200616;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import org.aspectj.lang.annotation.Before;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import mySpringBootApp.livro.Livro;
import mySpringBootApp.livro.LivroRepository;


public class TesteApiLivros {
    
    //URL base para onde as requests serão feitas
    final String BASE_PATH = "http://localhost:8888/livro";
 
    //Injetamos o repositório para acesso ao Banco de dados
    @Autowired
    private LivroRepository repository;
     
    //Definimos o restTemplate
    private RestTemplate restTemplate;
     
    //Definimos o JacksonMapper responsável por converter
    //JSON para Objeto e vice versa
    private ObjectMapper MAPPER = new ObjectMapper();
     
    //Definimos o que será feito antes da execução do teste
    @Before(value = "")
    public void setUp() throws Exception {
 
        //Deletamos todos os registros do banco
        repository.deleteAll();
 
        //Inserimos alguma pessoas no banco
        repository.save(new Livro("1", "Harry Potter",900));
        repository.save(new Livro("2", "Alto da Compadecida",500));
               
        
        //Inicializamos o objeto restTemplate
        restTemplate = new RestTemplate();
    }
     
    @Test
    public void testCreateLivro() throws JsonProcessingException{
 
        //Criamos uma nova pessoa
        Livro livro = new Livro("3","Quincas Borba",445);
 
        //Fazemos um HTTP request do tipo POST passando a pessoa como parâmetro
        Livro response = restTemplate.postForObject(BASE_PATH, livro, Livro.class);
 
        //Verificamos se o resultado da requisição é igual ao esperado
        //Se sim significa que tudo correu bem
        Assert.assertEquals("Quincas Borba", response.getTitulo());
    }
     
    
    @Test
    public void testFindOne() throws IOException{
     
        //Fazemos uma requisição HTTP GET buscando por todas as pessoas
        String response = restTemplate
            .getForObject(BASE_PATH + "/findAll", String.class);
     
        //Convertemos a resposta JSON para Objeto usando op Jackson
        List<Livro> livros = MAPPER.readValue(response, 
            MAPPER.getTypeFactory().constructCollectionType(List.class, Livro.class));
        //Pegamos o ID da pessoa na posição 1 da lista e fazemos nova requisição
        //Recuperando as informações da mesma
        Livro livro = restTemplate.getForObject(BASE_PATH + "/" +
            livros.get(1).getId(), Livro.class);
         
        //Verificamos se o resultado da requisição é igual ao esperado
        //Se sim significa que tudo correu bem
        Assert.assertNotNull(livro);
        Assert.assertEquals("Bruce Lee", livro.getTitulo());
        
   
 
}
    
    
    @Test
    public void testUpdateLivro() throws IOException{
        String response = restTemplate.getForObject(BASE_PATH + "/findAll", String.class);
        List<Livro> livros = MAPPER.readValue(response,
            MAPPER.getTypeFactory().constructCollectionType(List.class, Livro.class));
         
        //Pegamos o ID da pessoa na posição 2 da lista e fazemos nova requisição
        //Recuperando as informações da mesma
        Livro livro = restTemplate.getForObject(BASE_PATH + "/" +
            livros.get(2).getId(), Livro.class);
     
        //Alteramos as informações da pessoa recuperada
        livro.setTitulo("Quincas");
        
     
        //Fazemos um HTTP request do tipo PUT passando a pessoa 
        //e suas novas informações como parâmetro
        restTemplate.put(BASE_PATH, livro);
         
        //como a operação PUT do RestTemplate é do tipo void
        //Precisamos fazer uma nova requisição para recuperar 
        //as informações da pessoa atualizada
        Livro livro2 = restTemplate.getForObject(BASE_PATH + "/" +
            livros.get(2).getId(), Livro.class);
        
        //Verificamos se o resultado da requisição é igual ao esperado
        //Se sim significa que tudo correu bem
        Assert.assertNotNull(livro2);
        Assert.assertEquals("Quincas", livro2.getTitulo());
        
         
    }
}