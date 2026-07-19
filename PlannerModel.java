/**
 * @author Khanh Nguyen
 * @author Duc Tan Tran
 * @author Paloma Ortiz
 * @author Braa Oudeh
 * Purpose: This program is the model of Planner. It contains information about the all of the users, their respective files for user data.
 * 		The model also has some flags that handle with user's login.
 * 		User Persistency is implemented here, user credentials are stored in "src/username.csv". Each user's tasks are stored in their
 * 		respective file.
 */
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.HashMap;

public class PlannerModel {
	HashMap<String, String> allUsers;
	User currentUser;
	private final String SENTINEL = "`<>~><~><>?><}{,";
	private final String FILENAME = "src/username.csv";
	private String[] settings;
	private final String DEFAULT_MUSIC = "Morning.wav";
	private final int DEFAULT_MODE = 2;
	private final String DEFAULT_HEADER_BACKGROUND = "#2b2b2b";
	private final String DEFAULT_BACKGROUND = "#3f3f3f";
	private final String DEFAULT_BORDER = "#5f5f5f";
	private final String DEFAULT_HEADER_TEXT = "#dcdccc";
	private final String DEFAULT_REGULAR_TEXT = "#9f9f9f";
	private final String DEFAULT_BUTTONS = "#f0dfaf";
	private final String DEFAULT_TASK_BACKGROUND = "#f5f5dc";
	private final String DEFAULT_TASK_BORDER = "#93e0e3";

	/**
	 * This is the constructor.
	 */
	public PlannerModel(){
		allUsers = new HashMap<>();
		currentUser = null;	
		readFile();
	}

	/**
	 * hashPassword() -- Hash the user's password
	 * @param password - user password
	 * @return - String of hashed user password
	 */
	private String hashPassword(String password) {
		// TODO add proper Hashing
		return password;
	}
	
