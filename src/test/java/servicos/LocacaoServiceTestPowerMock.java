package servicos;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.EmailService;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.servicos.SPCService;
import br.ce.wcaquino.utils.DataUtils;
import builders.FilmeBuilder;
import builders.UsuarioBuilder;
import matchers.MatchersProprios;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static builders.FilmeBuilder.umFilme;
import static org.hamcrest.CoreMatchers.is;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class})
public class LocacaoServiceTestPowerMock {

    @InjectMocks
    private LocacaoService service;
    @Mock
    private SPCService spcService;
    @Mock
    private EmailService emailService;
    @Mock
    private LocacaoDAO dao;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        service = PowerMockito.spy(service);
    }

    @Test
    public void deveAlugarFilme() throws Exception {

        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(12,8,2020));

        //acao
        Locacao locacao = null;
        locacao = service.alugarFilme(usuario, filmes);

        //verificacao
        error.checkThat(locacao.getValor(), is(CoreMatchers.equalTo(5.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(12,8,2020)), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(13,8,2020)), is(true));
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
//        assume.assumetrue(datautils.verificardiasemana(new date(), calendar.saturday));

        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(13,8,2020));

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificacao
        Assert.assertThat(locacao.getDataRetorno(), MatchersProprios.caiNumaSegunda());
    }

    @Test
    public void deveAlugarFilmeSemCalcularValor() throws Exception {
        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

        PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificacao
        Assert.assertEquals(locacao.getValor(), is(1.0));
        PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
    }

    @Test
    public void deveCalcularValorLocacao() throws Exception {
        //cenario
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

        //acao
        Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);

        //verificacao
        Assert.assertThat(valor, is(4.0));
    }

}