package servicos;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.servicos.EmailService;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.servicos.SPCService;
import br.ce.wcaquino.utils.DataUtils;
import builders.FilmeBuilder;
import builders.LocacaoBuilder;
import builders.UsuarioBuilder;
import matchers.MatchersProprios;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static builders.FilmeBuilder.umFilme;
import static matchers.MatchersProprios.caiNumaSegunda;
import static org.hamcrest.CoreMatchers.is;

public class LocacaoServiceTest {

    @InjectMocks @Spy
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
        System.out.println("Iniciando 2...");
    }

    @After
    public void tearDown() {
        System.out.println("Finalizando 2...");
    }

    @Test
    public void deveAlugarFilme() throws Exception {
//        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());

        Mockito.doReturn(DataUtils.obterData(15, 8, 2020)).when(service).obterData();

        //acao
        Locacao locacao = null;
        locacao = service.alugarFilme(usuario, filmes);

        //verificacao
        error.checkThat(locacao.getValor(), is(CoreMatchers.equalTo(5.0)));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(15,8,2020)), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(17,8,2020)), is(true));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void naoDeveAlugarFilmeSemEstoque() throws Exception {
        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilmeSemEstoque().agora());
        //acao
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
        //cenario
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        //acao
        try {
            service.alugarFilme(null, filmes);
            Assert.fail();
        } catch (LocadoraException e) {
            Assert.assertThat(e.getMessage(), is("Usuario Vazio"));
        }
    }

    @Test
    public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme Vazio");

        //acao
        service.alugarFilme(usuario, null);
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
//        assume.assumetrue(datautils.verificardiasemana(new date(), calendar.saturday));

        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        Mockito.doReturn(DataUtils.obterData(15, 8, 2020)).when(service).obterData();

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificacao
        Assert.assertThat(locacao.getDataRetorno(), caiNumaSegunda());
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        Mockito.when(spcService.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);

        //acao
        try {
            service.alugarFilme(usuario, filmes);
        //verificacao
            Assert.fail();
        } catch (LocadoraException e) {
            Assert.assertThat(e.getMessage(), is("Usuario Negativado"));
        }

        Mockito.verify(spcService).possuiNegativacao(usuario);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas() {
        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario em dia").agora();
        Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Outro atrasado").agora();
        List<Locacao> locacoes = Arrays.asList(LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario).agora(),
                                                LocacaoBuilder.umLocacao().comUsuario(usuario2).agora(),
                                                LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario3).agora(),
                                                LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario3).agora());
        Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        //acao
        service.notificarAtrasos();

        //verificacao
        Mockito.verify(emailService, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));

        Mockito.verify(emailService).notificarAtraso(usuario);
        Mockito.verify(emailService, Mockito.atLeastOnce()).notificarAtraso(usuario3);
        Mockito.verify(emailService, Mockito.never()).notificarAtraso(usuario2);
        Mockito.verifyNoMoreInteractions(emailService);
        Mockito.verifyZeroInteractions(spcService);

    }

    @Test
    public void deveTratarErroNoSPC() throws Exception {
        //cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

        Mockito.when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha Catastrofica!!!"));

        //verificacao
        exception.expect(LocadoraException.class);
        exception.expectMessage("Problemas com SPC, tente novamente!");

        //acao
        service.alugarFilme(usuario, filmes);

    }

    @Test
    public void deveProrrogarUmaLocacao() {
        //cenário
        Locacao locacao = LocacaoBuilder.umLocacao().agora();


        //ação
        service.prorrogarLocacao(locacao, 3);

        //verificação
        ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(dao).salvar(argCapt.capture());
        Locacao locacaoRetornada = argCapt.getValue();

        error.checkThat(locacaoRetornada.getValor(), is(12.0));
        error.checkThat(locacaoRetornada.getDataLocacao(), MatchersProprios.ehHoje());
        error.checkThat(locacaoRetornada.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDeDias(3));

    }

    @Test
    public void deveCalcularValorLocacao() throws Exception {
        //cenario
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

        //acao
        Class<LocacaoService> clazz = LocacaoService.class;
        Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
        metodo.setAccessible(true);
        Double valor = (Double) metodo.invoke(service, filmes);
//        Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);

        //verificacao
        Assert.assertThat(valor, is(4.0));
    }

}