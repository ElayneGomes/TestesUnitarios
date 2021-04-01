package servicos;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;
import br.ce.wcaquino.servicos.Calculadora;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalculadoraTest {

    private Calculadora calc;

    @Before
    public void setup() {
        calc = new Calculadora();
        System.out.println("Iniciando...");
    }

    @After
    public void tearDown() {
        System.out.println("Finalizando...");
    }

    @Test
    public void deveSomarDoisValores() {

        //cenario
        int a = 5;
        int b = 3;

        //acao
        int resultado = calc.somar(a, b);

        //verificacao
        Assert.assertEquals(8, resultado);

    }

    @Test
    public void deveSubtrairDoisValores() {
        //cenario
        int a = 5;
        int b = 3;

        //acao
        int resultado = calc.subtracao(a, b);

        //verificacao
        Assert.assertEquals(2, resultado);
    }

    @Test
    public void deveMultiplicarDoisValores() {
        //cenario
        int a = 3;
        int b = 5;

        //acao
        int resultado = calc.multiplicar(a, b);

        //verificacao
        Assert.assertEquals(15, resultado);
    }

    @Test
    public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
        //cenario
        int a = 10;
        int b = 2;

        //acao
        int resultado = calc.dividir(a, b);

        //verificacao
        Assert.assertEquals(5, resultado);
    }

    @Test(expected = NaoPodeDividirPorZeroException.class)
    public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
        //cenario
        int a = 10;
        int b = 0;

        //acao
        int resultado = calc.dividir(a, b);
    }
}
