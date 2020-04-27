JAVA_SOURCES=\
  ./com/sandklef/compliance/LicenseStore.java \
  ./com/sandklef/compliance/License.java \
  ./com/sandklef/compliance/ObligationState.java \
  ./com/sandklef/compliance/LicenseViolationException.java \
  ./com/sandklef/compliance/Obligation.java \
  ./com/sandklef/compliance/Component.java \
  ./com/sandklef/compliance/LicenseType.java \
  ./com/sandklef/compliance/LicenseObligation.java \
  ./com/sandklef/compliance/LicenseArbiter.java \
  ./com/sandklef/compliance/ObligationBuilder.java \
  ./com/sandklef/compliance/json/JsonParser.java \

CLASSES=$(JAVA_SOURCES:.java=.class)

CLASSPATH="lib/org.json.jar:."

%.class:%.java
	javac -cp "$(CLASSPATH)" $<

all:$(CLASSES)


clean:
	rm -f $(CLASSES)
	find -name "*~" | xargs rm -f
	find -name "*.class" | xargs rm -f

test-json: ./com/sandklef/compliance/json/test/TestJsonParser.class  $(CLASSES)
	java -cp $(CLASSPATH) com.sandklef.compliance.json.test.TestJsonParser ./com/sandklef/compliance/json/test/simple.json
