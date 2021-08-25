package br.ce.wcaquino.servicos;

public class Calculadora {
    public int soma(int a, int b) {
        return a+b;
    }

    public int subtracao(int a, int b) {
        return a-b;
    }

    public int dividir(int a, int b) throws Exception {
        if(a == 0 || b == 0)
            throw new Exception("Nao e possivel dividir por zero!");
        return a/b;
    }
}
