/*
 * John McCarthy
 * CIS217 W18
 * 2018-04-22
 * 
 * This program uses a Timer to
 * pull date/time information from
 * a website and write it to a
 * SQLite DB.
 * 
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Final
{
    public static void main(String[] args)
    {
	// 1 hr = 3600 sec
	// 30 min = 1800 sec
	// 15 min = 900 sec
	int seconds = 900;
	Timer timer = new Timer();
	//TimerTask(delayStart, timeBetweenExexutions) (note: 1000 = second)
	timer.scheduleAtFixedRate(new MyTimerTask(), 0, seconds * 1000);
	Scanner in = new Scanner(System.in);
	while (!in.hasNextLine())
	{
	}
	in.close();
	timer.cancel();
	System.out.println("Done");
    }
}

//http://www.iitk.ac.in/esc101/05Aug/tutorial/essential/threads/timer.html
//"A TimerTask implements Runnable and overrides the basic run() method 
//    to define a task that is called by theTimer
class MyTimerTask extends TimerTask
{
    int counter = 0;
    Connection conn = null;
    String time = "";

    public void run()
    {
	// What to do on timer tick
	counter++;
	System.out.println("Timer task executed " + counter + " times.\n Press <enter> to stop:");
	time = getTime();
	conn = openDatabase(conn, "Final.db");
	insertTime(conn, time);
    }

    public static String getTime()
    {
	String time = "";
	try
	{
	    String url = "https://www.timeanddate.com/worldclock/USA/GrandRapids";
	    Document document = Jsoup.connect(url).get();
	    //the line with time has a class = "h1"
	    time = document.getElementsByClass("h1").first().text();
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	return time;
    }

    static Connection openDatabase(Connection conn, String dbName)
    {
	try
	{
	    if (conn == null)
	    {
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
		String sql = "CREATE TABLE IF NOT EXISTS timeTable "
			+ "(id INTEGER PRIMARY KEY, timeTxt TEXT, time TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
		//e.g. of timestamp   time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
		Statement statement = conn.createStatement();
		statement.executeUpdate(sql);
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    System.out.println("Could not open/create database.");
	}
	return conn;
    }

    static void insertTime(Connection conn, String timeString)
    {
	String sql = "INSERT INTO timeTable(timeTxt) VALUES(?)";
	try
	{
	    PreparedStatement statement = conn.prepareStatement(sql);
	    statement.setString(1, timeString);
	    statement.executeUpdate();
	}
	catch (SQLException e)
	{
	    e.printStackTrace();
	}
    }
}