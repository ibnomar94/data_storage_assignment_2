/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data_storage_assignment_2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

/**
 *
 * @author abdelrhman
 */
public class functions {
    
    static void CreateRecordsFile (String filename, int numberOfRecords) throws IOException
    {
        File file = new File (filename+".bin");
        if (file.exists()) file.delete();
        file.createNewFile();
        RandomAccessFile filedata = new RandomAccessFile(file, "rw");
        
        for (int i = 1 ; i <= numberOfRecords ; i ++)
        {
            if (i==numberOfRecords){
                filedata.writeInt(-1);
                filedata.writeInt(0);
                filedata.writeInt(0);
                filedata.writeInt(0);
            }
            else{
                filedata.writeInt(i);
                filedata.writeInt(0);
                filedata.writeInt(0);
                filedata.writeInt(0);
            }
        }
        //System.out.println("File name: " + filename + " Records:" + numberOfRecords);
    }
    
    static void DisplayIndexFileContent (String filename) throws FileNotFoundException, IOException{
            System.out.println("|Node|Offset|Left Child|Right Child|");
            RandomAccessFile filedata = new RandomAccessFile(filename+".bin", "rw");
            for (int j = 0 ; j < filedata.length()/16 ; j++)
            {
                filedata.seek(j*16);
                System.out.print("|  " +filedata.readInt()+" |");
                //filedata.seek((j*8)+4);
                
                System.out.print("  " + filedata.readInt()+ "   |");
                //filedata.seek((j*8)+4);
                
                System.out.print("    " + filedata.readInt() + "     |" );
                //filedata.seek((j*8)+4);
                
                System.out.print("     " + filedata.readInt()+ "     |" );
                //filedata.seek((j*8)+4);
                               
                System.out.println("");
            }
    }


    static int InsertNewRecordAtIndex (String filename, int Key, int ByteOffset) throws FileNotFoundException, IOException{
        RandomAccessFile filedata = new RandomAccessFile(filename+".bin", "rw");
        int empty_node ;
        filedata.seek(0);
        empty_node = filedata.readInt();
        if (empty_node == -1){
            filedata.writeInt(empty_node);
            return empty_node;
        }
        else{
        int indx = 0;
        filedata.seek(16*empty_node);
        indx = filedata.readInt();
        filedata.seek(16*empty_node);
        filedata.writeInt(Key);
        filedata.writeInt(ByteOffset);
        filedata.writeInt(-1);
        filedata.writeInt(-1);
        
        filedata.seek(0);
        filedata.writeInt(indx);
        
        
        return empty_node ;
        }
        
    }

    static void menue(String filename) throws IOException, InterruptedException{
        boolean flag = true;
        Scanner reader = new Scanner(System.in);
        while (flag!=false){
            System.out.println("Please choose and action to do");
            System.out.println("1)Add New Record \n2)Display File content \n"
                + "3)Search \n4)Display Tree in order \n5)Clear Screen\n6)Exit");
            int choice = 0;
            choice = reader.nextInt();
            
            if(choice == 6){
                flag = false;
            }
            else if(choice ==1){
                System.out.println("Enter Key");
                int key = reader.nextInt();
                System.out.println("Enter Byte offset");
                int byteoffset = reader.nextInt();
                int return_value  = InsertNewRecordAtIndex(filename,key,byteoffset);
                if (return_value == -1) System.err.println("You Can't add new record; Index is Full");
                choice = 0 ;
                Updatetree(filename,1, key, return_value);
            }
            
            else if(choice ==2){
                functions.DisplayIndexFileContent(filename);
                choice = 0 ;
            }
            
            else if(choice ==3){
                System.out.println("Enter a Key to search for");
                int key = reader.nextInt();
                SearchRecordInIndex(filename, key);
            }
            
            else if(choice ==4){
                
            }
            
            else if(choice ==5){
                    for (int i = 0 ; i < 24 ; i++) System.out.println("");
            }
            
        }
    }
    
    
    static void SearchRecordInIndex (String filename, int Key) throws FileNotFoundException, IOException {
        RandomAccessFile filedata = new RandomAccessFile(filename+".bin", "rw");
        //int number_of_records = (int) (filedata.length() / 16) ;
        int offset = -1 ;
        //System.err.println(number_of_records);
        for (int i=16 ; i < filedata.length() ; i= i + 16){
            filedata.seek(i);
            int temp = filedata.readInt();
            if (temp == Key){
                filedata.seek(i+4);
                offset = filedata.readInt();
                break;
            }
        }
        for (int i = 0 ; i < 4 ; i++) System.out.println("");
        System.out.println("Search result is: " + offset);
        for (int i = 0 ; i < 4 ; i++) System.out.println("");
    }

    
    static void Updatetree(String filename ,int start , int key, int position) throws FileNotFoundException, IOException{
        RandomAccessFile filedata = new RandomAccessFile(filename+".bin", "rw");
        filedata.seek(start*16);
        int node_value = filedata.readInt();
        //System.err.println("NODE value = "+ node_value);
        filedata.seek(start*16+8);
        int left_child = filedata.readInt() ;
        filedata.seek(start*16+8+4);
        int right_child = filedata.readInt() ;
        //System.err.println( "Start is " + start +" NODE value = "+ node_value+ " Left child= "+ left_child + " Right child= "+ right_child);
        if(key > node_value){
            if(right_child == -1){
                filedata.seek(start*16+8+4);
                filedata.writeInt(position);
            }
            else{
                Updatetree(filename, right_child, key, position);
            }
        }
        else if (key < node_value){
            if (left_child == -1){
                filedata.seek(start*16+8);
                filedata.writeInt(position);
            }
            else{
                Updatetree(filename, left_child, key, position);
            }
            
        }
    }
}
