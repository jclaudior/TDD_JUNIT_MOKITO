package br.ce.wcaquino.metchers;

import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import javax.xml.crypto.Data;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataAtualMetcher extends TypeSafeMatcher <Date> {

    private Integer qtdDias;

    public DataAtualMetcher(Integer qtdDias) {
        this.qtdDias = qtdDias;
    }



    @Override
    protected boolean matchesSafely(Date date) {
        return DataUtils.isMesmaData(date, DataUtils.obterDataComDiferencaDias(qtdDias));
    }

    @Override
    public void describeTo(Description description) {
        Date dataEsperada = DataUtils.obterDataComDiferencaDias(qtdDias);
        DateFormat format = new SimpleDateFormat("dd/MM/YYYY");
        description.appendText(format.format(dataEsperada));

    }
}
