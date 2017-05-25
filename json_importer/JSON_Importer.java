/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json_importer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Map;
/**
 *
 * @author reticent
 */
public class JSON_Importer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Declared Variables
        Gson gson = new Gson();
        String filename =  "/home/reticent/Downloads/AllSets-x.json";
        try
        {
            JsonReader reader = new JsonReader(new FileReader(filename));
//            JsonParser parser = new JsonParser();
//            JsonObject o = parser.parse(reader).getAsJsonObject();
            Type t = new TypeToken<Map<String, MTG_Set>>(){}.getType();
            Map<String, MTG_Set> map = gson.fromJson(reader, t);
            
            Card [] c = map.get("MM3").getCards();
            Legality [] l = c[1].getLegalities();
            Ruling [] r = c[4].getRulings();
            System.out.println(r[0].getText());
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
