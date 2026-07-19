import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.util.ArrayList;

/**
* @authors Braa Oudeh, Duc Tan Tran, Khanh Nguyen, Paloma Ortiz
*
* Represents the main view of the planner, 
*
*/
public class PlannerView extends Application {
	DashBoard dashboard;
	PlannerModel model;
	SimpleIntegerProperty mode;	// 0: login;	1: user creation;	2: Calendar 
	PlannerController plannerController; // I named this plannerController instead of controller
											// just in case of other controllers (tasks, users, ... )
	SimpleStringProperty HEADER_BACKGROUND_COLOR;
	BorderPane dashboardPane;

	private Button selectedMusicTrackButton = null; // very specific flag for changing the background of the music track selected
	
	/**
	 * Instaniates the default values to the Plannerview
	 * connects a controller and instantiates a integer listener
	 * to detect when a view mode has changed
	 */
	public PlannerView() {
		model = new PlannerModel();
		HEADER_BACKGROUND_COLOR = new SimpleStringProperty("#2b2b2b");
		plannerController = new PlannerController(model);
		plannerController.setDashBoard(this);
		mode = new SimpleIntegerProperty();
		mode.set(0);	// default mode is 0:login
	}
	

	/**
	 * Creates the default stage, functionality for switching views,
	 * and displays the stage that holds the primary dashboard, login,
	 * and create account.
	 */
	@Override
	public void start(Stage stage) throws Exception {
		stage.setScene(new Scene(loginScene()));
		mode.addListener((observable, oldVal, newVal) -> {
			if(newVal.intValue() == 2) {
				// sets to fullscreen that way it feels more like a real app :P
				// TODO Known issue: fullscreen is removed after stage.setScene() is called, even after re calling .setMmaximized();
				Calendar calendar = plannerController.getCalendarFromUser();
				
				stage.getScene().setRoot(defaultDashboard(calendar, plannerController.getSettings()));
				stage.setMaximized(true);
				stage.setOnCloseRequest(e ->{
					plannerController.saveUserData();
				});
			} else if(newVal.intValue() == 0) {
				stage.getScene().setRoot(loginScene());
				stage.setMaximized(true);
			} else {
				stage.getScene().setRoot(createUserPopup());
				stage.setMaximized(true);

			}
		});
		
		stage.setMaximized(true);
		stage.show();
	}
	
	/**
	 * Creates the loginPane that should be displayed before the user login 
	 * is completed. Features the functionality to check the username and password
	 * in the PlannerController
	 * 
	 * @returns the completed BorderPane containing the login text fields, login button
	 * create new account button, and the welcome label
	 */
	public BorderPane loginScene() {
		BorderPane loginPane = new BorderPane();
		
		Image image = new Image("file:assets/login background.png");

		
		BackgroundImage backgroundImage = new BackgroundImage(image,
		    BackgroundRepeat.NO_REPEAT,
		    BackgroundRepeat.NO_REPEAT,
		    BackgroundPosition.DEFAULT,
		    new BackgroundSize(1.0, 1.0, true, true, false, true)); 

		loginPane.setBackground(new Background(backgroundImage));
		
		Label welcome = new Label("Welcome to Your Dream Planner!");
		welcome.setPadding(new Insets(50));
		welcome.setAlignment(Pos.CENTER);
		welcome.setFont(new Font("Arial", 30));
		
		VBox fields = new VBox();
		fields.setMaxWidth(500);
		fields.setMaxHeight(200);
		
		TextField usernameField = new TextField();
		PasswordField passwordField = new PasswordField();
		
		fields.getChildren().addAll(new Label("Username:"), usernameField, 
				new Label("Password:"), passwordField);
		
		HBox buttons = new HBox();
		buttons.setSpacing(50);
		buttons.setPadding(new Insets(50));
		buttons.setAlignment(Pos.CENTER);
		
		Button loginButton = new Button("Log In");
		Label incorrectPasswordLabel = new Label("Incorrect Password, Please Try Again");
		incorrectPasswordLabel.setOpacity(0.0);
			// fixed bug where "Incorrect Password" Label stacks after every login attempts
		fields.getChildren().add(incorrectPasswordLabel);
		// hitting enter after entering password
		passwordField.setOnAction(e -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			
			if(plannerController.getUser(username, password) != null) {
				fields.getChildren().clear();
				fields.getChildren().add(new Label("Successfully Logged In!"));
				plannerController.login(username,password);
				mode.set(2);
			} else {
				incorrectPasswordLabel.setOpacity(1.0);
				usernameField.clear();
				passwordField.clear();
			}
		});
		
