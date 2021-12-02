package telconomics.rdg.model;


import org.apache.commons.csv.CSVRecord;

public interface CSVSerializable {

    String[] mapToCSVRecord();

}
