JAVA_SOURCES=\
  ./com/sandklef/compliance/domain/Violation.java \
  ./com/sandklef/compliance/domain/Component.java \
  ./com/sandklef/compliance/domain/NoLicenseException.java \
  ./com/sandklef/compliance/domain/Conclusion.java \
  ./com/sandklef/compliance/domain/LicenseObligation.java \
  ./com/sandklef/compliance/domain/Report.java \
  ./com/sandklef/compliance/domain/LicenseViolationException.java \
  ./com/sandklef/compliance/domain/License.java \
  ./com/sandklef/compliance/domain/Obligation.java \
  ./com/sandklef/compliance/domain/ObligationState.java \
  ./com/sandklef/compliance/domain/LicenseType.java \
  ./com/sandklef/compliance/utils/LeastPermissiveLicenseComparator.java \
  ./com/sandklef/compliance/utils/MostPermissiveLicenseComparator.java \
  ./com/sandklef/compliance/utils/Log.java \
  ./com/sandklef/compliance/utils/ObligationBuilder.java \
  ./com/sandklef/compliance/utils/LicenseStore.java \
  ./com/sandklef/compliance/utils/LicenseArbiter.java \
  ./com/sandklef/compliance/json/JsonLicenseParser.java \
  ./com/sandklef/compliance/json/JsonUtils.java \
  ./com/sandklef/compliance/json/JsonComponentParser.java \
  ./com/sandklef/compliance/cli/LicenseChecker.java \


TEST_SOURCES=\
  ./com/sandklef/compliance/json/test/TestLicenseParser.java \
  ./com/sandklef/compliance/json/test/TestJsonComponentParser.java \
  ./com/sandklef/compliance/test/TestSubComponents.java \
  ./com/sandklef/compliance/test/TestDualLicenses.java \
  ./com/sandklef/compliance/test/TestCanAUseB.java \
  ./com/sandklef/compliance/test/TestComponents.java \
  ./com/sandklef/compliance/test/TestPrintLicenses.java \
  ./com/sandklef/compliance/test/TestLicense.java \



CLASSES=$(JAVA_SOURCES:.java=.class)
TEST_CLASSES=$(TEST_SOURCES:.java=.class)

JSON_JAR=lib/org.json.jar
CLI_JAR=lib/commons-cli-1.4.jar
CLASSPATH=".:$(JSON_JAR):$(CLI_JAR)"

%.class:%.java
	javac  -Xdiags:verbose -cp "$(CLASSPATH)" $<

all: $(CLASSES) $(JSON_JAR) 



$(CLI_JAR):
	mkdir tmp; cd tmp ; wget "https://downloads.apache.org//commons/cli/binaries/commons-cli-1.4-bin.tar.gz"
	cd tmp; tar zxvf commons-cli-1.4-bin.tar.gz commons-cli-1.4/commons-cli-1.4.jar
	cd tmp; mv commons-cli-1.4/commons-cli-1.4.jar ../lib

$(JSON_JAR):
	mkdir -p lib
	wget 'https://search.maven.org/remotecontent?filepath=org/json/json/20171018/json-20171018.jar' -O lib/org.json.jar

$(CLASSES): $(JSON_JAR) $(CLI_JAR)

cli:
	make cli-licenses
	echo "Press enter to continue"; read EINAR
	make cli-violations

cli-licenses: com/sandklef/compliance/cli/LicenseChecker.class
	java -cp $(CLASSPATH) com/sandklef/compliance/cli/LicenseChecker  -p -dc --license-dir licenses/json 
cli-violations:
	java -cp $(CLASSPATH) com/sandklef/compliance/cli/LicenseChecker  -dc -v --license-dir licenses/json  --component ./com/sandklef/compliance/json/test/simple-problem.json

stat:
	@echo "Java: " ; 
	@echo -n " * files: " ; find . -name "*.java" | wc -l
	@echo -n " * loc: " ; find . -name "*.java" | xargs wc -l| tail -1
	@echo "Json: " ; 
	@echo -n " * files: " ; find . -name "*.json" | wc -l
	@echo -n " * loc: " ; find . -name "*.json" | xargs wc -l| tail -1

clean:
	rm -f $(CLASSES)
	find -name "*~" | xargs rm -f
	find -name "*.class" | xargs rm -f

test: test-all test-json 

test-all: $(TEST_CLASSES)
	for i in $(TEST_CLASSES); \
	do \
		export CLASS=`echo $$i | sed -e 's,\.class,,g' -e 's,/,\.,g'` ; \
		echo "Test: $$CLASS"; \
		java -ea -cp $(CLASSPATH) $$CLASS ; \
		if [ $$? -ne 0 ] ; then echo "$$CLASS failed"; break; fi ; \
	done;

test-json: ./com/sandklef/compliance/json/test/TestJsonParser.class  com/sandklef/compliance/json/test/TestLicenseParser.class $(CLASSES) $(JSON_JAR)
	@echo " --- License parsers ----"
	java -cp $(CLASSPATH) com.sandklef.compliance.json.test.TestLicenseParser --verbose ./licenses/json/
	@echo " --- Json parsers ----"
	java -cp $(CLASSPATH) com.sandklef.compliance.json.test.TestJsonParser --verbose ./com/sandklef/compliance/json/test/simple.json
	@echo " --- Json parsers with violation expected ----"
	java -cp $(CLASSPATH) com.sandklef.compliance.json.test.TestJsonParser --violation --verbose ./com/sandklef/compliance/json/test/simple-problem.json