		// pressing login
		loginButton.setOnAction(e -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			
			if(plannerController.getUser(username, password) != null && username!=null && password!=null) {
				fields.getChildren().clear();
				fields.getChildren().add(new Label("Successfully Logged In!"));
				plannerController.login(username,password);
				mode.set(2);
			} else {
				incorrectPasswordLabel.setOpacity(1.0);
				usernameField.clear();
				passwordField.clear();
			}
		});
		
		
		buttons.getChildren().addAll(loginButton, createAccountButton());
		buttons.setPrefWidth(200);
		
		loginPane.setTop(welcome);
		loginPane.setCenter(fields);
		loginPane.setBottom(buttons);
		
		return loginPane;
	}
	
	/**
	 * Creates the createUserPane that should be displayed before the user creates a new
	 * account. Features the functionality to check the username is avaliable and
	 * set a password in the PlannerController
	 * 
	 * @returns the completed BorderPane containing the new account text fields,
	 * create new account button, and the welcome label
	 */
	public BorderPane createUserPopup() {
		
		BorderPane createUserPane = new BorderPane();
		
		Image image = new Image("file:assets/login background.png");

		
		BackgroundImage backgroundImage = new BackgroundImage(image,
		    BackgroundRepeat.NO_REPEAT,
		    BackgroundRepeat.NO_REPEAT,
		    BackgroundPosition.DEFAULT,
		    new BackgroundSize(1.0, 1.0, true, true, false, true)); 

		createUserPane.setBackground(new Background(backgroundImage));
		
		
		Label welcome = new Label("Welcome to Your Dream Planner!");
		welcome.setPadding(new Insets(50));
		welcome.setAlignment(Pos.CENTER);
		welcome.setFont(new Font("Arial", 30));
		
		VBox fields = new VBox();
		fields.setMaxWidth(500);
		fields.setMaxHeight(200);
		
		// TextFields for username and passwords
		TextField usernameField = new TextField();
		PasswordField passwordField = new PasswordField();
		
		fields.getChildren().addAll(new Label("Username:"), usernameField, 
				new Label("Password:"), passwordField);
		
		HBox buttons = new HBox();
		buttons.setSpacing(50);
		buttons.setPadding(new Insets(50));
		buttons.setAlignment(Pos.CENTER);
		
		Button createUserButton = new Button("Create User");
		Button returnToLoginButton = new Button("Already have an account");
		Label existstedUserLabel = new Label("Username already existed, please try a diffrent Username");
		existstedUserLabel.setOpacity(0.0);
		fields.getChildren().add(existstedUserLabel);
		Alert userCreated = new Alert(AlertType.INFORMATION);
		userCreated.setContentText("New User Register Successfully. Please login with the newly created credential");
		userCreated.setHeaderText("Success");
		// hitting enter after entering password
		passwordField.setOnAction(e -> {
			String newUsername = usernameField.getText();
			String newPassword = passwordField.getText();
			
			if(plannerController.createUser(newUsername, newPassword) == true) {
				userCreated.showAndWait();	// TODO remove after logout is implemented
				fields.getChildren().clear();
				fields.getChildren().add(new Label("New User Created"));
				mode.set(0);
				
			} else {
				existstedUserLabel.setOpacity(1.0);
				usernameField.clear();
				passwordField.clear();
			}
		});
		
		returnToLoginButton.setOnMouseClicked(e->{
			mode.set(0);
		});
		
		createUserButton.setOnMouseClicked(e -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			
			if(plannerController.createUser(username, password) == true) {
				userCreated.showAndWait();	// TODO remove after logout is implemented
				fields.getChildren().clear();
				fields.getChildren().add(new Label("New User Created"));
				mode.set(0);
			} else {
				fields.getChildren().add(new Label("Username already existed, please try a diffrent Username"));
				usernameField.clear();
				passwordField.clear();
			}
		});
		
		buttons.getChildren().addAll(returnToLoginButton);
		buttons.setPrefWidth(200);
		
		buttons.getChildren().addAll(createUserButton);
		buttons.setPrefWidth(200);

		
		
		createUserPane.setTop(welcome);
		createUserPane.setCenter(fields);
		createUserPane.setBottom(buttons);
		
		// loginScreen = new Scene(createUserPane, 700, 700);
		return createUserPane;
					
	}
	
	/**
	 * Provides Create Account Button that allows scene to be changed to 
	 * the create account screen
	 * 
	 * @returns the functional Create Account Button
	 */
	public Button createAccountButton() {
		Button createAccount = new Button("Create New Account");

		createAccount.setOnMouseClicked(e -> {
			mode.set(1);
		});

		return createAccount;
	}

	/**
	 * Provides the create task button and its functionality, connecting
	 * to dashboard and controller to update appropiately.
	 * 
	 * @returns the functional JavaFX button Node for creating
	 * new tasks
	 */
	public Node createTaskButton(){
		Button button = new Button("Add Task");
		button.setStyle(
				"-fx-background-color: #93e0e3;" +
				"-fx-text-fill: #3f3f3f;"
		);
			
		TextField year = new TextField();
		year.setPromptText("YY");
		year.setPrefColumnCount(2);

		TextField month = new TextField();
		month.setPromptText("MM");
		month.setPrefColumnCount(2);

		TextField date = new TextField();
		date.setPromptText("DD");
		date.setPrefColumnCount(2);

		HBox dates = new HBox(5, year, month, date);
		dates.setAlignment(Pos.CENTER_LEFT);


		TextField titleField = new TextField();
		titleField.setPromptText("Title: ");

		TextField timeStartField = new TextField();
		timeStartField.setPromptText("Time start (HHMM): ");

		TextField timeEndField = new TextField();
		timeEndField.setPromptText("Time end (HHMM): ");

		TextField descriptionField = new TextField();
		descriptionField.setPromptText("Description: ");


		button.setOnAction(e ->{
			String title = titleField.getText();
			String timeStart = timeStartField.getText();
            String timeEnd = timeEndField.getText();
			String description = descriptionField.getText();
			String yearValue = year.getText();
			String monthValue = month.getText();
			String dateValue = date.getText();

			if (title != null && timeStart != null && timeEnd != null) {
				// adding the new data
				int taskTimeStart = Integer.parseInt(timeStart);
                int taskTimeEnd = Integer.parseInt(timeEnd);

				try{
					plannerController.addTask(Integer.parseInt(yearValue + monthValue + dateValue), title, taskTimeStart, taskTimeEnd, description);
					// clear everything
					titleField.clear();
					year.clear();
					month.clear();
					date.clear();
					timeStartField.clear();
					timeEndField.clear();
					descriptionField.clear();

					dashboard.updateView();
				} catch (Exception error) {
					Alert dateAlert = new Alert(AlertType.ERROR);
					dateAlert.setTitle("Date error");
					dateAlert.setContentText("Day doesnt exist. Make sure you enter the year as the final 2 digits, the month and day are reasonable numbers");
					dateAlert.showAndWait();
				}

			}
		});
		VBox box = new VBox(10, dates, titleField, timeStartField, timeEndField, descriptionField, button);
		box.setPadding(new Insets(15));
		box.setAlignment(Pos.CENTER_LEFT);
		return box;
	}

	/**
	 * Creates the interaction box that contains the buttons
	 *  with their functionalities for adding new tasks
	 * changing the color palette, adjusting music, and 
	 * the previous and next time periods.
	 * 
	 * @returns a JavaFX node containing all functional buttons
	 */
	public Node interactionBox(){
        Button addTaskButton = new Button();
        Image imgForAdd = new Image("file:assets/add-button.png");
        ImageView imageView = new ImageView(imgForAdd);
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        imageView.setPreserveRatio(true);
        addTaskButton.setGraphic(imageView);
 
        addTaskButton.setOnAction(e -> {
            BorderPane addTaskPane = new BorderPane();
            addTaskPane.setCenter(createTaskButton());
            Scene addTaskScene = new Scene(addTaskPane, 400, 400);
            Stage addTaskStage = new Stage();
            addTaskStage.setScene(addTaskScene);
            addTaskStage.show();
 
            addTaskStage.setOnCloseRequest(event -> {
                dashboard.updateView();
            });
        });
        
        //adding the color palette button
        Button colorButton = new Button();
        Image imgForColor = new Image("file:assets/color.png");
        ImageView imageViewColor = new ImageView(imgForColor);
        imageViewColor.setFitWidth(20);
        imageViewColor.setFitHeight(20);
        imageViewColor.setPreserveRatio(true);
        colorButton.setGraphic(imageViewColor);
        
        // bringing up the color palette editor
        colorButton.setOnAction(e -> {
        	BorderPane addColorPane = new BorderPane();
            addColorPane.setCenter(colorBox());
            Scene addColorScene = new Scene(addColorPane, 400, 400);
            Stage addColorStage = new Stage();
            addColorStage.setScene(addColorScene);
            addColorStage.show();
        });


		//adding the music button
		Button musicButton = new Button();
		Image imgForMusic = new Image("file:assets/speaker.png");
		ImageView imageViewMusic = new ImageView(imgForMusic);
		imageViewMusic.setFitWidth(20);
		imageViewMusic.setFitHeight(20);
		imageViewMusic.setPreserveRatio(true);
		musicButton.setGraphic(imageViewMusic);

		// bringing up the music editor
		musicButton.setOnAction(e -> {
			BorderPane addMusicPane = new BorderPane();
			addMusicPane.setCenter(musicBox());
			Scene addMusicStage = new Scene(addMusicPane, 400, 400);
			Stage addMusicScene = new Stage();
			addMusicScene.setScene(addMusicStage);
			addMusicScene.show();
		});
 
        
        Button previousBtn = new Button();
        Image imgForPrevious = new Image("file:assets/left-arrow.png");
        ImageView imageViewPrevious = new ImageView(imgForPrevious);
        imageViewPrevious.setFitWidth(20);
        imageViewPrevious.setFitHeight(20);
        imageViewPrevious.setPreserveRatio(true);
        previousBtn.setGraphic(imageViewPrevious);
 
 
        Button nextBtn = new Button();
        Image imgForNext = new Image("file:assets/right-arrow.png");
        ImageView imageViewNext = new ImageView(imgForNext);
        imageViewNext.setFitWidth(20);
        imageViewNext.setFitHeight(20);
        imageViewNext.setPreserveRatio(true);
        nextBtn.setGraphic(imageViewNext);

		previousBtn.setOnAction(e -> {
			int viewPeriod = dashboard.getCurrentDate();
			// if view day, go previous day
			if (dashboard.getDisplayMode() == 0)
			{
				viewPeriod = plannerController.getPreviousDay(viewPeriod);
			}

			else if (dashboard.getDisplayMode() == 1)
			{
				viewPeriod = plannerController.getPrevWeek(viewPeriod);
			}

			// if view month, go previous month
			else if (dashboard.getDisplayMode() == 2)
			{
				viewPeriod = plannerController.getPrevMonth(viewPeriod);
			}

			dashboard.changeCurrentDateTo(viewPeriod);
			dashboard.updateView();
		});

		nextBtn.setOnAction(e -> {
			int viewPeriod = dashboard.getCurrentDate();

			if (dashboard.getDisplayMode() == 0)	// day view
			{
				viewPeriod = plannerController.getNextDay(viewPeriod);

			}

			else if (dashboard.getDisplayMode() == 1)	// week view
			{
				viewPeriod = plannerController.getNextWeek(viewPeriod);
			}

			// if view month, go previous month
			else if (dashboard.getDisplayMode() == 2)
			{
				viewPeriod = plannerController.getNextMonth(viewPeriod);
			}
			System.out.println(viewPeriod);
			dashboard.changeCurrentDateTo(viewPeriod);
			dashboard.updateView();
		});

		
        BorderPane box = new BorderPane();
        
        HBox interactionLeft = new HBox(10, previousBtn, nextBtn);
        HBox interactionRight = new HBox(10, musicButton, colorButton, addTaskButton);
        box.setLeft(interactionLeft);
        box.setRight(interactionRight);
        box.setPadding(new Insets(15));
        return box;
    }
	

	/**
	 * Provides the popup window that allows for the music
	 * to be controlled.
	 * 
	 * @returns new BorderPane with music box functionality to display
	 * as a popup
	 */
	public BorderPane musicBox(){
		BorderPane musicEditor = new BorderPane();

		// all available songs panel
		VBox availableMusic = new VBox(10);
		availableMusic.setPadding(new Insets(15));

		ArrayList<String> allTracks = plannerController.getAllTracks();
		System.out.println(allTracks.toString());

		allTracks.forEach(songName ->{
			Button button = new Button(songName);
			button.setMaxWidth(Double.MAX_VALUE);

			button.setOnAction(e -> {

				if (selectedMusicTrackButton != null) {
					selectedMusicTrackButton.setStyle(""); // back to default
				}

				plannerController.changeMusic(songName);
				plannerController.playMusic();
				// set the background to jade color
				button.setStyle("-fx-background-color: #00A86B;");

				selectedMusicTrackButton = button;

			});

			availableMusic.getChildren().add(button);
		});

		// bottom control section
		HBox musicControl = new HBox(10);
		musicControl.setPadding(new Insets(15));

		Button stopButton = new Button("Stop");
		stopButton.setOnAction(e -> {
			plannerController.stopMusic();
		});
		Button resumeButton = new Button("Resume");
		resumeButton.setOnAction(e -> {
			plannerController.continueMusic();
		});
		Button increaseVolumnButton = new Button("Vol +");
		increaseVolumnButton.setOnAction(e -> {
			plannerController.increaseVolumn();
		});
		Button decreaseVolumnButton = new Button("Vol -");
		decreaseVolumnButton.setOnAction(e -> {
			plannerController.decreaseVolumn();
		});

		musicControl.getChildren().add(stopButton);
		musicControl.getChildren().add(resumeButton);
		musicControl.getChildren().add(increaseVolumnButton);
		musicControl.getChildren().add(decreaseVolumnButton);

		musicEditor.setTop(new Label("Select your song!"));
		musicEditor.setCenter(availableMusic);
		musicEditor.setBottom(musicControl);

		return musicEditor;
	}

	/**
	 * Calls the functions to add color pickers and buttons to the color box
	 * 
	 * @param dashboard, used for updating dashboard colors
	 * @return the JavaFx BorderPane that was created
	 */
	public BorderPane colorBox() {
		BorderPane colorEditor = new BorderPane();
		
		VBox colorPickers = new VBox(
				new Label("Advanced Customization: "),
				backgroundColorPicker(),
				attributeColorPicker("BACKGROUND_COLOR"),
				attributeColorPicker("BORDER_COLOR"),
				attributeColorPicker("HEADER_TEXT"),
				attributeColorPicker("REGULAR_TEXT"),
				attributeColorPicker("BUTTONS_COLOR"),
				attributeColorPicker("TASK_BACKGROUND_COLOR"),
				attributeColorPicker("TASK_BORDER")
		);
		
		Button zenburn = new Button("Dark");
		zenburn.setOnAction(e -> {
			dashboard.updatePalette("Dark");
			updatePalette("Dark");
		});
		Button light = new Button("Light");
		light.setOnAction(e -> {
			dashboard.updatePalette("Light");
			updatePalette("Light");
		});
		Button burnt = new Button("Burnt");
		burnt.setOnAction(e -> {
			dashboard.updatePalette("Burnt");
			updatePalette("Burnt");
		});
		Button dreamy = new Button("Bubble Gum");
		dreamy.setOnAction(e -> {
			dashboard.updatePalette("Bubble Gum");
			updatePalette("Bubble Gum");
		});
		
		
		HBox themes = new HBox(10, zenburn, light, burnt, dreamy);
		colorEditor.setTop(new Label("Color Palette Editor"));
		colorEditor.setCenter(themes);
		colorEditor.setBottom(colorPickers);
		colorEditor.setPadding(new Insets(15));
		
		return colorEditor;
	}
	
	/**
	 * Creates the functional color picker for the given attribute
	 * 
	 * @param dashboard is the DashBoard to be updated
	 * @param attribute is the name of the color attribute to edit
	 * 
	 * @returns newly created borderpane containing the functional color picker
	 */
	public BorderPane attributeColorPicker(String attribute) {
		
		
		ColorPicker colorPicker = new ColorPicker();
		colorPicker.setOnAction(e -> {
			
			Color c = colorPicker.getValue();
			String hex = String.format( "#%02X%02X%02X",
			             (int)( c.getRed() * 255 ),
			             (int)( c.getGreen() * 255 ),
			             (int)( c.getBlue() * 255 ) );
			dashboard.updateColor(attribute, hex);
		 });
		
		String label = String.format("%s: ",
			    attribute.toLowerCase().replace("_", " ")
		);

		
		BorderPane attributeColorBox = new BorderPane();
		attributeColorBox.setLeft(new Label(label));
		attributeColorBox.setRight(colorPicker);
		attributeColorBox.setPadding(new Insets(2.5));
		return attributeColorBox;
	}
	
	/**
	 * Allows for colors to be loaded upon start up
	 * 
	 * @param colors is an array of 8 hexcode strings to
	 * feed into the colors in the layout
	 */
	public void loadColors(String[] colors) {
		HEADER_BACKGROUND_COLOR.set(colors[0]);
		dashboard.updateAllColors(colors);
	}
	

	/**
	 * Allows for colors to be saved upon log out and
	 * window closing
	 * 
	 * @returns an array of 8 hexcode strings to
	 * allow the color customization to be preserved
	 * between sessions
	 */
	public String[] saveColors() {
		String[] colors = dashboard.saveAllColors();
		colors[0] = HEADER_BACKGROUND_COLOR.get();
		return colors;
	}
	
	
  
	/**
	 * Calls the function to update/redraw the current DashBoard
	 */
	public void updateDashboard() {
		dashboard.updateView();
	}
	
	/**
	 * Provides the BorderPane that contains the colorpicker
	 * associated with the background of the outer planner
	 * 
	 * @returns newly created borderpane containing the functional color picker
	 */
	public BorderPane backgroundColorPicker() {
		ColorPicker colorPicker = new ColorPicker();
		colorPicker.setOnAction(e -> {
			Color c = colorPicker.getValue();
			String hex = String.format( "#%02X%02X%02X",
				             (int)( c.getRed() * 255 ),
				             (int)( c.getGreen() * 255 ),
				             (int)( c.getBlue() * 255 ) );
			HEADER_BACKGROUND_COLOR.set(hex);
			
		});
		
		String label = "Interaction Frame Color";

		
		BorderPane attributeColorBox = new BorderPane();
		attributeColorBox.setLeft(new Label(label));
		attributeColorBox.setRight(colorPicker);
		attributeColorBox.setPadding(new Insets(2.5));
		return attributeColorBox;
	}
	
    /**
     * Updates the entire Palette based on PlannerView selection
     * 
     * @param palette is the new palette that has been selected
     */
    public void updatePalette(String palette) {
    	if(palette.equals("Dark")){
    	    HEADER_BACKGROUND_COLOR.set("#2b2b2b");
    	    
    	}
    	if(palette.equals("Light")){
    	    HEADER_BACKGROUND_COLOR.set("#f4e3d7");
    	    
    	}
    	if(palette.equals("Burnt")) {
    		HEADER_BACKGROUND_COLOR.set("#003333");
    	}
    	if(palette.equals("Bubble Gum")){
    	    HEADER_BACKGROUND_COLOR.set("#E6B3CC");
    	}
    }
  
	/**
	 * Creates the borderpane that holds the dashboard of the planner using
	 * the calendar provided
	 * 
	 * @param calendar is the calender that will be used to occupy the
	 * dashboard
	 * @returns the BorderPane containing the dashboard, and the interaction box
	 * at the bottom
	 */

	public BorderPane defaultDashboard(Calendar calendar, String[] settings) {
		dashboard = new DashBoard(plannerController, calendar,this, Integer.valueOf(settings[1]));
		BorderPane pane = new BorderPane();
		
		dashboard.updateAllColors(settings);
		HEADER_BACKGROUND_COLOR = new SimpleStringProperty(settings[2]);
		pane.setCenter(dashboard.getPane());
		pane.setPadding(new Insets(80,0,0,0));
		
		pane.setStyle("-fx-background-color: " + HEADER_BACKGROUND_COLOR.getValue() + ";");
		HEADER_BACKGROUND_COLOR.addListener((observable, oldValue, newValue) -> {
			pane.setStyle("-fx-background-color: " + newValue + ";");
		});
		pane.setBottom(interactionBox());
		return pane;
	}
	
	/**
	* Allows for colors to be loaded upon start up
	*
	* @param colors is an array of 8 hexcode strings to
	* feed into the colors in the layout
	*/
	
	public int getDisplayMode() {
		return dashboard.getDisplayMode();
	}
	
	public void updateDisplayMode(int mode) {
		if (dashboard == null) {
			return;
		}
    	dashboard.updateDisplayMode(mode);
    }

	public String getHeaderBackground() {
		return HEADER_BACKGROUND_COLOR.getValue();
	}
}
