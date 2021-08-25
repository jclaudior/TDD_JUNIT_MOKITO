package br.ce.wcaquino.suites;

import br.ce.wcaquino.servicos.CalculadoraTeste;
import br.ce.wcaquino.servicos.CalculoValorLocacaoTeste;

import br.ce.wcaquino.servicos.LocacaoServiceTeste;
import br.ce.wcaquino.servicos.LocacaoServiceTesteComentado;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        //CalculadoraTeste.class,
        CalculoValorLocacaoTeste.class,
        LocacaoServiceTeste.class
})
public class SuiteExecucao {
}
