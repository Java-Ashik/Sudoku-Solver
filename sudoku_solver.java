// Ashik Rai
// Date: 26-07-2019

// If any suggestion or question mail me at rai_ashik7@yahoo.com
/* This is actually a problem from leetcode.
   the values are char and the empty cell stores '.' 
*/
// Importing necessary packages
import java.util.Scanner;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

class sudoku_solver{
	private Scanner in;		// for taking input
	private char[][] sudoku;	// To store the sudoku data
	private final int size;
	sudoku_solver(){
		size= 9;
		// Here we are creating the matrix instead of asking as input
		char [][] arr= {{'5','3','.','.','7','.','.','.','.'},
			{'6','.','.','1','9','5','.','.','.'},
			{'.','9','8','.','.','.','.','6','.'},
			{'8','.','.','.','6','.','.','.','3'},
			{'4','.','.','8','.','3','.','.','1'},
			{'7','.','.','.','2','.','.','.','6'},
			{'.','6','.','.','.','.','2','8','.'},
			{'.','.','.','4','1','9','.','.','5'},
			{'.','.','.','.','8','.','.','7','.'}};
		sudoku= arr;
	}
	private boolean start()	
	{
		// Here we are printing the sudoku with initial values.
		System.out.println("Input");
		print_sudoku();
		// This part we are checking if the the initial values of the sudoku are correct or not.
		for (int i= 0; i < size; i++)
			for(int j= 0; j < size; j++)
			{
				// check_H_V_C means check_Horizontal_Vertical_Cube
				// if the chech is false then it will return without solving because the sudoku is incorrect
				if (sudoku[i][j]!='.' && !check_H_V_C(i,j)){
					System.err.println("ERROR: Wrong initial values of sudoku");
					return false;
				}
			}
		// If all are fine then we will start solving the sudoku by calling a method called solve
		solve();
		System.out.println("Output");
		print_sudoku();
		return true;
	}
	
	// This method will solve the sudoku matrix 
	private boolean solve()
	{
		int i=0,j=0;
		
		/*
		 We are using list to store all the possible values that an empty cell can take in the matrix.
		 Since there are many such empty cells we are storing this list data in a hashmap.
		 Since the cell index values [i][j] is unique we are using the index [i][j] as the key in the hashmap 
		 and values as the list.
	
			List<Integer> list=  stores all the possible values a cell can take
			HashMap <String, List<Integer>> hmap= Stores all the list of the empty cells with cells index [i][j] as the key.
			List <String> keys=  stores the key value which needs to be removed from the hashmap because that cell was filled.
		*/		

		List<Integer> list= new ArrayList<Integer>();
		HashMap <String,List<Integer>> hmap= new HashMap<String,List<Integer>>();
		List<String> keys= new ArrayList<String>();

		// Here we are generating all the possible values that a cell can take and we are storing in the hasmap 
		// ... with key as the index of the cell and list to store all the possible values of that cell

		for (i= 0; i < size; i++)
			for(j=0; j < size; j++)
			{
				// We are calculating the list only if the cell is empty
				if (sudoku[i][j] == '.'){

					// Here we are getting list of possible values that a cell [i][j] can store
					list= get_list(i,j);
					// If the particular cell has only one value that can be assigned ..
					// .... then we directly assign that value to that cell
					if (list.size() == 1)
					{
						sudoku[i][j]= (""+list.get(0)).charAt(0);
						// After filling the empty cell we might need to update other list ..
						// ... in the same row, column or box
						update_map(hmap,i,j,list.get(0));
					}
					else	// if the current cell can take multiple values then we store that list in the hashmap
						hmap.put(i+""+j,list);
				}
			}

		// When we fill a cell with a value we will remove its data from the hashmap
		// so when sudoku is solve hashmap will be empty

		// Now we will loop untill all the cells are filled.
		// .. all the data in the hashmap is removed.
		while(hmap.size() > 0)
		{
			// Here we are iterating through the hashmap
			for(Entry<String,List<Integer>> entry:hmap.entrySet())
				// If there is any list with only one value then we will assign that value
				// .. to the respective cell, whose index is store as key.
				// ... and remove the respective data from the hashmap
				if (entry.getValue().size() == 1)
				{
					// calculating the index of the cell from the key
					String index= entry.getKey();
					i= Character.getNumericValue(index.charAt(0));
					j= Character.getNumericValue(index.charAt(1));

					// getting the value to assign to the cell from the list
					int val= entry.getValue().get(0);

					// assigning the value to the cell
					sudoku[i][j]= (""+entry.getValue().get(0)).charAt(0);
		
					// After filling an empty cell we need to update the row, column and the box
					update_map(hmap,i,j,entry.getValue().get(0));
					
					// Here we are storing the keys so that we can delete it later from the hashmap
					keys.add(index);
				}
			// Now for each key in the list 'keys' we are removing it from the hashmap
			for(String key: keys)
				hmap.remove(key);
			// Here we are clearing the list so that we can store new keys in next iteration
			keys.clear();
			
		}
		return true;
	}

