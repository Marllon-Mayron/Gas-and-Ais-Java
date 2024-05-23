package simulator.entities.animalia;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import simulator.ambiente.Ambiente;
import simulator.entities.SpecieAnimalia;
import simulator.enums.EatingPractices;
import simulator.main.Window;
import simulator.utils.Node;
import simulator.utils.Utils;

public class Coquito extends SpecieAnimalia {

	public Coquito(String specie_name, EatingPractices eating_pratices, String dna, int pos_x, int pos_y, double width,
			double heigt, Ambiente jungle) {
		super(specie_name, eating_pratices, dna, pos_x, pos_y, width, heigt, jungle);
		// TODO Auto-generated constructor stub
	}

	// TEMPO DE CADA PASSO
	private int moveTime = 60 / this.getMove_rate();
	public int frame;
	
	// VARIAVEL QUE CONTROLA SE ELE SE MOVERA ALEATORIAMENTE OU EM DIREÇÃO A UM
	// PONTO
	private boolean moveTarget = false;
	// CAMINHO MAIS CURTO A SER SEGUIDO
	private List<Node> path = new ArrayList<>();

	public void tick() {

		frame++;
		if (frame == moveTime) {
			//VERIFICAR COMIDAS
			if(this.isHungry()) {
				for (int i = 0; i < this.getAmbiente().getPlants().size(); i++) {
					// DETECTAR SE VIU ALGUMA COMIDA CASO ESTEJA COM FOME
					if (rangeColiddingFood(this, this.getAmbiente().getPlants().get(i)) && isHungry()) {
						// VERIFICA SE JA CHEGOU NO ALIMENTO
						if (isColiddingFood(this, this.getAmbiente().getPlants().get(i))) {
							// SE ALIMENTANDO
							toFeed(this, this.getAmbiente().getPlants(), i);
							break;
						}
						// MONTANDO O CAMINHO MAIS PRÁTICO
						Node targetNode = new Node(this.getAmbiente().getPlants().get(i).getPos_x(),
								this.getAmbiente().getPlants().get(i).getPos_y(), null, 0, 0);
						findPath(targetNode);
						moveTarget = true;
					}

				}
			}else if(this.getGender().equals("Male")){
				//VERIFICAR PARCEIRAS
				int minDistance = (Utils.numGrid * Utils.numGrid) + 1;
				SpecieAnimalia motherTarget = null;
				
				int motherRangeX = 0;
				int motherRangeY = 0;
				
				for (int i = 0; i < this.getAmbiente().getAnimals().size(); i++) {
					if(this.getAmbiente().getAnimals().get(i).getGender().equals("Female")) {
						if(this.rangeColiddingAnimal(this, this.getAmbiente().getAnimals().get(i))) {
							
							//GARANTIR QUE NÃO QUEIRA IR PRA UM PONTO BLOQUEADO
							do {
								//GARANTIR QUE NAO FIQUE FORA DO MAPA
								do {
									motherRangeX = this.getAmbiente().getAnimals().get(i).getPos_x() - (this.getAmbiente().getAnimals().get(i).getVision_range()/2) + Utils.random.nextInt(this.getAmbiente().getAnimals().get(i).getVision_range());
									motherRangeY = this.getAmbiente().getAnimals().get(i).getPos_y() - (this.getAmbiente().getAnimals().get(i).getVision_range()/2) + Utils.random.nextInt(this.getAmbiente().getAnimals().get(i).getVision_range());
								
								}while((motherRangeX < 0 || motherRangeX > Utils.numGrid - 1) || (motherRangeY < 0 || motherRangeY > Utils.numGrid - 1));								
							}while(this.getAmbiente().path[motherRangeX][motherRangeY]);
							
							int distance = Utils.heuristic(this.getPos_x(), this.getPos_x(), motherRangeX, motherRangeY);
							if(minDistance > distance) {
								motherTarget = this.getAmbiente().getAnimals().get(i);
							}
						}
					}
				}
				
				if(!(motherTarget == null)) {
					Node targetNode = new Node(motherRangeX,
							motherRangeY, null, 0, 0);
					findPath(targetNode);
					moveTarget = true;
				}
			}
			
			
			// MOVIMENTAR O OBJETO
			if (moveTarget && !path.isEmpty()) {
				movimentation();
			}
			else {
				moveRandomly();
			}

			frame = 0;
		}

	}

