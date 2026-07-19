import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import java.util.ArrayList;

/**
* @authors Braa Oudeh, Duc Tan Tran, Khanh Nguyen, Paloma Ortiz
*
* Represents the dashboard of the planner, and contains most data
* that will be unique to a user account. It features different
* month, week, and day layouts, allowing for tasks to be viewed, edited
* deleted, and added. As well as allowing for the user to log out from 
* the dashboard. It functions by redrawing itself everytime its update
* function is called. Similar to how observer works.
*/
public class DashBoard {
    private static final String[] DAYNAMES      = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private static final String[] MONTHNAMES = {"", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    final int DAY = 0;
    final int WEEK = 1;
    final int MONTH = 2;

    private Calendar calendar;
    private int currentDateEncode;
    private BorderPane pane;
    private PlannerController controller;
    private PlannerView view;

    // Day = 0, Week = 1, Month = 2, Year = 3
    private int displayMode;

    // coloring scheme: zenburn
    private String BACKGROUND_COLOR = "#3f3f3f";
    private String BORDER_COLOR = "#5f5f5f";
    private String HEADER_TEXT = "#dcdccc";
    private String REGULAR_TEXT = "#9f9f9f";
    private String BUTTONS_COLOR = "#f0dfaf";
    private String TASK_BACKGROUND_COLOR = "#f5f5dc";
    private String TASK_BORDER = "#93e0e3";


    /**
     * Instantiates the required data to build the DashBoard used by the PlannerView
     *
     * @param controller is the PlannerController that is used by the DashBoard
     * @param calendar is the Calendar object that is used by the DashBoard
     * @param view is the PlannerView that is used by the Dashboard
     * @param displayMode is int value of which layout should be loaded into
     */

    public DashBoard(PlannerController controller, Calendar calendar, PlannerView view, int displayMode){ 
        this.calendar = calendar;
        this.controller = controller;
        this.view = view;
        this.currentDateEncode = controller.getTodayDate();
        this.pane = new BorderPane();

        this.displayMode = displayMode; // Whoever hard coded the default display in the constructor instead of when calling it - please kindly run into a wall 


        Background background = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
        pane.setBackground(background);
        updateView();
    }
    
    /**
     * redraws the DashBoard view, by updating the task displays
     * and the date displayed by the timespan header
     */
    public void updateView(){
        pane.setTop(timeSpanHeader());
        pane.setCenter(makeDashBoard());
    }
    

    /**
     * Handles the very top header that display the time period that
     * the user is viewing, as well as the layout selector and logout
     * button
     *
     * @returns the HBox with the timespan label, layout selector,
     * and logout button
     */
    public HBox timeSpanHeader(){
        int year = 2000 + (currentDateEncode / 10000);
        int month = (currentDateEncode / 100) % 100;
        int day = currentDateEncode % 100;

        String timeString;
        if (displayMode == DAY){
            timeString = MONTHNAMES[month] + " " + day + ", " + year;
        }
        else {
            timeString = MONTHNAMES[month] + ", " + year;
        }

        Label dateLabel = new Label(timeString);
        dateLabel.setStyle(
                "-fx-text-fill: " + HEADER_TEXT + ";" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;"
        );

        ChoiceBox<String> viewSelector = new ChoiceBox<String>();
        viewSelector.getItems().addAll("Day", "Week", "Month");
        viewSelector.setPrefWidth(150);
        viewSelector.setValue("Select a view");

        viewSelector.setOnAction(e -> {
            String selectedView = viewSelector.getValue();
            if (selectedView.equals("Day")){
                displayMode = DAY;
            }
            else if (selectedView.equals("Week")){
                 displayMode = WEEK;
             }
            else if (selectedView.equals("Month")){
                displayMode = MONTH;
            }
            updateView();
        });
        
        Button logOutButton = new Button();
        Image imgForLogout = new Image("file:assets/logout.png");
        ImageView imageViewLogout = new ImageView(imgForLogout);
        imageViewLogout.setFitWidth(20);
        imageViewLogout.setFitHeight(20);
        imageViewLogout.setPreserveRatio(true);
        logOutButton.setGraphic(imageViewLogout);

        logOutButton.setOnAction(e -> {
            controller.logout();
            view.mode.set(0);
        });

        Region spaceBetweenButtons = new Region();
        HBox.setHgrow(spaceBetweenButtons, Priority.ALWAYS);
        Region spaceBetweenViewSelectorAndLogout = new Region();
        spaceBetweenViewSelectorAndLogout.setMinWidth(10);

        HBox header = new HBox(dateLabel, spaceBetweenButtons, viewSelector, spaceBetweenViewSelectorAndLogout, logOutButton);

        header.setPadding(new Insets(10, 10, 10, 80));
        header.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        return header;
    }


    /**
     * Retrieves the proper dashboard layout to display
     * the selected/preferred view
     *
     * @return the javaFX node corresponding to the correct
     * display mode
     */
    private Node makeDashBoard(){
        if (displayMode == DAY){
            return dayView();
        }
        else if (displayMode == WEEK){
        	return weekView();
        }
        else{
            return monthView();
        }
    }
    
    /**
     * calls the correct functions to retrieve the correct
     * parts of the daylayout and wraps it into a ScrollPane
     * using the putGridInScrollPane
     *
     * @returns JavaFX node of the dayView layout
     */
    private Node dayView() {
        Day day = this.calendar.getDate(currentDateEncode);
        GridPane grid = hourGrid(1);
        if (day != null) placeAllTasksInColumn(grid, day, 1);
        return putGridInScrollPane(grid);
    }
    


    /**
     * calls the correct functions to retrieve the correct
     * parts of the week layout and wraps it into a ScrollPane
     * using the putGridInScrollPane
     *
     * @returns JavaFX node of the weekView layout
     */
    private Node weekView(){
       
        Day currDayOfWeekView = controller.getFirstDayOfWeek(currentDateEncode);


        GridPane grid = hourGrid(7);
        HBox dates = new HBox();
        
        for(int i = 1; i < 8; i++) {
            if (currDayOfWeekView != null) placeAllTasksInColumn(grid, currDayOfWeekView, i);
            
            Label date = new Label(String.valueOf(currDayOfWeekView.getDate()));
            date.setAlignment(Pos.CENTER);
            date.setMaxWidth(Double.MAX_VALUE);
            date.setPadding(new Insets(4));
            date.setStyle(
                    "-fx-text-fill: " + HEADER_TEXT + ";" +
                    "-fx-font-size: 14;" +
                    "-fx-background-color: " + BACKGROUND_COLOR+ ";"
            );
            Label weekDayName = new Label(DAYNAMES[i-1]);
            weekDayName.setAlignment(Pos.CENTER);
            weekDayName.setMaxWidth(Double.MAX_VALUE);
            weekDayName.setPadding(new Insets(4));
            weekDayName.setStyle(
                    "-fx-text-fill: " + HEADER_TEXT + ";" +
                    "-fx-font-size: 14;" +
                    "-fx-background-color: " + BACKGROUND_COLOR+ ";"
            );
            VBox dayOfWeek = new VBox(weekDayName, date);
            // Automatic resizing width
            HBox.setHgrow(dayOfWeek, Priority.ALWAYS);
            dates.getChildren().add(dayOfWeek);
            
            currDayOfWeekView = currDayOfWeekView.getNextDay();
        }
        
        ScrollPane weekScroll = putGridInScrollPane(grid);
        
        VBox container = new VBox(0, dates, weekScroll);
        VBox.setVgrow(grid, Priority.ALWAYS);
        return container;
    }

    /**
     * calls the correct functions to retrieve the correct
     * parts of the month layout and wraps it into a ScrollPane
     * using the putGridInScrollPane
     *
     * @returns JavaFX node of the monthView layout
     */
    private Node monthView(){
        int year = currentDateEncode / 10000;
        int month = (currentDateEncode / 100) % 100;

        Day firstDayOfMonth = controller.getFirstDayOfMonth(year, month);
        Day firstDayOfMonthGridView = controller.getFirstDayOfMonthGridView(firstDayOfMonth, year, month);
        int numberOfRows = controller.calculateMonthRows(firstDayOfMonth, year, month);

        HBox weekDayRow = new HBox();
        for (String name : DAYNAMES) {
            Label weekDayName = new Label(name);
            weekDayName.setAlignment(Pos.CENTER);
            weekDayName.setMaxWidth(Double.MAX_VALUE);
            weekDayName.setPadding(new Insets(4));
            weekDayName.setStyle(
                    "-fx-text-fill: " + HEADER_TEXT + ";" +
                    "-fx-font-size: 14;" +
                    "-fx-background-color: " + BACKGROUND_COLOR+ ";"
            );
            // Automatic resizing width
            HBox.setHgrow(weekDayName, Priority.ALWAYS);
            weekDayRow.getChildren().add(weekDayName);
        }
        weekDayRow.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        // set up the 7 columns
        for (int i = 0; i < 7; i ++){
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0/7); // 7 equal width columns
            col.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(col);
        }

        // set up the rows
        for (int i = 0; i < numberOfRows; i ++){
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100/numberOfRows); // equal height rows
            row.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(row);
        }
        
