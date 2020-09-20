package demos;

import processing.core.PApplet;
import processing.event.MouseEvent;

public class MyPlayPApplet extends PApplet {
	
	private float boxTopLeftX, boxTopLeftY, boxBottomRightX, boxBottomRightY;
	private boolean boxSelected = false;
	private char key_recorded;
	private int mouseClickNum;
	
	public void setup() {
		size(400, 400);
		background(200, 200, 200);
	} 

//	public void draw() {
//		background(204);
//		if (mousePressed == true) {
//			fill(255); // White
//		} else {
//			fill(0); // Black
//		}
//		rectMode(CENTER); 
//		rect(200, 200, 200, 200);
//	}
	
//	public void draw() { 
//		  if (mouseButton == LEFT) {
//		    fill(0); // Black
//		  } else if (mouseButton == RIGHT) {
//		    fill(255); // White
//		  } else { 
//		    fill(126); // Gray
//		  }
//		  
//		  rectMode(CENTER); 
//		  rect(200, 200, 200, 200);
//		}
	
//	public void draw() {
//		  if (mousePressed == true) {
//		    if (mouseButton == LEFT) {
//		      fill(0); // Black
//		    } else if (mouseButton == RIGHT) { 
//		      fill(255); // White
//		    }
//		  } else {
//		    fill(126); // Gray
//		  }
//		  rectMode(CENTER); 
//		  rect(200, 200, 200, 200);
//	}
	public void draw() {
		
		background(204);
		fill(255, 255, 0);
		ellipse(200, 200, 390, 390);
		fill(0, 0, 0);
		ellipse(120, 130, 50, 70);
		ellipse(280, 130, 50, 70);
		// If the 'A' key is pressed draw a line
//		if ((keyPressed == true) && (key == 's')) {
//			rectMode(CENTER);
//			rect(200, 200, 200, 200);
//		}
		if (boxSelected) {
			line(boxTopLeftX, boxTopLeftY, boxBottomRightX, boxTopLeftY);
			line(boxBottomRightX, boxTopLeftY, boxBottomRightX, boxBottomRightY);
			line(boxBottomRightX, boxBottomRightY, boxTopLeftX, boxBottomRightY);
			line(boxTopLeftX, boxBottomRightY, boxTopLeftX, boxTopLeftY);
		}
	}
	
	public void mousePressed() {
		fill(255, 0, 0);
		rect(mouseX, mouseY, 50, 50);
	}
	
	public void mouseClicked() {
		if (key_recorded == 's') { // enter selection mode
			if (mouseClickNum < 2) { // check if we have two selected point
				if (mouseClickNum == 0) { // collect the first point
					mouseClickNum = 1;
					boxTopLeftX = mouseX;
					boxTopLeftY = mouseY;
					System.out.println("the first point is selected");
				}
				else if (mouseClickNum == 1) { // collect the second point
					mouseClickNum = 2;
					boxBottomRightX = mouseX;
					boxBottomRightY = mouseY;
					boxSelected = true;
					System.out.println("the second point is selected");
				}

			}	
		}
	}
	
	public void keyPressed() {
		System.out.println(key);
		key_recorded = key;
		if (key_recorded != 's') {
			mouseClickNum = 0;
			boxSelected = false;
		}
		
	}
	
//	public void keyPressed(MouseEvent event) {
//		
//		System.out.println(key);
//		
////		if (key == 's') {
////			// make sure the key is pressed
////			if (keyPressed == true) {
////				System.out.println("key is being pressed");
////				// need to have two mouse clicks to determine 
////				// the selected box
////				int mousePressNum = 0;
////				
////				while (mousePressNum < 2) {
////					System.out.println("Please click the mouse...");
////					// if press the button
////					if (mousePressed == true && mousePressNum == 0) {
////						mousePressNum = 1;
////						boxTopLeftX = mouseX;
////						boxTopLeftY = mouseY;
////						System.out.println("the first point is selected");
////					}
////					// first click
////					if (mousePressed == true && mousePressNum == 1) {
////						mousePressNum = 2;
////						boxBottomRightX = mouseX;
////						boxBottomRightY = mouseY;
////						boxSelected = true;
////						System.out.println("the second point is selected");
////					}
////				}
////				
////				// box being selected
////			}
////			
////		}
////		else if (key == 'c') {
////			boxSelected = false;
////		}
////		System.out.println(boxSelected);
//
//		//
//		if (key == 's') {
//			// make sure the key is pressed
//			if (keyPressed == true) {
//				System.out.println("key is being pressed");
//				// need to have two mouse clicks to determine 
//				// the selected box
//				int mousePressNum = 0;
//				
//				while (mousePressNum < 2) {
//					//System.out.println("Please click the mouse...");
//					// if press the button
//					if (event.getCount() == 1 && mousePressNum == 0) {
//						mousePressNum = 1;
//						boxTopLeftX = mouseX;
//						boxTopLeftY = mouseY;
//						System.out.println("the first point is selected");
//					}
//					// first click
//					if (event.getCount() == 2  && mousePressNum == 1) {
//						mousePressNum = 2;
//						boxBottomRightX = mouseX;
//						boxBottomRightY = mouseY;
//						boxSelected = true;
//						System.out.println("the second point is selected");
//					}
//				}
//				
//				// box being selected
//			}
//			
//		}
//		else if (key == 'c') {
//			boxSelected = false;
//		}
//		System.out.println(boxSelected);		
//		
//	}
	
	
}
