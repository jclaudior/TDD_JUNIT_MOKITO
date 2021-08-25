package br.ce.wcaquino.metchers;

import java.util.Calendar;

public class MatcherProprios {
    public static DiaSemanaMetcher caiEm (Integer diaSemana){
        return new DiaSemanaMetcher(diaSemana);
    }

    public static DiaSemanaMetcher caiNumaSegunda (){
        return new DiaSemanaMetcher(Calendar.MONDAY);
    }

    public static DataAtualMetcher dataAtualMais(Integer dias){
        return new DataAtualMetcher(dias);
    }

    public static DataAtualMetcher dataAtual(){
        return new DataAtualMetcher(0);
    }


}
