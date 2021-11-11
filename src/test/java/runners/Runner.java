package runners;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import hooks.AFStartup;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import testbase.BaseClass;

@RunWith(Cucumber.class)
@CucumberOptions(
    features =
    	{"src/test/resources/features"}, // Emplacement des fichiers .feature 

    plugin =
		// {"progress", "json:target/cucumber.json"}, // --> verbose---
	    // {"pretty"  , "json:target/cucumber.json"}, // --> verbose+++
	    {"pretty", "json:target/cucumber.json" , // --> default formatter (recommanded)
	    "html:target/cucumber-default-report"},

    glue =
    {
        "hooks",  // Glues Framework (et hooks)
        "utils",  // Glues Framework Utils
        "stepsDefinition",    // Glues Projet
        "utilsProject",     // Glues Projet Utils
        "testbase" // Hooks Projet
    },

    dryRun = false,
    
	// si true alors les glues inexistantes mettront le build en erreur
    strict = true,

    tags = // Test à lancer
    
            "@login"
    ,

    monochrome = true // coloration
)

public class Runner {

    /**
     * Fichier de configuration
     */
    private static final String CONF_FILE = "./src/test/resources/configs/local_system_properties.properties";
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        AFStartup.startFramework(CONF_FILE);
        BaseClass.setUp();
    }

    @AfterClass
    public static void afterTest() throws Exception {
        AFStartup.stopFramework();
//        ProjectCore.destroy();
    }


}
