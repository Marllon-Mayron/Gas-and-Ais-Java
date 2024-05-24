package simulator.graphics;

import java.awt.Color;
import java.awt.Graphics;

import simulator.main.Window;

public class Charts {
	
	public Charts() {
		
	}
	
	public void tick() {
		
	}
	public void render(Graphics g) {
		g.setColor(Color.black);
        int totalWidth = Window.frame.getWidth();
        int totalHeight = Window.frame.getHeight();

        int xPosition = (int) (totalWidth * 0.90); 
        int yPosition = (int) (totalHeight * 0.10); 

        g.drawString("Gráficos da simulação", xPosition, yPosition);
	}

}
