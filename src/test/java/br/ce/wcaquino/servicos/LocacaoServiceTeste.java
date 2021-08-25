package br.ce.wcaquino.servicos;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.metchers.MatcherProprios.*;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;


public class LocacaoServiceTeste {
    @InjectMocks @Spy
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


    @Test
    public void testeLocacao() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora(),umFilme().agora());
        Mockito.doReturn(DataUtils.obterData(28,4,2017)).when(locacaoService).obeterData();

        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

        error.checkThat("Erro na comparacao de valor da locacao!", locacao.getValor(), is(equalTo(10.0)));
        error.checkThat("Erro na Data de devolucao!",isMesmaData(locacao.getDataLocacao(), obterData(28,04,2017)),is(true));
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


    @Test
    public void deveDevolverNaSegundaSeAlugarNoSabado() throws Exception {
        //cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        Mockito.doReturn(DataUtils.obterData(29,4,2017)).when(locacaoService).obeterData();

        //acao
        Locacao retorno = locacaoService.alugarFilme(usuario, filmes);

        //verificacao
        assertThat(retorno.getDataRetorno(), caiNumaSegunda());

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

    @Test
    public void deveCalcularValorLocacao() throws Exception {
        //cenario
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        //acao
        Class<LocacaoService> clazz = LocacaoService.class;
        Method metodo = clazz.getDeclaredMethod("getValorLocacao",List.class);
        metodo.setAccessible(true);
        Double valor = (Double) metodo.invoke(locacaoService,filmes);

        //verificacao
        assertThat(valor, is(5.0));
    }

}