        Day currDayPointer = firstDayOfMonthGridView;
        int daysDisplay = 7 * numberOfRows;
        for (int i = 0; i < daysDisplay; i++){

            int col = i % 7;
            int row = i / 7;
            
            VBox targetDay = dayInMonth(currDayPointer, month);
            targetDay.setAlignment(Pos.TOP_CENTER);
            
            grid.add(targetDay, col, row);

            currDayPointer = currDayPointer.getNextDay();
            
        }

        grid.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        VBox container = new VBox(0, weekDayRow, grid);
        VBox.setVgrow(grid, Priority.ALWAYS);
        return container;
    }

    /**
     * Creates the day representation to be displayed by the month
     * view
     * 
     * @param currDay is the day that shpould be represented
     * @param targetMonth is the month to be displayed
     * 
     * @returns the filled vbox containing the visualization
     * related to currDay
     */
    private VBox dayInMonth(Day currDay, int targetMonth){
        VBox day = new VBox(2); // vertical gap for every child is 2px
        day.setPadding(new Insets(3,3,3,3));
        day.setMaxHeight(Integer.MAX_VALUE);
        day.setMaxWidth(Integer.MAX_VALUE);
        day.setFillWidth(true);
        day.setStyle(
                "-fx-background-color: " + BACKGROUND_COLOR + "; " +
                "-fx-border-color: " + BORDER_COLOR + "; " +
                "-fx-border-width: 1 1 1 1;"
        );
        
        // if its a date in the targeted month, make the label date bigger, else smaller

        Label date = new Label(String.valueOf(currDay.getDate()));
        if (currDay.getMonth() == targetMonth){
            date.setStyle(
                    "-fx-text-fill: " + HEADER_TEXT + "; " +
                    "-fx-font-size: 15;" +
                    "-fx-font-weight: bold;"
            );
        }
        else{
            date.setStyle(
                    "-fx-text-fill: " + REGULAR_TEXT + "; " +
                    "-fx-font-size: 11;"
            );
        }
        day.getChildren().add(date);

        // display max 3 task, otherwise have a "..."
        if (currDay.getTaskList().size() < 4){
            for (Task task : currDay.getTaskList()) {
                day.getChildren().add(makeTaskBox(task, currDay.getDateEncode()));
            }
            return day;
        }
        else{
            ArrayList<Task> taskList = currDay.getTaskList();
            for (int i = 0; i < 3; i ++){
                day.getChildren().add(makeTaskBox(taskList.get(i), currDay.getDateEncode()));
            }
            Label more = new Label (" . . . ");
            more.setStyle(
                    "-fx-text-fill: " + REGULAR_TEXT + "; " +
                    "-fx-font-size: 11;"
            );
            day.getChildren().add(more);
            return day;
        }
    }


    /**
     * Look at the column, then place all the task of that day into that column
     * 
     * @param grid is the GridPane that the tasks from day should be placed into
     * @param day is the Day that tasks should be taken from
     * @param column is the column that the tasks should be placed in
     */
    private void placeAllTasksInColumn(GridPane grid, Day day, int column){
    
        // go through every tasks
        for (Task task : day.getTaskList()) {
            int hourStart = task.getTimeStart() / 100; // get the hour from HHMM
            int hourEnd = task.getTimeEnd() / 100;
            VBox taskBox = makeTaskBox(task, day.getDateEncode());
           
            
            taskBox.setMaxHeight((hourEnd - hourStart) * 60); 
            
            grid.add(taskBox, column, hourStart);
            GridPane.setRowSpan(taskBox, hourEnd - hourStart); 
            GridPane.setMargin(taskBox, new Insets(2, 4, 2, 4));
            
        }
    }

    /**
     * Creates the edit task button to be displayed in the Task Boxes,
     * including editing functionality
     * 
     * @param originalTitle is the title of the original task, that is
     * being edited
     * @returns the JavaFX Node representing the edit button
     */
    public Node editTaskButton(String originalTitle){
		Button button = new Button("Edit Task");
		// adds Buttons Color to button
		button.setStyle(
				"-fx-background-color:" + BUTTONS_COLOR + ";" +
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

        
		TextField titleField = new TextField();
		titleField.setPromptText("New Title: ");

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

				controller.editTask(Integer.parseInt(yearValue + monthValue + dateValue), originalTitle, title, taskTimeStart, taskTimeEnd, description);
                
				// clear everything
				titleField.clear();
				year.clear();
				month.clear();
				date.clear();
				timeStartField.clear();
                timeEndField.clear();
				descriptionField.clear();

				this.updateView();
			}
		});
        HBox dates = new HBox(5, year, month, date);
        dates.setAlignment(Pos.CENTER_LEFT);
        
		VBox box = new VBox(10, dates, titleField, timeStartField, timeEndField, descriptionField, button);
		box.setPadding(new Insets(15));
		box.setAlignment(Pos.CENTER_LEFT);
		return box;
	}
    

    /**
     * Creates the box that represents a task in the Planner Dashboard
     * 
     * @param task is the Task to be represented 
     * @returns a borderpane showing the information related to the task
     * as well as offering editing and deletion functionality
     */
    private VBox makeTaskBox(Task task, int dateEncode){
    	
        Label taskLabel = new Label(task.getTitle()); 
        taskLabel.setMouseTransparent(true);

        Button editBtn = new Button();
        Image imgForEdit = new Image("file:assets/edit.png");
        ImageView imageViewEdit = new ImageView(imgForEdit);

        imageViewEdit.setPreserveRatio(true);
        editBtn.setGraphic(imageViewEdit);
        // adds Buttons Color to button
        editBtn.setStyle(
				"-fx-background-color:" + BUTTONS_COLOR + ";" +
				"-fx-text-fill: #3f3f3f;"
		);

        Button deleteButton = new Button();
        Image imgForDelete = new Image("file:assets/remove.png");
        ImageView imageViewDelete = new ImageView(imgForDelete);
        
        
        // adds Buttons Color to button
        deleteButton.setStyle(
				"-fx-background-color:" + BUTTONS_COLOR + ";" +
				"-fx-text-fill: #3f3f3f;"
		);

       
        if (displayMode == MONTH)
        {
        	taskLabel.setMaxWidth(100); // Set a limit
            
            imageViewEdit.setFitWidth(10);
            imageViewEdit.setFitHeight(10);
            imageViewDelete.setFitWidth(10);
            imageViewDelete.setFitHeight(10);
            taskLabel.setStyle("-fx-font-size: 10; -fx-padding: 2 4 2 4;");

        }
        // adding week display
        if (displayMode == WEEK)
        {
        	taskLabel.setMaxWidth(90); // Set a limit
           
            taskLabel.setWrapText(true);
            imageViewEdit.setFitWidth(10);
            imageViewEdit.setFitHeight(10);
            imageViewDelete.setFitWidth(10);
            imageViewDelete.setFitHeight(10);
            taskLabel.setStyle("-fx-font-size: 10; -fx-padding: 2 4 2 4;");

        }
        else if (displayMode == DAY)
        {
        	taskLabel.setWrapText(true);
            imageViewEdit.setFitWidth(20);
            imageViewEdit.setFitHeight(20);
            imageViewDelete.setFitWidth(20);
            imageViewDelete.setFitHeight(20);
            taskLabel.setStyle("-fx-font-size: 12; -fx-padding: 4 6 4 6;");
        }
        // task deletion functionality
        deleteButton.setOnAction(e -> {
            controller.removeTaskByTitle(dateEncode, task.getTitle());
            updateView();
        });
        // task editing functionality
        editBtn.setOnAction(e -> {
            BorderPane editTaskPane = new BorderPane();
            editTaskPane.setCenter(editTaskButton(task.getTitle()));
            Scene editTaskScene = new Scene(editTaskPane, 400, 400);
            Stage editTaskStage = new Stage();
            editTaskStage.setScene(editTaskScene);
            editTaskStage.show();
            editTaskStage.setOnCloseRequest(event -> {
                updateView();
                
            });
        });
        // task label
        taskLabel.setStyle(      
            "-fx-text-fill: " + REGULAR_TEXT + "; "
            + "-fx-background-radius: 4; "
            + "-fx-padding: 2 6 2 6;"
        );
        taskLabel.setAlignment(Pos.CENTER_LEFT);
        // button icons
        imageViewEdit.setPreserveRatio(true);
        editBtn.setGraphic(imageViewEdit);

        imageViewDelete.setPreserveRatio(true);
        deleteButton.setGraphic(imageViewDelete);

        int dynamicPaddingBetweenButtons = 2;
        HBox buttonContainer = new HBox(dynamicPaddingBetweenButtons, editBtn, deleteButton);
        buttonContainer.setAlignment(Pos.TOP_RIGHT);

        
        Label taskDescription = new Label(task.getDescription());
        taskDescription.setWrapText(true);
        taskDescription.setStyle(      
                "-fx-text-fill: " + REGULAR_TEXT + "; "
                + "-fx-background-radius: 4; "
                + "-fx-padding: 2 6 2 6;"
            );
        taskDescription.setAlignment(Pos.TOP_CENTER);
        
        taskLabel.setStyle(      
                "-fx-text-fill: " + REGULAR_TEXT + "; "
                + "-fx-background-radius: 4; "
                + "-fx-padding: 2 6 2 6;"
            );
        
        
        BorderPane taskHeader = new BorderPane();
        taskHeader.setLeft(taskLabel);
        taskHeader.setRight(buttonContainer);
        VBox taskBox = new VBox(taskHeader);
        
        // adds style to task box
        taskBox.setStyle(
                "-fx-background-color: " + TASK_BACKGROUND_COLOR + "; "
                + "-fx-border-color: " + TASK_BORDER + "; "
                + "-fx-text-fill: " + REGULAR_TEXT + "; "
                + "-fx-background-radius: 4; "
                + "-fx-padding: 2 6 2 6;"
                + "-fx-background-radius: 15; -fx-border-radius: 15;"
            );

        if (displayMode == MONTH)
        {
            taskBox.setMaxWidth(500);
            taskBox.setMaxHeight(200);
        }
        else if(displayMode == WEEK) {
        	taskBox.setMaxWidth(500);
            taskBox.setMaxHeight(200);
          
            taskBox.getChildren().add(taskDescription);
        }
        else if (displayMode == DAY)
        {
            taskBox.setMaxWidth(Double.MAX_VALUE);
            taskBox.setMaxHeight(Double.MAX_VALUE);
            taskBox.getChildren().add(taskDescription);
        }
        return taskBox;
    }


    /**
     * Wraps a given javaFX node in scroll pane
     *
     * @param gridContent is the content to wrap
     * @returns the gridContent now inside a ScrollPane
     */
    private ScrollPane putGridInScrollPane(Node gridContent) {
        ScrollPane scrollPane = new ScrollPane(gridContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background: transparent;");
        scrollPane.setVvalue(8.0 / 24.0); // display 8 hours out of 24 hours
        return scrollPane;
    }

    /**
     * Returns a grid with the rows corresponding
     * to hours in the day, and columns being the days that
     * are shown
     * 
     * @param numberOfShownDays increases the amount of columns to be shown
     * @returns the populated hour grid with 24 rows and numberOfShownDays columns
     */
    private GridPane hourGrid(int numberOfShownDays){
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: " + BACKGROUND_COLOR +";");

        if(numberOfShownDays == 1) {
        	// define the width of the columns before anything is put in
            ColumnConstraints hourColumnWidth = new ColumnConstraints(60);
            grid.getColumnConstraints().add(hourColumnWidth);
            for (int i = 0; i < numberOfShownDays; i++) {
                ColumnConstraints col = new ColumnConstraints();
                col.setHgrow(Priority.ALWAYS);
                col.setMinWidth(100);
                grid.getColumnConstraints().add(col);
            }
        }
        else {
        	ColumnConstraints hourColumnWidth = new ColumnConstraints();
        	hourColumnWidth.setPercentWidth(4.5);
            grid.getColumnConstraints().add(hourColumnWidth);
            for (int i = 0; i < numberOfShownDays; i++) {
                ColumnConstraints col = new ColumnConstraints();
                col.setPercentWidth(95.5/numberOfShownDays); // 7 equal width columns
                col.setHgrow(Priority.ALWAYS);
                col.setMinWidth(100);
                grid.getColumnConstraints().add(col);
            }
        }
        // creating the rows, every row is 60 pixel tall
        for (int hour = 0; hour < 24; hour++) {
            grid.getRowConstraints().add(new RowConstraints(60));

            // make the time label and place it in column 0
            Label time = new Label(hourFormat(hour));
            time.setStyle(
                "-fx-text-fill: " + HEADER_TEXT
                + "; -fx-font-size: 12;"
            );
            time.setPadding(new Insets(0, 0, 45, 15));
            grid.add(time, 0, hour); // place all of the time in col 0 (the hour column)

            // inside every hour row, add the number of cells corresponding to the number of shown days
            for (int i = 1; i <= numberOfShownDays; i++) {
                VBox cell = new VBox();
                cell.setStyle(
                    "-fx-border-color: " + BORDER_COLOR + "; "
                    + "-fx-border-width: 1 1 1 1;"
                );
                grid.add(cell, i, hour);
            }
        }
        return grid;
    }
    
    /**
     * Provides hour format for week and day display
     *
     * @param hour is the int that should be formatted
     * @return String of int as "XX:00"
     */
    private String hourFormat(int hour){
        if (hour < 10){
            return "0" + String.valueOf(hour) + ":00";
        }
        return String.valueOf(hour) + ":00";
    }
    
    /**
     * returns the pane that held by the BorderPane
     *
     * @returns the BorderPane held by this.pane
     */
    public BorderPane getPane(){
        return this.pane;
    }

    /**
     * provides outside access to the current display mode
     * that is selected
     *
     * @returns the int associated with the displayMode
     * displayMode == 0 is day
     * displayMode == 1 is week
     * displayMode == 2 is month
     */
    public int getDisplayMode(){
        return this.displayMode;
    }
    
   
    /**
     * Updates the entire Palette based on PlannerView selection
     * 
     * @param palette is the new palette that has been selected
     */
    public void updatePalette(String palette) {
    	if(palette.equals("Dark")){
    	    BACKGROUND_COLOR = "#3f3f3f";
    	    BORDER_COLOR = "#5f5f5f";
    	    HEADER_TEXT = "#dcdccc";
    	    REGULAR_TEXT = "#9f9f9f";
    	    BUTTONS_COLOR = "#cccccc";
    	    TASK_BACKGROUND_COLOR = "#f2f2f2";
    	    TASK_BORDER = "#999999";
    	    
    	}
    	if(palette.equals("Light")){
    	    BACKGROUND_COLOR = "#f9f5f0";
    	    BORDER_COLOR = "#e0e0e0";
    	    HEADER_TEXT = "#7b6b6b";
    	    REGULAR_TEXT = "#4c3e3e";
    	    BUTTONS_COLOR = "#a8a08f";
    	    TASK_BACKGROUND_COLOR = "#e0d2c2";
    	    TASK_BORDER = "#c4b6a6";
    	    
    	}
    	if(palette.equals("Burnt")){
    	    BACKGROUND_COLOR = "#e8efef";
    	    BORDER_COLOR = "#d0dfdf";
    	    HEADER_TEXT = "#006666"; // #5e9291
    	    REGULAR_TEXT = "#330000";
    	    BUTTONS_COLOR = "#852323";
    	    TASK_BACKGROUND_COLOR = "#c0a5a5";
    	    TASK_BORDER = "#660000";
    	    
    	}
    	if(palette.equals("Bubble Gum")){
    	    BACKGROUND_COLOR = "#FFE6E8";
    	    BORDER_COLOR = "#E78F8E";
    	    HEADER_TEXT = "#5A4D57";
    	    REGULAR_TEXT = "#13070C";
    	    BUTTONS_COLOR = "#F2CCC3";
    	    TASK_BACKGROUND_COLOR = "#DD7C9C";
    	    TASK_BORDER = "#661A33";
    	    
    	}
    	// for any future palettes, copy and paste with new palette name + new hexcodes
    	updateView();
    }
     
    /**
     * updates attribute colors based on the color picker from PlannerView
     * 
     * @param attribute is the attribute to edit the color of
     * @param color is the new hexcode of the color to change attribute to
     */
    public void updateColor(String attribute, String color) {
        if (attribute.equals("BACKGROUND_COLOR")) {BACKGROUND_COLOR = color; System.out.print(color);}
        if (attribute.equals("BORDER_COLOR")) {BORDER_COLOR = color; System.out.print(BORDER_COLOR);}
        if (attribute.equals("HEADER_TEXT")) {HEADER_TEXT = color; System.out.print(HEADER_TEXT);}
        if (attribute.equals("REGULAR_TEXT")) {REGULAR_TEXT = color; System.out.print(REGULAR_TEXT);}
        if (attribute.equals("BUTTONS_COLOR")) {BUTTONS_COLOR = color; System.out.print(BUTTONS_COLOR);}
        if (attribute.equals("TASK_BACKGROUND_COLOR")) {TASK_BACKGROUND_COLOR = color; System.out.print(TASK_BACKGROUND_COLOR);}
        if (attribute.equals("TASK_BORDER")) {TASK_BORDER = color; System.out.print(TASK_BORDER);}
        updateView();
    }
    
    
    /**
     * get the current date as an integer
     *
     * @return the currentDateEncode int currently held by DashBoard
     */
    public int getCurrentDate(){
        return this.currentDateEncode;
    }

    /**
     * Provides a way to assign a new date encode
     *
     * @param newDateEncode is the new int that should be
     * assigned to currentDateEncode
     */
    public void changeCurrentDateTo(int newDateEncode){
        this.currentDateEncode = newDateEncode;
    }
    
    public String[] saveAllColors() {
    	String[] colors = new String[8];
        colors[1] =  BACKGROUND_COLOR;
        colors[2] = BORDER_COLOR;
        colors[3] = HEADER_TEXT;
        colors[4] = REGULAR_TEXT;
        colors[5] = BUTTONS_COLOR;
        colors[6] = TASK_BACKGROUND_COLOR;
        colors[7] = TASK_BORDER;
        return colors;
    }
    
    /**
    * updates attribute colors based on the color picker from PlannerView
    *
    * @param color is the new hexcode of the color to change attribute to
    */
    public void updateAllColors(String[] colors) {
    	BACKGROUND_COLOR = colors[3];
    	BORDER_COLOR = colors[4];
    	HEADER_TEXT = colors[5];
    	REGULAR_TEXT = colors[6];
    	BUTTONS_COLOR = colors[7];
    	TASK_BACKGROUND_COLOR = colors[8];
    	TASK_BORDER = colors[9];
    	updateView();
    	}
    
    public void updateDisplayMode(int mode) {
    	this.displayMode = mode;
    	updateView();
    }
}