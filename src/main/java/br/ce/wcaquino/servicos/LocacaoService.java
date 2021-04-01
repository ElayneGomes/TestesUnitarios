package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

public class LocacaoService {

	public String vPublica;
	protected String vProtegida;
	private String vPrivada;
	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {

		if(filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme Vazio");
		}

		if(usuario == null) {
			throw new LocadoraException("Usuario Vazio");
		}

		for (Filme filme : filmes) {
			if(filme.getEstoque() == 0){
				throw new FilmeSemEstoqueException();
			}
		}

		boolean negativado;
		try {
			negativado = spcService.possuiNegativacao(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Problemas com SPC, tente novamente!");
		}

		if (negativado) {
			throw new LocadoraException("Usuario Negativado");
		}

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(obterData());
		locacao.setValor(calcularValorLocacao(filmes));

		//Entrega no dia seguinte
		Date dataEntrega = obterData();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		dao.salvar(locacao);
		
		return locacao;
	}

	public Date obterData() {
		return new Date();
	}

	private Double calcularValorLocacao(List<Filme> filmes) {
		System.out.println("Estou Calculando!!!");
		Double valorTotal = 0d;
		for (int i =0; i < filmes.size(); i++) {
			Filme filme = filmes.get(i);
			Double valorFilme = filme.getPrecoLocacao();
			switch (i) {
				case 2:
					valorFilme = valorFilme * 0.75; break;
				case 3:
					valorFilme = valorFilme * 0.5; break;
				case 4:
					valorFilme = valorFilme * 0.25; break;
				case 5:
					valorFilme = valorFilme * 0; break;
			}
			valorTotal += valorFilme;
		}
		return valorTotal;
	}

	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		for (Locacao locacao: locacoes) {
			if (locacao.getDataRetorno().before(obterData())) {
				emailService.notificarAtraso(locacao.getUsuario());
			}
		}
	}

	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilmes(locacao.getFilmes());
		novaLocacao.setValor(locacao.getValor() * dias);
		novaLocacao.setDataLocacao(obterData());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		dao.salvar(novaLocacao);
	}

}