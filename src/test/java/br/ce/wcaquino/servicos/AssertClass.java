package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Usuario;
import org.junit.Assert;
import org.junit.Test;

public class AssertClass {
    @Test
    public void test(){
        Assert.assertTrue(true);
        Assert.assertFalse(false);

        Assert.assertEquals("Erro de comparacao!",1,1);
        Assert.assertEquals(0.5123,0.512,0.001);
        Assert.assertEquals(Math.PI,3.14,0.01);

        int i = 2;
        Integer j = 2;
        Assert.assertEquals(Integer.valueOf(i), j);
        Assert.assertEquals(i,j.intValue());

        Assert.assertEquals("bola", "bola");
        Assert.assertNotEquals("bola", "casa");

        Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
        Assert.assertTrue("bola".startsWith("bo"));

        Usuario usuario1 = new Usuario("Usuario 1");
        Usuario usuario2 = new Usuario("Usuario 1");
        Usuario usuario3 = usuario2;
        Usuario usuario4 = null;

        Assert.assertEquals(usuario1,usuario2);

        //Verifica se sao a mesma instancia
        Assert.assertSame(usuario2,usuario3);

        Assert.assertNull(usuario4);


    }
}
