/*
Name: Carlos A. Rios
Date: May 22, 2017
Purpose: The purpose of this program is to parse all the information from the .JSON for the card game,
         Magic the Gathering, and then import the information to the MYSQL database. 
         
         //EXAMPLE OF RUNNING THIS WITH MYSQL CONNCETOR IN DIFF FOLDER
          javac -cp gson.jar:/home/reticent/netbeans-8.2/ide/modules/ext/mysql-connector-java-5.1.23-bin.jar: -d ./ main.java
          java -cp gson.jar:/home/reticent/netbeans-8.2/ide/modules/ext/mysql-connector-java-5.1.23-bin.jar: testing/main

          //TO RUN FROM THIS FOLDER (Folder JSON_Importer/src)
          1) javac -cp JAR/gson.jar:JAR/mysql-connector-java-5.1.23-bin.jar: -d ./ JSON_Import.java
          2) java JSON_Import/JSON_Import
*/

package JSON_Import;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class JSON_Import
{
    public static void main (String args [])
    {
       //Declared Variables
        Gson gson = new Gson();
        Connection connect = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String filename =  "/home/reticent/Downloads/AllSets-x.json";   //Filepath to MTGJson file
        
        try
        {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/mtg_dbm?" + "user=root&password=q1w2e3r4");
            JsonReader reader = new JsonReader(new FileReader(filename));
            Type t = new TypeToken<Map<String, MTG_Set>>(){}.getType();
            Map<String, MTG_Set> map = gson.fromJson(reader, t);
            
            //Iterating through map
            for (Map.Entry<String, MTG_Set> entry : map.entrySet())
            {
                int i = entry.getValue().getCards().length; //Length of cards in Set
                
                System.out.println(entry.getKey() + "/" + entry.getValue().getName());
                
                for (int x = 0; x < i; x++)
                {
                    if (entry.getValue().getCards()[x].getRulings() != null)
                        System.out.println(entry.getValue().getCards()[x].getName());
                }
            }      
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}