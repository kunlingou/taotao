package utils;

import static org.mybatis.generator.internal.util.ClassloaderUtility.getCustomClassloader;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.ShellRunner;
import org.mybatis.generator.api.VerboseProgressCallback;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.logging.LogFactory;

public class MybatisGeneratorShellRunner extends ShellRunner{

	public static void main(String[] args) {
		
		args = new String[] { "-configfile", 
				"classpath:mybatis-generator/generatorConfig.xml",
				"-overwrite" };
//		parseCommandLine0(args);
//		loadClass();
//		run(args);
	}
	
	private static void parseconfig(Configuration config) throws ClassNotFoundException {
		List<String> entries = config.getClassPathEntries();
		for(int i=0;i<entries.size();i++) {
			if("com.mysql.jdbc.Driver".equals(entries.get(i))) {
//				entries.set(i, element)
				ObjectFactory.addExternalClassLoader(Class.forName("com.mysql.jdbc.Driver").getClassLoader());
			}
			
		}
	}

	private static void loadClass() {
		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
//		ObjectFactory.addExternalClassLoader(ClassloaderUtility.getCustomClassloader(Arrays.asList("")));
	}

	private static void parseCommandLine0(String[] args) {
		for(int i=0;i<args.length;i++) {
			if(args[i].startsWith("classpath:")) {
				args[i] = ClassLoader.getSystemResource(args[i].replace("classpath:", "")).getPath();
			}
		}
	}
	
	public static void run(String[] args) throws ClassNotFoundException {
        if (args.length == 0) {
            usage();
            System.exit(0);
            return; // only to satisfy compiler, never returns
        }

        Map<String, String> arguments = parseCommandLine(args);

        if (arguments.containsKey(HELP_1)) {
            usage();
            System.exit(0);
            return; // only to satisfy compiler, never returns
        }

        if (!arguments.containsKey(CONFIG_FILE)) {
            writeLine(getString("RuntimeError.0")); //$NON-NLS-1$
            return;
        }

        final List<String> warnings = new ArrayList<String>();

        String configfile = arguments.get(CONFIG_FILE);
        File configurationFile = new File(configfile);
        if (!configurationFile.exists()) {
            writeLine(getString("RuntimeError.1", configfile)); //$NON-NLS-1$
            return;
        }

        Set<String> fullyqualifiedTables = new HashSet<String>();
        if (arguments.containsKey(TABLES)) {
            StringTokenizer st = new StringTokenizer(arguments.get(TABLES), ","); //$NON-NLS-1$
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                if (s.length() > 0) {
                    fullyqualifiedTables.add(s);
                }
            }
        }

