REM - This bat file should be run from the directory that contains the jar file
REM - The directory that contains the jar file should have a directory called lib which contains all of the jars used by this app
REM - The command below assumes that the configuration files (application.properties & logback.xml) are in the jar

java -cp "bourbon-checker-1.0.jar;lib/*" com.wolfesoftware.bourbonchecker.BourbonCheckerApplication