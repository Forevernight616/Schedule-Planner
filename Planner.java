/**
 * @author Braa Oudeh
 * @author Duc Tan Tran
 * @author Khanh Nguyen
 * @author Paloma Ortiz
 * Purpose: This is the main file for our planner project. It starts up the front end view, and the model and controller also start from there.
 * 		For a full implementation of the program (including networking), run PlannerServer, then run as many instances of planner as you want at the same time.
 * 		User data of the program will be stored in username.csv, and the userData/ folder.
 */
import javafx.application.Application;

public class Planner {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Application.launch(PlannerView.class, args);
	}
}
