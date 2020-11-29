import java.applet.*;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

public class PaintApp extends Applet{
	//FINAL STATIC INTS FOR REFERENCE
	private static final int RECT = 0;
	private static final int OVAL = 1;
	private static final int LINE = 2;
	private static final int FREEHAND = 3;
	private static final int ERASER = 4;
	//Off Screen Buffering
	int windWidth;		int windHeight;		Image offscreen;	Graphics2D doubleBuffer;
	//Temporary drawing parameters
	int x1;				int y1;				int x2;				int y2;	
	int shape = 3;		int stroke = 5; 	Color color = Color.black;
	BasicStroke myStroke = new BasicStroke(stroke);
	//Helper variables
	boolean clicked;	boolean drag;		int counter;		Vector<Integer> iterator = new Vector(1, 1);			
	//Color Buttons
	Button red;			Button yellow;		Button blue;		Button black;
	//Shape Buttons
	Button rectangle;	Button oval;		Button line;		Button freehand;
	//Drawing Options Buttons
	Checkbox dotted;	Checkbox filled;	Button incSt;		Button decSt;
	//Other Buttons
	Button eraser;		Button clearAll;	Button undo;
	//Vectors for Shapes
	Vector<Shapes> myShapes = new Vector(0, 1);
	Vector<Integer> undoQ = new Vector(1, 1);
	
	
	//APPLET OVERRIDDEN METHODS
	public void init(){
		setDoubleBuffer();
		setColorButtons();
		setShapeButtons();
		setOtherButtons();
		setDrawingOps();
		setMouseListeners();
	}
		
	public void update(Graphics g){
		paint(g);
    }
	
	public void paint(Graphics g){
		
		if ((getWidth() != windWidth) || (getHeight() != windHeight)){
			offscreen = createImage(getWidth(), getHeight());
			doubleBuffer = (Graphics2D) offscreen.getGraphics();
			windWidth = getWidth();
			windHeight = getHeight();
		}
		
		doubleBuffer.clearRect(0,0,getWidth(), getHeight());
		
		drawPrevious();
		
		drawTemporary();
		
		g.drawImage(offscreen, 0, 0, this);
	}
	
	//METHODS CALLED DURING INITALIZATION
	private void setDoubleBuffer(){
		windWidth = getWidth();
		windHeight = getHeight();
		offscreen = createImage(windWidth, windHeight);
		doubleBuffer = (Graphics2D) offscreen.getGraphics();
	}
	
