package br.ce.wcaquino.servicos;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

//@RunWith(ParallelRunner.class)
public class CalculadoraTeste {

    private Calculadora calculadora;

    @Before
    public void  setup(){
        calculadora = new Calculadora();
    }

    @Test
    public void deveSomarDoisValores(){
        //senario
        int a = 5;
        int b = 3;


        //ação
        int resultado = calculadora.soma(a,b);

        //verificação
        assertThat(resultado, is(equalTo(8)));
    }

    @Test
    public void deveSubtrairDoisValores(){
        //senario
        int a = 8;
        int b = 5;

        //ação
        int resultado = calculadora.subtracao(a,b);

        //verificação
        assertThat(resultado,is(equalTo(3)));
    }

    @Test
    public  void deveDividirDoisValores() throws Exception {
        //senario
        int a = 6;
        int b = 3;

        //ação
        int resultado = calculadora.dividir(a,b);

        //verificação
        assertThat(resultado,is(equalTo(2)));
    }


    @Test
    public void deveLancarUmaExcecaoDividindoPorZero(){
        //senario
        int a = 6;
        int b = 0;

        //ação
        try{
            calculadora.dividir(a,b);
        }catch (Exception e){
            Assert.assertEquals("Nao e possivel dividir por zero!",e.getMessage());
        }
    }
}
