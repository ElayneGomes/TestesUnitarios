package servicos;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.daos.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.servicos.SPCService;
import builders.FilmeBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

    @Parameterized.Parameter
    public List<Filme> filmes;

    @Parameterized.Parameter(value = 1)
    public Double valorLocacao;

    @Parameterized.Parameter(value = 2)
    public String cenario;

    @InjectMocks
    private LocacaoService service;
    @Mock
    private SPCService spcService;
    @Mock
    private LocacaoDAO dao;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private static Filme filme1 = FilmeBuilder.umFilme().agora();
    private static Filme filme2 = FilmeBuilder.umFilme().agora();
    private static Filme filme3 = FilmeBuilder.umFilme().agora();

    private static Filme filme4 = FilmeBuilder.umFilme().agora();
    private static Filme filme5 = FilmeBuilder.umFilme().agora();
    private static Filme filme6 = FilmeBuilder.umFilme().agora();
    private static Filme filme7 = FilmeBuilder.umFilme().agora();

    @Parameterized.Parameters(name = "Teste= {2}")
    public static Collection<Object[]> getParametros() {
        return Arrays.asList(new Object[][] {
                {Arrays.asList(filme1, filme2), 8.0, "2 Filmes: Sem desconto"},
                {Arrays.asList(filme1, filme2, filme3), 11.0, "3 Filmes: 25% de desconto"},
                {Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 Filmes: 50% de desconto"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "5 Filmes: 75% de desconto"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 Filmes: 100% de desconto"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "7 Filmes: Sem desconto"}
        });
    }

    @Test
    public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario("Usuário1");

        //acao
        Locacao resultado = service.alugarFilme(usuario, filmes);

        //verificacao
        Assert.assertThat(resultado.getValor(), CoreMatchers.is(CoreMatchers.equalTo(valorLocacao)));
    }

    @Test
    public void print() {
        System.out.println(valorLocacao);
    }

}