	private void setColorButtons(){
		red = new Button("Red");
		yellow = new Button("Yellow");
		blue = new Button("Blue");
		black = new Button("Black");
		red.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ev){
										color = Color.red;
									}
								});
		yellow.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ev){
										color = Color.yellow;
									}
								});
		blue.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ev){
										color = Color.blue;
									}
								});
		black.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ev){
										color = Color.black;
									}
								});
		add(red);
		add(yellow);
		add(blue);
		add(black);
	}
	
	private void setShapeButtons(){
		rectangle = new Button("Rectangle");
		oval = new Button("Oval");
		line = new Button("Line");
		freehand = new Button("Free Hand");
		rectangle.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ev){
										shape = PaintApp.RECT;
									}
								});
		oval.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ev){
										shape = PaintApp.OVAL;
									}
								});
		line.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ev){
										shape = PaintApp.LINE;
									}
								});
		freehand.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ev){
										shape = PaintApp.FREEHAND;
									}
								});
		add(rectangle);
		add(oval);
		add(line);
		add(freehand);
	}
	
	private void setOtherButtons(){
		eraser = new Button("Eraser");
		clearAll = new Button("Clear All");
		undo = new Button("Undo");
		eraser.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ev){
										shape = PaintApp.ERASER;
									}
								});
		clearAll.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ev){
										myShapes.removeAllElements();
										undoQ.removeAllElements();
										x1 = x2 = y1 = y2 = 0;
										repaint();
									}
								});
		undo.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ev){
										if (!undoQ.isEmpty()){
											switch (undoQ.lastElement()){
											case 3:
											for (int i = 0; i < iterator.lastElement(); i++)
												myShapes.remove(myShapes.size()-1);
											iterator.remove(iterator.size()-1);
											break;
											case 4:
											for (int i = 0; i < iterator.lastElement(); i++)
												myShapes.remove(myShapes.size()-1);
											iterator.remove(iterator.size()-1);
											break;
											default:
											myShapes.remove(myShapes.size() - 1);
										}
										undoQ.remove(undoQ.size()-1);
										x1 = y1 = x2 = y2 = 0;
										repaint();
										}
									}
								});
		add(eraser);
		add(clearAll);
		add(undo);
	}
	
	private void setDrawingOps(){
		incSt = new Button("Increase Stroke");
		decSt = new Button ("Decrese Stroke");
		dotted = new Checkbox("dotted");
		filled = new Checkbox("filled");
		incSt.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ev){
										if (stroke < 15){
											stroke++;
											if (dotted.getState())
												myStroke = new BasicStroke(stroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL , 10, new float[]{15}, 0);
											else
												myStroke = new BasicStroke(stroke);
										}
									}
								});
		decSt.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ev){
										if (stroke > 1){
											stroke--;
											if (dotted.getState())
												myStroke = new BasicStroke(stroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL , 10, new float[]{15}, 0);
											else
												myStroke = new BasicStroke(stroke);
										}
									}
								});
		dotted.addItemListener(new ItemListener(){
									public void itemStateChanged(ItemEvent e){
										if (dotted.getState())
											myStroke = new BasicStroke(stroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL , 10, new float[]{10}, 1);
										else
											myStroke = new BasicStroke(stroke);
									}
		});
		add(incSt);
		add(decSt);
		add(dotted);
		add(filled);
	}
	
	private void setMouseListeners(){
		addMouseListener(new MouseAdapter(){
								public void mouseReleased(MouseEvent e){
									if (drag){
										x2 = e.getX();
										y2 = e.getY();
										switch (shape){
											case PaintApp.RECT:
											myShapes.add(new Rectangle(Math.min(x1,x2), Math.min(y1,y2), Math.max(x1,x2),Math.max(y1,y2), color, myStroke, filled.getState()));
											undoQ.add(shape);
											break;
											case PaintApp.OVAL:
											myShapes.add(new Oval(Math.min(x1,x2), Math.min(y1,y2), Math.max(x1,x2),Math.max(y1,y2), color, myStroke, filled.getState()));
											undoQ.add(shape);
											break;
											case PaintApp.LINE:
											myShapes.add(new Line(x1, y1, x2, y2, color, myStroke));
											undoQ.add(shape);
											break;
											case PaintApp.FREEHAND:
											myShapes.add(new Line(x1, y1, x2, y2, color, myStroke));
											counter++;
											undoQ.add(shape);
											iterator.add(counter);
											counter = 0;
											break;
											case PaintApp.ERASER:
											undoQ.add(shape);
											iterator.add(counter);
											counter = 0;
											break;
										}
										clicked = false;
										drag = false;
										repaint();
								}}
								public void mouseClicked(MouseEvent e){
									if (shape == 4){
										x1 = e.getX() - 50;
										y1 = e.getY() - 50;
										x2 = e.getX() + 50;
										y2 = e.getY() + 50;
										myShapes.add(new Rectangle(x1, y1, x2, y2, Color.white, new BasicStroke(), true));
										undoQ.add(shape);
										iterator.add(1);
										repaint();
									}
								}
								});
		addMouseMotionListener(new MouseAdapter(){
									public void mouseDragged(MouseEvent e){
									if (!clicked){
										x1 = e.getX();
										y1 = e.getY();
										x2 = x1 + 1;
										y2 = y1 + 1;
										clicked = true;
										drag = true;
									}else{
										x2 = e.getX();
										y2 = e.getY();
									}
									switch (shape){
										case PaintApp.FREEHAND:
										double length = Math.pow(Math.pow((x2-x1),2) + Math.pow((y2-y1),2),0.5);
										if (length > 10){
											myShapes.add(new Line(x1,y1,x2,y2,color,myStroke));
											counter++;
											x1 = x2;
											y1 = y2;
										}
										break;
										case 4:
										x1 = e.getX() - 50;
										y1 = e.getY() - 50;
										x2 = e.getX() + 50;
										y2 = e.getY() + 50;
										myShapes.add(new Rectangle(x1, y1, x2, y2, Color.white, new BasicStroke(), true));
										counter++;
										break;
									}
									repaint();									
								}});
	}
	
	//METHODS CALLED DURING PAINT
	private void drawPrevious(){
		for (int i = 0; i<myShapes.size(); i++){
			doubleBuffer.setColor(myShapes.get(i).color);
			doubleBuffer.setStroke(myShapes.get(i).myStroke);
			myShapes.get(i).drawShape(doubleBuffer);
		}
	}
	
	private void drawTemporary(){
		doubleBuffer.setColor(color);
		doubleBuffer.setStroke(myStroke);
		switch (shape){
			case PaintApp.RECT:
			if (filled.getState())
				doubleBuffer.fillRect(Math.min(x1,x2), Math.min(y1,y2), Math.abs(x2 - x1),Math.abs(y2 - y1));
			else
				doubleBuffer.drawRect(Math.min(x1,x2), Math.min(y1,y2), Math.abs(x2 - x1),Math.abs(y2 - y1));
			break;
			case PaintApp.OVAL:
			if (filled.getState())
				doubleBuffer.fillOval(Math.min(x1,x2), Math.min(y1,y2), Math.abs(x2 - x1),Math.abs(y2 - y1));
			else
				doubleBuffer.drawOval(Math.min(x1,x2), Math.min(y1,y2), Math.abs(x2 - x1),Math.abs(y2 - y1));
			break;
			case PaintApp.LINE:
			doubleBuffer.drawLine(x1,y1,x2,y2);
			break;
			case PaintApp.FREEHAND:
			doubleBuffer.drawLine(x1,y1,x2,y2);
			break;
			case PaintApp.ERASER:
			doubleBuffer.setColor(Color.white);
			doubleBuffer.fillRect(x1,y1,100,100);
			break;
		}
	}
	
	//INNER CLASSES
	abstract class Shapes{
		int x1;
		int y1;
		int x2;
		int y2;
		Color color = Color.black;
		BasicStroke myStroke;
		public Shapes(){}
		public Shapes(int x1,int y1,int x2,int y2, Color color, BasicStroke myStroke){
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.color = color;
			this.myStroke = myStroke;
		}
		public abstract void drawShape(Graphics2D g);
	}

	class Rectangle extends Shapes{
		int width;
		int height;
		boolean filled;
		public Rectangle(){}
		public Rectangle(int x1,int y1,int x2,int y2, Color color, BasicStroke myStroke, boolean filled){
			super(x1,y1,x2,y2,color,myStroke);
			width = x2 - x1;
			height = y2 - y1;
			this.filled = filled;
		}
		public void drawShape(Graphics2D g){
			if (filled)
				g.fillRect(x1,y1,width,height);
			else
				g.drawRect(x1,y1,width,height);
		}
	}

	class Oval extends Shapes{
		int width;
		int height;
		boolean filled;
		public Oval(){}
		public Oval(int x1,int y1,int x2,int y2, Color color, BasicStroke myStroke, boolean filled){
			super(x1,y1,x2,y2,color,myStroke);
			width = x2 - x1;
			height = y2 - y1;
			this.filled = filled;
		}
		public void drawShape(Graphics2D g){
			if(filled)
				g.fillOval(x1,y1,width,height);
			else
				g.drawOval(x1,y1,width,height);
		}
	}

	class Line extends Shapes{
		public Line(){}
		public Line(int x1,int y1,int x2,int y2, Color color, BasicStroke myStroke){
			super(x1,y1,x2,y2,color,myStroke);
		}
		public void drawShape(Graphics2D g){
			g.drawLine(x1,y1,x2,y2);
		}
	}
}