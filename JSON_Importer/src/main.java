// Name: Carlos A. Rios
// Date: Mar 8, 2017
// Purpose: The purpose of this program is to learn how to look up data and pull that data
//          from the MTG JSON file. Also, to add this information into MYSQL Database.
//
// NOTES TO MYSELF: To run this program (through command lines, use the following lines
//          Compile:
//              javac -cp gson.jar:/home/reticent/netbeans-8.2/ide/modules/ext/mysql-connector-java-5.1.23-bin.jar: -d ./ main.java
//          Running:
//              java -cp gson.jar:/home/reticent/netbeans-8.2/ide/modules/ext/mysql-connector-java-5.1.23-bin.jar: testing/main
//      *ASSUMING THAT YOU ARE IN FOLDER THAT MAIN.JAVA IS IN (which is .../Testing/src)
//      *Replace .jar paths with wherever .jar file is found on computer & the dir for the JSON file
//      * .jar files include the Google GSON.jar file & the mysql connector .jar file
//      *Total of 32519 Cards | only 30279 have multiverseid (Difference of 2218)


package testing;

import java.io.FileReader;
import java.io.IOException;
import com.google.gson.Gson;  
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author reticent
 */
public class main {
    
    public static void main (String args[])
    {
        System.out.println("Hello World!");
        
        //************************************************/
        //Declared Variables
        //************************************************/

        //Set information
        ArrayList<String> setName = new ArrayList<String>();    //Name of set
        ArrayList<String> code = new ArrayList<String>();   //Set abbreviated code
        ArrayList<String> releaseDate = new ArrayList<String>();   //Year relased YYYY-MM-DD
        ArrayList<String> setType = new ArrayList<String>();    //Type of set
        ArrayList<String> block = new ArrayList<String>();  //Block the set is in

        //Card information
        ArrayList<String> cardName = new ArrayList<String>();  //Holds name of all cards
        ArrayList<Integer> cardID = new ArrayList<Integer>();   //Holds multiverseid of all card
        ArrayList<ArrayList<String>> names = new ArrayList<ArrayList<String>>();  //Holds names of double-side/split cards
        ArrayList<String> layout = new ArrayList<String>();  //Holds layout of card
        ArrayList<String> manaCost = new ArrayList<String>();    //Holds mana cost of card
        ArrayList<Float> cmc = new ArrayList<Float>();  //Holds converted mana cost of card
        ArrayList<ArrayList<String>> colors = new ArrayList<ArrayList<String>>(); //Holds card colors (derived from casting cost and keywords)
        ArrayList<ArrayList<String>> colorIdentity = new ArrayList<ArrayList<String>>();    //Holds cards color information and costs (Used for commander)
        ArrayList<String> type = new ArrayList<String>();   //Holds the card type
        ArrayList<ArrayList<String>> supertypes = new ArrayList<ArrayList<String>>();   //Holds the supertypes of the card
        ArrayList<ArrayList<String>> types = new ArrayList<ArrayList<String>>();    //Holds the types of the card
        ArrayList<ArrayList<String>> subtypes = new ArrayList<ArrayList<String>>(); //Holds the subtypes of the card
        ArrayList<String> rarity = new ArrayList<String>(); //Rarity of the card
        ArrayList<String> text = new ArrayList<String>();   //Text of the card
        ArrayList<String> flavor = new ArrayList<String>(); //Flavor text of the card
        ArrayList<String> artist = new ArrayList<String>(); //The artist of the card
        ArrayList<String> power = new ArrayList<String>();  //Power of the card (Only on creatures)
        ArrayList<String> toughness = new ArrayList<String>();  //Toughness of the card (Only on creatures)
        ArrayList<Integer> loyalty = new ArrayList<Integer>();  //Loyalty of the card (Only on planeswalkers)
        ArrayList<String> cardSet = new ArrayList<String>();    //Name of set that card came from.
        ArrayList<ArrayList<String>> banDate = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> banCard = new ArrayList<ArrayList<String>>();

        //Type Information
        ArrayList<String> typeName = new ArrayList<String>();   //Holds Name of the type
        ArrayList<String> typeT = new ArrayList<String>();  //Holds what kind of type the name is (Supertype, Subtype, or Type)

        //Ruling Information
        ArrayList<ArrayList<String>> rulings = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> rulingsDate = new ArrayList<ArrayList<String>>();

        //Legality Information (Card)
        ArrayList<ArrayList<String>> legality = new ArrayList<ArrayList<String>>(); //Card Legality in Format
        ArrayList<ArrayList<String>> format = new ArrayList<ArrayList<String>>();   //Card Format (Legality)
        ArrayList<String> listFormat = new ArrayList<String>(); //Names of Formats

        //Reading from JSON
        try
        {            
            //Declared Variables
            ArrayList <String> ls = new ArrayList<String>();    //Holds code name for all sets
            Set s;  //Temporarily holds the JObject as a set to substring the code name for each set
            Gson gson = new Gson();

            BufferedReader br = new BufferedReader(
                new FileReader("/home/reticent/Downloads/AllSets-x.json")); //BufferedReader to read from JSON file

            JsonParser parser = new JsonParser();
            JsonObject test = parser.parse(br).getAsJsonObject();
            s = test.entrySet();

            //Subtring each set code name to be able to pull from JSON File
            for (Object o : s)
            {   
                String str = o.toString();
                ls.add(str.substring(0,str.indexOf("=")));

                //System.out.println(str.substring(0,str.indexOf("=")));
            }

            //******************************************************************************************************
            // Iterating through each set to gather information
            //*******************************************************************************************************
            for (int x = 0; x < ls.size(); x++) //1st For used to iterate through each set in ArrayList ls
            {
                //Instanced Variables
                JsonObject set = test.getAsJsonObject(ls.get(x));   //Json Object of specific set
                JsonArray card = set.getAsJsonArray("cards");   //Json Array of the all cards in specific set
                int size_of_card = card.size(); // # of cards within set

                //****************************************************************************************
                // Gathering Set Information (Set Name, Code, Released Date, Set Type, and Block)
                //****************************************************************************************
                setName.add(set.get("name").getAsString()); //Add name of set to ArrayList setName
                code.add(set.get("code").getAsString()); //Add code of set to ArrayList code
                releaseDate.add(set.get("releaseDate").getAsString()); //Add release date of set to ArrayList releaseDate
                setType.add(set.get("type").getAsString()); //Add set type to ArrayList setType

                if (set.get("block")!= null)
                {
                    block.add(set.get("block").getAsString()); //Add block to ArrayList block
                }
                else
                {
                    block.add(null); //Add block to ArrayList block
                }

                //*****************************************************************************************
                // Gathering Card Information from each set with each iteration
                //*****************************************************************************************
                for (int i = 0; i < size_of_card; i++)  
                {
                    JsonElement c = card.get(i);    //JsonElement of card

                    if (c.getAsJsonObject().get("multiverseid") != null)    //If card has multiverseid, then add to ArrayList
                    {
                        cardName.add(c.getAsJsonObject().get("name").getAsString());   //Add name of card to ArrayList cardName
                        cardID.add(c.getAsJsonObject().get("multiverseid").getAsInt()); //Adds  multiverseid to ArrayList cardID
                        layout.add(c.getAsJsonObject().get("layout").getAsString());    //Adds card layout to ArrayList layout
                        type.add(c.getAsJsonObject().get("type").getAsString());    //Adds card type to ArrayList type
                        rarity.add(c.getAsJsonObject().get("rarity").getAsString());    //Adds card rarity to ArrayList raritys
                        artist.add(c.getAsJsonObject().get("artist").getAsString());    //Adds artist to the ArrayList artist
                        cardSet.add(set.get("name").getAsString()); //Adds set which card belongs to the ArrayList cardSet

                        //********************************************************* */
                        //Checks if Card has rulings
                        //********************************************************* */
                        if (c.getAsJsonObject().get("rulings") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("rulings").getAsJsonArray();   //JsonArray of card rulings
                            int size = a.size();    //Size of card rulings
                            ArrayList<String> l = new ArrayList<String>();  //Stores Ruling text
                            ArrayList<String> d = new ArrayList<String>();  //Stores Date of Ruling

                            for (int counter = 0; counter < size; counter++)
                            {
                                l.add(a.get(counter).getAsJsonObject().get("text").getAsString());
                                d.add(a.get(counter).getAsJsonObject().get("date").getAsString());
                            }
                            rulings.add(l);
                            rulingsDate.add(d);
                        }
                        else if (c.getAsJsonObject().get("rulings") == null)
                        {
                            rulings.add(null);
                            rulingsDate.add(null);
                        }

                        //********************************************************* */
                        //Checks if Card has Legality
                        //********************************************************* */
                        if (c.getAsJsonObject().get("legalities") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("legalities").getAsJsonArray();   //JsonArray of card rulings
                            int size = a.size();    //Size of card rulings
                            ArrayList<String> f = new ArrayList<String>();  //Stores Ruling text
                            ArrayList<String> l = new ArrayList<String>();  //Stores Date of Ruling

                            for (int counter = 0; counter < size; counter++)
                            {
                                f.add(a.get(counter).getAsJsonObject().get("format").getAsString());
                                l.add(a.get(counter).getAsJsonObject().get("legality").getAsString());
                            }
                            legality.add(l);
                            format.add(f);
                        }
                        else if (c.getAsJsonObject().get("legalities") == null)
                        {
                            legality.add(null);
                            format.add(null);
                        }

                        //****************************************************** */
                        // Checks if manaCost is not null then adds to manCost ArrayList 
                        //****************************************************** */
                        if (c.getAsJsonObject().get("manaCost") != null)
                        {
                            manaCost.add(c.getAsJsonObject().get("manaCost").getAsString());
                        }
                        else if (c.getAsJsonObject().get("manaCost") == null)
                        {
                            manaCost.add(null);
                        }

                        //****************************************************** */
                        // Checks if converted Mana Cost (cmc) is not null 
                        // then adds to cmc ArrayList.
                        //****************************************************** */
                        if (c.getAsJsonObject().get("cmc") != null)
                        {
                            cmc.add(c.getAsJsonObject().get("cmc").getAsFloat());
                        }
                        else if (c.getAsJsonObject().get("cmc") == null)
                        {
                            cmc.add(null);
                        }

                        //********************************************************
                        // Gathers "names" field from card that are split/flip cards
                        //********************************************************
                        if (c.getAsJsonObject().get("names") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("names").getAsJsonArray();   //JsonArray of cards names (for split/flip cards)
                            int size = a.size();    //Size of card names
                            ArrayList<String> l = new ArrayList<String>();

                            for (int counter = 0; counter < size; counter++)
                            {
                                l.add(a.get(counter).getAsString());
                            }
                            names.add(l);
                        }
                        else if (c.getAsJsonObject().get("names") == null)
                        {
                              names.add(null);
                        }

                        //****************************************************** */
                        // Checks if colors is not null 
                        // then adds to colors ArrayList 
                        //****************************************************** */
                        if (c.getAsJsonObject().get("colors") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("colors").getAsJsonArray();
                            int size = a.size();    //Size of colors in card
                            ArrayList<String> l = new ArrayList<String>();

                            for (int counter = 0; counter < size; counter++)
                            {
                                l.add(a.get(counter).getAsString());
                            }
                            colors.add(l);
                            
                        }
                        else if (c.getAsJsonObject().get("colors") == null)
                        {
                            colors.add(null);
                        }

                        //****************************************************** */
                        // Checks if colorsIdentity is not null 
                        // then adds to colorsIdentity ArrayList 
                        //****************************************************** */
                        if (c.getAsJsonObject().get("colorIdentity") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("colorIdentity").getAsJsonArray();
                            int size = a.size();    //Size of colorIdentity in card
                            ArrayList<String> l = new ArrayList<String>();

                            for (int counter = 0; counter < size; counter++)
                            {
                                l.add(a.get(counter).getAsString());
                            }
                            colorIdentity.add(l);

                        }
                        else if (c.getAsJsonObject().get("colorIdentity") == null)
                        {
                            colorIdentity.add(null);
                        }

                        //****************************************************** */
                        // Checks if supertypes is not null 
                        // then adds to supertypes ArrayList 
                        //****************************************************** */
                        if (c.getAsJsonObject().get("supertypes") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("supertypes").getAsJsonArray();
                            int size = a.size();    //Size of supertypes in card
                            ArrayList<String> l = new ArrayList<String>();

                            for (int counter = 0; counter < size; counter++)
                            {
                                l.add(a.get(counter).getAsString());

                                //Adds name and type(supertype) to typeName & typeT ArrayList if it hasn't been added
                                if (typeName.contains(a.get(counter).getAsString()) == false)
                                {
                                    typeName.add(a.get(counter).getAsString());
                                    typeT.add("Supertypes");
                                }
                            }
                            supertypes.add(l);

                        }
                        else if (c.getAsJsonObject().get("supertypes") == null)
                        {
                            supertypes.add(null);
                        }
                        //****************************************************** */
                        // Checks if types is not null 
                        // then adds to types ArrayList 
                        //****************************************************** */
                        if (c.getAsJsonObject().get("types") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("types").getAsJsonArray();
                            int size = a.size();    //Size of card types in card
                            ArrayList<String> l = new ArrayList<String>();

                            for (int counter = 0; counter < size; counter++)
                            {
                                l.add(a.get(counter).getAsString());

                                //Adds name and type(types) to typeName & typeT ArrayList if it hasn't been added
                                if (typeName.contains(a.get(counter).getAsString()) == false)
                                {
                                    typeName.add(a.get(counter).getAsString());
                                    typeT.add("Types");
                                }
                            }
                            types.add(l);

                        }
                        else if (c.getAsJsonObject().get("types") == null)
                        {
                            types.add(null);
                        }


                        
                        //****************************************************** */
                        // Checks if subtypes is not null 
                        // then adds to subtypes to ArrayList subtypes 
                        //****************************************************** */
                        if (c.getAsJsonObject().get("subtypes") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("subtypes").getAsJsonArray();
                            int size = a.size();    //Size of colorIdentity in card
                            ArrayList<String> l = new ArrayList<String>();

                            for (int counter = 0; counter < size; counter++)
                            {
                                l.add(a.get(counter).getAsString());

                                //Adds name and type(subtypes) to typeName & typeT ArrayList if it hasn't been added
                                if (typeName.contains(a.get(counter).getAsString()) == false)
                                {
                                    typeName.add(a.get(counter).getAsString());
                                    typeT.add("Subtypes");
                                }
                                
                            }
                            subtypes.add(l);

                        }
                        else if (c.getAsJsonObject().get("subtypes") == null)
                        {
                            subtypes.add(null);
                        }


                        
                        //****************************************************** */
                        // Checks if text is not null 
                        // then adds to text ArrayList.
                        //****************************************************** */
                        if (c.getAsJsonObject().get("text") != null)
                        {
                            text.add(c.getAsJsonObject().get("text").getAsString());
                        }
                        else if (c.getAsJsonObject().get("text") == null)
                        {
                            text.add(null);
                        }


                        //****************************************************** */
                        // Checks if flavor text is not null 
                        // then adds to flavor text to flavor ArrayList.
                        //****************************************************** */
                        if (c.getAsJsonObject().get("flavor") != null)
                        {
                            flavor.add(c.getAsJsonObject().get("flavor").getAsString());
                        }
                        else if (c.getAsJsonObject().get("flavor") == null)
                        {
                            flavor.add(null);
                        }
                        
                        //****************************************************** */
                        // Checks if power is not null 
                        // then adds to power to power ArrayList.
                        //****************************************************** */
                        if (c.getAsJsonObject().get("power") != null)
                        {
                            power.add(c.getAsJsonObject().get("power").getAsString());
                        }
                        else if (c.getAsJsonObject().get("power") == null)
                        {
                            power.add(null);
                        }

                        //****************************************************** */
                        // Checks if toughness is not null 
                        // then adds to toughness to toughness ArrayList.
                        //****************************************************** */
                        if (c.getAsJsonObject().get("toughness") != null)
                        {
                            toughness.add(c.getAsJsonObject().get("toughness").getAsString());
                        }
                        else if (c.getAsJsonObject().get("toughness") == null)
                        {
                            toughness.add(null);
                        }

                        //****************************************************** */
                        // Checks if loyalty is not null 
                        // then adds to loyalty to loyalty ArrayList.
                        //****************************************************** */
                        if (c.getAsJsonObject().get("loyalty") != null)
                        {
                            if (c.getAsJsonObject().get("name").getAsString().equals("Nissa, Steward of Elements"))
                                loyalty.add(0);
                            else
                                loyalty.add(c.getAsJsonObject().get("loyalty").getAsInt());
                        }
                        else if (c.getAsJsonObject().get("loyalty") == null)
                        {
                            loyalty.add(null);
                        }
                        
                    }   
                    else    //Prints set name that include cards with no multiverseid
                    {
                        //System.out.println(set.get("name"));
                        break;
                    }  

                }
            }


            //************************************************* */
            //Stores the name of every format
            //************************************************* */
            for (int k = 0; k < legality.size(); k++)
            {
                if (format.get(k)!= null)
                {
                    for(int y = 0; y < format.get(k).size(); y++)
                    {
                        if (listFormat.contains(format.get(k).get(y)) == false)
                        {
                            listFormat.add(format.get(k).get(y));
                        }
                    }
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error with JSON!");
        }  


        //******************************************************************* */
        //Storing Card information into the Database
        //*******************************************************************  */
        try
        {
            Connection connect = null;
            Statement statement = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            connect = DriverManager.getConnection("jdbc:mysql://localhost/mtg_dbm?" + "user=root&password=q1w2e3r4");

            statement = connect.createStatement();

            //Storing Information into MTGSet Table
            for (int x = 0; x < setName.size(); x++)
            {
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                Date parsed = f.parse(releaseDate.get(x));
                java.sql.Date data = new java.sql.Date(parsed.getTime());
                
                preparedStatement = connect.prepareStatement("insert into mtg_dbm.MTGSet values (?, ?, ?, ?, ?)");
                preparedStatement.setString(1, setName.get(x));
                preparedStatement.setString(2, code.get(x));
                preparedStatement.setString(3, setType.get(x));
                preparedStatement.setDate(4, data);
                preparedStatement.setString(5, block.get(x));
                preparedStatement.executeUpdate();
            }

            //Storing Information to Type Table
            for (int x =0; x < typeName.size(); x++)
            {
                preparedStatement = connect.prepareStatement("insert into mtg_dbm.Type values (default, ?, ?)");
                preparedStatement.setString(1, typeName.get(x));
                preparedStatement.setString(2, typeT.get(x));
                preparedStatement.executeUpdate();
            }

            // Storing Information into Card Table
            for (int x = 0; x < cardName.size(); x++)
            {
                preparedStatement = connect.prepareStatement("insert into mtg_dbm.Card values (default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                System.out.println(cardName.get(x) + ":" + x);   //For Testing
                preparedStatement.setString(1,cardName.get(x));
                preparedStatement.setInt(2,cardID.get(x));
                preparedStatement.setString(3,layout.get(x));
                preparedStatement.setString(4,manaCost.get(x));
                if (cmc.get(x) != null)
                {
                    preparedStatement.setFloat(5,cmc.get(x));
                }
                else
                {
                    preparedStatement.setNull(5, java.sql.Types.FLOAT);
                }

                preparedStatement.setString(6,text.get(x));
                preparedStatement.setString(7,type.get(x));
                preparedStatement.setString(8,rarity.get(x));
                preparedStatement.setString(9,flavor.get(x));
                preparedStatement.setString(10,artist.get(x));
                preparedStatement.setString(11,power.get(x));
                preparedStatement.setString(12,toughness.get(x));

                if (loyalty.get(x) != null)
                {
                    preparedStatement.setInt(13,loyalty.get(x));
                }
                else
                {
                    preparedStatement.setNull(13,java.sql.Types.INTEGER);                    
                }
                     
                preparedStatement.setString(14,cardSet.get(x));

                preparedStatement.executeUpdate();
            }

            // Storing Information into Ruling Table
            for (int x = 0; x < rulings.size(); x++)
            {
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                java.sql.Date data;

                preparedStatement = connect.prepareStatement("insert into mtg_dbm.Ruling values (?, ?, ?)");

                if (rulings.get(x) != null)
                {
                    for (int counter = 0; counter < rulings.get(x).size(); counter++)
                    {
                        Date parsed = f.parse(rulingsDate.get(x).get(counter));
                        data = new java.sql.Date(parsed.getTime()); 

                        preparedStatement.setInt(1, x+1);
                        preparedStatement.setString(2, rulings.get(x).get(counter));
                        preparedStatement.setDate(3, data);

                        preparedStatement.executeUpdate();
                    } 
                }               
            }

            //Storing Information into Card_Type Table
            for (int x = 0; x < types.size(); x++)
            {
                //System.out.println("Testing: " + x);   //For Testing
                //If there exists supertypes in card
                if (supertypes.get(x) != null)
                {
                    for (int counter = 0; counter < supertypes.get(x).size(); counter ++)
                    {
                        //Searches for TypeID
                        int id = 0;
                        preparedStatement = connect.prepareStatement("select * from mtg_dbm.Type where Types=?");
                        preparedStatement.setString(1,supertypes.get(x).get(counter));
                        //System.out.println(supertypes.get(x).get(counter));    //For Testing
                    
                        resultSet = preparedStatement.executeQuery();   //Get TypeID of supertypes
                        while (resultSet.next())
                        {
                            id = resultSet.getInt("TypeID");
                        }

                        preparedStatement = connect.prepareStatement("insert into mtg_dbm.Card_Type values (?, ?)");
                        preparedStatement.setInt(1, x+1);   //Card ID
                        preparedStatement.setInt(2, id);    //TypeID
                        preparedStatement.executeUpdate();
                    }
                }

                //If there exist types in card
                if (types.get(x) != null)
                {
                    for (int counter = 0; counter < types.get(x).size(); counter ++)
                    {
                        //Searches for TypeID
                        int id = 0;
                        preparedStatement = connect.prepareStatement("select * from mtg_dbm.Type where Types=?");
                        preparedStatement.setString(1,types.get(x).get(counter));
                        //System.out.println(types.get(x).get(counter)); //FOR TESTING
                    
                        resultSet = preparedStatement.executeQuery();   //Get TypeID of supertypes
                        while (resultSet.next())
                        {
                            id = resultSet.getInt("TypeID");
                        }

                        preparedStatement = connect.prepareStatement("insert into mtg_dbm.Card_Type values (?, ?)");
                        preparedStatement.setInt(1, x+1);   //Card ID
                        preparedStatement.setInt(2, id);    //TypeID
                        preparedStatement.executeUpdate();
                    }
                }

                //If there exists subtypes in card
                if (subtypes.get(x) != null)
                {
                    for (int counter = 0; counter < subtypes.get(x).size(); counter ++)
                    {
                        //Searches for TypeID
                        int id = 0;
                        preparedStatement = connect.prepareStatement("select * from mtg_dbm.Type where Types=?");
                        preparedStatement.setString(1,subtypes.get(x).get(counter));
                        //System.out.println(subtypes.get(x).get(counter));  //FOR TESTING
                    
                        resultSet = preparedStatement.executeQuery();   //Get TypeID of supertypes
                        while (resultSet.next())
                        {
                            id = resultSet.getInt("TypeID");
                        }

                        preparedStatement = connect.prepareStatement("insert into mtg_dbm.Card_Type values (?, ?)");
                        preparedStatement.setInt(1, x+1);   //Card ID
                        preparedStatement.setInt(2, id);    //TypeID
                        preparedStatement.executeUpdate();
                    }
                }
            }

            //Storing Information into Card_Color Table
            for (int x = 0; x < colors.size(); x++)
            {
                //System.out.println("Testing: " + x);   //FOR TESTING
                
                //If there exists colors in card
                if (colors.get(x) != null)
                {
                    for(int counter = 0; counter < colors.get(x).size(); counter++)
                    {
                        //Searches for ColorID
                        int id = 0;
                        //System.out.println(colors.get(x).get(counter));    //FOR TESTING
                        preparedStatement = connect.prepareStatement("select * from mtg_dbm.Color where ColorName=?");
                        preparedStatement.setString(1,colors.get(x).get(counter));
                    
                        resultSet = preparedStatement.executeQuery();   //Get TypeID of supertypes
                        while (resultSet.next())
                        {
                            id = resultSet.getInt("ColorID");
                        }

                        preparedStatement = connect.prepareStatement("insert into mtg_dbm.Card_Color values (?, ?)");
                        preparedStatement.setInt(1, x+1);   //Card ID
                        preparedStatement.setInt(2, id);    //TypeID
                        preparedStatement.executeUpdate();
                    }
                }
            } 

            //Storing Information into Card_ColorIdentity Table
            for (int x = 0; x < colorIdentity.size(); x++)
            {   
                //If there exists colors in card
                if (colorIdentity.get(x) != null)
                {
                    for(int counter = 0; counter < colorIdentity.get(x).size(); counter++)
                    {
                        //Searches for ColorID
                        int id = 0;
                        //System.out.println(colorIdentity.get(x).get(counter));    //FOR TESTING
                        preparedStatement = connect.prepareStatement("select * from mtg_dbm.ColorIdentity where ColorSymbol=?");
                        preparedStatement.setString(1,colorIdentity.get(x).get(counter));
                    
                        resultSet = preparedStatement.executeQuery();   //Get TypeID of colorIdentity
                        while (resultSet.next())
                        {
                            id = resultSet.getInt("ColorID");
                        }

                        preparedStatement = connect.prepareStatement("insert into mtg_dbm.Card_ColorIdentity values (?, ?)");
                        preparedStatement.setInt(1, x+1);   //Card ID
                        preparedStatement.setInt(2, id);    //TypeID
                        preparedStatement.executeUpdate();
                    }
                }
            }    

            //Storing Information into Split_Flip_Card Table
            for (int x = 0; x < names.size(); x++)
            {
                //If there exists cards associate with card(x)
                if (names.get(x) != null)
                {
                    for(int counter = 0; counter < names.get(x).size(); counter++)
                    {
                        preparedStatement = connect.prepareStatement("insert into mtg_dbm.Split_Flip_Card values (default, ?, ?)");
                        preparedStatement.setInt(1, x+1);   //Card ID
                        preparedStatement.setString(2, names.get(x).get(counter));    //TypeID
                        preparedStatement.executeUpdate();
                    }
                }
            } 

            //Storing Information into Format Table
            for (int x = 0; x < listFormat.size(); x++)
            {
                preparedStatement = connect.prepareStatement("insert into mtg_dbm.Format values (?)");
                preparedStatement.setString(1, listFormat.get(x));
                preparedStatement.executeUpdate();   
            }

            //Storing Information into Format_Card Table
            for (int x =0; x < legality.size(); x++)
            {

                //If there exists legality for card(x)
                if (legality.get(x) != null)
                {
                    for (int counter = 0; counter < legality.get(x).size(); counter++)
                    {
                        preparedStatement = connect.prepareStatement("insert into mtg_dbm.Format_Card values (?, ?, ?)");
                        preparedStatement.setInt(1, // Name: Carlos A. Rios
// Date: Mar 8, 2017
// Purpose: The purpose of this program is to learn how to look up data and pull that data
//          from the MTG JSON file. Also, to add this information into MYSQL Database.
//
// NOTES TO MYSELF: To run this program (through command lines, use the following lines
//          Compile:
//              javac -cp gson.jar:/home/reticent/netbeans-8.2/ide/modules/ext/mysql-connector-java-5.1.23-bin.jar: -d ./ main.java
//          Running:
//              java -cp gson.jar:/home/reticent/netbeans-8.2/ide/modules/ext/mysql-connector-java-5.1.23-bin.jar: testing/main
//      *ASSUMING THAT YOU ARE IN FOLDER THAT MAIN.JAVA IS IN (which is .../Testing/src)
//      *Replace .jar paths with wherever .jar file is found on computer & the dir for the JSON file
//      * .jar files include the Google GSON.jar file & the mysql connector .jar file
//      *Total of 32519 Cards | only 30279 have multiverseid (Difference of 2218)


package testing;

import java.io.FileReader;
import java.io.IOException;
import com.google.gson.Gson;  
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author reticent
 */
public class main {
    
    public static void main (String args[])
    {
        System.out.println("Hello World!");
        
        //************************************************/
        //Declared Variables
        //************************************************/

        //Set information
        ArrayList<String> setName = new ArrayList<String>();    //Name of set
        ArrayList<String> code = new ArrayList<String>();   //Set abbreviated code
        ArrayList<String> releaseDate = new ArrayList<String>();   //Year relased YYYY-MM-DD
        ArrayList<String> setType = new ArrayList<String>();    //Type of set
        ArrayList<String> block = new ArrayList<String>();  //Block the set is in

        //Card information
        ArrayList<String> cardName = new ArrayList<String>();  //Holds name of all cards
        ArrayList<Integer> cardID = new ArrayList<Integer>();   //Holds multiverseid of all card
        ArrayList<ArrayList<String>> names = new ArrayList<ArrayList<String>>();  //Holds names of double-side/split cards
        ArrayList<String> layout = new ArrayList<String>();  //Holds layout of card
        ArrayList<String> manaCost = new ArrayList<String>();    //Holds mana cost of card
        ArrayList<Float> cmc = new ArrayList<Float>();  //Holds converted mana cost of card
        ArrayList<ArrayList<String>> colors = new ArrayList<ArrayList<String>>(); //Holds card colors (derived from casting cost and keywords)
        ArrayList<ArrayList<String>> colorIdentity = new ArrayList<ArrayList<String>>();    //Holds cards color information and costs (Used for commander)
        ArrayList<String> type = new ArrayList<String>();   //Holds the card type
        ArrayList<ArrayList<String>> supertypes = new ArrayList<ArrayList<String>>();   //Holds the supertypes of the card
        ArrayList<ArrayList<String>> types = new ArrayList<ArrayList<String>>();    //Holds the types of the card
        ArrayList<ArrayList<String>> subtypes = new ArrayList<ArrayList<String>>(); //Holds the subtypes of the card
        ArrayList<String> rarity = new ArrayList<String>(); //Rarity of the card
        ArrayList<String> text = new ArrayList<String>();   //Text of the card
        ArrayList<String> flavor = new ArrayList<String>(); //Flavor text of the card
        ArrayList<String> artist = new ArrayList<String>(); //The artist of the card
        ArrayList<String> power = new ArrayList<String>();  //Power of the card (Only on creatures)
        ArrayList<String> toughness = new ArrayList<String>();  //Toughness of the card (Only on creatures)
        ArrayList<Integer> loyalty = new ArrayList<Integer>();  //Loyalty of the card (Only on planeswalkers)
        ArrayList<String> cardSet = new ArrayList<String>();    //Name of set that card came from.
        ArrayList<ArrayList<String>> banDate = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> banCard = new ArrayList<ArrayList<String>>();

        //Type Information
        ArrayList<String> typeName = new ArrayList<String>();   //Holds Name of the type
        ArrayList<String> typeT = new ArrayList<String>();  //Holds what kind of type the name is (Supertype, Subtype, or Type)

        //Ruling Information
        ArrayList<ArrayList<String>> rulings = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> rulingsDate = new ArrayList<ArrayList<String>>();

        //Legality Information (Card)
        ArrayList<ArrayList<String>> legality = new ArrayList<ArrayList<String>>(); //Card Legality in Format
        ArrayList<ArrayList<String>> format = new ArrayList<ArrayList<String>>();   //Card Format (Legality)
        ArrayList<String> listFormat = new ArrayList<String>(); //Names of Formats

        //Reading from JSON
        try
        {            
            //Declared Variables
            ArrayList <String> ls = new ArrayList<String>();    //Holds code name for all sets
            Set s;  //Temporarily holds the JObject as a set to substring the code name for each set
            Gson gson = new Gson();

            BufferedReader br = new BufferedReader(
                new FileReader("/home/reticent/Downloads/AllSets-x.json")); //BufferedReader to read from JSON file

            JsonParser parser = new JsonParser();
            JsonObject test = parser.parse(br).getAsJsonObject();
            s = test.entrySet();

            //Subtring each set code name to be able to pull from JSON File
            for (Object o : s)
            {   
                String str = o.toString();
                ls.add(str.substring(0,str.indexOf("=")));

                //System.out.println(str.substring(0,str.indexOf("=")));
            }

            //******************************************************************************************************
            // Iterating through each set to gather information
            //*******************************************************************************************************
            for (int x = 0; x < ls.size(); x++) //1st For used to iterate through each set in ArrayList ls
            {
                //Instanced Variables
                JsonObject set = test.getAsJsonObject(ls.get(x));   //Json Object of specific set
                JsonArray card = set.getAsJsonArray("cards");   //Json Array of the all cards in specific set
                int size_of_card = card.size(); // # of cards within set

                //****************************************************************************************
                // Gathering Set Information (Set Name, Code, Released Date, Set Type, and Block)
                //****************************************************************************************
                setName.add(set.get("name").getAsString()); //Add name of set to ArrayList setName
                code.add(set.get("code").getAsString()); //Add code of set to ArrayList code
                releaseDate.add(set.get("releaseDate").getAsString()); //Add release date of set to ArrayList releaseDate
                setType.add(set.get("type").getAsString()); //Add set type to ArrayList setType

                if (set.get("block")!= null)
                {
                    block.add(set.get("block").getAsString()); //Add block to ArrayList block
                }
                else
                {
                    block.add(null); //Add block to ArrayList block
                }

                //*****************************************************************************************
                // Gathering Card Information from each set with each iteration
                //*****************************************************************************************
                for (int i = 0; i < size_of_card; i++)  
                {
                    JsonElement c = card.get(i);    //JsonElement of card

                    if (c.getAsJsonObject().get("multiverseid") != null)    //If card has multiverseid, then add to ArrayList
                    {
                        cardName.add(c.getAsJsonObject().get("name").getAsString());   //Add name of card to ArrayList cardName
                        cardID.add(c.getAsJsonObject().get("multiverseid").getAsInt()); //Adds  multiverseid to ArrayList cardID
                        layout.add(c.getAsJsonObject().get("layout").getAsString());    //Adds card layout to ArrayList layout
                        type.add(c.getAsJsonObject().get("type").getAsString());    //Adds card type to ArrayList type
                        rarity.add(c.getAsJsonObject().get("rarity").getAsString());    //Adds card rarity to ArrayList raritys
                        artist.add(c.getAsJsonObject().get("artist").getAsString());    //Adds artist to the ArrayList artist
                        cardSet.add(set.get("name").getAsString()); //Adds set which card belongs to the ArrayList cardSet

                        //********************************************************* */
                        //Checks if Card has rulings
                        //********************************************************* */
                        if (c.getAsJsonObject().get("rulings") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("rulings").getAsJsonArray();   //JsonArray of card rulings
                            int size = a.size();    //Size of card rulings
                            ArrayList<String> l = new ArrayList<String>();  //Stores Ruling text
                            ArrayList<String> d = new ArrayList<String>();  //Stores Date of Ruling

                            for (int counter = 0; counter < size; counter++)
                            {
                                l.add(a.get(counter).getAsJsonObject().get("text").getAsString());
                                d.add(a.get(counter).getAsJsonObject().get("date").getAsString());
                            }
                            rulings.add(l);
                            rulingsDate.add(d);
                        }
                        else if (c.getAsJsonObject().get("rulings") == null)
                        {
                            rulings.add(null);
                            rulingsDate.add(null);
                        }

                        //********************************************************* */
                        //Checks if Card has Legality
                        //********************************************************* */
                        if (c.getAsJsonObject().get("legalities") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("legalities").getAsJsonArray();   //JsonArray of card rulings
                            int size = a.size();    //Size of card rulings
                            ArrayList<String> f = new ArrayList<String>();  //Stores Ruling text
                            ArrayList<String> l = new ArrayList<String>();  //Stores Date of Ruling

                            for (int counter = 0; counter < size; counter++)
                            {
                                f.add(a.get(counter).getAsJsonObject().get("format").getAsString());
                                l.add(a.get(counter).getAsJsonObject().get("legality").getAsString());
                            }
                            legality.add(l);
                            format.add(f);
                        }
                        else if (c.getAsJsonObject().get("legalities") == null)
                        {
                            legality.add(null);
                            format.add(null);
                        }

                        //****************************************************** */
                        // Checks if manaCost is not null then adds to manCost ArrayList 
                        //****************************************************** */
                        if (c.getAsJsonObject().get("manaCost") != null)
                        {
                            manaCost.add(c.getAsJsonObject().get("manaCost").getAsString());
                        }
                        else if (c.getAsJsonObject().get("manaCost") == null)
                        {
                            manaCost.add(null);
                        }

                        //****************************************************** */
                        // Checks if converted Mana Cost (cmc) is not null 
                        // then adds to cmc ArrayList.
                        //****************************************************** */
                        if (c.getAsJsonObject().get("cmc") != null)
                        {
                            cmc.add(c.getAsJsonObject().get("cmc").getAsFloat());
                        }
                        else if (c.getAsJsonObject().get("cmc") == null)
                        {
                            cmc.add(null);
                        }

                        //********************************************************
                        // Gathers "names" field from card that are split/flip cards
                        //********************************************************
                        if (c.getAsJsonObject().get("names") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("names").getAsJsonArray();   //JsonArray of cards names (for split/flip cards)
                            int size = a.size();    //Size of card names
                            ArrayList<String> l = new ArrayList<String>();

                            for (int counter = 0; counter < size; counter++)
                            {
                                l.add(a.get(counter).getAsString());
                            }
                            names.add(l);
                        }
                        else if (c.getAsJsonObject().get("names") == null)
                        {
                              names.add(null);
                        }

                        //****************************************************** */
                        // Checks if colors is not null 
                        // then adds to colors ArrayList 
                        //****************************************************** */
                        if (c.getAsJsonObject().get("colors") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("colors").getAsJsonArray();
                            int size = a.size();    //Size of colors in card
                            ArrayList<String> l = new ArrayList<String>();

                            for (int counter = 0; counter < size; counter++)
                            {
                                l.add(a.get(counter).getAsString());
                            }
                            colors.add(l);
                            
                        }
                        else if (c.getAsJsonObject().get("colors") == null)
                        {
                            colors.add(null);
                        }

                        //****************************************************** */
                        // Checks if colorsIdentity is not null 
                        // then adds to colorsIdentity ArrayList 
                        //****************************************************** */
                        if (c.getAsJsonObject().get("colorIdentity") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("colorIdentity").getAsJsonArray();
                            int size = a.size();    //Size of colorIdentity in card
                            ArrayList<String> l = new ArrayList<String>();

                            for (int counter = 0; counter < size; counter++)
                            {
                                l.add(a.get(counter).getAsString());
                            }
                            colorIdentity.add(l);

                        }
                        else if (c.getAsJsonObject().get("colorIdentity") == null)
                        {
                            colorIdentity.add(null);
                        }

                        //****************************************************** */
                        // Checks if supertypes is not null 
                        // then adds to supertypes ArrayList 
                        //****************************************************** */
                        if (c.getAsJsonObject().get("supertypes") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("supertypes").getAsJsonArray();
                            int size = a.size();    //Size of supertypes in card
                            ArrayList<String> l = new ArrayList<String>();

                            for (int counter = 0; counter < size; counter++)
                            {
                                l.add(a.get(counter).getAsString());

                                //Adds name and type(supertype) to typeName & typeT ArrayList if it hasn't been added
                                if (typeName.contains(a.get(counter).getAsString()) == false)
                                {
                                    typeName.add(a.get(counter).getAsString());
                                    typeT.add("Supertypes");
                                }
                            }
                            supertypes.add(l);

                        }
                        else if (c.getAsJsonObject().get("supertypes") == null)
                        {
                            supertypes.add(null);
                        }
                        //****************************************************** */
                        // Checks if types is not null 
                        // then adds to types ArrayList 
                        //****************************************************** */
                        if (c.getAsJsonObject().get("types") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("types").getAsJsonArray();
                            int size = a.size();    //Size of card types in card
                            ArrayList<String> l = new ArrayList<String>();

                            for (int counter = 0; counter < size; counter++)
                            {
                                l.add(a.get(counter).getAsString());

                                //Adds name and type(types) to typeName & typeT ArrayList if it hasn't been added
                                if (typeName.contains(a.get(counter).getAsString()) == false)
                                {
                                    typeName.add(a.get(counter).getAsString());
                                    typeT.add("Types");
                                }
                            }
                            types.add(l);

                        }
                        else if (c.getAsJsonObject().get("types") == null)
                        {
                            types.add(null);
                        }


                        
                        //****************************************************** */
                        // Checks if subtypes is not null 
                        // then adds to subtypes to ArrayList subtypes 
                        //****************************************************** */
                        if (c.getAsJsonObject().get("subtypes") != null)
                        {
                            JsonArray a = c.getAsJsonObject().get("subtypes").getAsJsonArray();
                            int size = a.size();    //Size of colorIdentity in card
                            ArrayList<String> l = new ArrayList<String>();

                            for (int counter = 0; counter < size; counter++)
                            {
                                l.add(a.get(counter).getAsString());

                                //Adds name and type(subtypes) to typeName & typeT ArrayList if it hasn't been added
                                if (typeName.contains(a.get(counter).getAsString()) == false)
                                {
                                    typeName.add(a.get(counter).getAsString());
                                    typeT.add("Subtypes");
                                }
                                
                            }
                            subtypes.add(l);

                        }
                        else if (c.getAsJsonObject().get("subtypes") == null)
                        {
                            subtypes.add(null);
                        }


                        
                        //****************************************************** */
                        // Checks if text is not null 
                        // then adds to text ArrayList.
                        //****************************************************** */
                        if (c.getAsJsonObject().get("text") != null)
                        {
                            text.add(c.getAsJsonObject().get("text").getAsString());
                        }
                        else if (c.getAsJsonObject().get("text") == null)
                        {
                            text.add(null);
                        }


                        //****************************************************** */
                        // Checks if flavor text is not null 
                        // then adds to flavor text to flavor ArrayList.
                        //****************************************************** */
                        if (c.getAsJsonObject().get("flavor") != null)
                        {
                            flavor.add(c.getAsJsonObject().get("flavor").getAsString());
                        }
                        else if (c.getAsJsonObject().get("flavor") == null)
                        {
                            flavor.add(null);
                        }
                        
                        //****************************************************** */
                        // Checks if power is not null 
                        // then adds to power to power ArrayList.
                        //****************************************************** */
                        if (c.getAsJsonObject().get("power") != null)
                        {
                            power.add(c.getAsJsonObject().get("power").getAsString());
                        }
                        else if (c.getAsJsonObject().get("power") == null)
                        {
                            power.add(null);
                        }

                        //****************************************************** */
                        // Checks if toughness is not null 
                        // then adds to toughness to toughness ArrayList.
                        //****************************************************** */
                        if (c.getAsJsonObject().get("toughness") != null)
                        {
                            toughness.add(c.getAsJsonObject().get("toughness").getAsString());
                        }
                        else if (c.getAsJsonObject().get("toughness") == null)
                        {
                            toughness.add(null);
                        }

                        //****************************************************** */
                        // Checks if loyalty is not null 
                        // then adds to loyalty to loyalty ArrayList.
                        //****************************************************** */
                        if (c.getAsJsonObject().get("loyalty") != null)
                        {
                            if (c.getAsJsonObject().get("name").getAsString().equals("Nissa, Steward of Elements"))
                                loyalty.add(0);
                            else
                                loyalty.add(c.getAsJsonObject().get("loyalty").getAsInt());
                        }
                        else if (c.getAsJsonObject().get("loyalty") == null)
                        {
                            loyalty.add(null);
                        }
                        
                    }   
                    else    //Prints set name that include cards with no multiverseid
                    {
                        //System.out.println(set.get("name"));
                        break;
                    }  

                }
            }


            //************************************************* */
            //Stores the name of every format
            //************************************************* */
            for (int k = 0; k < legality.size(); k++)
            {
                if (format.get(k)!= null)
                {
                    for(int y = 0; y < format.get(k).size(); y++)
                    {
                        if (listFormat.contains(format.get(k).get(y)) == false)
                        {
                            listFormat.add(format.get(k).get(y));
                        }
                    }
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error with JSON!");
        }  


        //******************************************************************* */
        //Storing Card information into the Database
        //*******************************************************************  */
        try
        {
            Connection connect = null;
            Statement statement = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            connect = DriverManager.getConnection("jdbc:mysql://localhost/mtg_dbm?" + "user=root&password=q1w2e3r4");

            statement = connect.createStatement();

            //Storing Information into MTGSet Table
            for (int x = 0; x < setName.size(); x++)
            {
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                Date parsed = f.parse(releaseDate.get(x));
                java.sql.Date data = new java.sql.Date(parsed.getTime());
                
                preparedStatement = connect.prepareStatement("insert into mtg_dbm.MTGSet values (?, ?, ?, ?, ?)");
                preparedStatement.setString(1, setName.get(x));
                preparedStatement.setString(2, code.get(x));
                preparedStatement.setString(3, setType.get(x));
                preparedStatement.setDate(4, data);
                preparedStatement.setString(5, block.get(x));
                preparedStatement.executeUpdate();
            }

            //Storing Information to Type Table
            for (int x =0; x < typeName.size(); x++)
            {
                preparedStatement = connect.prepareStatement("insert into mtg_dbm.Type values (default, ?, ?)");
                preparedStatement.setString(1, typeName.get(x));
                preparedStatement.setString(2, typeT.get(x));
                preparedStatement.executeUpdate();
            }

            // Storing Information into Card Table
            for (int x = 0; x < cardName.size(); x++)
            {
                preparedStatement = connect.prepareStatement("insert into mtg_dbm.Card values (default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                System.out.println(cardName.get(x) + ":" + x);   //For Testing
                preparedStatement.setString(1,cardName.get(x));
                preparedStatement.setInt(2,cardID.get(x));
                preparedStatement.setString(3,layout.get(x));
                preparedStatement.setString(4,manaCost.get(x));
                if (cmc.get(x) != null)
                {
                    preparedStatement.setFloat(5,cmc.get(x));
                }
                else
                {
                    preparedStatement.setNull(5, java.sql.Types.FLOAT);
                }

                preparedStatement.setString(6,text.get(x));
                preparedStatement.setString(7,type.get(x));
                preparedStatement.setString(8,rarity.get(x));
                preparedStatement.setString(9,flavor.get(x));
                preparedStatement.setString(10,artist.get(x));
                preparedStatement.setString(11,power.get(x));
                preparedStatement.setString(12,toughness.get(x));

                if (loyalty.get(x) != null)
                {
                    preparedStatement.setInt(13,loyalty.get(x));
                }
                else
                {
                    preparedStatement.setNull(13,java.sql.Types.INTEGER);                    
                }
                     
                preparedStatement.setString(14,cardSet.get(x));

                preparedStatement.executeUpdate();
            }

            // Storing Information into Ruling Table
            for (int x = 0; x < rulings.size(); x++)
            {
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                java.sql.Date data;

                preparedStatement = connect.prepareStatement("insert into mtg_dbm.Ruling values (?, ?, ?)");

                if (rulings.get(x) != null)
                {
                    for (int counter = 0; counter < rulings.get(x).size(); counter++)
                    {
                        Date parsed = f.parse(rulingsDate.get(x).get(counter));
                        data = new java.sql.Date(parsed.getTime()); 

                        preparedStatement.setInt(1, x+1);
                        preparedStatement.setString(2, rulings.get(x).get(counter));
                        preparedStatement.setDate(3, data);

                        preparedStatement.executeUpdate();
                    } 
                }               
            }

            //Storing Information into Card_Type Table
            for (int x = 0; x < types.size(); x++)
            {
                //System.out.println("Testing: " + x);   //For Testing
                //If there exists supertypes in card
                if (supertypes.get(x) != null)
                {
                    for (int counter = 0; counter < supertypes.get(x).size(); counter ++)
                    {
                        //Searches for TypeID
                        int id = 0;
                        preparedStatement = connect.prepareStatement("select * from mtg_dbm.Type where Types=?");
                        preparedStatement.setString(1,supertypes.get(x).get(counter));
                        //System.out.println(supertypes.get(x).get(counter));    //For Testing
                    
                        resultSet = preparedStatement.executeQuery();   //Get TypeID of supertypes
                        while (resultSet.next())
                        {
                            id = resultSet.getInt("TypeID");
                        }

                        preparedStatement = connect.prepareStatement("insert into mtg_dbm.Card_Type values (?, ?)");
                        preparedStatement.setInt(1, x+1);   //Card ID
                        preparedStatement.setInt(2, id);    //TypeID
                        preparedStatement.executeUpdate();
                    }
                }

                //If there exist types in card
                if (types.get(x) != null)
                {
                    for (int counter = 0; counter < types.get(x).size(); counter ++)
                    {
                        //Searches for TypeID
                        int id = 0;
                        preparedStatement = connect.prepareStatement("select * from mtg_dbm.Type where Types=?");
                        preparedStatement.setString(1,types.get(x).get(counter));
                        //System.out.println(types.get(x).get(counter)); //FOR TESTING
                    
                        resultSet = preparedStatement.executeQuery();   //Get TypeID of supertypes
                        while (resultSet.next())
                        {
                            id = resultSet.getInt("TypeID");
                        }

                        preparedStatement = connect.prepareStatement("insert into mtg_dbm.Card_Type values (?, ?)");
                        preparedStatement.setInt(1, x+1);   //Card ID
                        preparedStatement.setInt(2, id);    //TypeID
                        preparedStatement.executeUpdate();
                    }
                }

                //If there exists subtypes in card
                if (subtypes.get(x) != null)
                {
                    for (int counter = 0; counter < subtypes.get(x).size(); counter ++)
                    {
                        //Searches for TypeID
                        int id = 0;
                        preparedStatement = connect.prepareStatement("select * from mtg_dbm.Type where Types=?");
                        preparedStatement.setString(1,subtypes.get(x).get(counter));
                        //System.out.println(subtypes.get(x).get(counter));  //FOR TESTING
                    
                        resultSet = preparedStatement.executeQuery();   //Get TypeID of supertypes
                        while (resultSet.next())
                        {
                            id = resultSet.getInt("TypeID");
                        }

                        preparedStatement = connect.prepareStatement("insert into mtg_dbm.Card_Type values (?, ?)");
                        preparedStatement.setInt(1, x+1);   //Card ID
                        preparedStatement.setInt(2, id);    //TypeID
                        preparedStatement.executeUpdate();
                    }
                }
            }

            //Storing Information into Card_Color Table
            for (int x = 0; x < colors.size(); x++)
            {
                //System.out.println("Testing: " + x);   //FOR TESTING
                
                //If there exists colors in card
                if (colors.get(x) != null)
                {
                    for(int counter = 0; counter < colors.get(x).size(); counter++)
                    {
                        //Searches for ColorID
                        int id = 0;
                        //System.out.println(colors.get(x).get(counter));    //FOR TESTING
                        preparedStatement = connect.prepareStatement("select * from mtg_dbm.Color where ColorName=?");
                        preparedStatement.setString(1,colors.get(x).get(counter));
                    
                        resultSet = preparedStatement.executeQuery();   //Get TypeID of supertypes
                        while (resultSet.next())
                        {
                            id = resultSet.getInt("ColorID");
                        }

                        preparedStatement = connect.prepareStatement("insert into mtg_dbm.Card_Color values (?, ?)");
                        preparedStatement.setInt(1, x+1);   //Card ID
                        preparedStatement.setInt(2, id);    //TypeID
                        preparedStatement.executeUpdate();
                    }
                }
            } 

            //Storing Information into Card_ColorIdentity Table
            for (int x = 0; x < colorIdentity.size(); x++)
            {   
                //If there exists colors in card
                if (colorIdentity.get(x) != null)
                {
                    for(int counter = 0; counter < colorIdentity.get(x).size(); counter++)
                    {
                        //Searches for ColorID
                        int id = 0;
                        //System.out.println(colorIdentity.get(x).get(counter));    //FOR TESTING
                        preparedStatement = connect.prepareStatement("select * from mtg_dbm.ColorIdentity where ColorSymbol=?");
                        preparedStatement.setString(1,colorIdentity.get(x).get(counter));
                    
                        resultSet = preparedStatement.executeQuery();   //Get TypeID of colorIdentity
                        while (resultSet.next())
                        {
                            id = resultSet.getInt("ColorID");
                        }

                        preparedStatement = connect.prepareStatement("insert into mtg_dbm.Card_ColorIdentity values (?, ?)");
                        preparedStatement.setInt(1, x+1);   //Card ID
                        preparedStatement.setInt(2, id);    //TypeID
                        preparedStatement.executeUpdate();
                    }
                }
            }    

            //Storing Information into Split_Flip_Card Table
            for (int x = 0; x < names.size(); x++)
            {
                //If there exists cards associate with card(x)
                if (names.get(x) != null)
                {
                    for(int counter = 0; counter < names.get(x).size(); counter++)
                    {
                        preparedStatement = connect.prepareStatement("insert into mtg_dbm.Split_Flip_Card values (default, ?, ?)");
                        preparedStatement.setInt(1, x+1);   //Card ID
                        preparedStatement.setString(2, names.get(x).get(counter));    //TypeID
                        preparedStatement.executeUpdate();
                    }
                }
            } 

            //Storing Information into Format Table
            for (int x = 0; x < listFormat.size(); x++)
            {
                preparedStatement = connect.prepareStatement("insert into mtg_dbm.Format values (?)");
                preparedStatement.setString(1, listFormat.get(x));
                preparedStatement.executeUpdate();   
            }

            //Storing Information into Format_Card Table
            for (int x =0; x < legality.size(); x++)
            {

                //If there exists legality for card(x)
                if (legality.get(x) != null)
                {
                    for (int counter = 0; counter < legality.get(x).size(); counter++)
                    {
                        preparedStatement = connect.prepareStatement("insert into mtg_dbm.Format_Card values (?, ?, ?)");
                        preparedStatement.setInt(1, x+1);   //CardID
                        preparedStatement.setString(2, format.get(x).get(counter));    //Format Name
                        preparedStatement.setString(3, legality.get(x).get(counter));    //Legality of Card

                        System.out.println("Card: " + x + " | Format: " + format.get(x).get(counter) +  " | Legality: " + legality.get(x).get(counter));  //For Testing
                        preparedStatement.executeUpdate();
                    }
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error with Database!");
        }   
    }
}
x+1);   //CardID
                        preparedStatement.setString(2, format.get(x).get(counter));    //Format Name
                        preparedStatement.setString(3, legality.get(x).get(counter));    //Legality of Card

                        System.out.println("Card: " + x + " | Format: " + format.get(x).get(counter) +  " | Legality: " + legality.get(x).get(counter));  //For Testing
                        preparedStatement.executeUpdate();
                    }
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error with Database!");
        }   
    }
}
