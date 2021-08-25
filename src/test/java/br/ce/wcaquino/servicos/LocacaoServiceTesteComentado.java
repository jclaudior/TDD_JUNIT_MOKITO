package br.ce.wcaquino.servicos;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.metchers.DataAtualMetcher;
import br.ce.wcaquino.metchers.MatcherProprios;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.metchers.MatcherProprios.*;
import static br.ce.wcaquino.utils.DataUtils.*;
import static br.ce.wcaquino.utils.DataUtils.verificarDiaSemana;
import static java.util.Calendar.SATURDAY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class, DataUtils.class})
public class LocacaoServiceTesteComentado {
    @InjectMocks
    private LocacaoService locacaoService = null;
    @Mock
    private SPCService spcService = null;
    @Mock
    private LocacaoDAO dao = null;
    @Mock
    private MailService mailService = null;


    private static int count = 0;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

//    @After
//    public void tearDown(){
//        System.out.println("After");
//    }
//
//    @BeforeClass
//    public static void setupClass(){
//        System.out.println("BeforeClass");
//    }
//
//    @AfterClass
//    public static void tearDownClass(){
//        System.out.println("AfterClass");
//    }

    @Test
    public void testeLocacao() throws Exception {

        //PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(obterData(28, 4, 2017));
        //Assume.assumeFalse(verificarDiaSemana(new Date(), SATURDAY));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 28);
        calendar.set(Calendar.MONTH, Calendar.APRIL);
        calendar.set(Calendar.YEAR, 2017);
        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora(),umFilme().agora());
        Locacao locacao = null;

        locacao = locacaoService.alugarFilme(usuario, filmes);

//        Assert.assertEquals("Erro na comparacao de valor da locacao!",5.0, locacao.getValor(),0.01);
//        Assert.assertTrue("Erro na compracao na Data da locacao!",DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
//        Assert.assertTrue("Erro na Data de devolucao!",DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));

//        assertThat("Erro na comparacao de valor da locacao!", locacao.getValor(), is(equalTo(5.0)));
//        assertThat("Erro na compracao na Data da locacao!", isMesmaData(locacao.getDataLocacao(), new Date()),is(true));
//        assertThat("Erro na Data de devolucao!", isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)),is(true));

        error.checkThat("Erro na comparacao de valor da locacao!", locacao.getValor(), is(equalTo(10.0)));
//        error.checkThat("Erro na compracao na Data da locacao!", isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat("Erro na compracao na Data da locacao!",locacao.getDataLocacao(), dataAtual());
        error.checkThat("Erro na compracao na Data da locacao!",  isMesmaData(locacao.getDataLocacao(), obterData(28,04,2017)),is(true));
       // error.checkThat("Erro na Data de devolucao!", isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
        error.checkThat("Erro na Data de devolucao!",locacao.getDataRetorno(), dataAtualMais(1));
        error.checkThat("Erro na Data de devolucao!", isMesmaData(locacao.getDataRetorno(), obterData(29,04,2017)),is(true));

    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void testeLocacao_FilmeSemEstoque() throws Exception{
        Usuario usuario = umUsuario().agora();
        Filme filme1 = umFilme().agora();
        Filme filme2 = umFilme().semEstoque().agora();
        Filme filme3 = umFilme().agora();

        List<Filme> filmes = Arrays.asList(filme1,filme2,filme3);

        Locacao locacao = null;

        locacao = locacaoService.alugarFilme(usuario, filmes);

    }

    @Test
    public void testeLocacaoUsuarioVazio () throws FilmeSemEstoqueException {
        Filme filme1 = umFilme().agora();
        Filme filme2 = umFilme().agora();
        Filme filme3 = umFilme().agora();

        List<Filme> filmes = Arrays.asList(filme1,filme2,filme3);

        Locacao locacao = null;

        try {
            locacao = locacaoService.alugarFilme(null, filmes);
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuario Vazio"));
        }

    }

