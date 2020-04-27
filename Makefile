JAVA_SOURCES=\
  ./com/sandklef/compliance/utils/MostPermissiveLicenseComparator.java \
  ./com/sandklef/compliance/domain/Component.java \
  ./com/sandklef/compliance/domain/LicenseObligation.java \
  ./com/sandklef/compliance/domain/LicenseViolationException.java \
  ./com/sandklef/compliance/domain/License.java \
  ./com/sandklef/compliance/domain/Obligation.java \
  ./com/sandklef/compliance/domain/ObligationState.java \
  ./com/sandklef/compliance/domain/LicenseType.java \
  ./com/sandklef/compliance/utils/Log.java \
  ./com/sandklef/compliance/utils/ObligationBuilder.java \
  ./com/sandklef/compliance/utils/LicenseStore.java \
  ./com/sandklef/compliance/utils/LicenseArbiter.java \
  ./com/sandklef/compliance/json/JsonParser.java \

TEST_SOURCES=\
  com/sandklef/compliance/test/Test.java \
  com/sandklef/compliance/test/TestSubComponents.java \
  com/sandklef/compliance/test/TestDualLicenses.java \
  com/sandklef/compliance/test/TestComponents.java \
  com/sandklef/compliance/test/TestPrintLicenses.java \
  com/sandklef/compliance/test/TestCanAUseB.java \
  com/sandklef/compliance/test/TestLicense.java \


CLASSES=$(JAVA_SOURCES:.java=.class)
TEST_CLASSES=$(TEST_SOURCES:.java=.class)

CLASSPATH="lib/org.json.jar:."

%.class:%.java
	javac -cp "$(CLASSPATH)" $<

all: $(CLASSES) $(JSON_JAR) 


JSON_JAR=lib/org.json.jar

$(JSON_JAR):
	mkdir -p lib
	wget 'https://search.maven.org/remotecontent?filepath=org/json/json/20171018/json-20171018.jar' -O lib/org.json.jar

$(CLASSES): $(JSON_JAR)

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

test: test-json test-all

test-all: $(TEST_CLASSES)
	for i in $(TEST_CLASSES); \
	do \
		export CLASS=`echo $$i | sed -e 's,\.class,,g' -e 's,/,\.,g'` ; \
		echo "Test: $$CLASS"; \
		java -ea -cp $(CLASSPATH) $$CLASS ; \
		if [ $$? -ne 0 ] ; then echo "$$CLASS failed"; break; fi ; \
	done;

test-json: ./com/sandklef/compliance/json/test/TestJsonParser.class  $(CLASSES) $(JSON_JAR)
	java -cp $(CLASSPATH) com.sandklef.compliance.json.test.TestJsonParser --verbose ./com/sandklef/compliance/json/test/simple.json
	java -cp $(CLASSPATH) com.sandklef.compliance.json.test.TestJsonParser --violation --verbose ./com/sandklef/compliance/json/test/simple-problem.json
