/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data_storage_assignment_2;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile ;
import data_storage_assignment_2.functions;


/**
 *
 * @author abdelrhman
 */
public class Data_Storage_Assignment_2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO code application logic here
        String filename = "";
        int numberOfRecords = 0 ;
        Scanner reader = new Scanner(System.in);
        
        System.out.println("Please choose and action to do");
        System.out.println("1)Create New File \n2)Open Existing file");
        int choice = 0;
        choice = reader.nextInt();
        
        if (choice == 1)
        {
            System.out.println("Enter The name of the file: ");
            filename = reader.next();

            System.out.println("Enter Number of Records in the file: ");
            numberOfRecords = reader.nextInt();

            functions.CreateRecordsFile(filename,numberOfRecords);   
        }
        
        else if(choice == 2)
        {
            String file = "filled_data_final";
            filename = file ;
            
        }
        functions.menue(filename);
        
        
    }
    
}
