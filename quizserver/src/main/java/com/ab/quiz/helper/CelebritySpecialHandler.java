package com.ab.quiz.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ab.quiz.exceptions.NotAllowedException;
import com.ab.quiz.handlers.GameManager;
import com.ab.quiz.pojo.CelebrityDetails;
import com.ab.quiz.pojo.CelebrityFullDetails;
import com.ab.quiz.pojo.UpcomingCelebrity;

public class CelebritySpecialHandler {
	
	private static final Logger logger = LogManager.getLogger(CelebritySpecialHandler.class);
	private static CelebritySpecialHandler instance = null;
	private Properties props;
	
	private CelebritySpecialHandler() {
	}
	
	public static CelebritySpecialHandler getInstance() {
		if (instance == null) {
			logger.debug("In CelebritySpecialHandler getInstance() method instance created");
			instance = new CelebritySpecialHandler();
			instance.initialize();
		}
		return instance;
	}
	
	private void initialize() {
		
		String fileName = "celebrity.txt";
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(fileName);
		
	    try {
	    	props = new Properties();
	    	props.load(inputStream);
	    } catch(FileNotFoundException fnfe) {
	    	logger.error("File Not Found ", fnfe);
	    } catch(IOException ioe) {
	    	logger.error("IO Exception ", ioe);
	    } finally {
	    	try {
				inputStream.close();
			} catch (IOException e) {
				logger.error("IO Exception while closing the inputStream", e);
			}
	    }
	}
	
	public List<CelebrityDetails> getCelebrityDetails(long gameStartTime, int maxSize) throws NotAllowedException {
		List<CelebrityDetails> details = new ArrayList<>();
		String hour = getHourIn24HrsFormat(gameStartTime);
		
		String celebrityStr = props.getProperty(hour, null);
		if (celebrityStr == null) {
			throw new NotAllowedException("No entry found for :" + celebrityStr);
		}
		
		List<CelebrityDetails> fullDetails = convertStrToList(celebrityStr);
		if (fullDetails.size() < maxSize) {
			throw new NotAllowedException("Configured size is less than asked size : " + fullDetails.size() + ":" + maxSize);
		}
		for (int index = 0; index < maxSize; index ++) {
			details.add(fullDetails.get(index));
		}
		return details;
	}
	
	public List<UpcomingCelebrity> getUpcomingCelebrityDetails(int maxSize) {
		
		long currentTime = System.currentTimeMillis();
		String hour = getHourIn24HrsFormat(currentTime);
		int hourInt = Integer.valueOf(hour);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(currentTime);
		calendar.set(Calendar.HOUR_OF_DAY, (hourInt % 24));
		calendar.set(Calendar.MINUTE, 5);
		calendar.set(Calendar.SECOND, 0);
		long nextStartTime = calendar.getTimeInMillis();
		
		List<UpcomingCelebrity> details = new ArrayList<>();
		
		for (int index = hourInt + 1; index <= 23; index ++) {
			
			nextStartTime = nextStartTime + (60 * 60 * 1000);
			UpcomingCelebrity scheduledCeleb = new UpcomingCelebrity();
			List<String> celebNames = new ArrayList<>();
			
			String celebrityStr = props.getProperty(String.valueOf(index), "");
			celebrityStr = celebrityStr.trim();
			
			StringTokenizer strTokenizer1 = new StringTokenizer(celebrityStr, ":");
			
			while (strTokenizer1.hasMoreTokens()) {
				String eachTimeLine = strTokenizer1.nextToken();
				eachTimeLine = eachTimeLine.trim();
				
				StringTokenizer strTokenizer2 = new StringTokenizer(eachTimeLine, ",");
				celebNames.add(strTokenizer2.nextToken());
			}
			
			List<String> finalCelebNames = new ArrayList<>();
			for (int i = 0; i < maxSize; i ++) {
				finalCelebNames.add(celebNames.get(i));
			}
			List<String> sortedNames = finalCelebNames.stream().sorted().collect(Collectors.toList());
			scheduledCeleb.setCelebrityNames(sortedNames);
			scheduledCeleb.setHourVal(String.valueOf(index));
			scheduledCeleb.setGameStartTime(new Date(nextStartTime).toString());
			
			details.add(scheduledCeleb);
		}
		
		for (int index = 0; index < hourInt; index ++) {
			nextStartTime = nextStartTime + (60 * 60 * 1000);
			
			UpcomingCelebrity scheduledCeleb = new UpcomingCelebrity();
			List<String> celebNames = new ArrayList<>();
			
			String celebrityStr = props.getProperty(String.valueOf(index), "");
			celebrityStr = celebrityStr.trim();
			
			StringTokenizer strTokenizer1 = new StringTokenizer(celebrityStr, ":");
			
			while (strTokenizer1.hasMoreTokens()) {
				String eachTimeLine = strTokenizer1.nextToken();
				eachTimeLine = eachTimeLine.trim();
				
				StringTokenizer strTokenizer2 = new StringTokenizer(eachTimeLine, ",");
				celebNames.add(strTokenizer2.nextToken());
			}
			
			List<String> finalCelebNames = new ArrayList<>();
			for (int i = 0; i < maxSize; i ++) {
				finalCelebNames.add(celebNames.get(i));
			}
			List<String> sortedNames = finalCelebNames.stream().sorted().collect(Collectors.toList());
			scheduledCeleb.setCelebrityNames(sortedNames);
			scheduledCeleb.setHourVal(String.valueOf(index));
			scheduledCeleb.setGameStartTime(new Date(nextStartTime).toString());
			details.add(scheduledCeleb);
		}
		return details;
	}
	
	private String getHourIn24HrsFormat(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		int hrs = calendar.get(Calendar.HOUR_OF_DAY);
		return String.valueOf(hrs);
	}
	
	private List<CelebrityDetails> convertStrToList(String str) {
		str = str.trim();
		List<CelebrityDetails> details = new ArrayList<>();
		
		StringTokenizer strTokenizer1 = new StringTokenizer(str, ":");
		
		while (strTokenizer1.hasMoreTokens()) {
			String eachCelebrity = strTokenizer1.nextToken();
			eachCelebrity = eachCelebrity.trim();
			
			StringTokenizer strTokenizer2 = new StringTokenizer(eachCelebrity, ",");
			
			CelebrityDetails celebDetail = new CelebrityDetails();
			celebDetail.setName(strTokenizer2.nextToken());
			celebDetail.setCode(Integer.valueOf(strTokenizer2.nextToken()));
			
			details.add(celebDetail);
		}
		Collections.sort(details, new Comparator<CelebrityDetails>() {
			public int compare(final CelebrityDetails summary1, final CelebrityDetails summary2) {
				return summary1.getName().compareTo(summary2.getName());
			}
		});
		return details;
	}
	
	public static void main(String[] args) throws NotAllowedException {
		
		CelebritySpecialHandler handler = CelebritySpecialHandler.getInstance();
		
		List<CelebrityDetails> details = handler.getCelebrityDetails(System.currentTimeMillis(), 21);
		for (CelebrityDetails cd: details) {
			System.out.println(cd);
		}
		
		List<UpcomingCelebrity> upcomingEntries = handler.getUpcomingCelebrityDetails(3); 
		for (UpcomingCelebrity str : upcomingEntries) {
			System.out.println(str);
		}
		
		GameManager gameManager = GameManager.getInstance();
		CelebrityFullDetails list = gameManager.getCelebrityFullDetails();
		System.out.println(list);
	}
}
