/*package declaration*/
package csv;

/*necessary imports*/
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * CSVWriter instances can be used to create or manipulate the content of a CSV file
 * @author Shubham Panchal(http://shubhampanchal.herokuapp.com)
 * @version 1.0
 * */
public final class CSVWriter {
    /*each csv writer has a set of keys*/
    private final Set<String> keys;
    /*each csv writer has a list of maps*/
    private final List<Map<String, String>> list;


    /**
     * Creates a CSVWriter from given set of keys and and mappings for each key
     * @param keys set of keys for csv file
     * @throws IllegalArgumentException if keys is null
     * */
    public CSVWriter(Set<String> keys) throws IllegalArgumentException {
        /*check if any of the argument is null*/
        if (keys == null)   throw new IllegalArgumentException("IllegalArgumentException: keys cannot be null");
        /*initialize the list*/
        this.list = new ArrayList<>();
        /*initialize keys*/
        this.keys = new HashSet<>(keys);
    }

    /**
     * Creates a CSVWriter from given CSVReader
     * @param csvReader reader from which writer is to be created
     * @throws IllegalArgumentException if csvReader is null
     * */
    public CSVWriter(CSVReader csvReader) throws IllegalArgumentException{
        /*check if csvReader is null*/
        if (csvReader == null)  throw new IllegalArgumentException("IllegalArgumentException: csvReader cannot be null");
        /*initialize keys*/
        this.keys = csvReader.getKeys();
        /*initialize list*/
        this.list = new ArrayList<>();
        /*get the number of rows*/
        int rowCount = csvReader.getRowCount();
        /*iterate through set of list*/
        for (int i = 1; i <= rowCount; ++i)
            this.list.add(new HashMap<>(csvReader.get(i)));
    }

    /**
     * Adds the given set of values to the CSVWriter
     * @param values set of values to be added
     * @throws IllegalArgumentException if key-set of given map doesn't match with key-set for CSVWriter or if values is null
     * */
    public void addRow(Map<String, String> values) throws IllegalArgumentException{
        /*check values*/
        if (values == null) throw new IllegalArgumentException("IllegalArgumentException: values cannot be null");
        /*compare the key-sets*/
        if(!this.keys.equals(values.keySet()))   throw new IllegalArgumentException("IllegalArgumentException: key-set doesn't match with CSVWriter's key-set");
        /*add this mapping to list*/
        this.list.add(new HashMap<>(values));
    }

    /**
     * Removes the mapping for given index in CSVWriter
     * @param index zero-based for which entry-row to be deleted
     * @throws IllegalArgumentException if index is out of Bound
     * */
    public void removeRow(int index) throws IllegalArgumentException{
        /*check if index is out of bound*/
        if (!(0 <= index && index < this.list.size())) throw new IllegalArgumentException("IllegalArgumentException: index out of bound");
        /*remove the entry at given index*/
        this.list.remove(index);
    }

    /**
     * Searches for the row which contains the given key-value mapping
     * @param key key of (key,value) search
     * @param value value of (key, value) search
     * @throws IllegalArgumentException if either key or value is null
     * @return zero-based index of 1st row containing (key,value) mapping if exist; -1 otherwise
     * */
    public int indexOf(String key, String value) throws IllegalArgumentException{
        /*check if key is null*/
        if (key == null) throw new IllegalArgumentException("IllegalArgumentException: key cannot be null");
        if (value == null) throw new IllegalArgumentException("IllegalArgumentException: value cannot be null");
        /*stores the index*/
        int index = 0; boolean found = false;
        /*iterate through each row*/
        for (Map<String, String> row : this.list) {
            String val = row.getOrDefault(key, null);
            if (val != null && val.equals(value))
                /*update found status*/
                found = true;
            /*increase the index by 1*/
            index++;
        }
        /*return the appropriate values*/
        return found ? index : -1;
    }

    /**
     * Adds given key to set of keys
     * @param key key for insertion
     * @param values map of row to value for this key for all pre-existing zero-based-index rows
     * @throws IllegalArgumentException if either key or value is null or if key already exist or if values aren't provided for all pre-existing entries
     * */
    public void addKey(String key, Map<Integer, String> values){
        /*check if key is null*/
        if (key == null) throw new IllegalArgumentException("IllegalArgumentException: key cannot be null");
        /*check if values are null*/
        if (values == null) throw new IllegalArgumentException("IllegalArgumentException: value cannot be null");
        /*check if key already exist*/
        if (this.keys.contains(key))    throw new IllegalArgumentException("IllegalArgumentException: duplicate key");
        /*check size of values*/
        if (values.size() < this.list.size()) throw new IllegalArgumentException("IllegalArgumentException: Found fewer values than expected. (Expected: " + values.size() + ", Found: " + this.list.size() + ")");
        /*check size of values*/
        if (values.size() > this.list.size()) throw new IllegalArgumentException("IllegalArgumentException: Found more values than expected. (Expected: " + values.size() + ", Found: " + this.list.size() + ")");
        /*add key to set of keys*/
        this.keys.add(key);
        /*add values corresponding to this key for all pre-existing entries*/
        for (Integer row : values.keySet())
            this.list.get(row).put(key, values.get(row));
    }

    /**
     * Removes the key from CSVWriter if exist
     * @param key key for which values to be removed
     * @return list of values associated with given if exist; null or otherwise
     * */
    public final List<String> removeKey(String key){
        /*check if key-set contains given key*/
        if (!this.keys.contains(key)) return null;
        /*get values for given key*/
        List<String> returnValue = this.get(key);
        /*iterate through map and remove all value for this key*/
        for (Map<String, String> stringStringMap : this.list) stringStringMap.remove(key);
        /*remove the key from key-set*/
        this.keys.remove(key);
        /*return the computed value*/
        return returnValue;
    }

    /**
     * Checks if CSVWriter contains given key
     * @param key key to be searched for
     * @return true if CSVWriter contains given key
     * */
    public final boolean containsKey(String key){
        /*check if key-set contains given key*/
        return this.keys.contains(key);
    }

    /**
     * Fetches a copy of list of keys (tag of CSV file)
     * @return list of keys (tag of CSV file)
     * */
    public final List<String> getKeys() { return new ArrayList<>(this.keys); }

    /**
     * Fetches the values for given row number
     * @param rowNumber for which record has to be fetched
     * @throws IllegalArgumentException if rowNumber is not in range[0...L - 1] where
     * L is the length of list i.e. number of entries
     * @return Map&le;String, String&ge; of key:value for each value in given row
     * */
    public final Map<String, String> get(int rowNumber) throws IllegalArgumentException{
        /*check if row number is in bound*/
        if ( !(0 <= rowNumber && rowNumber <= list.size() - 1))
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
     * Creates a CSV file for given CSVWriter
     * @param filename name of CSV file
     * @throws IllegalArgumentException if given filename is not valid (*.csv)
     * @throws IOException if fails to open file for writing
     * */
    public void toCSV(String filename) throws IllegalArgumentException, IOException{
        /*validate the filename*/
        if (!filename.endsWith(".csv"))
            throw new IllegalArgumentException("IllegalArgumentException: invalid filename provided");
        /*open file for writing*/
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
        /*write comma separated keys*/
        bw.write(String.join(", ", this.keys)); bw.newLine();
        /*write values*/
        List<String> values;
        for (Map<String, String> map : this.list){
            values = new ArrayList<>();
            /*get all values for this map*/
            for (String key : this.keys)  values.add(map.get(key));
            /*add this to file*/
            bw.write(String.join(", ", values)); bw.newLine();
        }
        /*close the buffered-writer*/
        bw.close();
    }
    /**
     * Converts the CSVWriter Object into a readable-string
     * @return the string representation of given CSVWriter
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
     * Compares the content of CSVWriters independent of order i.e. checks if two writer has the same information
     * @return true if object contains the same content
     * */
    @Override
    public boolean equals(Object obj) {
        /*if object is null then we cannot compare*/
        if (obj == null)    return false;
        /*cannot compare objects of two different types*/
        if (!(obj instanceof csv.CSVWriter))    return false;
        /*cast object to csv writer*/
        csv.CSVWriter writer = (csv.CSVWriter)obj;
        /*get the keys of the writer*/
        List<String> writerKeys = writer.getKeys();
        /*check the size of writerKeys and keys - if number of keys are different then data is different*/
        if (this.keys.size() != writerKeys.size())  return false;
        /*sort the writerKeys*/
        Collections.sort(writerKeys);
        /*get a copy of current key and sort them*/
        List<String> thisKeys = this.getKeys();
        /*sort the current keys*/
        Collections.sort(thisKeys);
        /*compare the content - if keys are different then so the content*/
        if (!thisKeys.equals(writerKeys))   return false;
        /*now we have the same keys for both csv-writers; next we compare the content*/
        for (String key : thisKeys) {
            /*get the values for this writer*/
            List<String> thisValues = this.get(key);
            /*get the values for writer*/
            List<String> thatValues = writer.get(key);
            /*sort both list and compare data*/
            Collections.sort(thisValues); Collections.sort(thatValues);
            /*if values doesn't match then so information*/
            if (!thisValues.equals(thatValues)) return false;
        }
        /*all conditions are satisfied*/
        return true;
    }
    
}
