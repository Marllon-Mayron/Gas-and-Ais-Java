package simulator.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import simulator.ambiente.Ambiente;
import simulator.entities.animalia.Coquito;
import simulator.enums.EatingPractices;
import simulator.main.Window;
import simulator.utils.Utils;

public class SpecieAnimalia {

	// NOME DA ESPECIE DA ENTIDADE
	// DNA É UMA VARIAVEL DE 6 LETRAS, ESSA VARIAVEL SERÁ PASSADA PARA OS FILHOS.
	// PARA DNA:
	// 1L = Cor, 2L = Tamanho, 3L = Comportamento, 4L = Velocidade, 5L = Taxa de
	// movimentação, 6L = Range de visão.
	// POSIÇÕES XY DE CADA ENTIDADE, ESSAS VARIAVEIS REPRESENTAM A LOCALIZAÇÃO DE
	// CADA SER VIVO.
	// WIDTH HEIGHT SÃO O TAMANHO DA ENTIDADE
	private String specie_name;

	private EatingPractices eating_pratices;

	private String dna;

	private int pos_x;
	private int pos_y;

	private double width;
	private double height;

	private double speed;

	private String gender;

	private boolean hungry;

	private Color color;

	private int colorNum;

	private int vision_range;

	private int move_rate;

	private int min_vision = 3;

	public int ponto;

	private Ambiente ambiente;

	private int mutationPercent = 10;

	public SpecieAnimalia(String specie_name, EatingPractices eating_pratices, String dna, int pos_x, int pos_y,
			double width, double heigt, Ambiente ambiente) {
		super();
		this.specie_name = specie_name;
		this.eating_pratices = eating_pratices;
		this.dna = dna;
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.width = width;
		this.height = heigt;
		this.ambiente = ambiente;

		this.setHungry(true);

		// DEFININDO O GENERO DO MEU ANIMAL
		if (Utils.random.nextBoolean()) {
			this.setGender("Male");
		} else {
			this.setGender("Female");
		}

		// DEFINIR DNA DE CRIATURAS QUE NÃO SÃO FILHAS
		if (dna == "" || dna == null) {
			// DEFININDO A VISÃO DO MEU ANIMAL
			this.vision_range = min_vision + 2 * Utils.random.nextInt(15);
			// DEFININDO UMA COR PARA MEU ANIMAL
			this.colorNum = Utils.random.nextInt(6);
			this.color = getColorList(colorNum);
			// DEFINIR QUANTIDADES DE MOVIMENTOS POR SEGUNDO
			this.move_rate = 20 + Utils.random.nextInt(10);
			// CRIAR O DNA COM AS CARACTERISTICAS ACIMA
			this.dna = createDNA();
		}

	}

	// 1L = Cor, 2L = Tamanho, 3L = Velocidade, 4L = Taxa de movimentação, 5L =
	// Range de visão.
	public String createDNA() {
		String dna = colorNum + decideGeneticTrasmission() + "-" + getHeight() + decideGeneticTrasmission() + "-"
				+ getSpeed() + decideGeneticTrasmission() + "-" + getMove_rate() + decideGeneticTrasmission() + "-"
				+ getVision_range() + decideGeneticTrasmission();
		return dna;
	}

	public String decideGeneticTrasmission() {
		if (Utils.random.nextBoolean()) {
			return "D";
		} else {
			return "R";
		}
	}

	// METODO RESPONSAVEL POR FAZER A REPRODUÇÃO DA ESPECIE (CROSSOVER DOS GENES)
	public void reproduce(SpecieAnimalia father) {
		newSpecieOfDna(this, organizeDna(father));
	}

	public String organizeDna(SpecieAnimalia father) {
		// DNA DOS PAIS
		String fatherChromosomes[] = father.dna.split("-");
		String motherChromosomes[] = this.dna.split("-");
		// DNA DO FILHO
		StringBuilder sonDna = new StringBuilder();

		for (int i = 0; i < motherChromosomes.length; i++) {
			// SE AMBOS FOREM DOMINANTES/RECESSIVOS A CARACTERISTICA SERÁ ALEATORIA
			if ((motherChromosomes[i].contains("D") && fatherChromosomes[i].contains("D"))
					|| (motherChromosomes[i].contains("R") && fatherChromosomes[i].contains("R"))) {
				if (Utils.random.nextBoolean()) {
					sonDna.append(checkMutation(motherChromosomes[i]));
				} else {
					sonDna.append(checkMutation(fatherChromosomes[i]));
				}
			} else {
				// PASSANDO GENES
				if (motherChromosomes[i].contains("D")) {
					sonDna.append(checkMutation(motherChromosomes[i]));
				} else {
					sonDna.append(checkMutation(fatherChromosomes[i]));
				}
			}
			sonDna.append("-");

		}
		// DELETANDO ULTIMO TRACINHO
		sonDna.deleteCharAt(sonDna.length() - 1);
		return "" + sonDna;
	}

