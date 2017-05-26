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


public class JSON_Import
{
    public static void main (String args [])
    {
        System.out.println("Hello World");

        //Reading from JSON
        try
        {

            Gson gson = new Gson();
            String filename = "/home/reticent/Downloads/AllSets-x.json";
            
            JsonReader reader = new JsonReader(new FileReader(filename));
            Type t = new TypeToken<Map<String, MTG_Set>>(){}.getType();
            Map<String, MTG_Set> map = gson.fromJson(reader, t);

            System.out.println(map.get("LEA"));
            Card [] c = map.get("LEA").getCards();
            System.out.println(c[0].getName());
            

        }
        catch (Exception e)
        {
            System.out.println(e);
        }

    }
}