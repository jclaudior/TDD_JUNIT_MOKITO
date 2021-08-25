package br.ce.wcaquino.servicos;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;
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

import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.metchers.MatcherProprios.caiNumaSegunda;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class, DataUtils.class})
public class LocacaoServiceTestePowerMocki {
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
        locacaoService = PowerMockito.spy(locacaoService);
    }


    @Test
    public void testeLocacao() throws Exception {

//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.DAY_OF_MONTH, 28);
//        calendar.set(Calendar.MONTH, Calendar.APRIL);
//        calendar.set(Calendar.YEAR, 2017);
//        PowerMockito.mockStatic(Calendar.class);
//        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(obterData(28, 4, 2017));
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora(),umFilme().agora());

        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

        error.checkThat("Erro na comparacao de valor da locacao!", locacao.getValor(), is(equalTo(10.0)));
        error.checkThat("Erro na Data de devolucao!",isMesmaData(locacao.getDataLocacao(), obterData(28,04,2017)),is(true));
        error.checkThat("Erro na Data de devolucao!", isMesmaData(locacao.getDataRetorno(), obterData(29,04,2017)),is(true));

    }

    @Test
    public void deveDevolverNaSegundaSeAlugarNoSabado() throws Exception {
        //cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.DAY_OF_MONTH, 29);
//        calendar.set(Calendar.MONTH, Calendar.APRIL);
//        calendar.set(Calendar.YEAR, 2017);
//        PowerMockito.mockStatic(Calendar.class);
//        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(obterData(29, 4, 2017));

        //acao
        Locacao retorno = locacaoService.alugarFilme(usuario, filmes);

        //verificacao
        assertThat(retorno.getDataRetorno(), caiNumaSegunda());

    }

    @Test
    public void deveAlugarFimeSemCacularValor() throws Exception {
        //cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        PowerMockito.doReturn(1.0).when(locacaoService, "getValorLocacao", filmes);

        //acao
        Locacao locacao = locacaoService.alugarFilme(usuario,filmes);

        //verificacao
        assertThat(locacao.getValor(), is(1.0));
        PowerMockito.verifyPrivate(locacaoService).invoke("getValorLocacao", filmes);
    }

    @Test
    public void deveCalcularValorLocacao() throws Exception {
        //cenario
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        //acao
        Double valor = (Double) Whitebox.invokeMethod(locacaoService,"getValorLocacao", filmes);

        //verificacao
        assertThat(valor, is(5.0));
    }

}
