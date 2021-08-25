package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Filme;

public class FilmeBuilder {

    private Filme filme;

    private FilmeBuilder(){

    }

    public static FilmeBuilder umFilme(){
        FilmeBuilder builder = new FilmeBuilder();
        builder.filme =  new Filme();
        builder.filme.setNome("De volta para o futuro");
        builder.filme.setEstoque(5);
        builder.filme.setPrecoLocacao(5.0);
        return  builder;

    }

    public FilmeBuilder semEstoque(){
        filme.setEstoque(0);
        return this;
    }

    public Filme agora(){
        return filme;
    }
}
