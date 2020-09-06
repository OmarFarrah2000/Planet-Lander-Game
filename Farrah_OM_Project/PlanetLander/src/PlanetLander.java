import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PlanetLander extends Application{
	
	//reference to stage (allows other methods in class to change stage properties)
	private Stage mainStage;
	
	//Fields for start screen
	private Pane startPane;
	private Scene startScene;
	private ImageView titleImage;
	private Button startBtn;
	private Button helpBtn;
	
	//Fields for help screen
	private Pane helpPane;
	private Scene helpScene;
	private ImageView helpImage;
	private ImageView rocketHelpImage;
	private Button helpBackBtn;
	private Button quitBtn;
	
	//Fields for game screen
	private Pane gamePane;
	private Scene gameScene;
	
	private Button gameBackBtn;
	private Label objective;
	private Label levelNum;
	
	private Canvas canvas;
	private GraphicsContext gc;
	
	private Factory factory; //Factory for creation of game objects
	
	private Rocket rocket; //Field for static rocket object
	//Booleans to determine which rocket control keys are being pressed.
	private boolean rocketLeft;
	private boolean rocketRight;
	private boolean rocketUp;
	private boolean rocketDown;
	
	//Creation of keyboard handler fields
	private EventHandler<KeyEvent> keyReleaseHandler;
	private EventHandler<KeyEvent> keyPressedHandler;
	
	//round number and asteroid speed
	private int roundNum;
	private double asteroidSpeed;
	
	private ArrayList<GameObject> objects; //List to store gameobjects
	
	//Hard-coded array of planet names (for planet selection)
	private ArrayList<String> planetNames;
	private Random r = new Random(System.currentTimeMillis());

	private AnimationTimer timer;
	
	//Fields for win screen
	private Pane winPane;
	private Scene winScene;
	private ImageView winImage;
	private Button winRestartBtn;
	
	//Fields for lose screen
	private Pane losePane;
	private Scene loseScene;
	private ImageView loseImage;
	private Label loseReason;
	private Button loseRestartBtn;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		
		mainStage = stage;
		
		//Defining start screen components (images/buttons
		startPane = new Pane();
		startScene = new Scene(startPane, 1500,500);
		
		titleImage = new ImageView(new Image("resources/title.png"));
		titleImage.relocate(450, 50);
		
		startBtn = new Button(); //Start button
		startBtn.setLayoutX(690);
		startBtn.setLayoutY(170);
		startBtn.setGraphic(new ImageView(new Image("resources/start.png"))); //Adding image to button
		startBtn.setBackground(null);
		startBtn.setOnAction(e -> { //If clicked reset game and start new round
			reset();
			newRound(); 
			mainStage.setScene(gameScene); //switch to game scene
		});
		
		helpBtn = new Button(); //Help screen button
		helpBtn.setLayoutX(690);
		helpBtn.setLayoutY(220);
		helpBtn.setGraphic(new ImageView(new Image("resources/help.png")));
		helpBtn.setBackground(null);
		helpBtn.setOnAction(e -> mainStage.setScene(helpScene)); //switch to help screen
		
		quitBtn = new Button(); //Quit button
		quitBtn.setLayoutX(690);
		quitBtn.setLayoutY(270);
		quitBtn.setGraphic(new ImageView(new Image("resources/quit.png")));
		quitBtn.setBackground(null);
		quitBtn.setOnAction(e -> Platform.exit()); //close application
		//Add elements to pane
		startPane.getChildren().addAll(new Rectangle(1500,500, Color.BLACK), titleImage, startBtn, helpBtn, quitBtn);

		//Defining help pane components
		helpPane = new Pane();
		helpScene = new Scene(helpPane, 1500,500);
		
		helpImage = new ImageView(new Image("resources/planethelp.png")); //display planet help image
		helpImage.relocate(0,50);
		
		rocketHelpImage = new ImageView(new Image("resources/rockethelp.png")); //display rocket help image
		rocketHelpImage.relocate(1060,200);
		
		helpBackBtn = new Button(); //back button
		helpBackBtn.setLayoutX(0);
		helpBackBtn.setLayoutY(0);
		helpBackBtn.setGraphic(new ImageView(new Image("resources/back.png")));
		helpBackBtn.setBackground(null);
		helpBackBtn.setOnAction(e -> mainStage.setScene(startScene)); //switch to start screen
		//Add elements to help pane
		helpPane.getChildren().addAll(new Rectangle(1500,500, Color.BLACK), helpImage, helpBackBtn, rocketHelpImage);
		
		//Fields for main game screen
		gamePane = new Pane();
		gameScene = new Scene(gamePane, 1500, 500);
		
		gameBackBtn = new Button(); //Game back button (leaves current game)
		gameBackBtn.setLayoutX(0);
		gameBackBtn.setLayoutY(0);
		gameBackBtn.setGraphic(new ImageView(new Image("resources/back.png")));
		gameBackBtn.setBackground(null);
		gameBackBtn.setOnAction(e -> { //stop animation timer, clear all game objects
			timer.stop();
			objects.clear();
			mainStage.setScene(startScene); //switch to start screen
		});

		objective = new Label(); //Display objective
		objective.setLayoutX(500);
		objective.setLayoutY(10);
		objective.setFont(new Font("Arial", 30));
		objective.setTextFill(Color.WHITE);
		
		levelNum = new Label(); //Display level number
		levelNum.setLayoutX(1300);
		levelNum.setLayoutY(10);
		levelNum.setFont(new Font("Arial", 30));
		levelNum.setTextFill(Color.WHITE);
		
		//Create a canvas the same size as screen
		canvas = new Canvas(1500, 500);
		gc = canvas.getGraphicsContext2D();
		
		//Draw a black background to screen
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		//Add elements to game screen
		gamePane.getChildren().addAll(canvas, gameBackBtn, objective, levelNum); //Adds canvas to pane
		
		
		/*
		 * Rocket Movement Input Detection
		 * 
		 * Updating the state of movement fields allows for the rocket to travel
		 * in different directions and reduces the amount of Rocket.update() calls
		 */
		
		//Instantiate keyPressed handler with private method that overrides 'handle'
		keyPressedHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent key) {
				//If direction key pressed update appropriate field to true 
				if(key.getCode() == KeyCode.D)
					rocketRight = true;
				if(key.getCode() == KeyCode.A)
					rocketLeft = true;
				if(key.getCode() == KeyCode.W)
					rocketUp = true;
				if(key.getCode() == KeyCode.S)
					rocketDown = true;
			}
		};
		
		//Instantiate keyRelease handler with private method that overrides 'handle'
		keyReleaseHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent key) {
				//If direction key released update appropriate field to false 
				if(key.getCode() == KeyCode.D)
					rocketRight = false;
				if(key.getCode() == KeyCode.A)
					rocketLeft = false;
				if(key.getCode() == KeyCode.W)
					rocketUp = false;
				if(key.getCode() == KeyCode.S)
					rocketDown = false;
			}
		};
		
		//Add key handlers to relevant scene
		gameScene.setOnKeyPressed(keyPressedHandler);
		gameScene.setOnKeyReleased(keyReleaseHandler);
		
		objects = new ArrayList<GameObject>(); //Defines blank array of gameobjects
		
		//Fields for win screen
		winPane = new Pane();
		winScene = new Scene(winPane, 1500,500);
		
		winImage = new ImageView(new Image("resources/win.png")); //win image
		winImage.relocate(450, 50);
		
		winRestartBtn = new Button(); //restart button
		winRestartBtn.setLayoutX(556.5);
		winRestartBtn.setLayoutY(390);
		winRestartBtn.setGraphic(new ImageView(new Image("resources/restart.png")));
		winRestartBtn.setBackground(null);
		winRestartBtn.setOnAction(e -> mainStage.setScene(startScene)); //switch to start screen
		//Add elements to win pane
		winPane.getChildren().addAll(new Rectangle(1500,500, Color.BLACK), winImage, winRestartBtn);
		
		//Fields for lose screen
		losePane = new Pane();
		loseScene = new Scene(losePane, 1500,500);
		
		loseImage = new ImageView(new Image("resources/lose.png")); //Lose image
		loseImage.relocate(464.5, 50);
		
		loseReason = new Label(); //Display for lose reason
		loseReason.setLayoutY(180);
		loseReason.setLayoutX(540);
		loseReason.setMaxWidth(500);
		loseReason.setFont(new Font("Arial", 30));
		loseReason.setTextFill(Color.WHITE);
		
		loseRestartBtn = new Button(); //Restart button
		loseRestartBtn.setLayoutX(556.5);
		loseRestartBtn.setLayoutY(300);
		loseRestartBtn.setGraphic(new ImageView(new Image("resources/restart.png")));
		loseRestartBtn.setBackground(null);
		loseRestartBtn.setOnAction(e -> mainStage.setScene(startScene)); //Switch to start screen
		//Add elements to lose Pane
		losePane.getChildren().addAll(new Rectangle(1500,500, Color.BLACK), loseImage, loseReason, loseRestartBtn);
		
		//Add logo, title and first scene to stage
		mainStage.getIcons().add(new Image("resources/rocketup.png"));
		mainStage.setTitle("Planet Lander");
		mainStage.setScene(startScene);
		mainStage.show(); //show stage
	}
	
	private void newRound() { //Starts new round
		if(roundNum != 5) { //Keeps track of rounds
			createObjects(); //Create all game objects
			
			/* Pick random int between 0 and size of planetNames array
			 * Select planet name in the ints position
			 * that is the planet to land on for this round.
			 * Remove from array to avoid it being chosen again
			 */
			int randomInt = r.nextInt(planetNames.size());
			String planet = planetNames.get(randomInt);
			planetNames.remove(randomInt);
			objective.setText("Help the rocket land on "+planet+"!"); //display planet to land on
			
			roundNum++; //Increments round number
			levelNum.setText("Level "+roundNum); //displays round number
			
			asteroidSpeed += 0.3; //increase asteroid speed each level
			for(GameObject o: objects) { //updates all asteroids speed
				if(o instanceof Asteroid) {
					((Asteroid) o).setSpeed(asteroidSpeed);
				}
			}
			rocket.resetPosition(); //reset rocket position
			
			timer = new AnimationTimer() { //Defining animation timer for game
				@Override
				public void handle(long arg0) {
						//update canvas background
						gc.setFill(Color.BLACK);
						gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
						//Update rocket position based on which key is being pressed
						if(rocketLeft)
							rocket.moveLeft();
						if(rocketRight)
							rocket.moveRight();
						if(rocketUp)
							rocket.moveUp();
						if(rocketDown)
							rocket.moveDown();
						//variables for win/lose scenario
						boolean win = false;
						String reason = "";
						rocket.update(); //updates rocket instance
						for(GameObject o: objects) { //updates all instances of GameObjects in object list
							if(o != null) {
								o.update(); //update objects
								//Detect Collisions
								if(o.getIntersectShape().getBoundsInParent().intersects(rocket.getIntersectShape().getBoundsInParent())){
									if(o instanceof Asteroid) { //If collided object is an asteroid, lose
										reason = "You hit an Asteroid!";
									}
									if(o instanceof Planets) { //If collided object is a planet
										if(((Planets) o).getPlanetImage().equals("sun.png")) { //if sun, lose
											reason = "You got too close to the sun!";
										}else if(((Planets) o).getPlanetImage().equals(planet.toLowerCase()+".png")) { //if correct planet, win
											win = true;
										}else { //otherwise, lose
											reason = "You landed on the wrong planet!";
										}
									}
								}
							}
						}
						if(win) { //If win scenario, stop timer, clear objects, initiate next round
							timer.stop();
							objects.clear();
							newRound();
						}
						if(!reason.equals("")) { //if lose scenario, stop timer, clear objects display lose scene
							timer.stop();
							objects.clear();
							loseReason.setText(reason);
							mainStage.setScene(loseScene);
						}
			}};
			timer.start(); //start timer
		}else { //if round reaches 6, user has won display win screen
			mainStage.setScene(winScene);
		}
	}
	
	private void reset() { //Reset game fields
		roundNum=0;
		asteroidSpeed = 1;
		planetNames = new ArrayList<String>(Arrays.asList("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"));
		//reset movement fields
		rocketLeft = false;
		rocketRight = false;
		rocketUp = false;
		rocketDown = false;

	}
	
	private void createObjects() {
		//Creation of Level Objects
		rocket = Rocket.getInstance(gc, 1400, 150, new SquareIntersect());
		
		factory = new ObjectFactory(gc);
		
		//Creation of planets & sun - Adding to object list (with specific x,y and sizes)
		objects.add(factory.createProduct("sun.png", 0, 50, 100, 500, new SquareIntersect()));
		objects.add(factory.createProduct("mercury.png", 150, 270, 40, 40, new CircleIntersect()));
		objects.add(factory.createProduct("venus.png", 240, 265, 50, 50, new CircleIntersect()));
		objects.add(factory.createProduct("earth.png", 340, 262.5, 55, 55, new CircleIntersect()));
		objects.add(factory.createProduct("mars.png", 440, 270, 40, 40, new CircleIntersect()));
		objects.add(factory.createProduct("jupiter.png", 550, 220, 140, 140, new CircleIntersect()));
		objects.add(factory.createProduct("saturn.png", 760, 230, 200, 150, new CircleIntersect()));
		objects.add(factory.createProduct("uranus.png", 1030, 255, 70, 70, new CircleIntersect()));
		objects.add(factory.createProduct("neptune.png", 1170, 255, 70, 70, new CircleIntersect()));
				
		//Creation of asteroids - Adding to object list (with specific x,y)
		objects.add(factory.createProduct("asteroid.png", 200 , 250, 0, 0, new CircleIntersect()));
		objects.add(factory.createProduct("asteroid.png", 310 , 70, 0, 0, new CircleIntersect()));
		objects.add(factory.createProduct("asteroid.png", 500 , 250, 0, 0, new CircleIntersect()));
		objects.add(factory.createProduct("asteroid.png", 700 , 100, 0, 0, new CircleIntersect()));
		objects.add(factory.createProduct("asteroid.png", 730 , 450, 0, 0, new CircleIntersect()));
		objects.add(factory.createProduct("asteroid.png", 980 , 250, 0, 0, new CircleIntersect()));
		objects.add(factory.createProduct("asteroid.png", 1120 , 450, 0, 0, new CircleIntersect()));
		objects.add(factory.createProduct("asteroid.png", 1255 , 70, 0, 0, new CircleIntersect()));
	}

}
