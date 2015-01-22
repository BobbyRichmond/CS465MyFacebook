
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;

public class access{
	//initialize global variables
	public static String viewby= "";
	private static ArrayList<String> listofFriends = new ArrayList<String>();
	public static HashMap<String,ArrayList<String>> listofLists = new HashMap<String,ArrayList<String>>();
	public static ArrayList<String> templist = new ArrayList<String>();
	public static ArrayList<Picture> picArray = new ArrayList<Picture>();
	
	//method for adding a friend to the friendlist                                                      
	public static void friendadd(String friendName, FileWriter audit) throws IOException{
		
		//check for existing friend                                                                   
		if(listofFriends.contains(friendName)){
			System.out.println("That friend already exists");
		}
		//check for viewby for root owner views.                                                      
		if(viewby.equals("root")){
			listofFriends.add(friendName);
			audit.write("Friend "+ friendName + " added");
		}
		//System.out.println(viewby + " : viewer ");
		else{
		System.out.println("You must be the profile owner to add a friend");
		}
	}

	//method to check and add a viewby for a friend
	public static String viewbyCommand(String viewer, FileWriter audit) throws IOException{
		//any viewing
		if(!viewby.equals("")){
			System.out.println("only one person can view the profile at a time");
			audit.write("Login failed: simultaneous login not permitted");
			return viewby;
		}
		//Does the friend exist?
		if(!listofFriends.contains(viewer)){
			System.out.println("That friend doesnt exist");
			audit.write("login faild: friend does not exist");
			return viewby;
		}
		//viewby is now set
		viewby=viewer;
		audit.write("Friend " + viewby + " views the profile");
		return viewby;
	}

	//method for finding what command is being asked, and directing to appropriate method
	public static boolean commandFinder(String command, boolean finished, FileWriter audit) throws IOException{
		String[] tokens;
		String[] brokenToken;
		tokens = command.split(" ", 2);
		brokenToken = command.split(" ", 3);
		//	System.out.println("token 0: " + tokens[0]);
		if(tokens[0].equals("friendadd"))
		{
			friendadd(tokens[1], audit);
		}
		else if(tokens[0].equals("viewby"))
		{
			viewby=viewbyCommand(tokens[1], audit);
		}
		else if(tokens[0].equals("logout"))
		{
			if(!viewby.equals("")){
				viewby="";
				System.out.println("logout successful.");
				audit.write("Friend "+ viewby +" logged out");
			}
			else
				System.out.println("No one is currently logged in.");
		}
		else if(tokens[0].equals("listadd"))
		{ 
			//	System.out.println("inside listadd");
			listadd(tokens[1], audit);
		}
		else if(tokens[0].equals("friendlist"))
		{
			friendList(brokenToken[1],brokenToken[2],audit);
		}
		else if(tokens[0].equals("postpicture"))
		{ 
			postPicture(tokens[1],audit);
		}
		else if(tokens[0].equals("readcomments"))
		{ 
			//POSSIBLE IF STATMENT HERE if(readcomments(tokens[1])){
			readComments(tokens[1],audit);
		}
		else if(tokens[0].equals("writecomments"))
		{ 
			writeComments(brokenToken[1],brokenToken[2],audit);
		}
		else if(tokens[0].equals("chlst"))
		{ 
			chlst(brokenToken[1],brokenToken[2],audit);
		}
		else if(tokens[0].equals("chmod"))
		{
			chmod(brokenToken[1],brokenToken[2],audit);
		}
		else if(tokens[0].equals("chown"))
		{
			chown(brokenToken[1], brokenToken[2],audit);
		}
		else if(tokens[0].equals("end"))
		{
			finished=true;
			viewby="";
			end();
			//proceed to output stuffs
		}
		else
		{
			System.out.println("Command not found");
		}
		return finished;	
	}

