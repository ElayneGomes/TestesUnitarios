import br.ce.wcaquino.entidades.Usuario;
import org.junit.Assert;
import org.junit.Test;

public class AssertTest {

    @Test
    public void test(){
        Assert.assertTrue(true);
        Assert.assertFalse(false);

        Assert.assertEquals("Erro de Comparação", 1, 1);
        Assert.assertEquals(0.51, 0.51, 0.001);
        Assert.assertEquals(Math.PI, 3.14, 0.01);

        int i = 5;
        Integer i2 = 5;
        Assert.assertEquals(Integer.valueOf(i), i2);
        Assert.assertEquals(i, i2.intValue());

        Assert.assertEquals("bola", "bola");
        Assert.assertNotEquals("bola", "casa");
        Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
        Assert.assertTrue("bola".startsWith("bo"));

        Usuario usuario = new Usuario("Usuario1");
        Usuario usuario2 = new Usuario("Usuario1");
        Usuario usuario3 = usuario2;
        Usuario usuario4 = null;

        Assert.assertEquals(usuario, usuario2);
        Assert.assertSame(usuario2, usuario2);
        Assert.assertSame(usuario2, usuario3);
        Assert.assertNotSame(usuario, usuario2);

        Assert.assertNull(usuario4);
        Assert.assertNotNull(usuario2);

    }
}
