package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Usuario;

public interface MailService {
    public void notificarAtraso(Usuario usuario);
}
