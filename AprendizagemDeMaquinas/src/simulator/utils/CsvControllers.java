package simulator.utils;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simulator.ambiente.Ambiente;
import simulator.entities.SpecieAnimalia;

public class CsvControllers {

	public void csvCreaturePerformance(SpecieAnimalia sa) {
		String fileName = "Performances";
		FileWriter fileWriter = null;

		// PREPARAR DIRETÓRIO ONDE ARQUIVO VAI SER SALVO
		String projectDir = System.getProperty("user.dir");
		String resDir = projectDir + File.separator + "res";

		File resDirectory = new File(resDir);
		if (!resDirectory.exists()) {
			resDirectory.mkdir();
		}

		String filePath = resDir + File.separator + fileName;
		File file = new File(filePath);

		try {
			// VERIFICAR SE ARQUIVO EXISTE
			boolean fileExists = file.exists();

			// FILEWRITER TEM O APPEND TRUE PARA NÃO SOBRESCREVER O ARQUIVO
			fileWriter = new FileWriter(file, true);

			// Escreve o cabeçalho apenas se o arquivo ainda não existir
			if (!fileExists) {
				fileWriter.append("SpecieName,MoveRate,VisionRange,DaysSurvived\n");
			}

			// Escreve os dados da SpecieAnimalia
			fileWriter.append(sa.getSpecie_name());
			fileWriter.append(",");
			fileWriter.append(Integer.toString(sa.getMove_rate()));
			fileWriter.append(",");
			fileWriter.append(Integer.toString(sa.getVision_range()));
			fileWriter.append(",");
			fileWriter.append(Integer.toString(sa.ponto));
			fileWriter.append("\n");

		} catch (IOException e) {
			System.out.println("Erro ao manipular o arquivo CSV!");
			e.printStackTrace();
		} finally {
			try {
				if (fileWriter != null) {
					fileWriter.flush();
					fileWriter.close();
				}
			} catch (IOException e) {
				System.out.println("Erro ao fechar o FileWriter!");
				e.printStackTrace();
			}
		}
	}

	public void csvCurrentDay(Ambiente ambiente, List<SpecieAnimalia> animals) {
		String fileName = "CurrentDay-" + animals.get(0).getSpecie_name();
		FileWriter fileWriter = null;

		// PREPARAR DIRETÓRIO ONDE ARQUIVO VAI SER SALVO
		String projectDir = System.getProperty("user.dir");
		String resDir = projectDir + File.separator + "res";

		File resDirectory = new File(resDir);
		if (!resDirectory.exists()) {
			resDirectory.mkdir();
		}

		String filePath = resDir + File.separator + fileName;
		File file = new File(filePath);

		try {
			// VERIFICAR SE ARQUIVO EXISTE
			boolean fileExists = file.exists();

			// FILEWRITER TEM O APPEND TRUE PARA NÃO SOBRESCREVER O ARQUIVO
			fileWriter = new FileWriter(file, true);

			// Escreve o cabeçalho apenas se o arquivo ainda não existir
			if (!fileExists) {
				fileWriter.append("Day,MoveRate,VisionRange,Colors\n");
			}

			double moveRateAvarage = 0;
			double visionRangeAvarage = 0;
			StringBuilder colorsCounts = new StringBuilder();
			
			List<Color> colors = new ArrayList<>();

			for (int i = 0; i < animals.size(); i++) {
				moveRateAvarage += animals.get(i).getMove_rate();
				visionRangeAvarage += animals.get(i).getVision_range();
				colors.add(animals.get(i).getColor());
			}
			
			Map<Color, Integer> contagemCores = new HashMap<>();

			for (Color cor : colors) {
				int contagemAtual = contagemCores.getOrDefault(cor, 0);
				contagemCores.put(cor, contagemAtual + 1);
			}

			for (Map.Entry<Color, Integer> parCorContagem : contagemCores.entrySet()) {
				Color cor = parCorContagem.getKey();
				int contagem = parCorContagem.getValue();
				
				colorsCounts.append((contagem + "-" + cor.getRed() + "-" + cor.getGreen() + "-" + cor.getBlue()+"|"));
			}
			
			System.out.println(colorsCounts);
			
			moveRateAvarage = moveRateAvarage / animals.size();
			visionRangeAvarage = visionRangeAvarage / animals.size();
			// Escreve os dados da SpecieAnimalia
			fileWriter.append(Integer.toString(ambiente.getDays()));
			fileWriter.append(",");
			fileWriter.append("" + moveRateAvarage);
			fileWriter.append(",");
			fileWriter.append("" + visionRangeAvarage);
			fileWriter.append(",");
			fileWriter.append(colorsCounts);
			fileWriter.append("\n");

		} catch (IOException e) {
			System.out.println("Erro ao manipular o arquivo CSV!");
			e.printStackTrace();
		} finally {
			try {
				if (fileWriter != null) {
					fileWriter.flush();
					fileWriter.close();
				}
			} catch (IOException e) {
				System.out.println("Erro ao fechar o FileWriter!");
				e.printStackTrace();
			}
		}
	}

}
