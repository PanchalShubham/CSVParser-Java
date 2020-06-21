/*package declaration*/
package csv;

/*necessary imports*/
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * Suppose the content of CSV file is as follows: <br>
 * Parses a CSV file into a Java Object <br>
 * id,name,position <br>
 * 1,Alice,Programmer <br>
 * 2,Bob,Manager <br>
 * Then CSVReader instance has the following structure: <br>
 * [{"id" : "1", "name" : "Alice", "position" : "Programmer"}, <br>
 * {"id" : "2", "name" : "Bob", "position" : "Manager"}] <br>
 * */

/**
 * Parses a CSV file into a Java Object <br>
 * @author Shubham Panchal (http://shubhampanchal.herokuapp.com)
 * @version 1.0
 * */
public final class CSVReader {
    /*each csv reader has its own filename*/
    private final String filename;
    /*each csv reader has a set of keys*/
    private final Set<String> keys;
    /*each csv reader has a list of maps*/
    private final List<Map<String, String>> list;

    /**
     * Parses a CSV file into a Java Object
     * @param filename name of file to read data from
     * @throws IOException if fails to open file for reading
     * @throws ParseException if given file doesn't contains valid format data
     * */
    public CSVReader(String filename) throws IOException, ParseException {
        /*open the file*/
        BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
        /*stores the line number*/
        long lineNumber = 0;
        /*read the first line of file*/
        String tag;
        /*read the first non-empty line*/
        while ( (tag = br.readLine()) != null && tag.trim().isEmpty())  lineNumber++;
        /*check if tag is empty*/
        if (tag == null)
            throw new ParseException("ParseException: Found empty file", 1);
        /*check if string starts or ends with delimiter*/
        if (!Pattern.matches("^[^,].*[^,]$", tag))
            throw new ParseException("ParseException: Expected value before or after delimiter(,)", 1);
        /*increase the line number by 1*/
        lineNumber++;
        /*get the keys*/
        String[] keys = process(tag, lineNumber);
        /*trim the keys i.e. remove leading and trailing whitespaces*/
        for (int i = 0; i < keys.length; ++i)   keys[i] = keys[i].trim();
        /*iterate through keys and check if it contains empty empty key or duplicate keys*/
        Set<String> keyFrequencies = new HashSet<>();
        /*iterate through each possible key*/
        for (String key : keys){
            /*check if key is empty*/
            if (key.isEmpty())
                throw new ParseException("ParseException: empty key found", 1);
            /*check if it is a duplicate key*/
            if (keyFrequencies.contains(key))
                throw new ParseException("ParseException: duplicate key found", 1);
            /*add this key to map*/
            keyFrequencies.add(key);
        }
        /*create a new list of maps*/
        this.list = new ArrayList<>();
        /*stores the input line from file*/
        String line;
        /*read input from file*/
        while ( (line = br.readLine()) != null ){
            /*discard empty lines in file*/
            if(line.trim().isEmpty())
                continue;
            /*increase the line number by 1*/
            lineNumber++;
            /*check if string starts or ends with delimiter*/
            if (!Pattern.matches("^[^,].*[^,]$", line))
                throw new ParseException("ParseException: Expected value before or after delimiter(,)", 1);
            /*split the line into values*/
            String[] values = process(line, lineNumber);
            /*match the #keys in line*/
            if (keys.length < values.length)        throw new ParseException("ParseException at line " + lineNumber + ". Found more values than expected. (Expected: " + keys.length + ", Found: " + values.length + ")", (int)lineNumber);
            else if (keys.length > values.length)   throw new ParseException("ParseException at line " + lineNumber + ". Found fewer values than expected. (Expected: " + keys.length + ", Found: " + values.length + ")", (int)lineNumber);
            /*#keys matches the #values so we can store them in map*/
            Map<String, String> map = new HashMap<>();
            /*iterate through keys one by one*/
            for (int i = 0; i < keys.length; ++i)   map.put(keys[i], values[i].trim());
            /*append this map to list*/
            list.add(map);
        }
        /*close the buffered reader*/
        br.close();

        /*update the filename*/
        this.filename = filename;
        /*update the list of keys*/
        this.keys = new HashSet<>(keys.length);
        /*iterate through list and populate data*/
        Collections.addAll(this.keys, keys);
    }

    /**
     * Fetches the name of file used for creating CSVReader
     * @return the filename whose content was read to create this CSVReader
     * */
    public final String getFilename(){ return this.filename; }
    
    /**
     * @return number of rows read from CSV file
     * */
    public final int getRowCount() { return this.list.size(); }

    /**
     * Fetches a copy of set of keys (tag of CSV file)
     * @return list of keys (tag of CSV file)
     * */
    public final Set<String> getKeys() { return new HashSet<>(this.keys); }

    /**
     * Fetches the values for given row number
     * @param rowNumber for which record has to be fetched (must point to a non-empty row in CSV file)
     * @throws IllegalArgumentException if rowNumber is not in range[1...R - 1] where
     * R is the total number of rows in CSV file (index 0 corresponds to tag row)
     * @return Map&le;String, String&ge; of key:value for each value in given row
     * */
    public final Map<String, String> get(int rowNumber) throws IllegalArgumentException{
        /*check if row number is in bound*/
        if ( !(1 <= rowNumber && rowNumber <= list.size() - 1))
            throw new IllegalArgumentException("IllegalArgumentException: invalid row-number found");
        /*return the entry at given row number*/
        return list.get(rowNumber - 1);
    }

    /**
     * Fetches all values for a given key in CSV file
     * @param key for which values to be fetched
     * @return list of value for given key in CSV file
     * */
    public final List<String> get(String key){
        /*stores the result*/
        List<String> list = new ArrayList<>(this.list.size());
        /*if key exist then fetch all values for this key*/
        if (this.keys.contains(key))
            for (Map<String, String> stringStringMap : this.list) list.add(stringStringMap.get(key));
        /*return the content*/
        return list;
    }