	public String checkMutation(String dna) {
		int numMutation = 1;
		if(Utils.random.nextBoolean()) {
			numMutation = 3;
		}
		if (Utils.random.nextInt(100) + 1 < mutationPercent) {
			// Separar tipo de alelo
			char alleleType = dna.charAt(dna.length() - 1);
			// Extraindo o valor do alelo
			String numericPart = dna.substring(0, dna.length() - 1);
			// Passando uma mutação para um gene
			try {
				boolean isDouble = numericPart.contains(".");
				double alleleValue;
				if (isDouble) {
					alleleValue = Double.parseDouble(numericPart);
				} else {
					alleleValue = Integer.parseInt(numericPart);
				}
				// Definir se a mutação será boa ou ruim
				if (Utils.random.nextBoolean()) {
					alleleValue += Utils.random.nextInt(numMutation)+1;
				} else {
					if (alleleValue > 3) {
						alleleValue -= Utils.random.nextInt(numMutation)+1;
					}
				}
				// Formatar o valor de volta para a string, mantendo o formato original
				if (isDouble) {
					dna = String.format("%.1f%s", alleleValue, alleleType);
				} else {
					dna = String.format("%d%s", (int) alleleValue, alleleType);
				}
				Utils.showMessages("Houve uma mutação", true);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		// GARANTIR QUE NENHUM NUMERO VENHA COM ,
		if(dna.contains(",")) {
			dna = dna.replace(",", ".");
		}

		return dna;
	}

	public void newSpecieOfDna(SpecieAnimalia mother, String dna) {
		String alelos[] = dna.split("-");

		SpecieAnimalia son = new SpecieAnimalia(mother.getSpecie_name(), mother.getEating_pratices(), mother.getDna(),
				0, 0, mother.getWidth(), mother.getWidth(), mother.getAmbiente());
		// CHECAR QUAL ESPECIE PELO NOME CIENTIFICO:

		for (int i = 0; i < alelos.length; i++) {
			alelos[i] = alelos[i].substring(0, alelos[i].length() - 1);
		}

		try {
			son.setColor(getColorList(Integer.parseInt(alelos[0])));
			son.setWidth(Double.parseDouble(alelos[1]));
			son.setSpeed(Double.parseDouble(alelos[2]));
			son.setMove_rate(Integer.parseInt(alelos[3]));
			son.setVision_range(Integer.parseInt(alelos[4]));
		} catch (NumberFormatException e) {
			System.err.println("Error parsing alelos values: " + e.getMessage());
		}
		// COLOCANDO O INDIVIDUO EM UM LUGAR QUE NÃO ESTEJA BLOQUEADO:
		int tempx = 0;
		int tempy = 0;
		int distanceMother = 1;
		
		int approximation = (Utils.numGrid/2) - 1;
		int dirApproximation = 3;
		do {
			
			//GARANTIR QUE NAO FIQUE FORA DO MAPA
			do {
				tempx = this.getPos_x() - ((Utils.numGrid/2)-approximation) +Utils.random.nextInt(dirApproximation);
				tempy = this.getPos_y() - ((Utils.numGrid/2)-approximation) +Utils.random.nextInt(dirApproximation);
			
			}while((tempx < 0 || tempx > Utils.numGrid - 1) || (tempy < 0 ||tempy > Utils.numGrid - 1));	
			approximation--;
			dirApproximation+=2;
		}while(this.getAmbiente().path[tempx][tempy]);
		// ADICIONANDO INDIVIDUO A LISTA:
		son.setPos_x(tempx);
		son.setPos_y(tempy);

		// PASSAR OS DADOS DO FILHO GENERICO, PRA A ESPECIE FILHO CORRETA
		if (mother.getSpecie_name() == "Coquithus Cochais") {
			Coquito coquito = new Coquito(son.getSpecie_name(), mother.getEating_pratices(), "", 0, 0, son.getWidth(),
					son.getWidth(), son.getAmbiente());
			coquito.setDna(son.getDna());
			coquito.setColor(son.getColor());
			coquito.setWidth(son.getWidth());
			coquito.setSpeed(son.getSpeed());
			coquito.setMove_rate(son.getMove_rate());
			coquito.setVision_range(son.getVision_range());
			coquito.setPos_x(son.getPos_x());
			coquito.setPos_y(son.getPos_y());
			this.getAmbiente().getAnimals().add(coquito);
		}

	}

	private Color getColorList(int cor) {
		if (cor < 0) {
			// INDIVIDUOS ROSAS SÓ PODEM SER RESULTADO DE ALGUMA MUTAÇÃO GENETICA
			return Color.PINK;
		} else if (cor == 0) {
			return Color.red;
		} else if (cor == 1) {
			return Color.blue;
		} else if (cor == 2) {
			return Color.green;
		} else if (cor == 3) {
			return Color.yellow;
		} else if (cor == 4) {
			return Color.orange;
		} else if (cor == 5) {
			return Color.black;
		} else {
			// INDIVIDUOS BRANCOS SÓ PODEM SER RESULTADO DE ALGUMA MUTAÇÃO GENETICA
			return Color.white;
		}
	}

	public void death() {
		this.getAmbiente().path[getPos_x()][getPos_y()] = false;
	}

	public boolean rangeColiddingFood(SpecieAnimalia e1, SpeciePlantae e2) {
		Rectangle e1Mask = new Rectangle((e1.getPos_x() - e1.getVision_range() / 2) * (Window.WIDTH / Utils.numGrid),
				(e1.getPos_y() - e1.getVision_range() / 2) * (Window.HEIGHT / Utils.numGrid),
				e1.getVision_range() * Window.WIDTH / Utils.numGrid,
				e1.getVision_range() * Window.HEIGHT / Utils.numGrid);
		Rectangle e2Mask = new Rectangle((int) e2.getPos_x() * (Window.WIDTH / Utils.numGrid),
				(int) e2.getPos_y() * (Window.HEIGHT / Utils.numGrid), (int) e2.getWidth(), (int) e2.getHeight());

		return e1Mask.intersects(e2Mask);
	}
	public boolean rangeColiddingAnimal(SpecieAnimalia e1, SpecieAnimalia e2) {
		Rectangle e1Mask = new Rectangle((e1.getPos_x() - e1.getVision_range() / 2) * (Window.WIDTH / Utils.numGrid),
				(e1.getPos_y() - e1.getVision_range() / 2) * (Window.HEIGHT / Utils.numGrid),
				e1.getVision_range() * Window.WIDTH / Utils.numGrid,
				e1.getVision_range() * Window.HEIGHT / Utils.numGrid);
		Rectangle e2Mask = new Rectangle((int) e2.getPos_x() * (Window.WIDTH / Utils.numGrid),
				(int) e2.getPos_y() * (Window.HEIGHT / Utils.numGrid), (int) e2.getWidth(), (int) e2.getHeight());

		return e1Mask.intersects(e2Mask);
	}

	public static boolean isColiddingFood(SpecieAnimalia e1, SpeciePlantae e2) {
		Rectangle e1Mask = new Rectangle(e1.getPos_x(), e1.getPos_y(), 1, 1);
		Rectangle e2Mask = new Rectangle(e2.getPos_x(), e2.getPos_y(), 1, 1);

		return e1Mask.intersects(e2Mask);
	}

	public static void toFeed(SpecieAnimalia sa, List<?> list, int index) {
		list.remove(list.get(index));
		sa.ponto++;
		sa.setHungry(false);
	}

	public void render(Graphics g) {

	}

	public void renderSmall(Graphics g) {

	}

	public void tick() {

	}

	public String getSpecie_name() {
		return specie_name;
	}

	public void setSpecie_name(String specie_name) {
		this.specie_name = specie_name;
	}

	public EatingPractices getEating_pratices() {
		return eating_pratices;
	}

	public void setEating_pratices(EatingPractices eating_pratices) {
		this.eating_pratices = eating_pratices;
	}

	public String getDna() {
		return dna;
	}

	public void setDna(String dna) {
		this.dna = dna;
	}

	public int getPos_x() {
		return pos_x;
	}

	public void setPos_x(int pos_x) {
		this.pos_x = pos_x;
	}

	public int getPos_y() {
		return pos_y;
	}

	public void setPos_y(int pos_y) {
		this.pos_y = pos_y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public boolean isHungry() {
		return hungry;
	}

	public void setHungry(boolean hungry) {
		this.hungry = hungry;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getVision_range() {
		return vision_range;
	}

	public void setVision_range(int vision_range) {
		this.vision_range = vision_range;
	}

	public int getMove_rate() {
		return move_rate;
	}

	public void setMove_rate(int move_rate) {
		this.move_rate = move_rate;
	}

	public Ambiente getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(Ambiente ambiente) {
		this.ambiente = ambiente;
	}

	@Override
	public String toString() {
		return "Entity [specie_name=" + specie_name + ", dna=" + dna + ", speed=" + speed + ", gender=" + gender
				+ ", hungry=" + hungry + ", color=" + color + ", vision_range=" + vision_range + ", move_rate="
				+ move_rate + "]";
	}

}