	/**
	 * saveUserData() -- save the user data
	 */
	public void saveUserData() {
		if (currentUser==null) return;
		StringBuilder content= new StringBuilder();
		String username = currentUser.getUsername();
		String userFileName = username + ".csv";
		Day pointer = currentUser.getCalendar().getFirstDate();
		while (pointer!=null) {
			if (!pointer.isEmpty()) {
				for (Task task: pointer.getTaskList()) {
					content.append(task.getTitle());
					content.append(SENTINEL);
					content.append(String.valueOf(pointer.getDateEncode()));
					content.append(SENTINEL);
					content.append(String.valueOf(task.getTimeStart()));
					content.append(SENTINEL);
					content.append(String.valueOf(task.getTimeEnd()));
					content.append(SENTINEL);
					content.append(String.valueOf(task.getDescription()) + "\n");
				}
			}
			pointer=pointer.getNextDay();
		}
		try {
			Files.writeString(Path.of("src/userData/"+userFileName), content.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void saveUserAll(String music, int displayMode, String headerBackground ,  String[] color) {
		if (currentUser==null) return;
		StringBuilder content= new StringBuilder();
		String username = currentUser.getUsername();
		String userFileName = username + ".csv";
		Day pointer = currentUser.getCalendar().getFirstDate();
		content.append(music);
		content.append(SENTINEL);
		content.append(String.valueOf(displayMode));
		content.append(SENTINEL);
		content.append(String.valueOf(color[0]));
		content.append(SENTINEL);
		content.append(String.valueOf(color[1]));
		content.append(SENTINEL);
		content.append(String.valueOf(color[2]));
		content.append(SENTINEL);
		content.append(String.valueOf(color[3]));
		content.append(SENTINEL);
		content.append(String.valueOf(color[4]));
		content.append(SENTINEL);
		content.append(String.valueOf(color[5]));
		content.append(SENTINEL);
		content.append(String.valueOf(color[6]));
		content.append(SENTINEL);
		content.append(String.valueOf(color[7]));
		content.append("\n");
		while (pointer!=null) {
			if (!pointer.isEmpty()) {
				for (Task task: pointer.getTaskList()) {
					content.append(task.getTitle());
					content.append(SENTINEL);
					content.append(String.valueOf(pointer.getDateEncode()));
					content.append(SENTINEL);
					content.append(String.valueOf(task.getTimeStart()));
					content.append(SENTINEL);
					content.append(String.valueOf(task.getTimeEnd()));
					content.append(SENTINEL);
					content.append(String.valueOf(task.getDescription()) + "\n");
				}
			}
			pointer=pointer.getNextDay();
		}
		try {
			Files.writeString(Path.of("src/userData/"+userFileName), content.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * getSettings() -- returns the various settings stored in userFile
	 * @return
	 */
	public String[] getSettings() {
		return settings;
	}
	
	/**
	 * readFile() -- Reads stored user data their file
	 */
	private void readFile() {
		try {
			File userFile = new File(FILENAME);
			Scanner scanner = new Scanner(userFile);
			while (scanner.hasNextLine()) {
				String[] line = scanner.nextLine().strip().split(", ");
				String username = line[0];
				String passHash = line[1];
				allUsers.put(username,  passHash);
			}
			scanner.close();
		}
		catch(FileNotFoundException e) {
			try {
				new File(FILENAME).createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * createUser -- Creates a new user account (including the file)
	 * @param username - user name
	 * @param password - user password
	 * @return true if the user was successfully created, false otherwise
	 */
	public boolean createUser(String username, String password) {
		if(username == "") {
			return false;
		}
		if(password == "") {
			return false;
		}
		
		if (allUsers.keySet().contains(username)) {
			return false;
		}
		else {
			File userFile = new File(FILENAME);
			try {
				String hash = hashPassword(password);
				FileWriter write = new FileWriter(userFile, true);
				write.append(username + ", " + hash + "\n");
				new File("src/userData").mkdirs();
				new File("src/userData/" + username+".csv").createNewFile();
				FileWriter writeData = new FileWriter("src/userData/" + username+".csv", true);
				writeData.append(DEFAULT_MUSIC);
				writeData.append(SENTINEL);
				writeData.append(String.valueOf(DEFAULT_MODE));
				writeData.append(SENTINEL);
				writeData.append(String.valueOf(DEFAULT_HEADER_BACKGROUND));
				writeData.append(SENTINEL);
				writeData.append(String.valueOf(DEFAULT_BACKGROUND));
				writeData.append(SENTINEL);
				writeData.append(String.valueOf(DEFAULT_BORDER));
				writeData.append(SENTINEL);
				writeData.append(String.valueOf(DEFAULT_HEADER_TEXT));
				writeData.append(SENTINEL);
				writeData.append(String.valueOf(DEFAULT_REGULAR_TEXT));
				writeData.append(SENTINEL);
				writeData.append(String.valueOf(DEFAULT_BUTTONS));
				writeData.append(SENTINEL);
				writeData.append(String.valueOf(DEFAULT_TASK_BACKGROUND));
				writeData.append(SENTINEL);
				writeData.append(String.valueOf(DEFAULT_TASK_BORDER));
				writeData.append("\n");
				writeData.close();
				write.close();
				allUsers.put(username, hash);
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		return true;
	}

	/**
	 * getUser() -- Authenticate and load user data and settings
	 * @param username - the user name
	 * @param password - the user password
	 * @return the User object if authentication completes, null otherwise
	 */
	public User getUser(String username, String password) { 
		String hash = hashPassword(password);
		// fixed bug where after createUser, getUser never returns null
		if(allUsers.containsKey(username) && allUsers.get(username).equals(hash)) {
			File currentUserFile = new File("src/userData/" + username+".csv");
			try {
				currentUser = new User(username, hash); 
				Scanner scanner= new Scanner(currentUserFile);
				settings = scanner.nextLine().strip().split(Pattern.quote(SENTINEL));
				while(scanner.hasNextLine()) {
					String[] data = scanner.nextLine().strip().split(Pattern.quote(SENTINEL));
					int dayEncode = Integer.parseInt(data[1]);
					int timeStart = Integer.parseInt(data[2]);
					int timeEnd = Integer.parseInt(data[3]);
					if (data.length == 5) {
						currentUser.addTask(dayEncode, timeStart, timeEnd, data[0], data[4]);
					}
					else {
						currentUser.addTask(dayEncode, timeStart, timeEnd, data[0], "");
					}
					
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return currentUser;
		
	}


	
	/**
	 * logOut, and save current user
	 */
	public void logOut() {
		saveUserData();
		currentUser = null;
	}

	/**
	 * userIsLoggedIn() -- looks at currentUser flag and checks whether a user is currently logged in.
	 * @return
	 */
	public boolean userIsLoggedIn(){
		if (currentUser == null){
			return false;
		}
		return true;
	}
}
