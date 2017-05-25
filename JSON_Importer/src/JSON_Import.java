/*
Name: Carlos A. Rios
Date: May 22, 2017
Purpose: The purpose of this program is to parse all the information from the .JSON for the card game,
         Magic the Gathering, and then import the information to the MYSQL database. 
         

          javac -cp gson.jar:/home/reticent/netbeans-8.2/ide/modules/ext/mysql-connector-java-5.1.23-bin.jar: -d ./ main.java
          java -cp gson.jar:/home/reticent/netbeans-8.2/ide/modules/ext/mysql-connector-java-5.1.23-bin.jar: testing/main
*/

package JSON_Import;

import java.io.FileReader;
import com.google.gson.Gson;
import java.io.BufferedReader;
import com.google.gson.stream.JsonReader;
import java.util.*;
import com.google.gson.GsonBuilder;



public class JSON_Import
{
    public static void main (String args [])
    {
        System.out.println("Hello World");

        //Reading from JSON
        try
        {

            Gson gson = new GsonBuilder().create();
            String filename = "/home/reticent/Downloads/AllSets-x.json";

            // BufferedReader br = new BufferedReader(
            //     new FileReader("/home/reticent/Downloads/AllSets-x.json") );
            
            JsonReader reader = new JsonReader(new FileReader(filename));
            MTG_Set o = gson.fromJson(reader, MTG_Set.class);

           
            //System.out.println(reader.nextString());
            System.out.println(o);
            

        }
        catch (Exception e)
        {
            System.out.println(e);
        }

    }
}