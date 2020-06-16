import br.com.tlmacedo.cafeperfeito.model.enums.CteTomadorServico;
import br.com.tlmacedo.cafeperfeito.service.ServiceUtilJSon;

public class Test {

    public static void main(String[] args) {
        ServiceUtilJSon.printJsonFromList(CteTomadorServico.getList(), "listTeste");
    }
}
