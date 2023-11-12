package de.rpgframework.shadowrun6.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.update4j.Configuration;
import org.update4j.FileMetadata;
import org.update4j.LaunchContext;
import org.update4j.service.DefaultLauncher;
import org.update4j.service.Launcher;

import javafx.stage.Stage;

/**
 * @author prelle
 *
 */
public class CommlinkLauncher2 extends DefaultLauncher implements Launcher {

	private final static Logger logger = System.getLogger("commlink6.updater");
	public static Stage primaryStage;

	//-------------------------------------------------------------------
	public CommlinkLauncher2() {
	}

	//-------------------------------------------------------------------
	private static Thread createOutputThread(BufferedReader reader, PrintStream out) {
		Runnable sysOut = () -> {
			try {
				do {
					String line = reader.readLine();
					if (line==null)
						break;
					out.println(line);
				} while (true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		};

		Thread thread = new Thread(sysOut, "outputThread");
		return thread;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.update4j.service.Launcher#run(org.update4j.LaunchContext)
	 */
    @Override
    public void run(LaunchContext context) {
        Configuration config = context.getConfiguration();

        String mainClass = config.getResolvedProperty(MAIN_CLASS_PROPERTY_KEY);
        mainClass = "de.rpgframework.shadowrun6.comlink.ComLinkStarter";

        List<String> pathes = new ArrayList<>();
        for (FileMetadata meta : config.getFiles()) {
        	String path =meta.getPath().toString();
        	pathes.add(path);
        	logger.log(Level.DEBUG, "Classpath: {0}",path);
        }

		Optional<String> info = ProcessHandle.current().info().command();
		Path cwd = Paths.get(info.get()).getParent();
		if (cwd.getFileName().toString().equals("bin"))
			cwd = cwd.getParent();
		logger.log(Level.INFO, "Current working directory: {0}",cwd);
    	Path jvmPath = cwd.resolve("lib").resolve("runtime").resolve("bin").resolve("java");
    	logger.log(Level.INFO, "JVM to use: {0}",jvmPath);
     	logger.log(Level.INFO, " exists  : {0}",Files.exists(jvmPath));

        List<String> commandList = new ArrayList<>();
        commandList.add(jvmPath.toString());
        commandList.add("-Dproject.version="+config.getProperties("project.version").get(0).getValue());
        commandList.add("-Dprofile="+System.getProperty("profile"));
        commandList.add("--class-path");
        commandList.add( String.join(":", pathes));
        commandList.add(mainClass);
        logger.log(Level.INFO, "Execute {0}",commandList);
        String[] cmdArray = new String[commandList.size()];
        cmdArray = commandList.toArray(cmdArray);
        try {
        	Process proc = Runtime.getRuntime().exec(cmdArray, null);
			System.out.println("Proc = "+proc);
			Thread threadI = createOutputThread(proc.inputReader(), System.out);
			threadI.start();
			Thread threadE = createOutputThread(proc.errorReader(), System.err);
			threadE.start();
		} catch (IOException e) {
			logger.log(Level.ERROR, "Error starting process",e);
			e.printStackTrace();
		}
        logger.log(Level.INFO, "Leaving launcher");
    }

}