        Set<String> contexts = new HashSet<String>();
        if (arguments.containsKey(CONTEXT_IDS)) {
            StringTokenizer st = new StringTokenizer(
                    arguments.get(CONTEXT_IDS), ","); //$NON-NLS-1$
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                if (s.length() > 0) {
                    contexts.add(s);
                }
            }
        }

        try {
            ConfigurationParser cp = new ConfigurationParser(warnings);
            final Configuration config = cp.parseConfiguration(configurationFile);
            
            parseconfig(config);

            DefaultShellCallback shellCallback = new DefaultShellCallback(
                    arguments.containsKey(OVERWRITE));

            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback, warnings) {
            	
            	@Override
            	public void generate(ProgressCallback callback, Set<String> contextIds,
                        Set<String> fullyQualifiedTableNames, boolean writeFiles) throws SQLException,
                        IOException, InterruptedException {

                    if (callback == null) {
                        callback = new NullProgressCallback();
                    }

                    getGeneratedJavaFiles().clear();
                    getGeneratedXmlFiles().clear();
                    ObjectFactory.reset();
                    RootClassInfo.reset();

                    // calculate the contexts to run
                    List<Context> contextsToRun;
                    if (contextIds == null || contextIds.size() == 0) {
                        contextsToRun = config.getContexts();
                    } else {
                        contextsToRun = new ArrayList<Context>();
                        for (Context context : config.getContexts()) {
                            if (contextIds.contains(context.getId())) {
                                contextsToRun.add(context);
                            }
                        }
                    }

                    // setup custom classloader if required
                    if (config.getClassPathEntries().size() > 0) {
                        ClassLoader classLoader = getCustomClassloader(config.getClassPathEntries());
                        ObjectFactory.addExternalClassLoader(classLoader);
                    }

                    // now run the introspections...
                    int totalSteps = 0;
                    for (Context context : contextsToRun) {
                        totalSteps += context.getIntrospectionSteps();
                    }
                    callback.introspectionStarted(totalSteps);

                    for (Context context : contextsToRun) {
                        context.introspectTables(callback, warnings,
                                fullyQualifiedTableNames);
                    }

                    // now run the generates
                    totalSteps = 0;
                    for (Context context : contextsToRun) {
                        totalSteps += context.getGenerationSteps();
                    }
                    callback.generationStarted(totalSteps);

                    for (Context context : contextsToRun) {
                        context.generateFiles(callback, getGeneratedJavaFiles(),
                                getGeneratedXmlFiles(), warnings);
                    }

                    // now save the files
                    if (writeFiles) {
                        callback.saveStarted(getGeneratedXmlFiles().size()
                                + getGeneratedJavaFiles().size());

                        for (GeneratedXmlFile gxf : getGeneratedXmlFiles()) {
                            projects.add(gxf.getTargetProject());
                            writeGeneratedXmlFile(gxf, callback);
                        }

                        for (GeneratedJavaFile gjf : generatedJavaFiles) {
                            projects.add(gjf.getTargetProject());
                            writeGeneratedJavaFile(gjf, callback);
                        }

                        for (String project : projects) {
                            shellCallback.refreshProject(project);
                        }
                    }

                    callback.done();
                }
            };

            ProgressCallback progressCallback = arguments.containsKey(VERBOSE) ? new VerboseProgressCallback()
                    : null;

            myBatisGenerator.generate(progressCallback, contexts, fullyqualifiedTables);

        } catch (XMLParserException e) {
            writeLine(getString("Progress.3")); //$NON-NLS-1$
            writeLine();
            for (String error : e.getErrors()) {
                writeLine(error);
            }

            return;
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InvalidConfigurationException e) {
            writeLine(getString("Progress.16")); //$NON-NLS-1$
            for (String error : e.getErrors()) {
                writeLine(error);
            }
            return;
        } catch (InterruptedException e) {
            // ignore (will never happen with the DefaultShellCallback)
        }

        for (String warning : warnings) {
            writeLine(warning);
        }

        if (warnings.size() == 0) {
            writeLine(getString("Progress.4")); //$NON-NLS-1$
        } else {
            writeLine();
            writeLine(getString("Progress.5")); //$NON-NLS-1$
        }
    }
	
	private static void usage() {
	        String lines = getString("Usage.Lines"); //$NON-NLS-1$
	        int intLines = Integer.parseInt(lines);
	        for (int i = 0; i < intLines; i++) {
	            String key = "Usage." + i; //$NON-NLS-1$
	            writeLine(getString(key));
	        }
	    }

	    private static void writeLine(String message) {
	        System.out.println(message);
	    }

	    private static void writeLine() {
	        System.out.println();
	    }

	    private static Map<String, String> parseCommandLine(String[] args) {
	        List<String> errors = new ArrayList<String>();
	        Map<String, String> arguments = new HashMap<String, String>();

	        for (int i = 0; i < args.length; i++) {
	            if (CONFIG_FILE.equalsIgnoreCase(args[i])) {
	                if ((i + 1) < args.length) {
	                    arguments.put(CONFIG_FILE, args[i + 1]);
	                } else {
	                    errors.add(getString(
	                            "RuntimeError.19", CONFIG_FILE)); //$NON-NLS-1$
	                }
	                i++;
	            } else if (OVERWRITE.equalsIgnoreCase(args[i])) {
	                arguments.put(OVERWRITE, "Y"); //$NON-NLS-1$
	            } else if (VERBOSE.equalsIgnoreCase(args[i])) {
	                arguments.put(VERBOSE, "Y"); //$NON-NLS-1$
	            } else if (HELP_1.equalsIgnoreCase(args[i])) {
	                arguments.put(HELP_1, "Y"); //$NON-NLS-1$
	            } else if (HELP_2.equalsIgnoreCase(args[i])) {
	                // put HELP_1 in the map here too - so we only
	                // have to check for one entry in the mainline
	                arguments.put(HELP_1, "Y"); //$NON-NLS-1$
	            } else if (FORCE_JAVA_LOGGING.equalsIgnoreCase(args[i])) {
	                LogFactory.forceJavaLogging();
	            } else if (CONTEXT_IDS.equalsIgnoreCase(args[i])) {
	                if ((i + 1) < args.length) {
	                    arguments.put(CONTEXT_IDS, args[i + 1]);
	                } else {
	                    errors.add(getString(
	                            "RuntimeError.19", CONTEXT_IDS)); //$NON-NLS-1$
	                }
	                i++;
	            } else if (TABLES.equalsIgnoreCase(args[i])) {
	                if ((i + 1) < args.length) {
	                    arguments.put(TABLES, args[i + 1]);
	                } else {
	                    errors.add(getString("RuntimeError.19", TABLES)); //$NON-NLS-1$
	                }
	                i++;
	            } else {
	                errors.add(getString("RuntimeError.20", args[i])); //$NON-NLS-1$
	            }
	        }

	        if (!errors.isEmpty()) {
	            for (String error : errors) {
	                writeLine(error);
	            }

	            System.exit(-1);
	        }

	        return arguments;
	    }
	    private static final String CONFIG_FILE = "-configfile"; //$NON-NLS-1$
	    private static final String OVERWRITE = "-overwrite"; //$NON-NLS-1$
	    private static final String CONTEXT_IDS = "-contextids"; //$NON-NLS-1$
	    private static final String TABLES = "-tables"; //$NON-NLS-1$
	    private static final String VERBOSE = "-verbose"; //$NON-NLS-1$
	    private static final String FORCE_JAVA_LOGGING = "-forceJavaLogging"; //$NON-NLS-1$
	    private static final String HELP_1 = "-?"; //$NON-NLS-1$
	    private static final String HELP_2 = "-h"; //$NON-NLS-1$
}
