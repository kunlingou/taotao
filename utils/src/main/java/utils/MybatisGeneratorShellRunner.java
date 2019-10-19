package utils;

import static org.mybatis.generator.internal.util.messages.Messages.getString;
import static utils.Constant.APPLICATION_YML;

import java.io.File;
import java.io.IOException;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.VerboseProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.logging.LogFactory;
/**
 * 只需要在配置文件中修改数据库连接信息，设置需要导出的表，即可使用。<br>
 * 可以识别的驱动名，包括但不限于：<br>
 * com.mysql.jdbc.Driver<br>
 * oracle.jdbc.driver.OracleDriver<br>
 * oracle.jdbc.OracleDriver<br>
 * @author zhangluru
 * @date 2019/10/19
 */
public class MybatisGeneratorShellRunner{
	
	public static void main(String[] args) {

		args = new String[] { "-configfile", "classpath:mybatis-generator/generatorConfig.xml", "-overwrite" };
		
		run(args);
	}
	

	private static void parseCommandLine0(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("classpath:")) {
				args[i] = ClassLoader.getSystemResource(args[i].replace("classpath:", "")).getPath();
			}
		}
	}
	
	private static void parseConfiguration0(Configuration config) {
		List<String> classPathEntries = config.getClassPathEntries();
		for(int i=0,len=classPathEntries.size();i<len;i++) {
			String classPathEntry = classPathEntries.get(i);
			if(APPLICATION_YML.containsKey(classPathEntry)) {
				Driver driver = loadDriver(APPLICATION_YML.getProperty(classPathEntry));
				String path = driver.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
				classPathEntries.set(i, path);
            }
		}
		
		List<Context> contexts = config.getContexts();
		for(int i=0,len=contexts.size();i<len;i++) {
			Context context = contexts.get(i);
			String property = System.getProperty("user.dir");
			JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = context.getJavaModelGeneratorConfiguration();
			javaModelGeneratorConfiguration.setTargetProject(property + javaModelGeneratorConfiguration.getTargetProject());
			SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = context.getSqlMapGeneratorConfiguration();
			sqlMapGeneratorConfiguration.setTargetProject(property + sqlMapGeneratorConfiguration.getTargetProject());
			JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = context.getJavaClientGeneratorConfiguration();
			javaClientGeneratorConfiguration.setTargetProject(property + javaClientGeneratorConfiguration.getTargetProject());
			JDBCConnectionConfiguration jdbcConnectionConfiguration = context.getJdbcConnectionConfiguration();
			jdbcConnectionConfiguration.setDriverClass(APPLICATION_YML.getProperty(jdbcConnectionConfiguration.getDriverClass()));
			jdbcConnectionConfiguration.setConnectionURL(APPLICATION_YML.getProperty(jdbcConnectionConfiguration.getConnectionURL()));
			jdbcConnectionConfiguration.setUserId(APPLICATION_YML.getProperty(jdbcConnectionConfiguration.getUserId()));
			jdbcConnectionConfiguration.setPassword(APPLICATION_YML.getProperty(jdbcConnectionConfiguration.getPassword()));
		}
	}
	
	private static Driver loadDriver(String classPathEntry) {
		Driver driver = null;
		try {
			Class<?> forName = Class.forName(classPathEntry);
			driver = (Driver) forName.newInstance();
		} catch (Exception e) {
			System.out.println("not found driver:"+classPathEntry);
		}
		return driver;
	}
	
	private static Map<String, String> argsVaild(String[] args) {
	    if (args.length == 0) {
            usage();System.exit(0);return null;
        }
        parseCommandLine0(args);
        Map<String, String> arguments = parseCommandLine(args);
        if (arguments.containsKey(HELP_1)) {
            usage();
            System.exit(0);
            return null;
        }
        if (!arguments.containsKey(CONFIG_FILE)) {
            writeLine(getString("RuntimeError.0"));
            return null;
        }
        return arguments;
	}
	
	/*********************************************************************
	 * copy from {@link org.mybatis.generator.api.ShellRunner}           *
	 *********************************************************************/
	private static final String CONFIG_FILE = "-configfile";
    private static final String OVERWRITE = "-overwrite";
    private static final String CONTEXT_IDS = "-contextids";
    private static final String TABLES = "-tables";
    private static final String VERBOSE = "-verbose";
    private static final String FORCE_JAVA_LOGGING = "-forceJavaLogging";
    private static final String HELP_1 = "-?";
    private static final String HELP_2 = "-h";
    
    public static void run(String[] args) {
        Map<String, String> arguments = argsVaild(args);
        List<String> warnings = new ArrayList<String>();
        String configfile = arguments.get(CONFIG_FILE);
        File configurationFile = new File(configfile);
        if (!configurationFile.exists()) {
            writeLine(getString("RuntimeError.1", configfile));
            return;
        }
        Set<String> fullyqualifiedTables = new HashSet<String>();
        if (arguments.containsKey(TABLES)) {
            StringTokenizer st = new StringTokenizer(arguments.get(TABLES), ",");
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
                    arguments.get(CONTEXT_IDS), ",");
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                if (s.length() > 0) {
                    contexts.add(s);
                }
            }
        }
        try {
            ConfigurationParser cp = new ConfigurationParser(warnings);
            Configuration config = cp.parseConfiguration(configurationFile);
            parseConfiguration0(config);
            DefaultShellCallback shellCallback = new DefaultShellCallback(
                    arguments.containsKey(OVERWRITE));
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback, warnings);
            ProgressCallback progressCallback = arguments.containsKey(VERBOSE) ? new VerboseProgressCallback()
                    : null;
            myBatisGenerator.generate(progressCallback, contexts, fullyqualifiedTables);

        } catch (XMLParserException e) {
            writeLine(getString("Progress.3"));
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
            writeLine(getString("Progress.16"));
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
            writeLine(getString("Progress.4"));
        } else {
            writeLine();
            writeLine(getString("Progress.5"));
        }
    }
    
    private static void usage() {
        String lines = getString("Usage.Lines");
        int intLines = Integer.parseInt(lines);
        for (int i = 0; i < intLines; i++) {
            String key = "Usage." + i;
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
        Map<String, String> arguments = new HashMap<String, String>(16);

        for (int i = 0; i < args.length; i++) {
            if (CONFIG_FILE.equalsIgnoreCase(args[i])) {
                if ((i + 1) < args.length) {
                    arguments.put(CONFIG_FILE, args[i + 1]);
                } else {
                    errors.add(getString(
                            "RuntimeError.19", CONFIG_FILE));
                }
                i++;
            } else if (OVERWRITE.equalsIgnoreCase(args[i])) {
                arguments.put(OVERWRITE, "Y");
            } else if (VERBOSE.equalsIgnoreCase(args[i])) {
                arguments.put(VERBOSE, "Y");
            } else if (HELP_1.equalsIgnoreCase(args[i])) {
                arguments.put(HELP_1, "Y");
            } else if (HELP_2.equalsIgnoreCase(args[i])) {
                // put HELP_1 in the map here too - so we only
                // have to check for one entry in the mainline
                arguments.put(HELP_1, "Y");
            } else if (FORCE_JAVA_LOGGING.equalsIgnoreCase(args[i])) {
                LogFactory.forceJavaLogging();
            } else if (CONTEXT_IDS.equalsIgnoreCase(args[i])) {
                if ((i + 1) < args.length) {
                    arguments.put(CONTEXT_IDS, args[i + 1]);
                } else {
                    errors.add(getString(
                            "RuntimeError.19", CONTEXT_IDS));
                }
                i++;
            } else if (TABLES.equalsIgnoreCase(args[i])) {
                if ((i + 1) < args.length) {
                    arguments.put(TABLES, args[i + 1]);
                } else {
                    errors.add(getString("RuntimeError.19", TABLES));
                }
                i++;
            } else {
                errors.add(getString("RuntimeError.20", args[i]));
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
}