    @Test
    public void testeLocacaoFilmeVazio () throws FilmeSemEstoqueException, LocadoraException {
        Usuario usuario = umUsuario().agora();

        Locacao locacao = null;

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filmes Vazio");

        locacao = locacaoService.alugarFilme(usuario, null);


    }

//    @Test
//    public void testeLocacao_FilmeSemEstoque2(){
//        Usuario usuario = new Usuario("João");
//        Filme filme = new Filme("De volta para o futuro", 0, 5.0);
//
//        LocacaoService locacaoService = new LocacaoService();
//
//        Locacao locacao = null;
//
//        try {
//            locacao = locacaoService.alugarFilme(usuario, filme);
//        } catch (Exception e) {
//           assertThat(e.getMessage(), is("Filme sem Estoque"));
//        }
//
//    }
//
//    @Test
//    public void testeLocacao_FilmeSemEstoque3() throws Exception{
//        Usuario usuario = new Usuario("João");
//        Filme filme = new Filme("De volta para o futuro", 0, 5.0);
//
//        LocacaoService locacaoService = new LocacaoService();
//
//        Locacao locacao = null;
//
//        exception.expect(Exception.class);
//        exception.expectMessage("Filme sem Estoque");
//
//        locacao = locacaoService.alugarFilme(usuario, filme);
//
//
//    }


//    @Test
//    public void testaTerceiroFimeDescontoVinteCincoPorcento() throws FilmeSemEstoqueException, LocadoraException {
//        Usuario usuario = umUsuario().agora();
//        Filme filme1 = new Filme("De volta para o futuro", 5, 5.0);
//        Filme filme2 = new Filme("De volta para o futuro II", 2, 5.0);
//        Filme filme3 = new Filme("De volta para o futuro III", 5, 5.0);
//
//        List<Filme> filmes = Arrays.asList(filme1,filme2,filme3);
//
//        Locacao locacao = null;
//
//        locacao = locacaoService.alugarFilme(usuario, filmes);
//
//        assertThat(locacao.getValor(), is(equalTo(13.75)));
//
//    }
//
//    @Test
//    public void testaQuartoFilmeDescontoCinquentaPorcento() throws FilmeSemEstoqueException, LocadoraException {
//        Usuario usuario = umUsuario().agora();
//        Filme filme1 = new Filme("De volta para o futuro", 5, 5.0);
//        Filme filme2 = new Filme("De volta para o futuro II", 2, 5.0);
//        Filme filme3 = new Filme("De volta para o futuro III", 5, 5.0);
//        Filme filme4 = new Filme("A Mumia", 5, 5.0);
//
//        List<Filme> filmes = Arrays.asList(filme1,filme2,filme3, filme4);
//
//        Locacao locacao = null;
//
//        locacao = locacaoService.alugarFilme(usuario, filmes);
//
//        assertThat(locacao.getValor(), is(equalTo(16.25)));
//
//    }
//
//    @Test
//    public void testaQuintoFilmeDescontoSetentaECincoPorcento() throws FilmeSemEstoqueException, LocadoraException {
//        Usuario usuario = umUsuario().agora();
//        Filme filme1 = new Filme("De volta para o futuro", 5, 5.0);
//        Filme filme2 = new Filme("De volta para o futuro II", 2, 5.0);
//        Filme filme3 = new Filme("De volta para o futuro III", 5, 5.0);
//        Filme filme4 = new Filme("A Mumia", 5, 5.0);
//        Filme filme5 = new Filme("Homem de Ferro", 5, 5.0);
//
//        List<Filme> filmes = Arrays.asList(filme1,filme2,filme3, filme4, filme5);
//
//        Locacao locacao = null;
//
//        locacao = locacaoService.alugarFilme(usuario, filmes);
//
//        assertThat(locacao.getValor(), is(equalTo(17.5)));
//
//    }
//
//    @Test
//    public void testaSextoFilmeDescontoCemPorcento() throws FilmeSemEstoqueException, LocadoraException {
//        Usuario usuario = umUsuario().agora();
//        Filme filme1 = new Filme("De volta para o futuro", 5, 5.0);
//        Filme filme2 = new Filme("De volta para o futuro II", 2, 5.0);
//        Filme filme3 = new Filme("De volta para o futuro III", 5, 5.0);
//        Filme filme4 = new Filme("A Mumia", 5, 5.0);
//        Filme filme5 = new Filme("Homem de Ferro", 5, 5.0);
//        Filme filme6 = new Filme("Interestelar", 5, 5.0);
//
//        List<Filme> filmes = Arrays.asList(filme1,filme2,filme3, filme4, filme5, filme6);
//
//        Locacao locacao = null;
//
//        locacao = locacaoService.alugarFilme(usuario, filmes);
//
//        assertThat(locacao.getValor(), is(equalTo(17.5)));
//
//    }

