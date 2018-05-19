import java.io.*;
import java.net.SocketException;
import java.util.*;
import org.apache.commons.net.SocketClient;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPFile;

public class Assn3 {
	
	private static FTPClient client = null;
	
    public static void main(String [] args) {
    	
    	boolean successful = false;
    	
        if (args.length < 2) {
            System.out.println("The command line arguments are: IP of a FTP server id:password [command]");
        }
        String server = args[0];
        System.out.println("Connecting to the server: " + server);
        String [] s = args[1].split(":");
        String arguments = "";
        Queue <String> items = new LinkedList <String> ();
        //checks to see if user inputted correct amount of arguments
        if (args.length > 2) {
        	//iterated through user arguments and adds to the list of commands and arguments
            for (int i = 2; i < args.length; i++) {
            	arguments += args[i] + " "; 
            	items.add(args[i]);
            }
        }
        //initialize the ftp client
        client = new FTPClient();
        try {
        	client.connect(server);
        	//returns whether client was able to successfully login with user credentials
            successful = client.login(s[0], s[1]);
            //current working directory
            System.out.println(client.printWorkingDirectory());
            
            while (!items.isEmpty()) {
                String input = items.remove();
                String file;
                String dir;
                //executes the user command
                if (input.equals("ls")){
                	listFiles();
                } else if(input.equals("cd")){
                	dir = items.remove(); 
                    if (dir.startsWith("\"")) {
                    	dir += items.remove(); 
                    }
                    System.out.println(dir);
                    changeDir(dir);
                    System.out.println(client.printWorkingDirectory());
                } else if(input.equals("mkdir")){
                	dir = items.remove(); 
                    if (dir.startsWith("\"")) {
                            dir += items.remove(); 
                    }
                    System.out.println(dir);
                    makeDirectory(dir);
                } else if(input.equals("rm")){
                	file = items.remove();
                    if (file.startsWith("\"")) {
                    	file += items.remove(); 
                    }
                    System.out.println(file);
                    removeFile(file);
                } else if(input.equals("rmdir")){
                	dir = items.remove();
                    if (dir.startsWith("\"")) {
                           dir += items.remove(); 
                    }
                    System.out.println(dir);
                    removeDirectory(dir);
                } else if(input.equals("get")){
                	file = items.remove();
                    if (file.startsWith("\"")) {
                         file += items.remove(); 
                    }
                    System.out.println(file);
                    get(file);
                } else if(input.equals("put")){
                	file = items.remove();
                    if (file.startsWith("\"")) {
                          file += items.remove(); 
                    }
                    System.out.println(file);
                    put(file);
                } else{
                	System.out.println("Please input valid command");
                }
            }
                System.out.println();

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    //lists the files
    public static void listFiles() throws IOException {
    	 for (String name : client.listNames()) {
             System.out.println(name);
         }
    }
    
    //changes the directory
    public static void changeDir(String pathName) throws IOException {
        	client.changeWorkingDirectory(pathName);
    }
    
    public static void get(String fileName) throws IOException {
        if (isDirectory(fileName)){
            getDirectory(client.printWorkingDirectory(), fileName, System.getProperty("user.dir"));
        } else if (isFile(fileName)) {
            File downloadFile = new File(fileName);
            OutputStream outputStream = new FileOutputStream(downloadFile);
            try {
            	client.retrieveFile(fileName, outputStream);
            } catch(IOException io) {
                io.printStackTrace(); 
            }
            outputStream.flush();
            outputStream.close();
        } else {
            System.out.println("Could not download");
        }
    }

    public static boolean makeDirectory(String directoryName) throws IOException {
        return client.makeDirectory(directoryName);
    }

    public static void removeFile(String fileName) throws IOException {
    	client.deleteFile(fileName); 
    }

    public static void removeDirectory(String directoryName) throws IOException {
    	client.removeDirectory(directoryName); 
    }

    public static void getFile(String fileName, String location) throws IOException {
    	//creates new file
        File downloadFile = new File(location);
        //downloads the file
        File parentDir = downloadFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdir();
        }

        OutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(downloadFile));
        try {
        	client.setFileType(FTP.BINARY_FILE_TYPE);
        	client.retrieveFile(fileName, outputStream);
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }


    //checks whether the directory exists
    public static boolean isDirectory(String dirName) throws IOException {
        String dir = client.printWorkingDirectory();
        client.changeWorkingDirectory(dirName);
        int returnCode = client.getReplyCode();
        client.changeWorkingDirectory(dir);
        if (returnCode == 550) {
            return false;
        }
        return true;
    }

    //checks whether the name is a file in the directory
    public static boolean isFile(String fileName) throws IOException {
        InputStream inputStream = client.retrieveFileStream(fileName);
        int returnCode = client.getReplyCode();
        if (inputStream == null || returnCode == 550) {
            return false;
        }
        return true;
    }

    public static void getDirectory(String parentDirectory, String currentDirectory, String location) throws IOException {
        String dirToList = parentDirectory;
        if (!currentDirectory.equals("")) {
            dirToList += "/" + currentDirectory;
        }

        FTPFile[] subFiles = client.listFiles(dirToList);

        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();

                String filePath = parentDirectory + "/" + currentDirectory + "/"
                    + currentFileName;
                if (currentDirectory.equals("")) {
                    filePath = parentDirectory + "/" + currentFileName;
                }

                String newDirPath = location + parentDirectory + File.separator
                    + currentDirectory + File.separator + currentFileName;
                if (currentDirectory.equals("")) {
                    newDirPath = location + parentDirectory + File.separator
                        + currentFileName;
                }

                if (aFile.isDirectory()) {
                    File newDir = new File(newDirPath);
                    System.out.println("The directory was created");

                    getDirectory(dirToList, currentFileName,
                    		location);
                } else {
                    getFile(filePath, newDirPath);
                }
            }
        }
    }


    public static void putDirectory(String remoteDirPath, String localParentDir, String remoteParentDir) throws IOException{
        File localDir = new File(localParentDir);
        File[] files = localDir.listFiles();
        if (files != null && files.length > 0) {
            for (File item : files) {
                String remoteFilePath = remoteDirPath + "/" + remoteParentDir
                        + "/" + item.getName();
                if (remoteParentDir.equals("")) {
                    remoteFilePath = remoteDirPath + "/" + item.getName();
                }
     
                if (item.isFile()) {
                    // upload the file
                    String localFilePath = item.getAbsolutePath();
                    System.out.println("uploading: " + localFilePath);
                    putFile(localFilePath, remoteFilePath);
                } else {
                    // create directory on the server
                    boolean created = makeDirectory(remoteFilePath);
                    if (created) {
                        System.out.println("the directory has been created: "
                                + remoteFilePath);
                    } else {
                        System.out.println("directory was not created: "
                                + remoteFilePath);
                    }
     
                    // upload the sub directory
                    String parent = remoteParentDir + "/" + item.getName();
                    if (remoteParentDir.equals("")) {
                        parent = item.getName();
                    }
     
                    localParentDir = item.getAbsolutePath();
                    putDirectory(remoteDirPath, localParentDir,
                            parent);
                }
            }
        }
    }
    
    public static void put(String name) throws IOException {
        File file = new File(name);
         
        if (file.isDirectory()) {
            putDirectory(name, name, client.printWorkingDirectory());
        } else {
            putFile(name, name); 
        }
    }
    public static void putFile(String localFilePath, String remoteFilePath) throws IOException {
        File file = new File(localFilePath);
     
        InputStream inputStream = new FileInputStream(file);
        try {
        	client.setFileType(FTP.BINARY_FILE_TYPE);
        	client.storeFile(remoteFilePath, inputStream);
        } finally {
            inputStream.close();
        }
    }


}