package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderTeste {
    private static int contador = 0;

    @Test
    public void t1Inicio(){
        contador = 1;
    }

    @Test
    public void t2Verifica(){
        Assert.assertEquals(1,contador);
    }
}
