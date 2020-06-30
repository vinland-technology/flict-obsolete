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
  com/sandklef/compliance/domain/LicenseGroup.java \
  com/sandklef/compliance/domain/Obligation.java \
  com/sandklef/compliance/domain/Concern.java \
  com/sandklef/compliance/domain/LicenseViolation.java \
  com/sandklef/compliance/domain/ObligationState.java \
  com/sandklef/compliance/domain/LicenseType.java \
  com/sandklef/compliance/domain/PolicyConcern.java \
  com/sandklef/compliance/domain/ComplianceAnswer.java \
  com/sandklef/compliance/domain/PolicyViolation.java \
  com/sandklef/compliance/utils/LeastPermissiveLicenseComparator.java \
  com/sandklef/compliance/utils/MostPermissiveLicenseComparator.java \
  com/sandklef/compliance/utils/Log.java \
  com/sandklef/compliance/utils/ObligationBuilder.java \
  com/sandklef/compliance/utils/LicenseUtils.java \
  com/sandklef/compliance/utils/LicenseStore.java \
  com/sandklef/compliance/utils/LicenseArbiter.java \
  com/sandklef/compliance/json/JsonLicenseParser.java \
  com/sandklef/compliance/json/JsonPolicyParser.java \
  com/sandklef/compliance/exporter/TextReportExporter.java \
  com/sandklef/compliance/exporter/TestExporterFactory.java \
  com/sandklef/compliance/json/JsonUtils.java \
  com/sandklef/compliance/json/JsonComponentParser.java \
  com/sandklef/compliance/json/JsonLicenseConnectionsParser.java \
  com/sandklef/compliance/cli/LicenseChecker.java \
  com/sandklef/compliance/exporter/ReportExporter.java \
  com/sandklef/compliance/exporter/ReportExporterFactory.java \
  com/sandklef/compliance/exporter/JsonExporter.java \
  com/sandklef/compliance/exporter/MDExporter.java \
  com/sandklef/compliance/utils/VirtualLicenseBuilder.java \
  com/sandklef/compliance/domain/LicenseExpression.java \
  com/sandklef/compliance/utils/LicenseExpressionParser.java \
  com/sandklef/compliance/domain/LicenseConnector.java \
  com/sandklef/compliance/domain/MetaData.java \


TEST_SOURCES=\
  com/sandklef/compliance/test/TestAll.java\
  com/sandklef/compliance/test/Utils.java\
  com/sandklef/compliance/test/TestComponents.java \
  com/sandklef/compliance/test/TestConcern.java \
  com/sandklef/compliance/test/TestCanAUseB.java \
  com/sandklef/compliance/test/TestPolicy.java \
  com/sandklef/compliance/test/TestDualLicenses.java \
  com/sandklef/compliance/test/TestMostPermissiveLicenseComparator.java \
  com/sandklef/compliance/json/test/TestLicenseParser.java \
  com/sandklef/compliance/test/TestLicenseConnector.java \
  com/sandklef/compliance/test/VirtualLicenseBuilderTest.java \
  com/sandklef/compliance/json/test/TestJsonComponentParser.java \


CLASSES=$(JAVA_SOURCES:.java=.class)
TEST_CLASSES=$(TEST_SOURCES:.java=.class)

LIB_DIR=lib
CLI_JAR=commons-cli-1.4.jar
GSON_JAR=gson-2.2.2.jar
WINSTONE_JAR=$(LIB_DIR)/winstone.jar
#JUNIT_JAR=$(LIB_DIR)/junit-jupiter-api-5.6.2.jar
#JUNIT_V_JAR=$(LIB_DIR)/junit-vintage-engine-5.6.2.jar
CLASSPATH=.:$(LIB_DIR)/$(CLI_JAR):$(LIB_DIR)/$(GSON_JAR)
TEST_CLASSPATH=$(CLASSPATH):$(JUNIT_JAR)

%.class:%.java 
	javac  -Xdiags:verbose -Xlint:unchecked -cp "$(CLASSPATH)" $<

JARS=$(LIB_DIR)/$(CLI_JAR) $(LIB_DIR)/$(GSON_JAR)

all: $(CLASSES) $(JARS) Makefile
	@echo all is done

$(CLASSES): $(SOURCES) Makefile

JARS=$(LIB_DIR)/$(CLI_JAR) $(LIB_DIR)/$(GSON_JAR)

$(LIB_DIR):
	@echo "Creating $(LIB_DIR)"
	mkdir -p $(LIB_DIR)

$(LIB_DIR)/$(CLI_JAR):
	make $(LIB_DIR)
	mkdir -p tmp; cd tmp ; wget "https://downloads.apache.org//commons/cli/binaries/commons-cli-1.4-bin.tar.gz"
	cd tmp; tar zxvf commons-cli-1.4-bin.tar.gz commons-cli-1.4/commons-cli-1.4.jar
	cd tmp; mv commons-cli-1.4/commons-cli-1.4.jar ../lib

$(LIB_DIR)/$(GSON_JAR):
	make $(LIB_DIR)
	mkdir -p tmp && cd tmp && wget "http://www.java2s.com/Code/JarDownload/gson/gson-2.2.2.jar.zip" 
	cd tmp && unzip $(GSON_JAR).zip && mv $(GSON_JAR) ../$(LIB_DIR)/

$(WINSTONE_JAR):
	mkdir -p lib
	wget 'https://sourceforge.net/projects/winstone/files/latest/download?source=typ_redirect' -O $(LIB_DIR)/winstone.jar

dload-libs: $(JARS)
#$(JUNIT_JAR) $(JUNIT_V_JAR)

$(CLASSES): $(JARS)

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
	@echo -n " * files: " ; find . -name "*.java" | grep -v out/ | wc -l
	@echo -n " * loc: " ; find . -name "*.java" | grep -v out/ | xargs wc -l| tail -1
	@echo "Json: " ; 
	@echo -n " * files: " ; find . -name "*.json" | grep -v out/ | wc -l
	@echo -n " * loc: " ; find . -name "*.json" | grep -v out/ | xargs wc -l| tail -1
	@echo "Bash: " ; 
	@echo -n " * files: " ; find . -name "*.sh" -o -name configure | grep -v out/ | wc -l
	@echo -n " * loc: " ; find . -name "*.sh"  -o -name configure | grep -v out/ | xargs wc -l| tail -1

clean:
	rm -f $(CLASSES)
	find -name "*~" | xargs rm -f
	find -name "*.class" | xargs rm -f

test: all $(TEST_CLASSES) Makefile
	java -ea -cp $(CLASSPATH) com/sandklef/compliance/test/TestAll

cg: connector-grahp

connector-grahp:
	bin/license-checker.sh -cg -o license.dot
	dot -Tpdf license.dot > license.pdf
	file  license.pdf