	// ESTE MÉTODO IRÁ CRIAR 2 LISTAS, UMA FECHADA (PARA CAMINHOS JÁ MAPEADOS), E
	// UMA ABERTA (PARA OS CAMINHOS QUE ESTÃO SENDO ANALIZADOS), NO FINAL, OS NÓS
	// COM MENOS CUSTO SERÃO PRIORIZADOS
	public void findPath(Node target) {
		PriorityQueue<Node> openList = new PriorityQueue<>();
		Set<Node> closedList = new HashSet<>();

		Node startNode = new Node(this.getPos_x(), this.getPos_y(), null, 0,
				Utils.heuristic(this.getPos_x(), this.getPos_y(), target.x, target.y));
		openList.add(startNode);

		while (!openList.isEmpty()) {
			Node currentNode = openList.poll();
			closedList.add(currentNode);
			// QUANDO MEUS CALCULOS ACABAREM, SERÁ CRIADO O CAMINHO A SER SEGUIDO
			if (currentNode.x == target.x && currentNode.y == target.y) {
				reconstructPath(currentNode);

				return;
			}
			// GETNEIGBOTS TRAS TODOS OS VIZINHOS QUE EU POSSO ME MOVIMENTAR 4 POSSIVEIS
			// DIREÇÕES;
			for (Node neighbor : getNeighbors(currentNode)) {
				// IGNORA CAMINHOS QUE JA FORAM MAPEADOS, OU ESTÃO BLOQUEADOS
				if (closedList.contains(neighbor) || this.getAmbiente().path[neighbor.x][neighbor.y]) {
					continue;
				}
				// CONTAR O CUSTO DE CADA PASSO, E MAPEAR NO NÓ
				int tentativeGCost = currentNode.gCost + 1;
				if (tentativeGCost < neighbor.gCost || !openList.contains(neighbor)) {
					neighbor.gCost = tentativeGCost;
					neighbor.hCost = Utils.heuristic(neighbor.x, neighbor.y, target.x, target.y);
					neighbor.parent = currentNode;

					if (!openList.contains(neighbor)) {
						openList.add(neighbor);
					}
				}
			}
		}

		// VERIFICAR SE O CAMINHO AINDA EXISTE:
		Utils.showMessages("Não foi possivel encontrar o caminho!", false);
	}

	// CRIAR CAMINHO
	private void reconstructPath(Node targetNode) {
		if (path.isEmpty()) {
			Node current = targetNode;
			while (current != null) {
				path.add(current);
				current = current.parent;
			}

			Collections.reverse(path); // Reverse the path to start from the current position
			// REMOVER O PRIMEIRO REGISTRO, POIS O MESMO SIGNIFICA A POSIÇÃO ATUAL
			path.remove(0);
		} else {
			// CASO ENCONTRE OUTRA COMIDA, VERIFICAR QUEM TEM O PATH MAIS CURTO
			List<Node> tempPath = new ArrayList<>();
			Node current = targetNode;
			while (current != null) {
				tempPath.add(current);
				current = current.parent;
			}

			Collections.reverse(tempPath);
			tempPath.remove(0);

			// VERIFICAR QUAL OPÇÃO TEM O CAMINHO MENOR, PARA SER SEGUIDA
			if (tempPath.size() < path.size()) {
				path.clear();
				path = tempPath;
			}
		}
	}

	// PEGAR TODOS OS CAMINHOS QUE PODEM SER SEGUIDOS, LEVANDO EM CONSIDERAÇÃO O
	// TAMANHO DO MAPA.
	private Set<Node> getNeighbors(Node node) {
		Set<Node> neighbors = new HashSet<>();
		if (node.x - 1 >= 0) {
			neighbors.add(new Node(node.x - 1, node.y, node, node.gCost + 1, 0));
		}
		if (node.x + 1 < Utils.numGrid) {
			neighbors.add(new Node(node.x + 1, node.y, node, node.gCost + 1, 0));
		}
		if (node.y - 1 >= 0) {
			neighbors.add(new Node(node.x, node.y - 1, node, node.gCost + 1, 0));
		}
		if (node.y + 1 < Utils.numGrid) {
			neighbors.add(new Node(node.x, node.y + 1, node, node.gCost + 1, 0));
		}
		return neighbors;
	}

