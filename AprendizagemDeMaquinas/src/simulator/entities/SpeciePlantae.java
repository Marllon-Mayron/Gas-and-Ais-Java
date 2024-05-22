package simulator.entities;

import java.awt.Graphics;

public class SpeciePlantae {
	
	private int pos_x;
	private int pos_y;
	
	private double width;
	private double height;
	
	private int alimentationRange;
	
	private double minHeight;
	
	private int currentAlimentation;

	
	public SpeciePlantae(int pos_x, int pos_y, double width, double height, double minHeight, int currentAlimentation, int alimentationRange) {
		super();
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.width = width;
		this.height = height;
		this.minHeight = minHeight;
		this.currentAlimentation = currentAlimentation;
		this.alimentationRange = alimentationRange;
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

	public double getMinHeight() {
		return minHeight;
	}

	public void setMinHeigt(double minHeight) {
		this.minHeight = minHeight;
	}

	public int getCurrentAlimentation() {
		return currentAlimentation;
	}

	public void setCurrentAlimentation(int currentAlimentation) {
		this.currentAlimentation = currentAlimentation;
	}
	
	public int getAlimentationRange() {
		return alimentationRange;
	}

	public void setAlimentationRange(int alimentationRange) {
		this.alimentationRange = alimentationRange;
	}

	@Override
	public String toString() {
		return "SpeciePlantae [minHeigt=" + minHeight + ", currentAlimentation=" + currentAlimentation + "]";
	}

	public void tick() {
		
	}

	public void render(Graphics g) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