	// This method will update the row, column and the box in the sudoku
	private void update_map(HashMap<String,List<Integer>> hmap,int i,int j,int val){
		for (int k=0;k<size; k++)
		{
			if (hmap.get(i+""+k) != null)
				hmap.get(i+""+k).remove(new Integer(val));
				
			if (hmap.get(k+""+j) != null)
				hmap.get(k+""+j).remove(new Integer(val));
		}
		int k= get_index(i);
		int l= get_index(j);
		i+= k;
		j+= l;
		for (; k < i; k++)
			for(; l < j; l++)
				if (hmap.get(k+""+l) != null)
					hmap.get(k+""+l).remove(new Integer(val));
		return;
	}

	// Here we are calculating what all possible values a cell can take given the cell index
	private List<Integer> get_list(int i, int j)
	{
		/*
			First we will fill the list with values from 1-9
			then we will see the horizontal , vertical and in the respective box and remove values
			from the list that are appearing in the row, column or in the respective box.
		*/
		List<Integer> list= new ArrayList<Integer>();
		for(int k= 0; k< size; k++,list.add(k));	// assigning values 1-9 to the list

		for(int k= 0; k < size; k++)
		{
			// cheking horizontal
			// removing the values from the list that appeared in the row	
			if (sudoku[i][k]!='.')
				list.remove(new Integer(Character.getNumericValue(sudoku[i][k])));	
			// removing the values from the list that appeared in the column
			if (sudoku[k][j] != '.')
				list.remove(new Integer(Character.getNumericValue(sudoku[k][j])));
			
		}
		int k= get_index(i);
		int l= get_index(j);
		for(int m= k; m < k+size/3; m++)
			for(int n= l; n < l+size/3; n++)
				// removing the values from the list that appeared in the box
				if (sudoku[m][n] != '.')
					list.remove(new Integer(Character.getNumericValue(sudoku[m][n])));
		return list;
	}

	// Here we are checking for the validaty of the sudoku values in horizontal, vertical and in the box/cube.
	public boolean check_H_V_C(int i, int j){
		char val= sudoku[i][j];
		for (int k= 0; k < size; k++)
		{
			// chekcking Horizontal
			if (k!=j)
				if (sudoku[i][k] == val)
					return false;
			// chekcking Vertical
			if (k!=i)
				if (sudoku[k][j] == val)
					return false;
		}

		//Cheking in the square cell
		int k= get_index(i);
		int l= get_index(j);

		for(int m= k; m < k+size/3; m++)
			for (int n= l;n<l+size/3; n++)
				if (m != i && n != j && sudoku[m][n] != '.' && sudoku[m][n] == val)
					return false;
			
		return true;
	}

	// Here we are calculating the start index of the box where the cell [i][j] belong
	private int get_index(int i){
		if (i <3)
			return 0;
		if (i < 6)
			return 3;
		return 6; 
	}


	// Here we are printing the sudoku
	public void print_sudoku(){
		for (int i=0;i<size;i++)
		{	
			for(int j= 0; j < size; j++)
				System.out.print(sudoku[i][j]+" ");
			System.out.println();
		}
			System.out.println();
	}
	public static void main(String[] args)
	{
		new sudoku_solver().start();
	}
}
