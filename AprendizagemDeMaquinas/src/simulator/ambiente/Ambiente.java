package simulator.ambiente;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import simulator.entities.SpecieAnimalia;
import simulator.entities.SpeciePlantae;
import simulator.entities.plantae.Brush;
import simulator.main.Window;
import simulator.utils.Utils;


//CLASSE RESPONSAVEL POR GERENCIAR O AMBIENTE ONDE OS INDIVIDUOS ESTÃO SENDO POSICIONADOS
public class Ambiente {
	
	private int days;
	private int populationInitial;
	private int numPlants;
	private int frame = 0;
	public int sec = 0;
	//VARIAVEL QUE CONTROLA QUANTO TEMPO VALE UM DIA
	public int secDay = 5;
	public boolean[][] path = new boolean[Utils.numGrid][Utils.numGrid];
	
	private List<SpecieAnimalia> animals = new ArrayList<>();
	private List<SpeciePlantae> plants = new ArrayList<>();
	
	public Ambiente(int populationInitial, int numPlants) {
		super();
		this.populationInitial = populationInitial;
		this.numPlants = numPlants;
	}
	
	public void tick() {
		//DEFININDO QUANTOS SEGUNDOS VALE UM DIA DE SIMULAÇÃO;
		frame++;
		if(frame == 60) {
			sec++;
			if(sec >= secDay) {
				nextDay();
				sec = 0;
			}
			frame = 0;
		}
		
	}
	//METODO QUE CONTROLA O QUE ACONTECE QUANDO PASSA DE DIA
	public void nextDay() {
		this.setDays(this.getDays()+1);
		for(int i = 0; i < animals.size(); i++) {
			//MATAR OS INDIVIDUOS QUE NÃO SE ALIMENTARAM
			if(animals.get(i).isHungry()) {
				Utils.csvController.csvCreaturePerformance(animals.get(i));
				animals.get(i).death();
				animals.remove(i);
				i--;
			}else {				
				//RESETAR A FOME DO INDIVIDUO
				animals.get(i).setHungry(true);
			}
			
		}
		int currentListSize = animals.size();
		for(int i = 0; i < currentListSize; i++) {
			//LIBERAR TEMPORADA DE REPRODUÇÃO DE ESPECIE
			if(i%2 == 1) {
				animals.get(i).reproduce(animals.get(i-1));
				i++;
			}
		}
		
		//RESETAR COMIDAS
		this.getPlants().clear();
		//this.setNumPlants(this.getNumPlants()/2);
		this.generateFoods();
		
		//REGISTRAR O NOVO DIA:
		Utils.csvController.csvCurrentDay(this, this.getAnimals());
	
	}
	public void generateFoods() {
		for (int i = 0; i < this.getNumPlants(); i++) {
			int tempx = 0;
			int tempy = 0;
			do {
				tempx = Utils.random.nextInt(Utils.numGrid);
				tempy = Utils.random.nextInt(Utils.numGrid);
			} while ( this.path[tempx][tempy]);
			Brush brush = new Brush(tempx, tempy, (Window.WIDTH / (Utils.numGrid + 1)), (Window.HEIGHT / (Utils.numGrid + 1)), 0, 1,
					3);
			this.getPlants().add(brush);
			// jungle.path[tempx][tempy] = true;
		}
	}
	public void generateWalls(int wallNum) {
		int tempY = 0;
		int tempX = 0;
		
		for(int k = 0; k < wallNum; k++) {
			//PAREDE PEQUENA VERTICAL
			tempY = Utils.random.nextInt(Utils.numGrid);
			tempX = Utils.random.nextInt(Utils.numGrid);
			for(int i = 0; i < 4; i++) {
				try{
					this.path[tempX][tempY+i] = true;
				}catch(Exception e) {
					//NÃO EXISTE ESPAÇO PARA ESSE BLOQUEIO
				}
			}
		}
		
		for(int k = 0; k < wallNum; k++) {
			//PAREDE PEQUENA HORIZONTAL
			tempY = Utils.random.nextInt(Utils.numGrid);
			tempX = Utils.random.nextInt(Utils.numGrid);
			for(int i = 0; i < 4; i++) {
				try{
					this.path[tempX+i][tempY] = true;
				}catch(Exception e) {
					//NÃO EXISTE ESPAÇO PARA ESSE BLOQUEIO
				}
			}
		}
		
		
		for(int k = 0; k < wallNum; k++) {
			//BLOCO PEQUENO
			tempY = Utils.random.nextInt(Utils.numGrid);
			tempX = Utils.random.nextInt(Utils.numGrid);
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					try{
						this.path[tempX][tempY] = true;
						this.path[tempX+i][tempY] = true;
						this.path[tempX][tempY+j] = true;
						this.path[tempX+j][tempY+i] = true;
					}catch(Exception e) {
						//NÃO EXISTE ESPAÇO PARA ESSE BLOQUEIO
					}
				}
				
			}
		}

		
		
		for(int k = 0; k < wallNum; k++) {
			tempY = Utils.random.nextInt(Utils.numGrid);
			tempX = Utils.random.nextInt(Utils.numGrid);
			//PAREDE VERTICAL PARALELA
			for(int i = 0; i < 7; i++) {
				try{
					if(!(i == 3)) {
						this.path[tempX+i][tempY] = true;
						this.path[tempX+i][tempY+2] = true;
					}
					
				}catch(Exception e) {
					//NÃO EXISTE ESPAÇO PARA ESSE BLOQUEIO
				}
			}
		}
		
	}
	public void render(Graphics g) {
		g.setColor(Color.DARK_GRAY);
		
	}
	
	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	public int getPopulationInitial() {
		return populationInitial;
	}
	public void setPopulationInitial(int populationInitial) {
		this.populationInitial = populationInitial;
	}
	
	public int getNumPlants() {
		return numPlants;
	}

	public void setNumPlants(int numPlants) {
		this.numPlants = numPlants;
	}

	public List<SpecieAnimalia> getAnimals() {
		return animals;
	}

	public void setAnimals(List<SpecieAnimalia> animals) {
		this.animals = animals;
	}
	
	public List<SpeciePlantae> getPlants() {
		return plants;
	}

	public void setPlants(List<SpeciePlantae> plants) {
		this.plants = plants;
	}

	
	
	

}