	//finalizing all output files NOT FINISHED
	private static void end() throws IOException {
		FileWriter friends = new FileWriter("friends.txt");
		for(String f : listofFriends){
			friends.write(f);
		}
		FileWriter lists = new FileWriter("lists.txt");
		for(String l : listofLists.keySet()){
			lists.write(l + " " + listofLists.get(l));
		}
		friends.close();
		lists.close();
	}

	//method for appending strings to a picture file
	private static void writeComments(String fileName, String content, FileWriter audit) throws IOException {
		if(viewby.equals("")){
			System.out.println("someone must be viewing the profile");
			audit.write("no one is viwing the profile");
			return;
		}
		if(fileName.equals("friends.txt")||fileName.equals("audit.txt")||fileName.equals("lists.txt")||fileName.equals("pictures.txt")){
			System.out.println("you can not use the filename :" + fileName);
			audit.write("filename "+fileName+" is not allowed to be written to");
			return;
		} 
		for(int i=0; i<picArray.size();i++){
			if(picArray.get(i).picName.equals(fileName)){
				if(viewby.equals("root")){
					picArray.get(i).write(content);
					audit.write("Friend "+viewby+" wrote to "+fileName+": "+ content);
					return;
				}
				if(picArray.get(i).owner.equals(viewby)){
					if(picArray.get(i).getOwnerWritePerms()){
						picArray.get(i).write(content);
						audit.write("Friend "+viewby+" wrote to "+fileName+": "+ content);
						return;
					}
				}
				templist=picArray.get(i).getList();
				if(templist.contains(viewby)){
					if(picArray.get(i).getListWritePerms()){
						picArray.get(i).write(content);
						audit.write("Friend "+viewby+" wrote to "+fileName+": "+ content);
						return;
					}
				}
				if(picArray.get(i).getGlobalWritePerms()){
					picArray.get(i).write(content);
					audit.write("Friend "+viewby+" wrote to "+fileName+": "+ content);
					return;
				}
				else{
					System.out.println("You do not have permission to write to this file.");
					audit.write("friend " +viewby+" denied permission to write to this picture");
				}
			}
		}
		System.out.println("Picture not found");
		audit.write("picture not found");
	}


	//method for reading the comments from a picture file
	private static void readComments(String fileName, FileWriter audit) throws IOException {
		if(viewby.equals("")){
			System.out.println("someone must be viewing the profile");
			audit.write("no one is viewing the profile");
			return;
		}
		if(fileName.equals("friends.txt")||fileName.equals("audit.txt")||fileName.equals("lists.txt")||fileName.equals("pictures.txt")){
			System.out.println("you can not use the filename :" + fileName);
			audit.write("filename "+fileName+" not allowed");
			return;
		} 
		for(int i=0; i<picArray.size();i++){
			if(picArray.get(i).picName.equals(fileName)){
				if(viewby.equals("root")){
					System.out.print(picArray.get(i).body);
					audit.write("Friend "+viewby+" reads "+fileName+"as: ");
					return;
				}
				if(picArray.get(i).owner.equals(viewby)){
					if(picArray.get(i).getOwnerReadPerms()){
						System.out.print(picArray.get(i).body);
						audit.write("Friend "+viewby+" reads "+fileName+"as: ");
						return;
					}
				}
				templist=picArray.get(i).getList();
				if(templist.contains(viewby)){
					if(picArray.get(i).getListReadPerms()){
						System.out.print(picArray.get(i).body);
						audit.write("Friend "+viewby+" reads "+fileName+"as: ");
						return;
					}
				}
				if(picArray.get(i).getGlobalReadPerms()){
					System.out.print(picArray.get(i).body);
					audit.write("Friend "+viewby+" reads "+fileName+"as: ");
					return;
				}
				else{
					System.out.println("You do not have permission to read this file.");
					audit.write("Friend: "+viewby+" does not have permission to read this file");
				}
			}
		}
		System.out.println("Picture not found");
	}


