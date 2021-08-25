package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.Assert;
import org.junit.Test;

public class LocacaoService {

	private LocacaoDAO dao;
	private SPCService spcService;
	private MailService mailService;

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {


		if(usuario == null){
			throw  new LocadoraException("Usuario Vazio");
		}
		if(filmes == null || filmes.isEmpty()){
			throw new LocadoraException("Filmes Vazio");
		}
		for (Filme filme: filmes) {
			if (filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException();
			}
		}
		boolean negativado;
		try {
			negativado = spcService.possuiNegativacao(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Problemas com spc, tente novamente");
		}

		if(negativado){
			throw  new LocadoraException("Usuario Negativado");
		}
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(obeterData());
		locacao.setValor(getValorLocacao(filmes));

		//Entrega no dia seguinte
		Date dataEntrega = obeterData();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)){
			dataEntrega = DataUtils.adicionarDias(dataEntrega, 1);
		}


		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		dao.salvar(locacao);
		
		return locacao;
	}

	protected Date obeterData() {
		return new Date();
	}

	private double getValorLocacao(List<Filme> filmes) {
		double valorLocacao = 0;
		for(int i = 0; i < filmes.size(); i ++){
			Filme filme = filmes.get(i);
			switch (i){
				case 2: valorLocacao += filme.getPrecoLocacao() * 0.75; break;
				case 3: valorLocacao += filme.getPrecoLocacao() * 0.50; break;
				case 4: valorLocacao += filme.getPrecoLocacao() * 0.25; break;
				case 5: valorLocacao += filme.getPrecoLocacao() * 0.00; break;
				default: valorLocacao += filme.getPrecoLocacao();
			}
		}
		return valorLocacao;
	}

	public void notificarAtrasos(){
		List<Locacao> locacoes = dao.obterLocacoesPedentes();
		for(Locacao locacao: locacoes){
			if(locacao.getDataRetorno().before(obeterData())) {
				mailService.notificarAtraso(locacao.getUsuario());
			}
		}

	}

	public void prorrogarLocacao(Locacao locacao, int dias){
		Locacao novaLocacao = new Locacao();
		novaLocacao.setFilmes(locacao.getFilmes());
		novaLocacao.setDataLocacao(obeterData());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor()*dias);
		dao.salvar(novaLocacao);

	}
}