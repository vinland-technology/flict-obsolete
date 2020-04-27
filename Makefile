JAVA_SOURCES=\
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

CLASSES=$(JAVA_SOURCES:.java=.class)

CLASSPATH="lib/org.json.jar:."

%.class:%.java
	javac -cp "$(CLASSPATH)" $<

all:$(CLASSES) $(JSON_JAR) 

JSON_JAR=lib/org.json.jar

$(JSON_JAR):
	mkdir -p lib
	wget 'https://search.maven.org/remotecontent?filepath=org/json/json/20171018/json-20171018.jar' -O lib/org.json.jar

$(CLASSES): $(JSON_JAR)



clean:
	rm -f $(CLASSES)
	find -name "*~" | xargs rm -f
	find -name "*.class" | xargs rm -f

test: test-json

test-json: ./com/sandklef/compliance/json/test/TestJsonParser.class  $(CLASSES) $(JSON_JAR)
	java -cp $(CLASSPATH) com.sandklef.compliance.json.test.TestJsonParser ./com/sandklef/compliance/json/test/simple.json
	java -cp $(CLASSPATH) com.sandklef.compliance.json.test.TestJsonParser --violation ./com/sandklef/compliance/json/test/simple-problem.json