	//method for changing the list of a particular picture
	private static void chlst(String fileName, String newList, FileWriter audit) throws IOException {
		if(viewby.equals("")){
			System.out.println("someone must be viewing the profile");
			audit.write("no one is viewing the profile");
			return;
		}
		if(fileName.equals("friends.txt")||fileName.equals("audit.txt")||fileName.equals("lists.txt")||fileName.equals("pictures.txt")){
			System.out.println("you can not use the filename :" + fileName);
			audit.write("you can not use the filename : " +fileName);
			return;
		} 

		for(int i=0; i<picArray.size();i++){
			if(picArray.get(i).picName.equals(fileName)){
				if(viewby.equals("root")){
					//root doesnt need to be in the list setList(blah);
					if(listofLists.containsKey(newList)){
						templist=listofLists.get(newList);
						picArray.get(i).setList(templist);
						audit.write("friend " +viewby+" changed the list of " +fileName+ " to " + newList);
						return;
					}
					if(picArray.get(i).owner.equals(viewby)){
						if(listofLists.containsKey(newList)){
							templist=listofLists.get(newList);
							if(templist.contains(viewby)){
								picArray.get(i).setList(templist);
								audit.write("friend " +viewby+" changed the list of " +fileName+ " to " + newList);
								return;
							}
							else{
								System.out.println("The picture owner must be part of the list");
								audit.write("friend " +viewby+" is not part of that list access denied");
							}
						}
						else{
							System.out.println("That list doesnt exist.");
							audit.write("List "+newList+" does not exist");
						}
					}
					else{
						System.out.println("You must be the profile owner or the picture owner to chlst");
						audit.write("friend " +viewby+" denied permission to chlst");
						return;
					}
				}
			}
		}
		System.out.println("Picture not found.");
		audit.write("Picture not found");
	}


	//command for changing permissions of a particular picture
	private static void chmod(String fileName, String newPerms, FileWriter audit) throws IOException {
		if(viewby.equals("")){
			System.out.println("someone must be viewing the profile");
			audit.write("No one is viewing the profile");
			return;
		}
		if(fileName.equals("friends.txt")||fileName.equals("audit.txt")||fileName.equals("lists.txt")||fileName.equals("pictures.txt")){
			System.out.println("you can not use the filename :" + fileName);
			audit.write("friend " +viewby+" denied permission to chmod: FileName incorrect");
			return;
		} 
		for(int i=0; i<picArray.size();i++){
			if(picArray.get(i).picName.equals(fileName)){
				if(picArray.get(i).owner.equals(viewby)){
					//change permissions
					picArray.get(i).setOwnPerms(newPerms);
					audit.write("friend " +viewby+" changed the permissions of " +fileName+ " to " + newPerms);
					return;
				}
				else if(viewby.equals("root")){
					//change profOwner permissions? does there need to be two? who cares
					picArray.get(i).setRootPerms(newPerms);
					audit.write("friend " +viewby+" changed the permissions of " +fileName+ " to " + newPerms);
					return;
				}
				else{
					System.out.println("You must be the profile owner or the picture owner to chmod");
					audit.write("friend " +viewby+" denied permission to chmod");
					return;
				}
			}
		}
		System.out.println("Picture not found.");
		audit.write("Picture not found");
	}


	// method to post a picture with a given fileName
	private static void postPicture(String fileName, FileWriter audit) throws IOException {
		//read the file, store it as a 'Picture'
		if(viewby.equals("")){
			System.out.println("someone must be viewing the profile");
			audit.write("someone must be viewing the profile");
			return;
		}
		if(fileName.equals("friends.txt")||fileName.equals("audit.txt")||fileName.equals("lists.txt")||fileName.equals("pictures.txt")){
			System.out.println("you can not use the filename :" + fileName);
			audit.write("filename: "+fileName+" not allowed");
			return;
		} 
		for(int i=0; i<picArray.size();i++){
			if(picArray.get(i).picName.equals(fileName)){
				System.out.println("That picture already exists");
				audit.write("That picture name already exists");
				return;
			}
		}
		Picture newPic= new Picture(fileName, viewby, "root", listofFriends);
		picArray.add(newPic);
		audit.write("Picture "+fileName+" with owner "+viewby+" and default permissions created");

	}

