package suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import servicos.CalculadoraTest;
import servicos.CalculoValorLocacaoTest;
import servicos.LocacaoServiceTest;

//@RunWith(Suite.class)
@Suite.SuiteClasses({
//        CalculadoraTest.class,
        CalculoValorLocacaoTest.class,
        LocacaoServiceTest.class
})
public class SuiteExecucao {
    //É obrigado pelo Java ter uma declaração de classe
}
