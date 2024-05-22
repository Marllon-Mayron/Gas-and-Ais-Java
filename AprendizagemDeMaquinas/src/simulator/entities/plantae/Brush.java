package simulator.entities.plantae;

import java.awt.Color;
import java.awt.Graphics;

import simulator.entities.SpeciePlantae;
import simulator.main.Window;
import simulator.utils.Utils;

public class Brush extends SpeciePlantae{

	public Brush(int pos_x, int pos_y, double width, double height, double minHeight, int currentAlimentation, int alimentationRange) {
		super(pos_x, pos_y, width, height, minHeight, currentAlimentation, alimentationRange);
		// TODO Auto-generated constructor stub
	}
	
	public void tick() {
		
	}
	public void render(Graphics g) {
		
		g.setColor(Color.green);
		g.fillRect((getPos_x() * Window.WIDTH / Utils.numGrid), (getPos_y() * Window.HEIGHT / Utils.numGrid), (int)getWidth()/2,(int)getHeight()/2);
	}

}