	//method to create a new list and add it to the list of lists
	public static void listadd(String list, FileWriter audit) throws IOException{
		ArrayList<String> newList = new ArrayList<String>();
		//the first element of the arraylist is the name of the list
		if(viewby.equals("root")){
			if(listofLists.containsKey(list)){
				System.out.println("That list already exists.");
				audit.write("The list: "+newList+" already exists");
			}
			else{
				listofLists.put(list,newList);
				audit.write("List" + list +" added");
			}
		}
		else{
			System.out.println("You must be the root owner to add a list.");
			audit.write("friend " +viewby+" denied permission to addlist");
		}
	}

	//method for adding a friend to an existing list
	public static void friendList(String friend, String list, FileWriter audit) throws IOException{
		ArrayList<String> templist = new ArrayList<String>();
		if(viewby.equals("root")){
			if(listofFriends.contains(friend)){
				if(listofLists.containsKey(list)){
					System.out.println("found the list");
					templist=listofLists.get(list);
					templist.add(friend);
					listofLists.put(list,templist);
					audit.write("Friend " + friend + " added to list " + list);
				}
				else{
					System.out.println("That list does not exist");
					audit.write("The list: "+list+" does not exist");
				}
			}
			else{
				System.out.println("That friend does not exist");
				audit.write("friend " +friend+" does not exist");
			}
		}
		else{
			System.out.println("You must be the profile owner to add a friend to a list");
			audit.write("friend " +viewby+" denied permission add a friend to a list");
		}
	}

	//method for changing the owner of a given picture
	public static void chown(String fileName,String newOwner, FileWriter audit) throws IOException{
		if(viewby.equals("")){
			System.out.println("someone must be viewing the profile");
			audit.write("no one is viewing the profile");
			return;
		}
		if(fileName.equals("friends.txt")||fileName.equals("audit.txt")||fileName.equals("lists.txt")||fileName.equals("pictures.txt")){
			System.out.println("you can not use the filename :" + fileName);
			audit.write("filename " +fileName+" is not allowed to chown");
			return;
		} 
		for(int i=0; i<picArray.size();i++){
			if(picArray.get(i).picName.equals(fileName)){
				if(picArray.get(i).owner.equals(viewby)||viewby.equals("root")){
					picArray.get(i).setOwner(newOwner);
					audit.write("Owner of "+fileName+" changed to "+newOwner);
					return;
				}
				else{
					System.out.println("You must be the profile owner or the picture owner to chown");
					audit.write("friend " +viewby+" denied permission to chown");
					return;
				}
			}
		}
		System.out.println("Picture not found.");
		audit.write("Picture not found");
	}

	//driver of the project
	public static void main(String[] args)throws IOException{
		FileWriter audit = new FileWriter("Audit.txt");
		String command;
		boolean finished=false;
		//read an input file from command line
		if(args.length > 0){
			File inputFile = new File(args[0]);
			Scanner fileScanner = new Scanner(inputFile);
			//line by line read the file for each command and vars
			command=fileScanner.nextLine();
			if(!command.equals("friendadd root")){
				System.out.println("error first line must add root user");
				return;
			}
			else{
				//go to friends and add root
				viewby="root";
				friendadd("root", audit);
				audit.write("Friend root added");

			}
			command=fileScanner.nextLine();
			if(!command.equals("viewby root")){
				System.out.println("error second line must viewby root user");
			}
			else{
				viewby="root";
				audit.write("Friend root views the profile");
			}
			while(fileScanner.hasNextLine()){
				command = fileScanner.nextLine();
				//System.out.println(command + " :is in command");
				commandFinder(command,finished, audit);
			}
			fileScanner.close();
			audit.close();
			return;
		}
		else
			System.out.println("no input file was detected");
	}
}
