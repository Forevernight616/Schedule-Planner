import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class PlannerModelTest {
	private List<String> backup;

	@BeforeEach
	void saveAndWipeData() throws IOException {
		// back up existing username.csv
		Path path = Path.of("src/username.csv");
		backup = new ArrayList<>();
		if (Files.exists(path)) {
			this.backup = Files.readAllLines(path);
		}
		// wipe username.csv so each test starts clean
		Files.write(path, new ArrayList<>());
	}

	@AfterEach
	void revertTheData() throws IOException {
		// restore original username.csv
		Path path = Path.of("src/username.csv");
		Files.write(path, this.backup);
		Path userPath = Path.of("src/userData");
		// convert path to file object for listFiles
		// delete every tests
		for (File file : userPath.toFile().listFiles()) {
			if (file.getName().equals("john.csv") || file.getName().startsWith("testOnly")) {
				file.delete();
			}
		}
	}

	@Test
	void saveUserDataFailed(){
		PlannerModel model = new PlannerModel();
		model.saveUserData();
	}

	@Test
	void saveUserDataHasFile(){
		PlannerModel model = new PlannerModel();
		model.createUser("john", "1");
		model.getUser("john", "1");
		File file = new File("src/userData/john.csv");
		assertNotNull(file);
	}
	@Test
	void saveUserDataTrue(){
		PlannerModel model = new PlannerModel();
		model.createUser("john", "1");
		User user = model.getUser("john", "1");

		Calendar calendar = user.getCalendar();
		Day day = calendar.getFirstDate();
		day.addTask(1200, 1300, "h", "h");
		day.addTask(1200, 1300, "h", "");
		model.saveUserData();

		File file = new File("src/userData/john.csv");
		assertNotNull(file);
	}

	@Test
	void testCreateUser() throws IOException {
		// because of the weird way that username.csv connects everything, i cant delete fully
		// so this is doing : get username.csv, save it as a backup, clean the csv, add john and assert, clean the csv again and reinstall the previous data
		Path path = Path.of("src/username.csv");

		List<String> backup = new ArrayList<>();
		if (Files.exists(path)) {
			backup = Files.readAllLines(path);
		}

		try {
			Files.write(path, new ArrayList<>());
			PlannerModel model = new PlannerModel();
			assertTrue(model.createUser("john", "1"));

		} finally {
			Files.write(path, backup);
		}
	}
	@Test
	void testCreateUserFail() {
		PlannerModel model = new PlannerModel();
		model.createUser("john", "1");
		assertFalse(model.createUser("john", "1"));
	}

	@Test
	void testCreateUserEmptyUsername() {
		PlannerModel model = new PlannerModel();
		assertFalse(model.createUser("", "1"));
	}

	@Test
	void testCreateUserEmptyPassword() {
		PlannerModel model = new PlannerModel();
		assertFalse(model.createUser("1", ""));
	}

	@Test
	void testGetUser(){
		PlannerModel model = new PlannerModel();
		model.createUser("john", "1");
		User user = model.getUser("john", "1");
        assertNotNull(user);
	}

	@Test
	void testGetUserWrongName() {
		PlannerModel model = new PlannerModel();
		model.createUser("john", "1");
		User user = model.getUser("John", "2");
		assertNull(user);
	}

	@Test
	void testGetUserWrongPassword() {
		PlannerModel model = new PlannerModel();
		model.createUser("john", "1");
		User user = model.getUser("john", "2");
		assertNull(user);
	}

	@Test
	void testUserIsLoggedIn() {
		PlannerModel model = new PlannerModel();
		model.createUser("john", "1");
		model.getUser("john", "1");
		assertTrue(model.userIsLoggedIn());
	}

	@Test
	void testUserIsLoggedInFail() {
		PlannerModel model = new PlannerModel();
		model.createUser("john", "1");
		model.getUser("John", "1");
		assertFalse(model.userIsLoggedIn());
	}

	@Test
	void testLogOut() {
		PlannerModel model = new PlannerModel();
		model.createUser("john", "1");
		model.getUser("john", "1");
		model.logOut();
		assertFalse(model.userIsLoggedIn());
		assertNull(model.currentUser);
	}

	@Test
	void testReadFile(){
		PlannerModel model = new PlannerModel();
		model.createUser("john", "1");
		model.createUser("testOnlyJohn", "1");
		model.createUser("testOnlyJim", "1");
		model = new PlannerModel();
		assertTrue(true);
	}

	@Test
	void testSaveUserAll() throws IOException {
		PlannerModel model = new PlannerModel();
		model.createUser("john", "1");
		User user = model.getUser("john", "1");

		Day day = user.getCalendar().getFirstDate();
		day.addTask(1900, 2000, "h", "h");
		String[] colors = {"#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000"};
		model.saveUserAll("Morning.wav", 1, "#000000", colors);

		Path filePath = Path.of("src/userData/john.csv");
		List<String> lines = Files.readAllLines(filePath);

		assertTrue(lines.get(0).contains("Morning.wav"));
		assertTrue(lines.get(1).contains("h"));
	}

	@Test
	void testGetUserTrue() throws IOException {
		PlannerModel model = new PlannerModel();
		model.createUser("john", "1");
		User user = model.getUser("john", "1");

		Day day = user.getCalendar().getFirstDate();
		day.addTask(1900, 2000, "h", "h");
		day.addTask(2100, 2200, "h", "h");


		String[] colors = {"#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000"};
		model.saveUserAll("Morning.wav", 1, "#000000", colors);
		model.saveUserData();
		model.logOut();
		user = model.getUser("john", "1");
		assertNotNull(user);
	}

}
