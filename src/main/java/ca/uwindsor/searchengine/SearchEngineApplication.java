package ca.uwindsor.searchengine;

import ca.uwindsor.searchengine.services.CrawlerService;

import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class SearchEngineApplication {
	public static void main(String[] args) throws Exception {
		System.out.println("**********************************************************************");
		System.out.println("                                                                      ");
		System.out.println("                Welcome to the search engine application              ");
		System.out.println("                                                                      ");
		System.out.println("**********************************************************************");
		System.out.println("                           Team Members                               ");
		System.out.println("                                                                      ");
		System.out.println("                    Hetvi Khirsaria & Nirali Amrutiya                 ");
		System.out.println("                                                                      ");
//		System.out.println("**********************************************************************");
//		System.out.println("                                                                      ");

		while (true) {
			try {
				System.out.println("**********************************************************************");
				System.out.println("                                                                      ");
				System.out.println("                       Please choose operation...                     ");
				System.out.println("                                                                      ");
				System.out.println("**********************************************************************");
				System.out.println("                                                                      ");
				System.out.println("1 -> Search test");
				System.out.println("2 -> Register new webpage in search engine");
				System.out.println("3 -> Exit application");

				int option = getIntInput();
				if (option == 1) {
					searchTextMenu();
				} else if (option == 2) {
					registerUrlMenu();
				} else if (option == 3) {
					break;
				} else {
					System.out.println("Incorrect option...");
					Thread.sleep(3000);
				}
			}
			catch (Exception ex){
				System.out.println(ex.getMessage());
				Thread.sleep(3000);
			}

			clearConsole();
		}

		System.out.println("Closing application...");
		Thread.sleep(3000);
	}

	public static void registerUrlMenu() throws InterruptedException {
		System.out.println("Please enter URL to register new webpage with search engine:");

		try {
			clearConsole();

			String url = getUrlInput();
			if(Objects.equals(url, "")) {
				throw new Exception("Invalid input...");
			}

			CrawlerService crawlerService = new CrawlerService();
			crawlerService.parseWebpage(url);
			System.out.println("URL registered successfully.");
			System.out.println("                                                                      ");
			System.out.println("Redirecting to main menu in 3 second.");
			Thread.sleep(3000);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			System.out.println("Fail to register URL with search engine.");
			System.out.println("                                                                      ");
			System.out.println("Redirecting to main menu in 3 second.");
			Thread.sleep(3000);
		}
	}

	public static void searchTextMenu() throws InterruptedException {
		try {
			int searchResultMenuOption = 1;
			while (searchResultMenuOption == 1){
				clearConsole();

				System.out.println("Please enter search text:");
				String searchText = getStringInput();

				CrawlerService crawlerService = new CrawlerService();
				HashMap<String, Integer> searchResults = crawlerService.search(searchText);

				if(searchResults.isEmpty())
					System.out.println("No search matched results found.....");
				else {
					System.out.println("Results:");
					System.out.println(searchResults);
				}

				System.out.println("Please choose operation...");
				System.out.println("                                                                      ");
				System.out.println("1 -> Search new result");
				System.out.println("2 -> Redirect to main menu");

				searchResultMenuOption = getIntInput();
				if(searchResultMenuOption == 0)
					break;
			}

			System.out.println("Redirecting to main menu in 3 second.");
			Thread.sleep(3000);
		}
		catch (Exception ex){
			System.out.println("Fail to register URL with search engine.");
			System.out.println("Redirecting to main menu in 3 second.");
			Thread.sleep(3000);
		}
	}

	public static void clearConsole() throws Exception {
		String operatingSystem = System.getProperty("os.name");

		if(operatingSystem.contains("Windows")){
			ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
			Process startProcess = pb.inheritIO().start();
			startProcess.waitFor();
		} else {
			ProcessBuilder pb = new ProcessBuilder("clear");
			Process startProcess = pb.inheritIO().start();

			startProcess.waitFor();
		}
	}

	public static int getIntInput() throws Exception {
		try {
			Scanner scanner = new Scanner(System.in);
			return scanner.nextInt();
		}
		catch (Exception ex){
			throw new Exception("Invalid input...");
		}
	}

	public static String getStringInput() throws Exception {
		try {
			Scanner scanner = new Scanner(System.in);
			return scanner.nextLine();
		}
		catch (Exception ex){
			throw new Exception("Invalid input...");
		}
	}

	public static String getUrlInput() throws Exception {
		try {
			Scanner scanner = new Scanner(System.in);
			return scanner.next("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		}
		catch (Exception ex){
			throw new Exception("Invalid input...");
		}
	}
}