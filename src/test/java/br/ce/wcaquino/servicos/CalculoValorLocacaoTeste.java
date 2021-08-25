package br.ce.wcaquino.servicos;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.dao.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTeste {
    @InjectMocks
    private LocacaoService locacaoService = null;
    @Mock
    private SPCService spcService = null;
    @Mock
    private LocacaoDAO dao = null;

    @Parameterized.Parameter
    public List<Filme> filmes;
    @Parameterized.Parameter(value = 1)
    public Double valorLocacao;

    @Parameterized.Parameter(value = 2)
    public String descricaoTeste;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    private static Filme filme1 = umFilme().agora();
    private static Filme filme2 = umFilme().agora();
    private static Filme filme3 = umFilme().agora();
    private static Filme filme4 = umFilme().agora();
    private static Filme filme5 = umFilme().agora();
    private static Filme filme6 = umFilme().agora();

    @Parameterized.Parameters(name = "Teste {2}")
    public static Collection<Object[]> getParametros(){
        return Arrays.asList(new Object[][]{
                {Arrays.asList(filme1,filme2,filme3),13.75,"25% Desconto Terceiro Filme"},
                {Arrays.asList(filme1,filme2,filme3,filme4),16.25,"50% Desconto Quarto Filme"},
                {Arrays.asList(filme1,filme2,filme3,filme4,filme5),17.5,"75% Desconto Quinto Filme"},
                {Arrays.asList(filme1,filme2,filme3,filme4,filme5,filme6),17.5,"100% Desconto Sexto Filme"}
        });
    }

    @Test
    public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {
        Usuario usuario = new Usuario("João");

        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

        assertThat(locacao.getValor(), is(equalTo(valorLocacao)));

    }
}
