# SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
#
# SPDX-License-Identifier: GPL-3.0-or-later

JAVA_SOURCES=\
  com/sandklef/compliance/domain/LicensePolicy.java \
  com/sandklef/compliance/domain/ListType.java \
  com/sandklef/compliance/domain/Component.java \
  com/sandklef/compliance/domain/NoLicenseException.java \
  com/sandklef/compliance/domain/LicenseConclusion.java \
  com/sandklef/compliance/domain/LicenseObligation.java \
  com/sandklef/compliance/domain/Report.java \
  com/sandklef/compliance/domain/LicenseViolationException.java \
  com/sandklef/compliance/domain/License.java \
  com/sandklef/compliance/domain/Obligation.java \
  com/sandklef/compliance/domain/Concern.java \
  com/sandklef/compliance/domain/LicenseViolation.java \
  com/sandklef/compliance/domain/ObligationState.java \
  com/sandklef/compliance/domain/LicenseType.java \
  com/sandklef/compliance/domain/PolicyConcern.java \
  com/sandklef/compliance/domain/PolicyViolation.java \
  com/sandklef/compliance/utils/LeastPermissiveLicenseComparator.java \
  com/sandklef/compliance/utils/MostPermissiveLicenseComparator.java \
  com/sandklef/compliance/utils/Log.java \
  com/sandklef/compliance/utils/ObligationBuilder.java \
  com/sandklef/compliance/utils/LicenseStore.java \
  com/sandklef/compliance/utils/LicenseArbiter.java \
  com/sandklef/compliance/json/JsonLicenseParser.java \
  com/sandklef/compliance/json/JsonPolicyParser.java \
  com/sandklef/compliance/exporter/TextExporter.java \
  com/sandklef/compliance/exporter/TestExporterFactory.java \
  com/sandklef/compliance/json/JsonUtils.java \
  com/sandklef/compliance/json/JsonComponentParser.java \
  com/sandklef/compliance/cli/LicenseChecker.java \
  com/sandklef/compliance/exporter/ReportExporter.java \
  com/sandklef/compliance/exporter/ReportExporterFactory.java \
  com/sandklef/compliance/exporter/TestJsonComponentParser.java \
  com/sandklef/compliance/exporter/JsonExporter.java \
  com/sandklef/compliance/exporter/MDExporter.java \
  com/sandklef/compliance/utils/TextComponentExporter.java \
 com/sandklef/compliance/utils/VirtualLicenseBuilder.java \


TEST_SOURCES=\
  com/sandklef/compliance/test/TestAll.java\
  com/sandklef/compliance/test/Utils.java\
  com/sandklef/compliance/test/TestComponents.java \
  com/sandklef/compliance/test/TestCanAUseB.java \
  com/sandklef/compliance/test/TestPolicy.java \
  com/sandklef/compliance/test/TestDualLicenses.java \
  com/sandklef/compliance/test/TestMostPermissiveLicenseComparator.java \
  com/sandklef/compliance/json/test/TestLicenseParser.java \
  com/sandklef/compliance/test/TestLicenseConnector.java \
  com/sandklef/compliance/test/VirtualLicenseBuilderTest.java \


CLASSES=$(JAVA_SOURCES:.java=.class)
TEST_CLASSES=$(TEST_SOURCES:.java=.class)

LIB_DIR=lib
JSON_JAR=$(LIB_DIR)/org.json.jar
CLI_JAR=$(LIB_DIR)/commons-cli-1.4.jar
WINSTONE_JAR=$(LIB_DIR)/winstone.jar
#JUNIT_JAR=$(LIB_DIR)/junit-jupiter-api-5.6.2.jar
#JUNIT_V_JAR=$(LIB_DIR)/junit-vintage-engine-5.6.2.jar
CLASSPATH=.:$(JSON_JAR):$(CLI_JAR):
TEST_CLASSPATH=$(CLASSPATH):$(JUNIT_JAR)

%.class:%.java 
	javac  -Xdiags:verbose -cp "$(CLASSPATH)" $<

all: $(CLASSES) $(JSON_JAR) Makefile
	@echo

$(CLASSES): $(SOURCES) Makefile

$(CLI_JAR):
	mkdir tmp; cd tmp ; wget "https://downloads.apache.org//commons/cli/binaries/commons-cli-1.4-bin.tar.gz"
	cd tmp; tar zxvf commons-cli-1.4-bin.tar.gz commons-cli-1.4/commons-cli-1.4.jar
	cd tmp; mv commons-cli-1.4/commons-cli-1.4.jar ../lib

#$(JUNIT_JAR):
	mkdir -p lib
	wget "https://search.maven.org/remotecontent?filepath=org/junit/jupiter/junit-jupiter-api/5.6.2/junit-jupiter-api-5.6.2.jar" -O $(JUNIT_JAR)

#$(JUNIT_V_JAR):
	mkdir -p lib
	wget "https://search.maven.org/remotecontent?filepath=org/junit/vintage/junit-vintage-engine/5.6.2/junit-vintage-engine-5.6.2.jar" -O $(JUNIT_V_JAR)

$(JSON_JAR):
	mkdir -p lib
	wget 'https://search.maven.org/remotecontent?filepath=org/json/json/20171018/json-20171018.jar' -O $(LIB_DIR)/org.json.jar

$(WINSTONE_JAR):
	mkdir -p lib
	wget 'https://sourceforge.net/projects/winstone/files/latest/download?source=typ_redirect' -O $(LIB_DIR)/winstone.jar

dload-libs: $(JSON_JAR) $(CLI_JAR) $(WINSTONE_JAR)
#$(JUNIT_JAR) $(JUNIT_V_JAR)

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
	@echo "Bash: " ; 
	@echo -n " * files: " ; find . -name "*.sh" -o -name configure | wc -l
	@echo -n " * loc: " ; find . -name "*.sh"  -o -name configure | xargs wc -l| tail -1

clean:
	rm -f $(CLASSES)
	find -name "*~" | xargs rm -f
	find -name "*.class" | xargs rm -f

test: all $(TEST_CLASSES) Makefile
	java -ea -cp $(CLASSPATH) com/sandklef/compliance/test/TestAll

