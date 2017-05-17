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
import java.util.Arrays;
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
                for (int j = 0 ; j <11 ; j ++)
                filedata.writeInt(-1);
            }
            else{
                for (int j = 0 ; j <11 ; j ++)
                {
                    if (j==1) filedata.writeInt(i);
                    else filedata.writeInt(-1);
                }
            }
        }
        //System.out.println("File name: " + filename + " Records:" + numberOfRecords);
    }
    
    static void DisplayIndexFileContent (String filename) throws FileNotFoundException, IOException{
           // System.out.println("|Node|Offset|Left Child|Right Child|");
            RandomAccessFile filedata = new RandomAccessFile(filename+".bin", "rw");
            for (int j = 0 ; j < filedata.length()/(11*4) ; j++)
            {
                filedata.seek(j*(11*4));
                for (int i = 0 ; i < 11 ; i ++)
                {
                    System.out.print("|  " +filedata.readInt()+" |");
                }                   
                System.out.println("");
            }
    }


    static int InsertNewRecordAtIndex (String filename, int Key, int ByteOffset) throws FileNotFoundException, IOException{
        RandomAccessFile filedata = new RandomAccessFile(filename+".bin", "rw");
        int empty_node ;
        int[][] keys_values = new int[5][2] ;
        int[] keys = new int[5];
        filedata.seek(4);
        empty_node = filedata.readInt();
        keys_values[0][0] = keys[0] = Key;
        keys_values[0][1] = ByteOffset;
        int count = 0 ;
        for (int j = 0 ; j < filedata.length()/(11*4) ; j++)
            {
                filedata.seek(j*(11*4)+4);
                for (int i = 0 ; i < 11 ; i ++)
                {
                   if (i>0){
                       int key , value;
                       key = filedata.readInt();
                       value = filedata.readInt();
                       while (key !=-1 && value != -1){
                           keys_values[i][0] = keys[i] = key;
                           keys_values[i][1] = value;
                       }
                   }
                }
                
                Arrays.sort(keys);
                filedata.seek(j*(11*4));
                for (int i = 0 ; i <keys.length ; i++){
                    for (int k = 0 ; k < keys_values.length ; k++){
                        if (keys[i] == keys_values[j][0] && keys[i] !=-1){
                            filedata.writeInt(keys[i]);
                            filedata.writeInt(keys_values[i][1]);   
                            count ++ ;
                        }
                    }
                }
                for (int i = 0 ; i < 5 - keys.length ; i++) filedata.writeInt(keys[-1]);
                
            }
        
        
        int indx = 0;
        filedata.seek(((11*4)*empty_node)+4);
        indx = filedata.readInt();
        /*
        filedata.seek((11*4)*empty_node);
        int node_type = filedata.readInt();
        for (int k = 12*4*empty_node ; k < 12*4*empty_node+10*4 ; k+= 8 )
        {
                int temp = filedata.readInt() ;
                if (temp == -1 || temp == indx)
                {
                    filedata.seek((11*4)*empty_node - 4 );
                    filedata.writeInt(Key);
                    filedata.writeInt(ByteOffset);
                }
                
            
        }
//        filedata.writeInt(Key);
//        filedata.writeInt(ByteOffset);
//        filedata.writeInt(-1);
//        filedata.writeInt(-1);
        
        if (node_type == -1) node_type = 0 ;
        filedata.seek((11*4)*empty_node);
        filedata.writeInt(node_type);
        
        */
        if (count == 5){
            filedata.seek(4);
            filedata.writeInt(indx);
            filedata.seek(((11*4)*empty_node));
            filedata.writeInt(1);
        }
        
        return empty_node ;
        
        
    }

    static void menue(String filename) throws IOException, InterruptedException{
        boolean flag = true;
        Scanner reader = new Scanner(System.in);
        while (flag!=false){
            System.out.println("Please choose and action to do");
            System.out.println("1)Add New Record \n2)Display File content \n"
                + "3)Search \n4)Exit");
            int choice = 0;
            choice = reader.nextInt();
            
            if(choice == 4){
                flag = false;
            }
            else if(choice ==1){
                RandomAccessFile filedata = new RandomAccessFile(filename+".bin", "rw");
                filedata.seek(4);
                int checker = filedata.readInt();
                if (checker != -1)
                {
                    System.out.println("Enter Key");
                    int key = reader.nextInt();
                    System.out.println("Enter Byte offset");
                    int byteoffset = reader.nextInt();
                    int return_value  = InsertNewRecordAtIndex(filename,key,byteoffset);
                    if (return_value == -1) System.err.println("You Can't add new record; Index is Full");
                    choice = 0 ;
                   // Updatetree(filename,1, key, return_value);
                }
                else {
                    System.err.println("Index File is full");
                }
                
            }
            
            else if(choice ==2){
                functions.DisplayIndexFileContent(filename);
                choice = 0 ;
            }
            
            else if(choice ==3){
                System.out.println("Enter a Key to search for");
                int key = reader.nextInt();
                SearchRecordInIndex(filename, key , 1);
            }
            
        }
    }
    
    
    static void SearchRecordInIndex (String filename, int Key , int start) throws FileNotFoundException, IOException {
        RandomAccessFile filedata = new RandomAccessFile(filename+".bin", "rw");
        //int number_of_records = (int) (filedata.length() / 16) ;
        int offset = -1 ;
        int node_size ;
        node_size = 11*4;
        filedata.seek(node_size*start);
        int node_type = filedata.readInt();
       // System.err.println("Node Type = "+node_type);
        if (node_type == 0 ){
            for (int i = 0 ; i < node_size ; i+=8)
            {
                if( filedata.readInt() == Key){
                    offset = filedata.readInt();
                }
            }
             System.err.println("Search result is: " + offset);
        }
        
        else if (node_type == 1) {
            int first_match ;
            int next_node = offset ;
            for (int i = 0 ; i < node_size ; i+=8)
            {
                //System.err.println("Temp = "+ filedata.readInt());
                if(filedata.readInt()>= Key){
                    first_match = filedata.readInt();
                    if (next_node == -1 || next_node >= first_match){
                        next_node = first_match;
                    }
                }
            }
            
            if (next_node != -1){
             //   System.err.println("Next node is " + next_node);
                SearchRecordInIndex(filename, Key , (next_node));
            }
            
            else if (next_node == -1) System.err.println("Search result is: " + offset);
        }
            
           
        
        
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