    /**
     * Creates an HTML file for a tabular visualization of given CSV file
     * @param filename name of HTML file
     * @param title title of the HTML file
     * @throws IllegalArgumentException if given filename is not valid (*.html)
     * @throws IOException if fails to open file for writing
     * */
    public final void toHTML(String filename, String title) throws IOException, IllegalArgumentException{
        /*validate the filename*/
        if (!filename.endsWith(".html"))
            throw new IllegalArgumentException("IllegalArgumentException: invalid filename provided");
        String header = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\t<style>\n" +
                "\t\tbody {\n" +
                "\t\t\tpadding: 2%;\n" +
                "\t\t}\n" +
                "\t\ttable {\n" +
                "\t\t\tfont-family: arial, sans-serif;\n" +
                "\t\t\tborder-collapse: collapse;\n" +
                "\t\t\twidth: 90%;\n" +
                "\t\t}\n" +
                "\t\ttd, th {\n" +
                "\t\t\tborder: 1px solid black;\n" +
                "\t\t\ttext-align: left;\n" +
                "\t\t\tpadding: 8px;\n" +
                "\t\t}\n" +
                "\t\ttr:nth-child(even) {\n" +
                "\t\t\tbackground-color: #dddddd;\n" +
                "\t\t}\n" +
                "\t</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\t<h2>" + Character.toUpperCase(title.charAt(0)) + title.substring(1) + "</h2>\n" +
                "\t<table>\n";
        String footer = "\n\t</table>\n</body>\n</html>";
        /*create a string builder to create rows*/
        StringBuilder stringBuilder = new StringBuilder();
        /*iterate through keys and add table header*/
        stringBuilder.append("\t\t<tr>\n");
        for (String key : this.keys)    stringBuilder.append("\t\t\t<th>").append(key).append("</th>\n");
        stringBuilder.append("\t\t</tr>\n");
        /*iterate through each row in table*/
        for (Map<String, String> map : this.list){
            stringBuilder.append("\t\t<tr>\n");
            for (String key : this.keys)    stringBuilder.append("\t\t\t<td>").append(map.get(key)).append("</td>\n");
            stringBuilder.append("\t\t</tr>");
        }
        /*write the content to file*/
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
        bw.write(header);
        bw.write(stringBuilder.toString());
        bw.write(footer);
        bw.close();
    }

    /**
     * Converts the CSVReader Object into a readable-string
     * @return the string representation of given CSVReader
     * */
    @Override
    public String toString(){
        /*get the size of the list*/
        List<String> items = new ArrayList<>(this.list.size());
        /*iterate through each map and append it to items*/
        for (Map<String,String> map : this.list)    items.add(map.toString());
        /*return the value*/
        return "[" + String.join(",\n", items) + "]";
    }

    /**
     * Compares the content of CSVReaders independent of order in file i.e. checks if two files has the same information
     * @return true if object contains the same content
     * */
    @Override
    public boolean equals(Object obj) {
        /*if object is null then we cannot compare*/
        if (obj == null)    return false;
        /*cannot compare objects of two different types*/
        if (!(obj instanceof csv.CSVReader))    return false;
        /*cast object to csv reader*/
        csv.CSVReader reader = (csv.CSVReader)obj;
        /*get the keys of the reader*/
        Set<String> readerKeys = reader.getKeys();
        /*check the size of readerKeys and keys - if number of keys are different then data is different*/
        if (this.keys.size() != readerKeys.size())  return false;
        /*get a copy of current key and sort them*/
        Set<String> thisKeys = this.getKeys();
        /*compare the content - if keys are different then so the content*/
        if (!thisKeys.equals(readerKeys))   return false;
        /*now we have the same keys for both csv-readers; next we compare the content*/
        for (String key : thisKeys) {
            /*get the values for this reader*/
            List<String> thisValues = this.get(key);
            /*get the values for reader*/
            List<String> thatValues = reader.get(key);
            /*sort both list and compare data*/
            Collections.sort(thisValues); Collections.sort(thatValues);
            /*if values doesn't match then so information*/
            if (!thisValues.equals(thatValues)) return false;
        }
        /*all conditions are satisfied*/
        return true;
    }


    /*processes the given string by delimiting the values using , and and merging pieces*/
    private String[] process(String line, long lineNumber) throws ParseException{
        /*check if string starts or ends with delimiter*/
        if (!Pattern.matches("^([^,\"]*|\"[^\"]*\")(,([^,\"]*|\"[^\"]*\"))*$", line))
            throw new ParseException("ParseException: found non-terminating string value at line " + lineNumber + ".", 1);
        /*stores the intermediate information*/
        List<String> list = new ArrayList<>();
        /*split the line w.r.t ,*/
        String[] values = line.split(",");
        /*fetch the number of pieces*/
        int length = values.length;
        /*merge values w.r.t "*/
        for (int i = 0; i < length; ++i){
            /*if this part doesn't contain a " then we take it as single part*/
            if (!values[i].startsWith("\"")){
                list.add(values[i]);
                continue;
            }
            /*create a new list to store pieces*/
            List<String> tempList = new ArrayList<>();
            /*we look for remaining parts which contains "*/
            while (i < length && !values[i].endsWith("\"")) tempList.add(values[i++]);
            /*check if reach to end of string*/
            if (i == length)
                throw new ParseException("ParseException: found non-terminating string value at line " + lineNumber + ".", (int)lineNumber);
            /*join the items of tempList using , and add result to list*/
            list.add(String.join(",", tempList) + values[i]);
        }
        /*return the array representation of list*/
        return list.toArray(new String[]{});
    }
}
