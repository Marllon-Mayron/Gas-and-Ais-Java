package simulator.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	//ESTÁ CLASSE VAI CARREGAR METODOS QUE VÃO SER USADOS EM TODOS OS CANTOS DO CÓDIGO, E ALGUMAS CONFIGURAÇÕES
	public static Random random = new Random();
	//MOSTAR MENSAGENS NO CONSOLE
	public static boolean showConsole = false;
	//MOSTRAR RANGE DE VISÃO DOS INDIVIDUOS
	public static boolean showRangeVision = false;
	//TAMANHO DO MAPA
	public static int numGrid = 40;
	//CONTROLE DE CSV
	public static CsvControllers csvController = new CsvControllers();
	
	//METODO PARA O CONTROLE DE MENSAGENS NO CONSOLE
	public static void showMessages(String message, boolean breakLine) {
		if(showConsole) {
			if(breakLine) {
				System.out.println(message);
			}else {
				System.out.print(message);
			}
			
		}
		
	}
	//CALCULAR DISTANCIA DOS PONTOS
    public static int heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
    
    public static double truncate(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.DOWN);
        return bd.doubleValue();
    }
    
    public static boolean verificarPadrao(String regex,String string) {
        String padrao = regex;
        Pattern pattern = Pattern.compile(padrao);
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }
	
}
