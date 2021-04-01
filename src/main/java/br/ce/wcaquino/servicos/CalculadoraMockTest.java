package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class CalculadoraMockTest {

    @Mock
    private Calculadora calcMock;

    @Spy
    private Calculadora calcSpy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void devoMostrarDiferencaEntreMockSpy() {
        Mockito.when(calcMock.somar(1,2)).thenCallRealMethod();
        Mockito.when(calcSpy.somar(1,3)).thenReturn(8);

        System.out.println("Mock: " + calcMock.somar(1, 2));
        System.out.println("SPY: " + calcSpy.somar(1, 2));
    }

    @Test
    public void teste() {
        Calculadora calc = Mockito.mock(Calculadora.class);

        ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
        Mockito.when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);

//        System.out.println(calc.somar(1, 8));

        Assert.assertEquals(5, calc.somar(1, 100000));
//        System.out.println(argCapt.getAllValues());

    }
}