	// SE MOVIMENTAR NO CAMINHO DEFINIDO (PATH)
	public void movimentation() {
		if (!path.isEmpty()) {
			Node nextNode = path.remove(0);
			this.getAmbiente().path[this.getPos_x()][this.getPos_y()] = false;
			this.setPos_x(nextNode.x);
			this.setPos_y(nextNode.y);
			this.getAmbiente().path[this.getPos_x()][this.getPos_y()] = true;
		}
		if (path.isEmpty()) {
			moveTarget = false;
			path.clear();
		}
	}

	// MOVIMENTAÇÃO ALEATORIA
	public void moveRandomly() {
		int dir = Utils.random.nextInt(4);
		if (dir == 0) {
			if (!((this.getPos_x() - 1) < 0)) {
				if (!this.getAmbiente().path[this.getPos_x() - 1][this.getPos_y()]) {
					this.getAmbiente().path[this.getPos_x()][this.getPos_y()] = false;
					this.setPos_x(this.getPos_x() - 1);
					this.getAmbiente().path[this.getPos_x()][this.getPos_y()] = true;
				}
			}
		}
		if (dir == 1) {
			if (!((this.getPos_x() + 1) > (Utils.numGrid - 1))) {
				if (!this.getAmbiente().path[this.getPos_x() + 1][this.getPos_y()]) {
					this.getAmbiente().path[this.getPos_x()][this.getPos_y()] = false;
					this.setPos_x(this.getPos_x() + 1);
					this.getAmbiente().path[this.getPos_x()][this.getPos_y()] = true;
				}
			}
		}
		if (dir == 2) {
			if (!((this.getPos_y() - 1) < 0)) {
				if (!this.getAmbiente().path[this.getPos_x()][this.getPos_y() - 1]) {
					this.getAmbiente().path[this.getPos_x()][this.getPos_y()] = false;
					this.setPos_y(this.getPos_y() - 1);
					this.getAmbiente().path[this.getPos_x()][this.getPos_y()] = true;
				}
			}
		}
		if (dir == 3) {
			if (!((this.getPos_y() + 1) > (Utils.numGrid - 1))) {
				if (!this.getAmbiente().path[this.getPos_x()][this.getPos_y() + 1]) {
					this.getAmbiente().path[this.getPos_x()][this.getPos_y()] = false;
					this.setPos_y(this.getPos_y() + 1);
					this.getAmbiente().path[this.getPos_x()][this.getPos_y()] = true;
				}
			}
		}
	}

	// PARTE GRÁFICA
	public void render(Graphics g) {
		g.setColor(Color.MAGENTA);
		for (int i = 0; i < this.getVision_range(); i++) {
			for (int j = 0; j < this.getVision_range(); j++) {
				// g.fillRect(((this.getPos_x() - (this.getVision_range()/2)) * (Window.WIDTH /
				// Utils.numGrid)) + ((Window.WIDTH / Utils.numGrid) * i) , ((this.getPos_y() -
				// (this.getVision_range()/2)) * (Window.HEIGHT / Utils.numGrid)) +
				// ((Window.HEIGHT / Utils.numGrid) * j),Window.WIDTH / (Utils.numGrid+1),
				// Window.HEIGHT / (Utils.numGrid+1));
			}
		}
		g.setColor(getColor());
		if (Utils.showRangeVision) {
			g.drawRect((getPos_x() - this.getVision_range() / 2) * (Window.WIDTH / Utils.numGrid),
					(getPos_y() - this.getVision_range() / 2) * (Window.HEIGHT / Utils.numGrid),
					this.getVision_range() * Window.WIDTH / Utils.numGrid,
					this.getVision_range() * Window.HEIGHT / Utils.numGrid);
		}
		g.fillRect((getPos_x() * Window.WIDTH / Utils.numGrid), (getPos_y() * Window.HEIGHT / Utils.numGrid),
				(int) getWidth(), (int) getHeight());
	}
	public void renderSmall(Graphics g) {
		if(this.getGender().equals("Male")) {
			g.setColor(Color.white);
		}else{
			g.setColor(Color.red);
		}		
		g.drawString(this.ponto+ "", getPos_x() * (Window.WIDTH / Utils.numGrid) * Window.SCALE, ((getPos_y() * Window.HEIGHT / Utils.numGrid)) * Window.SCALE);
	}

}
