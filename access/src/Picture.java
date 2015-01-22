import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class Picture {
	//picture class
	public String[] ownerPerm= {"rw","--","--"};
	public String[] profileOwnerPerm= {"rw","--","--"};
	public String profOwner;
	public String owner;
	public String picName;
	public ArrayList<String> picList;
	public String body;
	
	//constructor
	public Picture(String pictName, String pOwner, String pProfOwner, ArrayList<String> pictList) throws FileNotFoundException{
		picName=pictName;
		owner=pOwner;
		profOwner=pProfOwner;
		picList=pictList;
		//create the bodhy of the picture
		File pictureFile = new File(picName);
		Scanner scan = new Scanner(pictureFile);
		while(scan.hasNextLine()){
			body+=scan.nextLine();
		}
	}
	public void setOwner(String newOwner){
		owner=newOwner;
	}
	public void setList(ArrayList<String> templist) {
		picList=templist;

	}
	public void setOwnPerms(String newPerms) {
		String[] newPermissions= newPerms.split(" ", 3);
		if(newPermissions[0].equals("rw")||newPermissions[0].equals("r-")||newPermissions[0].equals("-w")||newPermissions[0].equals("--"))
		{
			ownerPerm[0]=newPermissions[0];
		}
		else{
			System.out.println("incorrect permissions");
		}
		if(newPermissions[1].equals("rw")||newPermissions[1].equals("r-")||newPermissions[1].equals("-w")||newPermissions[1].equals("--"))
		{
			ownerPerm[1]=newPermissions[1];
		}
		else{
			System.out.println("incorrect permissions");
		}
		if(newPermissions[2].equals("rw")||newPermissions[2].equals("r-")||newPermissions[2].equals("-w")||newPermissions[2].equals("--"))
		{
			ownerPerm[2]=newPermissions[2];
		}
		else{
			System.out.println("incorrect permissions");
		}

	}
	public void setRootPerms(String newPerms) {
		String[] newPermissions= newPerms.split(" ", 3);
		if(newPermissions[0].equals("rw")||newPermissions[0].equals("r-")||newPermissions[0].equals("-w")||newPermissions[0].equals("--"))
		{
			profileOwnerPerm[0]=newPermissions[0];
		}
		else{
			System.out.println("incorrect permissions");
		}
		if(newPermissions[1].equals("rw")||newPermissions[1].equals("r-")||newPermissions[1].equals("-w")||newPermissions[1].equals("--"))
		{
			profileOwnerPerm[1]=newPermissions[1];
		}
		else{
			System.out.println("incorrect permissions");
		}
		if(newPermissions[2].equals("rw")||newPermissions[2].equals("r-")||newPermissions[2].equals("-w")||newPermissions[2].equals("--"))
		{
			profileOwnerPerm[2]=newPermissions[2];
		}
		else{
			System.out.println("incorrect permissions");
		}

	}
	public ArrayList<String> getList() {
		return picList;

	}
	public boolean getGlobalReadPerms() {
		if(ownerPerm[2].equals("rw")||ownerPerm[2].equals("r-")){
			return true;
		}
		return false;
	}
	public boolean getListReadPerms() {
		if(ownerPerm[1].equals("rw")||ownerPerm[1].equals("r-")){
			return true;
		}
		return false;
	}	
	public boolean getOwnerReadPerms() {
		if(ownerPerm[0].equals("rw")||ownerPerm[0].equals("-w")){
			return true;
		}
		return false;
	}
		public boolean getOwnerWritePerms() {
			if(ownerPerm[0].equals("rw")||ownerPerm[0].equals("-w")){
				return true;
			}
			return false;
		}
		public boolean getGlobalWritePerms() {
			if(ownerPerm[2].equals("rw")||ownerPerm[2].equals("-w")){
				return true;
			}
			return false;
		}
		public boolean getListWritePerms() {
			if(ownerPerm[1].equals("rw")||ownerPerm[1].equals("-w")){
				return true;
			}
		return false;
	}
		public void write(String content) {
			body+=content;	
		}
}