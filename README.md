# CSVParser-Java
A small Java API for working with comma-separated-values `*.csv` files.

# Features
1. Read CSV file and parse into java object for query.
2. Create CSV file from java object.
3. Generate HTML for tabular visualization of information.
4. Compare contents of CSV files on basis of information regardless of order. 

# Notes
Works with files containing comma separated non-escaped information with or without quotes.
For more information on type of info supported checkout the csvs directory for sample `*.csv` files
Also checkout the generated `*.html` files for corresponding csvs to get a feel of working of this API


# Documentation
A full documentation of csv package containing classes csv.CSVReader and csv.CSVWriter for reading/writing can be found at https://panchalshubham.github.io/CSVParser-Java/csv/package-summary.html (generated via JavaDoc tool)

# Examples
```java
/*
    Following code snippet shows the usage of CSVReader
    Let us say we have a file "records.csv" with the following content:
    ID,Address,Role,Contact
    01,"E-121 Main Street, ABC", Developer, +91-0123456789
    02,"E-122 Main Street, XYZ", Developer, +91-9087315346
    03,"E-123 Main Street, ABC", Developer, +91-7966512312
    04,"E-124 Main Street, XYZ", Developer, +91-7896488232
    05,"E-125 Main Street, ABC", Developer, +91-0782436945
    06,"E-126 Main Street, XYZ", Developer, +91-3256849425
 */
try{
  
    // Advanced editing operations can be done by creating a CSVWriter associated to a CSVReader

    // create a CSV Reader for /home/shubham/Desktop/records.csv file
    CSVReader reader = new CSVReader("/home/shubham/Desktop/records.csv");
    // print the content of csv reader
    System.out.println(reader);

    // get all keys in csv file
    Set<String> keys = reader.getKeys();
    // print the set of keys to console
    // [Role, Address, ID, Contact]
    System.out.println(keys);
    
    // get contacts for all records
    List<String> contacts = reader.get("Contact");
    // print the contacts
    // [+91-0123456789, +91-9087315346, +91-7966512312, +91-7896488232, +91-0782436945, +91-3256849425]
    System.out.println(contacts);
    
    // get the number of records read from csv files
    int count = reader.getRowCount();
    // print the content to console
    System.out.println("records-read: " + count);
    
    // get record for 1st entry from csv file
    Map<String, String> record1 = reader.get(1);
    // {Role=Developer, Address="E-121 Main Street ABC", ID=01, Contact=+91-0123456789}
    System.out.println(record1);
    
    // generate the HTML file for tabular visualization
    reader.toHTML("/home/shubham/Desktop/records.html","Title: Records");
} catch (IOException e){
    System.out.println("IOException: " + e.getMessage());
    e.printStackTrace();
} catch (ParseException e){
    System.out.println("ParseException: " + e.getMessage());
    e.printStackTrace();
}
```
```java

```
# Contribution
This is an open-source project. The source-code can be found under source folder. You can fork the repository, make changes and generate a pull request. 

# Contact
For any information feel free to contact shubhampanchal9773@gmail.com or http://shubhampanchal.herokuapp.com