    @Test
    public void deveDevolverNaSegundaSeAlugarNoSabado() throws Exception {
        //Assume.assumeTrue(verificarDiaSemana(new Date(), SATURDAY));

        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        //PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(obterData(29, 4, 2017));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        calendar.set(Calendar.MONTH, Calendar.APRIL);
        calendar.set(Calendar.YEAR, 2017);
        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
        //acao
        Locacao retorno = locacaoService.alugarFilme(usuario, filmes);

        //verificacao
        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
        //PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();

    }

    @Test
    public void naoDeveAlugarFilmeNegativadoSPC() throws Exception {
        Usuario usuario = umUsuario().agora();
        Usuario usuario1 = umUsuario().comNome("Daniela").agora();

        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spcService.possuiNegativacao(Matchers.<Usuario>any(Usuario.class))).thenReturn(true);

        try {
            locacaoService.alugarFilme(usuario,filmes);
            fail();
        }  catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuario Negativado"));
        }

        verify(spcService).possuiNegativacao(usuario);

    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrazadas(){
        //cenario
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Um usuario em dia").agora();
        Usuario usuario3 = umUsuario().comNome("Mais um usuario atrasado").agora();
        List<Locacao> locacaos = Arrays.asList(
                umLocacao().comUsuario(usuario).atrasada().agora(),
                umLocacao().comUsuario(usuario2).agora(),
                umLocacao().comUsuario(usuario3).atrasada().agora()
        );

        //cenario
        when(dao.obterLocacoesPedentes()).thenReturn(locacaos);

        //acao
        locacaoService.notificarAtrasos();


        //verificacao
        verify(mailService, times(2)).notificarAtraso(Matchers.<Usuario>any(Usuario.class));
        verify(mailService).notificarAtraso(usuario);
        verify(mailService, never()).notificarAtraso(usuario2);
        verify(mailService).notificarAtraso(usuario3);
        verifyNoMoreInteractions(mailService);

    }

    @Test
    public void deveTratarErronoSPC() throws Exception {
        //cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha castratrofica"));

        exception.expect(LocadoraException.class);
        exception.expectMessage("Problemas com spc, tente novamente");
        //acao
        locacaoService.alugarFilme(usuario,filmes);

    }

    @Test
    public void deveProrogarUmaLocacao(){
        //cenario
        Locacao locacao = umLocacao().agora();


        //acao
        locacaoService.prorrogarLocacao(locacao,3);

        //vericacao
        ArgumentCaptor<Locacao> argCap = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(dao).salvar(argCap.capture());
        Locacao locacaoCapturada = argCap.getValue();

        error.checkThat(locacaoCapturada.getValor(), is(15.0));
        error.checkThat(locacaoCapturada.getDataLocacao(), dataAtual());
        error.checkThat(locacaoCapturada.getDataRetorno(), dataAtualMais(3));
    }

}
