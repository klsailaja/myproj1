package com.ab.quiz;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ab.quiz.constants.QuizConstants;
import com.ab.quiz.helper.GamesGenerator;
import com.ab.quiz.helper.WinMsgHandler;
import com.ab.quiz.tasks.TestUsersTask;

@SpringBootApplication
public class TeluguMovieQuizApplication implements ApplicationRunner {
	
	private static final Logger logger = LogManager.getLogger(TeluguMovieQuizApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(TeluguMovieQuizApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		try {
			
			logger.debug("Starting the TeluguMovieQuizApplication application");
			
			GamesGenerator gameGenerator1 = new GamesGenerator(1);
			gameGenerator1.initialize();
			
			GamesGenerator gameGenerator2 = new GamesGenerator(2);
			gameGenerator2.initialize();
			
			boolean first = false;
			if (gameGenerator1.getFirstGameTime() < gameGenerator2.getFirstGameTime()) {
				first = true;
			}
			
			for (int index = 1; index <= QuizConstants.MAX_LIVE_SLOTS; index++) {
				if (first) {
					gameGenerator1.buildInitialGameSet();
					gameGenerator2.buildInitialGameSet();
				} else {
					gameGenerator2.buildInitialGameSet();
					gameGenerator1.buildInitialGameSet();
				}
			}
			
			int inMemSlots = 2 * QuizConstants.MAX_LIVE_SLOTS;
			for (int index = 1; index <= inMemSlots; index++) {
				if (first) {
					gameGenerator1.buildNextGameSet();
					gameGenerator2.buildNextGameSet();
				} else {
					gameGenerator2.buildNextGameSet();
					gameGenerator1.buildNextGameSet();
				}
			}
			
			gameGenerator1.setupGames();
			gameGenerator2.setupGames();
			
			logger.info("Server started successfully...");
			
			WinMsgHandler.getInstance();
			if (QuizConstants.TESTMODE == 1) {
				TestUsersTask task = new TestUsersTask();
				task.setUp();
			}
		} catch(SQLException ex) {
			logger.error("SQLException in TeluguMovieQuizApplication", ex);
		}

	}
}
